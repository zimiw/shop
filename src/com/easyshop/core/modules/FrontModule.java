package com.easyshop.core.modules;

import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.mvc.annotation.At;
import org.nutz.mvc.annotation.Fail;
import org.nutz.mvc.annotation.Ok;

@IocBean
@At("")
@Ok("redirect:/front/home.html")
@Fail("http:500")
public class FrontModule {
	@At("/")
	public void home(){
		System.out.println("用户登陆。。。");
	}
}
