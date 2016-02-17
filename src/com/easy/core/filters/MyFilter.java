package com.easy.core.filters;

import org.nutz.mvc.ActionContext;
import org.nutz.mvc.ActionFilter;
import org.nutz.mvc.View;

public class MyFilter implements ActionFilter {

	@Override
	public View match(ActionContext context) {
		// TODO Auto-generated method stub
		context.getResponse().addHeader("Access-Control-Allow-Origin", "*");
		return null;
	}
	
}
