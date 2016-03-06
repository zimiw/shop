package com.easyshop.core.modules;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.nutz.aop.interceptor.ioc.TransAop;
import org.nutz.dao.Cnd;
import org.nutz.dao.Dao;
import org.nutz.dao.Sqls;
import org.nutz.dao.entity.Record;
import org.nutz.dao.pager.Pager;
import org.nutz.dao.sql.Sql;
import org.nutz.ioc.aop.Aop;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.mvc.annotation.At;
import org.nutz.mvc.annotation.Fail;
import org.nutz.mvc.annotation.Ok;
import org.nutz.mvc.annotation.Param;
import org.nutz.trans.Atom;
import org.nutz.trans.Trans;

import com.easyshop.bean.ActivityProduct;
import com.easyshop.bean.Images;
import com.easyshop.bean.Product;
import com.easyshop.bean.ProductType;
import com.easyshop.utils.ExecuteStatus;
import com.easyshop.utils.StringUtils;
import com.easyshop.vo.ResultVo;

@IocBean
@At("/product")
@Ok("json")
@Fail("http:500")
public class ProductModule {

	@Inject
	protected Dao dao;
	private Map<String, Object> result;

	@Inject
	protected FrontDetailModule frontDetailModule;

	/**
	 * ===============================================================
	 *
	 * 新版本开始
	 *
	 *
	 * ===============================================================
	 */

	/**
	 *
	 * 新增商品 http://localhost:8181/easyshop/product/addProductByAll?name=2222&
	 * originPrice
	 * =10@20&currentPrice=8@12&supplyPrice=3@4&storeCount=30@40&SupplierId
	 * =2&catalogId1
	 * =11&catalogId2=22&catalogId3=33&country=america&color=red@blue&
	 * catalogId1=35&brandId=6&size=XL@XXL&topImg=13@23&detailImg=10@110
	 *
	 * 正常返回:{"status":"success"} 如果错误返回:{"status":"fail","msg", "添加商品错误"}
	 *
	 * 注意:有几个字段是用@分割的:colors sizes storeCounts supplyPrices currentPrices
	 * originPrices 因为这是这是一种商品的几种型号.
	 *
	 */
	@At
	public Object addProductByAll(@Param("name") String name,
			@Param("originPrice") String originPrice,
			@Param("currentPrice") String currentPrice,
			@Param("supplyPrice") String supplyPrice,
			@Param("isHotKey") boolean isHotKey,
			@Param("topImg") String topImg,
			@Param("storeCount") String storeCount,
			@Param("status") Boolean status,
			@Param("SupplierId") int SupplierId,
			@Param("catalogId1") int catalogId1,
			@Param("catalogId2") int catalogId2,
			@Param("catalogId3") int catalogId3,
			@Param("country") String country,
			@Param("detailImg") String detailImg,
			@Param("brandId") int brandId, @Param("color") String color,
			@Param("size") String size, @Param("unit") String unit) {
		this.result = new HashMap<String, Object>();
		// 要向两张表中进行添加数据
		final Product product = new Product();
		final ArrayList<ProductType> types = new ArrayList<ProductType>();
		final ArrayList<Images> images = new ArrayList<Images>();
		final ExecuteStatus es = new ExecuteStatus();

		// 首先添加商品规格

		// 进行颜色，尺码，库存拆分
		String[] colors = color.split("@");
		String[] sizes = size.split("@");
		String[] storeCounts = storeCount.split("@");
		String[] supplyPrices = supplyPrice.split("@");
		String[] currentPrices = currentPrice.split("@");
		String[] originPrices = originPrice.split("@");
		String[] topImgs = topImg.split("@");
		String[] detailImgs = detailImg.split("@");
		float minPrice = 0;
		// int totalCount=0;
		for (int i = 0; i < colors.length; i++) {
			// 找出最小值,搜索排序的时候用.
			if (i == 0 || minPrice > Float.parseFloat(currentPrices[i])) {
				minPrice = Float.parseFloat(currentPrices[i]);
			}
			// totalCount+=Integer.parseInt(storeCounts[i]);
			ProductType type = new ProductType();
			type.setCurrentPrice(Float.parseFloat(currentPrices[i]));
			type.setOriginPrice(Float.parseFloat(originPrices[i]));
			type.setSupplyPrice(Float.parseFloat(supplyPrices[i]));
			type.setColor(colors[i]);
			type.setSize(sizes[i]);
			type.setStoreCount(Integer.parseInt(storeCounts[i]));
			// type.setUnit(unit);
			type.setSellCount(0);
			types.add(type);
		}

		// 设置图片
		for (int i = 0; i < topImgs.length; i++) {
			Images image = new Images();
			image.setImgsource(topImgs[i]);
			image.setDetailPic(false);
			image.setTopimg(true);
			images.add(image);
		}
		for (int i = 0; i < detailImgs.length; i++) {
			Images image = new Images();
			image.setImgsource(detailImgs[i]);
			image.setDetailPic(true);
			image.setTopimg(false);
			images.add(image);
		}

		// 其次添加商品信息
		product.setInputDate(new Date());
		product.setName(name);
		product.setStatus(status != null);
		product.setHotKey(isHotKey);
		product.setBrandId(brandId);
		product.setUnit(unit);
		product.setCountry(country);
		product.setMinPrice(minPrice);
		product.setSupplierId(SupplierId);
		product.setCatalogId1(catalogId1);
		product.setCatalogId2(catalogId2);
		product.setCatalogId3(catalogId3);
		// 设置一些默认属性
		product.setViewCount(0);
		product.setSellCount(0);
		product.setActivityType(0);

		// Begin transaction
		Trans.exec(new Atom() {
			@Override
			public void run() {
				Product p = dao.insert(product);
				for (ProductType pt : types) {
					pt.setProductId(p.getProductId());
				}
				for (Images i : images) {
					i.setProductId(p.getProductId());
				}
				dao.insert(types);
				dao.insert(images);
				es.setStatus(true);
			}
		});

		// End transaction
		if (es.isStatus()) {
			this.result.put("status", "success");
		} else {
			this.result.put("status", "fail");
			this.result.put("msg", "添加商品错误");
		}
		return this.result;
	}

	/**
	 *
	 * 编辑商品
	 * http://localhost:8181/easyshop/product/updateProductByAll?productId=22
	 * &productTypeId
	 * =166@0&sellCount=0@0&limitActivityLeftCount=0@0&specialPrice=0.01
	 * 
	 * @0.02%20&name 
	 *               =2222&originPrice=10@20&currentPrice=8@12&supplyPrice=3@4&storeCount
	 *               =30@40&
	 *               SupplierId=2&catalogId1=11&catalogId2=22&catalogId3=
	 *               33&country=america &color
	 *               =red@blue&catalogId1=35&brandId=6&
	 *               size=XL@XXL&topImg=1113@1123&detailImg =1110@11110@11111
	 *
	 *
	 *               正常返回:{"status":"success"} 如果错误返回:{"status":"fail","msg",
	 *               "编辑商品错误"}
	 *
	 *               有几个字段是用@分割的:colors sizes storeCounts supplyPrices
	 *               currentPrices originPrices 因为这是这是一种商品的几种型号. 注意编辑比新增多了几个字段
	 *               productTypeIds sellCounts limitActivityLeftCount
	 *               specialPrices 依然都是用@分割的
	 *               还要注意,如果某一个型号是新增的,那么这个型号的productTypeId 设置为0 .如果是修改的,保持原
	 *               productTypeId 不变.
	 */
	@At
	public Object updateProductByAll(@Param("productId") int productId,
			@Param("name") String name,
			@Param("originPrice") String originPrice,
			@Param("currentPrice") String currentPrice,
			@Param("supplyPrice") String supplyPrice,
			@Param("productTypeId") String productTypeId,
			@Param("sellCount") String sellCount,
			@Param("limitActivityLeftCount") String limitActivityLeftCount,
			@Param("specialPrice") String specialPrice,
			@Param("isHotKey") boolean isHotKey,
			@Param("topImg") String topImg,
			@Param("storeCount") String storeCount,
			@Param("status") Boolean status,
			@Param("SupplierId") int SupplierId,
			@Param("catalogId1") int catalogId1,
			@Param("catalogId2") int catalogId2,
			@Param("catalogId3") int catalogId3,
			@Param("country") String country,
			@Param("detailImg") String detailImg,
			@Param("brandId") int brandId, @Param("color") String color,
			@Param("size") String size, @Param("unit") String unit) {

		// 编辑必须先下架再保存.

		this.result = new HashMap<String, Object>();
		final Product product = new Product();
		product.setProductId(productId);
		final ArrayList<ProductType> types = new ArrayList<ProductType>();
		final ArrayList<Images> images = new ArrayList<Images>();
		final ExecuteStatus es = new ExecuteStatus();

		// 首先删除所有的商品规格，再添加
		// 进行颜色，尺码，库存拆分
		String[] colors = color.split("@");
		String[] sizes = size.split("@");
		String[] storeCounts = storeCount.split("@");
		String[] supplyPrices = supplyPrice.split("@");
		String[] currentPrices = currentPrice.split("@");
		String[] originPrices = originPrice.split("@");
		// 下面这四个是编辑新加的属性.
		String[] productTypeIds = productTypeId.split("@");
		String[] sellCounts = sellCount.split("@");
		String[] limitActivityLeftCounts = limitActivityLeftCount.split("@");
		String[] specialPrices = specialPrice.split("@");
		float minPrice = 0;
		for (int i = 0; i < colors.length; i++) {
			// 找出最小值,搜索排序的时候用.
			if (i == 0 || minPrice > Float.parseFloat(currentPrices[i])) {
				minPrice = Float.parseFloat(currentPrices[i]);
			}
			ProductType type = new ProductType();
			type.setCurrentPrice(Float.parseFloat(currentPrices[i]));
			type.setOriginPrice(Float.parseFloat(originPrices[i]));
			type.setSupplyPrice(Float.parseFloat(supplyPrices[i]));
			type.setColor(colors[i]);
			type.setSize(sizes[i]);
			type.setStoreCount(Integer.parseInt(storeCounts[i]));
			type.setProductId(productId);
			type.setSellCount(Integer.parseInt(sellCounts[i]));
			type.setSpecialPrice(Float.parseFloat(specialPrices[i]));
			type.setLimitActivityLeftCount(Integer
					.parseInt(limitActivityLeftCounts[i]));
			type.setProductTypeId(Integer.parseInt(productTypeIds[i]));
			// type.setUnit(unit);
			types.add(type);
		}

		String[] topImgs = topImg.split("@");
		String[] detailImgs = detailImg.split("@");
		// 设置图片
		for (int i = 0; i < topImgs.length; i++) {
			Images image = new Images();
			image.setImgsource(topImgs[i]);
			image.setDetailPic(false);
			image.setTopimg(true);
			image.setProductId(productId);
			images.add(image);
		}
		for (int i = 0; i < detailImgs.length; i++) {
			Images image = new Images();
			image.setImgsource(detailImgs[i]);
			image.setDetailPic(true);
			image.setTopimg(false);
			image.setProductId(productId);
			images.add(image);
		}

		// 其次添加商品信息
		product.setInputDate(new Date());
		product.setName(name);
		product.setStatus(status != null);
		product.setHotKey(isHotKey);
		product.setBrandId(brandId);
		product.setUnit(unit);
		product.setCountry(country);
		product.setMinPrice(minPrice);
		product.setSupplierId(SupplierId);
		product.setCatalogId1(catalogId1);
		product.setCatalogId2(catalogId2);
		product.setCatalogId3(catalogId3);

		// Begin transaction
		Trans.exec(new Atom() {
			@Override
			public void run() {
				dao.updateIgnoreNull(product);
				Sql sql = Sqls
						.create("delete from producttype  where   productId=@productId");
				sql.params().set("productId", product.getProductId());
				dao.execute(sql);
				sql = Sqls
						.create("delete from images  where   productId=@productId");
				sql.params().set("productId", product.getProductId());
				dao.execute(sql);
				// dao.insert(types);
				for (ProductType pt : types) {
					// 如果是0代表是新增的.
					if (pt.getProductTypeId() == 0) {
						dao.insert(pt);
					} else {
						int oldProductTypeId = pt.getProductTypeId();
						// 必须要清零,不然下一行代码返回的主键不变
						pt.setProductTypeId(0);
						ProductType newPt = dao.insert(pt);
						sql = Sqls
								.create("update producttype set productTypeId=@oldProductTypeId where productTypeId=@productTypeId");
						sql.params().set("productTypeId",
								newPt.getProductTypeId());
						sql.params().set("oldProductTypeId", oldProductTypeId);
						dao.execute(sql);
					}
				}
				dao.insert(images);
				es.setStatus(true);
			}
		});
		// End transaction
		if (es.isStatus()) {
			this.result.put("status", "success");
		} else {
			this.result.put("status", "fail");
			this.result.put("msg", "编辑商品错误");
		}
		return this.result;
	}

	/**
	 *
	 * http://localhost:8181/easyshop/product/getAllProductInfo4Update?productId
	 * =26 获取商品的所有信息
	 *
	 * 返回: json字符串 { "originPrice": 0, "currentPrice": 0, "productId": 26,
	 * "name": "2222", "minPrice": 8, "activityType": 0, "limitActivityStatus":
	 * 0, "limitActivityTotalCount": 0, "limitActivityTotalLeftCount": 0,
	 * "weight": 0, "inputDate": "2016-01-27 09:59:29", "viewCount": 0,
	 * "sellCount": 0, "status": false, "supplierId": 2, "catalogId1": 11,
	 * "catalogId2": 22, "catalogId3": 33, "catalogId": 0, "isHotKey": false,
	 * "isNew": false, "brandId": 6, "specialPrice": 0, "activityId": 0,
	 * "country": "america", "productTypes": [ { "productTypeId": 152, "size":
	 * "XL", "color": "red", "productId": 26, "storeCount": 30, "sellCount": 0,
	 * "supplyPrice": 3, "originPrice": 10, "currentPrice": 8, "specialPrice":
	 * 0, "limitActivityLeftCount": 0 }, { "productTypeId": 153, "size": "XXL",
	 * "color": "blue", "productId": 26, "storeCount": 40, "sellCount": 0,
	 * "supplyPrice": 4, "originPrice": 20, "currentPrice": 12, "specialPrice":
	 * 0, "limitActivityLeftCount": 0 } ], "topImgs": [ { "imgID": 62,
	 * "imgsource": "13", "isTopimg": true, "productId": 26, "isSmallView":
	 * false, "isSizePic": false, "isDetailPic": false, "orderId": 0 }, {
	 * "imgID": 63, "imgsource": "23", "isTopimg": true, "productId": 26,
	 * "isSmallView": false, "isSizePic": false, "isDetailPic": false,
	 * "orderId": 0 } ], "detailImgs": [ { "imgID": 64, "imgsource": "10",
	 * "isTopimg": false, "productId": 26, "isSmallView": false, "isSizePic":
	 * false, "isDetailPic": true, "orderId": 0 }, { "imgID": 65, "imgsource":
	 * "110", "isTopimg": false, "productId": 26, "isSmallView": false,
	 * "isSizePic": false, "isDetailPic": true, "orderId": 0 } ], "number": 0,
	 * "price": 0 }
	 *
	 * @param productId
	 * @return
	 */
	@At
	public Object getAllProductInfo4Update(@Param("productId") int productId) {

		// return frontDetailModule.getDetailInfo(productId);

		Product product = dao.fetch(Product.class,
				Cnd.where("productId", "=", productId));
		product.setDetailImgs(dao.query(
				Images.class,
				Cnd.where("productId", "=", productId).and("isDetailPic", "=",
						true)));
		product.setTopImgs(dao.query(
				Images.class,
				Cnd.where("productId", "=", productId).and("isTopimg", "=",
						true)));
		product.setProductTypes(dao.query(ProductType.class,
				Cnd.where("productId", "=", productId)));
		return product;
	}

	/**
	 * 设置上架下架:
	 * http://localhost:8181/easyshop/product/setProductStatus?productIds
	 * =26&status=1
	 * http://localhost:8181/easyshop/product/setProductStatus?productIds
	 * =26,28&status=1 如果是批量上架下架,那么: 各个id用分号隔开 status 1表示上架 0表示下架
	 * 返回:{"status":"success"}
	 * 
	 * @param status
	 * @param productIds
	 * @return
	 */
	@At
	public Object setProductStatus(@Param("status") int status,
			@Param("productIds") String productIds) {
		Sql sql = Sqls
				.create("update product set status=@status where productId in ($productIds)");
		sql.params().set("status", status);
		sql.vars().set("productIds", productIds);
		dao.execute(sql);
		return new ResultVo();
	}

	/**
	 * http://localhost:8181/easyshop/product/deleteProduct?productIds=26
	 * http://localhost:8181/easyshop/product/deleteProduct?productIds=26,27
	 * 如果是批量上架下架,那么: 各个id用分号隔开 {"status":"success"}
	 * 
	 * @param productIds
	 * @return
	 */
	@At
	@Aop(TransAop.READ_COMMITTED)
	public Object deleteProduct(@Param("productIds") String productIds) {
		Sql sql = Sqls
				.create("DELETE from product  where productId in ($productIds)");
		sql.vars().set("productIds", productIds);
		dao.execute(sql);
		sql = Sqls
				.create("DELETE from producttype  where productId in ($productIds)");
		sql.vars().set("productIds", productIds);
		dao.execute(sql);
		sql = Sqls
				.create("DELETE from images  where productId in ($productIds)");
		sql.vars().set("productIds", productIds);
		dao.execute(sql);
		return new ResultVo();
	}

	/**
	 * 分页查询所有商品
	 * http://localhost:8181/easyshop/product/queryAllProducts?currentPage
	 * =1&peerpageRows=2
	 * http://localhost:8181/easyshop/product/queryAllProducts?
	 * currentPage=1&peerpageRows
	 * =2&name=22&status=1&catalogId1=11&catalogId2=22&
	 * catalogId3=33&brandId=6&supplierId=2
	 *
	 * 返回:{"total":16,"rows":[{"productid":26,"originprice":0.0,"currentprice":
	 * 0.0
	 * ,"name":"2222","minprice":8.0,"activitytype":0,"limitactivitystatus":0,
	 * "limitactivitytotalcount"
	 * :0,"limitactivitytotalleftcount":0,"limitactivitystarttime"
	 * :"2016-01-28 10:32:21"
	 * ,"limitactivityendtime":"2016-01-21 10:32:28","limitactivityupdatetime"
	 * :"2016-01-29 10:32:24"
	 * ,"weight":0.0,"inputdate":"2016-01-27 09:59:29","viewcount"
	 * :0,"sellcount":
	 * 0E-10,"status":true,"detailtitle":"111","descp":"222","supplierid"
	 * :2,"catalogid1"
	 * :35,"catalogid2":39,"catalogid3":40,"catalogid":0,"ishotkey"
	 * :false,"isnew"
	 * :false,"brandid":6,"specialprice":0.0,"activityid":0,"country"
	 * :"america","brandname"
	 * :"channel","catalogname1":"时尚美妆","catalogname2":"进口美食"
	 * ,"catalogname3":"母婴用品"
	 * ,"suppliername":"channel 供应商2","totalleftcount":70,"producttypes"
	 * :[{"productTypeId"
	 * :152,"size":"XL","color":"red","productId":26,"storeCount"
	 * :30.0,"sellCount"
	 * :0.0,"supplyPrice":3.0,"originPrice":10.0,"currentPrice":
	 * 8.0,"specialPrice":0.0,"limitActivityLeftCount":0},
	 * {"productTypeId":153,"size"
	 * :"XXL","color":"blue","productId":26,"storeCount"
	 * :40.0,"sellCount":0.0,"supplyPrice"
	 * :4.0,"originPrice":20.0,"currentPrice":
	 * 12.0,"specialPrice":0.0,"limitActivityLeftCount":0}]},
	 * {"productid":3,"originprice"
	 * :0.0,"currentprice":0.01,"name":"雅诗兰黛鲜亮焕采精粹水（特润型）"
	 * ,"inputdate":"2016-01-22 04:34:06"
	 * ,"viewcount":0,"sellcount":0E-10,"status"
	 * :true,"detailtitle":"","descp":""
	 * ,"catalogid":0,"ishotkey":false,"provider"
	 * :"北京美瑶","provideraddress":"北京","isnew"
	 * :false,"brandid":14,"wlstatus":"","paystatus"
	 * :"","orderstatus":"","unit":"瓶"
	 * ,"specialprice":595.0,"activityid":9,"country"
	 * :"美国","external":"","brandname"
	 * :"雅诗兰黛","totalleftcount":12,"producttypes":
	 * [{"productTypeId":141,"size":"50ml"
	 * ,"color":"棕色","unit":"瓶","productId":3,
	 * "storeCount":12.0,"sellCount":0.0,"supplyPrice"
	 * :0.0,"originPrice":336.0,"currentPrice"
	 * :125.0,"specialPrice":0.0,"limitActivityLeftCount":0}]}]}
	 * 如果没有数据:{"total":0,"rows":[]}
	 *
	 * @param currentPage
	 * @param peerpageRows
	 * @return
	 */
	@At
	public Object queryAllProducts(@Param("page") int currentPage,
			@Param("rows") int peerpageRows,
			@Param("productId") String productId, @Param("name") String name,
			@Param("catalogId1") String catalogId1,
			@Param("catalogId2") String catalogId2,
			@Param("catalogId3") String catalogId3,
			@Param("brandId") Integer brandId,
			@Param("supplierId") Integer supplierId,
			@Param("status") Boolean status) {
		// 设置分页参数
		Pager pager = dao.createPager(currentPage, peerpageRows);
		// 设置查询条件
		// todo 目前没有做SQL注入的防攻击,以后可以加一下,只过滤这个whereSqlStr即可.
		String whereSqlStr = " 1=1";
		if (!StringUtils.isEmpty(productId)) {
			whereSqlStr += " and product.productId=" + productId;
		}
		if (!StringUtils.isEmpty(name)) {
			whereSqlStr += " and product.name like '%" + name + "%'";
		}
		if (!StringUtils.isEmpty(catalogId1)) {
			whereSqlStr += " and product.catalogId1=" + catalogId1;
		}
		if (!StringUtils.isEmpty(catalogId2)) {
			whereSqlStr += " and product.catalogId2=" + catalogId2;
		}
		if (!StringUtils.isEmpty(catalogId3)) {
			whereSqlStr += " and product.catalogId3=" + catalogId3;
		}
		if (brandId != null) {
			whereSqlStr += " and product.brandId=" + brandId;
		}
		if (supplierId != null) {
			whereSqlStr += " and product.supplierId=" + supplierId;
		}
		if (status != null) {
			whereSqlStr += " and product.status=" + status;
		}

		this.result = new HashMap<String, Object>();
		this.result.put("total",
				dao.count(Product.class, Cnd.wrap(whereSqlStr)));
		// 首先查询出商品
		StringBuffer sb = new StringBuffer();
		sb.append("SELECT product.*, b.brandName,c1.catalogName catalogName1,c2.catalogName catalogName2,"
				+ "c3.catalogName catalogName3,  a.name  supplierName FROM product ");
		sb.append(" LEFT JOIN supplier a ON product.supplierId = a.id ");
		sb.append(" LEFT JOIN brand b ON product.brandId = b.brandId ");
		sb.append(" LEFT JOIN catalog c1 ON product.catalogId1 = c1.catalogId ");
		sb.append(" LEFT JOIN catalog c2 ON product.catalogId2 = c2.catalogId ");
		sb.append(" LEFT JOIN catalog c3 ON product.catalogId3 = c3.catalogId ");
		sb.append(" where ");
		sb.append(whereSqlStr);
		sb.append(" ORDER BY product.inputDate desc ");
		sb.append(" limit  " + pager.getOffset() + "," + pager.getPageSize());
		Sql sql2 = Sqls.queryRecord(sb.toString());
		dao.execute(sql2);
		List<Record> list = sql2.getList(Record.class);
		for (Record re : list) {
			// ModuleBean m = re.toEntity(dao.getEntity(ModuleBean.class, "a.");
			// String projectName = re.getString("b.PROJECT_NAME");
			// 继续你想做的事
			// 再查出每个商品对应的规格参数
			int curProductId = Integer.parseInt(re.getString("productId"));
			List<ProductType> pts = dao.query(ProductType.class,
					Cnd.where("productId", "=", curProductId));
			int totalLeftCount = 0;
			for (ProductType pt : pts) {
				totalLeftCount += pt.getStoreCount()
						+ pt.getLimitActivityLeftCount();
			}
			re.put("totalLeftCount", totalLeftCount);
			re.put("productTypes", pts);
		}
		this.result.put("rows", list);
		return this.result;
	}

	/*
	 * @At public Object queryAllProducts2(@Param("currentPage")int
	 * currentPage,@Param("peerpageRows")int peerpageRows,
	 * 
	 * @Param("productId")String productId,@Param("name")String name,
	 * 
	 * @Param("catalogId1")String catalogId1,@Param("catalogId2")String
	 * catalogId2,
	 * 
	 * @Param("catalogId3")String catalogId3,@Param("brandId")Integer brandId,
	 * 
	 * @Param("supplierId")Integer supplierId,
	 * 
	 * @Param("status")Boolean status ){ if
	 * (StringUtils.isEmpty(currentPage,peerpageRows)) { return new
	 * ResultVo("fail", "参数有误"); } //设置分页参数 Pager pager =
	 * dao.createPager(currentPage, peerpageRows); //设置查询条件 Cnd
	 * condition=Cnd.where("1","=",1); if (!StringUtils.isEmpty(productId)) {
	 * condition.and("productId", "=", Integer.parseInt(productId)); } if
	 * (!StringUtils.isEmpty(name)) { condition.and("name", "like", "%" + name +
	 * "%"); } if (!StringUtils.isEmpty(catalogId1)) {
	 * condition.and("catalogId1", "=", Integer.parseInt(catalogId1)); } if
	 * (!StringUtils.isEmpty(catalogId2)) { condition.and("catalogId2", "=",
	 * Integer.parseInt(catalogId2)); } if (!StringUtils.isEmpty(catalogId3)) {
	 * condition.and("catalogId3", "=", Integer.parseInt(catalogId3)); } if
	 * (brandId!=null) { condition.and("brandId", "=", brandId); } if
	 * (supplierId!=null) { condition.and("supplierId", "=", supplierId); } if
	 * (status!=null) { condition.and("status", "=", status); }
	 * 
	 * this.result=new HashMap<String,Object>(); this.result.put("total",
	 * dao.count(Product.class,condition)); //首先查询出商品 List<Product>
	 * products=dao.query(Product.class,condition,pager); //再查出每个商品对应的规格参数
	 * for(Product p : products){ List<ProductType> pts =
	 * dao.query(ProductType.class,
	 * Cnd.where("productId","=",p.getProductId())); int totalLeftCount=0; for
	 * (ProductType pt : pts) {
	 * totalLeftCount+=pt.getStoreCount()+pt.getLimitActivityLeftCount(); }
	 * p.setTotalLeftCount(totalLeftCount); p.setProductTypes(pts); }
	 * this.result.put("rows", products); return this.result; }
	 */

	/**
	 * ===============================================================
	 *
	 * 新版本结束
	 *
	 * ===============================================================
	 */

	/**
	 *
	 * @param ID
	 * @param pageNo
	 * @param pageSize
	 * @return
	 */
	@At
	public Object getAllProducts(@Param("productId") int ID,
			@Param("currentPage") int pageNo,
			@Param("peerpageRows") int pageSize) {
		// 设置分页参数
		Pager pager = dao.createPager(pageNo, pageSize);
		List<Product> temps = dao.query(Product.class, null);
		this.result = new HashMap<String, Object>();
		this.result.put("total", temps.size());
		// 首先查询出商品
		List<Product> products = dao.query(Product.class, null, pager);
		// 再查出每个商品对应的规格参数
		for (Product p : products) {
			List<ProductType> pts = dao.query(ProductType.class,
					Cnd.where("productId", "=", p.getProductId()));
			p.setProductTypes(pts);
		}
		this.result.put("rows", products);
		return this.result;
	}

	@At
	public Object addProduct(@Param("name") String name,
			@Param("originPrice") String originPrice,
			@Param("currentPrice") String currentPrice,
			@Param("isHotKey") boolean isHotKey,
			@Param("storeCount") String storeCount,
			@Param("status") boolean status,
			@Param("provider") String provider,
			@Param("providerAddress") String providerAddress,
			@Param("country") String country, @Param("brandId") int brandId,
			@Param("color") String color, @Param("size") String size,
			@Param("unit") String unit) {
		this.result = new HashMap<String, Object>();
		// 要向两张表中进行添加数据
		final Product product = new Product();
		final ArrayList<ProductType> types = new ArrayList<ProductType>();
		final ExecuteStatus es = new ExecuteStatus();

		// 首先添加商品规格

		// 进行颜色，尺码，库存拆分
		String[] colors = color.split("@");
		String[] sizes = size.split("@");
		String[] storeCounts = storeCount.split("@");
		String[] currentPrices = currentPrice.split("@");
		String[] originPrices = originPrice.split("@");
		for (int i = 0; i < colors.length; i++) {
			ProductType type = new ProductType();
			type.setCurrentPrice(Float.parseFloat(currentPrices[i]));
			type.setOriginPrice(Float.parseFloat(originPrices[i]));
			type.setColor(colors[i]);
			type.setSize(sizes[i]);
			type.setStoreCount(Integer.parseInt(storeCounts[i]));
			type.setUnit(unit);
			types.add(type);
		}

		// 其次添加商品信息
		product.setInputDate(new Date());
		product.setName(name);
		product.setStatus(status);
		product.setViewCount(0);
		product.setHotKey(isHotKey);
		product.setBrandId(brandId);
		product.setProvider(provider);
		product.setProviderAddress(providerAddress);
		product.setUnit(unit);
		product.setCountry(country);
		// Begin transaction
		Trans.exec(new Atom() {
			@Override
			public void run() {
				Product p = dao.insert(product);
				for (ProductType pt : types) {
					pt.setProductId(p.getProductId());
				}
				dao.insert(types);
				es.setStatus(true);
			}
		});
		// End transaction
		if (es.isStatus()) {
			this.result.put("status", "success");
		} else {
			this.result.put("status", "fail");
			this.result.put("msg", "添加商品错误");
		}
		return this.result;
	}

	@At
	public Object updateProduct(@Param("productId") int productId,
			@Param("name") String name,
			@Param("originPrice") String originPrice,
			@Param("currentPrice") String currentPrice,
			@Param("isHotKey") boolean isHotKey,
			@Param("storeCount") String storeCount,
			@Param("status") boolean status,
			@Param("provider") String provider,
			@Param("providerAddress") String providerAddress,
			@Param("country") String country, @Param("brandId") int brandId,
			@Param("color") String color, @Param("size") String size,
			@Param("unit") String unit) {
		this.result = new HashMap<String, Object>();
		final Product product = dao.fetch(Product.class,
				Cnd.where("productId", "=", productId));
		// 要向两张表中进行添加数据
		final ArrayList<ProductType> types = new ArrayList<ProductType>();
		final ExecuteStatus es = new ExecuteStatus();

		// 首先删除所有的商品规格，再添加
		dao.clear(ProductType.class, Cnd.where("productId", "=", productId));
		// 进行颜色，尺码，库存拆分
		String[] colors = color.split("@");
		String[] sizes = size.split("@");
		String[] storeCounts = storeCount.split("@");
		String[] currentPrices = currentPrice.split("@");
		String[] originPrices = originPrice.split("@");
		for (int i = 0; i < colors.length; i++) {
			ProductType type = new ProductType();
			type.setCurrentPrice(Float.parseFloat(currentPrices[i]));
			type.setOriginPrice(Float.parseFloat(originPrices[i]));
			type.setColor(colors[i]);
			type.setSize(sizes[i]);
			type.setStoreCount(Integer.parseInt(storeCounts[i]));
			type.setUnit(unit);
			types.add(type);
		}

		// 其次添加商品信息
		product.setInputDate(new Date());
		product.setName(name);
		product.setStatus(status);
		product.setViewCount(0);
		product.setHotKey(isHotKey);
		product.setBrandId(brandId);
		product.setProvider(provider);
		product.setProviderAddress(providerAddress);
		product.setUnit(unit);
		product.setCountry(country);
		// Begin transaction
		Trans.exec(new Atom() {
			@Override
			public void run() {
				dao.update(product);
				for (ProductType pt : types) {
					pt.setProductId(product.getProductId());
				}
				dao.insert(types);
				es.setStatus(true);
			}
		});
		// End transaction
		if (es.isStatus()) {
			this.result.put("status", "success");
		} else {
			this.result.put("status", "fail");
			this.result.put("msg", "编辑商品错误");
		}
		return this.result;
	}

	@At
	public Object deleteOne(@Param("productId") int ID) {
		this.result = new HashMap<String, Object>();
		if (dao.delete(Product.class, ID) > 0) {
			this.result.put("status", "success");
		} else {
			this.result.put("status", "fail");
			this.result.put("msg", "删除商品失败");
		}
		return this.result;
	}

	@At
	public Object updateProductStatus(@Param("productStatus") String status,
			@Param("productId") int ID) {
		this.result = new HashMap<String, Object>();
		Product product = dao.fetch(Product.class,
				Cnd.where("productId", "=", ID));
		if (status.equals("false")) {
			product.setStatus(true);
		} else {
			product.setStatus(false);
		}
		if (dao.update(product) > 0) {
			this.result.put("status", "success");
		} else {
			this.result.put("status", "fail");
			this.result.put("msg", "商品上下架状态更新失败");
		}
		return this.result;
	}

	@At
	public Object getImgs(@Param("productId") int ID) {
		this.result = new HashMap<String, Object>();
		List<Images> imgs = dao.query(Images.class,
				Cnd.where("productId", "=", ID).orderBy("orderId", "DESC"));
		this.result.put("rows", imgs);
		return this.result;
	}

	@At
	public Object changeOrder(@Param("productId") int productId,
			@Param("imgId") int ID, @Param("orderId") int orderId) {
		this.result = new HashMap<String, Object>();
		Images img = dao.fetch(Images.class, Cnd.where("imgID", "=", ID));
		img.setOrderId(orderId);
		if (dao.update(img) > 0) {
			this.result.put("status", "success");
			List<Images> imgs = dao.query(
					Images.class,
					Cnd.where("productId", "=", productId).orderBy("orderId",
							"DESC"));
			this.result.put("imgs", imgs);
		} else {
			this.result.put("status", "fail");
			this.result.put("msg", "图片调序失败");
		}
		return this.result;
	}

	@At
	public Object getProductDetail(@Param("productId") int ID) {
		this.result = new HashMap<String, Object>();

		return this.result;
	}

	@At
	public Object deleteImgs(@Param("imgIds") String ids) {
		this.result = new HashMap<String, Object>();
		String[] IDs = ids.split("@");
		if (IDs.length == 0) {
			this.result.put("status", "fail");
			this.result.put("msg", "没有选中的图片！");
			return this.result;
		} else {
			if (dao.clear(Images.class, Cnd.where("imgID", "in", IDs)) > 0) {
				this.result.put("status", "success");
			} else {
				this.result.put("status", "fail");
				this.result.put("msg", "图片删除失败");
			}
		}
		return this.result;
	}

	/**
	 * 获取限时活动库存数量
	 * 
	 * @author luocz
	 * @return
	 */
	@At
	public Object getProductActivtyNum(@Param("productTypeId") int productTypeId) {
		ActivityProduct ap = dao.fetch(ActivityProduct.class,
				Cnd.where("productTypeId", "=", productTypeId));
		int leftNum = 0;
		if (ap != null) {
			leftNum = ap.getLeftNum();
		}
		return leftNum < 0 ? 0 : leftNum;
	}
}
