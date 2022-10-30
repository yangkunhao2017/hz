package com.yhwl.hz.common.swagger.support;

import com.yhwl.hz.common.swagger.config.GatewaySwaggerAutoConfiguration;
import com.yhwl.hz.common.swagger.config.SwaggerAutoConfiguration;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.type.AnnotationMetadata;

/**
 * @author hz
 * @date 2022/6/28
 */
public class SwaggerBeanDefinitionRegistrar implements ImportBeanDefinitionRegistrar {

	/**
	 * 根据注解值动态注入资源服务器的相关属性
	 * @param metadata 注解信息
	 * @param registry 注册器
	 */
	@Override
	public void registerBeanDefinitions(AnnotationMetadata metadata, BeanDefinitionRegistry registry) {

		// swagger 功能依赖系统的动态路由，需要读取路由表中的参数
		if (registry.isBeanNameInUse("com.yhwl.hz.common.gateway.configuration.DynamicRouteAutoConfiguration")) {
			GenericBeanDefinition gatewaySwagger = new GenericBeanDefinition();
			gatewaySwagger.setBeanClass(GatewaySwaggerAutoConfiguration.class);
			registry.registerBeanDefinition(GatewaySwaggerAutoConfiguration.class.getSimpleName(), gatewaySwagger);
		}
		else {
			GenericBeanDefinition swaggerAutoConfiguration = new GenericBeanDefinition();
			swaggerAutoConfiguration.setBeanClass(SwaggerAutoConfiguration.class);
			registry.registerBeanDefinition(SwaggerAutoConfiguration.class.getSimpleName(), swaggerAutoConfiguration);
		}
	}

}
