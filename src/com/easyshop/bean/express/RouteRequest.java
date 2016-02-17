package com.easyshop.bean.express;

import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

/**
 * 路由查询接口
 * 
 * @author luocz
 *
 */
public class RouteRequest {

	@XStreamAsAttribute
	private int tracking_type;// Number(2) 否 1 查询号类别：

	@XStreamAsAttribute
	private String tracking_number;// String(4000) 是 查询号： 
									// 如果tracking_type=1，则此值为顺丰运单号 
									// 如果tracking_type=2，则此值为客户订单号
									// 如果有多个单号，以逗号分隔，

	@XStreamAsAttribute
	private int method_type;// Number(1) 否 1 路由查询类别：  1：标准路由查询  2：定制路由查询

	public int getTracking_type() {
		return tracking_type;
	}

	public void setTracking_type(int tracking_type) {
		this.tracking_type = tracking_type;
	}

	public String getTracking_number() {
		return tracking_number;
	}

	public void setTracking_number(String tracking_number) {
		this.tracking_number = tracking_number;
	}

	public int getMethod_type() {
		return method_type;
	}

	public void setMethod_type(int method_type) {
		this.method_type = method_type;
	}

}
