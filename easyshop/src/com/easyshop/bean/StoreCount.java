package com.easyshop.bean;

import org.nutz.dao.entity.annotation.Column;
import org.nutz.dao.entity.annotation.Id;
import org.nutz.dao.entity.annotation.Table;

@Table("storecount")
/*
 * 库存表
 * */
public class StoreCount {
	@Id
	private int storeCountId;
	@Column
	private double storeCount;//库存量
	@Column
	private int productTypeId;//商品规格记录ID
	
	public int getProductTypeId() {
		return productTypeId;
	}
	public void setProductTypeId(int productTypeId) {
		this.productTypeId = productTypeId;
	}
	public int getStoreCountId() {
		return storeCountId;
	}
	public void setStoreCountId(int storeCountId) {
		this.storeCountId = storeCountId;
	}
	public double getStoreCount() {
		return storeCount;
	}
	public void setStoreCount(double storeCount) {
		this.storeCount = storeCount;
	}
}
