package cn.gogosoft.pay.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import com.lly835.bestpay.config.AliPayConfig;
import com.lly835.bestpay.config.WxPayConfig;
import com.lly835.bestpay.service.BestPayService;
import com.lly835.bestpay.service.impl.BestPayServiceImpl;

/**
 * @author renzongchen
 * @data 2020-02-23 16:05
 * @description 支付配置类
 */
@Component
public class BestPayConfig {
	@Autowired
	private WxAccountConfig wxAccountConfig;
	@Autowired
	private AliPayAccountConfig aliPayAccountConfig;

	@Bean
	public BestPayService bestPayService(WxPayConfig wxPayConfig, AliPayConfig aliPayConfig) {
		BestPayServiceImpl bestPayService = new BestPayServiceImpl();
		bestPayService.setWxPayConfig(wxPayConfig);
		bestPayService.setAliPayConfig(aliPayConfig);
		return bestPayService;

	}

	@Bean
	public AliPayConfig aliPayConfig() {
		AliPayConfig aliPayConfig = new AliPayConfig();
		aliPayConfig.setAppId(aliPayAccountConfig.getAppId());
		aliPayConfig.setPrivateKey(aliPayAccountConfig.getPrivateKey());
		aliPayConfig.setAliPayPublicKey(aliPayAccountConfig.getAliPayPublicKey());
		aliPayConfig.setNotifyUrl(aliPayAccountConfig.getNotifyUrl() + "/pay/notify");
		aliPayConfig.setReturnUrl(aliPayAccountConfig.getReturnUrl());
		return aliPayConfig;
	}

	@Bean
	public WxPayConfig wxPayConfig() {
		WxPayConfig wxPayConfig = new WxPayConfig();
		wxPayConfig.setAppId(wxAccountConfig.getAppId());
		wxPayConfig.setMchId(wxAccountConfig.getMchId());
		wxPayConfig.setMchKey(wxAccountConfig.getMchKey());
		wxPayConfig.setNotifyUrl(wxAccountConfig.getNotifyUrl() + "/pay/notify");
		wxPayConfig.setReturnUrl(wxAccountConfig.getReturnUrl());
		return wxPayConfig;
	}
}
