package com.easyshop.core.modules;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;

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

import sun.misc.BASE64Encoder;

import com.easyshop.bean.Activity;
import com.easyshop.bean.Product;
import com.easyshop.bean.ProductType;

@IocBean
@At("/activity")
@Ok("json")
@Fail("http:500")
public class ActivityModule {
	@Inject
	protected Dao dao;
	private Map<String, Object> result;

	@At
	public Object getAllActivity(@Param("currentPage") int currentPage,
			@Param("peerpageRows") int peerpageRows) {
		this.result = new HashMap<String, Object>();
		this.result.put("total", dao.count(Activity.class));
		Pager pager = dao.createPager(currentPage, peerpageRows);
		List<Activity> activities = dao.query(Activity.class, null, pager);
		for (Activity a : activities) {
			List<Product> products = dao.query(Product.class,
					Cnd.where("activityId", "=", a.getActivityId()));
			for (Product p : products) {
				List<ProductType> pts = dao.query(ProductType.class,
						Cnd.where("productId", "=", p.getProductId()));
				p.setProductTypes(pts);
			}
			a.setProducts(products);
		}
		this.result.put("rows", activities);
		return this.result;
	}

	@At
	public Object addActivity(@Param("startTime") String startTime,
			@Param("endTime") String endTime,
			@Param("activityName") String activityName,
			@Param("specialPrice") String specialPrice,
			@Param("productIds") String pids) {
		this.result = new HashMap<String, Object>();
		Activity activity = new Activity();
		activity.setStartTime(startTime);
		activity.setEndTime(endTime);
		activity.setName(activityName);
		String[] specialPrices;
		String[] productIds;
		if (specialPrice != null && pids != null) {
			specialPrices = specialPrice.split("@");
			productIds = pids.split("@");
			int i = 0;
			for (String productId : productIds) {
				Product p = dao.fetch(Product.class, Cnd.where("productId",
						"=", Integer.parseInt(productId)));
				p.setSpecialPrice(Float.parseFloat(specialPrices[i++]));
				dao.update(p);
			}
		}
		if (dao.insert(activity) != null) {
			this.result.put("status", "success");
		} else {
			this.result.put("status", "fail");
			this.result.put("msg", "添加活动失败！");
		}
		return this.result;
	}

	@At
	public Object updateActivity(@Param("activityId") int ID,
			@Param("startTime") String startTime,
			@Param("endTime") String endTime,
			@Param("activityName") String activityName,
			@Param("specialPrice") String specialPrice,
			@Param("productIds") String pids) {
		this.result = new HashMap<String, Object>();
		Activity activity = dao.fetch(Activity.class,
				Cnd.where("activityId", "=", ID));

		activity.setStartTime(startTime);
		activity.setEndTime(endTime);
		activity.setName(activityName);
		String[] specialPrices;
		String[] productIds;
		if (specialPrice != null && pids != null) {
			specialPrices = specialPrice.split("@");
			productIds = pids.split("@");
			int i = 0;
			for (String productId : productIds) {
				Product p = dao.fetch(Product.class, Cnd.where("productId",
						"=", Integer.parseInt(productId)));
				if (specialPrices.length > 0) {
					if (specialPrices.length > i) {
						p.setSpecialPrice(Float.parseFloat(specialPrices[i++]));
					}
				}
				p.setActivityId(ID);
				dao.update(p);
			}
		}
		if (dao.update(activity) > 0) {
			this.result.put("status", "success");
		} else {
			this.result.put("status", "fail");
			this.result.put("msg", "添加活动失败！");
		}

		return this.result;
	}

	@At
	public Object deleteOne(@Param("activityId") int ID) {
		this.result = new HashMap<String, Object>();
		if (dao.delete(Activity.class, ID) > 0) {
			this.result.put("status", "success");
		} else {
			this.result.put("status", "fail");
			this.result.put("msg", "删除活动失败");
		}
		return this.result;

	}

	@At
	@AdaptBy(type = UploadAdaptor.class, args = { "ioc:picUpload" })
	public Object addMainPic(@Param("..") NutMap nm) throws IOException {
		this.result = new HashMap<String, Object>();
		LinkedList tfList = (LinkedList) nm.get("photoFile");
		if (!("".equals(tfList.get(0)))) {
			TempFile tf = (TempFile) tfList.get(0);
			File f = tf.getFile();
			BufferedImage image = Images.read(f);
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			Images.writeJpeg(image, out, 0.8f);
			FieldMeta meta = tf.getMeta();
			String oldName = meta.getFileLocalName();
			// 在这里进行base64转码
			ImageIO.write(image, "png", out);
			BASE64Encoder encoder = new BASE64Encoder();
			String result = encoder.encode(out.toByteArray());
			int id = Integer.parseInt((String) nm.get("activityId"));
			Activity ac = dao.fetch(Activity.class,
					Cnd.where("activityId", "=", id));
			ac.setMainPic(result);
			String url = nm.getString("url");
			ac.setUrl(url);
			if (dao.update(ac) > 0) {
				this.result.put("status", "success");
			} else {
				this.result.put("status", "fail");
				this.result.put("msg", "添加商品主图失败！");
			}
			return this.result;
		} else {
			int id = Integer.parseInt((String) nm.get("activityId"));
			Activity ac = dao.fetch(Activity.class,
					Cnd.where("activityId", "=", id));
			String url = nm.getString("url");
			ac.setUrl(url);
			if (dao.update(ac) > 0) {
				this.result.put("status", "success");
			} else {
				this.result.put("status", "fail");
				this.result.put("msg", "添加商品主图失败！");
			}
			return this.result;
		}

	}

}