/*
* Copyright (C), 2002-2016, 苏宁易购电子商务有限公司
* FileName: ResultVo
* Author:   15040635 wzs
* Date:     2016/1/5 21:02
* Description: //模块目的、功能描述      
* History: //修改记录
* <author> <time> <version> <desc>
* 修改人姓名             修改时间            版本号                  描述
*/
package com.easyshop.vo;

/**
 * 〈〉<br>
 *
 * @author 15040635 wzs
 */
public class ResultVo {

    public static String STATUS_SUCCESS="success";
    public static String STATUS_FAIL="fail";

    /**
     * 返回的状态 默认成功
     */
    private String status = STATUS_SUCCESS;

    /**
     * 额外的信息
     */
    private String msg;

    public ResultVo(){}

    public ResultVo(String msg) {
        this.msg = msg;
    }

    public ResultVo(String status, String msg) {
        this.status = status;
        this.msg = msg;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    @Override
    public String toString() {
        return "ResultVo{" +
                "status='" + status + '\'' +
                ", msg='" + msg + '\'' +
                '}';
    }
}