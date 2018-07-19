package com.happyshopping.common;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

/**
 * @author Flouis
 * @date 2018年7月19日
 * @Description 高复用服务响应对象(High-Level reusable response object)
 */
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class ServerResponse<T> implements Serializable {

	private static final long serialVersionUID = 1L;

	private int status; // 状态码(status code)
	private String msg; // 提示信息(hint is a supplement to explain what the status code means)
	private T data;     // 真正返回的数据(The data needed to response)
	
	public int getStatus(){
		return this.status;
	}
	public String getMsg(){
		return this.msg;
	}
	public T getData(){
		return this.data;
	}
	
	@JsonIgnore
	public boolean isSuccess(){
		return this.status == ResponseCode.SUCCESS.getCode();
	}
	
	private ServerResponse(){}
	
	private ServerResponse(int status, String msg, T data){
		this.status = status;
		this.msg = msg;
		this.data = data;
	}
	
	//以下是高复用服务响应对象生成器，我们将通过生成器来生成需要返回的数据，而不是直接通过构造方法：
	//Below are the generators and we would generate the data to response by these generators
	//rather than using constructors directly.
	
	//最通用的生成器(The commonest generator)
	public static <T> ServerResponse<T> createResponse(int status, String msg, T data){
		return new ServerResponse<T>(status, msg, data);
	}
	
	//无提示信息的生成器(Generator without message)
	public static <T> ServerResponse<T> createResponse(int status, T data){
		return new ServerResponse<T>(status, null, data);
	}
	
	//无交互数据的生成器(Generator without data)
	public static <T> ServerResponse<T> createResponse(int status, String msg){
		return new ServerResponse<T>(status, msg, null);
	}
	
	//success生成器(Create response by success)
	public static <T> ServerResponse<T> createBySuccess(String msg, T data){
		return new ServerResponse<T>(ResponseCode.SUCCESS.getCode(), msg, data);
	}
	
	//无交互数据的success生成器(Create response by success without data)
	public static <T> ServerResponse<T> createBySuccess(String msg){
		return new ServerResponse<T>(ResponseCode.SUCCESS.getCode(), msg, null);
	}
	
	//fail生成器(Create response by fail)
	public static <T> ServerResponse<T> createByFail(String msg, T data){
		return new ServerResponse<T>(ResponseCode.FAIL.getCode(), msg, data);
	}
	
	//无交互数据的fail生成器(Create response by fail without data)
	public static <T> ServerResponse<T> createByFail(String msg){
		return new ServerResponse<T>(ResponseCode.FAIL.getCode(), msg, null);
	}
	
}
