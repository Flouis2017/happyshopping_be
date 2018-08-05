package com.happyshopping.service;

import java.util.List;

import com.happyshopping.common.ServerResponse;
import com.happyshopping.pojo.Category;

/**
 * @author Flouis
 * @date 2018年8月2日
 * @Description category interface used in back stage
 */
public interface ICategoryService {

	ServerResponse<String> addCategory(String categoryName, Integer parentId);

	ServerResponse<String> setCategoryName(Integer categoryId, String categoryName);
	
	ServerResponse<List<Category>> getChildrenCategories(Integer categoryId);

	@SuppressWarnings("rawtypes")
	ServerResponse getIdOfCategoryAndDeepChildren(Integer categoryId);
}
