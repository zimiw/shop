package com.easyshop.bean.express;

/**
 * 顺丰接口调用返回内容封装
 * 
 * @author luocz
 */
public class ExperssResult {

    private String head;// OK 成功， ERR 失败

    private String msg;// 如果失败， 失败原因

    private String mailno;// 顺丰运单号

    public String getHead() {
        return head;
    }

    public void setHead(String head) {
        this.head = head;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getMailno() {
        return mailno;
    }

    public void setMailno(String mailno) {
        this.mailno = mailno;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("ExperssResult [head=");
        builder.append(head);
        builder.append(", msg=");
        builder.append(msg);
        builder.append(", mailno=");
        builder.append(mailno);
        builder.append("]");
        return builder.toString();
    }

}
