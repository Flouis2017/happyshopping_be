package com.happyshopping.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.happyshopping.pojo.OrderItem;

public interface OrderItemMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(OrderItem record);

    int insertSelective(OrderItem record);

    OrderItem selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(OrderItem record);

    int updateByPrimaryKey(OrderItem record);

	List<OrderItem> selectByUserIdAndOrderNo(@Param("userId") Integer userId, @Param("orderNo") Long orderNo);

	void batchInsert(@Param("orderItemList") List<OrderItem> orderItemList);
}