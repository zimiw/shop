package com.easyshop.bean.express;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

/**
 * 顺丰接口实体 --货物
 * 
 * @author luocz
 */
@XStreamAlias("Cargo")
public class Cargo {

	@XStreamAsAttribute
	private String name;// String(60) 是 货物名称，如果需要生成电子运单， 则为必填。

	@XStreamAsAttribute
	private int count;// Number(5) 条件 货物数量 跨境件报关需要填写

	@XStreamAsAttribute
	private String unit;// String(30) 条件 货物单位，如：个、台、本，跨境件 报关需要填写

	@XStreamAsAttribute
	private float weight;// Number(16,3) 条件 订单货物单位重量

	@XStreamAsAttribute
	private float amount;// Number(17,3) 条件 货物单价，精确到小数点后 3 位，跨 境件报关需要填写。

	@XStreamAsAttribute
	private String currency;// String(5) 条件 货物单价的币别 CNY: 人民币

	@XStreamAsAttribute
	private String source_area;// String(5) 条件 原产地国别

	@XStreamAsAttribute
	private String product_record_no;// String(18) 否 货物产品国检备案编号

	@XStreamAsAttribute
	private String good_prepard_no;// String(100) 否 商品海关备案号

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public String getUnit() {
		return unit;
	}

	public void setUnit(String unit) {
		this.unit = unit;
	}

	public float getWeight() {
		return weight;
	}

	public void setWeight(float weight) {
		this.weight = weight;
	}

	public float getAmount() {
		return amount;
	}

	public void setAmount(float amount) {
		this.amount = amount;
	}

	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}

	public String getSource_area() {
		return source_area;
	}

	public void setSource_area(String source_area) {
		this.source_area = source_area;
	}

	public String getProduct_record_no() {
		return product_record_no;
	}

	public void setProduct_record_no(String product_record_no) {
		this.product_record_no = product_record_no;
	}

	public String getGood_prepard_no() {
		return good_prepard_no;
	}

	public void setGood_prepard_no(String good_prepard_no) {
		this.good_prepard_no = good_prepard_no;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Cargo [name=");
		builder.append(name);
		builder.append(", count=");
		builder.append(count);
		builder.append(", unit=");
		builder.append(unit);
		builder.append(", weight=");
		builder.append(weight);
		builder.append(", amount=");
		builder.append(amount);
		builder.append(", currency=");
		builder.append(currency);
		builder.append(", source_area=");
		builder.append(source_area);
		builder.append(", product_record_no=");
		builder.append(product_record_no);
		builder.append(", good_prepard_no=");
		builder.append(good_prepard_no);
		builder.append("]");
		return builder.toString();
	}

}
