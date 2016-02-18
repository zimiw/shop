package com.easyshop.bean.express;

import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

/**
 * 订单确认/取消接口
 * 
 * @author luocz
 *
 */
public class OrderConfirm {

	@XStreamAsAttribute
	private String orderid;// String(64) 是 客户订单号

	@XStreamAsAttribute
	private String mailno;// String(20) 条件 顺丰母运单号(如果dealtype=1，必填)

	@XStreamAsAttribute
	private int dealtype;// Number(1) 否 1 客户订单操作标识：  1：确认  2：取消

	// OrderConfirmOption

	public String getOrderid() {
		return orderid;
	}

	public void setOrderid(String orderid) {
		this.orderid = orderid;
	}

	public String getMailno() {
		return mailno;
	}

	public void setMailno(String mailno) {
		this.mailno = mailno;
	}

	public int getDealtype() {
		return dealtype;
	}

	public void setDealtype(int dealtype) {
		this.dealtype = dealtype;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("OrderConfirm [orderid=");
		builder.append(orderid);
		builder.append(", mailno=");
		builder.append(mailno);
		builder.append(", dealtype=");
		builder.append(dealtype);
		builder.append("]");
		return builder.toString();
	}

}
