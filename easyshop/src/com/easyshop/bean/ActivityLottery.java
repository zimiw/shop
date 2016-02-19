package com.easyshop.bean;

import java.util.Date;

import org.nutz.dao.entity.annotation.Column;
import org.nutz.dao.entity.annotation.Id;
import org.nutz.dao.entity.annotation.Table;

/**
 * 活动---抽奖设定
 * 
 * @author luocz
 */
@Table("activitylottery")
public class ActivityLottery {

	@Id
	private int id;//

	@Column
	private int orderId; // 订单id

	@Column
	private int isLottery;// 是否中奖 1是，0否

	@Column
	private Date insertTime;// 设置参与时间

	@Column
	private Date lotteryTime;// 中奖时间

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getOrderId() {
		return orderId;
	}

	public void setOrderId(int orderId) {
		this.orderId = orderId;
	}

	public int getIsLottery() {
		return isLottery;
	}

	public void setIsLottery(int isLottery) {
		this.isLottery = isLottery;
	}

	public Date getInsertTime() {
		return insertTime;
	}

	public void setInsertTime(Date insertTime) {
		this.insertTime = insertTime;
	}

	public Date getLotteryTime() {
		return lotteryTime;
	}

	public void setLotteryTime(Date lotteryTime) {
		this.lotteryTime = lotteryTime;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("ActivityLottery [id=");
		builder.append(id);
		builder.append(", orderId=");
		builder.append(orderId);
		builder.append(", isLottery=");
		builder.append(isLottery);
		builder.append(", insertTime=");
		builder.append(insertTime);
		builder.append(", lotteryTime=");
		builder.append(lotteryTime);
		builder.append("]");
		return builder.toString();
	}

}
