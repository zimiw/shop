package com.easyshop.bean;

import org.nutz.dao.entity.annotation.*;

@Table("supplier")
public class Supplier {

	@Id
	private int id;

    /**
     * 供应商名称
     */
	@Column
    @ColDefine(type = ColType.VARCHAR, width = 200)
	private String name;

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

    @Override
    public String toString() {
        return "Supplier{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }
}
