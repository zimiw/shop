package com.easyshop.bean.express;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.easyshop.bean.express.annotation.ExpressParam;

public class AbstractOrder {

    public byte[] calcSignData() {

        Map<String, String> map = fieldMap();
        String signData = createLink(map);

        byte[] rlst = null;
        // try {
        // rlst = signData.getBytes(ExtTradeConstants.ORDER_CHARSET);
        // } catch (UnsupportedEncodingException e) {
        // LOG.error("AbstractOrderMis calcSignData", e);
        // }
        return rlst;
    }

    protected Map<String, String> fieldMap() {
        Map<String, String> map = new HashMap<String, String>();

        try {
            Field[] fields = getClass().getDeclaredFields();
            for (Field f : fields) {
                if (f.isAnnotationPresent(ExpressParam.class) && f.getAnnotation(ExpressParam.class).join()) {
                    String propertyName = f.getName();
                    // String propertyValue = BeanUtils.getProperty(this, propertyName);
                    // if (StringUtils.isBlank(propertyValue)) {// 空字符串或者null不参与计算
                    // continue;
                    // }
                    // map.put(propertyName, propertyValue);
                }
            }
        } catch (Exception e) {
            // LOG.error("AbstractOrderMis fieldMap", e);
        }
        return map;
    }

    protected String createLink(Map<String, String> map) {
        StringBuffer sb = new StringBuffer();

        List<String> keys = new ArrayList<String>(map.keySet());
        // 字段排序
        Collections.sort(keys);
        for (int i = 0; i < keys.size(); i++) {
            String key = keys.get(i);
            String value = map.get(key);
            // if (StringUtils.isBlank(value)) {
            // continue;
            // }
            if (i == keys.size() - 1) {// 拼接时，不包括最后一个&字符
                sb.append(key).append("=").append(value);
            } else {
                sb.append(key).append("=").append(value).append("&");
            }

        }
        return sb.toString();
    }
}
