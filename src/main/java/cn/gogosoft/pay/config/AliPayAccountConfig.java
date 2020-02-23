package cn.gogosoft.pay.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Data;

/**
 * @author renzongchen
 * @data 2020-02-24 01:10
 * @description 支付宝支付配置
 */
@Component
@ConfigurationProperties(prefix = "alipay")
@Data
public class AliPayAccountConfig {
	private String appId;
	private String privateKey;
	private String aliPayPublicKey;
	private String notifyUrl;
	private String returnUrl;
}
