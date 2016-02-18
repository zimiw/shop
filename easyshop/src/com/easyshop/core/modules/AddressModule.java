package com.easyshop.core.modules;

import java.util.List;

import org.nutz.dao.Cnd;
import org.nutz.dao.Dao;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.mvc.annotation.At;
import org.nutz.mvc.annotation.Fail;
import org.nutz.mvc.annotation.Ok;
import org.nutz.mvc.annotation.Param;

import com.easyshop.bean.Area;
import com.easyshop.bean.City;
import com.easyshop.bean.Province;

/**
 * 地址获取
 * 
 * @author luocz
 */

@IocBean
@At("/address")
@Ok("json")
@Fail("http:500")
public class AddressModule {
	@Inject
	protected Dao dao;

	/**
	 * 获取所有的省
	 * 
	 * @return
	 */
	@At
	public Object getProvinces() {
		List<Province> list = dao.query(Province.class, Cnd.NEW().desc("name"));
		return list;
	}

	/**
	 * 根据省code获取对应的市
	 * 
	 * @param provinceCode
	 * @return
	 */
	@At
	public Object getCitys(@Param("provinceCode") String provinceCode) {
		List<City> list = dao.query(City.class,
				Cnd.where("provincecode", "=", provinceCode).desc("name"));
		return list;
	}

	/**
	 * 根据市code获取对应的区县
	 * 
	 * @param provinceCode
	 * @return
	 */
	@At
	public Object getAreas(@Param("cityCode") String cityCode) {
		List<Area> list = dao.query(Area.class,
				Cnd.where("citycode", "=", cityCode).desc("name"));
		return list;
	}

}
