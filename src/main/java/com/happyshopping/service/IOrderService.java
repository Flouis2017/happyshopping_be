package com.happyshopping.service;

import java.util.Map;

import com.happyshopping.common.ServerResponse;

@SuppressWarnings("rawtypes")
public interface IOrderService {
	
	ServerResponse pay(Integer userId, Long orderNo, String path);

	ServerResponse alipayCallback(Map<String, String> params);

	ServerResponse<Boolean> queryOrderIsPaid(Integer userId, Long orderNo);

	ServerResponse create(Integer userId, Integer shippingId);

	ServerResponse cancel(Integer userId, Long orderNo);

	ServerResponse getOrderDetail(Integer userId, Long orderNo);

	ServerResponse getOrderList(Integer userId, int pageNum, int pageSize);

	ServerResponse manageList(int pageNum, int pageSize);

	ServerResponse manageDetail(Long orderNo);

	ServerResponse manageSendGoods(Long orderNo);

}
