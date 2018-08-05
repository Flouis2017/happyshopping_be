package com.happyshopping.controller.backstage;


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
import com.happyshopping.service.IUserService;

@Controller
@RequestMapping("/manage/user/")
public class UserManagementController {

	@Autowired
	private IUserService iUserService;
	
	/**
	 * @Description:back stage management user login
	 * @param username
	 * @param password
	 * @param session
	 * @return
	 */
	@RequestMapping(value = "login.do", method = RequestMethod.POST)
	@ResponseBody
	public ServerResponse<User> login(String username, String password, HttpSession session){
		ServerResponse<User> response = this.iUserService.login(username, password);
		// authority control: No permission to login management system without role of administrator
		if (response.isSuccess()){
			User user = response.getData();
			if (user.getRole() == Const.Role.ROLE_ADMIN){
				session.setAttribute(Const.CURRENTUSER, user);
			} else {
				return ServerResponse.createResponse(ResponseCode.NO_PERMISSION.getCode(), "您不是管理员，登录失败");
			}
		}
		return response;
	}
	
	/**
	 * @Description:admin logout
	 * @param session
	 * @return
	 */
	@RequestMapping(value = "logout.do", method = RequestMethod.POST)
	@ResponseBody
	public ServerResponse<String> logout(HttpSession session){
		try{
			session.removeAttribute(Const.CURRENTUSER);
			return ServerResponse.createBySuccess("登出成功");
		} catch(Exception e){
			return ServerResponse.createByFail("服务器异常，登出失败");
		}
	}
	
	
}
