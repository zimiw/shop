package com.easyshop.core.modules;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.nutz.dao.Cnd;
import org.nutz.dao.Dao;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.mvc.annotation.At;
import org.nutz.mvc.annotation.By;
import org.nutz.mvc.annotation.Fail;
import org.nutz.mvc.annotation.Filters;
import org.nutz.mvc.annotation.Ok;
import org.nutz.mvc.annotation.Param;

import com.easy.core.filters.CheckBackUserLoginFilter;
import com.easyshop.bean.Functions;
import com.easyshop.core.modules.admin.OrderConstant;

@IocBean
@At("/functions")
@Ok("json")
@Fail("http:500")
@Filters({ @By(type = CheckBackUserLoginFilter.class) })
public class FunctionsModule {
	@Inject
	protected Dao dao;

	private Map<String, Object> result;

	/**
	 * 获取登录用户菜单
	 * 
	 * @return
	 */
	@At
	public Object getUserFunctions(HttpSession session) {

		String userId = String.valueOf(session
				.getAttribute(OrderConstant.FRONT_USER_ID));

		List<Functions> list = dao
				.query(Functions.class,
						Cnd.wrap(" url is not null and pid !=0 "
								+ "	and id in (select f.functionId from connectorFR f, user u "
								+ " where f.roleId = u.roleId and u.userId = '"
								+ userId + "')"));
		return list;
	}

	@At
	public Object getAllFunctions(@Param("id") int ID) {
		this.result = new HashMap<String, Object>();
		List<Functions> catalogs = this.query(ID);
		this.result.put("total", catalogs.size());
		this.result.put("rows", catalogs);
		return this.result;
	}

	@At
	public Object queryAllFunctions() {
		this.result = new HashMap<String, Object>();
		List<Functions> functions = dao.query(Functions.class, null);
		this.result.put("total", functions.size());
		this.result.put("rows", functions);
		return this.result;
	}

	private List<Functions> query(int ID) {
		// 第一次查询，获得所有的顶级对象
		List<Functions> results = dao.query(Functions.class,
				Cnd.where("id", "=", ID).asc("orderId"));
		// 第二次查询。获取子级对象
		for (Functions catalog : results) {
			this.subquery(catalog, catalog.getId());
		}
		return results;
	}

	private void subquery(Functions catalog, int ID) {
		List<Functions> temp = dao.query(Functions.class,
				Cnd.where("pid", "=", catalog.getId()).asc("orderId"));
		// if(temp.size()>0){
		catalog.setSubFunctions(temp);
		// }
		for (Functions subtemp : temp) {
			subquery(subtemp, subtemp.getId());
		}
		return;
	}
}
