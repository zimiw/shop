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

import com.easy.core.filters.CheckFrontUserLoginFilter;
import com.easyshop.bean.ActivityProduct;
import com.easyshop.bean.Address;
import com.easyshop.bean.Area;
import com.easyshop.bean.City;
import com.easyshop.bean.ConnectorOP;
import com.easyshop.bean.Images;
import com.easyshop.bean.Order;
import com.easyshop.bean.OrderProgress;
import com.easyshop.bean.Product;
import com.easyshop.bean.ProductType;
import com.easyshop.bean.Province;
import com.easyshop.bean.Shoppingcart;
import com.easyshop.bean.StoreCount;
import com.easyshop.core.modules.admin.OrderConstant;
import com.easyshop.vo.ProductVo;
import com.pingplusplus.Pingpp;
import com.pingplusplus.exception.APIConnectionException;
import com.pingplusplus.exception.APIException;
import com.pingplusplus.exception.AuthenticationException;
import com.pingplusplus.exception.ChannelException;
import com.pingplusplus.exception.InvalidRequestException;
import com.pingplusplus.model.Charge;

@IocBean
@At("/shopcar")
@Ok("json")
@Fail("http:500")
@Filters(@By(type = CheckFrontUserLoginFilter.class))
public class ShopCarModule {
	@Inject
	protected Dao dao;

	public final static String FRONT_USER_ID = OrderConstant.FRONT_USER_ID;// 前台用户sessionId

	Logger logger = Logger.getLogger(ShopCarModule.class);

	/**
	 * 根据参数 获取商品的基本信息
	 * 
	 * @param productId
	 * @param productTypeId
	 * @return
	 */
	private ProductVo getProductAttr(int productId, int productTypeId) {

		ProductVo result = new ProductVo();
		Product pd = dao.fetch(Product.class,
				Cnd.where("productId", "=", productId));
		ProductType pt = dao.fetch(ProductType.class,
				Cnd.where("productTypeId", "=", productTypeId));
		Images image = dao.fetch(
				Images.class,
				Cnd.where("productId", "=", productId).and("isTopimg", "=",
						true));
		result.setProductId(productId);
		result.setProductTypeId(productTypeId);
		result.setName(pd.getName());
		result.setSize(pt.getSize());
		result.setImgUrl(image.getImgsource());
		// 如果是限时活动商品 且活动正在进行中，设置价格为活动价
		if (pd.getActivityType() == 1 && pd.getLimitActivityStatus() == 1) {
			result.setPrice(pt.getSpecialPrice());
		} else {
			result.setPrice(pt.getCurrentPrice());
		}
		return result;
	}

	/**
	 * 加入购物车操作，返回当前商品在购物车中的数量
	 * 
	 * @param session
	 * @param productId
	 *            --商品id
	 * @param productTypeId
	 *            --商品类型 id
	 * @param number
	 * @return
	 */
	@At
	public Object addShoppingcart(HttpSession session,
			@Param("productId") int productId,
			@Param("productTypeId") int productTypeId,
			@Param("number") int number) {

		HashMap<String, Object> result = new HashMap<String, Object>();
		String userId = String.valueOf(session.getAttribute(FRONT_USER_ID));
		Shoppingcart sc = dao.fetch(
				Shoppingcart.class,
				Cnd.where("userId", "=", userId)
						.and("productId", "=", productId)
						.and("selectedType", "=", 1)
						.and("productTypeId", "=", productTypeId));
		if (sc == null) {
			sc = new Shoppingcart();
			sc.setUserId(Integer.parseInt(userId));
			sc.setProductTypeId(productTypeId);
			sc.setProductId(productId);
			sc.setSelectedType(1);
			sc.setNumber(number);
			dao.insert(sc);
			result.put("number", number);
		} else {
			sc.setNumber(sc.getNumber() + number);
			dao.update(sc);
			result.put("number", sc.getNumber());
		}
		result.put("status", "sucess");
		return result;
	}

	/**
	 * 根据客户id查询购物车中商品种数
	 * 
	 * @ok
	 * @param HttpSession
	 * @return
	 */
	@At
	public Object checkShoppingcartSize(HttpSession session) {

		HashMap<String, Object> result = new HashMap<String, Object>();
		String userId = String.valueOf(session.getAttribute(FRONT_USER_ID));
		int count = dao.count(Shoppingcart.class,
				Cnd.where("userId", "=", userId).and("selectedType", "=", 1));
		result.put("size", count);
		return result;
	}

	/**
	 * 根据客户id查询购物车信息，返回当前客户购物车中商品和数量
	 * 
	 * @ok
	 * @param userId
	 * @return {"data":[{"ProductVo":商品信息, "number":数量}]}
	 */
	@At
	public Object checkShoppingcart(HttpSession session) {
		Date startTime = new Date();
		System.out.println("-----------------开始查询时间 ：" + startTime.getTime()
				+ "--------------------");
		HashMap<String, Object> result = new HashMap<String, Object>();
		String userId = String.valueOf(session.getAttribute(FRONT_USER_ID));

		List<Shoppingcart> shoppingcarts = dao.query(Shoppingcart.class, Cnd
				.where("userId", "=", userId).and("selectedType", "=", 1));
		if (shoppingcarts.size() > 0) {
			for (Shoppingcart item : shoppingcarts) {
				ProductVo pv = getProductAttr(item.getProductId(),
						item.getProductTypeId());
				item.setProductVo(pv);
			}
			result.put("data", shoppingcarts);
			result.put("number", shoppingcarts.size());
			Date endTime = new Date();
			System.out.println("-----------------结束查询时间 ："
					+ startTime.getTime() + "--------------------");
			System.out.println("花费时间为： "
					+ (endTime.getTime() - startTime.getTime()));
			return result;
		} else {
			result.put("data", null);
			result.put("number", 0);
			return result;
		}
	}

	/**
	 * 根据客户id查询购物车中已添加到订单信息，返回当前客户购物车中商品和数量
	 * 
	 * @ok
	 * @param userId
	 * @return {"data":[{"product":商品信息, "number":数量}]}
	 */
	@At
	public Object checkSelected(HttpSession session,
			@Param("selectedType") int selectedType) {
		HashMap<String, Object> result = new HashMap<String, Object>();
		String userId = String.valueOf(session.getAttribute(FRONT_USER_ID));
		List<Shoppingcart> shoppingcarts = dao.query(
				Shoppingcart.class,
				Cnd.where("userId", "=", userId).and("isSelected", "=", 1)
						.and("selectedType", "=", selectedType));
		if (shoppingcarts.size() > 0) {
			for (Shoppingcart item : shoppingcarts) {
				ProductVo pv = getProductAttr(item.getProductId(),
						item.getProductTypeId());
				item.setProductVo(pv);
			}
			result.put("data", shoppingcarts);
			return result;
		} else {
			result.put("data", null);
			return result;
		}
	}

	/**
	 * 修改购物车中物品数量
	 * 
	 * @ok
	 * @modified by wangzhiming 2016.1.14
	 * @param userId
	 * @param shoppingcartId
	 * @param number
	 * @return
	 */
	@At
	public Object modifyNumber(HttpSession session,
			@Param("shoppingcartId") int shoppingcartId,
			@Param("number") int number) {

		HashMap<String, Object> result = new HashMap<String, Object>();
		String userId = String.valueOf(session.getAttribute(FRONT_USER_ID));
		try {
			Shoppingcart sc = dao.fetch(
					Shoppingcart.class,
					Cnd.where("userId", "=", userId)
							.and("shoppingcartId", "=", shoppingcartId)
							.and("selectedType", "=", 1));
			sc.setNumber(number);
			dao.update(sc);
		} catch (Exception e) {
			result.put("status", "fail");
			return result;
		}
		result.put("status", "success");
		return result;
	}

	/**
	 * 直接购买（不通过购物车流程），生成预订单
	 * 
	 * @ok
	 * @modified by wangzhiming 2016.1.14
	 * @param session
	 * @param productId
	 * @param number
	 *            -数量，size-尺码，color-颜色
	 * @return
	 */
	@At
	public Object directAddToSelected(HttpSession session,
			@Param("productId") int productId,
			@Param("productTypeId") int productTypeId,
			@Param("number") int number) {
		logger.info("开始调用directAddToSelected方法,直接购买productId为：" + productId
				+ "productTypeId为" + productTypeId + "的" + number + "件商品");
		HashMap<String, Object> result = new HashMap<String, Object>();
		String userId = String.valueOf(session.getAttribute(FRONT_USER_ID));
		try {
			// 删除之前的临时数据
			List<Shoppingcart> list = dao.query(
					Shoppingcart.class,
					Cnd.where("userId", "=", userId)
							.and("selectedType", "=", 2)
							.and("productTypeId", "=", productTypeId));
			for (Shoppingcart item : list) {
				dao.delete(item);
			}
			// 插入新的临时数据
			if (number > 0) {
				Shoppingcart newSp = new Shoppingcart();
				newSp.setProductId(productId);
				newSp.setUserId(Integer.parseInt(userId));
				newSp.setIsSelected(1);
				newSp.setSelectedType(2);
				newSp.setNumber(number);
				dao.insert(newSp);
				result.put("status", "sucess");
			} else {
				result.put("status", "checkNumber");
			}
			logger.info("成功结束调用directAddToSelected方法,直接购买productId为："
					+ productId + "productTypeId为" + productTypeId + "的"
					+ number + "件商品");
		} catch (Exception e) {
			result.put("status", "fail");
			logger.error("调用directAddToSelected方法,直接购买productId为：" + productId
					+ "productTypeId为" + productTypeId + "的" + number
					+ "件商品 发生异常", e);
		}
		return result;
	}

	/**
	 * 添加或者撤销购物车中商品到订单
	 * 
	 * @ok
	 * @param shoppingcartIds
	 *            购物车id，以@@分割，如12@@13
	 * @param operate
	 *            添加-1，撤销-0
	 * @return
	 */
	@At
	public Object modifySelected(
			@Param("shoppingcartIds") String shoppingcartIds,
			@Param("operate") int operate) {

		HashMap<String, Object> result = new HashMap<String, Object>();
		String[] shoppingcartIdList = shoppingcartIds.split("@@");
		try {
			for (String item : shoppingcartIdList) {
				Shoppingcart sc = dao.fetch(Shoppingcart.class,
						Integer.parseInt(item));
				sc.setIsSelected(operate);
				sc.setSelectedType(1);
				dao.update(sc);
			}
		} catch (NumberFormatException e1) {
			e1.printStackTrace();
		} catch (Exception e) {
			result.put("status", "fail");
			return result;
		}
		result.put("status", "success");
		return result;
	}

	/**
	 * 删除购物车中的商品
	 * 
	 * @ok
	 * @modified by wangzhiming 2016.1.14
	 * @param userId
	 * @param shoppingcartId
	 * @return
	 */
	@At
	public Object delShoppingcart(HttpSession session,
			@Param("shoppingcartId") int shoppingcartId) {
		logger.info("开始调用delShoppingcart方法,删除shoppingcartId为：" + shoppingcartId
				+ "的商品");
		HashMap<String, Object> result = new HashMap<String, Object>();
		String userId = String.valueOf(session.getAttribute(FRONT_USER_ID));
		try {
			Shoppingcart sc = dao.fetch(
					Shoppingcart.class,
					Cnd.where("userId", "=", userId)
							.and("shoppingcartId", "=", shoppingcartId)
							.and("selectedType", "=", 1));
			dao.delete(Shoppingcart.class, sc.getShoppingcartId());
		} catch (Exception e) {
			logger.error("调用delShoppingcart方法,删除shoppingcartId为："
					+ shoppingcartId + "的商品发生异常");
			result.put("status", "fail");
			return result;
		}
		logger.info("成功调用delShoppingcart方法,删除shoppingcartId为：" + shoppingcartId
				+ "的商品");
		result.put("status", "success");
		return result;
	}

	/**
	 * 根据用户id获取用户的收货地址信息
	 * 
	 * @ok
	 * @param userId
	 * @return
	 */
	@At
	public Object getAddress(HttpSession session) {
		Date startTime = new Date();
		System.out.println("-----------------开始查询时间 ：" + startTime.getTime()
				+ "--------------------");
		HashMap<String, Object> result = new HashMap<String, Object>();
		String userId = String.valueOf(session.getAttribute(FRONT_USER_ID));

		List<Address> addresses = dao.query(Address.class,
				Cnd.where("userId", "=", userId).and("status", "=", 1));
		for(Address item : addresses){
			Province province = dao.fetch(Province.class, Cnd.where("code","=",item.getProvince()));
			City city = dao.fetch(City.class,Cnd.where("code","=",item.getCity()));
			Area area = dao.fetch(Area.class,Cnd.where("code","=", item.getDistrict()));
			item.setAddressStr(province.getName()+city.getName()+area.getName());
		}
		result.put("data", addresses);
		Date endTime = new Date();
		System.out.println("-----------------结束查询时间 ：" + startTime.getTime()
				+ "--------------------");
		System.out.println("花费时间为： "
				+ (endTime.getTime() - startTime.getTime()));
		return result;
	}

	/**
	 * 设置默认地址
	 * 
	 * @param newAddressId
	 * @param oldAddressId
	 * @return
	 */
	@At
	public Object setDefaultAddress(HttpSession session,
			@Param("addressId") final int addressId) {
		final String userId = String.valueOf(session
				.getAttribute(FRONT_USER_ID));

		Date startTime = new Date();
		System.out.println("-----------------开始查询时间 ：" + startTime.getTime()
				+ "--------------------");
		HashMap<String, Object> result = new HashMap<String, Object>();
		try {
			final Address defAddress = dao.fetch(
					Address.class,
					Cnd.where("userId", "=", userId).and("status", "=", 1)
							.and("isDefault", "=", true));
			// final Address newAddress = dao.fetch(Address.class,
			// Cnd.where("addressId", "=", addressId));

			Trans.exec(new Atom() {
				@Override
				public void run() {
					if (defAddress != null) {
						dao.update(
								Address.class,
								Chain.make("isDefault", false),
								Cnd.where("userId", "=", userId).and("status",
										"=", 1));// 先把这个用户的默认地址取消
					}
					dao.update(
							Address.class,
							Chain.make("isDefault", true),
							Cnd.where("userId", "=", userId)
									.and("status", "=", 1)
									.and("addressId", "=", addressId));// 在把对应地址设置为默认地址
				}
			});
		} catch (Exception e) {
			result.put("status", "fail");
			return result;
		}
		result.put("status", "success");
		Date endTime = new Date();
		System.out.println("-----------------结束查询时间 ：" + endTime.getTime()
				+ "--------------------");
		System.out.println("花费时间为： "
				+ (endTime.getTime() - startTime.getTime()));
		return result;
	}

	/**
	 * 添加新的地址信息
	 * 
	 * @param userId
	 * @param province
	 * @param city
	 * @param district
	 * @param street
	 * @param name
	 * @param post
	 * @param phone
	 * @param isDefault
	 *            是否为设置为默认地址 boolean
	 * @return
	 */
	@At
	public Object addAddress(HttpSession session,
			@Param("province") String province, @Param("city") String city,
			@Param("district") String district, @Param("street") String street,
			@Param("name") String name, @Param("post") String post,
			@Param("cellphone") String cellphone, @Param("phone") String phone,
			@Param("isDefault") final boolean isDefault) {
		HashMap<String, Object> result = new HashMap<String, Object>();
		final String userId = String.valueOf(session
				.getAttribute(FRONT_USER_ID));
		try {
			final Address address = new Address();
			address.setUserId(Integer.parseInt(userId));
			address.setProvince(province);
			address.setCity(city);
			address.setDistrict(district);
			address.setStreet(street);
			address.setName(name);
			address.setPost(post);
			address.setCellphone(cellphone);
			address.setPhone(phone);
			address.setIsOrder(0);// 新增订单 订单未使用
			address.setStatus(1);
			address.setDefault(isDefault);

			Trans.exec(new Atom() {
				@Override
				public void run() {
					if (isDefault) {
						Address defAddress = dao.fetch(
								Address.class,
								Cnd.where("isDefault", "=", true)
										.and("status", "=", 1)
										.and("userId", "=", userId));
						defAddress.setDefault(false);
						dao.update(defAddress);
					}
					dao.insert(address);
				}
			});

		} catch (Exception e) {
			result.put("status", "fail");
			return result;
		}
		result.put("status", "success");
		return result;
	}

	/**
	 * 修改地址信息
	 * 
	 * @modified by wangzhiming 2016.1.14 添加cellphone
	 * @param addressId
	 * @param userId
	 * @param province
	 * @param city
	 * @param district
	 * @param street
	 * @param name
	 * @param post
	 * @param phone
	 * @return
	 */
	@At
	public Object modifyAddress(HttpSession session,
			@Param("addressId") int addressId,
			@Param("province") String province, @Param("city") String city,
			@Param("district") String district, @Param("street") String street,
			@Param("name") String name, @Param("post") String post,
			@Param("cellphone") String cellphone, @Param("phone") String phone) {
		HashMap<String, Object> result = new HashMap<String, Object>();
		String userId = String.valueOf(session.getAttribute(FRONT_USER_ID));
		try {
			Address address = dao.fetch(Address.class, addressId);
			if (address.getIsOrder() == 1) {// 订单中已经使用
				dao.update(Address.class, Chain.make("status", 0),
						Cnd.where("addressId", "=", address.getAddressId()));// 原记录设置为无效

				// 添加一条新记录
				address.setAddressId(0);
				address.setUserId(Integer.parseInt(userId));
				address.setProvince(province);
				address.setCity(city);
				address.setDistrict(district);
				address.setStreet(street);
				address.setName(name);
				address.setPost(post);
				address.setCellphone(cellphone);
				address.setPhone(phone);
				address.setIsOrder(0);
				address.setStatus(1);
				dao.insert(address);
			} else {
				address.setUserId(Integer.parseInt(userId));
				address.setProvince(province);
				address.setCity(city);
				address.setDistrict(district);
				address.setStreet(street);
				address.setName(name);
				address.setPost(post);
				address.setCellphone(cellphone);
				address.setPhone(phone);
				dao.update(address);
			}
		} catch (Exception e) {
			result.put("status", "fail");
			return result;
		}
		result.put("status", "success");
		return result;
	}

	/**
	 * 删除地址信息
	 * 
	 * @param addressId
	 * @return
	 */
	@At
	public Object delAddress(HttpSession session,
			@Param("addressId") int addressId) {
		HashMap<String, Object> result = new HashMap<String, Object>();
		final String userId = String.valueOf(session
				.getAttribute(FRONT_USER_ID));
		final int addressIdf = addressId;
		try {
			final Address currentAddress = dao.fetch(Address.class,
					Cnd.where("addressId", "=", addressId)
							.and("status", "=", 1));
			if (currentAddress == null) {
				result.put("status", "fail");
				result.put("", "该地址信息不存在或已经删除!");
				return result;
			}
			Trans.exec(new Atom() {
				@Override
				public void run() {

					if (currentAddress.getIsOrder() == 1) {// 订单中已经使用
						currentAddress.setStatus(0);// 修改为无效
						currentAddress.setDefault(false);
						dao.update(currentAddress);
					} else {
						dao.delete(Address.class, addressIdf);
					}

					if (currentAddress.isDefault()) {// 是默认地址删除，要重新设置默认地址
						dao.update(
								Address.class,
								Chain.make("isDefault", true),
								Cnd.wrap(" status = 1 AND addressId = (SELECT MAX(addressId) FROM addresst WHERE t.status = 1 AND t.userId = "
										+ userId));
					}
				}
			});
		} catch (Exception e) {
			result.put("status", "fail");
			return result;
		}
		result.put("status", "success");
		return result;
	}

	/**
	 * 生成订单流程包括：1，查是否上架；2.查库存;3.插订单表;4.插订单商品关联表;5.插订单进度表; 6.删除购物车数据; 7.更新库存
	 * 
	 * @param session
	 * @param shoppingCartStr
	 *            - 格式为 shoppingcartId##color##size@@shoppingcartId##color##size
	 * @return
	 */
	@At
	public Object addToOrder(HttpSession session,
			@Param("shoppingCartStr") String shoppingCartStr,
			@Param("addressId") String addressId) {

		logger.info("开始调用addToOrder方法，生成订单");
		HashMap<String, Object> result = new HashMap<String, Object>();
		String userId = String.valueOf(session.getAttribute(FRONT_USER_ID));
		final String[] shoppingCartStrArr = shoppingCartStr.split("@@");
		final List<Shoppingcart> scList = new ArrayList<Shoppingcart>();
		float amount = 0;
		for (int i = 0; i < shoppingCartStrArr.length; i++) {

			Shoppingcart shoppingcart = dao.fetch(Shoppingcart.class, Cnd
					.where("shoppingcartId", "=", Integer
							.parseInt(shoppingCartStrArr[i].split("##")[0])));
			Product pro = dao.fetch(Product.class,
					Cnd.where("productId", "=", shoppingcart.getProductId()));
			if (!pro.getStatus()) {
				result.put("status", "fail");
				result.put("msg", "很抱歉，您购买的商品：" + pro.getName() + " 已下架，请谅解！");
				logger.info("结束调用addToOrder方法，生成订单失败，失败原因为商品已下架");
				return result;
			} else {
				// 根据尺码，颜色来获取当前商品的库存量
				// 首先获取商品规格对象
				ProductType pt = dao.fetch(
						ProductType.class,
						Cnd.where("size", "=",
								shoppingCartStrArr[i].split("##")[2])
								.and("color", "=",
										shoppingCartStrArr[i].split("##")[1])
								.and("productId", "=",
										shoppingcart.getProductId()));
				StoreCount sc = dao.fetch(StoreCount.class,
						Cnd.where("productTypeId", "=", pt.getProductTypeId()));
				if (sc.getStoreCount() < shoppingcart.getNumber()) {
					result.put("status", "fail");
					result.put("msg", "很抱歉，您购买的商品：" + pro.getName()
							+ " 已售完，请谅解！");
					logger.info("结束调用addToOrder方法，生成订单失败，失败原因为商品已售完");
					return result;
				}
				amount += shoppingcart.getNumber() * pro.getCurrentPrice();
				shoppingcart.setProduct(pro);
				scList.add(i, shoppingcart);
			}
		}
		final Order order = new Order();
		// 设置地址信息
		final Address address = dao.fetch(Address.class,
				Cnd.where("addressId", "=", addressId).and("status", "=", 1));
		if (address == null) {
			result.put("status", "fail");
			result.put("msg", "很抱歉，您选择是收货地址无效，请重新选择！");
			return result;
		}
		order.setAddressId(address.getAddressId()); // 收货地址
		// order.setConsignee(address.getName()); // 收货人
		// order.setCellphone(address.getCellphone()); // 电话

		order.setUserId(Integer.parseInt(userId));

		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");// 设置日期格式
		order.setCreateTime(df.format(new Date()));

		order.setStatus(101);// 状态为待付款

		// 设置中间表信息
		List<ConnectorOP> connects = new ArrayList<ConnectorOP>();
		for (Shoppingcart item : scList) {
			ConnectorOP connect = new ConnectorOP();
			connect.setProductId(item.getProductId());
			connect.setNumber(item.getNumber());
			connect.setColor(shoppingCartStrArr[scList.indexOf(item)]
					.split("##")[1]);
			connect.setSize(shoppingCartStrArr[scList.indexOf(item)]
					.split("##")[2]);
			connect.setPrice(item.getProduct().getCurrentPrice());
			connects.add(connect);
		}
		order.setConnectorOPs(connects);
		order.setNumber(scList.size());
		order.setAmount(amount);
		// 订单进度表
		final OrderProgress op = new OrderProgress();
		op.setStatusCode(201); // 提交订单
		op.setTime(df.format(new Date()));

		// 在这里进行事务的操作
		try {
			Trans.exec(new Atom() {
				@Override
				public void run() {
					// 插入订单信息
					Order o = dao.insertWith(order, "connectorOPs");
					order.setOrderId(o.getOrderId());
					op.setOrderId(o.getOrderId());
					dao.insert(op); // 插入订单进度表
					// 删除购物车中
					for (Shoppingcart shoppingcart : scList) {
						dao.delete(Shoppingcart.class,
								shoppingcart.getShoppingcartId());
					}

					dao.update(Address.class, Chain.make("isOrder", 1),
							Cnd.where("addressId", "=", address.getAddressId()));// 更新地址在订单中已经使用

				}
			});
			result.put("status", "success");
			result.put("orderId", order.getOrderId());
			logger.info("结束调用addToOrder方法，生成订单成功");
			return result;
		} catch (Exception e) {
			result.put("status", "fail");
			result.put("msg", "下单失败");
			logger.info("结束调用addToOrder方法，生成订单失败");
			return result;
		}
	}

	/**
	 * 生成订单流程包括：1，查是否上架；2.查库存;3.插订单表;4.插订单商品关联表;5.插订单进度表; 6.删除购物车数据; 7.更新库存
	 * 
	 * @param session
	 * @param shoppingcartIds
	 *            - 格式为 shoppingcartId@@shoppingcartId
	 * @return
	 */
	@At
	public Object addToOrderNew(HttpSession session,
			@Param("shoppingcartIds") String shoppingcartIds,
			@Param("addressId") String addressId) {

		logger.info("开始调用addToOrder方法，生成订单");
		final HashMap<String, Object> result = new HashMap<String, Object>();
		String userId = String.valueOf(session.getAttribute(FRONT_USER_ID));
		final String[] shoppingcartIdArr = shoppingcartIds.split("@@");
		final List<Shoppingcart> scList = new ArrayList<Shoppingcart>();
		float amount = 0;
		for (int i = 0; i < shoppingcartIdArr.length; i++) {

			Shoppingcart shoppingcart = dao.fetch(
					Shoppingcart.class,
					Cnd.where("shoppingcartId", "=",
							Integer.parseInt(shoppingcartIdArr[i])));
			Product pro = dao.fetch(Product.class,
					Cnd.where("productId", "=", shoppingcart.getProductId()));
			ProductType pt = dao.fetch(
					ProductType.class,
					Cnd.where("productTypeId", "=",
							shoppingcart.getProductTypeId()));

			if (!pro.getStatus()) {
				result.put("status", "fail");
				result.put("msg", "很抱歉，您购买的商品：" + pro.getName() + " 已下架，请谅解！");
				logger.info("结束调用addToOrder方法，生成订单失败，失败原因为商品已下架");
				return result;
			} else {
				// 是限时活动商品 且正在活动中
				if (pro.getActivityType() == 1
						&& pro.getLimitActivityStatus() == 1) {

					ActivityProduct ap = dao.fetch(ActivityProduct.class,
							Cnd.where("productId", "=", pro.getProductId())
									.and("status", "=", 1));

					// 库存不足
					if (ap.getLeftNum() < shoppingcart.getNumber()
							|| pt.getLimitActivityLeftCount() < shoppingcart
									.getNumber()) {
						result.put("status", "redirct");
						result.put("msg", "很抱歉，您购买的限时活动商品：" + pro.getName()
								+ " 已被疯抢一空，下次请早哦！");
						logger.info("结束调用addToOrder方法，生成订单失败，失败原因为商品已售完");
						return result;
					} else {
						amount += shoppingcart.getNumber()
								* pt.getSpecialPrice();
						shoppingcart.setPrice(pt.getSpecialPrice());
						shoppingcart.setLimitActivity(true);
					}
					// 正常流程
				} else {
					if (pt.getStoreCount() < shoppingcart.getNumber()) {
						result.put("status", "fail");
						result.put("msg", "很抱歉，您购买的商品：" + pro.getName()
								+ " 已售完，请谅解！");
						logger.info("结束调用addToOrder方法，生成订单失败，失败原因为商品已售完");
						return result;
					} else {
						amount += shoppingcart.getNumber()
								* pt.getCurrentPrice();
						shoppingcart.setPrice(pt.getCurrentPrice());
						shoppingcart.setLimitActivity(false);
					}
				}
				shoppingcart.setProduct(pro);
				shoppingcart.setProductType(pt);
				scList.add(i, shoppingcart);
			}
		}
		final Order order = new Order();
		// 设置地址信息
		final Address address = dao.fetch(Address.class,
				Cnd.where("addressId", "=", addressId).and("status", "=", 1));
		if (address == null) {
			result.put("status", "fail");
			result.put("msg", "很抱歉，您选择是收货地址无效，请重新选择！");
			return result;
		}
		order.setAddressId(address.getAddressId()); // 收货地址
		// Address address = dao.fetch(Address.class, Cnd.where("addressId",
		// "=", addressId));
		// order.setAddress(address.getCity() + address.getDistrict() +
		// address.getStreet()); // 收货地址
		// order.setConsignee(address.getName()); // 收货人
		// order.setCellphone(address.getCellphone()); // 电话
		order.setUserId(Integer.parseInt(userId));

		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");// 设置日期格式
		order.setCreateTime(df.format(new Date()));

		order.setStatus(101);// 状态为待付款

		// 设置中间表信息
		List<ConnectorOP> connects = new ArrayList<ConnectorOP>();
		for (Shoppingcart item : scList) {
			ConnectorOP connect = new ConnectorOP();
			connect.setProductId(item.getProductId());
			connect.setNumber(item.getNumber());
			connect.setColor(item.getProductType().getColor());
			connect.setSize(item.getProductType().getSize());
			connect.setPrice(item.getPrice());
			connect.setLimitActivity(item.isLimitActivity());
			connects.add(connect);
		}
		order.setConnectorOPs(connects);
		order.setNumber(scList.size());
		order.setAmount(amount);
		// 订单进度表
		final OrderProgress op = new OrderProgress();
		op.setStatusCode(201); // 提交订单
		op.setTime(df.format(new Date()));

		// 在这里进行事务的操作
		try {
			Trans.exec(new Atom() {
				@Override
				public void run() {
					// 插入订单信息和中间表
					Order o = dao.insertWith(order, "connectorOPs");

					dao.update(Address.class, Chain.make("isOrder", 1),
							Cnd.where("addressId", "=", address.getAddressId()));// 更新地址在订单中已经使用

					for (Shoppingcart item : scList) {
						Product pro = dao.fetch(Product.class, Cnd.where(
								"productId", "=", item.getProductId()));
						ProductType pt = dao.fetch(
								ProductType.class,
								Cnd.where("productTypeId", "=",
										item.getProductTypeId()));
						// 是限时活动商品 且正在活动中
						if (pro.getActivityType() == 1
								&& pro.getLimitActivityStatus() == 1) {

							ActivityProduct ap = dao.fetch(
									ActivityProduct.class,
									Cnd.where("productId", "=",
											pro.getProductId()).and("status",
											"=", 1));

							// 库存不足
							if (ap.getLeftNum() < item.getNumber()
									|| pt.getLimitActivityLeftCount() < item
											.getNumber()) {
								result.put("status", "redirct");
								result.put("msg",
										"很抱歉，您购买的限时活动商品：" + pro.getName()
												+ " 已被疯抢一空，下次请早哦！");
								logger.info("结束调用addToOrder方法，生成订单失败，失败原因为商品已售完");
								throw new RuntimeException("很抱歉，您购买的限时活动商品："
										+ pro.getName() + " 已被疯抢一空，下次请早哦！");
							} else {
								// 在这里更新库存
								if (ap.getLeftNum() == item.getNumber()
										|| pt.getLimitActivityLeftCount() == item
												.getNumber()) {
									// 更新活动状态
									pro.setLimitActivityStatus(2);
								}

								String sqlStr = "update activityproduct set leftNum = leftNum-@num "
										+ " where  leftNum-@num >0 and activityId =@activityId ";
								Sql sql = Sqls.create(sqlStr);
								sql.params().set("num", item.getNumber());
								sql.params().set("activityId",
										ap.getActivityId());
								sql.setCallback(Sqls.callback.maps());
								dao.execute(sql);
								int result = sql.getInt();// 获取影响行数
								if (result == 0) {
									throw new RuntimeException(
											"很抱歉，您购买的限时活动商品：" + pro.getName()
													+ " 已被疯抢一空，下次请早哦！");
								}

								// dao.update(ActivityProduct.class,
								// Chain.make(name, value));
								// pro.setLimitActivityTotalLeftCount(ap
								// .getLeftNum() - item.getNumber());
								pro.setSellCount(pro.getSellCount()
										+ item.getNumber());
								pt.setLimitActivityLeftCount(pt
										.getLimitActivityLeftCount()
										- item.getNumber());
								dao.update(pro);
								dao.update(pt);
							}
							// 正常流程
						} else {
							if (pt.getStoreCount() < item.getNumber()) {
								result.put("status", "fail");
								result.put("msg", "很抱歉，您购买的商品：" + pro.getName()
										+ " 已售完，请谅解！");
								logger.info("结束调用addToOrder方法，生成订单失败，失败原因为商品已售完");
								return;
							} else {
								// 在这里更新库存
								pt.setStoreCount(pt.getStoreCount()
										- item.getNumber());
								pt.setSellCount(pt.getSellCount()
										+ item.getNumber());
								pro.setSellCount(pro.getSellCount()
										+ item.getNumber());
								dao.update(pt);
							}
						}
					}
					order.setOrderId(o.getOrderId());
					op.setOrderId(o.getOrderId());
					dao.insert(op); // 插入订单进度表
					// 删除购物车中
					for (Shoppingcart shoppingcart : scList) {
						dao.delete(Shoppingcart.class,
								shoppingcart.getShoppingcartId());
					}
				}
			});
			result.put("status", "success");
			result.put("orderId", order.getOrderId());
			logger.info("结束调用addToOrder方法，生成订单成功");
			return result;
		} catch (Exception e) {
			result.put("status", "fail");
			result.put("msg", "下单失败");
			logger.info("结束调用addToOrder方法，生成订单失败");
			return result;
		}
	}

	// 添加订单信息，但此时没有进行支付操作
	@At
	public Object addOrder1(@Param("userId") int userId,
			@Param("productIds") String productIds, @Param("size") String size,
			@Param("color") String color) {
		HashMap<String, Object> result = new HashMap<String, Object>();
		String[] pids = productIds.split("@");
		final List<Product> ps = new ArrayList<Product>();
		// 根据传过来的参数进行判断，看商品是否处于可销售状态
		for (String id : pids) {
			Product p = dao.fetch(Product.class,
					Cnd.where("productId", "=", Integer.parseInt(id)));
			// 首先看该商品处于什么状态，上架或下架，上架则继续进行判断，否则直接返回
			if (!p.getStatus()) {
				result.put("status", "fail");
				result.put("msg", "很抱歉，您购买的商品：" + p.getName() + " 已下架，请谅解！");
				return result;
			} else {
				// 根据尺码，颜色来获取当前商品的库存量
				// 首先获取商品规格对象
				ProductType pt = dao.fetch(ProductType.class,
						Cnd.where("size", "=", size).and("color", "=", color)
								.and("productId", "=", p.getProductId()));
				StoreCount sc = dao.fetch(StoreCount.class,
						Cnd.where("productTypeId", "=", pt.getProductTypeId()));
				if (sc.getStoreCount() > 0) {
					try {
						// 在这里与支付宝进行交互
						Pingpp.apiKey = "app_nLyzfPLSufj5mPO4";
						Map<String, Object> chargeParams = new HashMap<String, Object>();
						chargeParams.put("order_no", "123456789");
						chargeParams.put("amount", 100);
						Map<String, String> app = new HashMap<String, String>();
						app.put("id", "app_1Gqj58ynP0mHeX1q");
						chargeParams.put("app", app);
						chargeParams.put("channel", "alipay");
						chargeParams.put("currency", "cny");
						chargeParams.put("client_ip", "127.0.0.1");
						chargeParams.put("subject", "Your Subject");
						chargeParams.put("body", "Your Body");
						Charge.create(chargeParams);
						// 在这里与支付宝进行交互
						if (true) {
							// 销量+1
							p.setSellCount(p.getSellCount() + 1);
							// 库存-1
							sc.setStoreCount(sc.getStoreCount() - 1);
							// 将该商品加入缓存队列中
							ps.add(p);
						} else {
							result.put("status", "fail");
							result.put("msg", "很抱歉，您购买的商品付款失败！");
							return result;
						}
					} catch (AuthenticationException e) {
						// TODO Auto-generated catch block
						result.put("status", "fail");
						result.put("msg", "很抱歉，您购买的商品付款失败！");
						return result;
					} catch (InvalidRequestException e) {
						// TODO Auto-generated catch block
						result.put("status", "fail");
						result.put("msg", "很抱歉，您购买的商品付款失败！");
						return result;
					} catch (APIConnectionException e) {
						// TODO Auto-generated catch block
						result.put("status", "fail");
						result.put("msg", "很抱歉，您购买的商品付款失败！");
						return result;
					} catch (APIException e) {
						// TODO Auto-generated catch block
						result.put("status", "fail");
						result.put("msg", "很抱歉，您购买的商品付款失败！");
						return result;
					} catch (ChannelException e) {
						// TODO Auto-generated catch block
						result.put("status", "fail");
						result.put("msg", "很抱歉，您购买的商品付款失败！");
						return result;
					}
				} else {
					result.put("status", "fail");
					result.put("msg", "很抱歉，您购买的商品：" + p.getName() + " 已售完，请谅解！");
					return result;
				}
			}
		}
		final Order order = new Order();
		order.setUserId(userId);
		// order.setCount(false);
		// 在这里进行事务的操作
		try {
			Trans.exec(new Atom() {
				@Override
				public void run() {
					// 插入订单信息
					Order o = dao.insert(order);
					// 进行订单与商品的关联
					for (Product p : ps) {
						ConnectorOP op = new ConnectorOP();
						op.setOrderId(o.getOrderId());
						op.setProductId(p.getProductId());
						dao.insert(op);
						dao.update(p);
					}
				}
			});
			result.put("status", "success");
			return result;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			result.put("status", "fail");
			result.put("msg", "下单失败");
			return result;
		}

	}
}
