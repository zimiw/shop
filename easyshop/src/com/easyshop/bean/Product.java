package com.easyshop.bean;

import java.util.Date;
import java.util.List;

import org.nutz.dao.entity.annotation.Column;
import org.nutz.dao.entity.annotation.Id;
import org.nutz.dao.entity.annotation.Table;

@Table("product")
public class Product {

    /**
     * 要删掉 原价 展示在商品页面的原价
     */
    @Column
    private float originPrice;// 要删掉 原价 展示在商品页面的原价

    /**
     * 要删掉 现价 展示在商品页面的现价 也是当前时间的成交价格.
     */
    @Column
    private float currentPrice;// 要删掉 现价 展示在商品页面的现价 也是当前时间的成交价格

    @Id
    private int productId;// 商品ID
    @Column
    private String name;// 商品名

    /**
     * 这个商品底下所有规格的最低价,用以面向顾客搜索排序价格用 新增,编辑商品都要修改 还有就是在定时器参加活动开始的时候,以及参加活动结束的时候
     */
    @Column
    private float minPrice;// 这个商品底下所有规格的最低价,用以面向顾客搜索排序价格用

    /**
     * 参加活动类型 0无活动 1限时活动 2热卖一 3热卖二 只有手动删除了限时活动表里面的一条数据,才删除限时活动的状态
     */
    @Column
    private int activityType;// 参加活动类型 0无活动 1限时活动 2热卖一 3热卖二

    /**
     * 限时活动状态 0未开始，1已开始、2已售罄 3已结束 如果定时器触发 结束了这次限时活动,那么只更新状态是 3已结束 ,而不修改activityType
     */
    @Column
    private int limitActivityStatus;// 限时活动状态 0未开始，1已开始、2已售罄 3已结束

    // /**
    // * 参加限时活动的商品数量
    // */
    // @Column
    // private int limitActivityTotalCount;//参加限时活动的所有规格的总数量
    //
    // /**
    // * 参加限时活动的商品 剩余数量
    // */
    // @Column
    // private int limitActivityTotalLeftCount;//参加限时活动的商品 剩余数量
    //
    // /**
    // * 限时活动开始时间
    // */
    // @Column
    // private Date limitActivityStartTime;//限时活动开始时间
    //
    // /**
    // * 限时活动结束时间
    // */
    // @Column
    // private Date limitActivityEndTime;//限时活动结束时间
    //
    // /**
    // * 限时活动修改时间 用来前台的排序用
    // */
    // @Column
    // private Date limitActivityUpdateTime;//限时活动修改时间 用来前台的排序用

    /**
     * 重量
     */
    @Column
    private float weight;// 重量

    @Column
    private Date inputDate;// 录入日期
    @Column
    private int viewCount;// 浏览次数
    /**
     * 总销量 需要维护的
     */
    @Column
    private int sellCount;// 总销量 需要维护的
    /**
     * 总量 暂时用不到
     */
    private int totalCount;// 总量 暂时用不到
    /**
     * 剩余总量 只是临时用的
     */
    private int totalLeftCount;// 剩余总量 只是临时用的
    @Column
    private boolean status;// 状态 上架/下架
    @Column
    private String detailTitle;// 废弃
    @Column
    private String descp;// 商品描述

    /**
     * 供应商ID
     */
    @Column
    private int supplierId;// 供应商ID

    /**
     * 第一级分类ID
     */
    @Column
    private int catalogId1;// 第一级分类ID

    /**
     * 第二级分类ID
     */
    @Column
    private int catalogId2;// 第二级分类ID

    /**
     * 第三级分类ID
     */
    @Column
    private int catalogId3;// 第三级分类ID

    @Column
    private int catalogId;// 分类ID
    @Column
    private boolean isHotKey;// 是否是热词
    @Column
    private String provider;// 供货商
    @Column
    private String providerAddress;// 供货商地址
    @Column
    private boolean isNew;// 是否新品

    /**
     * 品牌id
     */
    @Column
    private int brandId;// 品牌id

    private String brandName;
    private String catalogName1;
    private String catalogName2;
    private String catalogName3;
    private String SupplierName;
    @Column
    private String wlStatus;
    @Column
    private String payStatus;
    @Column
    private String orderStatus;
    @Column
    private String unit;
    @Column
    private float specialPrice;
    @Column
    private int activityId;
    @Column
    private String country;
    @Column
    private String external;

    private List<ProductType> productTypes;
    private Brand brand;

    private List<Comment> comments;

    private Catalogs catalog;
    private List<Images> imgs;
    private List<Images> topImgs;
    private List<Images> detailImgs;

    private List<Order> orders;

    private Images topImg;

    private int number; // 对应ConnectorOP中的number 订单中当前商品的数量
    private String color; // 对应ConnectorOP中的color 订单中当前商品的颜色
    private String size; // 对应ConnectorOP中的size 订单中当前商品的尺码
    private float price; // 对应ConnectorOP中的price 订单中当前商品的价格

    public List<Images> getTopImgs() {
        return topImgs;
    }

    public void setTopImgs(List<Images> topImgs) {
        this.topImgs = topImgs;
    }

    public int getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }

    public String getBrandName() {
        return brandName;
    }

    public void setBrandName(String brandName) {
        this.brandName = brandName;
    }

    public String getCatalogName1() {
        return catalogName1;
    }

    public void setCatalogName1(String catalogName1) {
        this.catalogName1 = catalogName1;
    }

    public String getCatalogName2() {
        return catalogName2;
    }

    public void setCatalogName2(String catalogName2) {
        this.catalogName2 = catalogName2;
    }

    public String getCatalogName3() {
        return catalogName3;
    }

    public void setCatalogName3(String catalogName3) {
        this.catalogName3 = catalogName3;
    }

    public String getSupplierName() {
        return SupplierName;
    }

    public void setSupplierName(String supplierName) {
        SupplierName = supplierName;
    }

    public int getTotalLeftCount() {
        return totalLeftCount;
    }

    public void setTotalLeftCount(int totalLeftCount) {
        this.totalLeftCount = totalLeftCount;
    }

    public List<Images> getDetailImgs() {
        return detailImgs;
    }

    public void setDetailImgs(List<Images> detailImgs) {
        this.detailImgs = detailImgs;
    }

    public void setIsHotKey(boolean isHotKey) {
        this.isHotKey = isHotKey;
    }

    public void setIsNew(boolean isNew) {
        this.isNew = isNew;
    }

    public float getWeight() {
        return weight;
    }

    public int getViewCount() {
        return viewCount;
    }

    public void setViewCount(int viewCount) {
        this.viewCount = viewCount;
    }

    public float getOriginPrice() {
        return originPrice;
    }

    public void setOriginPrice(float originPrice) {
        this.originPrice = originPrice;
    }

    public float getCurrentPrice() {
        return currentPrice;
    }

    public void setCurrentPrice(float currentPrice) {
        this.currentPrice = currentPrice;
    }

    public int getSupplierId() {
        return supplierId;
    }

    public void setSupplierId(int supplierId) {
        this.supplierId = supplierId;
    }

    public int getCatalogId1() {
        return catalogId1;
    }

    public void setCatalogId1(int catalogId1) {
        this.catalogId1 = catalogId1;
    }

    public int getCatalogId2() {
        return catalogId2;
    }

    public float getMinPrice() {
        return minPrice;
    }

    public void setMinPrice(float minPrice) {
        this.minPrice = minPrice;
    }

    public void setCatalogId2(int catalogId2) {
        this.catalogId2 = catalogId2;
    }

    public int getCatalogId3() {
        return catalogId3;
    }

    public void setCatalogId3(int catalogId3) {
        this.catalogId3 = catalogId3;
    }

    public void setWeight(float weight) {
        this.weight = weight;
    }

    public int getActivityType() {
        return activityType;
    }

    public void setActivityType(int activityType) {
        this.activityType = activityType;
    }

    public boolean isStatus() {
        return status;
    }

    public String getExternal() {
        return external;
    }

    public void setExternal(String external) {
        this.external = external;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public int getActivityId() {
        return activityId;
    }

    public void setActivityId(int activityId) {
        this.activityId = activityId;
    }

    public float getSpecialPrice() {
        return specialPrice;
    }

    public void setSpecialPrice(float specialPrice) {
        this.specialPrice = specialPrice;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public List<ProductType> getProductTypes() {
        return productTypes;
    }

    public void setProductTypes(List<ProductType> productTypes) {
        this.productTypes = productTypes;
    }

    public String getWlStatus() {
        return wlStatus;
    }

    public void setWlStatus(String wlStatus) {
        this.wlStatus = wlStatus;
    }

    public String getPayStatus() {
        return payStatus;
    }

    public void setPayStatus(String payStatus) {
        this.payStatus = payStatus;
    }

    public String getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
    }

    public Brand getBrand() {
        return brand;
    }

    public void setBrand(Brand brand) {
        this.brand = brand;
    }

    public int getBrandId() {
        return brandId;
    }

    public void setBrandId(int brandId) {
        this.brandId = brandId;
    }

    public boolean isNew() {
        return isNew;
    }

    public void setNew(boolean isNew) {
        this.isNew = isNew;
    }

    public String getProvider() {
        return provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }

    public String getProviderAddress() {
        return providerAddress;
    }

    public void setProviderAddress(String providerAddress) {
        this.providerAddress = providerAddress;
    }

    public boolean isHotKey() {
        return isHotKey;
    }

    public void setHotKey(boolean isHotKey) {
        this.isHotKey = isHotKey;
    }

    public List<Order> getOrders() {
        return orders;
    }

    public void setOrders(List<Order> orders) {
        this.orders = orders;
    }

    public Images getTopImg() {
        return topImg;
    }

    public void setTopImg(Images topImg) {
        this.topImg = topImg;
    }

    public List<Comment> getComments() {
        return comments;
    }

    public void setComments(List<Comment> comments) {
        this.comments = comments;
    }

    public Catalogs getCatalog() {
        return catalog;
    }

    public void setCatalog(Catalogs catalog) {
        this.catalog = catalog;
    }

    // private List<Order> orders;

    public List<Images> getImgs() {
        return imgs;
    }

    public void setImgs(List<Images> imgs) {
        this.imgs = imgs;
    }

    public String getDetailTitle() {
        return detailTitle;
    }

    public void setDetailTitle(String detailTitle) {
        this.detailTitle = detailTitle;
    }

    public String getDescp() {
        return descp;
    }

    public void setDescp(String descp) {
        this.descp = descp;
    }

    public int getCatalogId() {
        return catalogId;
    }

    public void setCatalogId(int catalogId) {
        this.catalogId = catalogId;
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getSellCount() {
        return sellCount;
    }

    public void setSellCount(int sellCount) {
        this.sellCount = sellCount;
    }

    public boolean getStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    public Date getInputDate() {
        return inputDate;
    }

    public void setInputDate(Date inputDate) {
        this.inputDate = inputDate;
    }

    public int getLimitActivityStatus() {
        return limitActivityStatus;
    }

    public void setLimitActivityStatus(int limitActivityStatus) {
        this.limitActivityStatus = limitActivityStatus;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("Product [originPrice=");
        builder.append(originPrice);
        builder.append(", currentPrice=");
        builder.append(currentPrice);
        builder.append(", productId=");
        builder.append(productId);
        builder.append(", name=");
        builder.append(name);
        builder.append(", minPrice=");
        builder.append(minPrice);
        builder.append(", activityType=");
        builder.append(activityType);
        builder.append(", limitActivityStatus=");
        builder.append(limitActivityStatus);
        builder.append(", weight=");
        builder.append(weight);
        builder.append(", inputDate=");
        builder.append(inputDate);
        builder.append(", viewCount=");
        builder.append(viewCount);
        builder.append(", sellCount=");
        builder.append(sellCount);
        builder.append(", totalCount=");
        builder.append(totalCount);
        builder.append(", totalLeftCount=");
        builder.append(totalLeftCount);
        builder.append(", status=");
        builder.append(status);
        builder.append(", detailTitle=");
        builder.append(detailTitle);
        builder.append(", descp=");
        builder.append(descp);
        builder.append(", supplierId=");
        builder.append(supplierId);
        builder.append(", catalogId1=");
        builder.append(catalogId1);
        builder.append(", catalogId2=");
        builder.append(catalogId2);
        builder.append(", catalogId3=");
        builder.append(catalogId3);
        builder.append(", catalogId=");
        builder.append(catalogId);
        builder.append(", isHotKey=");
        builder.append(isHotKey);
        builder.append(", provider=");
        builder.append(provider);
        builder.append(", providerAddress=");
        builder.append(providerAddress);
        builder.append(", isNew=");
        builder.append(isNew);
        builder.append(", brandId=");
        builder.append(brandId);
        builder.append(", brandName=");
        builder.append(brandName);
        builder.append(", catalogName1=");
        builder.append(catalogName1);
        builder.append(", catalogName2=");
        builder.append(catalogName2);
        builder.append(", catalogName3=");
        builder.append(catalogName3);
        builder.append(", SupplierName=");
        builder.append(SupplierName);
        builder.append(", wlStatus=");
        builder.append(wlStatus);
        builder.append(", payStatus=");
        builder.append(payStatus);
        builder.append(", orderStatus=");
        builder.append(orderStatus);
        builder.append(", unit=");
        builder.append(unit);
        builder.append(", specialPrice=");
        builder.append(specialPrice);
        builder.append(", activityId=");
        builder.append(activityId);
        builder.append(", country=");
        builder.append(country);
        builder.append(", external=");
        builder.append(external);
        builder.append(", productTypes=");
        builder.append(productTypes);
        builder.append(", brand=");
        builder.append(brand);
        builder.append(", comments=");
        builder.append(comments);
        builder.append(", catalog=");
        builder.append(catalog);
        builder.append(", imgs=");
        builder.append(imgs);
        builder.append(", topImgs=");
        builder.append(topImgs);
        builder.append(", detailImgs=");
        builder.append(detailImgs);
        builder.append(", orders=");
        builder.append(orders);
        builder.append(", topImg=");
        builder.append(topImg);
        builder.append(", number=");
        builder.append(number);
        builder.append(", color=");
        builder.append(color);
        builder.append(", size=");
        builder.append(size);
        builder.append(", price=");
        builder.append(price);
        builder.append("]");
        return builder.toString();
    }
}
