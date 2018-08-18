package com.happyshopping.controller.portal;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.github.pagehelper.PageInfo;
import com.happyshopping.common.Const;
import com.happyshopping.common.ResponseCode;
import com.happyshopping.common.ServerResponse;
import com.happyshopping.pojo.Shipping;
import com.happyshopping.pojo.User;
import com.happyshopping.service.IShippingService;

@Controller
@RequestMapping("/shipping/")
public class ShippingController {
	
	@Autowired
	private IShippingService iShippingService;
	
	@RequestMapping(value = "add.do", method = RequestMethod.POST)
	@ResponseBody
	public ServerResponse<Integer> add(HttpSession session, Shipping shipping){
		User user = (User) session.getAttribute(Const.CURRENTUSER);
		if (user == null){
			return ServerResponse.createResponse(ResponseCode.NEED_LOGIN.getCode(), "用户未登录");
		}
		return this.iShippingService.add(user.getId(), shipping);
	}
	
	@RequestMapping(value = "delete.do", method = RequestMethod.POST)
	@ResponseBody
	public ServerResponse<String> delete(HttpSession session, Integer shippingId){
		User user = (User) session.getAttribute(Const.CURRENTUSER);
		if (user == null){
			return ServerResponse.createResponse(ResponseCode.NEED_LOGIN.getCode(), "用户未登录");
		}
		// 添加userId参数是为了防止横向越权，即一个普通用户随意传递一个shippingId就可能
		// 将原本不属于他的收货地址删除
		return this.iShippingService.delete(user.getId(), shippingId);
	}

	@RequestMapping(value = "update.do", method = RequestMethod.POST)
	@ResponseBody
	public ServerResponse<String> update(HttpSession session, Shipping shipping){
		User user = (User) session.getAttribute(Const.CURRENTUSER);
		if (user == null){
			return ServerResponse.createResponse(ResponseCode.NEED_LOGIN.getCode(), "用户未登录");
		}
		return this.iShippingService.update(user.getId(), shipping);
	}
	
	@RequestMapping(value = "select.do", method = RequestMethod.POST)
	@ResponseBody
	public ServerResponse<Shipping> select(HttpSession session, Integer shippingId){
		User user = (User) session.getAttribute(Const.CURRENTUSER);
		if (user == null){
			return ServerResponse.createResponse(ResponseCode.NEED_LOGIN.getCode(), "用户未登录");
		}
		// 添加userId同样是为了防止横向越权
		// add parameter of userId in order to avoid horizontal overpower
		return this.iShippingService.select(user.getId(), shippingId);
	}
	
	@SuppressWarnings("rawtypes")
	@RequestMapping(value = "list.do", method = RequestMethod.POST)
	@ResponseBody
	public ServerResponse<PageInfo> list(HttpSession session, 
			@RequestParam(value = "pageNum", defaultValue = "1") int pageNum, 
			@RequestParam(value = "pageSize", defaultValue = "10") int pageSize){
		User user = (User) session.getAttribute(Const.CURRENTUSER);
		if (user == null){
			return ServerResponse.createResponse(ResponseCode.NEED_LOGIN.getCode(), "用户未登录");
		}
		return this.iShippingService.list(user.getId(), pageNum, pageSize);
	}

}
