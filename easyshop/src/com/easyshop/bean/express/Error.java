package com.easyshop.bean.express;

import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

/**
 * 顺丰错误信息
 * 
 * @author luocz
 */
public class Error {

    @XStreamAsAttribute
    private String code;

    private String value;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("Error [code=");
        builder.append(code);
        builder.append(", value=");
        builder.append(value);
        builder.append("]");
        return builder.toString();
    }

}
