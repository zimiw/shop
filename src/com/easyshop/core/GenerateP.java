package com.easyshop.core;

import java.util.ArrayList;

import org.nutz.dao.Dao;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;

import com.easyshop.bean.Personal;

@IocBean
public class GenerateP {
	@Inject
    protected Dao dao;
	public GenerateP(Dao dao){
		this.dao=dao;
	}
	public void generatePerson(){
		System.out.println(dao.count(Personal.class));
		if(dao.count(Personal.class)==1){
			ArrayList<Personal> ps=new ArrayList<Personal>();
			for(int i=0;i<100;i++){
				Personal p = new Personal();
				p.setName("test-"+i);
				p.setNickname("test-"+i);
				p.setPassword("123456");
				ps.add(p);
				this.dao.insert(p);
			}
		}
	}
}
