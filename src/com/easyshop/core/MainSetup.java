package com.easyshop.core;

import com.easyshop.bean.Personal;
import org.nutz.dao.Cnd;
import org.nutz.dao.Dao;
import org.nutz.dao.util.Daos;
import org.nutz.ioc.Ioc;
import org.nutz.mvc.NutConfig;
import org.nutz.mvc.Setup;

import com.easyshop.bean.Catalogs;
import com.easyshop.bean.Role;
import com.easyshop.bean.User;

public class MainSetup implements Setup {
	@Override
	public void destroy(NutConfig conf) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void init(NutConfig conf) {
		// TODO Auto-generated method stub
		Ioc ioc = conf.getIoc();
        Dao dao = ioc.get(Dao.class);
        Daos.createTablesInPackage(dao, "com.easyshop.bean", false);
     // 初始化默认根用户
        if (dao.count(User.class) == 0 && dao.count(Role.class)==0 && dao.count(Catalogs.class)==0) {
            User user = new User();
            user.setName("admin");
            user.setPwd("123456");
            user.setBackup("超级管理员");
            user.setStatus("active");
            user.setType("superAdmin");
            Role role=new Role();
            role.setName("superAdmin");
            role.setDescp("超级管理员");
            Role roletemp=dao.insert(role);
            user.setRoleId(roletemp.getRoleId());
            dao.insertWith(user,"role");
            Catalogs catalog=new Catalogs();
            catalog.setCatalogName("商品分类");
            catalog.setPid(0);
            dao.insert(catalog);

        }
        //初始化前台用�
        if (dao.fetch(Personal.class, Cnd.where("phone", "=", "1"))==null) {
            Personal personal=new Personal();
            personal.setName("1");
            personal.setPhone("1");
            personal.setPassword("1");
            dao.insert(personal);
        }
        //初始化用户
        GenerateP gp=new GenerateP(dao);
        gp.generatePerson();
	}
}
