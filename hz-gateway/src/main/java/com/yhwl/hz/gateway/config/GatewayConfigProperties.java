package com.yhwl.hz.gateway.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;

/**
 * @author hz
 * @date 2020/10/2
 * <p>
 * 网关通用配置文件
 */
@Data
@Component
@RefreshScope
@ConfigurationProperties("gateway")
public class GatewayConfigProperties {

	/**
	 * 网关解密登录前端密码 秘钥 {@link com.yhwl.hz.gateway.filter.PasswordDecoderFilter}
	 */
	public String encodeKey;

}
