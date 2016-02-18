package com.easyshop.core.modules;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.nutz.dao.Cnd;
import org.nutz.dao.Dao;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.mvc.annotation.At;
import org.nutz.mvc.annotation.Fail;
import org.nutz.mvc.annotation.Ok;
import org.nutz.mvc.annotation.Param;

import com.easyshop.bean.Catalogs;
import com.easyshop.bean.Functions;

@IocBean
@At("/functions")
@Ok("json")
@Fail("http:500")
public class FunctionsModule {
	@Inject
    protected Dao dao;
	
	private Map<String,Object> result;
	
	@At
	public Object getAllFunctions(@Param("id")int ID){
		this.result=new HashMap<String,Object>();
		List<Functions> catalogs=this.query(ID);
		this.result.put("total",catalogs.size());
		this.result.put("rows",catalogs);
		return this.result;
	}

	private List<Functions> query(int ID){
		//第一次查询，获得所有的顶级对象
		List<Functions> results=dao.query(Functions.class, Cnd.where("id","=",ID).asc("orderId"));
		//第二次查询。获取子级对象
		for(Functions catalog : results){
			this.subquery(catalog, catalog.getId());
		}
		return results;
	}
	private void subquery(Functions catalog,int ID){
		List<Functions> temp = dao.query(Functions.class, Cnd.where("pid","=",catalog.getId()).asc("orderId"));
//		if(temp.size()>0){
			catalog.setSubFunctions(temp);
//		}
		for(Functions subtemp :temp){
			subquery(subtemp,subtemp.getId());
		}
		return;
	}
}
