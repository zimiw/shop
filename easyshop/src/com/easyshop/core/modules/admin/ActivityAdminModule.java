package com.easyshop.core.modules.admin;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.nutz.dao.Chain;
import org.nutz.dao.Cnd;
import org.nutz.dao.Dao;
import org.nutz.dao.Sqls;
import org.nutz.dao.pager.Pager;
import org.nutz.dao.sql.Sql;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.mvc.annotation.At;
import org.nutz.mvc.annotation.By;
import org.nutz.mvc.annotation.Fail;
import org.nutz.mvc.annotation.Filters;
import org.nutz.mvc.annotation.Ok;
import org.nutz.mvc.annotation.Param;
import org.nutz.trans.Atom;
import org.nutz.trans.Trans;

import com.easy.core.filters.CheckBackUserLoginFilter;
import com.easyshop.bean.ActivityBranner;
import com.easyshop.bean.ActivityLottery;
import com.easyshop.bean.ActivityLotteryConf;
import com.easyshop.bean.ActivityProduct;
import com.easyshop.bean.ActivtyHeat;
import com.easyshop.bean.ActivtySpecialty;
import com.easyshop.bean.Personal;
import com.easyshop.bean.Product;
import com.easyshop.bean.ProductType;
import com.easyshop.utils.StringUtils;
import com.easyshop.utils.TimeUtils;
import com.easyshop.vo.ResultVo;

/**
 * 活动管理
 * 
 * @author luocz
 *
 */
@IocBean
@At("/admin/activity")
@Ok("json")
@Fail("http:500")
@Filters({ @By(type = CheckBackUserLoginFilter.class) })
public class ActivityAdminModule {
	@Inject
	protected Dao dao;

	// @Inject
	// protected EhcacheManager ehcacheManager;

	/**
	 * 轮播图获取
	 * 
	 * @return
	 */
	@At
	public Object queryBranner() {

		List<ActivityBranner> list = dao.query(ActivityBranner.class, Cnd
				.where("status", "=", 1).desc("seq").desc("id"));
		return list;
	}

	/**
	 * 首页轮播图管理
	 * 
	 * @param branner
	 * @return
	 */
	@At
	public Object addBranner(@Param("..") final ActivityBranner branner) {

		Map<String, Object> result = new HashMap<String, Object>();

		if (branner == null) {
			result.put("status", "fail");
			result.put("msg", "请输入对应内容 !");
			return result;
		}

		int count = dao.count(ActivityBranner.class);

		if (count >= 5 && branner.getId() == 0) {
			result.put("status", "fail");
			result.put("msg", "首页轮播图片最多只能添加5个!");
			return result;
		}

		Trans.exec(new Atom() {
			@Override
			public void run() {
				// for (ActivityBranner branner : branners) {

				branner.setStatus(1);
				if (branner.getId() > 0) {// 修改
					dao.update(branner);
				} else {
					dao.insert(branner);// 新增
				}

				// }
			}
		});

		result.put("status", ResultVo.STATUS_SUCCESS);
		result.put("msg", "数据保存成功");

		return result;
	}

	/**
	 * 限时活动获取列表
	 * 
	 * @return
	 */
	@At
	public Object queryActivityList(@Param("productId") String productId,
			@Param("productName") String productName,
			@Param("status") String status, @Param("rows") int pageSize,
			@Param("page") int pageNum) {

		Map<String, Object> result = new HashMap<String, Object>();

		String sqlStr = "SELECT b.activityId, a.productId, a.name, a.minPrice, b.price,"
				+ " CONCAT(b.beginTime,'~', b.endTime) time, t.storeCount ,     "
				+ " ( CASE  WHEN b.leftNum<=0  THEN '已售罄' "
				+ "            WHEN b.beginTime <= @dayTime  AND b.endTime >=@dayTime  THEN '已开始' "
				+ "            WHEN b.endTime<@dayTime THEN '已结束' "
				+ "            WHEN b.beginTime >@dayTime THEN '未开始' END ) statusDesc  "
				+ " FROM  (select productId, sum(storeCount) storeCount from producttype group by productId)  t, "
				+ " product a, activityproduct b  WHERE  a.productId= t.productId and  a.productId = b.productId and b.status = 1 ";

		String sqlStr2 = "select count(1) from product a, activityproduct b "
				+ " where  a.productId = b.productId and b.status = 1 ";

		String sqlWhere = "";
		Cnd cnd = Cnd.NEW();
		List<String> paramsList = new ArrayList<String>();
		Map<String, Object> valMap = new HashMap<String, Object>();
		if (!StringUtils.isEmpty(productName)) {
			sqlWhere += " and a.name like @productName ";
			paramsList.add("productName");
			valMap.put("productName", "%"+productName+"%");
		}

		if (!StringUtils.isEmpty(productId)) {
			sqlWhere += " and a.productId  = @productId ";
			paramsList.add("productId");
			valMap.put("productId", productId);
		}

		if ("1".equals(status)) {// 已开始
			sqlWhere += " and  b.beginTime <= @dayTime  AND b.endTime >=@dayTime ";
		} else if ("2".equals(status)) {// 已结束
			sqlWhere += " and  b.endTime<@dayTime ";
		} else if ("3".equals(status)) {// 已售罄
			sqlWhere += " and  b.leftNum<=0  ";
		} else if ("4".equals(status)) {// 未开始
			sqlWhere += " and  b.beginTime >@dayTime  ";
		}

		Sql sql = Sqls.create(sqlStr2 + sqlWhere);
		sql.params().set("dayTime",
				TimeUtils.dateToStr(new Date(), TimeUtils.FORMAT14));
		for (String str : paramsList) {
			sql.params().set(str, valMap.get(str));
		}
		sql.setCallback(Sqls.callback.integer());
		dao.execute(sql);
		int total = sql.getInt();
		result.put("total", total);

		if (total == 0) {
			return result;
		}

		Pager pager = dao.createPager(pageNum, pageSize);
		sqlStr += sqlWhere + " limit @pageNum, @pageSize ";

		sql = Sqls.create(sqlStr);
		sql.params().set("dayTime",
				TimeUtils.dateToStr(new Date(), TimeUtils.FORMAT14));
		sql.params().set("pageNum", pager.getOffset());
		sql.params().set("pageSize", pager.getPageSize());
		for (String str : paramsList) {
			sql.params().set(str, valMap.get(str));
		}

		sql.setCallback(Sqls.callback.maps());
		dao.execute(sql);
		List<Map> list = sql.getList(Map.class);
		result.put("rows", list);
		return result;
	}

	/**
	 * 限时活动设置页面获取商品信息
	 * 
	 * @return
	 */
	@At
	public Object queryActivityProductList(
			@Param("productId") String productId,
			@Param("productName") String productName,
			@Param("rows") int pageSize, @Param("page") int pageNum) {

		Map<String, Object> result = new HashMap<String, Object>();

		// 1限时活动 2热卖一 3热卖二
		String sqlStr = "SELECT a.productId, a.name, a.minPrice, "
				+ "(case when a.activityType='1' then '限时活动 ' "
				+ " when a.activityType='2' then '热卖一 ' "
				+ " when a.activityType='3' then '热卖二 ' end ) activityType, "
				+ " (SELECT t.name  FROM Supplier t  WHERE a.supplierId = t.id) supplierName,  "
				+ " (SELECT t.catalogName  FROM catalog t  WHERE a.catalogId3 = t.catalogId) catalogName, "
				+ " b.storeCount   "
				+ " FROM   (select productId, sum(storeCount) storeCount from producttype group by productId)  b,product a "
				+ " where a.productId = b.productId and a.status = 1 ";

		Cnd cnd = Cnd.where("status", "=", 1);
		List<String> paramsList = new ArrayList<String>();
		Map<String, Object> valMap = new HashMap<String, Object>();
		if (!StringUtils.isEmpty(productName)) {
			cnd.and("name", "like", "%" + productName + "%");
			sqlStr += " and a.name like @productName ";
			paramsList.add("productName");
			valMap.put("productName", "%"+productName+"%");
		}

		if (!StringUtils.isEmpty(productId)) {
			cnd.and("productId", "=", productId);
			sqlStr += " and a.productId  = @productId ";
			paramsList.add("productId");
			valMap.put("productId", productId);
		}

		result.put("total", dao.count(Product.class, cnd));

		sqlStr += " limit @pageNum, @pageSize ";

		Sql sql = Sqls.create(sqlStr);
		Pager pager = dao.createPager(pageNum, pageSize);
		sql.params().set("dayTime",
				TimeUtils.dateToStr(new Date(), TimeUtils.FORMAT14));
		sql.params().set("pageNum", pager.getOffset());
		sql.params().set("pageSize", pager.getPageSize());
		for (String str : paramsList) {
			sql.params().set(str, valMap.get(str));
		}

		sql.setCallback(Sqls.callback.maps());
		dao.execute(sql);
		List<Map> list = sql.getList(Map.class);
		result.put("rows", list);
		return result;
	}

	/**
	 * 根据商品id获取该商品所有的型号
	 * 
	 * @param productId
	 * @return
	 */
	@At
	public Object queryActivityProductType(@Param("productId") String productId) {
		List<ProductType> list = dao.query(ProductType.class,
				Cnd.where("productId", "=", productId).desc("productTypeId"));
		return list;
	}

	/**
	 * 限时活动保存
	 * 
	 * @return
	 */
	@At
	public Object saveActivity(HttpServletRequest request) {
		final String productId = request.getParameter("productId");
		String[] productTypeIds = request.getParameterValues("productTypeId[]");
		String[] prices = request.getParameterValues("price[]");
		String[] nums = request.getParameterValues("num[]");
		String beginTime = request.getParameter("beginTime");
		String endTime = request.getParameter("endTime");

		// opt.productId = limitCtrl.setting.producttypes[i].productId;
		// opt.productTypeId = limitCtrl.setting.producttypes[i].productTypeId;
		// opt.price = limitCtrl.setting.producttypes[i].price;
		// opt.num = limitCtrl.setting.producttypes[i].num;
		// opt.beginTime = limitCtrl.setting.beginTime;
		// opt.endTime = limitCtrl.setting.endTime;

		ActivityProduct[] aps = new ActivityProduct[productTypeIds.length];
		ActivityProduct ap = null;
		for (int i = 0; i < productTypeIds.length; i++) {
			ap = new ActivityProduct();
			ap.setProductId(Integer.parseInt(productId));
			ap.setProductTypeId(Integer.parseInt(productTypeIds[i]));
			ap.setPrice(Double.parseDouble(prices[i]));
			ap.setNum(Integer.parseInt(nums[i]));
			ap.setBeginTime(beginTime);
			ap.setEndTime(endTime);
			aps[i] = ap;
		}

		final ActivityProduct[] activityProducts = aps;

		Map<String, Object> result = new HashMap<String, Object>();

		if (activityProducts == null || activityProducts.length == 0) {
			result.put("status", ResultVo.STATUS_FAIL);
			result.put("msg", "保存内容不能为空!");
			return result;
		}

		// String sqlStr = "SELECT COUNT(1) " + "FROM activityproduct a "
		// + "WHERE status =1 " + " AND a.productId = @productId "
		// + "AND ((  a.beginTime<= @beginTime "
		// + "        AND a.endTime>=@beginTime) "
		// + "    OR  ( a.beginTime<= @endTime "
		// + "        AND a.endTime>=@endTime ) "
		// + "    OR  ( a.beginTime>=@beginTime "
		// + "        AND a.beginTime<=@endTime ) "
		// + "    OR  ( a.endTime>=@beginTime "
		// + "       AND a.endTime<=@endTime)) ";
		// Sql sql = Sqls.create(sqlStr);
		// sql.params().set("productId", activityProduct.getProductId());
		// sql.params().set("beginTime", activityProduct.getBeginTime());
		// sql.params().set("endTime", activityProduct.getEndTime());
		// sql.setCallback(Sqls.callback.maps());
		// dao.execute(sql);
		// int count = sql.getInt();
		// if (count > 0) {
		// result.put("status", ResultVo.STATUS_FAIL);
		// result.put("msg", "该商品在该时间段内已经存在限时活动!");
		// return result;
		// }

		Trans.exec(new Atom() {
			@Override
			public void run() {

				for (ActivityProduct ap : activityProducts) {
					ap.setStatus(1);
					ap.setLeftNum(ap.getNum());// 剩余数量和活动数量相关
					dao.insert(ap);
				}

				// 根据商品中对应的状态
				dao.update(
						Product.class,
						Chain.make("activityType", 1),
						Cnd.where("productId", "=",
						        productId));
			}
		});

		result.put("status", ResultVo.STATUS_SUCCESS);
		result.put("msg", "限时活动保存成功");

		return result;
	}

	/**
	 * 删除对应的限时活动
	 * 
	 * @return
	 */
	@At
	public Object delActivity(@Param("activityId") String activityId) {

		Map<String, Object> result = new HashMap<String, Object>();
		int res = dao.update(ActivityProduct.class, Chain.make("status", 0),
				Cnd.where("activityId", "=", activityId));

		if (res == 1) {
			result.put("status", ResultVo.STATUS_SUCCESS);
			result.put("msg", "限时活动删除成功");
		} else {
			result.put("status", ResultVo.STATUS_FAIL);
			result.put("msg", "限时活动删除失败!");
		}

		return result;
	}

	/**
	 * 专场活动获取
	 * 
	 * @return
	 */
	@At
	public Object queryActivtySpecialtyList() {

		List<ActivtySpecialty> list = dao.query(ActivtySpecialty.class, Cnd
				.NEW().desc("beginTime").desc("endTime"));
		return list;
	}

	/**
	 * 专场活动保存
	 */
	@At
	public Object saveActivtySpecialty(
			@Param("..") final ActivtySpecialty activtySpecialtys) {
		Map<String, Object> result = new HashMap<String, Object>();

		if (StringUtils.isEmpty(activtySpecialtys.getBeginTime())
				|| StringUtils.isEmpty(activtySpecialtys.getEndTime())
				|| StringUtils.isEmpty(activtySpecialtys.getImgSource())
				|| StringUtils.isEmpty(activtySpecialtys.getUrl())) {
			result.put("status", ResultVo.STATUS_FAIL);
			result.put("msg", "保存参数不能为空!");
			return result;
		}

		Trans.exec(new Atom() {
			@Override
			public void run() {
				activtySpecialtys.setStatus(1);
				if (activtySpecialtys.getSpeId() > 0) {// 修改
					dao.update(activtySpecialtys);
				} else {
					dao.insert(activtySpecialtys);// 新增
				}

			}
		});

		result.put("status", ResultVo.STATUS_SUCCESS);
		result.put("msg", "数据保存成功");

		return result;
	}

	/**
	 * 专场活动删除
	 * 
	 * @return
	 */
	@At
	public Object delActivtySpecialty(@Param("speId") String speId) {

		Map<String, Object> result = new HashMap<String, Object>();

		dao.clear(ActivtySpecialty.class, Cnd.where("speId", "=", speId));

		result.put("status", ResultVo.STATUS_SUCCESS);
		result.put("msg", "数据删除成功");
		return result;
	}

	/**
	 * 热卖商品
	 * 
	 * @param type
	 *            1:热卖商品1，
	 * @return
	 */
	@At
	public Object queyActivtyHeatOneList() {

		return selectActivtyHeatList("1", 5, 1);
	}

	/**
	 * 热卖商品
	 * 
	 * @param type
	 *            2:热卖商品2，
	 * @return
	 */
	@At
	public Object queyActivtyHeatTwoList(@Param("rows") int pageSize,
			@Param("page") int pageNum) {

		return selectActivtyHeatList("2", pageSize, pageNum);
	}

	private Object selectActivtyHeatList(String type, int pageSize, int pageNum) {
		Map<String, Object> result = new HashMap<String, Object>();
		String sqlStr = "SELECT c.id, a.productId, a.name, a.activityType, a.minPrice, "
				+ "  (SELECT t.name  FROM Supplier t  WHERE a.supplierId = t.id) supplierName,  "
				+ " (SELECT t.catalogName  FROM catalog t  WHERE a.catalogId3 = t.catalogId) catalogName,     b.storeCount   "
				+ " FROM   (select productId, sum(storeCount) storeCount from producttype group by productId)  b, product a, activtyheat c   "
				+ " WHERE a.productId = b.productId  and  a.productId = c.productId"
				+ "  AND c.type = @type limit @pageNum, @pageSize  ";

		String sqlStr2 = "select count(1) from product a, activtyheat c where  a.productId = c.productId "
				+ " AND c.type = @type ";

		Sql sql = Sqls.create(sqlStr2);
		sql.params().set("type", type);
		sql.setCallback(Sqls.callback.integer());
		dao.execute(sql);
		int total = sql.getInt();

		result.put("total", total);
		if (total == 0) {
			return result;
		}

		Pager pager = dao.createPager(pageNum, pageSize);
		sql = Sqls.create(sqlStr);
		sql.params().set("pageNum", pager.getOffset());
		sql.params().set("pageSize", pager.getPageSize());
		sql.params().set("type", type);

		sql.setCallback(Sqls.callback.maps());
		dao.execute(sql);
		List<Product> list = sql.getList(Product.class);
		result.put("rows", list);
		return result;
	}

	/**
	 * 热卖商品保存
	 * 
	 * @param type
	 * @param productId
	 * @return
	 */
	@At
	public Object saveActivtyHeat(@Param("..") final ActivtyHeat activtyHeat) {

		Map<String, Object> result = new HashMap<String, Object>();

		if (StringUtils.isEmpty(activtyHeat.getProductId())
				|| StringUtils.isEmpty(activtyHeat.getType())) {
			result.put("status", ResultVo.STATUS_FAIL);
			result.put("msg", "保存参数不能为空!");
			return result;
		}

		Trans.exec(new Atom() {
			@Override
			public void run() {
				if (activtyHeat.getId() > 0) {
					dao.update(activtyHeat);
				} else {
					dao.insert(activtyHeat);
				}

				// //参加活动类型 0无活动 1限时活动 2热卖一 3热卖二
				int aactivtyType = activtyHeat.getType() == 1 ? 2 : 3;

				// 根据商品中对应的状态
				dao.update(Product.class,
						Chain.make("activityType", aactivtyType),
						Cnd.where("productId", "=", activtyHeat.getProductId()));
			}
		});

		result.put("status", ResultVo.STATUS_SUCCESS);
		result.put("msg", "数据保存成功");
		return result;
	}

	/**
	 * 热卖活动删除
	 * 
	 * @param activtyHeat
	 * @return
	 */
	@At
	public Object delActivtyHeat(@Param("..") final ActivtyHeat activtyHeat) {

		Map<String, Object> result = new HashMap<String, Object>();

		Trans.exec(new Atom() {
			@Override
			public void run() {
				dao.clear(
						ActivtyHeat.class,
						Cnd.where("id", "=", activtyHeat.getId()).and("type",
								"=", activtyHeat.getType()));

				// //参加活动类型 0无活动 1限时活动 2热卖一 3热卖二
				int aactivtyType = activtyHeat.getType() == 1 ? 2 : 3;

				// 根据商品中对应的状态
				dao.update(Product.class, Chain.make("activityType", 0), Cnd
						.where("productId", "=", activtyHeat.getProductId())
						.and("activityType", "=", aactivtyType));
			}
		});

		result.put("status", ResultVo.STATUS_SUCCESS);
		result.put("msg", "数据删除成功");
		return result;
	}

	/**
	 * 抽奖查询
	 * 
	 * @return
	 */
	@At
	public Object queryLotteryList(@Param("rows") int pageSize,
			@Param("page") int pageNum) {

		Map<String, Object> result = new HashMap<String, Object>();

		String sqlStr = " select a.orderId, a.userId, a.amount, a.chargePaidTime, "
				+ " (case when b.id  is not null then 1 else 0 end) isJoin "
				+ " from orders a left join activitylottery b on a.orderId = b.orderId"
				+ " where DATE_FORMAT(a.chargePaidTime, '%Y-%m-%d')  = @day  order by  a.chargePaidTime desc "
				+ " limit @pageNum, @pageSize ";

		String sqlStr2 = "select count(1) from orders a left join activitylottery b on a.orderId = b.orderId "
				+ "  where DATE_FORMAT(a.chargePaidTime, '%Y-%m-%d')  = @day ";

		Sql sql = Sqls.create(sqlStr2);
		sql.params().set(
				"day",
				TimeUtils.dateToStr(TimeUtils.getNextDay(new Date(), -1),
						TimeUtils.FORMAT10));// 昨天的时间

		sql.setCallback(Sqls.callback.integer());
		dao.execute(sql);
		int total = sql.getInt();
		result.put("total", total);
		if (total == 0) {
			return result;
		}

		sql = Sqls.create(sqlStr);
		sql.params().set(
				"day",
				TimeUtils.dateToStr(TimeUtils.getNextDay(new Date(), -1),
						TimeUtils.FORMAT10));// 昨天的时间
		sql.params().set("pageNum", pageNum);
		sql.params().set("pageSize", pageSize);

		sql.setCallback(Sqls.callback.maps());
		dao.execute(sql);
		List<Map> list = sql.getList(Map.class);

		String userId;
		for (Map map : list) {
			userId = String.valueOf(map.get("userId"));
			map.put("nickname", getOrderPersonal(userId).getNickname());// 会员昵称
		}

		result.put("rows", list);
		return result;
	}

	/**
	 * 抽奖设置是否参与
	 * 
	 * @param orderId
	 * @return
	 */
	@At
	public Object addLottery(@Param("orderId") int orderId) {
		Map<String, Object> result = new HashMap<String, Object>();

		if (StringUtils.isEmpty(orderId)) {
			result.put("status", ResultVo.STATUS_FAIL);
			result.put("msg", "保存参数不能为空!");
			return result;
		}

		ActivityLottery activityLottery = dao.fetch(ActivityLottery.class,
				Cnd.where("orderId", "=", orderId));

		if (activityLottery != null) {// 参与的删除就不参与
			dao.clear(ActivityLottery.class, Cnd.where("orderId", "=", orderId));
		} else {
			activityLottery = new ActivityLottery();
			activityLottery.setOrderId(orderId);
			activityLottery.setIsLottery(0);
			activityLottery.setInsertTime(new Date());
			dao.insert(activityLottery);
		}

		result.put("status", ResultVo.STATUS_SUCCESS);
		result.put("msg", "数据操作成功");
		return result;
	}

	/**
	 * 抽奖配置保存
	 * 
	 * @param orderId
	 * @return
	 */
	@At
	public Object addLotteryConf(@Param("lotteryTime") String lotteryTime,
			@Param("rate") String rate) {

		Map<String, Object> result = new HashMap<String, Object>();

		if (StringUtils.isEmpty(lotteryTime) || StringUtils.isEmpty(rate)) {
			result.put("status", ResultVo.STATUS_FAIL);
			result.put("msg", "保存参数不能为空!");
			return result;
		}

		ActivityLotteryConf conf = new ActivityLotteryConf();
		conf.setId(1);
		conf.setLotteryTime(lotteryTime);
		conf.setRate(Double.parseDouble(rate) / 100);// 页面上是%，这样除以100

		int count = dao.count(ActivityLotteryConf.class);
		if (count > 0) {
			dao.update(conf);
		} else {
			dao.insert(conf);
		}

		result.put("status", ResultVo.STATUS_SUCCESS);
		result.put("msg", "数据保存成功");
		return result;
	}

	/**
	 * 根据获取对应订单的会员信息
	 * 
	 * @return
	 */
	private Personal getOrderPersonal(String userId) {

		Personal personal = dao.fetch(Personal.class,
				Cnd.where("id", "=", userId));
		personal = personal == null ? new Personal() : personal;
		return personal;
	}

}