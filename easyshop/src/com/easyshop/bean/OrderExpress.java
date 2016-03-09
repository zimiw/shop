package com.easyshop.bean;

import org.nutz.dao.entity.annotation.Column;
import org.nutz.dao.entity.annotation.Id;
import org.nutz.dao.entity.annotation.Table;

/**
 * 订单发送寄件放信息
 * 
 * @author luocz
 */
@Table("orderexpress")
public class OrderExpress {

	@Id
	private int id;

	@Column
	private String transportId;

	@Column
	private int orderId;

	@Column
	private int addressId;
	@Column
	private String phone;// 联系方式
	@Column
	private String name;// 姓名
	@Column
	private String street;// 详细地址
	@Column
	private String postcode;// 邮政编码

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getTransportId() {
		return transportId;
	}

	public void setTransportId(String transportId) {
		this.transportId = transportId;
	}

	public int getOrderId() {
		return orderId;
	}

	public void setOrderId(int orderId) {
		this.orderId = orderId;
	}

	public int getAddressId() {
		return addressId;
	}

	public void setAddressId(int addressId) {
		this.addressId = addressId;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getStreet() {
		return street;
	}

	public void setStreet(String street) {
		this.street = street;
	}

	public String getPostcode() {
		return postcode;
	}

	public void setPostcode(String postcode) {
		this.postcode = postcode;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("OrderExpress [id=");
		builder.append(id);
		builder.append(", transportId=");
		builder.append(transportId);
		builder.append(", orderId=");
		builder.append(orderId);
		builder.append(", addressId=");
		builder.append(addressId);
		builder.append(", phone=");
		builder.append(phone);
		builder.append(", name=");
		builder.append(name);
		builder.append(", street=");
		builder.append(street);
		builder.append(", postcode=");
		builder.append(postcode);
		builder.append("]");
		return builder.toString();
	}

}
