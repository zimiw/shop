package com.easyshop.bean.express;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

/**
 * 请求
 * 
 * @author luocz
 */
@XStreamAlias("Request")
public class Request {

	@XStreamAsAttribute
	private String service;
	@XStreamAsAttribute
	private String lang = "zh-CN";

	private String Head;

	private Body Body;

	public Request(String service, String Head) {
		this.service = service;
		this.Head = Head;
		this.Body = new Body();
	}

	public void setOrder(ExpressOrder order) {
		this.Body.setOrder(order);
	}

	public void setRouteRequest(RouteRequest routeRequest) {
		this.Body.setRouteRequest(routeRequest);
	}

	public void setOrderConfirm(OrderConfirm confirm) {
		this.Body.setOrderConfirm(confirm);
	}

	public void setOrderSearch(OrderSearch orderSearch) {
		this.Body.setOrderSearch(orderSearch);
	}

	public String getService() {
		return service;
	}

	public void setService(String service) {
		this.service = service;
	}

	public String getLang() {
		return lang;
	}

	public void setLang(String lang) {
		this.lang = lang;
	}

	public String getHead() {
		return Head;
	}

	public void setHead(String head) {
		Head = head;
	}

	public Body getBody() {
		return Body;
	}

	public void setBody(Body body) {
		Body = body;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Request [service=");
		builder.append(service);
		builder.append(", lang=");
		builder.append(lang);
		builder.append(", Head=");
		builder.append(Head);
		builder.append(", Body=");
		builder.append(Body);
		builder.append("]");
		return builder.toString();
	}

}
