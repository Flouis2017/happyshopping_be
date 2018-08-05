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
import com.happyshopping.pojo.User;
import com.happyshopping.service.ICategoryService;
import com.happyshopping.service.IUserService;

/**
 * @author Flouis
 * @date 2018年8月1日
 * @Description category management controller used in back stage
 */

@Controller
@RequestMapping("/manage/category/")
public class CategoryManagementController {

	@Autowired
	private IUserService iUserService;
	
	@Autowired
	private ICategoryService iCategoryService;
	
	@RequestMapping(value = "add_category.do", method = RequestMethod.POST)
	@ResponseBody
	public ServerResponse<String> addCategory(HttpSession session, String categoryName,
			@RequestParam(value = "parentId", defaultValue = "0") int parentId){
		
		// Firstly check if user logged in
		User user = (User) session.getAttribute(Const.CURRENTUSER);
		if (user == null){
			return ServerResponse.createResponse(ResponseCode.NEED_LOGIN.getCode(), "用户未登录");
		}
		
		// Check if current user has the administrator permission
		if (this.iUserService.checkAdminRole(user).getData()){
			// do addition of category
			return this.iCategoryService.addCategory(categoryName, parentId);
		} else {
			return ServerResponse.createResponse(ResponseCode.NO_PERMISSION.getCode(), "无权限操作");
		}
	}
	
	@RequestMapping(value = "set_category_name.do", method = RequestMethod.POST)
	@ResponseBody
	public ServerResponse<String> setCategoryName(HttpSession session, Integer categoryId, String categoryName){
		// check if current user is logged in
		User user = (User) session.getAttribute(Const.CURRENTUSER);
		if (user ==  null){
			return ServerResponse.createResponse(ResponseCode.NEED_LOGIN.getCode(), "用户未登录");
		}
		if (this.iUserService.isAdmin(user)){
			// do update of category name:
			return this.iCategoryService.setCategoryName(categoryId, categoryName);
		} else {
			return ServerResponse.createResponse(ResponseCode.NO_PERMISSION.getCode(), "无权限操作");
		}
	}
	
	@SuppressWarnings("rawtypes")
	@RequestMapping(value = "get_children_categories.do", method = RequestMethod.POST)
	@ResponseBody
	public ServerResponse getChildrenCategories(HttpSession session, 
			@RequestParam(value = "categoryId", defaultValue = "0") Integer categoryId){
		User user = (User) session.getAttribute(Const.CURRENTUSER);
		if (user ==  null){
			return ServerResponse.createResponse(ResponseCode.NEED_LOGIN.getCode(), "用户未登录");
		}
		if (this.iUserService.isAdmin(user)){
			// do selection of children categories without recursion:
			return this.iCategoryService.getChildrenCategories(categoryId);
		} else {
			return ServerResponse.createResponse(ResponseCode.NO_PERMISSION.getCode(), "无权限操作");
		}
	}
	
	@SuppressWarnings("rawtypes")
	@RequestMapping(value = "getIdOfCategoryAndDeepChildren.do", method = RequestMethod.POST)
	@ResponseBody
	public ServerResponse getIdOfCategoryAndDeepChildren(HttpSession session, 
			@RequestParam(value = "categoryId", defaultValue = "0") Integer categoryId){
		User user = (User) session.getAttribute(Const.CURRENTUSER);
		if (user ==  null){
			return ServerResponse.createResponse(ResponseCode.NEED_LOGIN.getCode(), "用户未登录");
		}
		if (this.iUserService.isAdmin(user)){
			// do selection of children categories using recursion,for example if the given 
			// parameter of categoryId is 0,the top parentId, then all records should be returned 
			return this.iCategoryService.getIdOfCategoryAndDeepChildren(categoryId);
		} else {
			return ServerResponse.createResponse(ResponseCode.NO_PERMISSION.getCode(), "无权限操作");
		}
	}
	
	
}
