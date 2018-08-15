package com.happyshopping.vo;

import java.math.BigDecimal;

/**
 * @author Flouis
 * @date 2018年8月15日
 * @Description This is an abstract object which combines product & cart
 */
public class CartProductVO {

	// 以下四个字段是直接从cart数据表中获取的
	private Integer	id;
	private Integer userId;
	private Integer productId;
	private Integer productChecked; // 此商品是否被勾选了
	
	
	private Integer quantity;		// 此商品在购物车中的数量,会做限制处理
	private Integer productStatus;	
	private Integer productStock;	// 此商品的库存
	
	private String productName;
	private String productSubtitle;
	private String productMainImage;
	
	private BigDecimal productPrice;
	private BigDecimal productTotalPrice;
	
	private String limitQuantity;	// 限制数量的一个返回结果
	
	public void setLimitQuantity(String limitQuantity){
		this.limitQuantity = limitQuantity;
	}
	
	public String getLimitQuantity(){
		return this.limitQuantity;
	}
	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public Integer getUserId() {
		return userId;
	}
	public void setUserId(Integer userId) {
		this.userId = userId;
	}
	public Integer getProductId() {
		return productId;
	}
	public void setProductId(Integer productId) {
		this.productId = productId;
	}
	public Integer getQuantity() {
		return quantity;
	}
	public void setQuantity(Integer quantity) {
		this.quantity = quantity;
	}
	public Integer getProductStatus() {
		return productStatus;
	}
	public void setProductStatus(Integer productStatus) {
		this.productStatus = productStatus;
	}
	public Integer getProductStock() {
		return productStock;
	}
	public void setProductStock(Integer productStock) {
		this.productStock = productStock;
	}
	public Integer getProductChecked() {
		return productChecked;
	}
	public void setProductChecked(Integer productChecked) {
		this.productChecked = productChecked;
	}
	public String getProductName() {
		return productName;
	}
	public void setProductName(String productName) {
		this.productName = productName;
	}
	public String getProductSubtitle() {
		return productSubtitle;
	}
	public void setProductSubtitle(String productSubtitle) {
		this.productSubtitle = productSubtitle;
	}
	public String getProductMainImage() {
		return productMainImage;
	}
	public void setProductMainImage(String productMainImage) {
		this.productMainImage = productMainImage;
	}
	public BigDecimal getProductPrice() {
		return productPrice;
	}
	public void setProductPrice(BigDecimal productPrice) {
		this.productPrice = productPrice;
	}
	public BigDecimal getProductTotalPrice() {
		return productTotalPrice;
	}
	public void setProductTotalPrice(BigDecimal productTotalPrice) {
		this.productTotalPrice = productTotalPrice;
	}
	
	public CartProductVO(){}
	
}
