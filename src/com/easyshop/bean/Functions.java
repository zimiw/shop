package com.easyshop.bean;

import java.util.List;

import org.nutz.dao.entity.annotation.Column;
import org.nutz.dao.entity.annotation.Id;
import org.nutz.dao.entity.annotation.Table;

/**
 * @author 15060956
 *
 */
@Table("functions")
public class Functions {
	@Id
	private int id;
	@Column
	private String name;
	@Column
	private String url;
	@Column
	private int PID;
	@Column
	private int orderId;
	
	private List<Role> roles;
	
	
	public List<Role> getRoles() {
		return roles;
	}
	public void setRoles(List<Role> roles) {
		this.roles = roles;
	}
	public int getOrderId() {
		return orderId;
	}
	public void setOrderId(int orderId) {
		this.orderId = orderId;
	}
	private List<Functions> subFunctions;
	
	public List<Functions> getSubFunctions() {
		return subFunctions;
	}
	public void setSubFunctions(List<Functions> subFunctions) {
		this.subFunctions = subFunctions;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public int getPID() {
		return PID;
	}
	public void setPID(int pID) {
		PID = pID;
	}
	
}
