package com.easyshop.bean;

import org.nutz.dao.entity.annotation.Column;
import org.nutz.dao.entity.annotation.Id;
import org.nutz.dao.entity.annotation.Table;

@Table("brand_supplier_rel")
public class BrandSupplier {

    @Id
    private int id;

    @Column
    private int brandId;

    @Column
    private int supplierId;

    private Supplier supplier;

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

    public int getSupplierId() {
        return supplierId;
    }

    public void setSupplierId(int supplierId) {
        this.supplierId = supplierId;
    }

    public Supplier getSupplier() {
        return supplier;
    }

    public void setSupplier(Supplier supplier) {
        this.supplier = supplier;
    }

    public Brand getBrand() {
        return brand;
    }

    public void setBrand(Brand brand) {
        this.brand = brand;
    }

    @Override
    public String toString() {
        return "BrandSupplier{" +
                "id=" + id +
                ", brandId=" + brandId +
                ", supplierId=" + supplierId +
                ", supplier=" + supplier +
                ", brand=" + brand +
                '}';
    }
}
