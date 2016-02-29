package com.easyshop.bean;

import org.nutz.dao.entity.annotation.Column;
import org.nutz.dao.entity.annotation.Id;
import org.nutz.dao.entity.annotation.Table;

@Table("producttype")
/*
 * 商品规格表
 */
public class ProductType {
	@Id
	private int productTypeId;
	@Column
	private String size;
	@Column
	private String color;
	@Column
	private String unit;
	@Column
	private int productId;

	private Product product;
	@Column
	private int storeCount;
	@Column
	private int sellCount;

	/**
	 * 进货价
	 */
	@Column
	private float supplyPrice;// 进货价

	/**
	 * 原价 展示在商品页面的原价
	 */
	@Column
	private float originPrice;// 原价 展示在商品页面的原价

	/**
	 * 现价 展示在商品页面的现价 洋流价.
	 */
	@Column
	private float currentPrice;// 现价 展示在商品页面的现价 洋流价

	/**
	 * 限时活动的价格
	 */
	@Column
	private float specialPrice;// 限时活动的价格

	/**
	 * 参加限时活动的 剩余商品数量
	 */
	@Column
	private int limitActivityLeftCount;// 参加限时活动的 剩余商品数量

	public float getSupplyPrice() {
		return supplyPrice;
	}

	public void setSupplyPrice(float supplyPrice) {
		this.supplyPrice = supplyPrice;
	}

	// public int getLimitActivityLeftCount() {
	// return limitActivityLeftCount;
	// }
	//
	// public void setLimitActivityLeftCount(int limitActivityLeftCount) {
	// this.limitActivityLeftCount = limitActivityLeftCount;
	// }

	public Product getProduct() {
		return product;
	}

	public void setProduct(Product product) {
		this.product = product;
	}

	public float getCurrentPrice() {
		return currentPrice;
	}

	public void setCurrentPrice(float currentPrice) {
		this.currentPrice = currentPrice;
	}

	public float getOriginPrice() {
		return originPrice;
	}

	public void setOriginPrice(float originPrice) {
		this.originPrice = originPrice;
	}

	public String getUnit() {
		return unit;
	}

	public void setUnit(String unit) {
		this.unit = unit;
	}

	public int getProductTypeId() {
		return productTypeId;
	}

	public void setProductTypeId(int productTypeId) {
		this.productTypeId = productTypeId;
	}

	public String getSize() {
		return size;
	}

	public void setSize(String size) {
		this.size = size;
	}

	public String getColor() {
		return color;
	}

	public void setColor(String color) {
		this.color = color;
	}

	public int getProductId() {
		return productId;
	}

	public void setProductId(int productId) {
		this.productId = productId;
	}

	public int getStoreCount() {
		return storeCount;
	}

	public void setStoreCount(int storeCount) {
		this.storeCount = storeCount;
	}

	public int getSellCount() {
		return sellCount;
	}

	public void setSellCount(int sellCount) {
		this.sellCount = sellCount;
	}

	public float getSpecialPrice() {
		return specialPrice;
	}

	public void setSpecialPrice(float specialPrice) {
		this.specialPrice = specialPrice;
	}

	public int getLimitActivityLeftCount() {
		return limitActivityLeftCount;
	}

	public void setLimitActivityLeftCount(int limitActivityLeftCount) {
		this.limitActivityLeftCount = limitActivityLeftCount;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("ProductType [productTypeId=");
		builder.append(productTypeId);
		builder.append(", size=");
		builder.append(size);
		builder.append(", color=");
		builder.append(color);
		builder.append(", unit=");
		builder.append(unit);
		builder.append(", productId=");
		builder.append(productId);
		builder.append(", product=");
		builder.append(product);
		builder.append(", storeCount=");
		builder.append(storeCount);
		builder.append(", sellCount=");
		builder.append(sellCount);
		builder.append(", supplyPrice=");
		builder.append(supplyPrice);
		builder.append(", originPrice=");
		builder.append(originPrice);
		builder.append(", currentPrice=");
		builder.append(currentPrice);
		builder.append(", specialPrice=");
		builder.append(specialPrice);
		builder.append(", limitActivityLeftCount=");
		builder.append(limitActivityLeftCount);
		builder.append("]");
		return builder.toString();
	}
}
