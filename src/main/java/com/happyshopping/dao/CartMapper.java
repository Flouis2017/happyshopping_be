package com.happyshopping.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.happyshopping.pojo.Cart;

public interface CartMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Cart record);

    int insertSelective(Cart record);

    Cart selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Cart record);

    int updateByPrimaryKey(Cart record);

	Cart selectByUserIdAndProductId(@Param("userId")Integer userId, @Param("productId")Integer productId);

	int selectUncheckedCountByUserId(Integer userId);

	List<Cart> selectCartByUserId(Integer userId);

	int updateQuantityById(@Param("id")Integer id, @Param("quantity")Integer count);

	int deleteByUserIdAndProductIdList(@Param("userId")Integer userId, @Param("productIdList")List<String> productIdList);

	int getCartProductCount(Integer userId);

	int checkOrUncheckProduct(@Param("userId")Integer userId, @Param("productId")Integer productId, @Param("checked")int isChecked);
}