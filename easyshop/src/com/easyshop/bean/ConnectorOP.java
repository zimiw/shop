package com.easyshop.bean;

import org.nutz.dao.entity.annotation.Column;
import org.nutz.dao.entity.annotation.Id;
import org.nutz.dao.entity.annotation.Table;

@Table("connectorOP")
public class ConnectorOP {
	@Id
	private int connectorOPId;
	@Column
	private int orderId;
	@Column
	private int productId;
	@Column
	private int number; // 商品成交数量
	@Column
	private float price; // 商品成交单价
	@Column
	private String color;
	@Column
	private String size;
	@Column
	private int productTypeId; //商品类型id
	@Column
	private boolean isLimitActivity; //是否限时活动 

	public int getConnectorOPId() {
		return connectorOPId;
	}

	public void setConnectorOPId(int connectorOPId) {
		this.connectorOPId = connectorOPId;
	}

	public int getOrderId() {
		return orderId;
	}

	public void setOrderId(int orderId) {
		this.orderId = orderId;
	}

	public int getProductId() {
		return productId;
	}

	public void setProductId(int productId) {
		this.productId = productId;
	}

	// public Product getProduct() {
	// return product;
	// }
	//
	// public void setProduct(Product product) {
	// this.product = product;
	// }

	public int getNumber() {
		return number;
	}

	public void setNumber(int number) {
		this.number = number;
	}

	public float getPrice() {
		return price;
	}

	public void setPrice(float price) {
		this.price = price;
	}

	public String getColor() {
		return color;
	}

	public void setColor(String color) {
		this.color = color;
	}

	public String getSize() {
		return size;
	}

	public void setSize(String size) {
		this.size = size;
	}

	public int getProductTypeId() {
		return productTypeId;
	}

	public void setProductTypeId(int productTypeId) {
		this.productTypeId = productTypeId;
	}

	public boolean isLimitActivity() {
		return isLimitActivity;
	}

	public void setLimitActivity(boolean isLimitActivity) {
		this.isLimitActivity = isLimitActivity;
	}

}
