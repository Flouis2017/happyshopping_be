package com.happyshopping.common;

/**
 * @author Flouis
 * @date 2018年7月19日
 * @Description 返回码(response code)
 */
public enum ResponseCode {
	
	UNKNOWN(0,"unknown status"),
	SUCCESS(100,"success"),
	FAIL(101,"fail"),
	NEED_LOGIN(10,"need_login"),
	ILLEGAL_ARGUMENT(2,"illegal_argument"),
	COMMON(50,"common");
	
	private final int code;
	private final String description;
	
	ResponseCode(int code, String des){
		this.code = code;
		this.description = des;
	}
	
	public int getCode(){
		return this.code;
	}
	public String getDescription(){
		return this.description;
	}
	
}
