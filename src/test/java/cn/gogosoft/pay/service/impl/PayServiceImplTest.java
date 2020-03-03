package cn.gogosoft.pay.service.impl;

import java.math.BigDecimal;

import org.junit.Test;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;

import com.lly835.bestpay.enums.BestPayTypeEnum;

import cn.gogosoft.pay.PayApplicationTests;

/**
 * @author renzongchen
 * @data 2020-02-22 18:59
 * @description
 */
public class PayServiceImplTest extends PayApplicationTests {
	@Autowired
	PayServiceImpl payServiceImpl;
	@Autowired
	private AmqpTemplate amqpTemplate;

	@Test
	public void create() {
		payServiceImpl.create("1234556788877", BigDecimal.valueOf(0.01), BestPayTypeEnum.ALIPAY_PC);
	}

	@Test
	public void sendMQMsg() {
		amqpTemplate.convertAndSend("payNotify", "hello");
	}
}