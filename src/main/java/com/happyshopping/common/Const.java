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
	
	public static final String UPLOAD_DIR = "upload";
	
	public interface AlipayCallback{
        String TRADE_STATUS_WAIT_BUYER_PAY 	= "WAIT_BUYER_PAY";
        String TRADE_STATUS_TRADE_SUCCESS	= "TRADE_SUCCESS";

        String RESPONSE_SUCCESS	= "success";
        String RESPONSE_FAIL	= "failed";
    }
	
	public enum OrderStatusEnum{
		CANCELED(0,"已取消"),
        NO_PAY(10,"未支付"),
        PAID(20,"已付款"),
        SHIPPED(40,"已发货"),
        ORDER_SUCCESS(50,"订单完成"),
        ORDER_CLOSE(60,"订单关闭");
		
		OrderStatusEnum(int code, String value){
			this.code = code;
			this.value = value;
		}
		private String value;
		private int code;
		
		public String getValue(){
			return this.value;
		}
		public int getCode(){
			return this.code;
		}
		
		public static OrderStatusEnum codeOf(int code){
			for (OrderStatusEnum orderStatusEnum : values()){
				if (orderStatusEnum.getCode() == code){
					return orderStatusEnum;
				}
			}
			throw new RuntimeException("无对应的枚举值");
		}
	}
	
	public enum PayPlatformEnum{
		ALIPAY(1,"支付宝");
		
		PayPlatformEnum(int code, String value){
			this.code = code;
			this.value = value;
		}
		private String value;
		private int code;
		
		public String getValue(){
			return this.value;
		}
		public int getCode(){
			return this.code;
		}
	}
	
	public enum ProductStatusEnum{
		ON_SALE(1,"销售中"),
		OFF_SHELVE(0,"下架");
		
		ProductStatusEnum(int code, String value){
			this.code = code;
			this.value = value;
		}
		private String value;
		private int code;
		
		public String getValue(){
			return this.value;
		}
		public int getCode(){
			return this.code;
		}
	}
	
	public enum PostageEnum{
		YUAN(0,"元"),
		DOLLAR(1,"美元");
		
		PostageEnum(int code, String value){
			this.code = code;
			this.value = value;
		}
		private String value;
		private int code;
		
		public String getValue(){
			return this.value;
		}
		public int getCode(){
			return this.code;
		}
	}
	
	public enum PaymentTypeEnum{
		ONLINE(1,"在线支付");
		
		PaymentTypeEnum(int code, String value){
			this.code = code;
			this.value = value;
		}
		private String value;
		private int code;
		
		public String getValue(){
			return this.value;
		}
		public int getCode(){
			return this.code;
		}
		
		public static PaymentTypeEnum codeOf(int code){
            for(PaymentTypeEnum paymentTypeEnum : values()){
                if(paymentTypeEnum.getCode() == code){
                    return paymentTypeEnum;
                }
            }
            throw new RuntimeException("无对应的枚举");
        }
	}
	
}
