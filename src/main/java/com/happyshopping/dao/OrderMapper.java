package com.happyshopping.dao;

import java.util.Date;
import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.happyshopping.pojo.Order;

public interface OrderMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Order record);

    int insertSelective(Order record);

    Order selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Order record);

    int updateByPrimaryKey(Order record);

	Order selectByUserIdAndOrderNo(@Param("userId") Integer userId, @Param("orderNo") Long orderNo);

	Order selectByOrderNo(Long orderNo);

	int updateOrderStatus(@Param("id") Integer id, @Param("status") int status);

	List<Order> selectByUserId(Integer userId);

	List<Order> selectAllOrders();

	int updateOrderAfterSending(@Param("orderNo") Long orderNo, @Param("status") int status, @Param("sendTime") Date sendTime);
}