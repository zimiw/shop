package com.easyshop.core.modules;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.nutz.dao.Cnd;
import org.nutz.dao.Dao;
import org.nutz.dao.pager.Pager;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.mvc.annotation.At;
import org.nutz.mvc.annotation.Fail;
import org.nutz.mvc.annotation.Ok;
import org.nutz.mvc.annotation.Param;

import com.easyshop.bean.Brand;
import com.easyshop.bean.Catalogs;

@IocBean
@At("/brand")
@Ok("json")
@Fail("http:500")
public class BrandModule {
	@Inject
    protected Dao dao;
	private Map<String,Object> result;
	
	@At
	public Object getAllBrands(@Param("currentPage")int currentPage,@Param("peerpageRows")int peerpageRows){
		this.result=new HashMap<String,Object>();
		this.result.put("total", dao.count(Brand.class));
		Pager pager = dao.createPager(currentPage, peerpageRows);
		List<Brand> brands=dao.query(Brand.class,null,pager);
		for(Brand b : brands){
			Catalogs c=dao.fetch(Catalogs.class,Cnd.where("catalogId","=",b.getCatalogId()));
			if(c !=null){
				b.setCatalog(c);
			}else{
				Catalogs cc=new Catalogs();
				cc.setCatalogName("未分类");
				b.setCatalog(cc);
			}
		}
		this.result.put("rows", brands);
		return this.result;
	}
	@At
	public Object deleteOne(@Param("brandId")int ID){
		this.result=new HashMap<String,Object>();
		if(dao.delete(Brand.class,ID)>0){
			this.result.put("status", "success");
		}else{
			this.result.put("status", "fail");
			this.result.put("msg", "品牌删除失败");
		}
		return this.result;
	}
	@At
	public Object addBrand(@Param("catalogId")int catalogId,@Param("brandName")String brandName){
		this.result=new HashMap<String,Object>();
		Brand temp=new Brand();
		temp.setBrandName(brandName);
		temp.setCatalogId(catalogId);
		Brand tmp=dao.insert(temp);
		if(tmp!=null){
			this.result.put("status","success");
		}else{
			this.result.put("status", "fail");
			this.result.put("msg", "添加品牌失败");
		}
		return this.result;
	}
	@At
	public Object editBrand(@Param("catalogId")int catalogId,@Param("brandName")String brandName,@Param("brandId")int brandId){
		this.result=new HashMap<String,Object>();
		Brand b=dao.fetch(Brand.class,Cnd.where("brandId","=",brandId));
		b.setCatalogId(catalogId);
		b.setBrandName(brandName);
		if(dao.update(b)>0){
			this.result.put("status", "success");
		}else{
			this.result.put("status", "fail");
			this.result.put("msg", "更新品牌操作失败");
		}
		return this.result;
	}
}
