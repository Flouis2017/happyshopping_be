package com.happyshopping.service.impl;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alipay.api.AlipayResponse;
import com.alipay.api.response.AlipayTradePrecreateResponse;
import com.alipay.demo.trade.config.Configs;
import com.alipay.demo.trade.model.ExtendParams;
import com.alipay.demo.trade.model.GoodsDetail;
import com.alipay.demo.trade.model.builder.AlipayTradePrecreateRequestBuilder;
import com.alipay.demo.trade.model.result.AlipayF2FPrecreateResult;
import com.alipay.demo.trade.service.AlipayTradeService;
import com.alipay.demo.trade.service.impl.AlipayTradeServiceImpl;
import com.alipay.demo.trade.utils.ZxingUtils;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.happyshopping.common.Const;
import com.happyshopping.common.ResponseCode;
import com.happyshopping.common.ServerResponse;
import com.happyshopping.dao.CartMapper;
import com.happyshopping.dao.OrderItemMapper;
import com.happyshopping.dao.OrderMapper;
import com.happyshopping.dao.PayInfoMapper;
import com.happyshopping.dao.ProductMapper;
import com.happyshopping.dao.ShippingMapper;
import com.happyshopping.pojo.Cart;
import com.happyshopping.pojo.Order;
import com.happyshopping.pojo.OrderItem;
import com.happyshopping.pojo.PayInfo;
import com.happyshopping.pojo.Product;
import com.happyshopping.pojo.Shipping;
import com.happyshopping.service.IOrderService;
import com.happyshopping.util.BigDecimalUtil;
import com.happyshopping.util.DateTimeUtil;
import com.happyshopping.util.FTPUtil;
import com.happyshopping.util.PropertiesUtil;
import com.happyshopping.vo.OrderItemVO;
import com.happyshopping.vo.OrderVO;
import com.happyshopping.vo.ShippingVO;

@SuppressWarnings("rawtypes")
@Service("iOrderService")
public class OrderServiceImpl implements IOrderService {

	private static final Logger logger = LoggerFactory.getLogger(OrderServiceImpl.class);
	
	private static AlipayTradeService tradeService;
	static {
		/**
		 * 一定要在AlipayTradeService之前调用Configs.init()设置默认参数，
		 * Configs会读取classpath下的zfbinfo.properties文件配置信息，如果找不到该文件则确认该文件是否在classpath目录
		 */
		Configs.init("zfbinfo.properties");
		tradeService = new AlipayTradeServiceImpl.ClientBuilder().build();
	}
    
	@Autowired
	private OrderMapper orderMapper;
	
	@Autowired
	private OrderItemMapper orderItemMapper;
	
	@Autowired
	private PayInfoMapper payInfoMapper;
	
	@Autowired
	private CartMapper cartMapper;
	
	@Autowired
	private ProductMapper productMapper;
	
	@Autowired
	private ShippingMapper shippingMapper;
	
	
	/**
	 * pay by Alipay
	 */
	public ServerResponse pay(Integer userId, Long orderNo, String path) {
		Map<String, String> resultMap = Maps.newHashMap();
		// 首先根据用户id和订单号获取订单对象
		Order order = this.orderMapper.selectByUserIdAndOrderNo(userId, orderNo);
		if (order == null){
			return ServerResponse.createByFail("当前用户没有该订单");
		}
		resultMap.put("orderNo", order.getOrderNo().toString());
		
		StringBuilder sb = new StringBuilder();
		sb.setLength(0);
		//*************************支付宝源码修改*************************
		// (必填) 商户网站订单系统中唯一订单号，64个字符以内，只能包含字母、数字、下划线，
        // 需保证商户系统端不能重复，建议通过数据库sequence生成，
        String outTradeNo = order.getOrderNo().toString();

        // (必填) 订单标题，粗略描述用户的支付目的。如“xxx品牌xxx门店当面付扫码消费”
        String subject = sb.append("HappyShopping扫码支付，订单号：").append(outTradeNo).toString();

        // (必填) 订单总金额，单位为元，不能超过1亿元
        // 如果同时传入了【打折金额】,【不可打折金额】,【订单总金额】三者,则必须满足如下条件:【订单总金额】=【打折金额】+【不可打折金额】
        String totalAmount = order.getPayment().toString();

        // (可选) 订单不可打折金额，可以配合商家平台配置折扣活动，如果酒水不参与打折，则将对应金额填写至此字段
        // 如果该值未传入,但传入了【订单总金额】,【打折金额】,则该值默认为【订单总金额】-【打折金额】
        String undiscountableAmount = "0";

        // 卖家支付宝账号ID，用于支持一个签约账号下支持打款到不同的收款账号，(打款到sellerId对应的支付宝账号)
        // 如果该字段为空，则默认为与支付宝签约的商户的PID，也就是appid对应的PID
        String sellerId = "";

        // 订单描述，可以对交易或商品进行一个详细地描述，比如填写"购买商品2件共15.00元"
        sb.setLength(0);
        String body = sb.append("订单").append(outTradeNo).append(" 消费总计").append(totalAmount).append("元").toString();

        // 商户操作员(收银员)编号，添加此参数可以为商户操作员做销售统计
        String operatorId = "test_operator_id";

        // (必填) 商户门店编号，通过门店号和商家后台可以配置精准到门店的折扣信息，详询支付宝技术支持
        String storeId = "test_store_id";

        // 业务扩展参数，目前可添加由支付宝分配的系统商编号(通过setSysServiceProviderId方法)，详情请咨询支付宝技术支持
        ExtendParams extendParams = new ExtendParams();
        extendParams.setSysServiceProviderId("2088100200300400500");

        // 支付超时，定义为120分钟
        String timeoutExpress = "120m";

        // 商品明细列表，需填写购买商品详细信息，
        List<GoodsDetail> goodsDetailList = new ArrayList<GoodsDetail>();
        List<OrderItem> orderItemList = this.orderItemMapper.selectByUserIdAndOrderNo(userId, orderNo);
        // 将orderItemList和goodsDetailList进行适配
        for (OrderItem orderItem : orderItemList){
        	GoodsDetail goods = GoodsDetail.newInstance(orderItem.getProductId().toString(), orderItem.getProductName(), 
        			BigDecimalUtil.mul(orderItem.getCurrentUnitPrice().doubleValue(), new Double(100).doubleValue()).longValue(), 
        			orderItem.getQuantity());
        	goodsDetailList.add(goods);
        }

        // 创建扫码支付请求builder，设置请求参数
        AlipayTradePrecreateRequestBuilder builder = new AlipayTradePrecreateRequestBuilder()
            .setSubject(subject).setTotalAmount(totalAmount).setOutTradeNo(outTradeNo)
            .setUndiscountableAmount(undiscountableAmount).setSellerId(sellerId).setBody(body)
            .setOperatorId(operatorId).setStoreId(storeId).setExtendParams(extendParams)
            .setTimeoutExpress(timeoutExpress)
            .setNotifyUrl(PropertiesUtil.getProperty("alipay.callback.url"))//支付宝服务器主动通知商户服务器里指定的页面http路径,根据需要设置
            .setGoodsDetailList(goodsDetailList);

        AlipayF2FPrecreateResult result = tradeService.tradePrecreate(builder);
        switch (result.getTradeStatus()) {
            case SUCCESS:
            	logger.info("支付宝预下单成功: )");

                AlipayTradePrecreateResponse response = result.getResponse();
                dumpResponse(response);

                // 生成二维码并上传至ftp服务器，再将url返回前端进行展示：
                // 先去Tomcat服务器创建一个文件夹用于临时存入生成的二维码图片
                File folder = new File(path);
                if (!folder.exists()){
                	folder.setWritable(true);
                	folder.mkdirs();
                }
                
                String qrFileName = String.format("qr-%s.png", response.getOutTradeNo());
                // 特别注意：qrPath二维码url设值的时候，最后文件名之前一定要加“/”
                sb.setLength(0);
                String qrPath = sb.append(path).append("/").append(qrFileName).toString();
                ZxingUtils.getQRCodeImge(response.getQrCode(), 256, qrPath);
                File targetFile = new File(path, qrFileName);
				try {
					FTPUtil.uploadToFTPServer(Lists.newArrayList(targetFile));
				} catch (IOException e) {
					logger.error("上传二维码异常",e);
				}
                logger.info("qrPath:" + qrPath);
                String qrUrl = PropertiesUtil.getProperty("ftp.server.http.prefix") + targetFile.getName();
                resultMap.put("qrUrl", qrUrl);
                return ServerResponse.createBySuccess(resultMap);
            case FAILED:
            	logger.error("支付宝预下单失败!!!");
            	return ServerResponse.createByFail("支付宝预下单失败!!!");
            case UNKNOWN:
            	logger.error("系统异常，预下单状态未知!!!");
            	return ServerResponse.createByFail("系统异常，预下单状态未知!!!");
            default:
            	logger.error("不支持的交易状态，交易返回异常!!!");
            	return ServerResponse.createByFail("不支持的交易状态，交易返回异常!!!");
        }
	}
	
	// 简单打印应答
    private void dumpResponse(AlipayResponse response) {
        if (response != null) {
            logger.info(String.format("code:%s, msg:%s", response.getCode(), response.getMsg()));
            if (StringUtils.isNotEmpty(response.getSubCode())) {
            	logger.info(String.format("subCode:%s, subMsg:%s", response.getSubCode(),
                    response.getSubMsg()));
            }
            logger.info("body:" + response.getBody());
        }
    }

    /**
     * Alipay callback
     */
	public ServerResponse alipayCallback(Map<String, String> params) {
		Long orderNo = Long.parseLong(params.get("out_trade_no"));
		String tradeNo = params.get("trade_no");
		String tradeStatus = params.get("trade_status");
		Order order = this.orderMapper.selectByOrderNo(orderNo);
		if (order == null){
			return ServerResponse.createByFail("非HappyShopping的订单，回调忽略");
		}
		if (order.getStatus() >= Const.OrderStatusEnum.PAID.getCode()){
			return ServerResponse.createBySuccess("支付宝重复调用");
		}
		if (Const.AlipayCallback.TRADE_STATUS_TRADE_SUCCESS.equals(tradeStatus)){
			// 支付成功后，更新支付时间和支付状态
			order.setPaymentTime( DateTimeUtil.strToDate(params.get("gmt_payment")) );
			order.setStatus(Const.OrderStatusEnum.PAID.getCode());
			this.orderMapper.updateByPrimaryKeySelective(order);
		}
		PayInfo payInfo = new PayInfo();
		payInfo.setUserId(order.getUserId());
		payInfo.setOrderNo(order.getOrderNo());
		payInfo.setPayPlatform(Const.PayPlatformEnum.ALIPAY.getCode());
		payInfo.setPlatformNumber(tradeNo);
		payInfo.setStatus(tradeStatus);
		
		this.payInfoMapper.insert(payInfo);
		
		return ServerResponse.createBySuccess("支付宝回调成功");
	}

	/**
	 * query order is paid or not by order_no & user_id
	 */
	public ServerResponse<Boolean> queryOrderIsPaid(Integer userId, Long orderNo) {
		Order order = this.orderMapper.selectByUserIdAndOrderNo(userId, orderNo);
		if (order == null){
			return ServerResponse.createByFail("当前用户没有该订单");
		}
		if (order.getStatus() >= Const.OrderStatusEnum.PAID.getCode()){
			return ServerResponse.createResponse(ResponseCode.COMMON.getCode(), true);
		}
		return ServerResponse.createResponse(ResponseCode.COMMON.getCode(), false);
	}

	/**
	 * create order
	 */
	@SuppressWarnings("unchecked")
	public ServerResponse create(Integer userId, Integer shippingId) {
		// get data from cart
		List<Cart> cartList = this.cartMapper.selectCheckedByUserId(userId);
		
		// calculate total price of current order, needing get order item list at first
		ServerResponse serverResponse = this.getOrderItems(userId, cartList);
		if (!serverResponse.isSuccess()){
			return serverResponse;
		}
		List<OrderItem> orderItemList = (List<OrderItem>) serverResponse.getData();
		if (CollectionUtils.isEmpty(orderItemList)){
			return ServerResponse.createByFail("购物车为空");
		}
		BigDecimal paymentAmount = this.getPaymentAmount(orderItemList);
		
		// generate order
		Order order = this.initOrder(userId, shippingId, paymentAmount);
		if (order == null){
			return ServerResponse.createByFail("生成订单出错");
		}
		// set orderNo in orderItemList
		for (OrderItem orderItem : orderItemList){
			orderItem.setOrderNo(order.getOrderNo());
		}
		// batch insert by MyBatis
		this.orderItemMapper.batchInsert(orderItemList);
		
		// update stock after placing the order,in other words order and orderItem
		// records had been inserted into database tables.
		this.updateProductStock(orderItemList);
		
		// clear checked cart record
		this.clearCartChecked(cartList);
		// return OrderVO object
		OrderVO orderVo = this.assembleOrderVo(order, orderItemList);
		return ServerResponse.createBySuccess(orderVo);
	}
	
	/**
	 * @Description:assemble order VO
	 * @param order
	 * @param orderItemList
	 * @return
	 */
	private OrderVO assembleOrderVo(Order order, List<OrderItem> orderItemList){
		OrderVO orderVo = new OrderVO();
		orderVo.setOrderNo(order.getOrderNo());
		orderVo.setPayment(order.getPayment());
		orderVo.setPaymentType(order.getPaymentType());
		orderVo.setPaymentTypeDesc(Const.PaymentTypeEnum.codeOf(order.getPaymentType()).getValue());
		
		orderVo.setPostage(order.getPostage());
		orderVo.setStatus(order.getStatus());
		orderVo.setStatusDesc(Const.OrderStatusEnum.codeOf(order.getStatus()).getValue());
		
		orderVo.setShippingId(order.getShippingId());
		Shipping shipping = this.shippingMapper.selectByPrimaryKey(order.getShippingId());
		if (shipping != null){
			orderVo.setReceiverName(shipping.getReceiverName());
			orderVo.setShippingVo(this.assembleShippingVo(shipping));
		}
		
		orderVo.setPaymentTime(DateTimeUtil.dateToStr(order.getPaymentTime()));
		orderVo.setSendTime(DateTimeUtil.dateToStr(order.getSendTime()));
        orderVo.setEndTime(DateTimeUtil.dateToStr(order.getEndTime()));
        orderVo.setCreateTime(DateTimeUtil.dateToStr(order.getCreateTime()));
        orderVo.setCloseTime(DateTimeUtil.dateToStr(order.getCloseTime()));
        
        orderVo.setImageHost(PropertiesUtil.getProperty("ftp.server.http.prefix"));
        
        List<OrderItemVO> orderItemVoList = Lists.newArrayList();
        for (OrderItem orderItem : orderItemList){
        	// assemble orderItemVo
        	OrderItemVO orderItemVo = this.assembleOrderItemVo(orderItem);
        	orderItemVoList.add(orderItemVo);
        }
        orderVo.setOrderItemVoList(orderItemVoList);
        
		return orderVo;
	}
	
	private void updateProductStock(List<OrderItem> orderItemList){
		for (OrderItem orderItem : orderItemList){
			Product product = this.productMapper.selectByPrimaryKey(orderItem.getProductId());
			int stock = product.getStock() - orderItem.getQuantity();
			this.productMapper.updateStock(product.getId(), stock);
		}
	}
	
	private void clearCartChecked(List<Cart> cartList){
		for (Cart cart : cartList){
			this.cartMapper.deleteByPrimaryKey(cart.getId());
		}
	}
	
	/**
	 * get order item list
	 */
	public ServerResponse getOrderItems(Integer userId, List<Cart> cartList){
		List<OrderItem> orderItemList = Lists.newArrayList();
		if (CollectionUtils.isEmpty(cartList)){
			return ServerResponse.createByFail("购物车为空");
		}
		
		// check cart data including goods' status and quantity
		for (Cart cartItem : cartList){
			OrderItem orderItem = new OrderItem();
			Product product = this.productMapper.selectByPrimaryKey(cartItem.getProductId());
			// check goods status on sale or off shelve
			if (Const.ProductStatusEnum.ON_SALE.getCode() != product.getStatus()){
				return ServerResponse.createByFail(product.getName() + "不是在线销售状态");
			}
			// check if goods stock is enough
			if (product.getStock() < cartItem.getQuantity()){
				return ServerResponse.createByFail(product.getName() + "库存不足");
			}
			
			// assemble orderItem
			orderItem.setUserId(userId);
			orderItem.setProductId(product.getId());
			orderItem.setProductName(product.getName());
			orderItem.setProductImage(product.getMainImage());
			orderItem.setCurrentUnitPrice(product.getPrice());
			orderItem.setQuantity(cartItem.getQuantity());
			orderItem.setTotalPrice(BigDecimalUtil.mul(orderItem.getQuantity().doubleValue(), 
					orderItem.getCurrentUnitPrice().doubleValue()));
			orderItemList.add(orderItem);
		}
		return ServerResponse.createBySuccess(orderItemList);
	}
	
	/**
	 * calculate total payment amount in current order
	 */
	private BigDecimal getPaymentAmount(List<OrderItem> orderItemList){
		BigDecimal pa = new BigDecimal("0");
		for (OrderItem tmp : orderItemList){
			pa = BigDecimalUtil.add(pa.doubleValue(), tmp.getTotalPrice().doubleValue());
		}
		return pa;
	}
	
	/**
	 * @Description:initialize order pojo
	 * @return
	 */
	private Order initOrder(Integer userId, Integer shippingId, BigDecimal paymentAmount){
		Order order = new Order();
		long orderNo = this.generateOrderNo();
		order.setOrderNo(orderNo);
		order.setStatus(Const.OrderStatusEnum.NO_PAY.getCode());
		order.setPostage(Const.PostageEnum.YUAN.getCode());
		order.setPaymentType(Const.PaymentTypeEnum.ONLINE.getCode());
		order.setPayment(paymentAmount);
		order.setUserId(userId);
		order.setShippingId(shippingId);
		
		// insert the initial order record into table hs_order
		int resCnt = this.orderMapper.insert(order);
		if (resCnt > 0){
			return order;
		}
		return null;
	}

	private long generateOrderNo(){
		long currentTime = System.currentTimeMillis();
		return currentTime + new Random().nextInt(100);
	}
	
	private ShippingVO assembleShippingVo(Shipping shipping){
		ShippingVO shippingVo = new ShippingVO();
		shippingVo.setReceiverName(shipping.getReceiverName());
		shippingVo.setReceiverAddress(shipping.getReceiverAddress());
		shippingVo.setReceiverProvince(shipping.getReceiverProvince());
		shippingVo.setReceiverCity(shipping.getReceiverCity());
		shippingVo.setReceiverDistrict(shipping.getReceiverDistrict());
		shippingVo.setReceiverZip(shipping.getReceiverZip());
		shippingVo.setReceiverMobile(shipping.getReceiverMobile());
		shippingVo.setReceiverPhone(shipping.getReceiverPhone());
		return shippingVo;
	}
	
	private OrderItemVO assembleOrderItemVo(OrderItem orderItem){
		OrderItemVO orderItemVo = new OrderItemVO();
		orderItemVo.setOrderNo(orderItem.getOrderNo());
		orderItemVo.setProductId(orderItem.getProductId());
		orderItemVo.setProductName(orderItem.getProductName());
		orderItemVo.setProductImage(orderItem.getProductImage());
		orderItemVo.setCurrentUnitPrice(orderItem.getCurrentUnitPrice());
		orderItemVo.setQuantity(orderItem.getQuantity());
		orderItemVo.setTotalPrice(orderItem.getTotalPrice());
		orderItemVo.setCreateTime(DateTimeUtil.dateToStr(orderItem.getCreateTime()));
		return orderItemVo;
	}
	
}
