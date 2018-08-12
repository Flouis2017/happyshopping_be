package com.happyshopping.service.impl;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import com.happyshopping.common.ResponseCode;
import com.happyshopping.common.ServerResponse;
import com.happyshopping.dao.CategoryMapper;
import com.happyshopping.dao.ProductMapper;
import com.happyshopping.pojo.Category;
import com.happyshopping.pojo.Product;
import com.happyshopping.service.IProductService;
import com.happyshopping.util.DateTimeUtil;
import com.happyshopping.util.PropertiesUtil;
import com.happyshopping.vo.ProductDetailVO;
import com.happyshopping.vo.ProductListVO;

@Service("iProductService")
public class ProductServiceImpl implements IProductService {

	@Autowired
	private ProductMapper productMapper;
	
	@Autowired
	private CategoryMapper categoryMapper;
	
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

	/**
	 * query product detail by id
	 */
	public ServerResponse<ProductDetailVO> getDetail(Integer productId) {
		if ( productId == null ){
			return ServerResponse.createResponse(ResponseCode.ILLEGAL_ARGUMENT.getCode(), "参数出错");
		}
		Product product = this.productMapper.selectByPrimaryKey(productId);
		if (product==null){
			return ServerResponse.createByFail("商品已下架");
		}
		// assemble pojo to vo
		ProductDetailVO productDetailVO = this.assembleProductDetailVO(product);
		return ServerResponse.createBySuccess(productDetailVO);
	}
	
	/**
	 * assemble product object to its vo
	 */
	public ProductDetailVO assembleProductDetailVO(Product product){
		ProductDetailVO productDetailVO = new ProductDetailVO();
		productDetailVO.setId(product.getId());
		productDetailVO.setSubtitle(product.getSubtitle());
		productDetailVO.setCategoryId(product.getCategoryId());
		productDetailVO.setName(product.getName());
		productDetailVO.setMainImage(product.getMainImage());
		productDetailVO.setSubImages(product.getSubImages());
		productDetailVO.setDetail(product.getDetail());
		productDetailVO.setPrice(product.getPrice());
		productDetailVO.setStock(product.getStock());
		productDetailVO.setStatus(product.getStatus());
		
		productDetailVO.setImageHost(PropertiesUtil.getProperty("ftp.server.http.prefix", "http://img.flouis.com/"));
		
		Category category = this.categoryMapper.selectByPrimaryKey(product.getCategoryId());
		if (category==null){
			productDetailVO.setParentCategoryId(0); // default use root category
		} else {
			productDetailVO.setParentCategoryId(category.getParentId());
		}
		
		productDetailVO.setCreateTime(DateTimeUtil.dateToStr(product.getCreateTime()));
		productDetailVO.setUpdateTime(DateTimeUtil.dateToStr(product.getUpdateTime()));
		return productDetailVO;
	}

	/**
	 * paging query to get product list
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public ServerResponse<PageInfo> getProductList(int pageNum, int pageSize) {
		// using PageHelper.startPage to start paging
		PageHelper.startPage(pageNum, pageSize);
		
		// do query operation and assemble result list to vo list
		List<Product> productList = this.productMapper.selectList();
		List<ProductListVO> productListVOList = Lists.newArrayList();
		for (Product tmp : productList){
			ProductListVO productListVO = this.assembleProductListVO(tmp);
			productListVOList.add(productListVO);
		}
		
		//return PageInfo object
		PageInfo pageInfo = new PageInfo(productList);
		pageInfo.setList(productListVOList);
		return ServerResponse.createBySuccess(pageInfo);
	}
	
	public ProductListVO assembleProductListVO(Product product){
		ProductListVO productListVO = new ProductListVO();
		productListVO.setId(product.getId());
		productListVO.setName(product.getName());
		productListVO.setCategoryId(product.getCategoryId());
		productListVO.setMainImage(product.getMainImage());
		productListVO.setPrice(product.getPrice());
		productListVO.setSubtitle(product.getSubtitle());
		productListVO.setStatus(product.getStatus());
		productListVO.setImageHost(PropertiesUtil.getProperty("ftp.server.http.prefix", "http://img.flouis.com/"));
		return productListVO;
	}

	/**
	 * search products by productName and productId then page the result
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public ServerResponse<PageInfo> searchProducts(String productName, Integer productId, int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);
		// if productName is not blank then add "%" for fuzzy match
		if (StringUtils.isNotBlank(productName)){
			productName = new StringBuilder().append("%").append(productName).append("%").toString();
		}
		List<Product> productList = this.productMapper.selectListByNameAndId(productName, productId);
		List<ProductListVO> productListVOList = Lists.newArrayList();
		for (Product tmp : productList){
			ProductListVO productListVO = this.assembleProductListVO(tmp);
			productListVOList.add(productListVO);
		}
		
		//return PageInfo object
		PageInfo pageInfo = new PageInfo(productList);
		pageInfo.setList(productListVOList);
		return ServerResponse.createBySuccess(pageInfo);
	}
	
}
