package com.happyshopping.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.happyshopping.pojo.Shipping;

public interface ShippingMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Shipping record);

    int insertSelective(Shipping record);

    Shipping selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Shipping record);

    int updateByPrimaryKey(Shipping record);

	int deleteByUserIdAndShippingId(@Param("userId") Integer userId, @Param("shippingId") Integer shippingId);

	int update(Shipping record);

	Shipping selectByUserIdAndShippingId(@Param("userId") Integer userId, @Param("shippingId") Integer shippingId);

	List<Shipping> selectByUserId(Integer userId);
	
	
}