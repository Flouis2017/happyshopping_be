package com.happyshopping.service;

import com.happyshopping.common.ServerResponse;
import com.happyshopping.vo.CartVO;

public interface ICartService {

	ServerResponse<CartVO> add(Integer userId, Integer productId, Integer count);
	
	ServerResponse<CartVO> getCartVO(Integer userId);

	ServerResponse<CartVO> update(Integer userId, Integer productId, Integer count);

	ServerResponse<CartVO> removeProducts(Integer userId, String productIds);

	ServerResponse<Integer> getCartProductCount(Integer userId);
	
	ServerResponse<CartVO> selectOrUnselect(Integer userId, Integer productId, Integer isChecked);
	
}
