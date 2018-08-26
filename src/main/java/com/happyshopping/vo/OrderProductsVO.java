package com.happyshopping.vo;

import java.math.BigDecimal;
import java.util.List;

public class OrderProductsVO {

	private List<OrderItemVO> orderItemVoList;
    private BigDecimal productTotalPrice;
    private String imageHost;
    
	public List<OrderItemVO> getOrderItemVoList() {
		return orderItemVoList;
	}
	public void setOrderItemVoList(List<OrderItemVO> orderItemVoList) {
		this.orderItemVoList = orderItemVoList;
	}
	public BigDecimal getProductTotalPrice() {
		return productTotalPrice;
	}
	public void setProductTotalPrice(BigDecimal productTotalPrice) {
		this.productTotalPrice = productTotalPrice;
	}
	public String getImageHost() {
		return imageHost;
	}
	public void setImageHost(String imageHost) {
		this.imageHost = imageHost;
	}
    
}
