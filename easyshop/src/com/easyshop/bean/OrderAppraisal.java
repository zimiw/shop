package com.easyshop.bean;

import org.nutz.dao.entity.annotation.Column;
import org.nutz.dao.entity.annotation.Id;
import org.nutz.dao.entity.annotation.Table;

/** 
 * 订单评价
 * @author  luocz
 */
@Table("orderappraisal")
public class OrderAppraisal {
    @Id
    private int appId ;//评价id
    
    @Column
    private int orderId; // 订单id
    
    @Column
    private int appType;//评价类型
    
    @Column
    private String appContent;//评价内容
    
    @Column
    private String appTime;//评价时间

    public int getAppId() {
        return appId;
    }

    public void setAppId(int appId) {
        this.appId = appId;
    }

    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public int getAppType() {
        return appType;
    }

    public void setAppType(int appType) {
        this.appType = appType;
    }

    public String getAppContent() {
        return appContent;
    }

    public void setAppContent(String appContent) {
        this.appContent = appContent;
    }

    public String getAppTime() {
        return appTime;
    }

    public void setAppTime(String appTime) {
        this.appTime = appTime;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("OrderAppraisal [appId=");
        builder.append(appId);
        builder.append(", orderId=");
        builder.append(orderId);
        builder.append(", appType=");
        builder.append(appType);
        builder.append(", appContent=");
        builder.append(appContent);
        builder.append(", appTime=");
        builder.append(appTime);
        builder.append("]");
        return builder.toString();
    }
    
    
    
    
}
