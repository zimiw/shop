package com.easyshop.bean.express;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

/**
 * 路由推送接口
 * 
 * @author luocz
 *
 */
@XStreamAlias("WaybillRoute")
public class WaybillRoute {

	@XStreamAsAttribute
	private String id;// Number(15) 是 路由节点信息编号，每一个id代表一条不同的路由节点信息。

	@XStreamAsAttribute
	private String mailno;// String(20) 是 顺丰运单号

	@XStreamAsAttribute
	private String orderid;// String(64) 是 客户订单号

	@XStreamAsAttribute
	private String acceptTime;// Date 是 路由节点产生的时间，格式：YYYY-MM-DD
								// HH24:MM:SS，示例：2012-7-30 09:30:00。
	@XStreamAsAttribute
	private String acceptAddress;// String(100) 是 路由节点发生的城市

	@XStreamAsAttribute
	private String remark;// String(300) 是 路由节点具体描述

	@XStreamAsAttribute
	private String opCode;// String(20) 否 路由节点操作码

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getMailno() {
		return mailno;
	}

	public void setMailno(String mailno) {
		this.mailno = mailno;
	}

	public String getOrderid() {
		return orderid;
	}

	public void setOrderid(String orderid) {
		this.orderid = orderid;
	}

	public String getAcceptTime() {
		return acceptTime;
	}

	public void setAcceptTime(String acceptTime) {
		this.acceptTime = acceptTime;
	}

	public String getAcceptAddress() {
		return acceptAddress;
	}

	public void setAcceptAddress(String acceptAddress) {
		this.acceptAddress = acceptAddress;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public String getOpCode() {
		return opCode;
	}

	public void setOpCode(String opCode) {
		this.opCode = opCode;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("WaybillRoute [id=");
		builder.append(id);
		builder.append(", mailno=");
		builder.append(mailno);
		builder.append(", orderid=");
		builder.append(orderid);
		builder.append(", acceptTime=");
		builder.append(acceptTime);
		builder.append(", acceptAddress=");
		builder.append(acceptAddress);
		builder.append(", remark=");
		builder.append(remark);
		builder.append(", opCode=");
		builder.append(opCode);
		builder.append("]");
		return builder.toString();
	}

}
