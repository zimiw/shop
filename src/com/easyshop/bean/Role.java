package com.easyshop.bean;

import java.util.List;

import org.nutz.dao.entity.annotation.Column;
import org.nutz.dao.entity.annotation.Id;
import org.nutz.dao.entity.annotation.Many;
import org.nutz.dao.entity.annotation.Table;

@Table("role")
public class Role {
	@Id
	private int roleId;
	@Column
	private String name;
	@Column
	private String descp;

    private List<User> Users;
    
    private List<Functions> functions;
	
	public List<Functions> getFunctions() {
		return functions;
	}
	public void setFunctions(List<Functions> functions) {
		this.functions = functions;
	}
	public List<User> getUsers() {
		return Users;
	}
	public void setUsers(List<User> users) {
		Users = users;
	}
	
	public int getRoleId() {
		return roleId;
	}
	public void setRoleId(int roleId) {
		this.roleId = roleId;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDescp() {
		return descp;
	}
	public void setDescp(String descp) {
		this.descp = descp;
	}
	
	
	
}
