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
public class RandomUtils {

    public static String getRandomNumberStr(){
        int max=999999;
        int min=100000;
        Random random = new Random();
        int num = random.nextInt(max)%(max-min+1) + min;
        return String.valueOf(num);
    }
}