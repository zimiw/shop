package com.easyshop.core;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.nutz.dao.Chain;
import org.nutz.dao.Cnd;
import org.nutz.dao.Dao;
import org.nutz.dao.Sqls;
import org.nutz.dao.sql.Sql;
import org.nutz.dao.util.Daos;
import org.nutz.ioc.Ioc;
import org.nutz.mvc.NutConfig;
import org.nutz.mvc.Setup;
import org.nutz.trans.Atom;
import org.nutz.trans.Trans;

import com.easyshop.bean.ActivityLottery;
import com.easyshop.bean.ActivityLotteryConf;
import com.easyshop.bean.Catalogs;
import com.easyshop.bean.ConnectorOP;
import com.easyshop.bean.Order;
import com.easyshop.bean.Personal;
import com.easyshop.bean.Role;
import com.easyshop.bean.User;
import com.easyshop.utils.TimeUtils;

public class MainSetup implements Setup {
	Logger logger = Logger.getLogger(MainSetup.class);

	@Override
	public void destroy(NutConfig conf) {
	}

	@Override
	public void init(NutConfig conf) {
		// TODO Auto-generated method stub
		Ioc ioc = conf.getIoc();
		Dao dao = ioc.get(Dao.class);
		Daos.createTablesInPackage(dao, "com.easyshop.bean", false);
		// 初始化默认根用户
		if (dao.count(User.class) == 0 && dao.count(Role.class) == 0
				&& dao.count(Catalogs.class) == 0) {
			User user = new User();
			user.setName("admin");
			user.setPwd("123456");
			user.setBackup("超级管理员");
			user.setStatus("active");
			user.setType("superAdmin");
			Role role = new Role();
			role.setName("superAdmin");
			role.setDescp("超级管理员");
			Role roletemp = dao.insert(role);
			user.setRoleId(roletemp.getRoleId());
			dao.insertWith(user, "role");
			Catalogs catalog = new Catalogs();
			catalog.setCatalogName("商品分类");
			catalog.setPid(0);
			dao.insert(catalog);

		}
		// 初始化前台用�
		if (dao.fetch(Personal.class, Cnd.where("phone", "=", "1")) == null) {
			Personal personal = new Personal();
			personal.setName("1");
			personal.setPhone("1");
			personal.setPassword("1");
			dao.insert(personal);
		}
		// 初始化用户
		GenerateP gp = new GenerateP(dao);
		gp.generatePerson();

		new Thread(new LotteryRun(dao)).start();
	}

	// 抽奖程序
	class LotteryRun implements Runnable {

		private Dao dao;

		public LotteryRun(Dao dao) {
			this.dao = dao;
		}

		// 相关时间像差多少分钟
		public int diffDate(Date jzDate, Date qsDate) {
			return (int) ((jzDate.getTime() - qsDate.getTime()) / (60 * 1000));
		}
		
		/**
		 * 根据限时商品价格修改商品最低价格
		 */
		private void runProduct(){
		    
		    String dayTime  = TimeUtils.dateToStr(new Date(), TimeUtils.FORMAT14);
		    //正在进行的限时活动
		    String sqlStr = 
		            "SELECT a.productId ,  MIN(a.price) price " +
		            " FROM activityproduct a " +
		            " WHERE beginTime<=@dayTime " +
		            " AND endTime>=@dayTime" +
		            " AND leftNum>0 " +
		            " AND status = 1  " +
		            " GROUP BY a.productId ";
		   Sql sql = Sqls.create(sqlStr);
	        sql.params().set("dayTime", dayTime);
	        sql.setCallback(Sqls.callback.maps());
	        dao.execute(sql);
	        List<Map> list = sql.getList(Map.class);
	        
	        sqlStr = " update product set minPrice = @minPrice, limitActivityStatus=1 "
                    + " where productId = @productId and minPrice>@minPrice   ";
	        // 更新商品中的价格
	        for(Map map : list){
	            sql = Sqls.create(sqlStr);
	            sql.params().set("productId", map.get("productId"));
	            sql.params().set("minPrice", map.get("price"));
	            dao.execute(sql);
	        }
		 
	        //售完的商品
	        sqlStr = "SELECT a.productId ,  MIN(a.price) price " +
                    " FROM activityproduct a " +
                    " WHERE beginTime<=@dayTime " +
                    " AND endTime>=@dayTime" +
                    " AND leftNum<=0 " +
                    " AND status = 1  " +
                    " GROUP BY a.productId ";
	        sql = Sqls.create(sqlStr);
            sql.params().set("dayTime", dayTime);
            sql.setCallback(Sqls.callback.maps());
            dao.execute(sql);
           list = sql.getList(Map.class);
           sqlStr = " update product set minPrice = (select min(currentPrice) from producttype t where t.productId = product.productId ), limitActivityStatus = 2 "
                   + " where productId = @productId and limitActivityStatus in (1, 0) ";
	        for(Map map : list){
                sql = Sqls.create(sqlStr);
                sql.params().set("productId", map.get("productId"));
                dao.execute(sql);
            }
	        
	        //结束的活动
	        sqlStr = "SELECT a.productId ,  MIN(a.price) price " +
                    " FROM activityproduct a " +
                    " WHERE endTime<@dayTime" +
                    " AND status = 1  " +
                    " GROUP BY a.productId ";
            sql = Sqls.create(sqlStr);
            sql.params().set("dayTime", dayTime);
            sql.setCallback(Sqls.callback.maps());
            dao.execute(sql);
            sqlStr = " update product set minPrice = (select min(currentPrice) from producttype t where t.productId = product.productId ), limitActivityStatus = 3 "
                    + " where productId = @productId and limitActivityStatus in (1, 2) ";
             for(Map map : list){
                 sql = Sqls.create(sqlStr);
                 sql.params().set("productId", map.get("productId"));
                 dao.execute(sql);
             }
		    
		}
		

		/**
		 * 每隔30分钟就把未支付订单取消
		 */
		private void runOrder() {
			List<Order> listOrder = dao.query(
					Order.class,
					Cnd.where("status", "=", 101)
							.and("TIMESTAMPDIFF(MINUTE,createTime, now())",
									">=", 30));

			if (listOrder != null && listOrder.size() > 0) {
				for (final Order order : listOrder) {

					logger.debug("runOrder 后台定时取消订单开始");

					Trans.exec(new Atom() {
						@Override
						public void run() {
							List<ConnectorOP> listOp = dao.query(
									ConnectorOP.class,
									Cnd.where("orderId", "=",
											order.getOrderId()));

							// 设置为取消
							dao.update(
									Order.class,
									Chain.make("status", 107),
									Cnd.where("orderId", "=",
											order.getOrderId()));

							Sql sql = null;
							for (ConnectorOP op : listOp) {
								if (op.isLimitActivity()) {
									sql = Sqls
											.create("update activityproduct "
													+ " set leftNum = leftNum +@num  where productTypeId =@productTypeId ");
								} else {
									sql = Sqls
											.create("update producttype "
													+ " set storeCount = storeCount +@num  where productTypeId =@productTypeId ");
								}

								sql.params()
										.set("num", op.getNumber())
										.set("productTypeId",
												op.getProductTypeId());
								dao.execute(sql);
							}

						}
					});

				}
			}

		}

		@Override
		public void run() {

			while (true) {

			    
			    try {
			        runProduct();
                } catch (Exception e) {
                    logger.error("限时活动商品价格修改", e);
                }
			    
				try {
					runOrder();
				} catch (Exception e) {
					logger.error("后台定时取消订单", e);
				}

				logger.debug("抽取程序后台监听中。。。");

				ActivityLotteryConf conf = dao.fetch(ActivityLotteryConf.class);
				if (conf != null) {

					Date date = new Date();
					String dayStr = TimeUtils.dateToStr(date,
							TimeUtils.FORMAT10);
					String hh = TimeUtils.dateToStr(new Date(), "HH:mm:ss");

					Date qsDate = TimeUtils.strToDateLong(
							dayStr + " " + conf.getLotteryTime(),
							TimeUtils.FORMAT14);
					int i = diffDate(date, qsDate);

					if ((i <= 2 && i >= 0) || hh.equals(conf.getLotteryTime())) {// 相差2分钟以内的都运行

						logger.debug("抽取程序开始。。。");

						// 今天参与的订单
						int count = dao.count(ActivityLottery.class, Cnd
								.wrap("DATE_FORMAT(insertTime, '%Y-%m-%d')='"
										+ dayStr + "'"));
						// 中奖订单数
						int lotteryCount = dao.count(ActivityLottery.class, Cnd
								.wrap("DATE_FORMAT(insertTime, '%Y-%m-%d')='"
										+ dayStr + "' and isLottery = 1 "));

						int rand = (int) Math.rint(count * conf.getRate());// 参与的总数乘以中奖概率
						rand = rand <= 1 ? 1 : rand;

						if (lotteryCount <= rand) {// 今天需要中奖的数量还有剩余

							rand = rand - lotteryCount;
							String sqlStr = "SELECT  id   FROM  activitylottery "
									+ "        WHERE isLottery = 0 "
									+ "        AND DATE_FORMAT(insertTime, '%Y-%m-%d')= @day "
									+ "        ORDER BY  rand() LIMIT 0,  "
									+ rand;
							Sql sql = Sqls.create(sqlStr);
							sql.params().set("day", dayStr);
							sql.setCallback(Sqls.callback.maps());
							dao.execute(sql);
							List<Map> list = sql.getList(Map.class);

							String sqlStr2 = "UPDATE activitylottery "
									+ "SET  isLottery = 1, lotteryTime=now() "
									+ "WHERE  isLottery = 0 "
									+ "AND DATE_FORMAT(insertTime, '%Y-%m-%d') = @day "
									+ "AND id = @id ";

							for (Map map : list) {
								sql = Sqls.create(sqlStr2);
								sql.params().set("day", dayStr);
								sql.params().set("id", map.get("id"));
								dao.execute(sql);
							}

						} else {
							logger.debug(dayStr + "今日中奖名额已经用完");
						}

						logger.debug(dayStr + "抽取程序结束");
					}
				}

				try {
					Thread.sleep(1000 * 60);// 1分钟
				} catch (InterruptedException e) {
				}

			}

		}
	}

}
