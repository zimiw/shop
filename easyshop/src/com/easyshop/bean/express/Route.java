package com.easyshop.bean.express;

import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

/**
 * @author luocz 元素<响应> RouteResponse/Route
 */
public class Route {

    @XStreamAsAttribute
    private String accept_time;// Date 是 路由节点发生的时间，格式：YYYY-MM-DD
                               // HH24:MM:SS，示例：2012-7-30 09:30:00。

    @XStreamAsAttribute
    private String accept_address;// String(100) 否 路由节点发生的地点

    @XStreamAsAttribute
    private String remark;// String(150) 是 路由节点具体描述

    @XStreamAsAttribute
    private String opcode;// String(20) 是 路由节点操作码

    public String getAccept_time() {
        return accept_time;
    }

    public void setAccept_time(String accept_time) {
        this.accept_time = accept_time;
    }

    public String getAccept_address() {
        return accept_address;
    }

    public void setAccept_address(String accept_address) {
        this.accept_address = accept_address;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getOpcode() {
        return opcode;
    }

    public void setOpcode(String opcode) {
        this.opcode = opcode;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("Route [accept_time=");
        builder.append(accept_time);
        builder.append(", accept_address=");
        builder.append(accept_address);
        builder.append(", remark=");
        builder.append(remark);
        builder.append(", opcode=");
        builder.append(opcode);
        builder.append("]");
        return builder.toString();
    }

}
