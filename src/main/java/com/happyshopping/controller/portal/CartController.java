package com.happyshopping.controller.portal;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.happyshopping.common.Const;
import com.happyshopping.common.ResponseCode;
import com.happyshopping.common.ServerResponse;
import com.happyshopping.pojo.User;
import com.happyshopping.service.ICartService;
import com.happyshopping.vo.CartVO;

@Controller
@RequestMapping("/cart/")
public class CartController {

	@Autowired
	private ICartService iCartService;
	
	@RequestMapping(value = "add.do", method = RequestMethod.POST)
	@ResponseBody
	public ServerResponse<CartVO> add(HttpSession session, Integer productId, Integer count){
		User user = (User)session.getAttribute(Const.CURRENTUSER);
		if (user == null){
			return ServerResponse.createResponse(ResponseCode.NEED_LOGIN.getCode(), "用户未登录");
		}
		return this.iCartService.add(user.getId(), productId, count);
	}
	
	@RequestMapping(value = "list.do", method = RequestMethod.POST)
	@ResponseBody
	public ServerResponse<CartVO> list(HttpSession session){
		User user = (User)session.getAttribute(Const.CURRENTUSER);
		if (user == null){
			return ServerResponse.createResponse(ResponseCode.NEED_LOGIN.getCode(), "用户未登录");
		}
		return this.iCartService.getCartVO(user.getId());
	}
	
	@RequestMapping(value = "update.do", method = RequestMethod.POST)
	@ResponseBody
	public ServerResponse<CartVO> update(HttpSession session, Integer productId, Integer count){
		User user = (User)session.getAttribute(Const.CURRENTUSER);
		if (user == null){
			return ServerResponse.createResponse(ResponseCode.NEED_LOGIN.getCode(), "用户未登录");
		}
		return this.iCartService.update(user.getId(), productId, count);
	}
	
	@RequestMapping(value = "remove_products.do", method = RequestMethod.POST)
	@ResponseBody
	public ServerResponse<CartVO> removeProducts(HttpSession session, String productIds){
		User user = (User)session.getAttribute(Const.CURRENTUSER);
		if (user == null){
			return ServerResponse.createResponse(ResponseCode.NEED_LOGIN.getCode(), "用户未登录");
		}
		return this.iCartService.removeProducts(user.getId(), productIds);
	}
	
	
	/**
	 * 全选 select all
	 */
	@RequestMapping(value = "select_all.do", method = RequestMethod.POST)
	@ResponseBody
	public ServerResponse<CartVO> selectAll(HttpSession session){
		User user = (User)session.getAttribute(Const.CURRENTUSER);
		if (user == null){
			return ServerResponse.createResponse(ResponseCode.NEED_LOGIN.getCode(), "用户未登录");
		}
		return this.iCartService.selectOrUnselect(user.getId(), null, Const.Cart.CHECKED);
	}
	
	/**
	 * 全不选 select none
	 */
	@RequestMapping(value = "unselect_all.do", method = RequestMethod.POST)
	@ResponseBody
	public ServerResponse<CartVO> unselectAll(HttpSession session){
		User user = (User)session.getAttribute(Const.CURRENTUSER);
		if (user == null){
			return ServerResponse.createResponse(ResponseCode.NEED_LOGIN.getCode(), "用户未登录");
		}
		return this.iCartService.selectOrUnselect(user.getId(), null, Const.Cart.UNCHECKED);
	}
	
	/**
	 * 单独选 single select
	 */
	@RequestMapping(value = "select.do", method = RequestMethod.POST)
	@ResponseBody
	public ServerResponse<CartVO> select(HttpSession session, Integer productId){
		User user = (User)session.getAttribute(Const.CURRENTUSER);
		if (user == null){
			return ServerResponse.createResponse(ResponseCode.NEED_LOGIN.getCode(), "用户未登录");
		}
		return this.iCartService.selectOrUnselect(user.getId(), productId, Const.Cart.CHECKED);
	}
	
	/**
	 * 单独不选 single select none
	 */
	@RequestMapping(value = "unselect.do", method = RequestMethod.POST)
	@ResponseBody
	public ServerResponse<CartVO> unselect(HttpSession session, Integer productId){
		User user = (User)session.getAttribute(Const.CURRENTUSER);
		if (user == null){
			return ServerResponse.createResponse(ResponseCode.NEED_LOGIN.getCode(), "用户未登录");
		}
		return this.iCartService.selectOrUnselect(user.getId(), productId, Const.Cart.UNCHECKED);
	}
	
	/**
	 * 获取购物车中商品的总数，用在门户首页中最顶上导航栏中购物车图标右侧显示
	 */
	@RequestMapping(value="get_cart_product_count.do", method=RequestMethod.POST)
	@ResponseBody
	public ServerResponse<Integer> getCartProductCount(HttpSession session){
		User user = (User)session.getAttribute(Const.CURRENTUSER);
		if (user == null){
			return ServerResponse.createBySuccess(0);
		}
		return this.iCartService.getCartProductCount(user.getId());
	}
	
}
