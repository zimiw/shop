package com.easyshop.bean;

import org.nutz.dao.entity.annotation.Column;
import org.nutz.dao.entity.annotation.Id;
import org.nutz.dao.entity.annotation.Table;

@Table("connectorSU")
public class ConnectorSU {
	@Id
	private int ConnectorSUId;
	@Column
	private int orderId;
	@Column
	private int userId;
	public int getConnectorSUId() {
		return ConnectorSUId;
	}
	public void setConnectorSUId(int connectorSUId) {
		ConnectorSUId = connectorSUId;
	}
	public int getOrderId() {
		return orderId;
	}
	public void setOrderId(int orderId) {
		this.orderId = orderId;
	}
	public int getUserId() {
		return userId;
	}
	public void setUserId(int userId) {
		this.userId = userId;
	}
}
