package com.happyshopping.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.happyshopping.common.Const;
import com.happyshopping.common.ResponseCode;
import com.happyshopping.common.ServerResponse;
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
		//全部验证通过后：
		return ServerResponse.createBySuccess("登录成功", user);
	}
	
	
	/**
	 * check records whether existed
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
	
}
