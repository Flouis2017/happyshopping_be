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
	
	/**
	 * @Description:user logout
	 * @param session
	 * @return 
	 */
	@RequestMapping(value = "logout.do", method = RequestMethod.POST)
	@ResponseBody
	public ServerResponse<String> logout(HttpSession session){
		try{
			session.removeAttribute(Const.CURRENTUSER);
			return ServerResponse.createBySuccess("登出成功");
		} catch (Exception e){
			return ServerResponse.createByFail("服务器异常，登出失败");
		}
	}
	
	/**
	 * @Description:check the account whether exists may be by username / email / phone etc.
	 * @param type
	 * @param str
	 * @return true / false
	 */
	@RequestMapping(value = "check_account.do", method = RequestMethod.POST)
	@ResponseBody
	public ServerResponse<Boolean> checkAccount(String type, String str){
		return this.iUserService.checkAccount(type, str);
	}
	
	
	/**
	 * @Description:user register
	 * @param user
	 * @param session
	 * @return 
	 */
	@RequestMapping(value = "register.do", method = RequestMethod.POST)
	@ResponseBody
	public ServerResponse<String> register(User user){
		return this.iUserService.register(user);
	}
	
	
	/**
	 * @Description:get current login user information
	 * @param session
	 * @return
	 */
	@RequestMapping(value = "get_current_user_info.do", method = RequestMethod.POST)
	@ResponseBody
	public ServerResponse<User> getCurrentUserInfo(HttpSession session){
		User user = (User) session.getAttribute(Const.CURRENTUSER);
		if (user==null){
			return ServerResponse.createResponse(ResponseCode.NEED_LOGIN.getCode(), "获取个人信息，请先登录");
		}
		return ServerResponse.createBySuccess(user);
	}
	
	/**
	 * @Description:users reset password after normal login
	 * @param session
	 * @param password
	 * @param newPassword
	 * @return
	 */
	@RequestMapping(value = "reset_password.do", method = RequestMethod.POST)
	@ResponseBody
	public ServerResponse<String> resetPassword(HttpSession session,
			String password, String newPassword){
		User user = (User) session.getAttribute(Const.CURRENTUSER);
		if (user==null){
			return ServerResponse.createByFail("用户未登录");
		}
		return this.iUserService.resetPassword(user.getUsername(), password, newPassword);
	}
	
	
	/**
	 * @Description:update personal information after normal login
	 * @param session
	 * @param user
	 * @return
	 */
	@RequestMapping(value = "update_user_info.do", method = RequestMethod.POST)
	@ResponseBody
	public ServerResponse<User> updateUserInfo(HttpSession session, User user){
		User currentUser = (User) session.getAttribute(Const.CURRENTUSER);
		if (currentUser==null){
			return ServerResponse.createByFail("用户未登录");
		}
		
		//设置username和id因为这两个属性值我们不允许修改，而且也需要这两个属性作为where的条件：
		user.setId(currentUser.getId());
		user.setUsername(currentUser.getUsername());
		ServerResponse<User> result = this.iUserService.updateUserInfo(user);
		if (result.isSuccess()){
			//根据用户id查出更新的用户信息，然后塞到session中：
			User tmpUser = this.iUserService.getUserInfoById(currentUser.getId()).getData();
			result.setData(tmpUser);
			session.setAttribute(Const.CURRENTUSER, tmpUser);
		}
		return result;
	}
	
}
