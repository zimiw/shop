package com.easyshop.bean;

import java.util.List;

import org.nutz.dao.entity.annotation.Column;
import org.nutz.dao.entity.annotation.Id;
import org.nutz.dao.entity.annotation.Table;

@Table("specialSell")
public class SpecialSell {
	@Id
	private int specialSellId;
	@Column
	private String startTime;
	@Column
	private String endTime;
	private List<Product> products;
	public int getSpecialSellId() {
		return specialSellId;
	}
	public void setSpecialSellId(int specialSellId) {
		this.specialSellId = specialSellId;
	}
	public String getStartTime() {
		return startTime;
	}
	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}
	public String getEndTime() {
		return endTime;
	}
	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}
	public List<Product> getProducts() {
		return products;
	}
	public void setProducts(List<Product> products) {
		this.products = products;
	} 
	
}
