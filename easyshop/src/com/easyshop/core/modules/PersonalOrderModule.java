package com.easyshop.core.modules;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.nutz.dao.Cnd;
import org.nutz.dao.Dao;
import org.nutz.dao.Sqls;
import org.nutz.dao.pager.Pager;
import org.nutz.dao.sql.Sql;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.mvc.annotation.At;
import org.nutz.mvc.annotation.By;
import org.nutz.mvc.annotation.Fail;
import org.nutz.mvc.annotation.Filters;
import org.nutz.mvc.annotation.Ok;
import org.nutz.mvc.annotation.Param;
import org.nutz.mvc.filter.CheckSession;

import com.easyshop.bean.Address;
import com.easyshop.bean.ConnectorOP;
import com.easyshop.bean.Images;
import com.easyshop.bean.Order;
import com.easyshop.bean.OrderChange;
import com.easyshop.bean.OrderChangeDetail;
import com.easyshop.bean.Personal;
import com.easyshop.bean.Product;
import com.easyshop.core.modules.admin.OrderConstant;
import com.easyshop.core.modules.admin.OrderUtil;
import com.easyshop.utils.StringUtils;
import com.easyshop.vo.ResultVo;

/**
 * 个人中心订单查询
 * 
 * @author luocz
 */
@IocBean
@At("/personalorder")
@Ok("json")
@Fail("http:500")
@Filters(@By(type = CheckSession.class, args = { OrderConstant.FRONT_USER_ID, "/front/login.html" }))
public class PersonalOrderModule {

    Logger logger = Logger.getLogger(PersonalOrderModule.class);

    @Inject
    protected Dao dao;
    @Inject
    protected OrderUtil orderUtil;

    /**
     * 根据用户id取最近3笔订单
     *
     * @param userId
     *
     * @return
     */
    @At
    public Object getRecent(HttpSession session) {

        Map<String, Object> result = new HashMap<String, Object>();
        String userId = String.valueOf(session.getAttribute(OrderConstant.FRONT_USER_ID));

        Pager pager = dao.createPager(0, 3);
        List<Order> orderList = dao.query(Order.class, Cnd.where("userId", "=", userId).desc("orderId"), pager);
        Order order = null;
        ConnectorOP op = null;
        List<ConnectorOP> listOp = null;
        List<Map<String, Object>> queryList = new ArrayList<Map<String, Object>>();
        Map<String, Object> map = new HashMap<String, Object>();
        Images image = null;
        Product product = null;
        // 取出最近3条记录
        for (int i = 0; i < orderList.size() && i <= 3; i++) {
            order = orderList.get(i);
            listOp = dao.query(ConnectorOP.class, Cnd.where("orderId", "=", order.getOrderId()));

            op = listOp.get(0);
            product = dao.fetch(Product.class, Cnd.where("productId", "=", op.getProductId()));
            image = fetchImg(product.getProductId());
            map = new HashMap<String, Object>();
            map.put("payno", order.getOrderId());
            map.put("status", OrderConstant.statusMap.get(order.getStatus()));
            map.put("img", image.getImgsource());
            map.put("name", product.getName());
            map.put("price", product.getCurrentPrice());
            queryList.add(map);
        }

        result.put("recent", queryList);
        return result;
    }

    /**
     * 获取用户订单数
     * 
     * @return
     */
    @At
    public Object getMyOrderNum(HttpSession session) {
        Map<String, Object> result = new HashMap<String, Object>();
        String userId = String.valueOf(session.getAttribute(OrderConstant.FRONT_USER_ID));
        Personal personal = dao.fetch(Personal.class, Cnd.where("id", "=", userId));
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("userId", personal.getId());
        map.put("all", personal.getPnopay() + personal.getPsend() + personal.getPwait() + personal.getPevaluate());
        map.put("pnopay", personal.getPnopay());// 待付款商品数量
        map.put("psend", personal.getPsend());// 待发货
        map.put("pwait", personal.getPwait());// 待收货商品数量
        map.put("pevaluate", personal.getPevaluate());// 待评价商品数量

        result.put("ordernum", map);
        return result;
    }

    /**
     * 查询用对应状态的订单
     * 
     * @return
     */
    @At
    public Object getMyOrder(HttpSession session, @Param("status") String status) {
        Map<String, Object> result = new HashMap<String, Object>();
        String userId = String.valueOf(session.getAttribute(OrderConstant.FRONT_USER_ID));

        Map<String, Object> map = null;
        List<Map<String, Object>> goodsList = null;// 每个订单对应的商品
        Map<String, Object> goods = null;

        String sqlOp = "SELECT a.number num, b.*" + " FROM connectorop a, product b  "
                + " WHERE t.productId = b.productId and a.orderId = @orderId ";

        Cnd cnd = Cnd.where("userId", "=", userId);
        if (!status.equals("0")) {// 不是查询全部
            cnd.and("status", "=", status);
        }
        // 查询用户所有订单 TODO：订单没有分页
        List<Order> orderList = dao.query(Order.class, cnd.desc("orderId"));
        List<Map<String, Object>> resList = new ArrayList<Map<String, Object>>();
        Sql sql = null;
        float total = 0l;
        for (Order order : orderList) {
            total = 0l;
            map = new HashMap<String, Object>();
            map.put("payno", order.getOrderId());
            map.put("paydate", order.getCreateTime());
            map.put("status", order.getStatus());
            map.put("statusDesc", OrderConstant.statusMap.get(order.getStatus()));

            // 查询每个订单对应商品和数量
            sql = Sqls.create(sqlOp);
            sql.params().set("orderId", order.getOrderId());
            sql.setCallback(Sqls.callback.maps());
            dao.execute(sql);
            List<Map> list = sql.getList(Map.class);

            goodsList = new ArrayList<Map<String, Object>>();

            for (Map opMap : list) {// 查询每个商品和对应的数量
                goods = new HashMap<String, Object>();
                float currentPrice = Float.parseFloat(String.valueOf(opMap.get("currentPrice")));
                int num = Integer.parseInt(String.valueOf(opMap.get("num")));
                goods.put("name", opMap.get("name"));
                goods.put("img", fetchImg(Integer.parseInt(String.valueOf(opMap.get("productId")))).getImgsource());
                goods.put("price", currentPrice);
                goods.put("num", num);
                goodsList.add(goods);
                total += currentPrice * num;// 当个商品价格
            }
            map.put("total", total);
            // total
            map.put("goods", goodsList);

            resList.add(map);
        }

        // { payno: "1563315489", paydate: "2014-05-08 18:00:26", total: "132",
        // status: 1
        // goods: [ {img: "img/demo_1.png", name:
        // "TAKEO KIKUCHI/菊池武2015A 男士牛皮鳄鱼纹公",
        // price: "606",num: 1 } ]}

        result.put("list", resList);

        return result;
    }

    /**
     * 查询订单<br/>
     * 个人订单页面订单查询
     * 
     * @return
     */
    @At
    public Object queryOrder(HttpSession session, @Param("queryContent") String queryContent,
            @Param("status") String status) {
        Map<String, Object> result = new HashMap<String, Object>();
        String userId = String.valueOf(session.getAttribute(OrderConstant.FRONT_USER_ID));

        if ("".equals(queryContent)) {
            return new ResultVo(ResultVo.STATUS_FAIL, "查询内容不能为空!");
        }

        // 查询内容 商品名称或订单号

        String sqlOp = "SELECT a.number num, b.*  " + " FROM connectorop a, product b  "
                + " WHERE t.productId = b.productId and a.orderId = @orderId ";

        List<Integer> orderIds = null;
        Sql sql = null;
        // 先根据订单号查询
        Order order = dao.fetch(Order.class, Cnd.where("userId", "=", userId).and("orderId", "=", queryContent));
        if (order == null) {// 再根据商品名称查询

            String sqlName = "SELECT a.orderId  FROM orders a,  product b, connectorop t "
                    + "WHERE a.orderId = t.orderId  AND b.productId = t.productId   AND a.userId = @userId "
                    + " AND b.name LIKE @name ";
            sql = Sqls.create(sqlName);
            sql.params().set("userId", userId).set("name", queryContent);
            sql.setCallback(Sqls.callback.ints());
            dao.execute(sql);
            orderIds = sql.getList(Integer.class);
        } else {
            orderIds = new ArrayList<Integer>();
            orderIds.add(order.getOrderId());
        }

        List<Map<String, Object>> resList = null;

        if (orderIds == null || orderIds.size() == 0) {
            result.put("list", resList);
            return result;
        }

        Map<String, Object> map = null;
        List<Map<String, Object>> goodsList = null;// 每个订单对应的商品
        Map<String, Object> goods = null;
        resList = new ArrayList<Map<String, Object>>();

        for (int orderId : orderIds) {// 根据查询出来的订单id生成数据

            order = dao.fetch(Order.class, Cnd.where("orderId", "=", orderId));
            float total = 0l;
            map = new HashMap<String, Object>();
            map.put("payno", order.getOrderId());
            map.put("paydate", order.getCreateTime());
            map.put("status", order.getStatus());
            map.put("statusDesc", OrderConstant.statusMap.get(order.getStatus()));

            goodsList = new ArrayList<Map<String, Object>>();

            sql = Sqls.create(sqlOp);
            sql.params().set("orderId", orderId);
            sql.setCallback(Sqls.callback.maps());
            dao.execute(sql);
            List<Map> list = sql.getList(Map.class);

            for (Map opMap : list) {// 查询每个商品和对应的数量
                goods = new HashMap<String, Object>();
                float currentPrice = Float.parseFloat(String.valueOf(opMap.get("currentPrice")));
                int num = Integer.parseInt(String.valueOf(opMap.get("num")));
                goods.put("name", opMap.get("name"));
                goods.put("img", fetchImg(Integer.parseInt(String.valueOf(opMap.get("productId")))).getImgsource());
                goods.put("price", currentPrice);
                goods.put("num", num);
                goodsList.add(goods);
                total += currentPrice * num;// 当个商品价格
            }
            map.put("total", total);
            // total
            map.put("goods", goodsList);

            resList.add(map);
        }

        result.put("list", resList);
        return result;
    }

    /**
     * 获取订单详情 <br/>
     * 订单详情页面
     * 
     * @return
     */
    @At
    public Object getOrderDetail(HttpSession session, @Param("orderId") String orderId) {

        Map<String, Object> result = new HashMap<String, Object>();
        String userId = String.valueOf(session.getAttribute(OrderConstant.FRONT_USER_ID));

        // 获取订单信息
        Cnd cnd = Cnd.where("userId", "=", userId).and("orderId", "=", orderId);
        Order queryOrder = dao.fetch(Order.class, cnd);

        if (queryOrder == null) {
            result.put("status", "fail");
            result.put("msg", "该订单不存在!");
            return result;
        }

        // 查询支付信息
//        PaymentInfo paymentInfo = dao.fetch(PaymentInfo.class, Cnd.where("orderId", "=", orderId));

        // 查询订单商品信息
        String sqlStr = "SELECT b.productId, b.name,  t.price, t.number num " + " FROM product b, connectorop t "
                + " WHERE b.productId = t.productId  and t.orderId = @orderId ";

        Sql sql = Sqls.create(sqlStr);
        sql.params().set("orderId", orderId);
        sql.setCallback(Sqls.callback.maps());
        dao.execute(sql);
        List<Map> goodsList = sql.getList(Map.class);
        String productId;
        for (Map map : goodsList) {// 取每个商品的主图片
            productId = String.valueOf(map.get("productId"));
            map.put("img", fetchImg(Integer.parseInt(productId)).getImgsource());
        }

        // dateList订单跟踪
        sqlStr = "SELECT  DATE_FORMAT(a.time, '%Y-%m-%d') day, " + "    DATE_FORMAT(a.time, '%T') time, statusCode "
                + "FROM  orderprogress a WHERE a.orderId = @orderId " + "ORDER BY a.statusCode";

        sql = Sqls.create(sqlStr);
        sql.params().set("orderId", orderId);
        sql.setCallback(Sqls.callback.maps());
        dao.execute(sql);
        List<Map> dateList = sql.getList(Map.class);
        // 订单信息
        // oid
        result.put("oid", queryOrder.getOrderId());
        result.put("odate", queryOrder.getCreateTime());
        result.put("status", queryOrder.getStatus());

        result.put("freight", "0");// 邮费
        result.put("total", queryOrder.getAmount());// 订单总金额

        result.put("dateList", dateList);

        // 收货人信息
        Address addr = orderUtil.getAdress(queryOrder.getAddressId());

        result.put("consignee", addr.getName());
        result.put("address", orderUtil.getAdressDesc(addr));
        result.put("phone", addr.getCellPhoneNew());

        // 支付及配送方式
        // paymentInfo
        result.put("paytype", OrderConstant.PAYTYPEMAP.get(queryOrder.getChargeChannel()));
        result.put("oid", queryOrder.getTransportId());
        result.put("company", "顺丰");

        result.put("goods", goodsList);

        return result;
    }

    /**
     * 换货订单详情
     * 
     * @return
     */
    @At
    public Object getChangeDetail(HttpSession session, @Param("orderId") String orderId,
            @Param("productId") String productId) {
        String userId = String.valueOf(session.getAttribute(OrderConstant.FRONT_USER_ID));
        return getOrderChangeReturn(userId, orderId, productId, 2);
    }

    /**
     * 退货订单详情 <br/>
     * 退货订单页面
     */
    @At
    public Object getReturnDetail(HttpSession session, @Param("orderId") String orderId,
            @Param("productId") String productId) {

        String userId = String.valueOf(session.getAttribute(OrderConstant.FRONT_USER_ID));
        return getOrderChangeReturn(userId, orderId, productId, 1);
    }

    /**
     * 退换货页面详情获取
     * 
     * @param userId
     * @param orderId
     * @param productId
     * @param changeType
     * @return
     */
    private Map<String, Object> getOrderChangeReturn(String userId, String orderId, String productId, int changeType) {
        Map<String, Object> result = new HashMap<String, Object>();

        Cnd cnd = Cnd.where("userId", "=", userId).and("orderId", "=", orderId).and("productId", "=", productId)
                .and("changeType", "=", changeType);
        OrderChange orderChange = dao.fetch(OrderChange.class, cnd);

        if (orderChange == null) {
            result.put("status", "fail");
            result.put("msg", "该订单不存在!");
            return result;
        }

        List<OrderChangeDetail> deltailList = dao.query(OrderChangeDetail.class,
                Cnd.where("changeId", "=", orderChange.getId()).asc("nodeTime"));

        // int changeType = orderChange.getChangeType();
        Map<String, Object> returnInfo = new HashMap<String, Object>();
        returnInfo.put("type", OrderConstant.changeTypeMap.get(changeType));
        returnInfo.put("price", orderChange.getAmount());
        returnInfo.put("num", orderChange.getNum());
        returnInfo.put("way", changeType == 1 ? "退货到付款账户" : "");// 退货才有
        returnInfo.put("reason", orderChange.getChangeReason());// 原因
        returnInfo.put("remark", orderChange.getRemark());
        returnInfo.put("orderChangeId", orderChange.getId());// 退换货id

        result.put("returnInfo", returnInfo);

        result.put("process", deltailList);

        List<Map> goodsList = getOrderProduct(orderId, productId);

        result.put("goods", goodsList);

        // dateList = deltailList

        return result;
    }

    /**
     * 获取订单对应的商品
     * 
     * @return
     */
    private List<Map> getOrderProduct(String orderId, String productId) {

        // 查询订单商品信息
        String sqlStr = "SELECT b.productId, b.name,  t.price, t.number num " + " FROM product b, connectorop t "
                + " WHERE b.productId = t.productId  and t.orderId = @orderId ";

        if (!StringUtils.isEmpty(productId)) {
            sqlStr += " and b.productId = @productId";
        }

        Sql sql = Sqls.create(sqlStr);
        sql.params().set("orderId", orderId);

        if (!StringUtils.isEmpty(productId)) {
            sql.params().set("productId", productId);
        }

        sql.setCallback(Sqls.callback.maps());
        dao.execute(sql);
        List<Map> goodsList = sql.getList(Map.class);
        String proId;
        for (Map map : goodsList) {// 取每个商品的主图片
            proId = String.valueOf(map.get("productId"));
            map.put("img", fetchImg(Integer.parseInt(proId)).getImgsource());
        }
        return goodsList;
    }

    /**
     * 根据订单id和商品id查询订单和商品信息<br/>
     * 退换货界面
     * 
     * @param session
     * @param orderId
     * @return
     */
    @At
    public Object getOrderAndProduct(HttpSession session, @Param("orderId") String orderId,
            @Param("productId") String productId) {

        Map<String, Object> result = new HashMap<String, Object>();
        String userId = String.valueOf(session.getAttribute(OrderConstant.FRONT_USER_ID));

        // 获取订单信息
        Cnd cnd = Cnd.where("userId", "=", userId).and("orderId", "=", orderId);
        Order queryOrder = dao.fetch(Order.class, cnd);

        // 商品信息
        List<Map> goodsList = getOrderProduct(orderId, productId);
        if (queryOrder == null || goodsList == null || goodsList.size() == 0) {
            result.put("status", "fail");
            result.put("msg", "该订单不存在 !");
            return result;
        }

        Map<String, Object> goodsMap = goodsList.get(0);

        result.put("name", goodsMap.get("name"));//
        result.put("img", goodsMap.get("img"));//
        goodsMap.put("oid", queryOrder.getOrderId());
        goodsMap.put("freight", 0);// 运费
        goodsMap.put("total", queryOrder.getAmount());
        goodsMap.put("odate", queryOrder.getCreateTime());
        goodsMap.put("returnPrice", 0);

        return goodsMap;
    }

    /**
     * 根据商品id获取对应是商品id
     * 
     * @return
     */
    private Images fetchImg(int productId) {
        Images imgs = dao.fetch(Images.class, Cnd.where("productId", "=", productId).and("isTopimg", "=", true));
        return imgs != null ? imgs : new Images();
    }
}
