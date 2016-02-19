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
import javax.servlet.http.HttpSession;

import org.nutz.mvc.Mvcs;
import org.nutz.mvc.RequestPath;

import com.easyshop.core.modules.UserModule;
import com.easyshop.core.modules.admin.OrderConstant;
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
		System.out.println(antPath.match("/front/personal_*.html",
				"/front/personal_accountManage.html"));
	}

	@Override
	public void doFilter(ServletRequest req, ServletResponse resp,
			FilterChain chain) throws IOException, ServletException {

		HttpServletRequest request = (HttpServletRequest) req;
		HttpServletResponse response = (HttpServletResponse) resp;
		RequestPath path = Mvcs.getRequestPathObject(request);
		String matchUrl = path.getUrl();
//		String str = matchUrl.substring(matchUrl.indexOf("."), matchUrl.length());
		if(antPath.match("/views/login.html", matchUrl)||  antPath.match("/front/login.html", matchUrl) ){//前后台登录界面
		    
		}else if (antPath.match("/views/**/*.html", matchUrl)) {// 后台管理页面
		    HttpSession session = request.getSession();
		    if (session == null || null == session.getAttribute(UserModule.BACK_USER_ID)) {
		      response.sendRedirect(request.getContextPath()+ "/views/login.html");
		      return ;
		    }
		}else if (antPath.match("/front/personal_*.html",matchUrl)) {// 前台管理页面
            HttpSession session = request.getSession();
            if (session == null || null == session.getAttribute(OrderConstant.FRONT_USER_ID)) {
                response.sendRedirect(request.getContextPath()+ "/front/login.html");
                return ;
            }
        }

		chain.doFilter(req, response);
	}

	@Override
	public void init(FilterConfig arg0) throws ServletException {
		// TODO Auto-generated method stub

	}

}
