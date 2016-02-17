package com.easyshop.bean.express;

import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

/**
 * 订单结果查询接口
 * 
 * @author luocz
 */
public class OrderSearch {

    @XStreamAsAttribute
    private String orderid;// String(64) 是 客户订单号

    public String getOrderid() {
        return orderid;
    }

    public void setOrderid(String orderid) {
        this.orderid = orderid;
    }

}
