package com.easyshop.core.modules;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.nutz.dao.Cnd;
import org.nutz.dao.Dao;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.mvc.annotation.At;
import org.nutz.mvc.annotation.Fail;
import org.nutz.mvc.annotation.Ok;
import org.nutz.mvc.annotation.Param;

import com.easyshop.bean.Catalogs;
import com.easyshop.bean.Images;
import com.easyshop.bean.Product;
import com.easyshop.bean.ProductScan;
import com.easyshop.bean.ProductType;
import com.easyshop.core.modules.admin.OrderConstant;
import com.easyshop.utils.StringUtils;

@IocBean
@At("/front/detail")
@Ok("json")
@Fail("http:500")
public class FrontDetailModule {
	@Inject
	protected Dao dao;
	private Map<String, Object> result;

	/**
	 * http://localhost:8181/easyshop/front/detail/getDetailInfo?productId=29
	 * {"topImgs":[{"imgID":74,"imgsource":
	 * "http://buylll.oss-cn-qingdao.aliyuncs.com/xxx_1453706125797"
	 * ,"isTopimg":true
	 * ,"productId":29,"isSmallView":false,"isSizePic":false,"isDetailPic"
	 * :false,"orderId":0}, {"imgID":75,"imgsource":
	 * "http://buylll.oss-cn-qingdao.aliyuncs.com/xxx_1453706148935"
	 * ,"isTopimg":true
	 * ,"productId":29,"isSmallView":false,"isSizePic":false,"isDetailPic"
	 * :false,"orderId":0}],"color":["red",
	 * "blue"],"detailImgs":[{"imgID":76,"imgsource"
	 * :"http://buylll.oss-cn-qingdao.aliyuncs.com/xxx_1453706231831"
	 * ,"isTopimg": false,"productId":29,"isSmallView":false,"isSizePic":false,
	 * "isDetailPic" :true,"orderId":0}, {"imgID":77,"imgsource":
	 * "http://buylll.oss-cn-qingdao.aliyuncs.com/xxx_1453707112901"
	 * ,"isTopimg":false
	 * ,"productId":29,"isSmallView":false,"isSizePic":false,"isDetailPic"
	 * :true,"orderId"
	 * :0}],"productInfo":{"originPrice":0.0,"currentPrice":0.0,"productId"
	 * :29,"name"
	 * :"28888","minPrice":8.0,"activityType":0,"limitActivityStatus":0
	 * ,"limitActivityTotalCount"
	 * :0,"limitActivityTotalLeftCount":0,"weight":0.0,
	 * "inputDate":"2016-01-28 17:30:49"
	 * ,"viewCount":0,"sellCount":0,"totalCount"
	 * :0,"totalLeftCount":0,"status":false
	 * ,"supplierId":2,"catalogId1":11,"catalogId2"
	 * :22,"catalogId3":33,"catalogId"
	 * :0,"isHotKey":false,"isNew":false,"brandId"
	 * :6,"specialPrice":0.0,"activityId"
	 * :0,"country":"america","number":0,"price"
	 * :0.0},"productTypes":[{"productTypeId"
	 * :158,"size":"XL","color":"red","productId"
	 * :29,"storeCount":30,"sellCount":
	 * 0,"supplyPrice":3.0,"originPrice":10.0,"currentPrice"
	 * :8.0,"specialPrice":0.0,"limitActivityLeftCount":0},
	 * {"productTypeId":159,
	 * "size":"XXL","color":"blue","productId":29,"storeCount"
	 * :40,"sellCount":0,"supplyPrice"
	 * :4.0,"originPrice":20.0,"currentPrice":12.0
	 * ,"specialPrice":0.0,"limitActivityLeftCount":0}],"size":["XL", "XXL"]}
	 *
	 * @param ID
	 * @return
	 */
	@At
	public Object getDetailInfo(@Param("productId") int ID, HttpSession session) {
		this.result = new HashMap<String, Object>();
		// 获取商品的基本信息
		Product p = dao.fetch(Product.class, Cnd.where("productId", "=", ID));
		// 获取相应的商品规格信息
		List<ProductType> pts = dao.query(ProductType.class,
				Cnd.where("productId", "=", ID));
		// ArrayList<String> colors=new ArrayList<String>();
		// ArrayList<String> sizes=new ArrayList<String>();
		// ArrayList<String> originPrices=new ArrayList<String>();
		// ArrayList<String> currentPrices=new ArrayList<String>();
		// for(ProductType pt : pts){
		// if(!this.isContent(colors, pt.getColor())){
		// colors.add(pt.getColor());
		// }
		// }
		// for(ProductType pt : pts){
		// if(!this.isContent(sizes, pt.getSize())){
		// sizes.add(pt.getSize());
		// }
		// //originPrices.add(pt.getOriginPrice());
		// }
		this.result.put("productInfo", p);
		this.result.put("productTypes", pts);
		// this.result.put("color",colors);
		// this.result.put("size", sizes);
		// this.result.put("originPrice", pts.get(0).getOriginPrice());
		// this.result.put("currentPrice", pts.get(0).getCurrentPrice());
		// 获取商品相应的主图片
		List<Images> topimgs = dao.query(Images.class,
				Cnd.where("productId", "=", ID).and("isTopimg", "=", true));
		this.result.put("topImgs", topimgs);
		// 获取商品相应的详情图片
		List<Images> detailimgs = dao.query(Images.class,
				Cnd.where("productId", "=", ID).and("isDetailPic", "=", true));
		this.result.put("detailImgs", detailimgs);

		String userId = String.valueOf(session
				.getAttribute(OrderConstant.FRONT_USER_ID));

		if (!StringUtils.isEmpty(userId) && !"null".equals(userId)) {
			// 添加最新浏览记录
			ProductScan scan = new ProductScan();
			scan.setPersonalId(userId);
			scan.setProductId(p.getProductId());
			scan.setProductTypeId(pts.get(0).getProductTypeId());
			scan.setScanTime(new Date());

			dao.insert(scan);
		}

		return this.result;
	}

	// 去重的功能性函数
	private boolean isContent(ArrayList<String> targetList, String source) {
		for (String s : targetList) {
			if (s.equals(source)) {
				return true;
			}
		}
		return false;
	}

	@At
	public Object getcatalogItems(@Param("productId") int ID) {
		this.result = new HashMap<String, Object>();
		Product p = dao.fetch(Product.class, Cnd.where("productId", "=", ID));
		int catalogId = p.getCatalogId();
		Catalogs catalog = dao.fetch(Catalogs.class,
				Cnd.where("catalogId", "=", catalogId));
		Catalogs parentcatalog = dao.fetch(Catalogs.class,
				Cnd.where("catalogId", "=", catalog.getPid()));
		this.result.put("catalogName", catalog.getCatalogName());
		this.result.put("parent", parentcatalog.getCatalogName());
		return this.result;
	}

	@At
	public Object getPrice(@Param("productId") int ID,
			@Param("color") String color, @Param("size") String size) {
		this.result = new HashMap<String, Object>();
		Product p = dao.fetch(Product.class, Cnd.where("productId", "=", ID));
		ProductType pt = dao.fetch(
				ProductType.class,
				Cnd.where("color", "=", color).and("size", "=", size)
						.and("productId", "=", ID));
		if (null != pt) {
			this.result.put("status", "success");
			this.result.put("originPrice", pt.getOriginPrice());
			this.result.put("currentPrice", pt.getCurrentPrice());
		} else {
			this.result.put("status", "fail");
		}
		return this.result;
	}
}
