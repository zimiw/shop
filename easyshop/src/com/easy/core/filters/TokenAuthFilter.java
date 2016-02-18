package com.easy.core.filters;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.nutz.mvc.Mvcs;
import org.nutz.mvc.RequestPath;

import com.easyshop.utils.AntPathMatcher;

/**
 * 系统过滤
 * 
 * @author luocz
 */
public class TokenAuthFilter implements Filter {

	private AntPathMatcher antPath = new AntPathMatcher();

	@Override
	public void destroy() {
		// TODO Auto-generated method stub

	}

	public static void main(String[] args) {

		AntPathMatcher antPath = new AntPathMatcher();
		System.out.println(antPath.match("/views/**/*.html",
				"/views/system/productManage.html"));
	}

	@Override
	public void doFilter(ServletRequest req, ServletResponse resp,
			FilterChain chain) throws IOException, ServletException {

		HttpServletRequest request = (HttpServletRequest) req;
		HttpServletResponse response = (HttpServletResponse) resp;
		RequestPath path = Mvcs.getRequestPathObject(request);
		String matchUrl = path.getUrl();
		System.out.println("1111" + matchUrl);

		if (!"/views/login.html".equals(matchUrl)
				&& antPath.match("/views/**/*.html", matchUrl)) {// 后台管理页面

			// response.sendRedirect(request.getContextPath()
			// + "/views/login.html");
		}

		chain.doFilter(req, response);
	}

	@Override
	public void init(FilterConfig arg0) throws ServletException {
		// TODO Auto-generated method stub

	}

}
