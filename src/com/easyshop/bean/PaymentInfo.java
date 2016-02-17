package com.easyshop.bean;

import org.nutz.dao.entity.annotation.Column;
import org.nutz.dao.entity.annotation.Id;
import org.nutz.dao.entity.annotation.Table;

/**
 * 支付信息表-记录每笔订单的支付相关信息
 * @author wangzhiming
 * @date 2015.1.13
 */
@Table("paymentinfo")
public class PaymentInfo {

	@Id
	private int id;
	@Column
	private int orderId;  //订单id
	@Column
	private int payWay;  //支付方式（1-银联,2-微信,3-支付宝）
	@Column
	private String acconut;  //支付帐号
	@Column
	private float amount;  //支付金额
	@Column
	private String payTime;  //支付时间
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getOrderId() {
		return orderId;
	}
	public void setOrderId(int orderId) {
		this.orderId = orderId;
	}
	public int getPayWay() {
		return payWay;
	}
	public void setPayWay(int payWay) {
		this.payWay = payWay;
	}
	public String getAcconut() {
		return acconut;
	}
	public void setAcconut(String acconut) {
		this.acconut = acconut;
	}
	public float getAmount() {
		return amount;
	}
	public void setAmount(float amount) {
		this.amount = amount;
	}
	public String getPayTime() {
		return payTime;
	}
	public void setPayTime(String payTime) {
		this.payTime = payTime;
	}
}
