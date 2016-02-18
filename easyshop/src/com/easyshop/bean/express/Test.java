package com.easyshop.bean.express;

import java.util.HashMap;
import java.util.Map;

import com.thoughtworks.xstream.XStream;

public class Test {

    static String url = "http://bspoisp.sit.sf-express.com:11080/bsp-oisp/sfexpressService";
    static String checkWord = "j8DzkIFgmlomPt0aLuwU";

    public static void main(String[] args) {

        Test test = new Test();
        // test.sfexpressService();

        test.orderSearch();

        // test.routePush();

        //
        // XStream xstream = new XStream();
        // xstream.autodetectAnnotations(true);
        //
        // Response res = new Response("OrderService", "BSPdevelop");
        // xstream.alias("order", Response.class);
        // Body body = new Body();
        // Order order = new Order();
        // body.setOrder(order);
        // res.setBody(body);
        //
        // String xml = xstream.toXML(res);
        //
        // System.out.println("xml-->" + xml);
        //
        // String str =
        // "<Response service=\"OrderService\">        <Head>ERR</Head>        <ERROR code=\"8016\">重复下单</ERROR>        </Response> ";
        // System.out.println("str-->" + str);
        // XStream xstream2 = new XStream();
        // xstream2.processAnnotations(Response.class);
        // xstream2.processAnnotations(Error.class);
        // xstream2.autodetectAnnotations(true);
        // xstream2.useAttributeFor(Error.class, "code");
        // xstream2.registerConverter(new ErrorConverter());
        // Response res2 = (Response) xstream2.fromXML(str);
        //
        // System.out.println(res2.getHead());
        // System.out.println(res2.getERROR());
        // System.out.println(res2.getERROR().getValue());
        // System.out.println(order2.getOrderid());

        // ExpressOrder cre_person = (ExpressOrder) xstream.fromXML(xml);
    }

    public Response analysisXml(String xml) {

        XStream xstream2 = new XStream();
        xstream2.processAnnotations(Response.class);
        xstream2.processAnnotations(Error.class);
        xstream2.processAnnotations(OrderResponse.class);
        xstream2.autodetectAnnotations(true);
        xstream2.useAttributeFor(Error.class, "code");
        xstream2.registerConverter(new ErrorConverter());
        Response res2 = (Response) xstream2.fromXML(xml);
        System.out.println(res2);
        return res2;
    }

    public XStream getXStream() {
        XStream xstream = new XStream();
        xstream.autodetectAnnotations(true);
        return xstream;
    }

    public Map<String, String> getParams(String xml) {
        Map<String, String> params = new HashMap<String, String>();
        String verifyCode = Util.md5EncryptAndBase64(xml + checkWord);
        // "xml=" + xml + "&verifyCode=" + verifyCode
        params.put("xml", xml);
        params.put("verifyCode", verifyCode);
        return params;
    }

    /**
     * 顺丰接口请求封装
     */
    public Response sfRequest(Request request) {
        XStream xstream = getXStream();
        String xml = xstream.toXML(request);
        System.out.println("xml-->" + xml);
        HttpClientTemplate http = new HttpClientTemplate();
        String reuslt = http.executePost(url, getParams(xml));
        System.out.println(reuslt);

        return analysisXml(reuslt);
    }

    /**
     * 下单接口
     */
    public void sfexpressService() {
        Request req = new Request("OrderService", "BSPdevelop");
        ExpressOrder order = new ExpressOrder();

        req.setOrder(order);

        Response res = sfRequest(req);
        System.out.println(res);
    }

    /**
     * 订单确认或取消
     */
    public void orderConfirm() {

        XStream xstream = getXStream();
        Request res = new Request("OrderConfirmService", "BSPdevelop");
        OrderConfirm confirm = new OrderConfirm();
        confirm.setDealtype(1);
        confirm.setOrderid("TE201500104");
        confirm.setMailno("444003078326");
        res.setOrderConfirm(confirm);

        String xml = xstream.toXML(res);
        System.out.println("xml-->" + xml);
        HttpClientTemplate http = new HttpClientTemplate();

        String reuslt = http.executePost(url, getParams(xml));
        System.out.println(reuslt);
        analysisXml(reuslt);

    }

    /**
     * 订单查询
     */
    public void orderSearch() {

        XStream xstream = getXStream();
        Request res = new Request("OrderConfirmService", "OrderSearchService");
        OrderSearch search = new OrderSearch();
        search.setOrderid("TE201500104");
        res.getBody().setOrderSearch(search);

        String xml = xstream.toXML(res);
        System.out.println("xml-->" + xml);
        // HttpClientTemplate http = new HttpClientTemplate();
        //
        // String reuslt = http.executePost(url, getParams(xml));

        String result = "<Response service=\"OrderSearchService\"> <Head>OK</Head> <Body> <OrderResponse orderid= "
                + "\"TE20150104\"mailno=\"444003078089\"origincode=\"755\"destcode=\"010\"filter_result=\"2\"/> </ "
                + "Body> </Response>";
        System.out.println("xml-->" + result);
        System.out.println(result);
        analysisXml(result);
    }

    /**
     * 路由查询
     */
    public void routeSearch() {
        Request req = new Request("RouteService", "BSPdevelop");
        RouteRequest route = new RouteRequest();
        route.setTracking_type(1);
        route.setTracking_number("444003077898");
        req.setRouteRequest(route);

        Response res = sfRequest(req);
        System.out.println(res);
    }

    /**
     * 路由推送接口
     */
    public void routePush() {

        String str = "<Request service=\"RoutePushService\" lang=\"zh-CN\"> " + "<Body> "
                + "	<WaybillRoute id=\"10049361064087\" "
                + " mailno=\"444003079772\"orderid=\"TE201500106\"acceptTime=\"2015-01-04 17:42:00\"acceptAddress= "
                + "\"深圳\"  remark=\"上门收件\"opCode=\"50\"/>  </Body> " + "</Request>";

        XStream xstream2 = new XStream();
        xstream2.processAnnotations(Request.class);
        xstream2.processAnnotations(Body.class);
        xstream2.processAnnotations(WaybillRoute.class);

        xstream2.processAnnotations(Error.class);
        xstream2.autodetectAnnotations(true);
        xstream2.useAttributeFor(Error.class, "code");
        xstream2.registerConverter(new ErrorConverter());
        Request res2 = (Request) xstream2.fromXML(str);
        System.out.println(res2);
    }

}
