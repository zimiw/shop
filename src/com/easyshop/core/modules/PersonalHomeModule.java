package com.easyshop.core.modules;

import javax.servlet.http.HttpSession;

import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.mvc.annotation.At;
import org.nutz.mvc.annotation.Fail;
import org.nutz.mvc.annotation.Ok;
import org.nutz.mvc.annotation.Param;

/**
 * 用户个人中心
 * 
 * @author luocz
 */
@IocBean
@At("personalhome")
@Fail("http:500")
public class PersonalHomeModule {

	/**
	 * 用户个人中心首页
	 */
	@At
	@Ok("redirect:/front/personal_home.html")
	public void index(HttpSession session) {
		System.out.println("个人中心。。。");
	}

	/**
	 * 用户收藏夹页面
	 */
	@At("/favorite")
	@Ok("redirect:/front/personal_myFavorite.html")
	public void favoriteIndex() {
		System.out.println("用户收藏夹。。。");
	}

	/**
	 * 用户订单页面
	 */
	@At("/myorder")
	@Ok("redirect:/front/personal_myOrder.html")
	public void orderIndex() {
		System.out.println("我的订单。。。");
	}

	/**
	 * 用户订单详情页面
	 */
	@At("/myorderdetail")
	@Ok("jsp:/front/personal_orderDetail.jsp")
	public String orderDetailIndex(@Param("orderId") String orderId) {
		System.out.println("我的订单详情。。。");
		return orderId;
	}

	/**
	 * 用户订单退货详情页面
	 */
	@At("/myrefunddetail")
	@Ok("jsp:/front/personal_returnDetail.jsp")
	public String orderRefundIndex(@Param("orderId") String orderId,
			@Param("productId") String productId) {
		System.out.println("我的订单退货详情。。。");
		return orderId + "&" + productId;
	}

}
