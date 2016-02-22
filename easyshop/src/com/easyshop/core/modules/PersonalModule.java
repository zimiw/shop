package com.easyshop.core.modules;

import com.easy.core.filters.CheckBackUserLoginFilter;
import com.easy.core.filters.CheckFrontUserLoginFilter;
import com.easyshop.bean.*;
import com.easyshop.core.modules.admin.OrderConstant;
import com.easyshop.utils.MailUtils;
import com.easyshop.utils.MessageUtils;
import com.easyshop.utils.RandomUtils;
import com.easyshop.utils.StringUtils;
import com.easyshop.vo.ResultVo;
import org.apache.log4j.Logger;
import org.nutz.dao.Cnd;
import org.nutz.dao.Dao;
import org.nutz.dao.Sqls;
import org.nutz.dao.pager.Pager;
import org.nutz.dao.sql.Sql;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.util.NutMap;
import org.nutz.mvc.Mvcs;
import org.nutz.mvc.annotation.*;
import org.nutz.mvc.filter.CheckSession;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.*;

/**
 * 个人中心
 * 
 * @author luocz
 */
@IocBean
@At("personal")
@Ok("json")
@Fail("http:500")
@Filters(@By(type = CheckSession.class, args = { OrderConstant.FRONT_USER_ID,
		"/front/login.html" }))
public class PersonalModule {

	Logger logger = Logger.getLogger(PersonalModule.class);

	@Inject
	protected Dao dao;

	/**
	 * 根据会员id获取用户信息<br/>
	 * 1、会员基本信息
	 *
	 * @return
	 */
	@At
	@Filters(@By(type = CheckFrontUserLoginFilter.class))
	public Object getUserInfo(HttpSession session) {

		String userId = String.valueOf(session
				.getAttribute(OrderConstant.FRONT_USER_ID));

		Map<String, Object> result = new HashMap<String, Object>();
		Personal personal = dao.fetch(Personal.class,
				Cnd.where("id", "=", userId));

		Calendar calendar = Calendar.getInstance();
		int hours = calendar.get(Calendar.HOUR_OF_DAY);
		String signature = "";
		if (hours < 12) {
			signature = "早上好~";
		} else if (hours >= 12 && hours <= 14) {
			signature = "中午好~";
		} else if (hours > 14 && hours <= 18) {
			signature = "下午好~";
		} else if (hours > 18) {
			signature = "晚上好~";
		}

		personal.setSignature(signature);

		result.put("personal", personal);
		return result;
	}

	/**
	 * 获取用户收藏的商品
	 * 
	 * @param session
	 * @return
	 */
	@At
	public Object getFavorite(HttpSession session) {
		Map<String, Object> result = new HashMap<String, Object>();
		String userId = String.valueOf(session
				.getAttribute(OrderConstant.FRONT_USER_ID));

		int count = dao.count(Favorite.class, Cnd.where("userId", "=", userId));
		List<Map<String, Object>> imgList = new LinkedList<Map<String, Object>>();
		Map<String, Object> imgMap = null;
		List<Favorite> favoriteList = null;
		if (count > 0) {
			// 取出最近6个收藏的商品
			Pager pager = dao.createPager(0, 6);
			favoriteList = dao.query(Favorite.class,
					Cnd.where("userId", "=", userId).desc("favoriteId"), pager);
			Images image = null;
			for (Favorite fav : favoriteList) {
				image = fetchImg(fav.getProductId());
				imgMap = new HashMap<String, Object>();
				imgMap.put("favoriteId", fav.getFavoriteId());
				imgMap.put("img", image.getImgsource());
				imgList.add(imgMap);
			}
		}
		result.put("favoriteNum", count);
		result.put("favoriteAll", imgList);

		return result;
	}

	/**
	 * 用户添加商品的收藏夹
	 * 
	 * @param session
	 * @return
	 */
	@At
	public Object addFavorite(HttpSession session,
			@Param("productId") String productId) {
		String userId = String.valueOf(session
				.getAttribute(OrderConstant.FRONT_USER_ID));

		ResultVo resultVo = new ResultVo();

		Favorite favorite = dao.fetch(
				Favorite.class,
				Cnd.where("productId", "=", productId).and("userId", "=",
						userId));
		if (favorite != null) {
			resultVo.setStatus(ResultVo.STATUS_FAIL);
			resultVo.setMsg("该商品已添加");
			return resultVo;
		}

		try {
			Favorite fav = new Favorite();
			fav.setProductId(Integer.parseInt(productId));
			fav.setUserId(Integer.parseInt(userId));
			dao.insert(fav);
			resultVo.setMsg("收藏夹添加成功");
		} catch (Exception e) {
			logger.error("收藏夹添加失败", e);
			resultVo.setStatus(ResultVo.STATUS_FAIL);
			resultVo.setMsg("收藏夹添加失败");
		}

		return resultVo;
	}

	/**
	 * 用户删除对应是收藏夹
	 * 
	 * @return
	 */
	@At
	public Object deleteFavorite(HttpSession session,
			@Param("favoriteId") String favoriteId) {
		Map<String, Object> result = new HashMap<String, Object>();
		String userId = String.valueOf(session
				.getAttribute(OrderConstant.FRONT_USER_ID));

		Favorite favorite = dao.fetch(
				Favorite.class,
				Cnd.where("userId", "=", userId).and("favoriteId", "=",
						favoriteId));

		if (favorite != null) {
			dao.delete(Favorite.class, favorite.getFavoriteId());
			result.put("status", "success");
			result.put("msg", "收藏夹删除成功");
		} else {
			result.put("status", "fail");
			result.put("msg", "收藏夹删除失败!");
		}

		return result;
	}

	/**
	 * 个人收藏夹页面获取所有收藏的商品信息
	 * 
	 * @return
	 */
	@At
	public Object getFavoriteAll(HttpSession session) {
		Map<String, Object> result = new HashMap<String, Object>();
		String userId = String.valueOf(session
				.getAttribute(OrderConstant.FRONT_USER_ID));
		int count = dao.count(Favorite.class, Cnd.where("userId", "=", userId));
		List<Map<String, Object>> favList = new LinkedList<Map<String, Object>>();
		Map<String, Object> favMap = null;
		if (count > 0) {
			List<Favorite> favoriteList = dao.query(Favorite.class,
					Cnd.where("userId", "=", userId).desc("favoriteId"));
			Product product = null;
			Images image = null;
			for (Favorite fav : favoriteList) {
				product = dao.fetch(Product.class,
						Cnd.where("productId", "=", fav.getProductId()));

				image = fetchImg(fav.getProductId());
				favMap = new HashMap<String, Object>();
				favMap.put("favoriteId", fav.getFavoriteId());
				favMap.put("productId", fav.getProductId());
				favMap.put("img", image.getImgsource());
				favMap.put("name", product.getName());
				favMap.put("area", product.getProvider());// 供货商
				favMap.put("flag", "");// TODO: 收藏夹页面 flag: "img/demo_9.png",
										// 没法取
				favMap.put("price", product.getCurrentPrice());
				favList.add(favMap);
			}
		}

		result.put("favorite", favList);

		return result;
	}

	/**
	 * 取最新3个购物车商品
	 * 
	 * @param session
	 * @return
	 */
	@At
	public Object getCart(HttpSession session) {
		Map<String, Object> result = new HashMap<String, Object>();
		String userId = String.valueOf(session
				.getAttribute(OrderConstant.FRONT_USER_ID));

		// 查看用户收藏商品数量
		int count = dao.count(Shoppingcart.class,
				Cnd.where("userId", "=", userId));
		List<Map<String, String>> imgList = new LinkedList<Map<String, String>>();
		if (count > 0) {
			Pager pager = dao.createPager(0, 3);
			List<Shoppingcart> cartList = dao
					.query(Shoppingcart.class, Cnd.where("userId", "=", userId)
							.desc("shoppingcartId"), pager);
			Images image = null;

			Map<String, String> map = null;
			for (Shoppingcart cart : cartList) {// 取没有商品的图片
				image = fetchImg(cart.getProductId());
				map = new HashMap<String, String>();
				map.put("img", image.getImgsource());
				imgList.add(map);
			}
		}

		result.put("cartNum", count);
		result.put("cart", imgList);
		return result;
	}

	/**
	 * 根据商品id获取对应是商品id
	 * 
	 * @return
	 */
	private Images fetchImg(int productId) {
		Images imgs = dao.fetch(
				Images.class,
				Cnd.where("productId", "=", productId).and("isTopimg", "=",
						true));
		return imgs != null ? imgs : new Images();
	}

	@At
	@Filters(@By(type = CheckFrontUserLoginFilter.class))
	public int count(HttpSession session) {
		System.out.println(session.getAttribute("frontUserId"));
		logger.debug("数目是:" + session.getAttribute("frontUserId"));
		return dao.count(Personal.class);
	}

	/**
	 * localhost:8181/easyshop/personal/login?name=1&password=1
	 *
	 * @param name
	 * @param password
	 * @param session
	 *
	 * @return
	 */
	@At
	@Filters
	public Object login(@Param("name") String name,
			@Param("password") String password,@Param("isAutoLogin") String isAutoLogin, HttpSession session,
			HttpServletResponse response,
			HttpServletRequest request) throws IOException {
		if (StringUtils.isEmpty(name) || StringUtils.isEmpty(password)) {
			return new ResultVo("fail", "用户名或密码为空");
		}
		Map<String, Object> result = new HashMap<String, Object>();
		// 这样分开写,主要是为了能用到索引
		Personal person = dao.fetch(Personal.class,
				Cnd.where("phone", "=", name).and("password", "=", password));
		if (person == null) {
			person = dao.fetch(Personal.class, Cnd.where("email", "=", name)
					.and("password", "=", password));
		}
		if (person == null) {
			return new ResultVo("fail", "用户名或者密码错误");
		} else {
			session.setAttribute("frontUserId", person.getId());
			// httpServletResponse.sendRedirect(httpServletRequest.getContextPath()
			// + "/front/home.html");

            if ("true".equals(isAutoLogin)) {
                //String host = request.getServerName();
                Cookie cookie = new Cookie("frontUserId", String.valueOf(person.getId())); // 保存用户名到Cookie
                //Cookie cookie = new Cookie("SESSION_LOGIN_USERNAME", name); // 保存用户名到Cookie
                cookie.setPath("/");
                //cookie.setDomain(host);
                //以秒为单位 目前是两周内有效.
                cookie.setMaxAge(2 * 7 * 24 * 60 * 60);
                response.addCookie(cookie);
                /*if (ParamUtils.getBooleanParameter(request, "savePassword")) {
                    // 保存密码到Cookie，注意需要加密一下
                    cookie = new Cookie("SESSION_LOGIN_PASSWORD", MD5.encode(u.getPassword()));
                    cookie.setPath("/");
                    cookie.setDomain(host);
                    cookie.setMaxAge(99999999);
                    response.addCookie(cookie);
                }*/
            }

			result.put("status", "success");
			result.put("msg", "登陆成功");
			result.put("target", "main.html");
			return result;
		}
	}

	/**
	 * 新增前台用户 注册使用
	 * http://localhost:8181/easyshop/personal/addPersonal?name=111&password
	 * =1&randomNumStr=817626 {"status":"success","frontUserId":5}
	 * 
	 * @param name
	 * @param password
	 * @param randomNumStr
	 * @return
	 */
	@At
	@Filters
	public ResultVo addPersonal(@Param("name") String name,
			@Param("password") String password,
			@Param("randomNumStr") String randomNumStr) {

		Map<String, Object> result = new HashMap<String, Object>();

		if (StringUtils.isEmpty(name, password, randomNumStr)) {
			return new ResultVo("fail", "参数错误");
		}

		Personal person = dao.fetch(Personal.class,
				Cnd.where("phone", "=", name));
		if (person != null) {
			return new ResultVo(ResultVo.STATUS_FAIL, "该用户名已经存在");
		}

		// 检测验证码
		ResultVo resultVo = new ResultVo();
		resultVo = validateSendPhoneMessage(name, randomNumStr);
		if (resultVo.getStatus().equals(ResultVo.STATUS_FAIL)) {
			return resultVo;
		}

		Personal personal = new Personal();
		personal.setPhone(name);
		personal.setPassword(password);
		dao.insert(personal);
		resultVo.setStatus(ResultVo.STATUS_SUCCESS);
		resultVo.setMsg(String.valueOf(personal.getId()));
		return resultVo;
	}

	/**
	 * 登出 localhost:8181/easyshop/personal/logout
	 * 
	 * @param session
	 * @param httpServletResponse
	 * @param httpServletRequest
	 * @throws IOException
	 */
	@At
	@Filters
	public void logout(HttpSession session,
			HttpServletResponse httpServletResponse,
			HttpServletRequest httpServletRequest) throws IOException {
		session.invalidate();
		httpServletResponse.sendRedirect(httpServletRequest.getContextPath()
				+ "/front/login.html");
	}

	/**
	 * 获取所有个人信息 http://localhost:8181/easyshop/personal/getFullPersonlInfoById
	 *
	 * 返回的是: {"id":1,"password":"1","name":"1","sex":0,"phone":"","email":"",
	 * "passwordLevel":"低","safe":"低",
	 * "bindPhone":0,"bindEmail":0,"pnopay":0,"psend":0,"pwait":0,"pevaluate":0}
	 * bindPhone 表示是否绑定手机 bindEmail表示是否绑定邮箱 1为绑定 0为未绑定 sex 性别 0未设置 1男 2女 name
	 * 是真实姓名 nickName 是昵称 如果缺少参数,比如地址,表示没有数据库为空没有设置
	 *
	 * @return Personal
	 */
	@At
	public Personal getFullPersonlInfoById() {
		Personal personal = dao.fetch(
				Personal.class,
				Integer.parseInt(Mvcs.getHttpSession(false)
						.getAttribute(OrderConstant.FRONT_USER_ID).toString()));

		int safeLevel = 0;
		// 判断密码等级 密码长度小于6就是低，6到10中，10以上高
		int passwordLength = personal.getPassword().length();
		if (passwordLength < 6) {
			personal.setPasswordLevel("低");
		} else if (passwordLength > 10) {
			personal.setPasswordLevel("高");
			safeLevel++;
		} else {
			personal.setPasswordLevel("中");
		}

		// 判断安全级别
		// 密码 手机 邮箱 2个出问题就是低 1个出问题就是中 0个出问题就是高

		if (!StringUtils.isEmpty(personal.getPhone())) {
			safeLevel++;
			personal.setBindPhone(1);
		}
		if (!StringUtils.isEmpty(personal.getEmail())) {
			safeLevel++;
			personal.setBindEmail(1);
		}
		if (safeLevel < 2) {
			personal.setSafe("低");
		} else if (safeLevel == 3) {
			personal.setSafe("高");
		} else {
			personal.setSafe("中");
		}
		return personal;
	}

	@At
    @Filters(@By(type = CheckFrontUserLoginFilter.class))
	public String getPersonlName() {
		Personal personal = dao.fetch(
				Personal.class,
				Integer.parseInt(Mvcs.getHttpSession(false)
						.getAttribute(OrderConstant.FRONT_USER_ID).toString()));

        //首先查看是否有名字,如果没有就返回手机号
        if (!StringUtils.isEmpty(personal.getNickname())) {
            return personal.getNickname();
        }

        return personal.getPhone();
	}

	/**
	 * 获取所有个人信息 http://localhost:8181/easyshop/personal/getPersonlInfoById?id=1
	 *
	 * 返回的是: {"id":1,"password":"1","name":"1","sex":0,"phone":"","email":"",
	 * "passwordLevel":"低","safe":"低",
	 * "bindPhone":0,"bindEmail":0,"pnopay":0,"psend":0,"pwait":0,"pevaluate":0}
	 * bindPhone 表示是否绑定手机 bindEmail表示是否绑定邮箱 1为绑定 0为未绑定 sex 性别 0未设置 1男 2女 name
	 * 是真实姓名 nickName 是昵称 如果缺少参数,比如地址,表示没有数据库为空没有设置
	 * @param id
	 * @return
	 */
	@At
	@Filters(@By(type = CheckBackUserLoginFilter.class))
	public Personal getPersonlInfoById(int id) {
		Personal personal = dao.fetch(Personal.class, id);

		int safeLevel = 0;
		// 判断密码等级 密码长度小于6就是低，6到10中，10以上高
		int passwordLength = personal.getPassword().length();
		if (passwordLength < 6) {
			personal.setPasswordLevel("低");
		} else if (passwordLength > 10) {
			personal.setPasswordLevel("高");
			safeLevel++;
		} else {
			personal.setPasswordLevel("中");
		}

		// 判断安全级别
		// 密码 手机 邮箱 2个出问题就是低 1个出问题就是中 0个出问题就是高

		if (!StringUtils.isEmpty(personal.getPhone())) {
			safeLevel++;
			personal.setBindEmail(1);
		}
		if (!StringUtils.isEmpty(personal.getEmail())) {
			safeLevel++;
			personal.setBindPhone(1);
		}
		if (safeLevel < 2) {
			personal.setSafe("低");
		} else if (safeLevel == 3) {
			personal.setSafe("高");
		} else {
			personal.setSafe("中");
		}
		return personal;
	}

	/**
	 *
	 * 查询前台用户列表:
	 * http://localhost:8181/easyshop/personal/getPersonallList?currentPage=1&peerpageRows=2&email=1&phone=1
	 *
	 *{"total":5,"rows":[{"id":1,"password":"1","name":"1","sex":0,"phone":"1","email":"1111","bindPhone":0,"bindEmail":0,"pnopay":0,"psend":0,"pwait":0,"pevaluate":0,"isPlayGame":false}, {"id":103,"password":"1986520","sex":0,"phone":"15951701933","email":"11","bindPhone":0,"bindEmail":0,"pnopay":0,"psend":0,"pwait":0,"pevaluate":0,"isPlayGame":false}]}
	 * 如果查询不到数据:{"total":0,"rows":[]}
	 *
	 * @param currentPage     当前第几页
	 * @param peerpageRows    每页多少条
	 * @param email 模糊查询
	 * @param phone 模糊查询
	 * @return
	 */
	@At
	@Filters(@By(type = CheckBackUserLoginFilter.class))
	public Object getPersonallList(@Param("page")int page,@Param("rows")int rows,
			@Param("email") String email,@Param("phone") String phone,@Param("name") String name) {
		Map<String,Object> result=new HashMap<String,Object>();
		//设置分页参数
		Pager pager = dao.createPager(page, rows);
		Cnd condition = Cnd.where("1", "=", 1);
		if (!StringUtils.isEmpty(email)) {
			condition.and("email", "like", "%" + email + "%");
		}
		if (!StringUtils.isEmpty(phone)) {
			condition.and("phone", "like", "%"+phone+"%");
		}
		if (!StringUtils.isEmpty(name)) {
			condition.and("name", "like", "%"+name+"%");
		}
		result.put("total", dao.count(Personal.class,condition));
		List<Personal> list = dao.query(Personal.class, condition,pager);
		result.put("rows",list);
		return result;
	}

	/**
	 * 保存所有个人信息
	 * http://localhost:8181/easyshop/personal/saveFullPersonlInfoById?id
	 * =1&name=2
	 *
	 * 传递的参数,都是数据库personal表里面的字段,上面的示例就不一一列举了.
	 *
	 * {"status":"fail","msg":"保存失败"} {"status":"fail","msg":"参数错误"}
	 * {"status":"success","msg":"保存成功"}
	 *
	 * @param newPersonal
	 *            待更新的
	 * @return return
	 */
	@At
	public ResultVo saveFullPersonlInfoById(@Param("..") Personal newPersonal,
			@Param("..") NutMap nm) {
		ResultVo resultVo = new ResultVo();
		try {
//			if (!StringUtils.isEmpty(newPersonal.getImg())) {
//				// 对图片做一下进行base64转码
//				newPersonal.setImg(ImageUtils.imageFileEncode2Str(nm, "img"));
//			}
			dao.updateIgnoreNull(newPersonal);
			resultVo.setMsg("保存成功");
		} catch (Exception e) {
			logger.info("saveFullPersonlInfoById 保存失败:", e);
			resultVo.setStatus(ResultVo.STATUS_FAIL);
			resultVo.setMsg("保存失败");
		}
		return resultVo;
	}

	/**
	 * 发送手机验证码 http://localhost:8181/easyshop/personal/sendPhoneMessage?phone=1
	 *
	 * {"status":"fail","msg":"网络繁忙,请稍后重试"} {"status":"fail","msg":"参数错误"}
	 * {"status":"success","msg":"发送成功"}
	 *
	 * @param phone
	 * @return
	 */
	@At
	@Filters
	public ResultVo sendPhoneMessage(@Param("phone") String phone,
			HttpSession session) {
		if (StringUtils.isEmpty(phone)) {
			return new ResultVo(ResultVo.STATUS_FAIL, "参数错误");
		}
		/*
		 * else if (dao.count(Personal.class, Cnd.where("phone", "=", phone)) !=
		 * 0){ return new ResultVo(ResultVo.STATUS_FAIL, "该手机已经注册过了,请直接登陆"); }
		 */
		ResultVo resultVo = new ResultVo();
		try {
			MessageRtnInfo messageRtnInfo = MessageUtils.sendMessage(phone);
			messageRtnInfo.setSendTime(new Date());
			/*
			 * MessageRtnInfo messageRtnInfo=new MessageRtnInfo();
			 * messageRtnInfo.setSendTime(new Date());
			 * //session.setAttribute("sendId", messageRtnInfo.getSendId());
			 * messageRtnInfo.setPhone(phone);
			 * messageRtnInfo.setSendId(RandomUtils.getRandomNumberStr());
			 * messageRtnInfo.setRandomNumStr(RandomUtils.getRandomNumberStr());
			 */
			// 记录到数据库
			dao.insert(messageRtnInfo);
			resultVo.setMsg("发送成功");
		} catch (Exception e) {
			logger.info("发送短信失败捕捉", e);
			resultVo.setStatus(ResultVo.STATUS_FAIL);
			resultVo.setMsg("网络繁忙,请稍后重试");
		}

		return resultVo;
	}

	/**
	 * 发送邮件 http://localhost:8181/easyshop/personal/sendEmailMessage?email=
	 * 1225304143@qq.com {"status":"success","msg":"邮件发送成功"}
	 * {"status":"fail","msg":"邮件发送失败"} {"status":"fail","msg":"网络繁忙,请稍后重试"}
	 * 
	 * @param email
	 * @return
	 */
	@At
	public ResultVo sendEmailMessage(@Param("email") String email) {
		if (StringUtils.isEmpty(email)) {
			return new ResultVo(ResultVo.STATUS_FAIL, "参数错误");
		}
		ResultVo resultVo = new ResultVo();
		try {
			MessageRtnInfo messageRtnInfo = new MessageRtnInfo();
			messageRtnInfo.setSendTime(new Date());
			messageRtnInfo.setPhone(email);
			messageRtnInfo.setRandomNumStr(RandomUtils.getRandomNumberStr());
			if (!MailUtils.send(email, messageRtnInfo.getRandomNumStr())) {
				return new ResultVo(ResultVo.STATUS_FAIL, "邮件发送失败");
			}
			// 记录到数据库
			dao.insert(messageRtnInfo);
			resultVo.setMsg("邮件发送成功");
		} catch (Exception e) {
			logger.info("邮件发送失败捕捉", e);
			resultVo.setStatus(ResultVo.STATUS_FAIL);
			resultVo.setMsg("网络繁忙,请稍后重试");
		}

		return resultVo;
	}

	/**
	 * 校验手机验证码
	 * http://localhost:8181/easyshop/personal/validateSendPhoneMessage?phone
	 * =1225304143&randomNumStr=1 {"status":"success","msg":"验证码校验成功"}
	 * {"status":"fail","msg":"验证码校验失败"}
	 * 
	 * @param phone
	 * @param randomNumStr
	 * @return
	 */
	@At
	@Filters
	public ResultVo validateSendPhoneMessage(@Param("phone") String phone,
			@Param("randomNumStr") String randomNumStr) {

		// if (StringUtils.isEmpty(phone, randomNumStr,
		// session.getAttribute("sendId"))) {
		if (StringUtils.isEmpty(phone, randomNumStr)) {
			return new ResultVo(ResultVo.STATUS_FAIL, "参数错误");
		}
		ResultVo resultVo = new ResultVo();
		// 暂时,只用session 的sendMessage4Register 验证即可.暂时不比较时间
		List<MessageRtnInfo> messageRtnInfoList = dao.query(
				MessageRtnInfo.class,
				Cnd.where("phone", "=", phone)
						.and("randomNumStr", "=", randomNumStr)
						.orderBy("sendTime", "desc"));

		if (messageRtnInfoList.size() > 0) {
			resultVo.setMsg("验证码校验成功");
			dao.delete(MessageRtnInfo.class, messageRtnInfoList.get(0).getId());
		} else {
			return new ResultVo(ResultVo.STATUS_FAIL, "验证码校验失败");
		}
		return resultVo;
	}

	/**
	 * 验证邮件
	 * http://localhost:8181/easyshop/personal/validateSendEmailMessage?email
	 * =1225304143@qq.com&randomNumStr=1 {"status":"success","msg":"验证码校验成功"}
	 * {"status":"fail","msg":"验证码校验失败"}
	 * 
	 * @param email
	 * @param randomNumStr
	 * @return
	 */
	@At
	public ResultVo validateSendEmailMessage(@Param("email") String email,
			@Param("randomNumStr") String randomNumStr) {

		if (StringUtils.isEmpty(email, randomNumStr)) {
			return new ResultVo(ResultVo.STATUS_FAIL, "参数错误");
		}
		ResultVo resultVo = new ResultVo();
		// 暂时,只用session 的sendMessage4Register 验证即可.暂时不比较时间
		List<MessageRtnInfo> messageRtnInfoList = dao.query(
				MessageRtnInfo.class,
				Cnd.where("phone", "=", email)
						.and("randomNumStr", "=", randomNumStr)
						.orderBy("sendTime", "desc"));

		if (messageRtnInfoList.size() > 0) {
			resultVo.setMsg("验证码校验成功");
			dao.delete(MessageRtnInfo.class, messageRtnInfoList.get(0).getId());
		} else {
			return new ResultVo(ResultVo.STATUS_FAIL, "验证码校验失败");
		}
		return resultVo;
	}

	/**
	 * 解绑邮箱 http://localhost:8181/easyshop/personal/unbindEmail?email=1225304143@qq
	 * .com&randomNumStr=1 {"status":"success","msg":"解绑成功"}
	 * {"status":"fail","msg":"验证码校验失败"} {"status":"fail","msg":"参数错误"}
	 * 
	 * @param email
	 * @param randomNumStr
	 * @return
	 */
	@At
	public ResultVo unbindEmail(@Param("email") String email,
			@Param("randomNumStr") String randomNumStr) {

		if (StringUtils.isEmpty(email, randomNumStr)) {
			return new ResultVo(ResultVo.STATUS_FAIL, "参数错误");
		}
		ResultVo resultVo = new ResultVo();
		// 暂时,只用session 的sendMessage4Register 验证即可.暂时不比较时间
		List<MessageRtnInfo> messageRtnInfoList = dao.query(
				MessageRtnInfo.class,
				Cnd.where("phone", "=", email)
						.and("randomNumStr", "=", randomNumStr)
						.orderBy("sendTime", "desc"));

		if (messageRtnInfoList.size() > 0) {
			dao.delete(MessageRtnInfo.class, messageRtnInfoList.get(0).getId());
			Sql sql = Sqls
					.create("update personal set email=NULL where id=@id");
			sql.params().set(
					"id",
					String.valueOf(Mvcs.getHttpSession(false).getAttribute(
							OrderConstant.FRONT_USER_ID)));
			dao.execute(sql);
			resultVo.setMsg("解绑成功");
		} else {
			return new ResultVo(ResultVo.STATUS_FAIL, "验证码校验失败");
		}
		return resultVo;
	}

	/**
	 * 查询是否是已经注册的邮箱
	 *
	 * http://localhost:8181/easyshop/personal/checkIsUnusedEmail?email=1
	 * {"status":"success","msg":"false"} {"status":"success","msg":"true"}
	 * {"status":"fail","msg":"参数错误"}
	 *
	 * @param email
	 * @return
	 */
	@At
	public ResultVo checkIsUnusedEmail(@Param("email") String email) {
		if (StringUtils.isEmpty(email)) {
			return new ResultVo(ResultVo.STATUS_FAIL, "参数错误");
		}
		return new ResultVo(String.valueOf(dao.count(Personal.class,
				Cnd.where("email", "=", email)) == 0));
	}

	/**
	 * 查询是否是已经注册的手机号
	 *
	 * http://localhost:8181/easyshop/personal/checkIsUnusedPhone?phone=1
	 * {"status":"success","msg":"false"} {"status":"success","msg":"true"}
	 * {"status":"fail","msg":"参数错误"}
	 *
	 * @param phone
	 * @return
	 */
	@At
	@Filters
	public ResultVo checkIsUnusedPhone(@Param("phone") String phone) {
		if (StringUtils.isEmpty(phone)) {
			return new ResultVo(ResultVo.STATUS_FAIL, "参数错误");
		}
		return new ResultVo(String.valueOf(dao.count(Personal.class,
				Cnd.where("phone", "=", phone)) == 0));
	}

	/**
	 * 忘记密码,通过手机验证修改密码
	 * http://localhost:8181/easyshop/personal/updatePasswordByPhone
	 * ?newPassword=1&phone=1 {"status":"success","msg":"修改成功"}
	 * {"status":"fail","msg":"参数错误"}
	 * 
	 * @param newPassword
	 * @param phone
	 * @return
	 */
	@At
	@Filters
	public ResultVo updatePasswordByPhone(
			@Param("newPassword") String newPassword,
			@Param("phone") String phone) {
		if (StringUtils.isEmpty(newPassword, phone)) {
			return new ResultVo(ResultVo.STATUS_FAIL, "参数错误");
		}
		Sql sql = Sqls
				.create("update personal set password=@newPassword where phone=@phone");
		sql.params().set("newPassword", newPassword);
		sql.params().set("phone", phone);
		dao.execute(sql);
		return new ResultVo("修改成功");
	}

	/**
	 * 登录进去修改密码
	 * http://localhost:8181/easyshop/personal/updatePassword?newPassword=1
	 * {"status":"success","msg":"修改成功"} {"status":"fail","msg":"参数错误"}
	 * 
	 * @param newPassword
	 * @return
	 */
	@At
	public ResultVo updatePassword(@Param("newPassword") String newPassword) {
		if (StringUtils.isEmpty(newPassword)) {
			return new ResultVo(ResultVo.STATUS_FAIL, "参数错误");
		}
		Sql sql = Sqls
				.create("update personal set password=@newPassword where id=@id");
		sql.params().set("newPassword", newPassword);
		sql.params().set(
				"id",
				String.valueOf(Mvcs.getHttpSession(false).getAttribute(
						OrderConstant.FRONT_USER_ID)));
		dao.execute(sql);
		return new ResultVo("修改成功");
	}

    /**
     * 重置密码为123456
     * http://localhost:8181/easyshop/personal/resetPassword?id=1
     * {"status":"success","msg":"修改成功"}
     * {"status":"fail","msg":"参数错误"}
     * @param id 前台用户id
     * @return
     */
	@At
    @Filters(@By(type = CheckBackUserLoginFilter.class))
	public ResultVo resetPassword(@Param("id") String id) {
		if (StringUtils.isEmpty(id)) {
			return new ResultVo(ResultVo.STATUS_FAIL, "参数错误");
		}
		Sql sql = Sqls
				.create("update personal set password='123456' where id=@id");
		sql.params().set("id", id);
		dao.execute(sql);
		return new ResultVo("修改成功");
	}

	/**
	 * 修改手机号 http://localhost:8181/easyshop/personal/updatePhone?newPhone=1
	 * {"status":"success","msg":"修改成功"}
     * {"status":"fail","msg":"参数错误"}
	 * 
	 * @param newPhone
	 * @return
	 */
	@At
	public ResultVo updatePhone(@Param("newPhone") String newPhone) {
		if (StringUtils.isEmpty(newPhone)) {
			return new ResultVo(ResultVo.STATUS_FAIL, "参数错误");
		}
		Sql sql = Sqls
				.create("update personal set phone=@newPhone where id=@id");
		sql.params().set("newPhone", newPhone);
		sql.params().set(
				"id",
				String.valueOf(Mvcs.getHttpSession(false).getAttribute(
						OrderConstant.FRONT_USER_ID)));
		dao.execute(sql);
		return new ResultVo("修改成功");
	}

	/**
	 * 修改邮箱 http://localhost:8181/easyshop/personal/updateEmail?neweEmail=1
	 * {"status":"success","msg":"修改成功"} {"status":"fail","msg":"参数错误"}
	 * 
	 * @param neweEmail
	 * @return
	 */
	@At
	public ResultVo updateEmail(@Param("neweEmail") String neweEmail) {
		if (StringUtils.isEmpty(neweEmail)) {
			return new ResultVo(ResultVo.STATUS_FAIL, "参数错误");
		}
		Sql sql = Sqls
				.create("update personal set email=@neweEmail where id=@id");
		sql.params().set("neweEmail", neweEmail);
		sql.params().set(
				"id",
				String.valueOf(Mvcs.getHttpSession(false).getAttribute(
						OrderConstant.FRONT_USER_ID)));
		dao.execute(sql);
		return new ResultVo("修改成功");
	}

	/**
	 * 修改密码的时候,查询老密码是否正确
	 * http://localhost:8181/easyshop/personal/checkIsCorrectPassword?password=1
	 * {"status":"success","msg":"原密码正确"} {"status":"fail","msg":"原密码错误"}
	 * {"status":"fail","msg":"参数错误"}
	 * 
	 * @param password
	 * @return
	 */
	@At
	public Object checkIsCorrectPassword(@Param("password") String password) {
		if (StringUtils.isEmpty(password)) {
			return new ResultVo(ResultVo.STATUS_FAIL, "参数错误");
		}
		Map<String, Object> result = new HashMap<String, Object>();
		Personal person = dao.fetch(
				Personal.class,
				Cnd.where(
						"id",
						"=",
						String.valueOf(Mvcs.getHttpSession(false).getAttribute(
								OrderConstant.FRONT_USER_ID))).and("password",
						"=", password));
		if (person == null) {
			return new ResultVo(ResultVo.STATUS_FAIL, "原密码错误");
		} else {
			return new ResultVo("原密码正确");
		}
	}

	@At
	@Filters
	public Object printAllSessionAttribute() {
		HttpSession session = Mvcs.getHttpSession(false);
		if (session == null) {
			return "没有session";
		} else {
			String rtnStr = "";
			java.util.Enumeration e = Mvcs.getHttpSession(false)
					.getAttributeNames();
			System.out
					.println("=======================================================================");
			while (e.hasMoreElements()) {
				String sessionName = (String) e.nextElement();
				rtnStr += sessionName + ":" + session.getAttribute(sessionName);
				System.out.println("====" + sessionName + ":"
						+ session.getAttribute(sessionName));
			}
			return rtnStr;
		}
	}

}
