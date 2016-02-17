package com.easyshop.core.modules;

import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.mvc.annotation.At;
import org.nutz.mvc.annotation.Fail;
import org.nutz.mvc.annotation.Ok;

@IocBean
@At("/back")
@Ok("redirect:/views/login.html")
@Fail("http:500")
public class LoginModule {
	@At
	public void login(){
		System.out.println("用户登陆。。。");
	}
}
