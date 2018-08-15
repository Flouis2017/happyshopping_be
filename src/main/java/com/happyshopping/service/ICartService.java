package com.happyshopping.service;

import com.happyshopping.common.ServerResponse;
import com.happyshopping.vo.CartVO;

public interface ICartService {

	ServerResponse<CartVO> add(Integer userId, Integer productId, Integer count);
	
	ServerResponse<CartVO> getCartVO(Integer userId);
	
}
