package com.easyshop.core.modules.admin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

import com.easy.core.filters.CheckBackUserLoginFilter;
import com.easyshop.bean.Brand;
import com.easyshop.bean.Catalog;
import com.easyshop.bean.Supplier;
import com.easyshop.utils.StringUtils;

/**
 * 销售统计
 * 
 * @author luocz
 */
@IocBean
@At("/admin/report")
@Ok("json")
@Fail("http:500")
@Filters({ @By(type = CheckBackUserLoginFilter.class) })
public class ReportModule {

	@Inject
	protected Dao dao;

	/**
	 * 销售统计
	 * 
	 * @return
	 */
	@At
	public Object salestatistic(@Param("type") String type,
			@Param("beginTime") String beginTime,
			@Param("endTime") String endTime, @Param("brand") String brand,
			@Param("kind") String kind, @Param("provider") String provider,
			@Param("rows") int pageSize, @Param("page") int pageNum) {

		List<String> paramsList = new ArrayList<String>();
		Map<String, Object> valMap = new HashMap<String, Object>();

		String sqlStr = " SELECT t.productId,t.name, t.minPrice, c.num, c.price "
				+ " FROM product t, "
				+ "    (SELECT b.productId,  SUM(b.number) num, SUM(b.number*b.price) price "
				+ "        FROM orders a, connectorop b "
				+ "        WHERE a.orderId = b.orderId "
				+ "        AND a.status IN (102,103,104,106 ) ";

		if (!StringUtils.isEmpty(beginTime)) {// 支付完成时间
			sqlStr += " and DATE_FORMAT(a.chargePaidTime, '%Y-%m-%d') >=@beginTime ";
			paramsList.add("beginTime");
			valMap.put("beginTime", beginTime);
		}

		if (!StringUtils.isEmpty(endTime)) {// 支付完成时间
			sqlStr += " and DATE_FORMAT(a.chargePaidTime, '%Y-%m-%d') <=@endTime ";
			paramsList.add("endTime");
			valMap.put("endTime", endTime);
		}

		sqlStr += "        GROUP BY b.productId ) c "
				+ " WHERE t.productId = c.productId ";

		if ("2".equals(type)) { // type 1 单品 2品牌， 3品类， 4供应商
			if (!StringUtils.isEmpty(brand)) {
				sqlStr += " and t.brandId = @brandId";
				paramsList.add("brandId");
				valMap.put("brandId", brand);
			}
		} else if ("3".equals(type)) {// 分类

			if (!StringUtils.isEmpty(kind)) {// 第3级
				sqlStr += " and (t.catalogId1 = @kind or t.catalogId2 = @kind or t.catalogId3 = @kind)";
				paramsList.add("kind");
				valMap.put("kind", kind);
			}
		} else if ("4".equals(type)) {// 供应商
			if (!StringUtils.isEmpty(provider)) {// 第3级
				sqlStr += " and t.provider = @provider";
				paramsList.add("provider");
				valMap.put("provider", provider);
			}
		}

		Pager pager = dao.createPager(pageNum, pageSize);
		sqlStr += " limit @pageNum, @pageSize ";

		Sql sql = Sqls.create(sqlStr);

		sql.params().set("pageNum", pager.getOffset());
		sql.params().set("pageSize", pager.getPageSize());
		for (String str : paramsList) {
			sql.params().set(str, valMap.get(str));
		}

		sql.setCallback(Sqls.callback.maps());
		dao.execute(sql);
		List<Map> list = sql.getList(Map.class);
		return list;
	}

	/**
	 * 销售报表
	 * 
	 * @return
	 */
	@At
	public Object salesreport(@Param("type") String type,
			@Param("beginTime") String beginTime,
			@Param("endTime") String endTime, @Param("rows") int pageSize,
			@Param("page") int pageNum) {

		// <option value="1">销售</option>
		// <option value="2">退换货</option>

		List<String> paramsList = new ArrayList<String>();
		Map<String, Object> valMap = new HashMap<String, Object>();

		String sqlStr = "SELECT c.productId, c.name, SUM(b.number) num,SUM(a.amount) amount, "
				+ "    SUM(t.supplyPrice) supplyPrice, SUM(a.amount)-SUM(t.supplyPrice) amount2,  "
				+ "( SELECT s.name FROM  supplier s WHERE c.supplierId = s.id) supplierName,  "
				+ "( SELECT s.brandName FROM brand s WHERE c.brandId = s.brandId) brandName, "
				+ "( SELECT s.catalogName FROM catalog s WHERE c.catalogId3 = s.catalogId) catalogName "
				+ " FROM orders a, connectorOP b, "
				+ "    producttype t, product c "
				+ "WHERE a.orderId = b.orderId "
				+ "AND b.productTypeId = t.productTypeId "
				+ "AND t.productId = c.productId ";

		if (!StringUtils.isEmpty(beginTime)) {// 支付完成时间
			sqlStr += " and DATE_FORMAT(a.chargePaidTime, '%Y-%m-%d') >=@beginTime ";
			paramsList.add("beginTime");
			valMap.put("beginTime", beginTime);
		}

		if (!StringUtils.isEmpty(endTime)) {// 支付完成时间
			sqlStr += " and DATE_FORMAT(a.chargePaidTime, '%Y-%m-%d') <=@endTime ";
			paramsList.add("endTime");
			valMap.put("endTime", endTime);
		}

		if ("1".equals(type)) {// 销售
			sqlStr += "  AND a.status IN (102,103,104,106 ) ";
		} else if ("2".equals(type)) {// 退换货
			sqlStr += "  AND a.status IN (105 ) ";
		}

		sqlStr += "GROUP BY c.productId, c.name, c.supplierId, c.brandId, c.catalogId3 "
				+ "  limit @pageNum, @pageSize";

		Pager pager = dao.createPager(pageNum, pageSize);
		Sql sql = Sqls.create(sqlStr);
		sql.params().set("pageNum", pager.getOffset());
		sql.params().set("pageSize", pager.getPageSize());
		for (String str : paramsList) {
			sql.params().set(str, valMap.get(str));
		}

		sql.setCallback(Sqls.callback.maps());
		dao.execute(sql);
		List<Map> list = sql.getList(Map.class);
		return list;
	}

	/**
	 * 抽奖统计
	 * 
	 * @return
	 */
	@At
	public Object luckstatistic(@Param("beginTime") String beginTime,
			@Param("endTime") String endTime, @Param("rows") int pageSize,
			@Param("page") int pageNum) {

		List<String> paramsList = new ArrayList<String>();
		Map<String, Object> valMap = new HashMap<String, Object>();

		String sqlStr = "SELECT a.orderId, t.nickname,a.amount,a.chargePaidTime, "
				+ "    b.lotteryTime "
				+ "FROM orders a, personal t,  activitylottery b "
				+ "WHERE  a.userId = t.id " + "AND a.orderId = b.orderId ";

		if (!StringUtils.isEmpty(beginTime)) {// 支付完成时间
			sqlStr += " and DATE_FORMAT(a.chargePaidTime, '%Y-%m-%d') >=@beginTime ";
			paramsList.add("beginTime");
			valMap.put("beginTime", beginTime);
		}

		if (!StringUtils.isEmpty(endTime)) {// 支付完成时间
			sqlStr += " and DATE_FORMAT(a.chargePaidTime, '%Y-%m-%d') <=@endTime ";
			paramsList.add("endTime");
			valMap.put("endTime", endTime);
		}

		sqlStr += " ORDER BY b.lotteryTime  limit @pageNum, @pageSize";

		Pager pager = dao.createPager(pageNum, pageSize);
		Sql sql = Sqls.create(sqlStr);
		sql.params().set("pageNum", pager.getOffset());
		sql.params().set("pageSize", pager.getPageSize());
		for (String str : paramsList) {
			sql.params().set(str, valMap.get(str));
		}

		sql.setCallback(Sqls.callback.maps());
		dao.execute(sql);
		List<Map> list = sql.getList(Map.class);
		return list;
	}

	/**
	 * 运费统计
	 * 
	 * @return
	 */
	@At
	public Object transportstatistic(@Param("beginTime") String beginTime,
			@Param("endTime") String endTime, @Param("rows") int pageSize,
			@Param("page") int pageNum) {

		List<String> paramsList = new ArrayList<String>();
		Map<String, Object> valMap = new HashMap<String, Object>();
		String sqlStr = "SELECT a.orderId, c.name, b.price,b.number,a.amount,a.chargePaidTime, 0 expressAmount "
				+ "FROM orders a, connectorOP b, product c "
				+ "WHERE  a.orderId = b.orderId "
				+ "AND b.productId = c.productId ";
		if (!StringUtils.isEmpty(beginTime)) {// 支付完成时间
			sqlStr += " and DATE_FORMAT(a.chargePaidTime, '%Y-%m-%d') >=@beginTime ";
			paramsList.add("beginTime");
			valMap.put("beginTime", beginTime);
		}

		if (!StringUtils.isEmpty(endTime)) {// 支付完成时间
			sqlStr += " and DATE_FORMAT(a.chargePaidTime, '%Y-%m-%d') <=@endTime ";
			paramsList.add("endTime");
			valMap.put("endTime", endTime);
		}
		sqlStr += " ORDER BY a.chargePaidTime  limit @pageNum, @pageSize";

		Pager pager = dao.createPager(pageNum, pageSize);
		Sql sql = Sqls.create(sqlStr);
		sql.params().set("pageNum", pager.getOffset());
		sql.params().set("pageSize", pager.getPageSize());
		for (String str : paramsList) {
			sql.params().set(str, valMap.get(str));
		}

		sql.setCallback(Sqls.callback.maps());
		dao.execute(sql);
		List<Map> list = sql.getList(Map.class);
		return list;
	}

	/**
	 * 下拉框选择供应商
	 * 
	 * @return
	 */
	@At
	public Object loadSupplierList() {
		return dao.query(Supplier.class, Cnd.NEW().desc("name"));
	}

	/**
	 * 加载分类
	 * 
	 * @return
	 */
	@At
	public Object loadCatalogIdList() {
		return dao.query(Catalog.class,
				Cnd.NEW().desc("catalogName").desc("pid"));
	}

	/**
	 * 加载品牌
	 * 
	 * @return
	 */
	@At
	public Object loadBrandList() {
		return dao.query(Brand.class, Cnd.NEW().desc("brandName"));
	}
}
