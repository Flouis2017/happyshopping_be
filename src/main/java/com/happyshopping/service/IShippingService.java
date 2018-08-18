package com.happyshopping.service;

import com.github.pagehelper.PageInfo;
import com.happyshopping.common.ServerResponse;
import com.happyshopping.pojo.Shipping;

public interface IShippingService {

	ServerResponse<Integer> add(Integer userId, Shipping shipping);

	ServerResponse<String> delete(Integer userId, Integer shippingId);

	ServerResponse<String> update(Integer userId, Shipping shipping);

	ServerResponse<Shipping> select(Integer userId, Integer shippingId);

	@SuppressWarnings("rawtypes")
	ServerResponse<PageInfo> list(Integer userId, int pageNum, int pageSize);

}
