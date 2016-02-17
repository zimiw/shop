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

import org.nutz.img.Images;
import org.nutz.lang.util.NutMap;
import org.nutz.mvc.upload.TempFile;
import sun.misc.BASE64Encoder;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.LinkedList;

/**
 * 〈〉<br>
 *
 * @author 15040635 wzs
 */
public class ImageUtils {

    public static String imageFileEncode2Str(NutMap nm,String imageName){
        LinkedList tfList = (LinkedList)nm.get(imageName);
        TempFile tf = (TempFile)tfList.get(0);
        File f = tf.getFile();
        BufferedImage image = Images.read(f);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Images.writeJpeg(image, out, 0.8f);
//        FieldMeta meta = tf.getMeta();
//        String oldName = meta.getFileLocalName();
        //在这里进行base64转码
        BASE64Encoder encoder = new BASE64Encoder();
        String result = encoder.encode(out.toByteArray());
        return result;
    }
}