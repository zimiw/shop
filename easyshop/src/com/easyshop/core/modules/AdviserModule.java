package com.easyshop.core.modules;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.nutz.dao.Cnd;
import org.nutz.dao.Dao;
import org.nutz.dao.pager.Pager;
import org.nutz.img.Images;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.util.NutMap;
import org.nutz.mvc.annotation.AdaptBy;
import org.nutz.mvc.annotation.At;
import org.nutz.mvc.annotation.Fail;
import org.nutz.mvc.annotation.Ok;
import org.nutz.mvc.annotation.Param;
import org.nutz.mvc.upload.FieldMeta;
import org.nutz.mvc.upload.TempFile;
import org.nutz.mvc.upload.UploadAdaptor;

import com.easyshop.bean.Activity;
import com.easyshop.bean.Adviser;

import sun.misc.BASE64Encoder;

@IocBean
@At("/adviser")
@Ok("json")
@Fail("http:500")
public class AdviserModule {
	@Inject
    protected Dao dao;
	private Map<String,Object> result;
	
	@At
	@AdaptBy(type = UploadAdaptor.class, args = { "ioc:picUpload" })
	public Object addAd(@Param("..")NutMap nm,HttpServletRequest req){
		this.result=new HashMap<String,Object>();
		String url = (String)nm.get("url");
		String type = (String)nm.get("type");//type有两种类型1:banner;2:productPic
		//获取图片
		LinkedList tfList = (LinkedList)nm.get("photoFile");
		TempFile tf = (TempFile)tfList.get(0);
		File f = tf.getFile();
		BufferedImage image = Images.read(f);
		ByteArrayOutputStream out = new ByteArrayOutputStream();
        Images.writeJpeg(image, out, 0.8f);
		FieldMeta meta = tf.getMeta();
		String oldName = meta.getFileLocalName();
		//在这里进行base64转码
		BASE64Encoder encoder = new BASE64Encoder();
		String result = encoder.encode(out.toByteArray());
		//获取图片
		//开始进行组装对象
		Adviser ad = new Adviser();
		ad.setType(type);
		ad.setUrl(url);
		ad.setImgSource(result);
		if(dao.insert(ad)!=null){
			this.result.put("status", "success");
		}else{
			this.result.put("status", "fail");
			this.result.put("msg", "添加失败");
		}
		return this.result;
	}
	@At
	public Object deleteOne(@Param("adviserId")int ID){
		this.result=new HashMap<String,Object>();
		if(dao.delete(Adviser.class,ID)>0){
			this.result.put("status", "success");
		}else{
			this.result.put("status", "fail");
			this.result.put("msg", "删除失败");
		}
		return this.result;
	}
	
	@At
	public Object getAllAd(@Param("currentPage")int currentPage,@Param("peerpageRows")int peerpageRows){
		this.result=new HashMap<String,Object>();
		this.result.put("total", dao.count(Adviser.class));
		Pager pager = dao.createPager(currentPage, peerpageRows);
		List<Adviser> ads=dao.query(Adviser.class, null,pager);
		this.result.put("rows", ads);
		return this.result;
	}
	
	@At
	@AdaptBy(type = UploadAdaptor.class, args = { "ioc:picUpload" })
	public Object editAd(@Param("..")NutMap nm){
		this.result=new HashMap<String,Object>();
		int id = Integer.parseInt((String)nm.get("adviserId"));
		Adviser ad = dao.fetch(Adviser.class,Cnd.where("adviserId","=",id));
		if(ad!=null){
			String type=(String)nm.get("type");
			String url=(String)nm.get("url");
			//获取图片
			LinkedList tfList = (LinkedList)nm.get("photoFile");
			TempFile tf = (TempFile)tfList.get(0);
			File f = tf.getFile();
			BufferedImage image = Images.read(f);
			ByteArrayOutputStream out = new ByteArrayOutputStream();
	        Images.writeJpeg(image, out, 0.8f);
			FieldMeta meta = tf.getMeta();
			String oldName = meta.getFileLocalName();
			//在这里进行base64转码
			BASE64Encoder encoder = new BASE64Encoder();
			String result = encoder.encode(out.toByteArray());
			//获取图片
			ad.setImgSource(result);
			ad.setType(type);
			ad.setUrl(url);
			if(dao.update(ad)>0){
				this.result.put("status", "success");
			}else{
				this.result.put("status", "fail");
				this.result.put("msg", "没有找到对应的广告信息");
			}
		}else{
			this.result.put("status", "fail");
			this.result.put("msg", "没有找到对应的广告信息");
			return this.result;
		}
		return this.result;
	}
}
