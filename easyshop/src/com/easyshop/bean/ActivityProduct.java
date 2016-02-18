package com.easyshop.bean;

import org.nutz.dao.entity.annotation.Column;
import org.nutz.dao.entity.annotation.Id;
import org.nutz.dao.entity.annotation.Table;

/**
 * 活动 ---限时活动
 * 
 * @author luocz
 *
 */
@Table("activityproduct")
public class ActivityProduct {

	@Id
	private int activityId;

	@Column
	private int productId;// 商品id
	@Column
	private int productTypeId;//商品规格ID

	@Column
	private double price;// 抢购价格

	@Column
	private int num;// 参加活动数量

	@Column
	private int leftNum;// 剩余数量

	@Column
	private String beginTime;// 开始时间

	@Column
	private String endTime;// 结束时间

	@Column
	private int status;// 状态 1有限， 0无效

	
	public int getProductTypeId() {
        return productTypeId;
    }

    public void setProductTypeId(int productTypeId) {
        this.productTypeId = productTypeId;
    }

    public int getLeftNum() {
		return leftNum;
	}

	public void setLeftNum(int leftNum) {
		this.leftNum = leftNum;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public int getActivityId() {
		return activityId;
	}

	public void setActivityId(int activityId) {
		this.activityId = activityId;
	}

	public int getProductId() {
		return productId;
	}

	public void setProductId(int productId) {
		this.productId = productId;
	}

	public double getPrice() {
		return price;
	}

	public void setPrice(double price) {
		this.price = price;
	}

	public String getBeginTime() {
		return beginTime;
	}

	public void setBeginTime(String beginTime) {
		this.beginTime = beginTime;
	}

	public String getEndTime() {
		return endTime;
	}

	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}

	public int getNum() {
		return num;
	}

	public void setNum(int num) {
		this.num = num;
	}

	@Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("ActivityProduct [activityId=");
        builder.append(activityId);
        builder.append(", productId=");
        builder.append(productId);
        builder.append(", productTypeId=");
        builder.append(productTypeId);
        builder.append(", price=");
        builder.append(price);
        builder.append(", num=");
        builder.append(num);
        builder.append(", leftNum=");
        builder.append(leftNum);
        builder.append(", beginTime=");
        builder.append(beginTime);
        builder.append(", endTime=");
        builder.append(endTime);
        builder.append(", status=");
        builder.append(status);
        builder.append("]");
        return builder.toString();
    }

}
