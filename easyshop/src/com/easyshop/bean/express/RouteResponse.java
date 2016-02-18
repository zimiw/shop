package com.easyshop.bean.express;

import java.util.List;

import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

/**
 * 路由查询接口 元素<响应>
 * 
 * @author luocz
 *
 */
public class RouteResponse {

    @XStreamAsAttribute
    private String mailno;// String(20) 是 顺丰运单号

    @XStreamAsAttribute
    private String orderid;// String(64) 条件 客户订单号，

    private List<Route> Route;

    public String getMailno() {
        return mailno;
    }

    public void setMailno(String mailno) {
        this.mailno = mailno;
    }

    public String getOrderid() {
        return orderid;
    }

    public void setOrderid(String orderid) {
        this.orderid = orderid;
    }

    public List<Route> getRoute() {
        return Route;
    }

    public void setRoute(List<Route> route) {
        Route = route;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("RouteResponse [mailno=");
        builder.append(mailno);
        builder.append(", orderid=");
        builder.append(orderid);
        builder.append(", Route=");
        builder.append(Route);
        builder.append("]");
        return builder.toString();
    }

}
