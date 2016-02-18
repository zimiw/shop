package com.easyshop.core.modules.admin;

import org.nutz.dao.Cnd;
import org.nutz.dao.Dao;
import org.nutz.dao.Sqls;
import org.nutz.dao.sql.Sql;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;

import com.easyshop.bean.Address;
import com.easyshop.bean.Images;

/**
 * 订单相关公共方法
 * 
 * @author luocz
 *
 */
@IocBean
public class OrderUtil {

	@Inject
	protected Dao dao;

	/**
	 * 根据商品id获取对应是图片
	 * 
	 * @param productId
	 * @param isTopimg
	 *            true:主图片， false不是
	 * @return
	 */
	public Images fetchImg(String productId, Boolean isTopimg) {
		Images imgs = dao.fetch(
				Images.class,
				Cnd.where("productId", "=", productId).and("isTopimg", "=",
						isTopimg));
		return imgs != null ? imgs : new Images();
	}

	/**
	 * 根据地址id 地址信息
	 * 
	 * @param addressId
	 * @return
	 */
	public Address getAdress(int addressId) {

		String sqlStr = "SELECT   (SELECT t.name  FROM province t  WHERE t.code = a.province) province, "
				+ "    (SELECT t.name   FROM city t  WHERE t.code = a.city) city, "
				+ "    (SELECT t.name FROM area t  WHERE t.code = a.district) district, street, addressId, name "
				+ " FROM address a where addressId = @addressId  ";
		Sql sql = Sqls.create(sqlStr);
		sql.params().set("addressId", addressId);
		sql.setCallback(Sqls.callback.maps());
		dao.execute(sql);
		Address address = sql.getObject(Address.class);
		return address;
	}

	public String getAdressDesc(Address address) {
		StringBuffer str = new StringBuffer();
		str.append(address.getProvince());
		str.append(address.getCity());
		str.append(address.getDistrict());
		str.append(address.getStreet());
		return str.toString();
	}

	/**
	 * 根据地址id 获取完整的地址信息
	 * 
	 * @param addressId
	 * @return
	 */
	public String getAdressDesc(int addressId) {

		String sqlStr = "SELECT   (SELECT t.name  FROM province t  WHERE t.code = a.province) province, "
				+ "    (SELECT t.name   FROM city t  WHERE t.code = a.city) city, "
				+ "    (SELECT t.name FROM area t  WHERE t.code = a.district) district, street  FROM address a where addressId = @addressId  ";
		Sql sql = Sqls.create(sqlStr);
		sql.params().set("addressId", addressId);
		sql.setCallback(Sqls.callback.maps());
		dao.execute(sql);
		Address address = sql.getObject(Address.class);
		StringBuffer str = new StringBuffer();
		if (address != null) {
			str.append(address.getProvince());
			str.append(address.getCity());
			str.append(address.getDistrict());
			str.append(address.getStreet());
		}

		return str.toString();
	}
}
