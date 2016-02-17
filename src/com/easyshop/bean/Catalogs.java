package com.easyshop.bean;

import java.util.List;

import org.nutz.dao.entity.annotation.Column;
import org.nutz.dao.entity.annotation.Id;
import org.nutz.dao.entity.annotation.Table;

@Table("catalog")
public class Catalogs {
	@Id
	private int catalogId;
	@Column
	private String catalogName;
	@Column
	private int pid;
	
	private Catalogs parent;
	
	
	
	public Catalogs getParent() {
		return parent;
	}
	public void setParent(Catalogs parent) {
		this.parent = parent;
	}
	private List<Catalogs> subCatalogs;
	private List<Product> products;
	private List<Brand> brands;
	
	
	public List<Brand> getBrands() {
		return brands;
	}
	public void setBrands(List<Brand> brands) {
		this.brands = brands;
	}
	public List<Product> getProducts() {
		return products;
	}
	public void setProducts(List<Product> products) {
		this.products = products;
	}
	/**
	 * @return
	 */
	public int getCatalogId() {
		return catalogId;
	}
	public List<Catalogs> getSubCatalogs() {
		return subCatalogs;
	}
	public void setSubCatalogs(List<Catalogs> subCatalogs) {
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
