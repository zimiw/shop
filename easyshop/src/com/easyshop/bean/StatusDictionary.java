package com.easyshop.bean;

import org.nutz.dao.entity.annotation.Column;
import org.nutz.dao.entity.annotation.Id;
import org.nutz.dao.entity.annotation.Table;


/**
 * 状态字典表-归结了业务流程中所有的状态字段
 * @author wangzhiming
 * @date 2016.1.13
 *
 */
@Table("statusdictionary")
public class StatusDictionary {

	@Id
	private int statusdictionaryId;  //id

	/**
	 * 状态编码规则：编码为三位数，百位数区分应用场景，
	 * 后两位表示当前场景下不同的状态，编码的相邻关系能表示状态的前后导关系，
	 * 如 101表示应用场景1下的第一个状态，102则表示应用场景1下的第二个状态
	 */
	@Column
	private int code; //状态编码  
	@Column
	private String name;  //状态名
	@Column
	private String des;  //状态描述
	@Column
	private String stage;  //应用场景描述
	
	public int getStatusdictionaryId() {
		return statusdictionaryId;
	}
	public void setStatusdictionaryId(int statusdictionaryId) {
		this.statusdictionaryId = statusdictionaryId;
	}
	public int getCode() {
		return code;
	}
	public void setCode(int code) {
		this.code = code;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDes() {
		return des;
	}
	public void setDes(String des) {
		this.des = des;
	}
	public String getStage() {
		return stage;
	}
	public void setStage(String stage) {
		this.stage = stage;
	}
}
