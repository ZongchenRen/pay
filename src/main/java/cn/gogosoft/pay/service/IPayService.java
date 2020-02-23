package cn.gogosoft.pay.service;

import java.math.BigDecimal;

import com.lly835.bestpay.enums.BestPayTypeEnum;
import com.lly835.bestpay.model.PayResponse;

import cn.gogosoft.pay.pojo.PayInfo;

/**
 * @author renzongchen
 * @data 2020-02-22 18:28
 * @description 支付接口
 */
public interface IPayService {

	PayResponse create(String orderId, BigDecimal amount, BestPayTypeEnum bestPayTypeEnum);

	/**
	 * 异步通知处理
	 *
	 * @param notifyData
	 * @return void
	 */
	String asynNotify(String notifyData);

	/**
	 * 通过订单号查询支付记录
	 * 
	 * @param orderId
	 * @return
	 */
	PayInfo queryByOrderId(String orderId);
}
