package com.easyshop.bean;

import org.nutz.dao.entity.annotation.Column;
import org.nutz.dao.entity.annotation.Id;
import org.nutz.dao.entity.annotation.Table;

/**
 * 活动--抽奖规则
 * 
 * @author luocz
 */
@Table("activitylotteryconf")
public class ActivityLotteryConf {

	@Id
	private int id;

	@Column
	private String lotteryTime;// 抽奖时间
	@Column
	private double rate;// 中奖率

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getLotteryTime() {
		return lotteryTime;
	}

	public void setLotteryTime(String lotteryTime) {
		this.lotteryTime = lotteryTime;
	}

	public double getRate() {
		return rate;
	}

	public void setRate(double rate) {
		this.rate = rate;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("ActivityLotteryConf [id=");
		builder.append(id);
		builder.append(", lotteryTime=");
		builder.append(lotteryTime);
		builder.append(", rate=");
		builder.append(rate);
		builder.append("]");
		return builder.toString();
	}

}
