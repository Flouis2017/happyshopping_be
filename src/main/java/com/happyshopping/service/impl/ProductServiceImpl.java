package com.happyshopping.service.impl;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.happyshopping.common.ResponseCode;
import com.happyshopping.common.ServerResponse;
import com.happyshopping.dao.ProductMapper;
import com.happyshopping.pojo.Product;
import com.happyshopping.service.IProductService;

@Service("iProductService")
public class ProductServiceImpl implements IProductService {

	@Autowired
	private ProductMapper productMapper;
	
	/**
	 * insert or update a product record
	 */
	public ServerResponse<String> saveOrUpdate(Product product) {
		ServerResponse<String> response = null;
		if ( product != null ){
			// By default we set the first subImage as main image
			String subImgs = product.getSubImages();
			if (StringUtils.isNotBlank(subImgs)){
				String mainImg = subImgs.substring(0, subImgs.indexOf(","));
				product.setMainImage(mainImg);
			}
			
			// if product's id is existed then do updating
			if (product.getId() != null){
				int resCnt = this.productMapper.updateByPrimaryKey(product);
				response = resCnt > 0 ? ServerResponse.createBySuccess("更新商品成功") : 
					ServerResponse.createByFail("服务器出错，更新商品失败");
			} else {
				int resCnt = this.productMapper.insert(product);
				response = resCnt > 0 ? ServerResponse.createBySuccess("新增商品成功") : 
					ServerResponse.createByFail("服务器出错，新增商品失败");
			}
		} else {
			response = ServerResponse.createResponse(ResponseCode.ILLEGAL_ARGUMENT.getCode(), "商品参数出错");
		}
		return response;
	}

	/**
	 * update product status
	 */
	public ServerResponse<String> updateStatus(Integer productId, Integer status) {
		if (productId==null || status==null){
			return ServerResponse.createResponse(ResponseCode.ILLEGAL_ARGUMENT.getCode(), "参数出错");
		}
		int resCnt = this.productMapper.updateStatus(productId, status);
		if (resCnt>0){
			return ServerResponse.createBySuccess("商品销售状态变更成功");
		}
		return ServerResponse.createByFail("服务器出错，商品销售状态变更失败");
	}

	
	
}
