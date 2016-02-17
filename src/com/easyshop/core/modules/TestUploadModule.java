package com.easyshop.core.modules;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.codec.binary.Base64;
import org.nutz.dao.Cnd;
import org.nutz.dao.Dao;
import org.nutz.img.Images;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.util.NutMap;
import org.nutz.mvc.Scope;
import org.nutz.mvc.annotation.AdaptBy;
import org.nutz.mvc.annotation.At;
import org.nutz.mvc.annotation.Attr;
import org.nutz.mvc.annotation.Fail;
import org.nutz.mvc.annotation.GET;
import org.nutz.mvc.annotation.Ok;
import org.nutz.mvc.annotation.Param;
import org.nutz.mvc.upload.FieldMeta;
import org.nutz.mvc.upload.TempFile;
import org.nutz.mvc.upload.UploadAdaptor;

import sun.misc.BASE64Encoder;

@IocBean
@At("/uploadFile")
@Ok("json")
@Fail("http:500")
public class TestUploadModule {
	@Inject
    protected Dao dao;
	private Map<String,Object> result;
	/**
	 * 添加照片
	* @param albumId 相册ID
	* @param req 
	 * @throws IOException 
	*/
	@At
	@AdaptBy(type = UploadAdaptor.class, args = { "ioc:picUpload" })
	public void addPhoto(@Param("..")NutMap nm,HttpServletRequest req) throws IOException{
//		TempFile tf = (TempFile)nm.get("photoFile");
		LinkedList tfList = (LinkedList)nm.get("photoFile");
		TempFile tf = (TempFile)tfList.get(0);
		File f = tf.getFile();
		BufferedImage bfimage = Images.read(f);
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		FieldMeta meta = tf.getMeta();
		String oldName = meta.getFileLocalName();
		com.easyshop.bean.Images img = new com.easyshop.bean.Images();
		img.setProductId(Integer.parseInt((String)nm.get("productId")));
		img.setDescp((String)nm.get("description"));
		img.setTitle((String)nm.get("photoName"));
		img.setTopimg(Boolean.parseBoolean((String)nm.get("TopPic")));
		img.setDetailPic(Boolean.parseBoolean((String)nm.get("DetailPic")));
		img.setSmallView(Boolean.parseBoolean((String)nm.get("ViewPic")));
		img.setOrderId(Integer.parseInt((String)nm.get("orderId")));
		//在这里进行base64转码
		ImageIO.write(bfimage,"png",out);
		BASE64Encoder encoder = new BASE64Encoder();
		String result = encoder.encode(out.toByteArray()); 
		img.setImgsource(result);
//		img.setImgsource(out.toByteArray());
		dao.insert(img);
	}
	
	@At
	@Ok("raw:jpg")
    @GET
    public Object readImages(HttpServletRequest req,@Param("imgId")int ID) throws SQLException {
		com.easyshop.bean.Images result=dao.fetch(com.easyshop.bean.Images.class,Cnd.where("imgID","=",ID));
        if (result == null || result.getImgsource() == null) {
            return new File(req.getServletContext().getRealPath("/tmp/Koala.jpg"));
        }
        return result.getImgsource();
    }
	@At
	public Object queryImg(@Param("imgId")int ID){
		this.result=new HashMap<String,Object>();
		com.easyshop.bean.Images resultFile=dao.fetch(com.easyshop.bean.Images.class,Cnd.where("imgID","=",ID));
		if(resultFile!=null){
			this.result.put("status", "success");
			this.result.put("file", "data:image/jpg;base64,"+resultFile.getImgsource());
		}else{
			this.result.put("status", "fail");
			this.result.put("msg", "未找到相关的图片");
		}
		return this.result;
	}
}
