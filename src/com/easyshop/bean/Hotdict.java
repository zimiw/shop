package com.easyshop.bean;

import org.nutz.dao.entity.annotation.*;

@Table("hotdict")
public class Hotdict {

    /**
     * 不区分大小写
     */
    @Name
    private String name;

    /**
     * true表示在用 false表示未用
     */
    @Column
    private boolean status;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "Hotdict{" +
                "name='" + name + '\'' +
                ", status=" + status +
                '}';
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public Hotdict(String name, boolean status) {
        this.name = name;
        this.status = status;
    }

    public Hotdict(String name) {
        this.name = name;
    }

    public Hotdict() {
    }

}
