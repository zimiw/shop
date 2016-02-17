package com.easy.core.filters;

import com.easyshop.core.modules.UserModule;
import org.nutz.json.JsonFormat;
import org.nutz.mvc.ActionContext;
import org.nutz.mvc.ActionFilter;
import org.nutz.mvc.Mvcs;
import org.nutz.mvc.View;
import org.nutz.mvc.view.UTF8JsonView;

import javax.servlet.http.HttpSession;

/**
 * 〈〉<br>
 *
 * @author 15040635 wzs
 */
public class CheckBackUserLoginFilter implements ActionFilter {

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
        if (session == null || null == session.getAttribute(UserModule.BACK_USER_ID)) {
            UTF8JsonView utf8JsonView = new UTF8JsonView(JsonFormat.forLook());
            //utf8JsonView.setData(new ResultVo("fail", "后台用户未登录"));
            utf8JsonView.setData("{\"status\":\"fail\",\"target\":\"/views/login.html\"}");
            return utf8JsonView;
        }
        System.out.println("run here"+"<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<");
        return null;
    }
}