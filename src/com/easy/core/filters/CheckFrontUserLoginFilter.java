package com.easy.core.filters;

import javax.servlet.http.HttpSession;

import org.nutz.json.JsonFormat;
import org.nutz.mvc.ActionContext;
import org.nutz.mvc.ActionFilter;
import org.nutz.mvc.Mvcs;
import org.nutz.mvc.View;
import org.nutz.mvc.view.UTF8JsonView;

import com.easyshop.core.modules.admin.OrderConstant;

/**
 * 〈〉<br>
 *
 * @author 15040635 wzs
 */
public class CheckFrontUserLoginFilter implements ActionFilter {

	/**
	 * 过滤入口方法,属于前置过滤
	 *
	 * @param actionContext
	 *            执行上下文
	 *
	 * @return 如果为null, 则继续执行下一个动作链, 否则将使用它渲染响应
	 */
	@Override
	public View match(ActionContext actionContext) {
		HttpSession session = Mvcs.getHttpSession(false);
		if (session == null
				|| null == session.getAttribute(OrderConstant.FRONT_USER_ID)) {
			UTF8JsonView utf8JsonView = new UTF8JsonView(JsonFormat.forLook());
			// utf8JsonView.setData(new ResultVo("fail", "前台用户未登录"));
			utf8JsonView
					.setData("{\"status\":\"fail\",\"target\":\"login.html\"}");
			return utf8JsonView;
		}
		System.out.println("run here"
				+ "<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<");
		return null;
	}
}