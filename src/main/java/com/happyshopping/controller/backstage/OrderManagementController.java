package com.happyshopping.controller.backstage;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.happyshopping.common.Const;
import com.happyshopping.common.ResponseCode;
import com.happyshopping.common.ServerResponse;
import com.happyshopping.pojo.User;
import com.happyshopping.service.IOrderService;
import com.happyshopping.service.IUserService;

@SuppressWarnings("rawtypes")
@Controller
@RequestMapping("/manage/order/")
public class OrderManagementController {

	@Autowired
	private IUserService iUserService;
	
	@Autowired
	private IOrderService iOrderService;
	
	@RequestMapping(value = "list.do", method = RequestMethod.POST)
	@ResponseBody
	public ServerResponse list(HttpSession session,
			@RequestParam(value = "pageNum", defaultValue = "1") int pageNum,
			@RequestParam(value = "pageSize", defaultValue = "10") int pageSize){
		
		User user = (User) session.getAttribute(Const.CURRENTUSER);
		if (user == null){
			return ServerResponse.createResponse(ResponseCode.NEED_LOGIN.getCode(), 
					ResponseCode.NEED_LOGIN.getDescription());
		}
		if (this.iUserService.isAdmin(user)){
			return this.iOrderService.manageList(pageNum, pageSize);
		}
		return ServerResponse.createResponse(ResponseCode.NO_PERMISSION.getCode(), 
				ResponseCode.NO_PERMISSION.getDescription());
	}
	
	@RequestMapping(value = "detail.do", method = RequestMethod.POST)
	@ResponseBody
	public ServerResponse detail(HttpSession session, Long orderNo){
		User user = (User) session.getAttribute(Const.CURRENTUSER);
		if (user == null){
			return ServerResponse.createResponse(ResponseCode.NEED_LOGIN.getCode(), 
					ResponseCode.NEED_LOGIN.getDescription());
		}
		if (this.iUserService.isAdmin(user)){
			return this.iOrderService.manageDetail(orderNo);
		}
		return ServerResponse.createResponse(ResponseCode.NO_PERMISSION.getCode(), 
				ResponseCode.NO_PERMISSION.getDescription());
	}
	
	@RequestMapping(value = "send_goods.do", method = RequestMethod.POST)
	@ResponseBody
	public ServerResponse sendGoods(HttpSession session, Long orderNo){
		User user = (User) session.getAttribute(Const.CURRENTUSER);
		if (user == null){
			return ServerResponse.createResponse(ResponseCode.NEED_LOGIN.getCode(), 
					ResponseCode.NEED_LOGIN.getDescription());
		}
		if (this.iUserService.isAdmin(user)){
			return this.iOrderService.manageSendGoods(orderNo);
		}
		return ServerResponse.createResponse(ResponseCode.NO_PERMISSION.getCode(), 
				ResponseCode.NO_PERMISSION.getDescription());
	}
	
}
