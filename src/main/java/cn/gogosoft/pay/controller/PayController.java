package cn.gogosoft.pay.controller;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import com.lly835.bestpay.config.WxPayConfig;
import com.lly835.bestpay.enums.BestPayTypeEnum;
import com.lly835.bestpay.model.PayResponse;

import cn.gogosoft.pay.pojo.PayInfo;
import cn.gogosoft.pay.service.impl.PayServiceImpl;
import lombok.extern.slf4j.Slf4j;

/**
 * @author renzongchen
 * @data 2020-02-22 21:31
 * @description 支付
 */
@Controller
@RequestMapping("/pay")
@Slf4j
public class PayController {
	@Autowired
	private PayServiceImpl payService;
	@Autowired
	private WxPayConfig wxPayConfig;

	@GetMapping("/create")
	public ModelAndView create(@RequestParam("orderId") String orderId,
			@RequestParam("amount") BigDecimal amount,
			@RequestParam("payType") BestPayTypeEnum bestPayTypeEnum) {
		PayResponse response = payService.create(orderId, amount, bestPayTypeEnum);
		Map<String, String> map = new HashMap<>();
		// 支付方式不同，渲染不同 WXPAY_NATIVE 使用codeUrl ,ALIPAY_PC使用 body
		if (BestPayTypeEnum.WXPAY_NATIVE == bestPayTypeEnum) {
			map.put("codeUrl", response.getCodeUrl());
			map.put("orderId", orderId);
			map.put("returnUrl", wxPayConfig.getReturnUrl());
			return new ModelAndView("create", map);
		} else if (BestPayTypeEnum.ALIPAY_PC == bestPayTypeEnum) {
			map.put("body", response.getBody());
			return new ModelAndView("createForAlipayPc", map);
		}
		throw new RuntimeException("暂不支持的支付类型");
	}

	@PostMapping("/notify")
	@ResponseBody
	public String asynNotify(@RequestBody String notifyData) {
		return payService.asynNotify(notifyData);
	}

	@GetMapping("/queryByOrderId")
	@ResponseBody
	public PayInfo queryByOrderId(@RequestParam("orderId") String orderId) {
		System.out.println("查询支付记录...");
		return payService.queryByOrderId(orderId);
	}
}
