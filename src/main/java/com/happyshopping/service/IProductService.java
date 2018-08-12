package com.happyshopping.service;

import com.github.pagehelper.PageInfo;
import com.happyshopping.common.ServerResponse;
import com.happyshopping.pojo.Product;
import com.happyshopping.vo.ProductDetailVO;

public interface IProductService {

	ServerResponse<String> saveOrUpdate(Product product);

	ServerResponse<String> updateStatus(Integer productId, Integer status);

	ServerResponse<ProductDetailVO> getDetail(Integer productId);

	@SuppressWarnings("rawtypes")
	ServerResponse<PageInfo> getProductList(int pageNum, int pageSize);
	
	@SuppressWarnings("rawtypes")
	ServerResponse<PageInfo> searchProducts(String productName, Integer productId, int pageNum, int pageSize);
	
}
