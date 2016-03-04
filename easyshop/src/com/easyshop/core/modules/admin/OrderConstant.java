package com.easyshop.core.modules.admin;

import java.util.HashMap;
import java.util.Map;

/**
 * @author luocz
 *
 */
public class OrderConstant {

    public final static String SF_OK = "OK";// 顺丰成功标志

    public final static String SF_ERR = "ERR";// 顺丰失败

    public final static String FRONT_USER_ID = "frontUserId";// 前台用户sessionId

    // 1-银联,2-微信,3-支付宝
    //// 支付渠道 upacp_pc:银联 PC 网页支付 wx_pub:微信公众账号支付 wx_pub_qr:微信公众账号扫码支付
    public final static Map<String, String> PAYTYPEMAP = new HashMap<String, String>() {
        {
            put("upacp_pc", "银联");
            put("PC", "网页支付");
            put("wx_pub", "微信");
            put("wx_pub_qr", "微信");
            put("alipay_pc_direct", "支付宝");
        }
    };

    // 订单状态 待付款101， 待发货102， 待收货 103， 待评价 104，退/换货 105, 已完成106，取消100，管理员取消107
    public final static Map<Integer, Object> statusMap = new HashMap<Integer, Object>() {
        {
            put(101, "待付款");
            put(102, "待发货");
            put(103, "待收货");
            put(104, "待评价");
            put(105, "退货订单");
            put(106, "已完成");
            put(100, "取消");
            put(107, "管理员取消");
        }
    };

    // 退货方式 1:退货， 2：换货
    public final static Map<Integer, Object> changeTypeMap = new HashMap<Integer, Object>() {
        {
            put(1, "退货");
            put(2, "换货");
        }
    };

}
