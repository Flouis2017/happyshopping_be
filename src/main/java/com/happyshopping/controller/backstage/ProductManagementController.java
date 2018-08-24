package com.happyshopping.controller.backstage;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.google.common.collect.Maps;
import com.happyshopping.common.Const;
import com.happyshopping.common.ResponseCode;
import com.happyshopping.common.ServerResponse;
import com.happyshopping.pojo.Product;
import com.happyshopping.pojo.User;
import com.happyshopping.service.IFileService;
import com.happyshopping.service.IProductService;
import com.happyshopping.service.IUserService;
import com.happyshopping.util.PropertiesUtil;

@Controller
@RequestMapping("/manage/product/")
public class ProductManagementController {
	
	@Autowired
	private IUserService iUserService;
	
	@Autowired
	private IProductService iProductService;
	
	@Autowired
	private IFileService iFileService;

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
	 * @Description: search products by productName and productId then page the result
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
	
	/**
	 * @Description: use MultipartFile of SpringMVC to upload file to FTP server
	 * @param session
	 * @param file
	 * @param request
	 * @return
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@RequestMapping(value="upload.do", method=RequestMethod.POST)
	@ResponseBody
	public ServerResponse upload(HttpSession session, 
			@RequestParam(value="file", required=false)MultipartFile file, HttpServletRequest request){
		
		User user = (User) session.getAttribute(Const.CURRENTUSER);
		if (user == null){
			return ServerResponse.createResponse(ResponseCode.NEED_LOGIN.getCode(), "用户未登录");
		}
		if (this.iUserService.isAdmin(user)){
			// Firstly, we get the dir path in Tomcat server, file will temporarily store there
			// D:/Tomcat/eclipse-mars-tomcat-8.0.52/wtpwebapps/happyshopping/upload
			String path = request.getSession().getServletContext().getRealPath(Const.UPLOAD_DIR);
			// upload file
			String targetFileName = this.iFileService.upload(file, path);
			// combine url string
			String url = PropertiesUtil.getProperty("ftp.server.http.prefix","http://image.flouis.com/")+targetFileName;
			
			// return the result map
			Map map = Maps.newHashMap();
			map.put("uri", targetFileName);
			map.put("url", url);
			return ServerResponse.createBySuccess(map);
		}
		return ServerResponse.createResponse(ResponseCode.NO_PERMISSION.getCode(), "无权限操作");
	}
	
	/**
	 * @Description: similar to above upload function but we should return specific data
	 * according to requirement of simditor, which is a useful rich text plug.
	 * Simditor's JSON response after uploading completely as below:
	 * {
	 * 		"success"	: true / false,
	 * 		"msg"		: "error message", # optional
	 * 		"file_path"	: "[real file path]"
	 * }
	 * @param session
	 * @param file
	 * @param request
	 * @return
	 */
	@RequestMapping(value="richtext_image_upload.do", method=RequestMethod.POST)
	@ResponseBody
	public Map<String, Object> richtextImageUpload(HttpSession session, 
			@RequestParam(value="file", required=false)MultipartFile file, 
			HttpServletRequest request, HttpServletResponse response){
		
		Map<String, Object> resultMap = Maps.newHashMap();
		User user = (User) session.getAttribute(Const.CURRENTUSER);
		if (user == null){
			resultMap.put("success", false);
			resultMap.put("msg", "用户未登录");
			return resultMap;
		}
		if (this.iUserService.isAdmin(user)){
			String path = request.getSession().getServletContext().getRealPath(Const.UPLOAD_DIR);
			String targetFileName = this.iFileService.upload(file, path);
			if (StringUtils.isBlank(targetFileName)){
				resultMap.put("success", false);
				resultMap.put("msg", "上传失败");
				return resultMap;
			}
			String url = PropertiesUtil.getProperty("ftp.server.http.prefix","http://image.flouis.com/")+targetFileName;
			resultMap.put("success", true);
			resultMap.put("msg", "上传成功");
			resultMap.put("file_path",url);
			
			// set response header because obey the appointment if we use Simditor
			response.addHeader("Access-Control-Allow-Headers", "X-File-Name");
			return resultMap;
		} else {
			resultMap.put("success", false);
			resultMap.put("msg", "无权限操作");
			return resultMap;
		}
	}
	
}
