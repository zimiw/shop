package com.easyshop.bean;

import org.nutz.dao.entity.annotation.Column;
import org.nutz.dao.entity.annotation.Id;
import org.nutz.dao.entity.annotation.Table;

@Table("adviser")
public class Adviser {
	@Id
	private int adviserId;
	@Column
	private String imgSource;
	@Column
	private String url;
	@Column
	private String type;
	public int getAdviserId() {
		return adviserId;
	}
	public void setAdviserId(int adviserId) {
		this.adviserId = adviserId;
	}
	public String getImgSource() {
		return imgSource;
	}
	public void setImgSource(String imgSource) {
		this.imgSource = imgSource;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	
}
