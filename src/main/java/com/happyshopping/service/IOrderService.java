package com.happyshopping.service;

import java.util.Map;

import com.happyshopping.common.ServerResponse;

@SuppressWarnings("rawtypes")
public interface IOrderService {

	
	ServerResponse pay(Integer userId, Long orderNo, String path);

	ServerResponse alipayCallback(Map<String, String> params);

	ServerResponse<Boolean> orderIsPaid(Integer userId, Long orderNo);

}
