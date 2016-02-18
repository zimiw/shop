package com.easyshop.bean;

import org.nutz.dao.entity.annotation.Column;
import org.nutz.dao.entity.annotation.Id;
import org.nutz.dao.entity.annotation.Table;

/**
 * 活动 ---首页轮播图片
 * 
 * @author luocz
 */
@Table("activitybranner")
public class ActivityBranner {

    @Id
    private int id;

    @Column
    private String imgSource;// 120 图片路径
    @Column
    private String url;// url
    @Column
    private int seq;// 排序
    @Column
    private int status;// 状态 1有限， 0无效

    public int getSeq() {
        return seq;
    }

    public void setSeq(int seq) {
        this.seq = seq;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("ActivityBranner [id=");
        builder.append(id);
        builder.append(", imgSource=");
        builder.append(imgSource);
        builder.append(", url=");
        builder.append(url);
        builder.append(", seq=");
        builder.append(seq);
        builder.append(", status=");
        builder.append(status);
        builder.append("]");
        return builder.toString();
    }

}
