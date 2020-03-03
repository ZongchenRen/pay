package cn.gogosoft.pay.service.impl;

import java.math.BigDecimal;

import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;
import com.lly835.bestpay.enums.BestPayPlatformEnum;
import com.lly835.bestpay.enums.BestPayTypeEnum;
import com.lly835.bestpay.enums.OrderStatusEnum;
import com.lly835.bestpay.model.PayRequest;
import com.lly835.bestpay.model.PayResponse;
import com.lly835.bestpay.service.BestPayService;

import cn.gogosoft.pay.dao.PayInfoMapper;
import cn.gogosoft.pay.enums.PayPlatformEnum;
import cn.gogosoft.pay.pojo.PayInfo;
import cn.gogosoft.pay.service.IPayService;
import lombok.extern.slf4j.Slf4j;

/**
 * @author renzongchen
 * @data 2020-02-22 18:29
 * @description
 */
@Slf4j
@Service
public class PayServiceImpl implements IPayService {
	private final static String QUEUE_PAY_NOTIFY = "payNotify";

	@Autowired
	private BestPayService bestPayService;

	@Autowired
	private PayInfoMapper payInfoMapper;

	@Autowired
	private AmqpTemplate amqpTemplate;

	/**
	 * 创建/发起支付
	 *
	 * @param orderId
	 * @param amount
	 */
	@Override
	public PayResponse create(String orderId, BigDecimal amount, BestPayTypeEnum bestPayTypeEnum) {
		PayInfo payInfon = new PayInfo(Long.parseLong(orderId),
				PayPlatformEnum.getByBestPayTypeEnum(bestPayTypeEnum).getCode(),
				OrderStatusEnum.NOTPAY.name(), amount);
		payInfoMapper.insertSelective(payInfon);
		// 写入数据库
		PayRequest request = new PayRequest();
		request.setOrderName("7270239-最好的支付SDK");
		request.setOrderId(orderId);
		request.setOrderAmount(amount.doubleValue());
		request.setPayTypeEnum(bestPayTypeEnum);

		PayResponse response = bestPayService.pay(request);
		log.info("发起支付 response={}" + response);
		return response;
	}

	/**
	 * 异步通知处理
	 *
	 * @param notifyData
	 * @return void
	 */
	@Override
	public String asynNotify(String notifyData) {
		// 1.签名校验
		PayResponse response = bestPayService.asyncNotify(notifyData);
		log.info(" 异步通知  response={}", response);
		// 2.金额校验（从数据库中查询订单）
		// 比较严重的情况(正常情况下不会发生的)，发出警告，钉钉、短信等
		PayInfo payInfo = payInfoMapper.selectByOrderNo(Long.parseLong(response.getOrderId()));
		if (payInfo == null) {
			// 1.这里发出警告
			// 2.抛异常
			throw new RuntimeException("通过OrderNo查询的结果是null");
		}
		// 如果订单支付状态不是"已支付"
		if (!OrderStatusEnum.SUCCESS.name().equals(payInfo.getPlatformStatus())) {
			// 比较金额
			if (payInfo.getPayAmount().compareTo(BigDecimal.valueOf(response.getOrderAmount())) != 0) {
				// 1.这里发出警告
				// 2.抛异常
				throw new RuntimeException("异步通知金额和数据库金额不一致，orderNo=" + response.getOrderId());
			}
			// 3.修改订单状态
			payInfo.setPlatformStatus(OrderStatusEnum.SUCCESS.name());
			payInfo.setPlatformNumber(response.getOutTradeNo());
			// 交由mysql 管理
			// payInfo.setUpdateTime(null);
			payInfoMapper.updateByPrimaryKeySelective(payInfo);
		}

		// TODO pay发送MQ消息，mall接收MQ消息
		amqpTemplate.convertAndSend(QUEUE_PAY_NOTIFY, new Gson().toJson(payInfo));
		if (response.getPayPlatformEnum() == BestPayPlatformEnum.WX) {
			// 4.告诉微信不要再通知
			return "<xml>\n" + "  <return_code><![CDATA[SUCCESS]]></return_code>\n"
					+ "  <return_msg><![CDATA[OK]]></return_msg>\n" + "</xml>";
		} else if (response.getPayPlatformEnum() == BestPayPlatformEnum.ALIPAY) {
			return "success";
		}
		throw new RuntimeException("异步平台格式错误");

	}

	/**
	 * 通过订单号查询支付记录
	 *
	 * @param orderId
	 * @return
	 */
	@Override
	public PayInfo queryByOrderId(String orderId) {
		return payInfoMapper.selectByOrderNo(Long.parseLong(orderId));
	}

}
