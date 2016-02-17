package com.easyshop.core.modules;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.Date;

import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.nutz.dao.Cnd;
import org.nutz.dao.Dao;
import org.nutz.img.Images;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.util.NutMap;
import org.nutz.mvc.annotation.AdaptBy;
import org.nutz.mvc.annotation.At;
import org.nutz.mvc.annotation.By;
import org.nutz.mvc.annotation.Fail;
import org.nutz.mvc.annotation.Filters;
import org.nutz.mvc.annotation.Ok;
import org.nutz.mvc.annotation.Param;
import org.nutz.mvc.filter.CheckSession;
import org.nutz.mvc.upload.TempFile;
import org.nutz.mvc.upload.UploadAdaptor;
import org.nutz.trans.Atom;
import org.nutz.trans.Trans;

import sun.misc.BASE64Encoder;

import com.easyshop.bean.OrderChange;
import com.easyshop.bean.OrderChangeDetail;
import com.easyshop.core.modules.admin.OrderConstant;
import com.easyshop.utils.TimeUtils;
import com.easyshop.vo.ResultVo;

/**
 * 退货换货
 * 
 * @author luocz
 *
 */
@IocBean
@At("/personalchange")
@Ok("json")
@Fail("http:500")
@Filters(@By(type = CheckSession.class, args = { OrderConstant.FRONT_USER_ID, "/front/login.html" }))
public class PersonalChangeModule {

    Logger logger = Logger.getLogger(PersonalChangeModule.class);

    @Inject
    protected Dao dao;

    @Inject
    protected ChargeModule chargeModule;

    /**
     * 前台用户发起换货<br/>
     * 
     * 换货申请: 换货原因 changeReason， 订单id orderId， 商品id productId， 数量 num， 换货说明 remark， 换货凭证1 image1， 换货凭证2 image2
     * 
     * @return
     */
    @At
    @AdaptBy(type = UploadAdaptor.class, args = { "ioc:picUpload" })
    public Object orderChange(HttpSession session, @Param("..") final OrderChange orderChange,
            @Param("image1") TempFile image1, @Param("image2") TempFile image2) {

        orderChange.setChangeType(2);
        ResultVo resultVo = checkRetrun(orderChange);
        if (resultVo != null) {
            return resultVo;
        }

        String userId = String.valueOf(session.getAttribute(OrderConstant.FRONT_USER_ID));
        resultVo = new ResultVo();

        // 换货原因 退款说明 退 款凭证
        orderChange.setUserId(Integer.parseInt(userId));

        // 换货凭证1 image1
        // 换货凭证2 image2
        orderChange.setImage1(getImageBase64(image1));
        orderChange.setImage2(getImageBase64(image2));
        orderChange.setStatus(301);// 用户提交

        Trans.exec(new Atom() {
            @Override
            public void run() {
                OrderChange change = dao.insert(orderChange);

                insertDetail(change, "买家已提交退货申请", orderChange.getStatus());// 插入流程信息
            }
        });

        resultVo.setMsg("数据保存成功");

        return resultVo;
    }

    /**
     * 用户发起退货<br/>
     * 订单id orderId， 商品id productId， 数量 num， 金额 amount， 退款原因 changeReason， 退款说明 remark， 退款凭证1 image1， 退款凭证2 image2
     * 
     * @throws Exception
     * 
     */
    @At
    @AdaptBy(type = UploadAdaptor.class, args = { "ioc:picUpload" })
    public Object orderReturn(HttpSession session, @Param("..") final OrderChange orderChange, @Param("..") NutMap nm,
            @Param("image1") TempFile image1, @Param("image2") TempFile image2) throws Exception {

        orderChange.setChangeType(1);
        // 退款金额不必须小于 商品价格*数量
        ResultVo resultVo = checkRetrun(orderChange);

        if (resultVo != null) {
            return resultVo;
        }

        String userId = String.valueOf(session.getAttribute(OrderConstant.FRONT_USER_ID));
        resultVo = new ResultVo();

        // 退款原因 退款说明 退款金额 退款凭证
        orderChange.setUserId(Integer.parseInt(userId));

        // 换货凭证1 image1
        // 换货凭证2 image2
        orderChange.setImage1(getImageBase64(image1));
        orderChange.setImage2(getImageBase64(image2));
        orderChange.setStatus(301);// 用户提交

        Trans.exec(new Atom() {
            @Override
            public void run() {
                OrderChange change = dao.insert(orderChange);

                insertDetail(change, "买家已提交退货申请", orderChange.getStatus());// 插入流程信息
            }
        });

        resultVo.setMsg("数据保存成功");

        return resultVo;
    }

    private String getImageBase64(TempFile tf) {

        if (tf == null) {
            return "";
        }
        File f = tf.getFile();
        BufferedImage image = Images.read(f);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Images.writeJpeg(image, out, 0.8f);
        // FieldMeta meta = tf.getMeta();
        // 在这里进行base64转码
        BASE64Encoder encoder = new BASE64Encoder();
        String result = encoder.encode(out.toByteArray());

        return result;
    }

    /**
     * 退换货取消
     * 
     * @return
     */
    @At
    public ResultVo cancelOrderChange(HttpSession session, @Param("orderChangeId") String orderChangeId) {

        String userId = String.valueOf(session.getAttribute(OrderConstant.FRONT_USER_ID));

        final OrderChange orderChange = dao.fetch(OrderChange.class,
                Cnd.where("userId", "=", userId).and("id", "id", orderChangeId));

        ResultVo resultVo = new ResultVo();
        if (orderChange == null) {
            resultVo.setStatus(ResultVo.STATUS_FAIL);
            resultVo.setMsg("该申请记录不存在!");
            return resultVo;
        }

        orderChange.setStatus(306);

        Trans.exec(new Atom() {
            @Override
            public void run() {
                dao.update(orderChange);

                insertDetail(orderChange, "买家已经取消申请", orderChange.getStatus());
            }
        });

        resultVo.setMsg("数据保存成功");
        return resultVo;
    }

    /**
     * 退货数据验证
     * 
     * @return
     */
    private ResultVo checkRetrun(OrderChange orderChange) {
        int type = orderChange.getChangeType();// 1:退货， 2：换货
        String str = type == 1 ? "退货" : "换货";
        ResultVo resultVo = new ResultVo();
        resultVo.setStatus(ResultVo.STATUS_FAIL);
        if (orderChange.getNum() <= 0) {
            resultVo.setMsg(str + "商品数量必须大于0!");
            return resultVo;
        }

        if (type == 1 && orderChange.getAmount() <= 0) {
            resultVo.setMsg("退货金额必须大于0!");
            return resultVo;
        }

        if ("".equals(orderChange.getChangeReason())) {
            resultVo.setMsg("请选择" + str + "原因!");
            return resultVo;
        }

        if ("".equals(orderChange.getRemark())) {

            resultVo.setMsg("请输入" + str + "说明!");
            return resultVo;
        }

        OrderChange queryChange = dao.fetch(OrderChange.class,
                Cnd.where("orderId", "=", orderChange.getOrderId()).and("productId", "=", orderChange.getProductId()));

        if (queryChange == null) {
            resultVo.setMsg("该订单已经申请过" + str);
            return resultVo;
        }

        return null;
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
