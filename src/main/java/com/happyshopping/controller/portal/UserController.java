package com.happyshopping.controller.portal;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.happyshopping.common.Const;
import com.happyshopping.common.ServerResponse;
import com.happyshopping.pojo.User;
import com.happyshopping.service.IUserService;

@Controller
@RequestMapping("/user/")
public class UserController {

	@Autowired
	private IUserService iUserService;
	
	/**
	 * @Description:user login
	 * @param username
	 * @param password
	 * @param session
	 * @return json data that corresponds to user login interface
	 */
	@RequestMapping(value = "login.do", method = RequestMethod.POST)
	@ResponseBody	// Using jackson plugin of SpringMVC to serialize response result.
	public ServerResponse<User> login(String username, String password, HttpSession session){
		ServerResponse<User> response = this.iUserService.login(username, password);
		if (response.isSuccess()){
			session.setAttribute(Const.CURRENTUSER, response.getData());
		}
		return response;
	}
	
	
	
	
}
