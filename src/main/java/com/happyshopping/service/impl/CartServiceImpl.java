package com.happyshopping.service.impl;

import java.math.BigDecimal;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.common.collect.Lists;
import com.happyshopping.common.Const;
import com.happyshopping.common.ResponseCode;
import com.happyshopping.common.ServerResponse;
import com.happyshopping.dao.CartMapper;
import com.happyshopping.dao.ProductMapper;
import com.happyshopping.pojo.Cart;
import com.happyshopping.pojo.Product;
import com.happyshopping.service.ICartService;
import com.happyshopping.util.BigDecimalUtil;
import com.happyshopping.util.PropertiesUtil;
import com.happyshopping.vo.CartProductVO;
import com.happyshopping.vo.CartVO;

@Service("iCartService")
public class CartServiceImpl implements ICartService {

	@Autowired
	private CartMapper cartMapper;
	
	@Autowired
	private ProductMapper productMapper;
	
	/**
	 * add same type products to the cart 
	 */
	public ServerResponse<CartVO> add(Integer userId, Integer productId, Integer count) {
		if (productId == null || count == null){
			return ServerResponse.createResponse(ResponseCode.ILLEGAL_ARGUMENT.getCode(), "参数出错");
		}
		Cart cart = this.cartMapper.selectByUserIdAndProductId(userId, productId);
		if (cart == null){
			// current product isn't in cart yet needing insert a new record
			Cart tmp = new Cart();
			tmp.setUserId(userId);
			tmp.setProductId(productId);
			tmp.setQuantity(count);
			tmp.setChecked(Const.Cart.CHECKED);
			this.cartMapper.insert(tmp);
		} else {
			// current product has existed in cart we just need to update the quality
			count = cart.getQuantity() + count;
			cart.setQuantity(count);
			this.cartMapper.updateByPrimaryKeySelective(cart);
		}
		return this.getCartVO(userId);
	}
	
	/**
	 * get cart VO including a list of products
	 */
	public ServerResponse<CartVO> getCartVO(Integer userId){
		CartVO cartVo = this.getCartVOLimit(userId);
		return ServerResponse.createBySuccess(cartVo);
	}
	
	private CartVO getCartVOLimit(Integer userId){
		CartVO cartVo = new CartVO();
		List<Cart> cartList = this.cartMapper.selectCartByUserId(userId);
		List<CartProductVO> cartProductVoList = Lists.newArrayList();
		
		BigDecimal cartTotalPrice = new BigDecimal("0");
		
		if (CollectionUtils.isNotEmpty(cartList)){
			for (Cart cartItem : cartList){
				CartProductVO cartProductVo = new CartProductVO();
				cartProductVo.setId(cartItem.getId());
				cartProductVo.setUserId(userId);
				cartProductVo.setProductId(cartItem.getProductId());
				cartProductVo.setProductChecked(cartItem.getChecked());
				Product product = this.productMapper.selectByPrimaryKey(cartItem.getProductId());
				// check stock if stock <= current product quantity then limit the 
				// quantity by setting stock to quantity
				if (product != null){
					cartProductVo.setProductName(product.getName());
					cartProductVo.setProductSubtitle(product.getSubtitle());
					cartProductVo.setProductMainImage(product.getMainImage());
					cartProductVo.setProductStatus(product.getStatus());
					cartProductVo.setProductPrice(product.getPrice());
					cartProductVo.setProductStock(product.getStock());
					
					int quantityLimit = 0;
					// do limit of quantity 
					if (product.getStock() >= cartItem.getQuantity()){
						// stock satisfies demand
						quantityLimit = cartItem.getQuantity();
						cartProductVo.setLimitQuantity(Const.Cart.LIMIT_NUM_SUCCESS);
					} else {
						// stock cannot satisfy demand
						quantityLimit = product.getStock();
						cartProductVo.setLimitQuantity(Const.Cart.LIMIT_NUM_FAIL);
						
						// update the valid quantity in cart
						Cart cartForQuantity = new Cart();
						cartForQuantity.setId(cartItem.getId());
						cartForQuantity.setQuantity(quantityLimit);
						this.cartMapper.updateByPrimaryKeySelective(cartForQuantity);
					}
					cartProductVo.setQuantity(quantityLimit);
					// calculate current product total price:
					cartProductVo.setProductTotalPrice(BigDecimalUtil.mul(product.getPrice().doubleValue(), cartProductVo.getQuantity()));
				}
				if (cartItem.getChecked() == Const.Cart.CHECKED){
					cartTotalPrice = BigDecimalUtil.add(cartTotalPrice.doubleValue(), cartProductVo.getProductTotalPrice().doubleValue());
				}
				cartProductVoList.add(cartProductVo);
			}
		}
		cartVo.setCartProductVOList(cartProductVoList);
		cartVo.setCartTotalPrice(cartTotalPrice);
		cartVo.setAllChecked(this.isAllChecked(userId));
		cartVo.setImageHost(PropertiesUtil.getProperty("ftp.server.http.prefix", "http://image.flouis.com/"));
		return cartVo;
	}
	
	private boolean isAllChecked(Integer userId){
		if (userId == null){
			return false;
		}
		return this.cartMapper.selectUncheckedCountByUserId(userId) == 0;
	}

}
