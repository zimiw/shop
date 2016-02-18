package com.easyshop.bean;

import java.util.Date;

import org.nutz.dao.entity.annotation.ColDefine;
import org.nutz.dao.entity.annotation.ColType;
import org.nutz.dao.entity.annotation.Column;
import org.nutz.dao.entity.annotation.Id;
import org.nutz.dao.entity.annotation.Table;

/**
 * 订单退换货表
 * 
 * @author luocz
 *
 */
@Table("orderchange")
public class OrderChange {

	@Id
	private long id; // 退换货订单id 一个主订单可能对应多个退换货订单

	@Column
	private int userId; // 用户id

	@Column
	private int orderId; // 订单id

	@Column
	private int productId;// 商品ID

	@Column
	private int num;// 数量

	@Column
	private float amount;// 金额

	/**
	 * 对应ping++ refund对象的transaction_no 给顾客的页面看的 支付渠道返回的交易流水号，部分渠道返回该字段为空。
	 */
	@Column
	private String refundNo;// 退款单号 给顾客的页面看的 支付渠道返回的交易流水号，部分渠道返回该字段为空。

	/**
	 * 由 Ping++ 生成，27 位长度字符串 退款内部使用,不对外
	 */
	@Column
	private String refundId; // 由 Ping++ 生成，27 位长度字符串 退款内部使用,不对外

	/**
	 * 退款状态（目前支持三种状态: pending: 处理中; succeeded: 成功; failed: 失败）
	 */
	@Column
	private String refundStatus; // 退款状态（目前支持三种状态: pending: 处理中; succeeded: 成功;
									// failed: 失败）

	/**
	 * 失败返回信息
	 */
	@Column
	@ColDefine(type = ColType.VARCHAR, width = 500)
	private String refundFailureMsg; // 失败返回信息

	/**
	 * 退款创建的时间
	 */
	@Column
	private Date refundCreatedTime; // 退款创建的时间

	/**
	 * 退款成功的时间。
	 */
	@Column
	private Date refundSucceedTime; // 退款成功的时间

	/**
	 * 退换货类型<br/>
	 * 1:退货， 2：换货
	 */
	@Column
	private int changeType;

	/**
	 * 进度状态<br/>
	 * 退货状态：301 用户提交，302 审核通过，304收到退货， 305退款进行中， 308退款失败，309退款完成<br/>
	 * 301用户提交，303 驳回<br/>
	 * 306用户取消
	 * 
	 * 换货货流程： 301用户提交，302 审核通过，304收到退货，310商品重新出库， 311等待收货， 312完成<br/>
	 * 301用户提交， 303驳回<br/>
	 * 306用户取消
	 */
	@Column
	private int status;

	@Column
	private String changeNo;// 换货物流单号

	@Column
	private String changeReason;// 退换货原因

	@Column
	private String remark; // 退换货说明

	@Column
	private String image1;// 凭证图片1

	@Column
	private String image2;// 凭证图片1

	public String getRefundFailureMsg() {
		return refundFailureMsg;
	}

	public void setRefundFailureMsg(String refundFailureMsg) {
		this.refundFailureMsg = refundFailureMsg;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public int getOrderId() {
		return orderId;
	}

	public void setOrderId(int orderId) {
		this.orderId = orderId;
	}

	public String getRefundId() {
		return refundId;
	}

	public void setRefundId(String refundId) {
		this.refundId = refundId;
	}

	public String getRefundStatus() {
		return refundStatus;
	}

	public void setRefundStatus(String refundStatus) {
		this.refundStatus = refundStatus;
	}

	public Date getRefundCreatedTime() {
		return refundCreatedTime;
	}

	public void setRefundCreatedTime(Date refundCreatedTime) {
		this.refundCreatedTime = refundCreatedTime;
	}

	public Date getRefundSucceedTime() {
		return refundSucceedTime;
	}

	public void setRefundSucceedTime(Date refundSucceedTime) {
		this.refundSucceedTime = refundSucceedTime;
	}

	public int getProductId() {
		return productId;
	}

	public void setProductId(int productId) {
		this.productId = productId;
	}

	public int getNum() {
		return num;
	}

	public void setNum(int num) {
		this.num = num;
	}

	public float getAmount() {
		return amount;
	}

	public void setAmount(float amount) {
		this.amount = amount;
	}

	public int getChangeType() {
		return changeType;
	}

	public void setChangeType(int changeType) {
		this.changeType = changeType;
	}

	public String getRefundNo() {
		return refundNo;
	}

	public void setRefundNo(String refundNo) {
		this.refundNo = refundNo;
	}

	public String getChangeNo() {
		return changeNo;
	}

	public void setChangeNo(String changeNo) {
		this.changeNo = changeNo;
	}

	public String getChangeReason() {
		return changeReason;
	}

	public void setChangeReason(String changeReason) {
		this.changeReason = changeReason;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public String getImage1() {
		return image1;
	}

	public void setImage1(String image1) {
		this.image1 = image1;
	}

	public String getImage2() {
		return image2;
	}

	public void setImage2(String image2) {
		this.image2 = image2;
	}

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("OrderChange [id=");
		builder.append(id);
		builder.append(", userId=");
		builder.append(userId);
		builder.append(", orderId=");
		builder.append(orderId);
		builder.append(", productId=");
		builder.append(productId);
		builder.append(", num=");
		builder.append(num);
		builder.append(", amount=");
		builder.append(amount);
		builder.append(", refundNo=");
		builder.append(refundNo);
		builder.append(", refundId=");
		builder.append(refundId);
		builder.append(", refundStatus=");
		builder.append(refundStatus);
		builder.append(", refundFailureMsg=");
		builder.append(refundFailureMsg);
		builder.append(", refundCreatedTime=");
		builder.append(refundCreatedTime);
		builder.append(", refundSucceedTime=");
		builder.append(refundSucceedTime);
		builder.append(", changeType=");
		builder.append(changeType);
		builder.append(", status=");
		builder.append(status);
		builder.append(", changeNo=");
		builder.append(changeNo);
		builder.append(", changeReason=");
		builder.append(changeReason);
		builder.append(", remark=");
		builder.append(remark);
		builder.append(", image1=");
		builder.append(image1);
		builder.append(", image2=");
		builder.append(image2);
		builder.append("]");
		return builder.toString();
	}

}
