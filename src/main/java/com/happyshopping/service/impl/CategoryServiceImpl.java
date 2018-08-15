package com.happyshopping.service.impl;

import java.util.List;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.happyshopping.common.ResponseCode;
import com.happyshopping.common.ServerResponse;
import com.happyshopping.dao.CategoryMapper;
import com.happyshopping.pojo.Category;
import com.happyshopping.service.ICategoryService;

@Service("iCategoryService")
public class CategoryServiceImpl implements ICategoryService {

	private Logger logger = LoggerFactory.getLogger(CategoryServiceImpl.class);
	
	@Autowired
	private CategoryMapper categoryMapper;
	
	/**
	 * add category
	 */
	public ServerResponse<String> addCategory(String categoryName, Integer parentId) {
		// check if parameters are valid
		if ( StringUtils.isBlank(categoryName) || parentId == null ){
			return ServerResponse.createResponse(ResponseCode.ILLEGAL_ARGUMENT.getCode(), "参数出错");
		}
		
		// build new insert category object
		Category category = new Category();
		category.setName(categoryName);
		category.setParentId(parentId);
		category.setStatus(true);
		
		// do addition(insert) operation
		int resCnt = this.categoryMapper.insert(category);
		if (resCnt>0){
			return ServerResponse.createBySuccess("添加分类成功");
		}
		return ServerResponse.createByFail("服务器出错，添加分类失败");
	}

	/**
	 * update category name
	 */
	public ServerResponse<String> setCategoryName(Integer categoryId, String categoryName) {
		if (categoryId==null || StringUtils.isBlank(categoryName)){
			return ServerResponse.createResponse(ResponseCode.ILLEGAL_ARGUMENT.getCode(), "参数出错");
		}
		Category category = new Category();
		category.setId(categoryId);
		category.setName(categoryName);
		int resCnt = this.categoryMapper.updateByPrimaryKeySelective(category);
		if (resCnt>0){
			return ServerResponse.createBySuccess("修改分类名称成功");
		}
		return ServerResponse.createByFail("服务器出错，修改分类名称失败");
	}

	/**
	 * get children parallel categories without recursion
	 */
	public ServerResponse<List<Category>> getChildrenCategories(Integer categoryId) {
		List<Category> categoryList = this.categoryMapper.selectCategoryChildrenByParentId(categoryId);
		if (CollectionUtils.isEmpty(categoryList)){
			logger.info("未找到子分类");
		}
		return ServerResponse.createBySuccess(categoryList);
	}

	@SuppressWarnings("rawtypes")
	public ServerResponse getIdOfCategoryAndDeepChildren(Integer categoryId) {
		Set<Category> categorySet = Sets.newHashSet();
		this.findCategoryAndDeepChildren(categorySet, categoryId);

		List<Integer> categoryIdList = Lists.newArrayList();
		for (Category categoryItem : categorySet){
			categoryIdList.add(categoryItem.getId());
		}
		
		return ServerResponse.createBySuccess(categoryIdList);
	}

	/**
	 * find category and its children categories using recursion
	 */
	private Set<Category> findCategoryAndDeepChildren(Set<Category> categorySet, Integer categoryId){
		// Firstly we get the parent category
		Category category = this.categoryMapper.selectByPrimaryKey(categoryId);
		if ( category != null ){
			// add current category into categorySet and finally we return this set
			categorySet.add(category);
		}
		// find children categories by recursion and recursion must have a condition to exit
		List<Category> categoryList = this.categoryMapper.selectCategoryChildrenByParentId(categoryId);
		// here if categoryList is empty then for_each block won't proceed
		// and whether this list is empty is the condition to exit recursion
		for (Category categoryItem : categoryList){
			findCategoryAndDeepChildren(categorySet, categoryItem.getId());
		}
		return categorySet;
	}
	
}
