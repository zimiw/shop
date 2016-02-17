/*
* Copyright (C), 2002-2016, 苏宁易购电子商务有限公司
* FileName: MessageRtnVo
* Author:   15040635 wzs
* Date:     2016/1/6 15:55
* Description: //模块目的、功能描述      
* History: //修改记录
* <author> <time> <version> <desc>
* 修改人姓名             修改时间            版本号                  描述
*/
package com.easyshop.bean;

import com.easyshop.utils.RandomUtils;
import org.nutz.dao.entity.annotation.Column;
import org.nutz.dao.entity.annotation.Id;
import org.nutz.dao.entity.annotation.Table;

import java.util.Date;

/**
 * 〈〉<br>
 *
 *     具体请参考 接口说明
 *
 *
 *
 *
 * @author 15040635 wzs
 */
@Table("message_rtn_info")
public class MessageRtnInfo {


    @Id
    private long id;

    @Column
    private String phone;


    @Column
    private Date sendTime;

    /**
     * 6位随机数字
     */
    @Column
    private String randomNumStr;

    /**
     * 发送结果 0成功 其他失败
     *
     代码	说明
     0	提交成功
     1	含有敏感词汇
     2	余额不足
     3	没有号码
     4	包含sql语句
     10	账号不存在
     11	账号注销
     12	账号停用
     13	IP鉴权失败
     14	格式错误
     -1	系统异常
     *
     */
    private String code;

    /**
     * 发送批次,要写到session里面去.
     */
    @Column
    private String sendId;

    /**
     * 完整信息
     * 成功:0, 20130821110353234137876543,0,500,0,提交成功
     * 失败:1,含有敏感词汇
     *
     */
    private String fullRtnInfo;

    public MessageRtnInfo() {
        this.randomNumStr = RandomUtils.getRandomNumberStr();
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getRandomNumStr() {
        return randomNumStr;
    }

    public void setRandomNumStr(String randomNumStr) {
        this.randomNumStr = randomNumStr;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getSendId() {
        return sendId;
    }

    public void setSendId(String sendId) {
        this.sendId = sendId;
    }

    public String getFullRtnInfo() {
        return fullRtnInfo;
    }

    public void setFullRtnInfo(String fullRtnInfo) {
        this.fullRtnInfo = fullRtnInfo;
    }

    @Override
    public String toString() {
        return "MessageRtnInfo{" +
                "id=" + id +
                ", phone='" + phone + '\'' +
                ", sendTime=" + sendTime +
                ", randomNumStr='" + randomNumStr + '\'' +
                ", code='" + code + '\'' +
                ", sendId='" + sendId + '\'' +
                ", fullRtnInfo='" + fullRtnInfo + '\'' +
                '}';
    }

    public Date getSendTime() {
        return sendTime;
    }

    public void setSendTime(Date sendTime) {
        this.sendTime = sendTime;
    }

}