package com.easyshop.bean;

import org.nutz.dao.entity.annotation.Column;
import org.nutz.dao.entity.annotation.Id;
import org.nutz.dao.entity.annotation.Table;

/**
 * 活动管理 热卖商品
 * 
 * @author luocz
 *
 */
@Table("activtyheat")
public class ActivtyHeat {

	@Id
	private int id;

	@Column
	private int productId;// 商品id
	@Column
	private int type;// 1：热卖1， 2：热卖2

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getProductId() {
		return productId;
	}

	public void setProductId(int productId) {
		this.productId = productId;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("ActivtyHeat [id=");
		builder.append(id);
		builder.append(", productId=");
		builder.append(productId);
		builder.append(", type=");
		builder.append(type);
		builder.append("]");
		return builder.toString();
	}

}
