package com.easyshop.bean;

import java.util.List;

import org.nutz.dao.entity.annotation.Column;
import org.nutz.dao.entity.annotation.Id;
import org.nutz.dao.entity.annotation.Table;

@Table("catalog")
public class Catalog {
	@Id
	private int catalogId;
	@Column
	private String catalogName;
	@Column
	private int pid;
	
	private List<Catalog> subCatalogs;
	
	/**
	 * @return
	 */
	public int getCatalogId() {
		return catalogId;
	}
	public List<Catalog> getSubCatalogs() {
		return subCatalogs;
	}
	public void setSubCatalogs(List<Catalog> subCatalogs) {
		this.subCatalogs = subCatalogs;
	}
	public void setCatalogId(int catalogId) {
		this.catalogId = catalogId;
	}
	public String getCatalogName() {
		return catalogName;
	}
	public void setCatalogName(String catalogName) {
		this.catalogName = catalogName;
	}
	public int getPid() {
		return pid;
	}
	public void setPid(int pid) {
		this.pid = pid;
	}
	
	
}
