package com.easyshop.bean;

import org.nutz.dao.entity.annotation.Column;
import org.nutz.dao.entity.annotation.Id;
import org.nutz.dao.entity.annotation.One;
import org.nutz.dao.entity.annotation.Table;

/**
 * 订单进度表-记录订单在流程中的生命轨迹
 * （提交订单-付款成功-商品出库-等待收获-完成）
 * @author wangzhiming
 * @date 2016.1.13
 */
@Table("orderprogress")
public class OrderProgress {

	@Id
	private int orderprogressId;  
	@Column
	private int orderId;  //订单id
	
	/**
     * 订单状态<br/>
     * 提交订单201， 付款成功202，商品出库203， 等待收货 204，完成 205
     */
	@Column
	private int statusCode; //状态编码
	@Column
	private String time;  //时间
	
	@One(target = StatusDictionary.class, field = "statusCode")
	private StatusDictionary status; //状态详情
	

	public int getOrderprogressId() {
		return orderprogressId;
	}

	public void setOrderprogressId(int orderprogressId) {
		this.orderprogressId = orderprogressId;
	}

	public int getOrderId() {
		return orderId;
	}

	public void setOrderId(int orderId) {
		this.orderId = orderId;
	}

	public int getStatusCode() {
		return statusCode;
	}

	public void setStatusCode(int statusCode) {
		this.statusCode = statusCode;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public StatusDictionary getStatus() {
		return status;
	}

	public void setStatus(StatusDictionary status) {
		this.status = status;
	}
	
}
