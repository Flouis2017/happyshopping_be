package com.happyshopping.controller.portal;

import java.util.Iterator;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alipay.api.AlipayApiException;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.demo.trade.config.Configs;
import com.google.common.collect.Maps;
import com.happyshopping.common.Const;
import com.happyshopping.common.ResponseCode;
import com.happyshopping.common.ServerResponse;
import com.happyshopping.pojo.User;
import com.happyshopping.service.IOrderService;

@Controller
@RequestMapping("/order/")
public class OrderController {

	private static final Logger logger = LoggerFactory.getLogger(OrderController.class);
	
	@Autowired
	private IOrderService iOrderService;
	
	@SuppressWarnings("rawtypes")
	@RequestMapping(value = "pay.do", method = RequestMethod.POST)
	@ResponseBody
	public ServerResponse pay(HttpSession session, Long orderNo, HttpServletRequest request){
		User user = (User) session.getAttribute(Const.CURRENTUSER);
		if (user == null){
			return ServerResponse.createResponse(ResponseCode.NEED_LOGIN.getCode(), ResponseCode.NEED_LOGIN.getDescription());
		}
		String path = request.getSession().getServletContext().getRealPath(Const.UPLOAD_DIR);
		return this.iOrderService.pay(user.getId(), orderNo, path);
	}
	
	/**
	 * @Description:这里参照支付宝回调函数的要求返回一个Object对象，参数也只能有一个HttpServletRequest
	 * 因为经过支付宝回调函数之后所有需要的参数都会被存放在request中
	 * @param request
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	@RequestMapping(value = "alipay_callback.do", method = RequestMethod.POST)
	@ResponseBody
	public Object alipayCallback(HttpServletRequest request){
		Map<String, String> params = Maps.newHashMap();
		
		Map parameterMap = request.getParameterMap();
		
		StringBuilder sb = new StringBuilder();
		//使用迭代器来遍历参数map
		Iterator iterator = parameterMap.entrySet().iterator();
		while (iterator.hasNext()){
			String key = (String) iterator.next();
			String[] values = (String[]) parameterMap.get(key);
			// 拼接参数的字符串：
			sb.setLength(0);
			for (int i = 0; i < values.length; i++){
				sb = ( i == values.length - 1 ) ? sb.append(values[i]) : sb.append(values[i]).append(",");
			}
			params.put(key, sb.toString());
		}
		logger.info("支付宝回调, sign:{},trade_status:{},params:{}", params.get("sign"),
				params.get("trade_status"), params.toString());
		
		// Very Important! 验证回调的正确性，是不是支付宝发起的
		params.remove("sign_type");
		try {
			boolean alipayRSACheckV2 = AlipaySignature.rsaCheckV2(params, 
					Configs.getAlipayPublicKey(), "utf-8", Configs.getSignType());
			if (!alipayRSACheckV2){
				return ServerResponse.createByFail("非法请求，验证失败!");
			}
		} catch (AlipayApiException e) {
			logger.error("支付宝验证回调异常", e);
		}
		
		// 验证各种数据，遇到错误，直接忽略此次回调
		ServerResponse serverResponse = this.iOrderService.alipayCallback(params);
		if (serverResponse.isSuccess()){
			return Const.AlipayCallback.RESPONSE_SUCCESS;
		}
		return Const.AlipayCallback.RESPONSE_FAIL;
	}
	
	@RequestMapping(value = "order_is_paid.do", method = RequestMethod.POST)
	@ResponseBody
	public ServerResponse<Boolean> orderIsPaid(HttpSession session, Long orderNo){
		User user = (User) session.getAttribute(Const.CURRENTUSER);
		if (user == null){
			return ServerResponse.createResponse(ResponseCode.NEED_LOGIN.getCode(), ResponseCode.NEED_LOGIN.getDescription());
		}
		return this.iOrderService.orderIsPaid(user.getId(), orderNo);
	}
	
}
