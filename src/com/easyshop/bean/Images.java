package com.easyshop.bean;

import org.nutz.dao.entity.annotation.Column;
import org.nutz.dao.entity.annotation.Id;
import org.nutz.dao.entity.annotation.Table;

@Table("images")
public class Images {
	@Id
	private int imgID;
	@Column
	private String imgsource;
	@Column
	private boolean isTopimg;//商品主图
	@Column
	private String descp;
	@Column
	private String title;
	@Column
	private int productId;
	@Column
	private boolean isSmallView;//缩略图
	@Column
	private boolean isSizePic;//颜色示例图
	@Column
	private boolean isDetailPic;//商品详情页图
	@Column
	private int orderId;
	
	
	
	public int getOrderId() {
		return orderId;
	}
	public void setOrderId(int orderId) {
		this.orderId = orderId;
	}
	public boolean isSmallView() {
		return isSmallView;
	}
	public void setSmallView(boolean isSmallView) {
		this.isSmallView = isSmallView;
	}
	public boolean isSizePic() {
		return isSizePic;
	}
	public void setSizePic(boolean isSizePic) {
		this.isSizePic = isSizePic;
	}
	public boolean isDetailPic() {
		return isDetailPic;
	}
	public void setDetailPic(boolean isDetailPic) {
		this.isDetailPic = isDetailPic;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public int getProductId() {
		return productId;
	}
	public void setProductId(int productId) {
		this.productId = productId;
	}
	public String getDescp() {
		return descp;
	}
	public void setDescp(String descp) {
		this.descp = descp;
	}
	public boolean isTopimg() {
		return isTopimg;
	}
	public void setTopimg(boolean isTopimg) {
		this.isTopimg = isTopimg;
	}
	public int getImgID() {
		return imgID;
	}
	public void setImgID(int imgID) {
		this.imgID = imgID;
	}
	public String getImgsource() {
		return imgsource;
	}
	public void setImgsource(String result) {
		this.imgsource = result;
	}
	
}
