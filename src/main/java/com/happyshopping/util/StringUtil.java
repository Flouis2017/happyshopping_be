package com.happyshopping.util;

import com.happyshopping.common.Const;

/**
 * @author Flouis
 * @date 2018年7月19日
 * @Description 
 */
public class StringUtil {
	
	/**
	 * @Description:check the value of type whether equals "username" or "email"
	 * @return boolean
	 */
	public static boolean checkTypeValid(String type){
		boolean flag = false;
		if (Const.USERNAME.equals(type) || 
				Const.EMAIL.equals(type) ){
			flag = true;
		}
		return flag;
	}

}
