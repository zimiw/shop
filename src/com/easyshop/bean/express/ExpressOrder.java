package com.easyshop.bean.express;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

/**
 * 顺丰请求实体
 * 
 * @author luocz
 */
@XStreamAlias("Order")
public class ExpressOrder {

    @XStreamAsAttribute
    private String orderid;// String(64) 是 客户订单号

    @XStreamAsAttribute
    private String mailno;// String(4000) 条件 顺丰运单号

    @XStreamAsAttribute
    private int is_gen_bill_no;// Number(1) 否 _SYSTEM //是否要求返回顺丰运单号 1：要求 其它为不要求

    @XStreamAsAttribute
    private String j_company; // String(100) 条件 _SYSTEM 寄件方公司名称，如果需要生成电子运单，则为必填。

    @XStreamAsAttribute
    private String j_contact;// String(100) 条件 _SYSTEM 寄件方联系人，如果需要生成电子运单，则为必填。

    @XStreamAsAttribute
    private String j_tel;// String(20) 条件 _SYSTEM 寄件方联系电话，如果需要生成电子 运单，则为必填。

    @XStreamAsAttribute
    private String j_mobile; // String(20) 否 寄件方手机

    @XStreamAsAttribute
    private String j_shippercode; // String(30) 条件 _SYSTEM
                                  // 寄件方国家/城市代码，如果是跨境件，则此字段为必填。

    @XStreamAsAttribute
    private String j_country;// String(30) 否 寄方国家

    @XStreamAsAttribute
    private String j_province; // String(30) 否 _SYSTEM寄件方所在省份 字段填写要求：必须是标准的省名称称谓
                               // 如：广东省，如果是直辖市，请直接传北京、上海等。

    @XStreamAsAttribute
    private String j_city;// String(100) 否 _SYSTEM 寄件方所在城市名称，字段填写要求：必须是标准的城市称谓
                          // 如：深圳市。

    @XStreamAsAttribute
    private String j_county;// String(30) 否 寄件人所在县/区，必须是标准的县/ 区称谓，示例：“福田区”。

    @XStreamAsAttribute
    private String j_address;// String(200) 条件 _SYSTEM 寄件方详细地址，包括省市区;

    @XStreamAsAttribute
    private String j_post_code;// String(25) 条件 寄方邮编，跨境件必填（中国大陆， 港澳台互寄除外）。

    // @ExpressParam(isRequired = true, length = 100)
    @XStreamAsAttribute
    private String d_company;// String(100) 是 到件方公司名称
    // @ExpressParam(isRequired = true, length = 100)
    @XStreamAsAttribute
    private String d_contact;// String(100) 是 到件方联系人;
    // @ExpressParam(isRequired = true, length = 100)
    @XStreamAsAttribute
    private String d_tel;// String(20) 是 到件方联系电话 ;
    // @ExpressParam(length = 100)
    @XStreamAsAttribute
    private String d_mobile;// String(20) 否 到件方手机;
    // @ExpressParam(length = 30)
    @XStreamAsAttribute
    private String d_deliverycode;// String(30) 条件 到件方代码，如果是跨境件，则要传 这个字段，;
    // @ExpressParam(length = 30)
    @XStreamAsAttribute
    private String d_country;// String(30) 否 到方国家;
    // @ExpressParam(isRequired = true, length = 30)
    @XStreamAsAttribute
    private String d_province;// String(30) 否 到件方所在省份
    // @ExpressParam(isRequired = true, length = 100)
    @XStreamAsAttribute
    private String d_city;// String(100) 否 到件方所在城市名称，
    // @ExpressParam(isRequired = true, length = 30)
    @XStreamAsAttribute
    private String d_county;// String(30) 否 到件方所在县/区
    // @ExpressParam(isRequired = true, length = 200)
    @XStreamAsAttribute
    private String d_address;// String(200) 是 到件方详细地址
    // @ExpressParam(length = 5)
    @XStreamAsAttribute
    private String d_post_code;// String(25) 条件; 到方邮编，跨境件必填（中国大陆， 港澳台互寄除外）。
    // @ExpressParam(isRequired = true, length = 20)
    @XStreamAsAttribute
    private String custid;// String(20) 条件 _SYSTEM 顺丰月结卡号
    // @ExpressParam(isRequired = true, length = 1, paramType =
    // ExpressParamType.Number)
    @XStreamAsAttribute
    private int pay_method;// Number(1) 否 1 _SYSTEM 付款方式：  1:寄方付  2:收方付 3:第三方付
    // @ExpressParam(isRequired = true, length = 5)
    @XStreamAsAttribute
    private String express_type;// String(5) 否 1 _SYSTEM 快件产品类别
    // @ExpressParam(length = 5, paramType = ExpressParamType.Number)
    @XStreamAsAttribute
    private int parcel_quantity;// Number(5) 否 1 包裹数，一个包裹对应一个运单号
    // @ExpressParam(length = 10, paramType = ExpressParamType.Number)
    @XStreamAsAttribute
    private float cargo_length;// Number(10, 3) 否 客户订单货物总长
    // @ExpressParam(length = 10, paramType = ExpressParamType.Number)
    @XStreamAsAttribute
    private float cargo_width;// Number(10, 3) 否 客户订单货物总宽
    // @ExpressParam(length = 10, paramType = ExpressParamType.Number)
    @XStreamAsAttribute
    private float cargo_height;// Number(10, 3) 否 客户订单货物总高
    // @ExpressParam(length = 17, paramType = ExpressParamType.Number)
    @XStreamAsAttribute
    private float volume;// Number(17,3) 否 订单货物总体积
    // @ExpressParam(length = 20, paramType = ExpressParamType.Number)
    @XStreamAsAttribute
    private float cargo_total_weight;// Number(10,3) 否 订单货物总重量;
    // @ExpressParam(length = 15, paramType = ExpressParamType.Number)
    @XStreamAsAttribute
    private float declared_value;// Number(15, 3) 条件 客户订单货物总声明价值
    // @ExpressParam(length = 5)
    @XStreamAsAttribute
    private String declared_value_currency;// String(5) 否 如果目的 地是中国 大陆的， 则默认为
                                           // CNY，否 则默认为 USD
    // @ExpressParam(length = 20)
    @XStreamAsAttribute
    private String customs_batchs;// String(20) 否 _SYSTEM 报关批次
    // @ExpressParam(length = 20)
    @XStreamAsAttribute
    private String sendstarttime;// Date 否 BSP 接收 到 XML 报文的时 间
                                 // 要求上门取件开始时间，格式：YYYYMM-DD HH24:MM:SS
    // @ExpressParam(length = 1, paramType = ExpressParamType.Number)
    @XStreamAsAttribute
    private int is_docall;// Number(1) 否 _SYSTEM 是否要求通过是否手持终端通知顺丰 收派员收件：
    // @ExpressParam(length = 2)
    @XStreamAsAttribute
    private String need_return_tracking_no;// String(2) 否
    // @ExpressParam(length = 30)
    @XStreamAsAttribute
    private String return_tracking;// String(30) 否 顺丰签回单服务运单号

    // @ExpressParam(isRequired = true, length = 30)
    @XStreamAsAttribute
    private String d_tax_no;// String(30) 否 收件人税号
    // @ExpressParam(length = 5)
    @XStreamAsAttribute
    private String tax_pay_type;// String(5) 否 税金付款方式：  1：寄付  2：到付
    // @ExpressParam(length = 30)
    @XStreamAsAttribute
    private String tax_set_accounts;// String(30) 否 税金结算账号
    // @ExpressParam(length = 100)
    @XStreamAsAttribute
    private String original_number;// String(100) 否 电商原始订单号
    // @ExpressParam(length = 100)
    @XStreamAsAttribute
    private String payment_tool;// String(100) 否 支付工具
    // @ExpressParam(length = 100)
    @XStreamAsAttribute
    private String payment_number;// String(100) 否 支付号码
    // @ExpressParam(length = 100)
    @XStreamAsAttribute
    private String goods_code;// String(100) 否 商品编号;
    // @ExpressParam(length = 100)
    @XStreamAsAttribute
    private String in_process_waybill_no;// String(100) 否 头程运单号
    // @ExpressParam(length = 600)
    @XStreamAsAttribute
    private String brand;// String(600) 否 货物品牌
    // @ExpressParam(length = 600)
    @XStreamAsAttribute
    private String specifications;// String(600) 否 货物规格型号
    // @ExpressParam(length = 1, paramType = ExpressParamType.Number)
    @XStreamAsAttribute
    private int temp_range;// Number(1,0) 条件 温度范围类型
    // @ExpressParam(length = 100)
    @XStreamAsAttribute
    private String order_name;// String(100) 否 客户订单下单人姓名
    // @ExpressParam(length = 100)
    @XStreamAsAttribute
    private String order_cert_type;// String(100) 否 客户订单下单人证件类型
    // @ExpressParam(length = 100)
    @XStreamAsAttribute
    private String order_cert_no;// String(100) 否 客户订单下单人证件号
    // @ExpressParam(length = 50)
    @XStreamAsAttribute
    private String order_source;// String(50) 否 客户订单来源
    // @ExpressParam(length = 30)
    @XStreamAsAttribute
    private String template;// String(30) 否业务模板编码，业务模板指 BSP 针对
    // @ExpressParam(length = 100)
    @XStreamAsAttribute
    private String remark;// String(100) 否 备注
    // @ExpressParam(length = 1, paramType = ExpressParamType.Number)
    @XStreamAsAttribute
    private int oneself_pickup_flg;// Number(1) 否 0 快件自取；

    private Cargo Cargo;

    public Cargo getCargo() {
        return Cargo;
    }

    public void setCargo(Cargo cargo) {
        Cargo = cargo;
    }

    public String getOrderid() {
        return orderid;
    }

    public void setOrderid(String orderid) {
        this.orderid = orderid;
    }

    public String getMailno() {
        return mailno;
    }

    public void setMailno(String mailno) {
        this.mailno = mailno;
    }

    public int getIs_gen_bill_no() {
        return is_gen_bill_no;
    }

    public void setIs_gen_bill_no(int is_gen_bill_no) {
        this.is_gen_bill_no = is_gen_bill_no;
    }

    public String getJ_company() {
        return j_company;
    }

    public void setJ_company(String j_company) {
        this.j_company = j_company;
    }

    public String getJ_contact() {
        return j_contact;
    }

    public void setJ_contact(String j_contact) {
        this.j_contact = j_contact;
    }

    public String getJ_tel() {
        return j_tel;
    }

    public void setJ_tel(String j_tel) {
        this.j_tel = j_tel;
    }

    public String getJ_mobile() {
        return j_mobile;
    }

    public void setJ_mobile(String j_mobile) {
        this.j_mobile = j_mobile;
    }

    public String getJ_shippercode() {
        return j_shippercode;
    }

    public void setJ_shippercode(String j_shippercode) {
        this.j_shippercode = j_shippercode;
    }

    public String getJ_country() {
        return j_country;
    }

    public void setJ_country(String j_country) {
        this.j_country = j_country;
    }

    public String getJ_province() {
        return j_province;
    }

    public void setJ_province(String j_province) {
        this.j_province = j_province;
    }

    public String getJ_city() {
        return j_city;
    }

    public void setJ_city(String j_city) {
        this.j_city = j_city;
    }

    public String getJ_county() {
        return j_county;
    }

    public void setJ_county(String j_county) {
        this.j_county = j_county;
    }

    public String getJ_address() {
        return j_address;
    }

    public void setJ_address(String j_address) {
        this.j_address = j_address;
    }

    public String getJ_post_code() {
        return j_post_code;
    }

    public void setJ_post_code(String j_post_code) {
        this.j_post_code = j_post_code;
    }

    public String getD_company() {
        return d_company;
    }

    public void setD_company(String d_company) {
        this.d_company = d_company;
    }

    public String getD_contact() {
        return d_contact;
    }

    public void setD_contact(String d_contact) {
        this.d_contact = d_contact;
    }

    public String getD_tel() {
        return d_tel;
    }

    public void setD_tel(String d_tel) {
        this.d_tel = d_tel;
    }

    public String getD_mobile() {
        return d_mobile;
    }

    public void setD_mobile(String d_mobile) {
        this.d_mobile = d_mobile;
    }

    public String getD_deliverycode() {
        return d_deliverycode;
    }

    public void setD_deliverycode(String d_deliverycode) {
        this.d_deliverycode = d_deliverycode;
    }

    public String getD_country() {
        return d_country;
    }

    public void setD_country(String d_country) {
        this.d_country = d_country;
    }

    public String getD_province() {
        return d_province;
    }

    public void setD_province(String d_province) {
        this.d_province = d_province;
    }

    public String getD_city() {
        return d_city;
    }

    public void setD_city(String d_city) {
        this.d_city = d_city;
    }

    public String getD_county() {
        return d_county;
    }

    public void setD_county(String d_county) {
        this.d_county = d_county;
    }

    public String getD_address() {
        return d_address;
    }

    public void setD_address(String d_address) {
        this.d_address = d_address;
    }

    public String getD_post_code() {
        return d_post_code;
    }

    public void setD_post_code(String d_post_code) {
        this.d_post_code = d_post_code;
    }

    public String getCustid() {
        return custid;
    }

    public void setCustid(String custid) {
        this.custid = custid;
    }

    public int getPay_method() {
        return pay_method;
    }

    public void setPay_method(int pay_method) {
        this.pay_method = pay_method;
    }

    public String getExpress_type() {
        return express_type;
    }

    public void setExpress_type(String express_type) {
        this.express_type = express_type;
    }

    public int getParcel_quantity() {
        return parcel_quantity;
    }

    public void setParcel_quantity(int parcel_quantity) {
        this.parcel_quantity = parcel_quantity;
    }

    public float getCargo_length() {
        return cargo_length;
    }

    public void setCargo_length(float cargo_length) {
        this.cargo_length = cargo_length;
    }

    public float getCargo_width() {
        return cargo_width;
    }

    public void setCargo_width(float cargo_width) {
        this.cargo_width = cargo_width;
    }

    public float getCargo_height() {
        return cargo_height;
    }

    public void setCargo_height(float cargo_height) {
        this.cargo_height = cargo_height;
    }

    public float getVolume() {
        return volume;
    }

    public void setVolume(float volume) {
        this.volume = volume;
    }

    public float getCargo_total_weight() {
        return cargo_total_weight;
    }

    public void setCargo_total_weight(float cargo_total_weight) {
        this.cargo_total_weight = cargo_total_weight;
    }

    public float getDeclared_value() {
        return declared_value;
    }

    public void setDeclared_value(float declared_value) {
        this.declared_value = declared_value;
    }

    public String getDeclared_value_currency() {
        return declared_value_currency;
    }

    public void setDeclared_value_currency(String declared_value_currency) {
        this.declared_value_currency = declared_value_currency;
    }

    public String getCustoms_batchs() {
        return customs_batchs;
    }

    public void setCustoms_batchs(String customs_batchs) {
        this.customs_batchs = customs_batchs;
    }

    public String getSendstarttime() {
        return sendstarttime;
    }

    public void setSendstarttime(String sendstarttime) {
        this.sendstarttime = sendstarttime;
    }

    public int getIs_docall() {
        return is_docall;
    }

    public void setIs_docall(int is_docall) {
        this.is_docall = is_docall;
    }

    public String getNeed_return_tracking_no() {
        return need_return_tracking_no;
    }

    public void setNeed_return_tracking_no(String need_return_tracking_no) {
        this.need_return_tracking_no = need_return_tracking_no;
    }

    public String getReturn_tracking() {
        return return_tracking;
    }

    public void setReturn_tracking(String return_tracking) {
        this.return_tracking = return_tracking;
    }

    public String getD_tax_no() {
        return d_tax_no;
    }

    public void setD_tax_no(String d_tax_no) {
        this.d_tax_no = d_tax_no;
    }

    public String getTax_pay_type() {
        return tax_pay_type;
    }

    public void setTax_pay_type(String tax_pay_type) {
        this.tax_pay_type = tax_pay_type;
    }

    public String getTax_set_accounts() {
        return tax_set_accounts;
    }

    public void setTax_set_accounts(String tax_set_accounts) {
        this.tax_set_accounts = tax_set_accounts;
    }

    public String getOriginal_number() {
        return original_number;
    }

    public void setOriginal_number(String original_number) {
        this.original_number = original_number;
    }

    public String getPayment_tool() {
        return payment_tool;
    }

    public void setPayment_tool(String payment_tool) {
        this.payment_tool = payment_tool;
    }

    public String getPayment_number() {
        return payment_number;
    }

    public void setPayment_number(String payment_number) {
        this.payment_number = payment_number;
    }

    public String getGoods_code() {
        return goods_code;
    }

    public void setGoods_code(String goods_code) {
        this.goods_code = goods_code;
    }

    public String getIn_process_waybill_no() {
        return in_process_waybill_no;
    }

    public void setIn_process_waybill_no(String in_process_waybill_no) {
        this.in_process_waybill_no = in_process_waybill_no;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getSpecifications() {
        return specifications;
    }

    public void setSpecifications(String specifications) {
        this.specifications = specifications;
    }

    public int getTemp_range() {
        return temp_range;
    }

    public void setTemp_range(int temp_range) {
        this.temp_range = temp_range;
    }

    public String getOrder_name() {
        return order_name;
    }

    public void setOrder_name(String order_name) {
        this.order_name = order_name;
    }

    public String getOrder_cert_type() {
        return order_cert_type;
    }

    public void setOrder_cert_type(String order_cert_type) {
        this.order_cert_type = order_cert_type;
    }

    public String getOrder_cert_no() {
        return order_cert_no;
    }

    public void setOrder_cert_no(String order_cert_no) {
        this.order_cert_no = order_cert_no;
    }

    public String getOrder_source() {
        return order_source;
    }

    public void setOrder_source(String order_source) {
        this.order_source = order_source;
    }

    public String getTemplate() {
        return template;
    }

    public void setTemplate(String template) {
        this.template = template;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public int getOneself_pickup_flg() {
        return oneself_pickup_flg;
    }

    public void setOneself_pickup_flg(int oneself_pickup_flg) {
        this.oneself_pickup_flg = oneself_pickup_flg;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("Order [orderid=");
        builder.append(orderid);
        builder.append(", mailno=");
        builder.append(mailno);
        builder.append(", is_gen_bill_no=");
        builder.append(is_gen_bill_no);
        builder.append(", j_company=");
        builder.append(j_company);
        builder.append(", j_contact=");
        builder.append(j_contact);
        builder.append(", j_tel=");
        builder.append(j_tel);
        builder.append(", j_mobile=");
        builder.append(j_mobile);
        builder.append(", j_shippercode=");
        builder.append(j_shippercode);
        builder.append(", j_country=");
        builder.append(j_country);
        builder.append(", j_province=");
        builder.append(j_province);
        builder.append(", j_city=");
        builder.append(j_city);
        builder.append(", j_county=");
        builder.append(j_county);
        builder.append(", j_address=");
        builder.append(j_address);
        builder.append(", j_post_code=");
        builder.append(j_post_code);
        builder.append(", d_company=");
        builder.append(d_company);
        builder.append(", d_contact=");
        builder.append(d_contact);
        builder.append(", d_tel=");
        builder.append(d_tel);
        builder.append(", d_mobile=");
        builder.append(d_mobile);
        builder.append(", d_deliverycode=");
        builder.append(d_deliverycode);
        builder.append(", d_country=");
        builder.append(d_country);
        builder.append(", d_province=");
        builder.append(d_province);
        builder.append(", d_city=");
        builder.append(d_city);
        builder.append(", d_county=");
        builder.append(d_county);
        builder.append(", d_address=");
        builder.append(d_address);
        builder.append(", d_post_code=");
        builder.append(d_post_code);
        builder.append(", custid=");
        builder.append(custid);
        builder.append(", pay_method=");
        builder.append(pay_method);
        builder.append(", express_type=");
        builder.append(express_type);
        builder.append(", parcel_quantity=");
        builder.append(parcel_quantity);
        builder.append(", cargo_length=");
        builder.append(cargo_length);
        builder.append(", cargo_width=");
        builder.append(cargo_width);
        builder.append(", cargo_height=");
        builder.append(cargo_height);
        builder.append(", volume=");
        builder.append(volume);
        builder.append(", cargo_total_weight=");
        builder.append(cargo_total_weight);
        builder.append(", declared_value=");
        builder.append(declared_value);
        builder.append(", declared_value_currency=");
        builder.append(declared_value_currency);
        builder.append(", customs_batchs=");
        builder.append(customs_batchs);
        builder.append(", sendstarttime=");
        builder.append(sendstarttime);
        builder.append(", is_docall=");
        builder.append(is_docall);
        builder.append(", need_return_tracking_no=");
        builder.append(need_return_tracking_no);
        builder.append(", return_tracking=");
        builder.append(return_tracking);
        builder.append(", d_tax_no=");
        builder.append(d_tax_no);
        builder.append(", tax_pay_type=");
        builder.append(tax_pay_type);
        builder.append(", tax_set_accounts=");
        builder.append(tax_set_accounts);
        builder.append(", original_number=");
        builder.append(original_number);
        builder.append(", payment_tool=");
        builder.append(payment_tool);
        builder.append(", payment_number=");
        builder.append(payment_number);
        builder.append(", goods_code=");
        builder.append(goods_code);
        builder.append(", in_process_waybill_no=");
        builder.append(in_process_waybill_no);
        builder.append(", brand=");
        builder.append(brand);
        builder.append(", specifications=");
        builder.append(specifications);
        builder.append(", temp_range=");
        builder.append(temp_range);
        builder.append(", order_name=");
        builder.append(order_name);
        builder.append(", order_cert_type=");
        builder.append(order_cert_type);
        builder.append(", order_cert_no=");
        builder.append(order_cert_no);
        builder.append(", order_source=");
        builder.append(order_source);
        builder.append(", template=");
        builder.append(template);
        builder.append(", remark=");
        builder.append(remark);
        builder.append(", oneself_pickup_flg=");
        builder.append(oneself_pickup_flg);
        builder.append(", Cargo=");
        builder.append(Cargo);
        builder.append("]");
        return builder.toString();
    }

}
