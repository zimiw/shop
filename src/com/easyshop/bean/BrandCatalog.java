package com.easyshop.bean;

import org.nutz.dao.entity.annotation.Column;
import org.nutz.dao.entity.annotation.Id;
import org.nutz.dao.entity.annotation.Table;

@Table("brand_catalog_rel")
public class BrandCatalog {

    @Id
    private int id;

    @Column
    private int brandId;

    @Column
    private int catalogId;

    private Catalogs catalog;

    private Brand brand;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getBrandId() {
        return brandId;
    }

    public void setBrandId(int brandId) {
        this.brandId = brandId;
    }

    public int getCatalogId() {
        return catalogId;
    }

    public void setCatalogId(int catalogId) {
        this.catalogId = catalogId;
    }

    public Catalogs getCatalog() {
        return catalog;
    }

    public void setCatalog(Catalogs catalog) {
        this.catalog = catalog;
    }

    public Brand getBrand() {
        return brand;
    }

    public void setBrand(Brand brand) {
        this.brand = brand;
    }

    @Override
    public String toString() {
        return "BrandCatalog{" +
                "id=" + id +
                ", brandId=" + brandId +
                ", catalogId=" + catalogId +
                ", catalog=" + catalog +
                ", brand=" + brand +
                '}';
    }
}
