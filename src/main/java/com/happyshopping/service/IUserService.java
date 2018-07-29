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

	ServerResponse<Boolean> checkAccount(String type, String str);

	ServerResponse<String> register(User user);

	ServerResponse<String> resetPassword(String username, String password, String newPassword);

	ServerResponse<User> updateUserInfo(User user);

	ServerResponse<User> getUserInfoById(Integer id);

	ServerResponse<String> getSecrectQuestion(String username);

	ServerResponse<String> checkSecretAnswer(String username, String answer);

	ServerResponse<String> resetForgottenPassword(String username, String newPassword);

}
