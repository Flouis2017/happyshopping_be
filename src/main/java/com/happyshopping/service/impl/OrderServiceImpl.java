package com.happyshopping.service.impl;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
import com.happyshopping.dao.OrderItemMapper;
import com.happyshopping.dao.OrderMapper;
import com.happyshopping.dao.PayInfoMapper;
import com.happyshopping.pojo.Order;
import com.happyshopping.pojo.OrderItem;
import com.happyshopping.pojo.PayInfo;
import com.happyshopping.service.IOrderService;
import com.happyshopping.util.BigDecimalUtil;
import com.happyshopping.util.DateTimeUtil;
import com.happyshopping.util.FTPUtil;
import com.happyshopping.util.PropertiesUtil;

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
	public ServerResponse<Boolean> orderIsPaid(Integer userId, Long orderNo) {
		Order order = this.orderMapper.selectByUserIdAndOrderNo(userId, orderNo);
		if (order == null){
			return ServerResponse.createByFail("当前用户没有该订单");
		}
		if (order.getStatus() >= Const.OrderStatusEnum.PAID.getCode()){
			return ServerResponse.createResponse(ResponseCode.COMMON.getCode(), true);
		}
		return ServerResponse.createResponse(ResponseCode.COMMON.getCode(), false);
	}

}
