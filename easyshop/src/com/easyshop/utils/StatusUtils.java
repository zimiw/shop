/*
* Copyright (C), 2002-2016, 苏宁易购电子商务有限公司
* FileName: RandomUtils
* Author:   15040635 wzs
* Date:     2016/1/6 15:50
* Description: //模块目的、功能描述      
* History: //修改记录
* <author> <time> <version> <desc>
* 修改人姓名             修改时间            版本号                  描述
*/
package com.easyshop.utils;

import java.util.Random;

/**
 * 〈〉<br>
 *
 * @author 15040635 wzs
 */
public class StatusUtils {

    public static int getStatusByRefundStatus(String refundStatus){
        //状态说明:状态说明:pending: 处理中; succeeded: 成功; failed: 失败
        //304收到退货， 305退款进行中， 308退款失败，309退款完成
        if ("pending".equals(refundStatus)) {
            return 305;
        }else if ("succeeded".equals(refundStatus)) {
            return 309;
        }else if ("failed".equals(refundStatus)) {
            return 308;
        }else{
            throw  new RuntimeException("退款返回异常");
        }
    }
}