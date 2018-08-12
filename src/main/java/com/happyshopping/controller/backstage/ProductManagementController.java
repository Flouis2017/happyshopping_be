package com.happyshopping.controller.backstage;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.happyshopping.common.Const;
import com.happyshopping.common.ResponseCode;
import com.happyshopping.common.ServerResponse;
import com.happyshopping.pojo.Product;
import com.happyshopping.pojo.User;
import com.happyshopping.service.IProductService;
import com.happyshopping.service.IUserService;

@Controller
@RequestMapping("/manage/product/")
public class ProductManagementController {
	
	@Autowired
	private IUserService iUserService;
	
	@Autowired
	private IProductService iProductService;

	/**
	 * @Description:do saving or updating. It is different operations in front end
	 * but we can use the same function to save or update depending on whether 
	 * product's id is existed.
	 * @param session
	 * @param product
	 * @return
	 */
	@RequestMapping(value="save_or_update.do", method=RequestMethod.POST)
	@ResponseBody
	public ServerResponse<String> saveOrUpdate(HttpSession session, Product product){
		User user = (User) session.getAttribute(Const.CURRENTUSER);
		if (user == null){
			return ServerResponse.createResponse(ResponseCode.NEED_LOGIN.getCode(), "用户未登录");
		}
		if (this.iUserService.isAdmin(user)){
			// do saving or updating
			return this.iProductService.saveOrUpdate(product);
		}
		return ServerResponse.createResponse(ResponseCode.NO_PERMISSION.getCode(), "无权限操作");
	}
	
	/**
	 * @Description:update sale status, number 1 means on sale
	 * @param session
	 * @param productId
	 * @param status
	 * @return
	 */
	@RequestMapping(value="set_sale_status.do", method=RequestMethod.POST)
	@ResponseBody
	public ServerResponse<String> setSaleStatus(HttpSession session, Integer productId, Integer status){
		User user = (User) session.getAttribute(Const.CURRENTUSER);
		if (user == null){
			return ServerResponse.createResponse(ResponseCode.NEED_LOGIN.getCode(), "用户未登录");
		}
		if (this.iUserService.isAdmin(user)){
			// update product status
			return this.iProductService.updateStatus(productId, status);
		}
		return ServerResponse.createResponse(ResponseCode.NO_PERMISSION.getCode(), "无权限操作");
	}
	
	/**
	 * @Description:get product detailed info
	 * @param session
	 * @param productId
	 * @param status
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	@RequestMapping(value="detail.do", method=RequestMethod.POST)
	@ResponseBody
	public ServerResponse getDetail(HttpSession session, Integer productId){
		User user = (User) session.getAttribute(Const.CURRENTUSER);
		if (user == null){
			return ServerResponse.createResponse(ResponseCode.NEED_LOGIN.getCode(), "用户未登录");
		}
		if (this.iUserService.isAdmin(user)){
			// do query of product detail
			return this.iProductService.getDetail(productId);
		}
		return ServerResponse.createResponse(ResponseCode.NO_PERMISSION.getCode(), "无权限操作");
	}
	
	
	/**
	 * @Description:paging query product list
	 * @param session
	 * @param productId
	 * @param status
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	@RequestMapping(value="list.do", method=RequestMethod.POST)
	@ResponseBody
	public ServerResponse getProductList(HttpSession session, 
			@RequestParam(value = "pageNum", defaultValue = "1") int pageNum, 
			@RequestParam(value = "pageSize", defaultValue = "10") int pageSize){
		User user = (User) session.getAttribute(Const.CURRENTUSER);
		if (user == null){
			return ServerResponse.createResponse(ResponseCode.NEED_LOGIN.getCode(), "用户未登录");
		}
		if (this.iUserService.isAdmin(user)){
			return this.iProductService.getProductList(pageNum, pageSize);
		}
		return ServerResponse.createResponse(ResponseCode.NO_PERMISSION.getCode(), "无权限操作");
	}
	
	
	/**
	 * @Description:paging query product list
	 * @param session
	 * @param productId
	 * @param status
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	@RequestMapping(value="search_products.do", method=RequestMethod.POST)
	@ResponseBody
	public ServerResponse searchProducts(HttpSession session, 
			String productName, Integer productId, 
			@RequestParam(value = "pageNum", defaultValue = "1") int pageNum, 
			@RequestParam(value = "pageSize", defaultValue = "10") int pageSize){
		User user = (User) session.getAttribute(Const.CURRENTUSER);
		if (user == null){
			return ServerResponse.createResponse(ResponseCode.NEED_LOGIN.getCode(), "用户未登录");
		}
		if (this.iUserService.isAdmin(user)){
			return this.iProductService.searchProducts(productName, productId, pageNum, pageSize);
		}
		return ServerResponse.createResponse(ResponseCode.NO_PERMISSION.getCode(), "无权限操作");
	}
	
	
	
}
