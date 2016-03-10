package com.easyshop.core.modules;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.servlet.http.HttpSession;

import org.nutz.dao.Cnd;
import org.nutz.dao.Dao;
import org.nutz.dao.Sqls;
import org.nutz.dao.sql.Sql;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.mvc.annotation.At;
import org.nutz.mvc.annotation.Fail;
import org.nutz.mvc.annotation.Ok;
import org.nutz.mvc.annotation.Param;

import com.easyshop.bean.Activity;
import com.easyshop.bean.ActivityBranner;
import com.easyshop.bean.ActivtySpecialty;
import com.easyshop.bean.Adviser;
import com.easyshop.bean.Catalogs;
import com.easyshop.bean.ConnectorAP;
import com.easyshop.bean.Images;
import com.easyshop.bean.Order;
import com.easyshop.bean.Personal;
import com.easyshop.bean.Product;
import com.easyshop.bean.ProductType;
import com.easyshop.bean.User;
import com.easyshop.core.ehcache.EhcacheContstant;
import com.easyshop.core.ehcache.EhcacheData;
import com.easyshop.core.ehcache.EhcacheManager;
import com.easyshop.utils.TimeUtils;

@IocBean
@At("/home")
@Ok("json")
@Fail("http:500")
public class HomeModule {
	@Inject
	protected Dao dao;

	@Inject
	protected EhcacheManager ehcacheManager;

	// private Map<String, Object> result;

	@At
	public Object getCurrentUserInfo(@Param("uid") int ID) {
		Map<String, Object> result = new HashMap<String, Object>();
		// 获取所有的热卖商品
		List<Product> hotKeys = dao.query(Product.class,
				Cnd.where("isHotKey", "=", true));
		String[] hotkeywords = new String[hotKeys.size()];
		int i = 0;
		for (Product p : hotKeys) {
			hotkeywords[i++] = p.getName();
		}
		// 获取当前用户的订单信息
		User user = dao.fetch(User.class, Cnd.where("userId", "=", ID));
		int orderNums = dao.count(Order.class, Cnd.where("userId", "=", ID));
		result.put("shopcartnum", orderNums);
		result.put("keywords", hotkeywords);
		return result;
	}

	@At
	public Object getActivityProducts(@Param("activityid") int ID) {
		Map<String, Object> result = new HashMap<String, Object>();
		// 先获取相应的活动
		Activity ac = dao.fetch(Activity.class,
				Cnd.where("activityId", "=", ID));
		// 获取活动商品连接表中的数据
		List<ConnectorAP> aps = dao.query(ConnectorAP.class,
				Cnd.where("activityId", "=", ac.getActivityId()));
		// 从连接表中获取所有的商品ID
		int[] productIds = new int[dao.count(ConnectorAP.class,
				Cnd.where("activityId", "=", ac.getActivityId()))];
		int i = 0;
		for (ConnectorAP ap : aps) {
			productIds[i++] = ap.getProductId();
		}
		// 获取所有的商品
		List<Product> products = dao.query(Product.class,
				Cnd.where("productId", "in", productIds));
		result.put("rows", products);
		return result;
	}

	@At
	public Object getHot() {
		Map<String, Object> result = new HashMap<String, Object>();
		List<Product> productList = dao.query(Product.class,
				Cnd.where("isHotKey", "=", true));
		result.put("rows", productList);
		return result;
	}

	@At
	public Object getHomeInfo() {
		Map<String, Object> result = new HashMap<String, Object>();
		// 获取所有的热卖商品
		List<Product> hotKeys = dao.query(Product.class,
				Cnd.where("isHotKey", "=", true));
		ArrayList<HashMap<String, Object>> hotKeyWords = new ArrayList<HashMap<String, Object>>();
		for (Product p : hotKeys) {
			HashMap<String, Object> temp = new HashMap<String, Object>();
			temp.put("hotKey", p.getName());
			temp.put("url", p.getProductId());
			hotKeyWords.add(temp);
		}
		result.put("keywords", hotKeyWords);
		// 获取所有的热卖商品
		// 获取所有的限时抢购的商品
		Activity limit = dao.fetch(Activity.class,
				Cnd.where("name", "=", "限时抢购"));
		List<Product> limitPs = dao.query(Product.class,
				Cnd.where("activityId", "=", limit.getActivityId()));
		ArrayList<HashMap<String, Object>> limits = new ArrayList<HashMap<String, Object>>();
		for (Product p : limitPs) {
			List<Images> imgs = dao.query(
					Images.class,
					Cnd.where("productId", "=", p.getProductId()).and(
							"isTopimg", "=", true));
			p.setImgs(imgs);
			ProductType pt = dao.fetch(ProductType.class,
					Cnd.where("productId", "=", p.getProductId()));
			p.setOriginPrice(pt.getOriginPrice());
			p.setCurrentPrice(pt.getCurrentPrice());
			// p.setSpecialPrice(pt.getSpecialPrice());
		}
		result.put("limit", limitPs);
		// 获取所有的限时抢购的商品
		// 获取所有的专场活动的商品
		Activity special = dao.fetch(Activity.class,
				Cnd.where("name", "=", "专场活动"));
		List<Product> specialPs = dao.query(Product.class,
				Cnd.where("activityId", "=", special.getActivityId()));
		ArrayList<HashMap<String, Object>> specials = new ArrayList<HashMap<String, Object>>();
		for (Product p : specialPs) {
			HashMap<String, Object> temp = new HashMap<String, Object>();
			Images i = dao.fetch(
					Images.class,
					Cnd.where("productId", "=", p.getProductId()).and(
							"orderId", "=", 0));
			temp.put("showPic", i.getImgsource());
			temp.put("url", p.getProductId());
			specials.add(temp);
		}
		result.put("activity", specials);
		// 获取所有的限时抢购的商品
		// 获取所有的热卖商品
		Activity hot = dao
				.fetch(Activity.class, Cnd.where("name", "=", "热卖产品"));
		List<Product> hotPs = dao.query(Product.class,
				Cnd.where("activityId", "=", hot.getActivityId()));
		for (Product p : hotPs) {
			List<Images> imgs = dao.query(
					Images.class,
					Cnd.where("productId", "=", p.getProductId()).and(
							"isTopimg", "=", true));
			p.setImgs(imgs);
			// 将第一个尺码颜色的现价和原价填充到p中
			ProductType pt = dao.fetch(ProductType.class,
					Cnd.where("productId", "=", p.getProductId()));
			p.setOriginPrice(pt.getOriginPrice());
			p.setCurrentPrice(pt.getCurrentPrice());
		}
		ArrayList<HashMap<String, Object>> hots = new ArrayList<HashMap<String, Object>>();
		// for(Product p : limitPs ){
		HashMap<String, Object> temp = new HashMap<String, Object>();
		temp.put("product", hotPs);
		hots.add(temp);
		// }
		result.put("hot", hotPs);
		// banner
		List<Adviser> ads = dao.query(Adviser.class,
				Cnd.where("type", "=", "banner"));
		result.put("banner", ads);
		// hotadvise
		List<Adviser> ads2 = dao.query(Adviser.class,
				Cnd.where("type", "=", "product"));
		result.put("adviser", ads2);
		// 获取所有的热卖商品
		// 抽奖
		Random rand = new Random();
		ArrayList ids = new ArrayList();
		for (int i = 0; i < 10; i++) {
			int j = rand.nextInt((dao.count(Personal.class,
					Cnd.where("isPlayGame", "=", true))));
			if (!isContent(ids, j)) {
				ids.add(j);
			}
		}
		List<Personal> gamers = dao.query(Personal.class,
				Cnd.where("id", "in", ids.toArray()));
		result.put("win", gamers);
		return result;
	}

	/**
	 * 商品管理类目
	 * 
	 * @return
	 */
	@At
	public Object getItems() {
		Map<String, Object> result = new HashMap<String, Object>();

		EhcacheData data = (EhcacheData) ehcacheManager
				.get(EhcacheContstant.HOMEITEMS);
		if (data != null) {
			result.put("items", data.getData());
			return result;
		}

		List<Catalogs> items = dao.query(Catalogs.class,
				Cnd.where("pid", "=", 1));
		for (Catalogs c : items) {
			Catalogs parent = dao.fetch(Catalogs.class,
					Cnd.where("catalogId", "=", c.getPid()));
			c.setParent(parent);
			List<Catalogs> subitems = dao.query(Catalogs.class,
					Cnd.where("pid", "=", c.getCatalogId()));
			for (Catalogs sc : subitems) {
				Catalogs sparent = dao.fetch(Catalogs.class,
						Cnd.where("catalogId", "=", sc.getPid()));
				sc.setParent(sparent);
				List<Catalogs> subsubitem = dao.query(Catalogs.class,
						Cnd.where("pid", "=", sc.getCatalogId()));
				for (Catalogs ssc : subsubitem) {
					Catalogs ssparent = dao.fetch(Catalogs.class,
							Cnd.where("catalogId", "=", sc.getPid()));
					ssc.setParent(ssparent);
				}
				sc.setSubCatalogs(subsubitem);
			}
			c.setSubCatalogs(subitems);
		}
		result.put("items", items);
		data = new EhcacheData(items);
		ehcacheManager.put(EhcacheContstant.HOMEITEMS, data);

		return result;
	}

	/**
	 * 首页轮播图片
	 * 
	 * @return
	 */
	@At
	public Object getBanner() {
		Map<String, Object> result = new HashMap<String, Object>();
		// banner
		List<ActivityBranner> list = dao.query(ActivityBranner.class, Cnd
				.where("status", "=", 1).desc("seq").desc("id"));

		// List<Adviser> ads = dao.query(Adviser.class,
		// Cnd.where("type", "=", "banner"));
		result.put("banner", list);
		return result;
	}

	@At
	public Object getAdviser() {
		Map<String, Object> result = new HashMap<String, Object>();
		List<Adviser> ads2 = dao.query(Adviser.class,
				Cnd.where("type", "=", "product"));
		result.put("adviser", ads2);
		return result;
	}

	/**
	 * 首页限时活动获取接口
	 * 
	 * @return
	 */
	@At
	public Object getLimit() {
		Map<String, Object> result = new HashMap<String, Object>();

		// 原价 originPrice 国内参考价 currentPrice 抢购价 price
		String sqlStr = " select a.country, a.name productName,  a.productId, t.productTypeId, t.originPrice, "
				+ " t.currentPrice, b.price, b.endTime external "
				+ "  from product a, producttype t, activityproduct b "
				+ " where a.productId = b.productId "
				+ " and a.productId = t.productId "
				+ " and b.status = 1 and  b.beginTime<= @dayTime and b.endTime>=@dayTime"
				+ " and b.leftNum >0 order by b.beginTime, b.endTime ";

		String dayTime = TimeUtils.dateToStr(new Date(), TimeUtils.FORMAT14);
		Sql sql = Sqls.create(sqlStr);
		sql.params().set("dayTime", dayTime);
		sql.setCallback(Sqls.callback.maps());
		dao.execute(sql);
		List<Map> limitPs = sql.getList(Map.class);
		for (Map map : limitPs) {
			List<Images> imgs = dao.query(
					Images.class,
					Cnd.where("productId", "=", map.get("productId"))
							.and("isTopimg", "=", true)
							.orderBy("orderId", "DESC"));
			map.put("imgs", imgs);

			// ProductType pt = dao.fetch(ProductType.class,
			// Cnd.where("productId", "=", map.get("productId")));
			// map.put("specialPrice", pt.getSpecialPrice());

		}
		result.put("limit", limitPs);
		return result;
	}

	/**
	 * 热卖商品1
	 * 
	 * @return
	 */
	@At
	public Object getHotSell() {
		Map<String, Object> result = new HashMap<String, Object>();

		String sqlStr = " select a.name, a.productId, a.country  "
				+ "  from product a, activtyheat b "
				+ " where a.productId = b.productId  " + "and b.type = 1  ";
		Sql sql = Sqls.create(sqlStr);
		sql.setCallback(Sqls.callback.maps());
		dao.execute(sql);
		List<Map> hotPs = sql.getList(Map.class);
		for (Map map : hotPs) {
			List<Images> imgs = dao.query(
					Images.class,
					Cnd.where("productId", "=", map.get("productId"))
							.and("isTopimg", "=", true)
							.orderBy("orderId", "DESC"));
			map.put("imgs", imgs);

			ProductType pt = dao.fetch(ProductType.class,
					Cnd.where("productId", "=", map.get("productId")));
			map.put("originPrice", pt.getOriginPrice());
			map.put("currentPrice", pt.getCurrentPrice());
		}
		ArrayList<HashMap<String, Object>> hots = new ArrayList<HashMap<String, Object>>();
		HashMap<String, Object> temp = new HashMap<String, Object>();
		temp.put("product", hotPs);
		hots.add(temp);
		result.put("hot", hotPs);
		return result;
	}

	/**
	 * 热卖商品2
	 * 
	 * @return
	 */
	@At
	public Object getHotSellTwo() {
		Map<String, Object> result = new HashMap<String, Object>();

		String sqlStr = " select a.name, a.productId, a.country  "
				+ "  from product a,  activtyheat b "
				+ " where a.productId = b.productId" + "  and b.type = 2  ";
		Sql sql = Sqls.create(sqlStr);
		sql.setCallback(Sqls.callback.maps());
		dao.execute(sql);
		List<Map> hotPs = sql.getList(Map.class);
		for (Map map : hotPs) {
			List<Images> imgs = dao.query(
					Images.class,
					Cnd.where("productId", "=", map.get("productId"))
							.and("isTopimg", "=", true)
							.orderBy("orderId", "DESC"));
			map.put("imgs", imgs);

			ProductType pt = dao.fetch(ProductType.class,
					Cnd.where("productId", "=", map.get("productId")));
			map.put("originPrice", pt.getOriginPrice());
			map.put("currentPrice", pt.getCurrentPrice());
		}
		ArrayList<HashMap<String, Object>> hots = new ArrayList<HashMap<String, Object>>();
		HashMap<String, Object> temp = new HashMap<String, Object>();
		temp.put("product", hotPs);
		hots.add(temp);
		result.put("hot", hotPs);
		return result;
	}

	/**
	 * 专场活动
	 * 
	 * @return
	 */
	@At
	public Object getSpecial() {
		Map<String, Object> result = new HashMap<String, Object>();
		List<ActivtySpecialty> activities = dao.query(ActivtySpecialty.class,
				Cnd.where("status", "=", 1));
		// List<Activity> activities = dao.query(Activity.class,
		// Cnd.where("name", "like", "%专场活动%"));
		result.put("activity", activities);
		return result;
	}

	@At
	public Object getWin() {
		Map<String, Object> result = new HashMap<String, Object>();
		Random rand = new Random();
		ArrayList ids = new ArrayList();
		int count = dao.count(Personal.class,
				Cnd.where("isPlayGame", "=", true));
		List<Personal> gamers = null;
		if (count > 0) {
			for (int i = 0; i < 10; i++) {
				int j = rand.nextInt(count);
				if (!isContent(ids, j)) {
					ids.add(j);
				}
			}
			gamers = dao.query(Personal.class,
					Cnd.where("id", "in", ids.toArray()));
		}

		result.put("win", gamers);
		return result;
	}

	@At
	public Object getCurrentUser(HttpSession session) {
		Map<String, Object> result = new HashMap<String, Object>();
		if (null == session.getAttribute("frontUserId")) {
			result.put("status", "fail");
			return result;
		}
		long personId = (long) session.getAttribute("frontUserId");
		Personal p = dao.fetch(Personal.class, Cnd.where("id", "=", personId));
		if (p != null) {
			result.put("status", "success");
			result.put("name",
					(p.getName() == null ? p.getPhone() : p.getNickname()));
			result.put("uid", p.getId());
		} else {
			result.put("status", "fail");
		}
		return result;
	}

	private boolean isContent(ArrayList list, int target) {
		for (Object o : list) {
			int temp = Integer.parseInt(o.toString());
			if (temp == target) {
				return true;
			}
		}
		return false;
	}
}
