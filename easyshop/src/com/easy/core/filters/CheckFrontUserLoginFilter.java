package com.easy.core.filters;

import com.easyshop.core.modules.admin.OrderConstant;
import com.easyshop.utils.StringUtils;
import org.nutz.json.JsonFormat;
import org.nutz.mvc.ActionContext;
import org.nutz.mvc.ActionFilter;
import org.nutz.mvc.Mvcs;
import org.nutz.mvc.View;
import org.nutz.mvc.view.UTF8JsonView;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpSession;

/**
 * 〈〉<br>
 *
 * @author 15040635 wzs
 */
public class CheckFrontUserLoginFilter implements ActionFilter {

    /**
     * 过滤入口方法,属于前置过滤
     *
     * @param actionContext 执行上下文
     *
     * @return 如果为null, 则继续执行下一个动作链, 否则将使用它渲染响应
     */
    @Override
    public View match(ActionContext actionContext) {
        HttpSession session = Mvcs.getHttpSession(false);
        if (session == null || null == session.getAttribute(OrderConstant.FRONT_USER_ID)) {

            //看看是否有自动登录的
            Cookie[] cookies = Mvcs.getReq().getCookies();
            if (cookies != null) {
                for (Cookie cookie : cookies) {
                    if (OrderConstant.FRONT_USER_ID.equals(cookie.getName()) &&
                            !StringUtils.isEmpty(cookie.getValue())) {
                        //重新放入session
                        Mvcs.getHttpSession(true).setAttribute("frontUserId", cookie.getValue());
                        return null;
                    }
                }
            }

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