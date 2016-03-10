package com.easyshop.core.modules;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.nutz.dao.Chain;
import org.nutz.dao.Cnd;
import org.nutz.dao.Dao;
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
import com.easy.core.filters.CheckFrontUserLoginFilter;
import com.easyshop.bean.Address;
import com.easyshop.bean.Area;
import com.easyshop.bean.City;
import com.easyshop.bean.Order;
import com.easyshop.bean.OrderExpress;
import com.easyshop.bean.OrderProgress;
import com.easyshop.bean.Province;
import com.easyshop.bean.express.ExperssResult;
import com.easyshop.core.modules.admin.OrderConstant;
import com.easyshop.core.modules.admin.OrderUtil;
import com.easyshop.utils.StringUtils;
import com.easyshop.utils.TimeUtils;
import com.easyshop.vo.ResultVo;

/**
 * 顺丰快递操作
 * 
 * @author luocz
 */
@IocBean
@At("/express")
@Ok("json")
@Fail("http:500")
public class ExpressModule {

	Logger LOGGER = Logger.getLogger(ChargeModule.class);

	@Inject
	protected Dao dao;
	@Inject
	private ExpressBsp expressBsp;

	@Inject
	protected OrderUtil orderUtil;

	/**
	 * 后台管理员发货
	 * 
	 * @return
	 */
	@At
	@Filters(@By(type = CheckBackUserLoginFilter.class))
	public Object endExpress(@Param("orderId") String orderId,
			@Param("provinceCode") String provinceCode,
			@Param("cityCode") String cityCode,
			@Param("araCode") String araCode,
			@Param("street") final String street,
			@Param("phone") final String phone,
			@Param("postcode") final String postcode,
			@Param("name") final String name) {

		ResultVo resultVo = new ResultVo();
		if (StringUtils.isEmpty(orderId) || StringUtils.isEmpty(provinceCode)
				|| StringUtils.isEmpty(cityCode)
				|| StringUtils.isEmpty(araCode) || StringUtils.isEmpty(street)
				|| StringUtils.isEmpty(phone) || StringUtils.isEmpty(postcode)
				|| StringUtils.isEmpty(name)) {
			resultVo.setStatus(ResultVo.STATUS_FAIL);
			resultVo.setMsg("发货信息不能为空!");
			return resultVo;
		}

		Province pro = dao.fetch(Province.class,
				Cnd.where("code", "=", provinceCode));
		City city = dao.fetch(City.class, Cnd.where("code", "=", cityCode));
		Area area = dao.fetch(Area.class, Cnd.where("code", "=", araCode));

		if (pro == null || city == null || area == null) {
			resultVo.setStatus(ResultVo.STATUS_FAIL);
			resultVo.setMsg("发货信息填写的不正确!");
			return resultVo;
		}

		// 查询对应订单
		final Order order = dao.fetch(Order.class,
				Cnd.where("orderId", "=", orderId));
		if (order == null) {
			resultVo.setStatus(ResultVo.STATUS_FAIL);
			resultVo.setMsg("该订单不存在 !");
			return resultVo;
		}
		if (order.getStatus() != 102) {// 订单非待付款状态，无法取消
			resultVo.setStatus(ResultVo.STATUS_FAIL);
			resultVo.setMsg("该订单待发货状态，无法发货!");
			return resultVo;
		}

		final ExperssResult exRes = expressBsp.sfExpressService(order,
				pro.getName(), city.getName(), area.getName(), street, phone,
				postcode, name);

		if (OrderConstant.SF_OK.equals(exRes.getHead())) {// 下单成功

			Trans.exec(new Atom() {
				@Override
				public void run() {
					// 更新运单号// 更新运单号
					dao.update(
							Order.class,
							Chain.make("transportId", exRes.getMailno()).add(
									"status", 103),
							Cnd.where("orderId", "=", order.getOrderId()));

					OrderProgress op = new OrderProgress();
					op.setOrderId(order.getOrderId());
					op.setStatusCode(203);
					op.setTime(TimeUtils.dateToStr(new Date(),
							TimeUtils.FORMAT14));
					dao.insert(op);

					OrderExpress oe = new OrderExpress();
					oe.setAddressId(order.getAddressId());
					oe.setOrderId(order.getOrderId());
					oe.setName(name);
					oe.setPhone(phone);
					oe.setPostcode(postcode);
					oe.setStreet(street);
					oe.setTransportId(exRes.getMailno());
					dao.insert(oe);
				}
			});

			resultVo.setStatus(ResultVo.STATUS_SUCCESS);
			resultVo.setMsg("该订单发货成功");
		} else {
			resultVo.setStatus(ResultVo.STATUS_FAIL);
			resultVo.setMsg(exRes.getMsg());
		}

		return resultVo;
	}

	/**
	 * 后台用户下单接口
	 * 
	 * @return
	 */
	@At
	@Filters(@By(type = CheckBackUserLoginFilter.class))
	public Object upExpress(@Param("orderId") String orderId,
			@Param("provinceCode") String provinceCode,
			@Param("cityCode") String cityCode,
			@Param("araCode") String araCode, @Param("street") String street,
			@Param("phone") String phone, @Param("postcode") String postcode) {

		ResultVo resultVo = new ResultVo();

		if (StringUtils.isEmpty(orderId) || StringUtils.isEmpty(provinceCode)
				|| StringUtils.isEmpty(cityCode)
				|| StringUtils.isEmpty(araCode) || StringUtils.isEmpty(street)
				|| StringUtils.isEmpty(phone) || StringUtils.isEmpty(postcode)) {
			resultVo.setStatus(ResultVo.STATUS_FAIL);
			resultVo.setMsg("发货信息不能为空!");
			return resultVo;
		}

		Province pro = dao.fetch(Province.class,
				Cnd.where("code", "=", provinceCode));
		City city = dao.fetch(City.class, Cnd.where("code", "=", cityCode));
		Area area = dao.fetch(Area.class, Cnd.where("code", "=", araCode));

		if (pro == null || city == null || area == null) {
			resultVo.setStatus(ResultVo.STATUS_FAIL);
			resultVo.setMsg("发货信息填写的不正确!");
			return resultVo;
		}

		// 查询对应订单
		Order order = dao
				.fetch(Order.class, Cnd.where("orderId", "=", orderId));
		if (order == null) {
			resultVo.setStatus(ResultVo.STATUS_FAIL);
			resultVo.setMsg("该订单不存在!");
			return resultVo;
		}

		// ExperssResult result = expressBsp.sfExpressService(order,
		// pro.getName(), city.getName(), area.getName(), street, phone,
		// postcode);
		//
		// if (OrderConstant.SF_OK.equals(result.getHead())) {// 成功
		// resultVo.setStatus(ResultVo.STATUS_SUCCESS);
		// resultVo.setMsg("下单成功");
		// } else {
		// resultVo.setStatus(ResultVo.STATUS_FAIL);
		// resultVo.setMsg(result.getMsg());
		// }

		return resultVo;
	}

	/**
	 * 后台管理员查询订单物流信息
	 * 
	 * @return
	 */
	@At
	@Filters(@By(type = CheckBackUserLoginFilter.class))
	public Object queryExpressAdmin(@Param("orderId") String orderId) {

		Map<String, Object> result = new HashMap<String, Object>();
		// 查询对应订单
		Order order = dao
				.fetch(Order.class, Cnd.where("orderId", "=", orderId));
		if (order == null) {
			result.put("status", "fail");
			result.put("msg", "该订单不存在 !");
			return result;
		}

		result.put("expressName", "顺丰快递");
		result.put("expressNo", order.getTransportId());
		result.put("expressList", expressBsp.queryOrderRoute(orderId));

		return result;
	}

	/**
	 * 会员查询自己订单物流信息
	 * 
	 * @return
	 */
	@At
	@Filters(@By(type = CheckFrontUserLoginFilter.class))
	public Object queryExpressList(HttpSession session,
			@Param("orderId") String orderId) {
		ResultVo resultVo = new ResultVo();

		String userId = String.valueOf(session
				.getAttribute(OrderConstant.FRONT_USER_ID));
		// 查询对应订单
		Order order = dao.fetch(Order.class, Cnd.where("orderId", "=", orderId)
				.and("userId", "=", userId));
		if (order == null) {
			resultVo.setStatus(ResultVo.STATUS_FAIL);
			resultVo.setMsg("该订单不存在!");
			return resultVo;
		}

		return expressBsp.queryOrderRoute(orderId);
	}

	/**
	 * 订单详情 获取物流新
	 * 
	 * @return
	 */
	@At
	@Filters(@By(type = CheckFrontUserLoginFilter.class))
	public Object queryOrderExpressInfo(HttpSession session,
			@Param("orderId") String orderId) {
		ResultVo resultVo = new ResultVo();

		String userId = String.valueOf(session
				.getAttribute(OrderConstant.FRONT_USER_ID));

		// 查询对应订单
		final Order order = dao.fetch(Order.class,
				Cnd.where("orderId", "=", orderId).and("userId", "=", userId));
		if (order == null) {
			resultVo.setStatus(ResultVo.STATUS_FAIL);
			resultVo.setMsg("该订单不存在!");
			return resultVo;
		}

		OrderExpress oe = dao.fetch(OrderExpress.class,
				Cnd.where("orderId", "=", order.getOrderId()));
		// 收货人信息
		Address addr = orderUtil.getAdress(order.getAddressId());

		Map<String, Object> result = new HashMap<String, Object>();
		result.put("transportId", oe.getTransportId());
		// 收货人姓名、地址、电话
		result.put("name", addr.getName());
		result.put("addr", orderUtil.getAdressDesc(addr));
		result.put("phone", addr.getCellPhoneNew());

		// 发货人姓名、地址、电话
		result.put("jName", oe.getName());
		result.put("street", oe.getStreet());
		result.put("jPhone", oe.getPhone());

		return result;
	}

	/**
	 * 物流路由推送接口
	 * 
	 * @return
	 */
	@At
	@Ok("raw:json")
	public Object routePush(@Param("content") String content) {
	    System.out.println("物流推送信息 routePush:" + content);
		return expressBsp.routePush(content);
	}

	/**
	 * 路由查询接口
	 * 
	 * @return
	 */
	public Object routeSearch(@Param("orderId") String orderId) {

		return null;
	}

	// RoutePushService 顺丰路由推送接口

}
