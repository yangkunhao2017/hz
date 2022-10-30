package com.yhwl.hz.common.data.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;

/**
 * Mybatis 配置
 *
 * @author hz
 * @date 2021/6/3
 */
@Data
@RefreshScope
@ConfigurationProperties("hz.mybatis")
public class HzMybatisProperties {

	/**
	 * 是否打印可执行 sql
	 */
	private boolean showSql = true;

}
