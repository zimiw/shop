package com.easyshop.core.modules;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.nutz.dao.Chain;
import org.nutz.dao.Cnd;
import org.nutz.dao.Dao;
import org.nutz.dao.Sqls;
import org.nutz.dao.pager.Pager;
import org.nutz.dao.sql.Sql;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.mvc.annotation.At;
import org.nutz.mvc.annotation.Fail;
import org.nutz.mvc.annotation.Ok;
import org.nutz.mvc.annotation.Param;
import org.nutz.trans.Atom;
import org.nutz.trans.Trans;

import com.easyshop.bean.Address;
import com.easyshop.bean.ConnectorOP;
import com.easyshop.bean.Images;
import com.easyshop.bean.Order;
import com.easyshop.bean.OrderAppraisal;
import com.easyshop.bean.OrderProgress;
import com.easyshop.bean.Product;
import com.easyshop.core.modules.admin.OrderConstant;
import com.easyshop.core.modules.admin.OrderUtil;
import com.easyshop.utils.StringUtils;
import com.easyshop.utils.TimeUtils;

@IocBean
@At("/order")
@Ok("json")
@Fail("http:500")
public class OrderModule {
	@Inject
	protected Dao dao;
	public final static String FRONT_USER_ID = "frontUserId";// 前台用户sessionId

	@Inject
	protected OrderUtil orderUtil;
	  
	Logger logger = Logger.getLogger(OrderModule.class);

	/**
	 * 分页返回订单
	 * 
	 * @return
	 */
	@At
	public Object getOrders(HttpSession session, @Param("pageNum") int pageNum,
			@Param("pageSize") int pageSize) {

		Date startTime = new Date();
		System.out.println("-----------------开始查询时间 ：" + startTime.getTime()
				+ "--------------------");
		HashMap<String, Object> result = new HashMap<String, Object>();
		String userId = String.valueOf(session.getAttribute(FRONT_USER_ID));
		logger.info("调用getOrders方法,获取用户id为" + userId + "的第" + pageNum + "页上的"
				+ pageSize + "条订单 ");
		try {
			Pager pager = dao.createPager(pageNum, pageSize);
			List<Order> list = dao.query(Order.class,
					Cnd.where("userId", "=", Integer.parseInt(userId)).desc("createTime"), pager);
			pager.setRecordCount(dao.count(Order.class,
					Cnd.where("userId", "=", Integer.parseInt(userId))));
			for (Order order : list) {
				order.setProducts(getRelativeProduct(order.getOrderId()));
			}
			result.put("status", "sucess");
			result.put("data", list);
			result.put("count", pager.getRecordCount());
			logger.info("调用getOrders方法,获取用户id为" + userId + "的第" + pageNum
					+ "页上的" + pageSize + "条订单 成功！");
		} catch (Exception e) {
			logger.error("调用getOrders方法,获取用户id为" + userId + "的第" + pageNum
					+ "页上的" + pageSize + "条订单 失败  in gerOrders", e);
			result.put("status", "fail");
		}
		Date endTime = new Date();
		System.out.println("-----------------结束查询时间 ：" + endTime.getTime()
				+ "--------------------");
		System.out.println("花费时间为： "
				+ (endTime.getTime() - startTime.getTime()));
		return result;
	}

	/**
	 * 获取订单相关的商品信息
	 * 
	 * @param orderId
	 * @return
	 */
	private List<Product> getRelativeProduct(int orderId) {

		List<Product> proList = new ArrayList<Product>();
		List<ConnectorOP> clist = dao.query(ConnectorOP.class,
				Cnd.where("orderId", "=", orderId));
		for (ConnectorOP connectorOP : clist) {
			Product pt = dao.fetch(Product.class,
					Cnd.where("productId", "=", connectorOP.getProductId()));
			pt.setNumber(connectorOP.getNumber());
			pt.setSize(connectorOP.getSize());
			pt.setColor(connectorOP.getColor());
			pt.setPrice(connectorOP.getPrice());
			List<Images> images = dao.query(Images.class,
					Cnd.where("productId", "=", connectorOP.getProductId())
							.and("isTopimg", "=", true));
			pt.setImgs(images);
			proList.add(pt);
		}
		return proList;
	}

	/**
	 * 获取某种状态的订单
	 * 
	 * @param session
	 * @param status
	 * @return
	 */
	@At
	public Object getOrdersByStatus(HttpSession session,
			@Param("status") int status, @Param("pageNum") int pageNum,
			@Param("pageSize") int pageSize) {

		HashMap<String, Object> result = new HashMap<String, Object>();
		String userId = String.valueOf(session.getAttribute(FRONT_USER_ID));
		logger.info("调用getOrdersByStatus方法，获取状态为：" + status + ",用户id为："
				+ userId + "订单");
		try {
			Pager pager = dao.createPager(pageNum, pageSize);
			List<Order> list = dao.query(
					Order.class,
					Cnd.where("userId", "=", Integer.parseInt(userId)).and(
							"status", "=", status).desc("createTime"), pager);
			for (Order order : list) {
				order.setProducts(getRelativeProduct(order.getOrderId()));
			}
			result.put("data", list);
			result.put("count", dao.count(Order.class, Cnd.where("userId", "=", Integer.parseInt(userId)).and(
                    "status", "=", status)));
			logger.info("调用getOrdersByStatus方法，获取状态为：" + status + ",用户id为："
					+ userId + "订单成功！");
		} catch (Exception e) {
			logger.error("调用getOrdersByStatus方法，获取状态为：" + status + ",用户id为："
					+ userId + "订单失败  in getOrdersByStatus", e);
			result.put("status", "fail");
		}
		return result;
	}

	/**
	 * 
	 * @param orderId
	 * @return
	 */
	@At
	public Object getOrderByOrderId(@Param("orderId") int orderId) {
		Date startTime = new Date();
		System.out.println("-----------------开始查询时间 ：" + startTime.getTime()
				+ "--------------------");
		logger.info("开始调用getOrderByOrderId方法，获取订单id为：" + orderId + "的订单基本字段");
		HashMap<String, Object> result = new HashMap<String, Object>();
		Order order = null;
		try {
			order = dao.fetch(Order.class, Cnd.where("orderId", "=", orderId));
			result.put("data", order);
			logger.info("成功调用getOrderByOrderId方法，获取订单id为：" + orderId
					+ "的订单基本字段");
		} catch (Exception e) {
			result.put("status", "fail");
			logger.error("调用getOrderByOrderId方法，获取订单id为：" + orderId
					+ "的订单基本字段出错", e);
		}
		Date endTime = new Date();
		System.out.println("-----------------结束查询时间 ：" + endTime.getTime()
				+ "--------------------");
		System.out.println("花费时间为： "
				+ (endTime.getTime() - startTime.getTime()));
		return result;
	}

	/**
	 * 获取订单详情
	 * 
	 * @param session
	 * @param orderId
	 * @return
	 */
	@At
	public Object getOrderDetail(HttpSession session,
			@Param("orderId") int orderId) {

	    Map<String, Object> result = new HashMap<String, Object>();
        String userId = String.valueOf(session.getAttribute(OrderConstant.FRONT_USER_ID));

        // 获取订单信息
        Cnd cnd = Cnd.where("userId", "=", userId).and("orderId", "=", orderId);
        Order queryOrder = dao.fetch(Order.class, cnd);

        if (queryOrder == null) {
            result.put("status", "fail");
            result.put("msg", "该订单不存在!");
            return result;
        }

        // 查询支付信息
//        PaymentInfo paymentInfo = dao.fetch(PaymentInfo.class, Cnd.where("orderId", "=", orderId));

        // 查询订单商品信息
        String sqlStr = "SELECT b.productId, b.name,  t.price, t.number num " + " FROM product b, connectorop t "
                + " WHERE b.productId = t.productId  and t.orderId = @orderId ";

        Sql sql = Sqls.create(sqlStr);
        sql.params().set("orderId", orderId);
        sql.setCallback(Sqls.callback.maps());
        dao.execute(sql);
        List<Map> goodsList = sql.getList(Map.class);
        String productId;
        for (Map map : goodsList) {// 取每个商品的主图片
            productId = String.valueOf(map.get("productId"));
            map.put("img", fetchImg(Integer.parseInt(productId)).getImgsource());
        }

        // dateList订单跟踪
        sqlStr = "SELECT  DATE_FORMAT(a.time, '%Y-%m-%d') day, " + "    DATE_FORMAT(a.time, '%T') time, statusCode "
                + "FROM  orderprogress a WHERE a.orderId = @orderId " + "ORDER BY a.statusCode";

        sql = Sqls.create(sqlStr);
        sql.params().set("orderId", orderId);
        sql.setCallback(Sqls.callback.maps());
        dao.execute(sql);
        List<Map> dateList = sql.getList(Map.class);
        // 订单信息
        // oid
        result.put("orderId", queryOrder.getOrderId());
        result.put("odate", queryOrder.getCreateTime());
        result.put("status", queryOrder.getStatus());

        result.put("freight", "0");// 邮费
        result.put("total", queryOrder.getAmount());// 订单总金额

        result.put("dateList", dateList);

        // 收货人信息
        Address addr = orderUtil.getAdress(queryOrder.getAddressId());

        result.put("consignee", addr.getName());
        result.put("address", orderUtil.getAdressDesc(addr));
        result.put("phone", addr.getCellPhoneNew());

        // 支付及配送方式
        // paymentInfo
        result.put("paytype", OrderConstant.PAYTYPEMAP.get(queryOrder.getChargeChannel()));
        result.put("oid", queryOrder.getTransportId());
        result.put("company", "顺丰");

        result.put("products", goodsList);
        return result;
	}
	
	 /**
     * 根据商品id获取对应是商品id
     * 
     * @return
     */
    private Images fetchImg(int productId) {
        Images imgs = dao.fetch(Images.class, Cnd.where("productId", "=", productId).and("isTopimg", "=", true));
        return imgs != null ? imgs : new Images();
    }

	/**
	 * 获取某种状态订单数
	 * 
	 * @return
	 */
	@At
	public Object getCountByStatus(HttpSession session,
			@Param("status") int status) {

		HashMap<String, Object> result = new HashMap<String, Object>();
		String userId = String.valueOf(session.getAttribute(FRONT_USER_ID));
		logger.info("调用getCountByStatus方法，获取状态为：" + status + ",用户id为：" + userId
				+ "的订单总数");
		try {
			int count = dao.count(
					Order.class,
					Cnd.where("userId", "=", Integer.parseInt(userId)).and(
							"status", "=", status));
			result.put("total", count);
			logger.info("调用getCountByStatus方法，获取状态为：" + status + ",用户id为："
					+ userId + "的订单总数成功");
		} catch (Exception e) {
			logger.error("获取状态为：" + status + ",用户id为：" + userId
					+ "的订单总数失败  in getCountByStatus", e);
			result.put("status", "fail");
		}
		return result;
	}

	/**
	 * 获取每一种状态的订单数
	 * 
	 * @param session
	 * @return [{status: 101,count:2}]
	 */
	@At
	public Object getAllCountByStatus(HttpSession session) {

		HashMap<String, Object> result = new HashMap<String, Object>();
		String userId = String.valueOf(session.getAttribute(FRONT_USER_ID));
		logger.info("调用getAllCountByStatus方法，获取每一种状态的订单总数");
		try {
			Sql sql = Sqls
					.create("select status,count(*) as count from orders where userId =@userId group by status order by status asc");
			sql.params().set("userId", Integer.parseInt(userId));
			sql.setCallback(Sqls.callback.maps());
			dao.execute(sql);
			result.put("count", sql.getResult());
			logger.info("调用getAllCountByStatus方法，获取每一种状态的订单总数成功");
		} catch (Exception e) {
			logger.error("调用getAllCountByStatus方法,获取每一种状态的订单总数失败", e);
			result.put("status", "fail");
		}
		return result;
	}

	/**
	 * 设置订单的进度 提交订单201， 付款成功202，商品出库203， 等待收货 204，完成 205,取消200
	 * 
	 * @param orderId
	 * @param statusCode
	 */
	public void setOrderProgress(int orderId, int statusCode) {

		logger.info("开始调用setOrderProgress方法，设置订单的进度为：" + statusCode + ",订单id为："
				+ orderId + "的订单总数");
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");// 设置日期格式

		OrderProgress op = dao.fetch(
				OrderProgress.class,
				Cnd.where("orderId", "=", orderId).and("statusCode", "=",
						statusCode));
		if (op == null) {
			op = new OrderProgress();
			op.setOrderId(orderId);
			op.setStatusCode(statusCode);
			op.setTime(df.format(new Date()));
			dao.insert(op);
		}
		logger.info("结束调用setOrderProgress方法，设置订单的进度为：" + statusCode + ",订单id为："
				+ orderId + "的订单总数");
	}

	/**
	 * 修改商品库存
	 * @author wangzhiming
	 * @date 2016.2.26
	 * @return
	 */
	public boolean updateStore(int orderId){
		
		List<ConnectorOP> list = new ArrayList<ConnectorOP>();
		list = dao.query(ConnectorOP.class, Cnd.where("orderId","=", orderId));
		for(ConnectorOP item : list){
			if(item.isLimitActivity()){
				
			}
		}
		return true;
	}
	
	/**
	 * 取消订单
	 * 
	 * @param session
	 * @param orderId
	 * @return
	 */
	@At
	public Object cancelOrder(HttpSession session,
			@Param("orderId") final int orderId) {

		logger.info("开始调用cancelOrder方法，取消订单id为：" + orderId + "的订单");
		HashMap<String, Object> result = new HashMap<String, Object>();
		String userId = String.valueOf(session.getAttribute(FRONT_USER_ID));
		final Order order = dao.fetch(
				Order.class,
				Cnd.where("orderId", "=", orderId).and("userId", "=",
						Integer.parseInt(userId)));
		if (order.getStatus() == 101) {
			// 在这里进行事务的操作
			try {
				Trans.exec(new Atom() {
					@Override
					public void run() {
						order.setStatus(100);
						dao.update(order);
						setOrderProgress(orderId, 200);
					}
				});
				result.put("status", "success");
				logger.info("结束调用cancelOrder方法，取消订单成功");
				return result;
			} catch (Exception e) {
				// TODO Auto-generated catch block
				result.put("status", "fail");
				result.put("msg", "取消订单失败");
				logger.info("结束调用cancelOrder方法，取消订单失败");
				return result;
			}
		} else {
			result.put("status", "fail");
			result.put("msg", "当前订单状态不允许取消");
			logger.info("结束调用cancelOrder方法，当前订单状态不允许取消");
			return result;
		}
	}
	
	/**
	 * 订单确认
	 * @param session
	 * @param orderId
	 * @return
	 */
	@At
	public Object orderConfirm(HttpSession session,
            @Param("orderId") final int orderId){
	    Map<String, Object> result = new HashMap<String, Object>();
	    String userId = String.valueOf(session.getAttribute(FRONT_USER_ID));
        final Order queryOrder = dao.fetch(
                Order.class,
                Cnd.where("orderId", "=", orderId).and("userId", "=",
                        userId).and("status", "=", 103));//查询待收货订单
        if (queryOrder == null ) {
            result.put("status", "fail");
            result.put("msg", "该订单不存在 !");
            return result;
        }
        
        int res = dao.update(Order.class, Chain.make("status", 104).add("confirmTime", TimeUtils.dateToStr(new Date(), TimeUtils.FORMAT14)), 
                Cnd.where("orderId", "=", orderId).and("userId", "=",
                userId).and("status", "=", 103));
        
        result.put("status", "success");
        result.put("msg", "确认收货成功");
        
        return result;
	}
	
	
	/**
	 * 订单评价
	 * @return
	 */
	@At
	public Object orderApp(HttpSession session,
            @Param("orderId") final int orderId, 
            @Param("appType") final int appType,
            @Param("appContent") final String appContent){
	    Map<String, Object> result = new HashMap<String, Object>();
        final String userId = String.valueOf(session.getAttribute(FRONT_USER_ID));
        
        if(orderId==0||appType==0||StringUtils.isEmpty(appContent)){
            result.put("status", "fail");
            result.put("msg", "参数不能为空 !");
            return result;
        }
        
        final Order queryOrder = dao.fetch(
                Order.class,
                Cnd.where("orderId", "=", orderId).and("userId", "=",
                        userId).and("status", "=", 104));//查询待收货订单
        
        if (queryOrder == null ) {
            result.put("status", "fail");
            result.put("msg", "该订单不存在 !");
            return result;
        }
        Trans.exec(new Atom() {
            @Override
            public void run() {
                dao.update(Order.class, Chain.make("status", 106), Cnd.where("orderId", "=", orderId).and("userId", "=",
                        userId).and("status", "=", 105));
                
                OrderAppraisal app = new OrderAppraisal();
                app.setOrderId(orderId);
                app.setAppTime(TimeUtils.dateToStr(new Date(), TimeUtils.FORMAT14));
                app.setAppContent(appContent);
                app.setAppType(appType);
            }
        });
        
        result.put("status", "success");
        result.put("msg", "评价成功");
        return result;
        
	}
	
	
	

}
