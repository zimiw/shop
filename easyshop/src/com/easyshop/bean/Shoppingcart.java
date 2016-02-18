package com.easyshop.bean;

import org.nutz.dao.entity.annotation.Column;
import org.nutz.dao.entity.annotation.Id;
import org.nutz.dao.entity.annotation.Table;

import com.easyshop.vo.ProductVo;

@Table("shoppingcart")

public class Shoppingcart {

	@Id
	private int shoppingcartId;  
	@Column
	private int productId;  //产品id
	@Column
	private int productTypeId; //产品类型id
	@Column
	private int userId;  //用户id
	@Column
	private int number;  //数量
	@Column
	private int isSelected; //是否被加入订单 1-是；0-否
	@Column
	private int selectedType; //预订单来源1-购物车；2-直接下单
	
	private float price;  //添加到订单时的辅助字段 当前商品价格，兼容 活动价格 
	private boolean isLimitActivity; //添加到订单时的辅助字段,是否限时活动 
	
	private Product product;
	
	private ProductVo productVo; //返回页面所需的商品相关字段
	
	private ProductType productType; 
	
	public int getShoppingcartId() {
		return shoppingcartId;
	}
	public void setShoppingcartId( int shoppingcartId) {
		this.shoppingcartId = shoppingcartId;
	}
	public int getProductId() {
		return productId;
	}
	public void setProductId(int productId) {
		this.productId = productId;
	}
	public int getUserId() {
		return userId;
	}
	public void setUserId(int userId) {
		this.userId = userId;
	}
	public int getNumber() {
		return number;
	}
	public void setNumber(int number) {
		this.number = number;
	}
	public Product getProduct() {
		return product;
	}
	public void setProduct(Product product) {
		this.product = product;
	}
	public int getSelectedType() {
		return selectedType;
	}
	public void setSelectedType(int selectedType) {
		this.selectedType = selectedType;
	}
	public int getIsSelected() {
		return isSelected;
	}
	public void setIsSelected(int isSelected) {
		this.isSelected = isSelected;
	}
	public int getProductTypeId() {
		return productTypeId;
	}
	public void setProductTypeId(int productTypeId) {
		this.productTypeId = productTypeId;
	}
	public ProductVo getProductVo() {
		return productVo;
	}
	public void setProductVo(ProductVo productVo) {
		this.productVo = productVo;
	}
	public ProductType getProductType() {
		return productType;
	}
	public void setProductType(ProductType productType) {
		this.productType = productType;
	}
	public float getPrice() {
		return price;
	}
	public void setPrice(float price) {
		this.price = price;
	}
	public boolean isLimitActivity() {
		return isLimitActivity;
	}
	public void setLimitActivity(boolean isLimitActivity) {
		this.isLimitActivity = isLimitActivity;
	}
}


