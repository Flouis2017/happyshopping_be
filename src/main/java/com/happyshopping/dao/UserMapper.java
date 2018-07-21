package com.happyshopping.dao;

import org.apache.ibatis.annotations.Param;

import com.happyshopping.pojo.User;

public interface UserMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(User record);

    int insertSelective(User record);

    User selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(User record);

    int updateByPrimaryKey(User record);

    //Added functions:
	int checkIsExisted(@Param("sqlString")String sqlString);
	
	int checkUsername(String username);
	
	int checkEmail(String email);

	User selectLoginUser(@Param("username")String username, @Param("md5pwd")String md5pwd);

	int checkPassword(@Param("username")String username, @Param("md5pwd")String password);

	int resetPassword(@Param("username")String username, @Param("md5newPwd")String password);

	int checkEmailById(@Param("userId")Integer id, @Param("email")String email);
}