package com.happyshopping.service;

import com.happyshopping.common.ServerResponse;
import com.happyshopping.pojo.User;

/**
 * @author Flouis
 * @date 2018年7月19日
 * @Description portal user service interface
 */
public interface IUserService {

	ServerResponse<User> login(String username, String password);

}
