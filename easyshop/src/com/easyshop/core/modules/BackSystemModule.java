package com.easyshop.core.modules;

import java.util.Map;

import org.nutz.dao.Cnd;
import org.nutz.dao.Dao;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.mvc.annotation.At;
import org.nutz.mvc.annotation.Fail;
import org.nutz.mvc.annotation.Ok;

import com.easyshop.bean.Functions;
import com.easyshop.bean.User;

@IocBean
@At("/system")
@Ok("json")
@Fail("http:500")
public class BackSystemModule {
	@Inject
    protected Dao dao;
	private Map<String,Object> result;

	@At
	public Object getItems(){
		Functions items = dao.fetch(Functions.class, Cnd.where("PID", "=", 0));
		return null;
	}
}
