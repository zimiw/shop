package com.easyshop.bean.express;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

/**
 * 顺丰接口响应
 * 
 * @author luocz
 */
@XStreamAlias("OrderResponse")
public class OrderResponse {

    @XStreamAsAttribute
    private String orderid;// String(64) 是 客户订单号

    @XStreamAsAttribute
    private String mailno;// String(4000) 否 顺丰运单号

    @XStreamAsAttribute
    private String return_tracking_no;// String(30) 否 顺丰签回单服务运单号

    @XStreamAsAttribute
    private String origincode;// String(10) 否 原寄地区域代码

    @XStreamAsAttribute
    private String destcode;// String(10) 否 目的地区域代码，

    @XStreamAsAttribute
    private int filter_result;// Number(2) 否 筛单结果：  1：人工确认  2：可收派  3：不可以收派

    @XStreamAsAttribute
    private String remark;// String(100) 条件 如果 filter_result=3 时为必填，不可以 收派的原因代码
                          // 1：收方超范围 2：派方超范围 3-：其它原因

    public String getOrderid() {
        return orderid;
    }

    public void setOrderid(String orderid) {
        this.orderid = orderid;
    }

    public String getMailno() {
        return mailno;
    }

    public void setMailno(String mailno) {
        this.mailno = mailno;
    }

    public String getReturn_tracking_no() {
        return return_tracking_no;
    }

    public void setReturn_tracking_no(String return_tracking_no) {
        this.return_tracking_no = return_tracking_no;
    }

    public String getOrigincode() {
        return origincode;
    }

    public void setOrigincode(String origincode) {
        this.origincode = origincode;
    }

    public String getDestcode() {
        return destcode;
    }

    public void setDestcode(String destcode) {
        this.destcode = destcode;
    }

    public int getFilter_result() {
        return filter_result;
    }

    public void setFilter_result(int filter_result) {
        this.filter_result = filter_result;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("OrderResponse [orderid=");
        builder.append(orderid);
        builder.append(", mailno=");
        builder.append(mailno);
        builder.append(", return_tracking_no=");
        builder.append(return_tracking_no);
        builder.append(", origincode=");
        builder.append(origincode);
        builder.append(", destcode=");
        builder.append(destcode);
        builder.append(", filter_result=");
        builder.append(filter_result);
        builder.append(", remark=");
        builder.append(remark);
        builder.append("]");
        return builder.toString();
    }

}
