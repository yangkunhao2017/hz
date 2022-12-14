/*
 *    Copyright (c) 2018-2025, hz All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer.
 * Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 * Neither the name of the pig4cloud.com developer nor the names of its
 * contributors may be used to endorse or promote products derived from
 * this software without specific prior written permission.
 * Author: hz
 */
package com.yhwl.hz.common.swagger.config;

import com.yhwl.hz.common.swagger.support.SwaggerProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.ReflectionUtils;
import org.springframework.web.servlet.mvc.method.RequestMappingInfoHandlerMapping;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.builders.RequestParameterBuilder;
import springfox.documentation.schema.ScalarType;
import springfox.documentation.service.*;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.plugins.ApiSelectorBuilder;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.spring.web.plugins.WebMvcRequestHandlerProvider;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * @author hz swagger?????? ????????????1???????????????@Profile({"dev","test"})
 * ?????????????????????????????????????????????????????????????????????????????? ????????????2???????????????@ConditionalOnProperty(name = "swagger.enable",
 * havingValue = "true") ????????????????????????????????????????????????swagger.enable=true????????????????????????????????????????????????Swagger.
 */
@Configuration
@EnableAutoConfiguration
@RequiredArgsConstructor
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
public class SwaggerAutoConfiguration {

	/**
	 * ??????????????????????????????Spring Boot????????????????????????????????????
	 */
	private static final List<String> DEFAULT_EXCLUDE_PATH = Arrays.asList("/error", "/actuator/**");

	private static final String BASE_PATH = "/**";

	private final SwaggerProperties swaggerProperties;

	@Bean
	@ConditionalOnProperty(name = "swagger.enabled", matchIfMissing = true)
	public Docket api() {
		// base-path??????
		if (swaggerProperties.getBasePath().isEmpty()) {
			swaggerProperties.getBasePath().add(BASE_PATH);
		}

		// exclude-path??????
		if (swaggerProperties.getExcludePath().isEmpty()) {
			swaggerProperties.getExcludePath().addAll(DEFAULT_EXCLUDE_PATH);
		}
		List<Predicate<String>> excludePath = new ArrayList<>();
		swaggerProperties.getExcludePath().forEach(path -> excludePath.add(PathSelectors.ant(path)));

		// ?????????????????????
		List<RequestParameter> pars = new ArrayList<>();

		RequestParameterBuilder versionPar = new RequestParameterBuilder().description("????????????????????????")
				.in(ParameterType.HEADER).name("VERSION").required(false)
				.query(param -> param.model(model -> model.scalarModel(ScalarType.STRING)));

		pars.add(versionPar.build());

		ApiSelectorBuilder builder = new Docket(DocumentationType.SWAGGER_2).host(swaggerProperties.getHost())
				.apiInfo(apiInfo(swaggerProperties)).globalRequestParameters(pars).select()
				.apis(RequestHandlerSelectors.basePackage(swaggerProperties.getBasePackage()));

		swaggerProperties.getBasePath().forEach(p -> builder.paths(PathSelectors.ant(p)));
		swaggerProperties.getExcludePath().forEach(p -> builder.paths(PathSelectors.ant(p).negate()));

		return builder.build().securitySchemes(Collections.singletonList(securitySchema()))
				.securityContexts(Collections.singletonList(securityContext())).pathMapping("/");
	}

	/**
	 * ???????????????????????????????????????????????????????????????????????????????????????????????????URL
	 * @return
	 */
	private SecurityContext securityContext() {
		return SecurityContext.builder().securityReferences(defaultAuth()).build();
	}

	/**
	 * ???????????????????????????
	 * @return
	 */
	private List<SecurityReference> defaultAuth() {
		ArrayList<AuthorizationScope> authorizationScopeList = new ArrayList<>();
		swaggerProperties.getAuthorization().getAuthorizationScopeList()
				.forEach(authorizationScope -> authorizationScopeList.add(
						new AuthorizationScope(authorizationScope.getScope(), authorizationScope.getDescription())));
		AuthorizationScope[] authorizationScopes = new AuthorizationScope[authorizationScopeList.size()];
		return Collections
				.singletonList(SecurityReference.builder().reference(swaggerProperties.getAuthorization().getName())
						.scopes(authorizationScopeList.toArray(authorizationScopes)).build());
	}

	private OAuth securitySchema() {
		ArrayList<AuthorizationScope> authorizationScopeList = new ArrayList<>();
		swaggerProperties.getAuthorization().getAuthorizationScopeList()
				.forEach(authorizationScope -> authorizationScopeList.add(
						new AuthorizationScope(authorizationScope.getScope(), authorizationScope.getDescription())));
		ArrayList<GrantType> grantTypes = new ArrayList<>();
		swaggerProperties.getAuthorization().getTokenUrlList()
				.forEach(tokenUrl -> grantTypes.add(new ResourceOwnerPasswordCredentialsGrant(tokenUrl)));
		return new OAuth(swaggerProperties.getAuthorization().getName(), authorizationScopeList, grantTypes);
	}

	private ApiInfo apiInfo(SwaggerProperties swaggerProperties) {
		return new ApiInfoBuilder().title(swaggerProperties.getTitle()).description(swaggerProperties.getDescription())
				.license(swaggerProperties.getLicense()).licenseUrl(swaggerProperties.getLicenseUrl())
				.termsOfServiceUrl(swaggerProperties.getTermsOfServiceUrl())
				.contact(new Contact(swaggerProperties.getContact().getName(), swaggerProperties.getContact().getUrl(),
						swaggerProperties.getContact().getEmail()))
				.version(swaggerProperties.getVersion()).build();
	}

	@Bean
	public BeanPostProcessor springfoxHandlerProviderBeanPostProcessor() {
		return new BeanPostProcessor() {

			@Override
			public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
				if (bean instanceof WebMvcRequestHandlerProvider) {
					customizeSpringfoxHandlerMappings(getHandlerMappings(bean));
				}
				return bean;
			}

			private <T extends RequestMappingInfoHandlerMapping> void customizeSpringfoxHandlerMappings(
					List<T> mappings) {
				List<T> copy = mappings.stream().filter(mapping -> mapping.getPatternParser() == null)
						.collect(Collectors.toList());
				mappings.clear();
				mappings.addAll(copy);
			}

			@SuppressWarnings("unchecked")
			private List<RequestMappingInfoHandlerMapping> getHandlerMappings(Object bean) {
				try {
					Field field = ReflectionUtils.findField(bean.getClass(), "handlerMappings");
					field.setAccessible(true);
					return (List<RequestMappingInfoHandlerMapping>) field.get(bean);
				}
				catch (IllegalArgumentException | IllegalAccessException e) {
					throw new IllegalStateException(e);
				}
			}
		};
	}

}
