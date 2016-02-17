package com.easyshop.core.modules;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.nutz.dao.Cnd;
import org.nutz.dao.Dao;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.mvc.annotation.At;
import org.nutz.mvc.annotation.Fail;
import org.nutz.mvc.annotation.Ok;
import org.nutz.mvc.annotation.Param;

import com.alibaba.druid.sql.visitor.functions.Function;
import com.easyshop.bean.ConnectorFR;
import com.easyshop.bean.Functions;
import com.easyshop.bean.Role;
import com.easyshop.bean.User;

@IocBean
@At("/role")
@Ok("json")
@Fail("http:500")
public class RoleModule {

	@Inject
    protected Dao dao;
	
	private Map<String,Object> result;
	
	@At
	public Object getRoleLists(){
		this.result=new HashMap<String,Object>();
		List<Role> roles = dao.query(Role.class, null);
		this.result.put("total", roles.size());
		this.result.put("rows", roles);
		return this.result;
	}
	@At
	public Object addRole(@Param("rolename")String name,@Param("roleDescp")String descp){
		this.result=new HashMap<String,Object>();
		if(name.equals("superAdmin")){
			this.result.put("status", "fail");
			this.result.put("msg","与超极管理员重名！");
			return this.result;
		}
		Role role = new Role();
		role.setName(name);
		role.setDescp(descp);
		//进行插入操作
		if(dao.insert(role)!=null){
			//插入成功
			this.result.put("status","success");
			return this.result;
		}else{
			//插入失败
			this.result.put("status", "fail");
			this.result.put("msg", "操作失败");
			return this.result;
		}
	}
	@At
	public Object deleteOne(@Param("roleId")int ID){
		//在这里需要注意的是一旦角色被删除，与其相关联的用户也要进行关联更新
		this.result=new HashMap<String,Object>();
		List<User> users=dao.query(User.class,Cnd.where("roleId","=",ID));
		for(User temp : users){
			temp.setRoleId(0);
			dao.update(temp);
		}
		if(dao.delete(Role.class,ID)>0){
			this.result.put("status", "success");
		}else{
			this.result.put("status", "fail");
			this.result.put("msg", "删除失败");
		}
		return this.result;
	}
	@At
	public Object updateRole(@Param("roleId")int ID,@Param("rolename")String name,@Param("roleDescp")String descp){
		this.result=new HashMap<String,Object>();
		Role role=dao.fetch(Role.class,Cnd.where("roleId","=",ID));
		role.setName(name);
		role.setDescp(descp);
		if(dao.update(role)>0){
			this.result.put("status","success");
		}else{
			this.result.put("status", "fail");
			this.result.put("msg", "更新失败");
		}
		return this.result;
	}

	@At
	public Object addRolePrivilege(@Param("roleId")int roleId,@Param("privilegeIds")String privilegeIds){
		this.result=new HashMap<String,Object>();
		String[] priIds=privilegeIds.split("@");
		//在插入之前先删除
		dao.clear(ConnectorFR.class,Cnd.where("roleId","=",roleId));
		Role role= dao.fetch(Role.class,Cnd.where("roleId","=",roleId));
		List<Functions> funs=dao.query(Functions.class, Cnd.where("id","in",priIds));
		for(String id : priIds){
			ConnectorFR cfr=new ConnectorFR();
			cfr.setRoleId(roleId);
			cfr.setFunctionId(Integer.parseInt(id));
			dao.insert(cfr);
		}
		this.result.put("status", "success");
		return this.result;
	}
	
	@At
	public Object getRolePrivileges(@Param("userName")String userName){
		this.result=new HashMap<String,Object>();
		User user = dao.fetch(User.class,Cnd.where("name","=",userName));
		Role role = dao.fetch(Role.class,Cnd.where("roleId","=",user.getRoleId()));
		List<ConnectorFR> conns= dao.query(ConnectorFR.class,Cnd.where("roleId","=",user.getRoleId()));
		String[] priIds=new String[conns.size()];
		int i = 0;
		for(ConnectorFR conn : conns){
			priIds[i]=String.valueOf(conn.getFunctionId());
			i++;
		}
		List<Functions> funs = dao.query(Functions.class, Cnd.where("id","in",priIds).asc("orderId"));
		List<Functions> results= new ArrayList<Functions>();
		for(Functions fs : funs){
			List<Functions> fun = this.query(fs.getId());
			results.add(fun.get(0));
		}
		this.result.put("rows",results);
		return this.result;
	}
	
	@At
	public Object getCurrentFuns(@Param("roleId")int ID){
		this.result=new HashMap<String,Object>();
		List<ConnectorFR> conns= dao.query(ConnectorFR.class,Cnd.where("roleId","=",ID));
		if(conns.size()>0){
			String[] priIds=new String[conns.size()];
			int i = 0;
			for(ConnectorFR conn : conns){
				priIds[i]=String.valueOf(conn.getFunctionId());
				i++;
			}
			List<Functions> funs = dao.query(Functions.class, Cnd.where("id","in",priIds));
			this.result.put("rows", funs);
		}
		return this.result;
	}
	private List<Functions> query(int ID){
		//第一次查询，获得所有的顶级对象
		List<Functions> results=dao.query(Functions.class, Cnd.where("id","=",ID).asc("orderId"));
		//第二次查询。获取子级对象
		for(Functions catalog : results){
			this.subquery(catalog, catalog.getId());
		}
		return results;
	}
	private void subquery(Functions catalog,int ID){
		List<Functions> temp = dao.query(Functions.class, Cnd.where("pid","=",catalog.getId()).asc("orderId"));
//		if(temp.size()>0){
			catalog.setSubFunctions(temp);
//		}
		for(Functions subtemp :temp){
			subquery(subtemp,subtemp.getId());
		}
		return;
	}
}
