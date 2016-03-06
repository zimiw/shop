package com.easyshop.bean;

import java.util.Date;

import org.nutz.dao.entity.annotation.Column;
import org.nutz.dao.entity.annotation.Id;
import org.nutz.dao.entity.annotation.Table;

/**
 * 最新浏览记录
 * 
 * @author luocz
 *
 */
@Table("productscan")
public class ProductScan {

	@Id
	private int id; // id

	@Column
	private String personalId;// 会员id

	@Column
	private int productTypeId;

	@Column
	private int productId;// 商品ID

	@Column
	private Date scanTime;// 浏览时间

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getPersonalId() {
		return personalId;
	}

	public void setPersonalId(String personalId) {
		this.personalId = personalId;
	}

	public int getProductTypeId() {
		return productTypeId;
	}

	public void setProductTypeId(int productTypeId) {
		this.productTypeId = productTypeId;
	}

	public int getProductId() {
		return productId;
	}

	public void setProductId(int productId) {
		this.productId = productId;
	}

	public Date getScanTime() {
		return scanTime;
	}

	public void setScanTime(Date scanTime) {
		this.scanTime = scanTime;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("ProductScan [id=");
		builder.append(id);
		builder.append(", personalId=");
		builder.append(personalId);
		builder.append(", productTypeId=");
		builder.append(productTypeId);
		builder.append(", productId=");
		builder.append(productId);
		builder.append(", scanTime=");
		builder.append(scanTime);
		builder.append("]");
		return builder.toString();
	}

}
