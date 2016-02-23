package com.easyshop.bean;

import org.nutz.dao.entity.annotation.Column;
import org.nutz.dao.entity.annotation.Id;
import org.nutz.dao.entity.annotation.Table;

import com.easyshop.utils.StringUtils;

@Table("address")
public class Address {

    @Id
    private int addressId;
    @Column
    private int userId;
    @Column
    private String province;// 省 Province code
    @Column
    private String city;// 市 City code
    @Column
    private String district; // 区、县 Area code
    @Column
    private String street;// 详细地址
    @Column
    private String name;// 收货人
    @Column
    private String post;
    @Column
    private String phone;// 电话
    @Column
    private String cellphone;// 手机号码
    @Column
    private boolean isDefault;
    @Column
    private int status;// 是否有效，1有效，0无效， 用户删除地址先判断订单中有没有使用，如何使用就不删除只是修改状态为0
    @Column
    private int isOrder;// 1是，0未使用 支付完成订单中是否已经使用
    
    private String addressStr; //包含省市区的地址字符串

    // 手机号码处理
    public String getCellPhoneNew() {
        String phone = cellphone;
        if (!StringUtils.isEmpty(phone) && phone.length() >= 11) {
            phone = phone.substring(0, 3) + "****" + phone.substring(7, 11);
        }
        return phone;
    }

    public int getAddressId() {
        return addressId;
    }

    public void setAddressId(int addressId) {
        this.addressId = addressId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPost() {
        return post;
    }

    public void setPost(String post) {
        this.post = post;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public boolean isDefault() {
        return isDefault;
    }

    public void setDefault(boolean isDefault) {
        this.isDefault = isDefault;
    }

    public String getCellphone() {
        return cellphone;
    }

    public void setCellphone(String cellphone) {
        this.cellphone = cellphone;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getIsOrder() {
        return isOrder;
    }

    public void setIsOrder(int isOrder) {
        this.isOrder = isOrder;
    }

    public void setAddressStr(String addressStr){
        this.addressStr = addressStr;
    }
    public String getAddressStr(){
        return addressStr;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("Address [addressId=");
        builder.append(addressId);
        builder.append(", userId=");
        builder.append(userId);
        builder.append(", province=");
        builder.append(province);
        builder.append(", city=");
        builder.append(city);
        builder.append(", district=");
        builder.append(district);
        builder.append(", street=");
        builder.append(street);
        builder.append(", name=");
        builder.append(name);
        builder.append(", post=");
        builder.append(post);
        builder.append(", phone=");
        builder.append(phone);
        builder.append(", cellphone=");
        builder.append(cellphone);
        builder.append(", isDefault=");
        builder.append(isDefault);
        builder.append(", status=");
        builder.append(status);
        builder.append(", isOrder=");
        builder.append(isOrder);
        builder.append("]");
        return builder.toString();
    }

}
