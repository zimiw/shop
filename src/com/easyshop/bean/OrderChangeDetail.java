package com.easyshop.bean;

import org.nutz.dao.entity.annotation.Column;
import org.nutz.dao.entity.annotation.Id;
import org.nutz.dao.entity.annotation.Table;

/**
 * 退换换流程信息
 * 
 * @author luocz
 *
 */
@Table("orderchangedetail")
public class OrderChangeDetail {

	@Id
	private long detailId;

	@Column
	private long changeId;// OrderChange id

	@Column
	private String nodeDesc;// 进度说明

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
	private String nodeTime;// 时间

	public long getDetailId() {
		return detailId;
	}

	public void setDetailId(long detailId) {
		this.detailId = detailId;
	}

	public long getChangeId() {
		return changeId;
	}

	public void setChangeId(long changeId) {
		this.changeId = changeId;
	}

	public String getNodeDesc() {
		return nodeDesc;
	}

	public void setNodeDesc(String nodeDesc) {
		this.nodeDesc = nodeDesc;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public String getNodeTime() {
		return nodeTime;
	}

	public void setNodeTime(String nodeTime) {
		this.nodeTime = nodeTime;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("OrderChangeDetail [detailId=");
		builder.append(detailId);
		builder.append(", changeId=");
		builder.append(changeId);
		builder.append(", nodeDesc=");
		builder.append(nodeDesc);
		builder.append(", status=");
		builder.append(status);
		builder.append(", nodeTime=");
		builder.append(nodeTime);
		builder.append("]");
		return builder.toString();
	}

}
