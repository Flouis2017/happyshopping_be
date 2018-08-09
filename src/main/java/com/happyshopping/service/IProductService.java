package com.happyshopping.service;

import com.happyshopping.common.ServerResponse;
import com.happyshopping.pojo.Product;

public interface IProductService {

	ServerResponse<String> saveOrUpdate(Product product);

	ServerResponse<String> updateStatus(Integer productId, Integer status);
	
}
