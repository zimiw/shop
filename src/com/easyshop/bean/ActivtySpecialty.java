package com.easyshop.bean;

import org.nutz.dao.entity.annotation.Column;
import org.nutz.dao.entity.annotation.Id;
import org.nutz.dao.entity.annotation.Table;

/**
 * 专场活动
 * 
 * @author luocz
 *
 */
@Table("activtyspecialty")
public class ActivtySpecialty {

    @Id
    private int speId;

    @Column
    private String beginTime;// 开始时间
    @Column
    private String endTime;// 结束时间
    @Column
    private String imgSource;// 图片
    @Column
    private String url;// url

    @Column
    private int status;// 状态 1有限， 0无效

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getSpeId() {
        return speId;
    }

    public void setSpeId(int speId) {
        this.speId = speId;
    }

    public String getBeginTime() {
        return beginTime;
    }

    public void setBeginTime(String beginTime) {
        this.beginTime = beginTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
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

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("ActivtySpecialty [speId=");
        builder.append(speId);
        builder.append(", beginTime=");
        builder.append(beginTime);
        builder.append(", endTime=");
        builder.append(endTime);
        builder.append(", imgSource=");
        builder.append(imgSource);
        builder.append(", url=");
        builder.append(url);
        builder.append(", status=");
        builder.append(status);
        builder.append("]");
        return builder.toString();
    }

}
