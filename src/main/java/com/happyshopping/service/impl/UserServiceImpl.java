package com.happyshopping.service.impl;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.happyshopping.common.Const;
import com.happyshopping.common.ResponseCode;
import com.happyshopping.common.ServerResponse;
import com.happyshopping.common.TokenCache;
import com.happyshopping.dao.UserMapper;
import com.happyshopping.pojo.User;
import com.happyshopping.service.IUserService;
import com.happyshopping.util.MD5Util;
import com.happyshopping.util.StringUtil;

/**
 * @author Flouis
 * @date 2018年7月19日
 * @Description portal user service implementation 
 */
@Service("iUserService")
public class UserServiceImpl implements IUserService {

	@Autowired
	private UserMapper userMapper;
	
	/**
	 * User Login
	 */
	public ServerResponse<User> login(String username, String password) {
		//验证username是否存在：
		if (!checkIsExisted(Const.USERNAME,username).getData()){
			return ServerResponse.createByFail("用户名不存在");
		}
		//当username存在继续验证密码是否正确：
		String md5pwd = MD5Util.MD5EncodeUtf8(password);
		User user = this.userMapper.selectLoginUser(username, md5pwd);
		if (user==null){
			return ServerResponse.createByFail("密码错误");
		}
		
		//全部验证通过后，将密码设置空，然后返回：
		user.setPassword(StringUtils.EMPTY);
		return ServerResponse.createBySuccess("登录成功", user);
	}
	
	
	/**
	 * check records whether existed
	 * true:existed
	 * false:not existed
	 */
	public ServerResponse<Boolean> checkIsExisted(String type, String str){
		boolean flag = false;
		//先判断type值是否为blank，如果为blank直接返回“参数错误”(注意blank和null是有区别的)：
		//Make sure that value of type is blank or not and if it is blank just return a fail
		//response with "argument error" message(Attention: blank is different from null)
		if (StringUtil.checkTypeValid(type)){
			String sqlString = "select count(1) from hs_user where "+type+" = '"+str+"'";
			int count = this.userMapper.checkIsExisted(sqlString);
			if ( count > 0 ){
				flag = true;
			}
		} else {
			return ServerResponse.createByFail("参数出错");
		}
		return ServerResponse.createResponse(ResponseCode.COMMON.getCode(), flag);
	}
	
	/**
	 * check the account whether exists may be by username / email / phone etc.
	 */
	public ServerResponse<Boolean> checkAccount(String type, String str){
		return this.checkIsExisted(type, str);
	}
	
	
	/**
	 * User register
	 */
	public ServerResponse<String> register(User user){
		//校验用户名是否已经存在：
		if (this.checkIsExisted(Const.USERNAME, user.getUsername()).getData()){
			return ServerResponse.createByFail("用户名已存在");
		}
		//校验邮箱是否已经占用：
		if (this.checkIsExisted(Const.EMAIL, user.getEmail()).getData()){
			return ServerResponse.createByFail("邮箱已被占用");
		}
		
		//insert 新用户：
		user.setRole(Const.Role.ROLE_CUSTOMER); // 设置用户权限，默认是普通用户权限
		user.setPassword(MD5Util.MD5EncodeUtf8(user.getPassword())); // 将密码转为md5进行存储
		int count = this.userMapper.insert(user);
		if (count>0){
			return ServerResponse.createBySuccess("注册成功");
		} else {
			return ServerResponse.createByFail("服务器出错，注册失败");
		}
	}
	
	/**
	 * users reset their password after normal login
	 */
	public ServerResponse<String> resetPassword(String username, String password, String newPassword) {
		//校验旧密码是否正确：
		int count = this.userMapper.checkPassword(username, MD5Util.MD5EncodeUtf8(password));
		if (count>0){
			// 进行update操作：
			int count2 = this.userMapper.resetPassword(username, MD5Util.MD5EncodeUtf8(newPassword));
			if (count2>0){
				return ServerResponse.createBySuccess("密码修改成功");
			}
		} else {
			return ServerResponse.createByFail("旧密码错误");
		}
		return ServerResponse.createByFail("服务器出错，密码修改失败");
	}


	/**
	 * 
	 */
	public ServerResponse<User> updateUserInfo(User user) {
		//检查更改的邮箱是否已经别其他用户使用了 ( 注意id是用!= )
		//select count(1) from hs_user where id != ... and email = ....
		int count = this.userMapper.checkEmailById(user.getId(), user.getEmail());
		if (count>0){
			return ServerResponse.createByFail("邮箱已被占用");
		}
		count = this.userMapper.updateByPrimaryKeySelective(user);
		if (count>0){
			return ServerResponse.createBySuccess("更新成功");
		}
		return ServerResponse.createByFail("服务器出错，更新失败");
	}


	/**
	 * 根据用户id获取用户信息
	 * get user info by user id but we hide the password when responding
	 */
	public ServerResponse<User> getUserInfoById(Integer id) {
		User user = this.userMapper.selectByPrimaryKey(id);
		if (user==null){
			return ServerResponse.createByFail("当前用户不存在");
		}
		user.setPassword(StringUtils.EMPTY);
		return ServerResponse.createBySuccess(user);
	}


	/**
	 * get secret question to find password back
	 */
	public ServerResponse<String> getSecrectQuestion(String username) {
		// check username is existed or not:
		if (!this.checkIsExisted(Const.USERNAME, username).getData()){
			return ServerResponse.createByFail("用户不存在");
		}
		String question = this.userMapper.getQuestionByUsername(username);
		if (StringUtils.isNoneBlank(question)){
			return ServerResponse.createBySuccess(null, question);
		}
		return ServerResponse.createByFail("密保问题为空");
	}

	/**
	 * check secret answer
	 */
	public ServerResponse<String> checkSecretAnswer(String username, String answer, String securityCode) {
		// check username is existed or not:
		if (!this.checkIsExisted(Const.USERNAME, username).getData()){
			return ServerResponse.createByFail("用户不存在");
		}
		
		// check if security is blank
		// 基本上不会出现，因为前端页面验证码输入框为空的话，无法回答密保问题，直接就无法提交
		// 密保问题，根本就无法执行到这里，但是以防万一和测试，这里也做一个校验：
		if (StringUtils.isBlank(securityCode)){
			return ServerResponse.createByFail("验证码不可为空");
		}
		int resCnt = this.userMapper.checkSecretAnswer(username, answer);
		if (resCnt>0){
			//passing validation of secret question set token and respond it to wait user reset pwd
			//token generation rule is encoding securityCode by md5
			String token = MD5Util.MD5EncodeUtf8(securityCode);
			TokenCache.setCache(TokenCache.TOKEN_PRIFIX + username, token);
			return ServerResponse.createBySuccess("密保问题回答正确", token);
		} else {
			return ServerResponse.createByFail("回答错误");
		}
	}

	/**
	 * reset password after checking secret question 
	 */
	public ServerResponse<String> resetForgottenPassword(String username, String newPassword, String token) {
		// check username is existed or not:
		if (!this.checkIsExisted(Const.USERNAME, username).getData()){
			return ServerResponse.createByFail("用户不存在");
		}
		
		// check the received token is blank or not
		if (StringUtils.isBlank(token)){
//			return ServerResponse.createByFail("参数错误，token需要传递");
			return ServerResponse.createByFail("验证码不可为空");
		}
		
		// check if local token expired
		String localToken = TokenCache.getCache(TokenCache.TOKEN_PRIFIX + username);
		if (StringUtils.isBlank(localToken)){
//			return ServerResponse.createByFail("token无效或已过期，请重新输入验证码");
			return ServerResponse.createByFail("验证码无效，请重新输入");
		}
		
		// reset password if token equals localToken:
		if (StringUtils.equals(token, localToken)){
			String md5Password = MD5Util.MD5EncodeUtf8(newPassword);
			int resCnt = this.userMapper.updatePassword(username, md5Password);
			if (resCnt>0){
				return ServerResponse.createBySuccess("密码重置成功");
			} else {
				return ServerResponse.createByFail("服务端出错，密码重置失败");
			}
		} else {
//			return ServerResponse.createByFail("前后token不一致，请尝试再次找回密码");
			return ServerResponse.createByFail("验证码异常，请尝试再次找回密码");
		}
		
	}


	/**
	 * check if user is administrator
	 */
	public ServerResponse<Boolean> checkAdminRole(User user) {
		if (user == null){
			return ServerResponse.createResponse(ResponseCode.ILLEGAL_ARGUMENT.getCode(), "用户对象为空");
		}
		ServerResponse<Boolean> res = ServerResponse.createResponse(ResponseCode.COMMON.getCode(), false);
		if (user.getRole() == Const.Role.ROLE_ADMIN){
			res.setData(true);;
		}
		return res;
	}
	
	/**
	 * @Description:check if current user owns administrator permission 
	 * @param user
	 * @return true / false
	 */
	public boolean isAdmin(User user){
		boolean flag = false;
		if (user.getRole() == Const.Role.ROLE_ADMIN){
			flag = true;
		} 
		return flag;
	}

}
