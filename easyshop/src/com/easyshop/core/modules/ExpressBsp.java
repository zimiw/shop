package com.easyshop.core.modules;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.nutz.dao.Cnd;
import org.nutz.dao.Dao;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.mvc.annotation.Param;

import com.easyshop.bean.Address;
import com.easyshop.bean.ConnectorOP;
import com.easyshop.bean.ExpressRoute;
import com.easyshop.bean.Order;
import com.easyshop.bean.Product;
import com.easyshop.bean.express.Cargo;
import com.easyshop.bean.express.Error;
import com.easyshop.bean.express.ErrorConverter;
import com.easyshop.bean.express.ExperssResult;
import com.easyshop.bean.express.ExpressOrder;
import com.easyshop.bean.express.HttpClientTemplate;
import com.easyshop.bean.express.OrderResponse;
import com.easyshop.bean.express.OrderSearch;
import com.easyshop.bean.express.Request;
import com.easyshop.bean.express.Response;
import com.easyshop.bean.express.Route;
import com.easyshop.bean.express.RouteRequest;
import com.easyshop.bean.express.RouteResponse;
import com.easyshop.bean.express.Util;
import com.easyshop.bean.express.WaybillRoute;
import com.easyshop.core.modules.admin.OrderConstant;
import com.easyshop.core.modules.admin.OrderUtil;
import com.easyshop.utils.StringUtils;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.XmlFriendlyNameCoder;
import com.thoughtworks.xstream.io.xml.XppDriver;

/**
 * 顺丰接口封装
 * 
 * @author luocz
 */
@IocBean
public class ExpressBsp {

	private final Logger logger = Logger.getLogger(ExpressBsp.class);

	private final static String url = "http://bspoisp.sit.sf-express.com:11080/bsp-oisp/sfexpressService";
	private final static String checkWord = "j8DzkIFgmlomPt0aLuwU";
	private final static String SF_COMPANY = "洋流网";// 传给顺丰的公司名称
	// private final static String J_CONTACT = "";// 寄件方联系人
	// private final static String J_TEL = "";// 寄件方联系电话
	private final static String D_COMPANY = "顺丰速运";// 到件方公司名称
	// private final static String CUSTID = "00009029";// 顺丰月结卡号
	private final static String CUSTID = "7551878519";// 顺丰月结卡号 debug
	private final static int PAY_METHOD = 1; // 付款方式：  1:寄方付  2:收方付 3:第三方付
	// private final static String EXPRESS_TYPE = "";

	// 账号：yangliu
	// 密码：wj7939446
	// APP ID ： 00009029
	// APP KEY ： E215D46D3724D40B674F0FE8943BAE48

	@Inject
	protected Dao dao;

	@Inject
	protected OrderUtil orderUtil;

	/**
	 * 下单接口
	 * 
	 * @return
	 */
	public ExperssResult sfExpressService(Order order, String province,
			String city, String ara, String street, String phone,
			String postcode, String j_contact) {

		ConnectorOP op = dao.fetch(ConnectorOP.class,
				Cnd.where("orderId", "=", order.getOrderId()));
		Product product = dao.fetch(Product.class,
				Cnd.where("productId", "=", op.getProductId()));

		Address address = orderUtil.getAdress(order.getAddressId());
		Request req = new Request("OrderService", "BSPdevelop");
		ExpressOrder exOrder = new ExpressOrder();
		exOrder.setOrderid(String.valueOf(order.getOrderId()));// 订单id
		exOrder.setIs_gen_bill_no(1);
		exOrder.setJ_company(SF_COMPANY);

		exOrder.setJ_contact(j_contact);// 发货人
		exOrder.setJ_tel(phone);
		exOrder.setJ_province(province);// 省
		exOrder.setJ_city(city);
		exOrder.setJ_county(ara);// 区县
		exOrder.setJ_address(street);
		exOrder.setJ_post_code(postcode);

		exOrder.setD_company(D_COMPANY);// 到件方公司名称
		exOrder.setD_contact(address.getName());// 到件方联系人;

		String dPhone = address.getCellphone();
		dPhone = StringUtils.isEmpty(dPhone) ? address.getPhone() : dPhone;
		exOrder.setD_tel(dPhone);
		exOrder.setD_province(address.getProvince());// 所在省份
		exOrder.setD_city(address.getCity());
		exOrder.setD_county(address.getDistrict());// 区县
		exOrder.setD_address(address.getStreet());
		exOrder.setCustid(CUSTID);
		exOrder.setPay_method(PAY_METHOD);
		// exOrder.setParcel_quantity(1);// 包裹数

		Cargo cargo = new Cargo();
		cargo.setName(product.getName());// 商品名
		cargo.setCurrency("CNY");
		exOrder.setCargo(cargo);
		req.setOrder(exOrder);

		String resultStr = sfRequest(req);
		if (resultStr == null || "".equals(resultStr)) {// 系统请求异常,再调用一下订单查询
			resultStr = orderSea(String.valueOf(order.getOrderId()));
		}

		Response res = analysisXml(resultStr);
		logger.debug("顺丰下单请求：" + res);
		ExperssResult result = new ExperssResult();
		result.setHead(res.getHead());
		if (OrderConstant.SF_OK.equals(res.getHead())) {// 成功
			OrderResponse orderRes = res.getBody().getOrderResponse();
			orderRes = orderRes == null ? res.getBody()
					.getOrderSearchResponse() : orderRes;

			if (orderRes.getFilter_result() == 2) {// 可派件, 下单成功
				result.setHead(OrderConstant.SF_OK);
				result.setMailno(orderRes.getMailno());
			} else if (orderRes.getFilter_result() == 3) {// 不可以派件
				result.setHead(OrderConstant.SF_ERR);
				result.setMsg(orderRes.getRemark());
			} else {// 人工确认
				result.setHead(OrderConstant.SF_ERR);
				result.setMsg("顺丰返回信息：人工确认， 请联系顺丰!");
			}
		} else {
			Error error = res.getERROR();
			result.setMsg(String.format("顺丰下单失败错误代码：%s, %s", error.getCode(),
					error.getValue()));
		}

		return result;
	}

	/**
	 * 查询订单物流信息
	 * 
	 * @return
	 */
	public List<ExpressRoute> queryOrderRoute(@Param("orderId") String orderId) {

		List<ExpressRoute> list = dao.query(ExpressRoute.class,
				Cnd.where("orderId", "=", orderId));

		return list;
	}

	/**
	 * 调用顺丰接口查询路由
	 * 
	 * @return
	 */
	public List<ExpressRoute> queryRouteSearch(String orderId) {

		Request req = new Request("RouteService", "BSPdevelop");

		RouteRequest routeRequest = new RouteRequest();
		routeRequest.setTracking_type(2);// 2：根据客户订单号查询， order 节点中
											// tracking_number 将被当作客户订单号处理
		routeRequest.setTracking_number(orderId);// 客户订单号
		routeRequest.setMethod_type(1);// 1：标准路由查询
		req.setRouteRequest(routeRequest);
		String resultStr = sfRequest(req);

		if (!StringUtils.isEmpty(resultStr)) {
			logger.equals("调用顺丰接口查询路由查询失败");
			return null;
		}

		Response res = analysisXml(resultStr);
		RouteResponse routeResponse = res.getBody().getRouteResponse();
		List<ExpressRoute> exList = null;
		if (routeResponse != null && routeResponse.getRoute() != null
				&& routeResponse.getRoute().size() > 0) {
			List<Route> routeList = routeResponse.getRoute();
			ExpressRoute expressRoute = null;
			exList = new LinkedList<ExpressRoute>();
			dao.clear(ExpressRoute.class, Cnd.where("orderId", "=", orderId));// 删除该订单之前的数据

			for (Route route : routeList) {
				expressRoute = new ExpressRoute();
				expressRoute.setAcceptAddress(route.getAccept_address());
				expressRoute.setAcceptTime(route.getAccept_time());
				expressRoute.setOpCode(route.getOpcode());
				expressRoute.setRemark(route.getRemark());
				expressRoute.setOrderId(orderId);
				exList.add(expressRoute);
				dao.insert(expressRoute);
			}

		} else {
			logger.debug("queryRouteSearch 顺丰查询接口无路由信息");
		}

		return exList;
	}

	/**
	 * 物流推送接口
	 * 
	 * @return
	 */
	public String routePush(String xml) {

		Response response = new Response("RoutePushService", "");
		XStream xstream = getXStream();

		String result = "";
		if ("".equals(xml)) {
			response.setHead(OrderConstant.SF_ERR);
			Error error = new Error();
			error.setCode("4001");
			error.setValue("推送的xml数据为空");
			response.setERROR(error);
			result = xstream.toXML(response);
			logger.error("routePush " + result);
			return result;
		}

		logger.debug("物流推送信息 routePush:" + xml);
		try {

			xstream.processAnnotations(Request.class);
			Request request = (Request) xstream.fromXML(xml);
			WaybillRoute route = request.getBody().getWaybillRoute();
			ExpressRoute expressRoute = new ExpressRoute();
			expressRoute.setAcceptAddress(route.getAcceptAddress());
			expressRoute.setAcceptTime(route.getAcceptTime());
			expressRoute.setOpCode(route.getOpCode());
			expressRoute.setRemark(route.getRemark());
			expressRoute.setOrderId(route.getOrderid());
			dao.insert(expressRoute);

			response.setHead(OrderConstant.SF_OK);
		} catch (Exception e) {
			logger.error(" routePush 解析失败:", e);
			response.setHead(OrderConstant.SF_ERR);
			Error error = new Error();
			error.setCode("4001");
			error.setValue("系统发生数据错误或运行时异常");
			response.setERROR(error);
		}

		result = xstream.toXML(response);
		logger.debug("routePush result :" + result);
		return result;
	}

	/**
	 * 对外提供接口订单查询
	 * 
	 * @param OrderId
	 * @return
	 */
	public ExperssResult orderSearch(String OrderId) {
		ExperssResult result = new ExperssResult();

		String resultStr = orderSea(OrderId);
		Response res = analysisXml(resultStr);

		if (OrderConstant.SF_OK.equals(res.getHead())) {// 成功
			OrderResponse orderRes = res.getBody().getOrderResponse();
			if (orderRes.getFilter_result() == 2) {// 可派件, 下单成功
				result.setHead(OrderConstant.SF_OK);
				result.setMailno(orderRes.getMailno());
			} else if (orderRes.getFilter_result() == 3) {// 不可以派件
				result.setHead(OrderConstant.SF_ERR);
				result.setMsg(orderRes.getRemark());
			} else {// 人工确认
				result.setHead(OrderConstant.SF_ERR);
				result.setMsg("顺丰返回信息：人工确认， 请联系顺丰!");
			}
		} else {
			Error error = res.getERROR();
			result.setMsg(String.format("顺丰查询失败错误代码：%s, %s", error.getCode(),
					error.getValue()));
		}
		return result;
	}

	/**
	 * 订单查询
	 */
	private String orderSea(String OrderId) {
		Request res = new Request("OrderConfirmService", "OrderSearchService");
		OrderSearch search = new OrderSearch();
		search.setOrderid(OrderId);
		res.getBody().setOrderSearch(search);

		return sfRequest(res);
	}

	/**
	 * 顺丰接口请求封装
	 */
	private String sfRequest(Request request) {
		XStream xstream = getXStream();
		String xml = xstream.toXML(request);
		logger.debug("ExpressBsp sfRequest:" + xml);
		HttpClientTemplate http = new HttpClientTemplate();
		String reuslt = http.executePost(url, getParams(xml));
		logger.debug("ExpressBsp sfRequest reuslt:" + reuslt);
		return reuslt;
	}

	private Response analysisXml(String xml) {

		XStream xstream2 = getXStream();
		xstream2.processAnnotations(Response.class);
		xstream2.processAnnotations(Error.class);
		xstream2.useAttributeFor(Error.class, "code");
		xstream2.registerConverter(new ErrorConverter());
		Response res2 = (Response) xstream2.fromXML(xml);
		logger.debug("ExpressBsp analysisXml reuslt:" + res2);
		return res2;
	}

	private XStream getXStream() {
		XStream xstream = new XStream(new XppDriver(new XmlFriendlyNameCoder(
				"_-", "_")));
		xstream.autodetectAnnotations(true);
		return xstream;
	}

	private Map<String, String> getParams(String xml) {
		Map<String, String> params = new HashMap<String, String>();
		String verifyCode = Util.md5EncryptAndBase64(xml + checkWord);
		// "xml=" + xml + "&verifyCode=" + verifyCode
		params.put("xml", xml);
		params.put("verifyCode", verifyCode);
		return params;
	}

}
