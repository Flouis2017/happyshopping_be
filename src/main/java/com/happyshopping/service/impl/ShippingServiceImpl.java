package com.happyshopping.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.happyshopping.common.ResponseCode;
import com.happyshopping.common.ServerResponse;
import com.happyshopping.dao.ShippingMapper;
import com.happyshopping.pojo.Shipping;
import com.happyshopping.service.IShippingService;

@Service("iShippingService")
public class ShippingServiceImpl implements IShippingService {

	@Autowired
	private ShippingMapper shippingMapper;
	
	/**
	 * insert new shipping
	 */
	public ServerResponse<Integer> add(Integer userId, Shipping shipping) {
		shipping.setUserId(userId);
		int resCnt = this.shippingMapper.insert(shipping);
		System.out.println(resCnt + " / " + shipping.getId());
		if (resCnt > 0){
			return ServerResponse.createBySuccess("新增收货地址成功", shipping.getId());
		}
		return ServerResponse.createByFail("新增收货地址失败");
	}

	/**
	 * delete a shipping address by shipping_id
	 */
	public ServerResponse<String> delete(Integer userId, Integer shippingId) {
		if (shippingId == null){
			return ServerResponse.createResponse(ResponseCode.ILLEGAL_ARGUMENT.getCode(), "参数出错");
		}
		int resCnt = this.shippingMapper.deleteByUserIdAndShippingId(userId, shippingId);
		if (resCnt > 0){
			return ServerResponse.createBySuccess("删除成功");
		}
		return ServerResponse.createByFail("删除失败");
	}

	/**
	 * update
	 */
	public ServerResponse<String> update(Integer userId, Shipping shipping) {
		shipping.setUserId(userId);
		int resCnt = this.shippingMapper.update(shipping);
		if (resCnt > 0){
			return ServerResponse.createBySuccess("更新成功");
		}
		return ServerResponse.createByFail("更新失败");
	}

	/**
	 * select shipping info by user_id & shippingId
	 */
	public ServerResponse<Shipping> select(Integer userId, Integer shippingId) {
		Shipping shipping = this.shippingMapper.selectByUserIdAndShippingId(userId, shippingId);
		if (shipping == null){
			return ServerResponse.createByFail("该收货地址不存在");
		}
		return ServerResponse.createBySuccess(shipping);
	}

	/**
	 * get shipping list with paging
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public ServerResponse<PageInfo> list(Integer userId, int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);
		List<Shipping> shippingList = this.shippingMapper.selectByUserId(userId);
		PageInfo pageInfo = new PageInfo(shippingList);
		return ServerResponse.createBySuccess(pageInfo);
	}

}
