package com.happyshopping.common;

public class Const {

	public static final String CURRENTUSER 	= "currentUser";
	public static final String USERNAME 	= "username";
	public static final String EMAIL 		= "email";
	
	public interface Cart{
		int CHECKED 	= 1; 	// checked status in cart
		int UNCHECKED 	= 0; 	// unchecked status in cart
		
		String LIMIT_NUM_SUCCESS	= "LIMIT_NUM_SUCCESS";
		String LIMIT_NUM_FAIL		= "LIMIT_NUM_FAIL";
	}
	
	public interface Role{
		int ROLE_CUSTOMER	= 1;
		int ROLE_ADMIN		= 0;
	}
}
