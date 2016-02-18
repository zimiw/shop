package com.easyshop.bean.express;

import com.thoughtworks.xstream.annotations.XStreamAlias;

/**
 * @author luocz
 */
@XStreamAlias("Body")
public class Body {

    private ExpressOrder Order;

    private OrderSearch OrderSearch;// 订单查询

    @XStreamAlias("OrderSearchResponse")
    private OrderResponse OrderSearchResponse;// 订单查询响应

    private OrderConfirm OrderConfirm;// 订单确认和取消

    @XStreamAlias("OrderResponse")
    private OrderResponse OrderResponse;// 订单相应

    private RouteRequest RouteRequest;// 路由查询接口

    private RouteResponse RouteResponse;// 路由查询相应

    private WaybillRoute WaybillRoute;// 路由推送接口

    public RouteResponse getRouteResponse() {
        return RouteResponse;
    }

    public void setRouteResponse(RouteResponse routeResponse) {
        RouteResponse = routeResponse;
    }

    public OrderResponse getOrderSearchResponse() {
        return OrderSearchResponse;
    }

    public void setOrderSearchResponse(OrderResponse orderSearchResponse) {
        OrderSearchResponse = orderSearchResponse;
    }

    public WaybillRoute getWaybillRoute() {
        return WaybillRoute;
    }

    public void setWaybillRoute(WaybillRoute waybillRoute) {
        WaybillRoute = waybillRoute;
    }

    public RouteRequest getRouteRequest() {
        return RouteRequest;
    }

    public void setRouteRequest(RouteRequest routeRequest) {
        RouteRequest = routeRequest;
    }

    public OrderResponse getOrderResponse() {
        return OrderResponse;
    }

    public void setOrderResponse(OrderResponse orderResponse) {
        OrderResponse = orderResponse;
    }

    public OrderConfirm getOrderConfirm() {
        return OrderConfirm;
    }

    public void setOrderConfirm(OrderConfirm orderConfirm) {
        OrderConfirm = orderConfirm;
    }

    public OrderSearch getOrderSearch() {
        return OrderSearch;
    }

    public void setOrderSearch(OrderSearch orderSearch) {
        OrderSearch = orderSearch;
    }

    public ExpressOrder getOrder() {
        return Order;
    }

    public void setOrder(ExpressOrder order) {
        Order = order;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("Body [Order=");
        builder.append(Order);
        builder.append(", OrderSearch=");
        builder.append(OrderSearch);
        builder.append(", OrderSearchResponse=");
        builder.append(OrderSearchResponse);
        builder.append(", OrderConfirm=");
        builder.append(OrderConfirm);
        builder.append(", OrderResponse=");
        builder.append(OrderResponse);
        builder.append(", RouteRequest=");
        builder.append(RouteRequest);
        builder.append(", RouteResponse=");
        builder.append(RouteResponse);
        builder.append(", WaybillRoute=");
        builder.append(WaybillRoute);
        builder.append("]");
        return builder.toString();
    }

}
