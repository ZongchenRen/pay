package cn.gogosoft.pay.enums;

import com.lly835.bestpay.enums.BestPayTypeEnum;

import lombok.Getter;

/**
 * @author renzongchen
 * @data 2020-02-23 21:05
 * @description 支付平台:1-支付宝,2-微信
 */
@Getter
public enum PayPlatformEnum {
	/**
	 * 支付宝支付
	 */
	ALIPAY(1),
	/**
	 * 微信支付
	 */
	WX(2),;

	Integer code;

	PayPlatformEnum(Integer code) {
		this.code = code;
	}

	public static PayPlatformEnum getByBestPayTypeEnum(BestPayTypeEnum bestPayTypeEnum) {

		for (PayPlatformEnum platformEnum : PayPlatformEnum.values()) {
			if (platformEnum.name().equals(bestPayTypeEnum.getPlatform().name())) {
				return platformEnum;
			}
		}
		throw new RuntimeException("错误的支付平台" + bestPayTypeEnum.name());
	}
}
