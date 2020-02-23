package cn.gogosoft.pay.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Data;

/**
 * @author renzongchen
 * @data 2020-02-24 00:58
 * @description 微信支付配置
 */
@Component
@ConfigurationProperties(prefix = "wx")
@Data
public class WxAccountConfig {
	private String appId;
	private String mchId;
	private String mchKey;
	private String notifyUrl;
	private String returnUrl;
}
