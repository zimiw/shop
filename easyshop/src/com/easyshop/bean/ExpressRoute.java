package com.easyshop.bean;

import org.nutz.dao.entity.annotation.Column;
import org.nutz.dao.entity.annotation.Id;
import org.nutz.dao.entity.annotation.Table;

/**
 * 物流节点信息
 * 
 * @author luocz
 *
 */
@Table("expressroute")
public class ExpressRoute {

    @Id
    private long id;

    @Column
    private String orderId; // 订单id

    @Column
    private String acceptTime;// 路由节点发生的时间，

    @Column
    private String acceptAddress;// 路由节点发生的地点
    @Column
    private String remark;// 路由节点具体描述
    @Column
    private String opCode;// 路由节点操作码

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getAcceptTime() {
        return acceptTime;
    }

    public void setAcceptTime(String acceptTime) {
        this.acceptTime = acceptTime;
    }

    public String getAcceptAddress() {
        return acceptAddress;
    }

    public void setAcceptAddress(String acceptAddress) {
        this.acceptAddress = acceptAddress;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getOpCode() {
        return opCode;
    }

    public void setOpCode(String opCode) {
        this.opCode = opCode;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("ExpressRoute [id=");
        builder.append(id);
        builder.append(", orderId=");
        builder.append(orderId);
        builder.append(", acceptTime=");
        builder.append(acceptTime);
        builder.append(", acceptAddress=");
        builder.append(acceptAddress);
        builder.append(", remark=");
        builder.append(remark);
        builder.append(", opCode=");
        builder.append(opCode);
        builder.append("]");
        return builder.toString();
    }

}
