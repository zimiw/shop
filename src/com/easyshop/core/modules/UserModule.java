package com.easyshop.core.modules;

import com.easy.core.filters.CheckBackUserLoginFilter;
import com.easyshop.bean.Personal;
import com.easyshop.bean.Role;
import com.easyshop.bean.User;
import org.nutz.dao.Cnd;
import org.nutz.dao.Dao;
import org.nutz.dao.pager.Pager;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.mvc.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@IocBean
@At("/user")
@Ok("json")
@Fail("http:500")
//@Filters(@By(type=CheckSession.class, args={"backUserId", "/views/login.html"}))
public class UserModule {

	public final static String BACK_USER_ID = "backUserId";// 后台用户sessionId

	@Inject
    protected Dao dao;
	private Map<String,Object> result;
	
	public Map<String, Object> getResult() {
		return result;
	}

	public void setResult(Map<String, Object> result) {
		this.result = result;
	}

	@At
	@Filters({@By(type = CheckBackUserLoginFilter.class)})
    public int count(HttpSession session) {
        System.out.println(session.getAttribute("backUserId"));
        return dao.count(User.class);
    }
	
    /**
     * 在这个方法里暂时没有进行权限的判�
     * @param name
     * @param password
     * @param session
     * @return
     */
	@At
    @Filters
    public Object login(@Param("username")String name, @Param("password")String password, HttpSession session) {
		this.result=new HashMap<String,Object>();
        User user = dao.fetch(User.class, Cnd.where("name", "=", name).and("pwd", "=", password));
        if (user == null) {
        	this.result.put("status", "fail");
        	this.result.put("msg", "该用户不存在");
            return this.result;
        } else {
            session.setAttribute("me", user.getName());
            session.setAttribute("backUserId", user.getUserId());
            if(user.equals("admin")){
            	session.setAttribute("isAdmin","true");
            }else{
            	session.setAttribute("isAdmin","false");
            }
            this.result.put("status", "success");
        	this.result.put("msg", "登陆成功");
        	this.result.put("target","main.html");

            return this.result;
        }
    }

	@At
	public Object currentUser(HttpSession session){
		this.result=new HashMap<String,Object>();
		if(session.getAttribute("me")!=null){
			this.result.put("status","success");
			this.result.put("currentUser",session.getAttribute("me"));
			this.result.put("isAdmin",session.getAttribute("isAdmin"));
		}else{
			this.result.put("status","fail");
		}
		return this.result;
	}
	@At
	public Object getAllUsers(@Param("currentPage")int currentPage,@Param("peerpageRows")int peerpageRows){
		this.result=new HashMap<String,Object>();
		List<User> temp = dao.query(User.class, null);
		this.result.put("total", temp.size());
		Pager pager = dao.createPager(currentPage, peerpageRows);
		List<User> users = dao.query(User.class, null,pager);
		for(User usertemp : users){
			usertemp.setRole(dao.fetch(Role.class, Cnd.where("roleId","=",usertemp.getRoleId())));
		}
		this.result.put("rows", users);
		return this.result;
	}
	@At
	public Object addUser(@Param("username")String name,@Param("password")String pwd,
			@Param("backup")String backup, @Param("status")String status,
			@Param("roleId")int roleId,@Param("type")String type){
		this.result=new HashMap<String,Object>();
		if(name=="" || pwd ==""){
			this.result.put("status", "error");
			this.result.put("msg","用户名或密码为空");
			return this.result;
		}else{
			User user=new User();
			user.setName(name);
			user.setPwd(pwd);
			user.setBackup(backup);
			String typeTemp=((type == "")?"customer":type);
			user.setType(typeTemp);
			String statusTemp=((status == "")?"active":status);
			user.setStatus(statusTemp);
			if(roleId != 0){
				Role roleresult=dao.fetch(Role.class,Cnd.where("roleId", "=", roleId));
				user.setRole(roleresult);
				user.setRoleId(roleId);
				dao.insert(user);
			}else{
				dao.insert(user);
			}
			this.result.put("status", "success");
			return this.result;
		}
	}
	@At
	public Object deleteOne(@Param("userId")int ID){
		this.result=new HashMap<String,Object>();
		try {
			dao.delete(User.class,ID);
			this.result.put("status", "success"); 
		} catch (Exception e) {
			// TODO Auto-generated catch block
			this.result.put("status", "fail");
			this.result.put("msg", e.getMessage());
		}finally{
			return this.result;
		}
	}
	@At
	public Object updateUser(@Param("userId")int ID,@Param("username")String name,@Param("password")String pwd,
			@Param("backup")String backup, @Param("status")String status,
			@Param("roleId")int roleId,@Param("type")String type){
		this.result=new HashMap<String,Object>();
		User user = dao.fetch(User.class,Cnd.where("userId","=",ID));
		user.setBackup(backup);
		user.setName(name);
		user.setRoleId(roleId);
		user.setStatus(status);
		user.setType(type);
		if(dao.update(user)>0){
			this.result.put("status","success");
			return this.result;
		}else{
			this.result.put("status", "fail");
			this.result.put("msg", "更新失败");
			return this.result;
		}
	}

	@At
	@Filters
	public void logout(HttpSession session, HttpServletResponse httpServletResponse,
			HttpServletRequest httpServletRequest)
			throws IOException {
		session.invalidate();
		httpServletResponse.sendRedirect(httpServletRequest.getContextPath() + "/views/login.html");
	}
	@At
    public Object getAllPersonals(@Param("currentPage")int currentPage,@Param("peerpageRows")int peerpageRows){
    	this.result = new HashMap<String, Object>();
    	this.result.put("total", dao.count(Personal.class));
    	Pager pager = dao.createPager(currentPage, peerpageRows);
    	this.result.put("rows", dao.query(Personal.class,null,pager));
    	return this.result;
    }
	@At
	public Object setStatus(@Param("isplayGame")boolean flag,@Param("personalId")int ID){
		this.result = new HashMap<String, Object>();
		Personal p = dao.fetch(Personal.class,Cnd.where("id","=",ID));
		if(flag){
			p.setPlayGame(true);
		}else{
			p.setPlayGame(false);
		}
		if(dao.update(p)>0){
			this.result.put("status", "success");
		}else{
			this.result.put("status", "fail");
			this.result.put("msg", "设置参数失败");
		}
		return this.result;
	}
	
}
