package com.easyshop.core.modules;

import com.easy.core.filters.CheckBackUserLoginFilter;
import com.easy.core.filters.CheckFrontUserLoginFilter;
import com.easyshop.bean.Order;
import com.easyshop.bean.OrderChange;
import com.easyshop.utils.StatusUtils;
import com.easyshop.utils.StringUtils;
import com.easyshop.utils.TimeUtils;
import com.easyshop.vo.ResultVo;
import com.google.gson.internal.LinkedTreeMap;
import com.pingplusplus.Pingpp;
import com.pingplusplus.exception.*;
import com.pingplusplus.model.Charge;
import com.pingplusplus.model.Event;
import com.pingplusplus.model.Refund;
import com.pingplusplus.model.Webhooks;
import org.apache.log4j.Logger;
import org.nutz.aop.interceptor.ioc.TransAop;
import org.nutz.dao.Dao;
import org.nutz.dao.Sqls;
import org.nutz.dao.sql.Sql;
import org.nutz.ioc.aop.Aop;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.mvc.Mvcs;
import org.nutz.mvc.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

/**
 * 个人中心
 * 
 * @author luocz
 */
@IocBean
@At("charge")
@Ok("json")
@Fail("http:500")
public class ChargeModule {

    Logger LOGGER = Logger.getLogger(ChargeModule.class);

    @Inject
    protected Dao dao;

    @Inject
    protected OrderModule orderModule;

    /**
     * pingpp 管理平台对应的 API key
     */
    //public static String API_KEY = "sk_test_W9C0SGTCibTC9iTWfHa9ebTS";
    public static String API_KEY = "sk_live_5W144KT00iv9njvfb9njrPaT";

    /**
     * pingpp 管理平台对应的应用 ID
     */
    public static String APP_ID = "app_qLO880GuzXv1K8qT";


    static {
        //线程安全的. 否则线性支付不坑爹么
        Pingpp.apiKey = ChargeModule.API_KEY;
    }

    /**
     * @param orderId
     * @param channel
     *
     * @return
     *
     * @throws Exception
     */
    @At
    @Filters(@By(type = CheckFrontUserLoginFilter.class))
    //@Aop(TransAop.READ_COMMITTED)
    public ResultVo getCharge(long orderId, String channel) throws Exception {

        //关于异常处理方面还是跟框架一致的
        if (StringUtils.isEmpty(orderId, channel)) {
            return new ResultVo("fail", "参数错误");
        }
        //todo 暂时没有必要放到一个事务中去
        //查询该订单所有信息
        Order order = dao.fetch(Order.class, orderId);
        //todo 客户端传过来的参数都不可信任,应该去数据库验证一把看看,该用户有没有这个订单,而且没有支付完成的
        //todo 如果是第一次支付没有完成,第二次支付的时候,应该直接从数据库查询并把该订单的charge对象返回
        //todo 如果第一次支付失败,然后发起第二次支付 但是为了安全,还是直接去远程请求吧.
        //可以第二次请求的.相同的数据.即使顾客打开了付款渠道的页面,只要没有付款成功,直接关闭了,也是可以的.
        //todo 支付失败的异常不好测试,是不是模拟,钱不多了这种情况呢,虽然支付失败的情况比较少.

        //先处理第一次请求的支付
        order.setChargeChannel(channel);
        Charge charge = this.getChargeFromPingxx(order);
        String chargeJsonStr = charge.toString();
        //设置支付凭证信息
        order.setChargeId(charge.getId());
        order.setChargeJsonStr(chargeJsonStr);
        order.setChargeCreatedTime(TimeUtils.timestamp2Date(charge.getCreated()));
        order.setChargeExpireTime(TimeUtils.timestamp2Date(charge.getTimeExpire()));
        dao.updateIgnoreNull(order);
        LOGGER.info("订单生成支付凭证charge订单信息: " + order);
        LOGGER.info("订单生成支付凭证charge信息: " + charge);
        return new ResultVo(charge.toString());
    }

    /**
     * 创建 Charge
     * <p/>
     * 创建 Charge 用户需要组装一个 map 对象作为参数传递给 Charge.create(); map 里面参数的具体说明请参考：https://pingxx.com/document/api#api-c-new
     *
     * @return
     */
    private Charge getChargeFromPingxx(Order order) throws Exception {
        Charge charge = null;
        Map<String, Object> chargeMap = new HashMap<String, Object>();
        chargeMap.put("amount", new Float(order.getAmount()*100).intValue());
        chargeMap.put("currency", "cny");
        //商品的标题，该参数最长为 32 个 Unicode 字符
        chargeMap.put("subject", "洋流网商品");
        //商品的描述信息，该参数最长为 128 个 Unicode 字符
        chargeMap.put("body", "洋流网海外正品速达");
        chargeMap.put("client_ip", Mvcs.getReq().getLocalAddr());
        //商户订单号，适配每个渠道对此参数的要求，必须在商户系统内唯一
        //upacp: 8-40 位  wx: 1-32 位
        chargeMap.put("order_no", order.getOrderId());
        //upacp_pc:银联 PC 网页支付 wx_pub:微信公众账号支付 wx_pub_qr:微信公众账号扫码支付
        String chargeChannel = order.getChargeChannel();
        chargeMap.put("channel", order.getChargeChannel());

        if ("upacp_pc".equals(chargeChannel) || "alipay_pc_direct".equals(chargeChannel)) {
            Map<String, String> extra = new HashMap<String, String>();
            //如果是银联的话,需要这个支付完成回调的地址
            //http://localhost:8181/easyshop/charge/getCharge
            //String reqUrl=Mvcs.getReq().getRequestURL().toString();
            //extra.put("result_url", reqUrl.substring(0, reqUrl.length()-10)+"/handleChargeSucessFromChannelCallBack?orderId="+order.getOrderId());
            String key = "upacp_pc".equals(chargeChannel) ? "result_url" : "success_url";
            extra.put(key, "http://127.0.0.1:8181/easyshop/charge/handleChargeSucessFromChannelCallBack?orderId" + order
                    .getOrderId());
            chargeMap.put("extra", extra);
        }else if ("wx_pub".equals(chargeChannel)) {
            Map<String, String> extra = new HashMap<String, String>();
            extra.put("open_id ", String.valueOf(order.getOrderId()));
            chargeMap.put("extra", extra);
        }else if ("wx_pub_qr".equals(chargeChannel)) {
            Map<String, String> extra = new HashMap<String, String>();
            extra.put("product_id", String.valueOf(order.getOrderId()));
            chargeMap.put("extra", extra);
        }
        Map<String, String> app = new HashMap<String, String>();
        app.put("id", ChargeModule.APP_ID);
        chargeMap.put("app", app);
        try {
            //发起交易请求
            charge = Charge.create(chargeMap);
            LOGGER.info(charge);
        } catch (PingppException e) {
            LOGGER.info("生成charge支付对象的时候失败:" + order + ":::" + chargeMap);
            //todo 各种异常信息,要不要撸一下.
            e.printStackTrace();
            throw e;
        }
        return charge;
    }

    @At
    @Filters
    public void handleChargeEvent(HttpServletRequest request, HttpServletResponse response) throws Exception {
        request.setCharacterEncoding("UTF8");
        //获取头部所有信息
        Enumeration headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String key = (String) headerNames.nextElement();
            String value = request.getHeader(key);
            System.out.println(key+" "+value);
        }
        // 获得 http body 内容
        BufferedReader reader = request.getReader();
        StringBuffer buffer = new StringBuffer();
        String string;
        while ((string = reader.readLine()) != null) {
            buffer.append(string);
        }
        reader.close();
        // 解析异步通知数据
        Event event = Webhooks.eventParse(buffer.toString());
        if (event!=null){
            //LinkedTreeMap map=((LinkedTreeMap)event.getData().get("object"));
            LinkedTreeMap map=new LinkedTreeMap();
            if ("charge.succeeded".equals(event.getType())) {
                LOGGER.info("支付成功" + event.getData());
                Charge charge = (Charge) event.getData().getObject();
                //Long orderId=Long.parseLong(map.get("order_no").toString());
                //String chargeId = map.get("id").toString();
                /*Sql sql = Sqls.create("update orders set status=102 , chargePaidTime=@chargePaidTime where "
                        + "orderId=@orderId  and chargeId=@chargeId");
                sql.params().set("orderId", map.get("order_no"));
                sql.params().set("chargeId", map.get("id"));
                sql.params().set("chargePaidTime", TimeUtils.timestamp2Date(map.get("time_paid")));
                dao.execute(sql);*/
                this.manualCheckIsHasPaid(Long.parseLong(charge.getOrderNo()));
                response.setStatus(200);
            } else if ("refund.succeeded".equals(event.getType())) {
                LOGGER.info("退款成功" + event.getData());
                Refund refund = (Refund) event.getData().getObject();
                //todo 事务开始
                Sql sql = Sqls.create("update orderchange set refundStatus=@refundStatus , refundNo=@refundNo "
                        + ",  refundFailureMsg=@refundFailureMsg , refundSucceedTime=@refundSucceedTime "
                        + ",  status=@status where "
                        + "refundId=@refundId ");
                sql.params().set("refundId", refund.getId());
                sql.params().set("refundStatus", refund.getStatus());
                sql.params().set("status", StatusUtils.getStatusByRefundStatus(refund.getStatus()));
                //存在某些渠道,是没有交易流水号的
                sql.params().set("refundNo", refund.getOrderNo());
                sql.params().set("refundFailureMsg", refund.getFailureMsg()==null?"":refund.getFailureMsg());
                sql.params().set("refundSucceedTime", TimeUtils.timestamp2Date(refund.getTimeSucceed()));
                dao.execute(sql);
                response.setStatus(200);
            } else {
                response.setStatus(500);
            }
        }else{
            response.setStatus(500);
        }
    }


    @At
    @Filters
    public void handleChargeEvent4Live(HttpServletRequest request, HttpServletResponse response) throws Exception {
        this.handleChargeEvent(request, response);
    }

    @At
    @Filters
    public void handleChargeSucessFromChannelCallBack(long orderId,HttpServletRequest request, HttpServletResponse response) throws Exception {
        //todo  要先查询订单的状态是不是已经支付成功了
        /**
         *
         *
         *
         *
         *
         *
         *
         */
        Order order = dao.fetch(Order.class, orderId);
        if (order!=null && order.getStatus()==102) {
            //todo 要替换为支付成功页面
            response.sendRedirect(request.getContextPath() + "/front/home.html");
        }else{
            //todo 要替换为支付失败页面
            response.sendRedirect(request.getContextPath() + "/front/home.html");
        }
    }

    /**
     * 调用方法如果捕捉到异常,那么就是退款失败,也不用关注太多.
     * @param orderChangeId
     * @return
     * @throws Exception
     */
    @At
    @Filters(@By(type = CheckBackUserLoginFilter.class))
    public int refund(long orderChangeId) throws Exception {
        OrderChange orderChange = dao.fetch(OrderChange.class, orderChangeId);
        Order order = dao.fetch(Order.class, orderChange.getOrderId());
        //从订单表,拿到付款的时候的支付凭证id
        Charge charge = Charge.retrieve(order.getChargeId());
        //退款参数填写
        Map<String, Object> refundParams = new HashMap<String, Object>();
        refundParams.put("description", StringUtils.isEmpty(orderChange.getChangeReason())?"退款":orderChange.getChangeReason());
        refundParams.put("amount", new Float(orderChange.getAmount() * 100).intValue());
        //发送退款请求,因为存在退款处理中的状态,因此可以马上返回
        // 如果发起退款失败的话,直接就会抛出异常的
        Refund refund = charge.getRefunds().create(refundParams);
        orderChange.setRefundCreatedTime(TimeUtils.timestamp2Date(refund.getCreated()));
        orderChange.setRefundId(refund.getId());
        orderChange.setRefundStatus(refund.getStatus());
        int status=StatusUtils.getStatusByRefundStatus(refund.getStatus());
        orderChange.setStatus(status);
        orderChange.setRefundFailureMsg(refund.getFailureMsg());
        dao.updateIgnoreNull(orderChange);
        return status;
    }

    @At
    @Filters
    public boolean manualCheckIsHasPaid(long orderId) throws Exception {
        //todo 下一步可以添加,对同一个orderId 加锁
        Order order = dao.fetch(Order.class, orderId);
        if (102 == order.getStatus()) {
            return true;
        }
        String chargeId = order.getChargeId();

        Charge charge = Charge.retrieve(chargeId);
        if (charge.getPaid() && 102 != order.getStatus()) {
            this.setOrderPaid(charge);
        }
        LOGGER.info("manualCheckIsHasPaid :" + charge);
        return charge.getPaid();
    }

    /**
     * 废弃
     *
     * @param map
     * @param charge
     *
     * @throws Exception
     */
    @Deprecated
    private void setOrderPaid2(Map map, Charge charge) throws Exception {
        Sql sql = Sqls.create("update orders set status=102 , chargePaidTime=@chargePaidTime where "
                + "orderId=@orderId  and chargeId=@chargeId and status=101");
        if (charge == null) {
            sql.params().set("orderId", map.get("order_no"));
            sql.params().set("chargeId", map.get("id"));
            sql.params().set("chargePaidTime", TimeUtils.timestamp2Date(map.get("time_paid")));
        } else {
            sql.params().set("orderId", charge.getOrderNo());
            sql.params().set("chargeId", charge.getId());
            sql.params().set("chargePaidTime", TimeUtils.timestamp2Date(charge.getTimePaid()));
        }
        dao.execute(sql);
    }

    /**
     * 目前是如果支付成功了,才更新数据库
     * 始终保持只有一个地方能更新为成功状态
     *
     * @param charge
     *
     * @throws Exception
     */
    @Aop(TransAop.READ_COMMITTED)
    public void setOrderPaid(Charge charge) throws Exception {
        Sql sql = Sqls.create("update orders set status=102 , chargePaidTime=@chargePaidTime where "
                + "orderId=@orderId  and chargeId=@chargeId and status=101");
        sql.params().set("orderId", charge.getOrderNo());
        sql.params().set("chargeId", charge.getId());
        sql.params().set("chargePaidTime", TimeUtils.timestamp2Date(charge.getTimePaid()));
        dao.execute(sql);
        orderModule.setOrderProgress(Integer.parseInt(charge.getOrderNo()),102);
    }

}
