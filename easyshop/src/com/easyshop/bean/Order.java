package com.easyshop.bean;

import java.util.Date;
import java.util.List;

import org.nutz.dao.entity.annotation.ColDefine;
import org.nutz.dao.entity.annotation.ColType;
import org.nutz.dao.entity.annotation.Column;
import org.nutz.dao.entity.annotation.Id;
import org.nutz.dao.entity.annotation.Many;
import org.nutz.dao.entity.annotation.Table;

/**
 * 订单表
 * 
 * @author wangzhiming
 * @date 2016.1.13
 */
@Table("orders")
public class Order {
    @Id
    private int orderId; // 订单id
    @Column
    private int userId; // 用户id
    @Column
    private float amount; // 订单金额
    @Column
    private int number; // 订单商品种数
    @Column
    private String paymentId; // 付款单号
    @Column
    private String transportId; // 运输单号
    /**
     * 订单状态<br/>
     * 待付款101， 待发货102， 待收货 103， 待评价 104，退/换货 105, 已完成106，取消100 ，管理员取消107
     */
    @Column
    private int status; // 订单状态
    @Column
    private int addressId; // 收单地址 Address addressId luocz add
    // @Column
    // private String consignee; // 收货人姓名
    // @Column
    // private String cellphone; // 手机号
    /**
     * 下单时间
     */
    @Column
    private String createTime; // 下单时间 yyyy-MM-dd HH:mm:ss

    /**
     * 支付凭证id
     */
    @Column
    private String chargeId; // 支付凭证id

    /**
     * 支付凭证串
     */
    @Column
    @ColDefine(type = ColType.VARCHAR, width = 5000)
    private String chargeJsonStr; // 支付凭证串

    /**
     * 支付凭渠道 upacp_pc:银联 PC 网页支付 wx_pub:微信公众账号支付 wx_pub_qr:微信公众账号扫码支付
     */
    @Column
    private String chargeChannel; // 支付渠道 upacp_pc:银联 PC 网页支付 wx_pub:微信公众账号支付 wx_pub_qr:微信公众账号扫码支付

    /**
     * 支付凭证生成的时间
     */
    @Column
    private Date chargeCreatedTime; // 支付凭证生成的时间

    /**
     * 订单支付完成时间，用 Unix 时间戳表示。
     */
    @Column
    private Date chargePaidTime; // 订单支付完成时间，用 Unix 时间戳表示

    /**
     * 订单失效时间 时间范围在订单创建后的 1 分钟到 15 天，默认为 1 天，创建时间以 Ping++ 服务器时间为准。 微信对该参数的有效值限制为 2 小时内；银联对该参数的有效值限制为 1 小时内
     */
    @Column
    private Date chargeExpireTime; // 订单失效时间
    
    @Column
    private Date confirmTime; // 用户确认收货时间

    private List<OrderProgress> orderProgress; // 订单进度

    private User user;

    @Many(target = ConnectorOP.class, field = "orderId")
    private List<ConnectorOP> connectorOPs;

    private List<Product> products;
 

    public Date getConfirmTime() {
        return confirmTime;
    }

    public void setConfirmTime(Date confirmTime) {
        this.confirmTime = confirmTime;
    }

    public String getChargeId() {
        return chargeId;
    }

    public Date getChargeCreatedTime() {
        return chargeCreatedTime;
    }

    public void setChargeCreatedTime(Date chargeCreatedTime) {
        this.chargeCreatedTime = chargeCreatedTime;
    }

    public Date getChargePaidTime() {
        return chargePaidTime;
    }

    public void setChargePaidTime(Date chargePaidTime) {
        this.chargePaidTime = chargePaidTime;
    }

    public Date getChargeExpireTime() {
        return chargeExpireTime;
    }

    public void setChargeExpireTime(Date chargeExpireTime) {
        this.chargeExpireTime = chargeExpireTime;
    }

    public void setChargeId(String chargeId) {
        this.chargeId = chargeId;
    }

    public String getChargeJsonStr() {
        return chargeJsonStr;
    }

    public void setChargeJsonStr(String chargeJsonStr) {
        this.chargeJsonStr = chargeJsonStr;
    }

    public String getChargeChannel() {
        return chargeChannel;
    }

    public void setChargeChannel(String chargeChannel) {
        this.chargeChannel = chargeChannel;
    }

    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public float getAmount() {
        return amount;
    }

    public void setAmount(float amount) {
        this.amount = amount;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public String getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(String paymentId) {
        this.paymentId = paymentId;
    }

    public String getTransportId() {
        return transportId;
    }

    public void setTransportId(String transportId) {
        this.transportId = transportId;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    // public String getConsignee() {
    // return consignee;
    // }
    //
    // public void setConsignee(String consignee) {
    // this.consignee = consignee;
    // }
    //
    // public String getCellphone() {
    // return cellphone;
    // }
    //
    // public void setCellphone(String cellphone) {
    // this.cellphone = cellphone;
    // }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public List<Product> getProducts() {
        return products;
    }

    public void setProducts(List<Product> products) {
        this.products = products;
    }

    public List<ConnectorOP> getConnectorOPs() {
        return connectorOPs;
    }

    public void setConnectorOPs(List<ConnectorOP> connectorOPs) {
        this.connectorOPs = connectorOPs;
    }

    public List<OrderProgress> getOrderProgress() {
        return orderProgress;
    }

    public void setOrderProgress(List<OrderProgress> orderProgress) {
        this.orderProgress = orderProgress;
    }

    public int getAddressId() {
        return addressId;
    }

    public void setAddressId(int addressId) {
        this.addressId = addressId;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("Order [orderId=");
        builder.append(orderId);
        builder.append(", userId=");
        builder.append(userId);
        builder.append(", amount=");
        builder.append(amount);
        builder.append(", number=");
        builder.append(number);
        builder.append(", paymentId=");
        builder.append(paymentId);
        builder.append(", transportId=");
        builder.append(transportId);
        builder.append(", status=");
        builder.append(status);
        builder.append(", addressId=");
        builder.append(addressId);
        builder.append(", createTime=");
        builder.append(createTime);
        builder.append(", chargeId=");
        builder.append(chargeId);
        builder.append(", chargeJsonStr=");
        builder.append(chargeJsonStr);
        builder.append(", chargeChannel=");
        builder.append(chargeChannel);
        builder.append(", chargeCreatedTime=");
        builder.append(chargeCreatedTime);
        builder.append(", chargePaidTime=");
        builder.append(chargePaidTime);
        builder.append(", chargeExpireTime=");
        builder.append(chargeExpireTime);
        builder.append(", confirmTime=");
        builder.append(confirmTime);
        builder.append(", orderProgress=");
        builder.append(orderProgress);
        builder.append(", user=");
        builder.append(user);
        builder.append(", connectorOPs=");
        builder.append(connectorOPs);
        builder.append(", products=");
        builder.append(products);
        builder.append("]");
        return builder.toString();
    }
}
