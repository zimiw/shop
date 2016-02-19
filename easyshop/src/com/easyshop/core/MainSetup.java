package com.easyshop.core;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.nutz.dao.Cnd;
import org.nutz.dao.Dao;
import org.nutz.dao.Sqls;
import org.nutz.dao.sql.Sql;
import org.nutz.dao.util.Daos;
import org.nutz.ioc.Ioc;
import org.nutz.mvc.NutConfig;
import org.nutz.mvc.Setup;

import com.easyshop.bean.ActivityLottery;
import com.easyshop.bean.ActivityLotteryConf;
import com.easyshop.bean.Catalogs;
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

		@Override
		public void run() {

			while (true) {
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

					if (i <= 2 || hh.equals(conf.getLotteryTime())) {// 相差2分钟以内的都运行

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
