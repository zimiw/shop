package com.easyshop.core.modules.admin;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

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
import org.nutz.trans.Atom;
import org.nutz.trans.Trans;

import com.easy.core.filters.CheckBackUserLoginFilter;
import com.easyshop.bean.Address;
import com.easyshop.bean.Order;
import com.easyshop.bean.OrderChange;
import com.easyshop.bean.OrderChangeDetail;
import com.easyshop.bean.Personal;
import com.easyshop.core.modules.ChargeModule;
import com.easyshop.utils.StringUtils;
import com.easyshop.utils.TimeUtils;
import com.easyshop.vo.ResultVo;

/**
 * 后台订单管理
 * 
 * @author luocz
 *
 */
@IocBean
@At("/admin/order")
@Ok("json")
@Fail("http:500")
 @Filters({ @By(type = CheckBackUserLoginFilter.class) })
public class OrderAdminModule {

    @Inject
    protected Dao dao;

    @Inject
    protected OrderUtil orderUtil;

    @Inject
    protected ChargeModule chargeModule;

    /**
     * 订单管理 管理页面
     * 
     * @return
     */
    @At
    public Object queryOrderList(@Param("orderId") String orderId, @Param("productName") String productName,
            @Param("status") String status, int page, int rows) {
        Map<String, Object> result = new HashMap<String, Object>();
        List<Map<String, Object>> goodsList = null;// 每个订单对应的商品
        Map<String, Object> goods = null;

        Sql sql = null;
       String sqlStr =  "SELECT t.orderId,  t.createTime,  t.userId, t.status, " +
               "    t.amount,  b.minPrice price, a.number num,  b.name   title " +
               "FROM orders t,  connectorop a, product b " +
               "WHERE t.orderId = a.orderId " +
               "AND a.productId = b.productId ";
       
       String sqlStr2 =  "SELECT  count(1) " +
               "FROM orders t,  connectorop a, product b " +
               "WHERE t.orderId = a.orderId " +
               "AND a.productId = b.productId ";
       
       String sqlWhere = "";
       List<String> paramsList = new ArrayList<String>();
       Map<String, Object> valMap = new HashMap<String, Object>();
       
       if (!StringUtils.isEmpty(status)) {
           sqlWhere += " and t.status = @status ";
           paramsList.add("status");
           valMap.put("status", status);
       }

       if (!StringUtils.isEmpty(orderId)) {// 订单编号
           sqlWhere += " and t.orderId = @orderId ";
           paramsList.add("orderId");
           valMap.put("orderId", orderId);
       }
       
       if (!StringUtils.isEmpty(productName)) {// 商品名称
           sqlWhere += " and b.name  like @productName  ";
           paramsList.add("productName");
           valMap.put("productName", "%" + productName + "%");
    }
       
       sql = Sqls.create(sqlStr2 + sqlWhere);
       for (String str : paramsList) {
           sql.params().set(str, valMap.get(str));
       }
       sql.setCallback(Sqls.callback.integer());
       dao.execute(sql);
       int total = sql.getInt();
       result.put("total", total);

       if (total == 0) {
           return result;
       }
       
       sqlStr += sqlWhere+" limit @pageNum, @pageSize ";
       sql = Sqls.create(sqlStr);
       for (String str : paramsList) {
           sql.params().set(str, valMap.get(str));
       }
       
       Pager pager = dao.createPager(page, rows);
       sql.params().set("pageNum", pager.getOffset());
       sql.params().set("pageSize", pager.getPageSize());
       
       sql.setCallback(Sqls.callback.maps());
       dao.execute(sql);
       List<Map> list = sql.getList(Map.class);
       String userId = "";
       String statusDesc = "";
       String productId = "";
       for(Map map : list){
           userId = String.valueOf(map.get("userId"));
           statusDesc = String.valueOf(map.get("status"));
           productId = String.valueOf(map.get("productId"));
           map.put("nickname", getOrderPersonal(Integer.parseInt(userId)).getNickname());// 会员昵称
           map.put("statusDesc", OrderConstant.statusMap.get(statusDesc));
           map.put("img", orderUtil.fetchImg(productId, true).getImgsource());
       }
       

//        String sqlOp = "SELECT a.number num, b.*  FROM connectorop a, product b  "
//                + " WHERE a.productId = b.productId and a.orderId = @orderId ";
//
//        for (Order order : orderList) {
//            map = new HashMap<String, Object>();
//            map.put("orderId", order.getOrderId());
//            map.put("createTime", order.getCreateTime());
//            map.put("userId", order.getUserId());
//            map.put("nickname", getOrderPersonal(order.getUserId()).getNickname());// 会员昵称
//            map.put("status", order.getStatus());
//            map.put("statusDesc", OrderConstant.statusMap.get(order.getStatus()));
//            map.put("amount", order.getAmount());
//            // 查询每个订单对应商品和数量
//            sql = Sqls.create(sqlOp);
//            sql.params().set("orderId", order.getOrderId());
//            sql.setCallback(Sqls.callback.maps());
//            dao.execute(sql);
//            List<Map> list = sql.getList(Map.class);
//
//            goodsList = new ArrayList<Map<String, Object>>();
//
//            for (Map opMap : list) {// 查询每个商品和对应的数量
//                goods = new HashMap<String, Object>();
//                float minPrice = Float.parseFloat(String.valueOf(opMap.get("minPrice")));
//                int num = Integer.parseInt(String.valueOf(opMap.get("num")));
//                goods.put("title", opMap.get("name"));
//                goods.put("img", orderUtil.fetchImg(String.valueOf(opMap.get("productId")), true).getImgsource());
//                goods.put("price", minPrice);
//                goods.put("num", num);
//                goodsList.add(goods);
//                // total += currentPrice * num;// 当个商品价格
//            }
//            // total
//            map.put("products", goodsList);
//
//            resList.add(map);
//        }
//
//        // { payno: "1563315489", paydate: "2014-05-08 18:00:26", total: "132",
//        // status: 1
//        // goods: [ {img: "img/demo_1.png", name:
//        // "TAKEO KIKUCHI/菊池武2015A 男士牛皮鳄鱼纹公",
//        // price: "606",num: 1 } ]}

        result.put("rows", list);

        return result;
    }

    /**
     * 根据获取对应订单的会员信息
     * 
     * @return
     */
    private Personal getOrderPersonal(int userId) {

        Personal personal = dao.fetch(Personal.class, Cnd.where("id", "=", userId));

        personal = personal == null ? new Personal() : personal;

        return personal;
    }

    /**
     * 根据订单id获取当个订单详情
     * 
     * @return
     */
    @At
    public Object getOrderDetail(@Param("orderId") String orderId) {

        Map<String, Object> result = new HashMap<String, Object>();
        Order order = dao.fetch(Order.class, Cnd.where("orderId", "=", orderId));
        if (order == null) {
            result.put("status", "fail");
            result.put("msg", "该订单不存在 !");
            return result;
        }

        // 查询订单商品
        String sqlOp = "SELECT a.number num, b.*" + " FROM connectorop a, product b  "
                + " WHERE a.productId = b.productId and a.orderId = @orderId ";

        Sql sql = Sqls.create(sqlOp);
        sql.params().set("orderId", order.getOrderId());
        sql.setCallback(Sqls.callback.maps());
        dao.execute(sql);
        List<Map> list = sql.getList(Map.class);

        Map<String, Object> map = null;
        List<Map<String, Object>> goodsList = new ArrayList<Map<String, Object>>();// 每个订单对应的商品
        Map<String, Object> goods = null;

        for (Map opMap : list) {// 查询每个商品和对应的数量
            goods = new HashMap<String, Object>();
            float currentPrice = Float.parseFloat(String.valueOf(opMap.get("currentPrice")));
            int num = Integer.parseInt(String.valueOf(opMap.get("num")));
            goods.put("name", opMap.get("name"));
            goods.put("img", orderUtil.fetchImg(String.valueOf(opMap.get("productId")), true).getImgsource());
            goods.put("price", currentPrice);
            goods.put("num", num);
            goodsList.add(goods);

            // total += currentPrice * num;// 当个商品价格
        }

        result.put("expressNo", order.getTransportId());
        result.put("expressName", "顺丰快递");
        result.put("addr", orderUtil.getAdressDesc(order.getAddressId()));
        result.put("order", order);
        result.put("nickname", getOrderPersonal(order.getUserId()).getNickname());// 会员昵称
        result.put("products", goodsList);
        return result;
    }

    /**
     * 后台订单取消, 关闭交易
     * 
     * @return
     */
    @At
    public Object orderCancel(@Param("orderId") String orderId) {
        Map<String, Object> result = new HashMap<String, Object>();
        Order order = dao.fetch(Order.class, Cnd.where("orderId", "=", orderId));
        if (order == null) {
            result.put("status", "fail");
            result.put("msg", "该订单不存在 !");
            return result;
        }
        if (order.getStatus() != 101) {// 订单非待付款状态，无法取消
            result.put("status", "fail");
            result.put("msg", "该订单非待付款状态，无法取消!");
            return result;
        }

        order.setStatus(107);

        int res = dao.update(order);

        if (res == 1) {
            result.put("status", "success");
            result.put("msg", "该订单取消成功");
        } else {
            result.put("status", "fail");
            result.put("msg", "该订单取消失败!");
        }
        return result;
    }

    /**
     * 订单金额修改
     * 
     * @return
     */
    @At
    public Object orderModifyAmount(@Param("orderId") String orderId, @Param("amount") float amount) {
        Map<String, Object> result = new HashMap<String, Object>();
        Order order = dao.fetch(Order.class, Cnd.where("orderId", "=", orderId));
        if (order == null) {
            result.put("status", "fail");
            result.put("msg", "该订单不存在 !");
            return result;
        }
        if (order.getStatus() != 101) {// 订单非待付款状态，无法取消
            result.put("status", "fail");
            result.put("msg", "该订单非待付款状态，无法修改价格!");
            return result;
        }
        order.setAmount(amount);

        int res = dao.update(order);

        if (res == 1) {
            result.put("status", "success");
            result.put("msg", "该订单价格修改成功");
        } else {
            result.put("status", "fail");
            result.put("msg", "该订单价格修改失败!");
        }
        return result;
    }

    /**
     * 后台管理员发货页面，获取物流信息
     * 
     * @return
     */
    @At
    public Object getOrderExpress(@Param("orderId") String orderId) {
        Map<String, Object> result = new HashMap<String, Object>();
        Order order = dao.fetch(Order.class, Cnd.where("orderId", "=", orderId));
        if (order == null) {
            result.put("status", "fail");
            result.put("msg", "该订单不存在 !");
            return result;
        }
        if (order.getStatus() != 102) {// 订单非待付款状态，无法取消
            result.put("status", "fail");
            result.put("msg", "该订单待发货状态，无法发货!");
            return result;
        }

        // 胡ｘｘ，13878923529，江苏省 南京市 鼓楼区 中门街道 南街43号2幢五单元678室 ，210003
        Address addr = dao.fetch(Address.class, Cnd.where("addressId", "=", order.getAddressId()));
        result.put("province", addr.getProvince());
        result.put("city", addr.getCity());
        result.put("district", addr.getDistrict());
        result.put("address", addr.getStreet());

        result.put("consignee", addr.getName());
        result.put("phone", addr.getCellPhoneNew());
        result.put("addr", orderUtil.getAdress(order.getAddressId()));

        return result;
    }

    /**
     * 后台退换货审核
     * 
     * @param session
     * @param changeId
     * @param type 1同意， 2驳回
     * @return
     * @throws Exception
     */
    @At
    public Object orderVerifyAdmin(HttpSession session, @Param("changeId") String changeId, @Param("type") String type)
            throws Exception {

        ResultVo resultVo = new ResultVo();
        final OrderChange change = dao.fetch(OrderChange.class, Cnd.where("id", "=", changeId));
        if (change == null) {
            resultVo.setMsg("查询不到对应申请记录!");
            return resultVo;
        }

        if (change.getStatus() != 301) {// 订单状态不是用户已提交
            resultVo.setMsg("该审核记录以及处理!");
            return resultVo;
        }

        String nodeDesc = "";
        if ("1".equals(type)) {// 退货审核通过
            if (change.getChangeType() == 1) {
                chargeModule.refund(change.getId());// 调用退款接口
            }

            change.setStatus(302);
            nodeDesc = "商家审核通过，请将货寄给商家并填写物流信息";
        } else if ("2".equals(type)) {
            change.setStatus(303);// 驳回 流程结束
            nodeDesc = "该请求已被驳回";
        }

        final String node = nodeDesc;

        Trans.exec(new Atom() {
            @Override
            public void run() {
                dao.update(change);

                insertDetail(change, node, change.getStatus());// 插入流程信息
            }
        });

        resultVo.setMsg("数据操作成功");
        return resultVo;

    }

    /**
     * 插入流程数据
     * 
     * @param change
     */
    private void insertDetail(OrderChange change, String nodeDesc, int status) {
        // 流程信息
        OrderChangeDetail detail = new OrderChangeDetail();
        detail.setChangeId(change.getId());
        detail.setStatus(status);
        detail.setNodeDesc(nodeDesc);
        detail.setNodeTime(TimeUtils.dateToStr(new Date(), TimeUtils.FORMAT14));

        dao.insert(detail);
    }

}
