/*
 *
 *      Copyright (c) 2018-2025, hz All rights reserved.
 *
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions are met:
 *
 * Redistributions of source code must retain the above copyright notice,
 *  this list of conditions and the following disclaimer.
 *  Redistributions in binary form must reproduce the above copyright
 *  notice, this list of conditions and the following disclaimer in the
 *  documentation and/or other materials provided with the distribution.
 *  Neither the name of the pig4cloud.com developer nor the names of its
 *  contributors may be used to endorse or promote products derived from
 *  this software without specific prior written permission.
 *  Author: hz
 *
 */

package com.yhwl.hz.auth.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yhwl.hz.common.security.component.HzCommenceAuthExceptionEntryPoint;
import com.yhwl.hz.common.security.component.HzWebResponseExceptionTranslator;
import com.yhwl.hz.common.security.grant.ResourceOwnerCustomeAppTokenGranter;
import com.yhwl.hz.common.security.service.HzCustomTokenServices;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.CompositeTokenGranter;
import org.springframework.security.oauth2.provider.TokenGranter;
import org.springframework.security.oauth2.provider.code.AuthorizationCodeServices;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;
import org.springframework.security.oauth2.provider.token.TokenStore;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * @author hz
 * @date 2018/6/22 ?????????????????????
 */
@Configuration
@AllArgsConstructor
@EnableAuthorizationServer
public class AuthorizationServerConfiguration extends AuthorizationServerConfigurerAdapter {

	private final ClientDetailsService hzClientDetailsServiceImpl;

	private final AuthenticationManager authenticationManagerBean;

	private final AuthorizationCodeServices authorizationCodeServices;

	private final AuthenticationManager authenticationManager;

	private final TokenStore redisTokenStore;

	private final TokenEnhancer tokenEnhancer;

	private final ObjectMapper objectMapper;

	@Override
	@SneakyThrows
	public void configure(ClientDetailsServiceConfigurer clients) {
		clients.withClientDetails(hzClientDetailsServiceImpl);
	}

	@Override
	public void configure(AuthorizationServerSecurityConfigurer oauthServer) {
		oauthServer.allowFormAuthenticationForClients()
				.authenticationEntryPoint(new HzCommenceAuthExceptionEntryPoint(objectMapper))
				.checkTokenAccess("isAuthenticated()");
	}

	@Override
	public void configure(AuthorizationServerEndpointsConfigurer endpoints) {
		endpoints.allowedTokenEndpointRequestMethods(HttpMethod.GET, HttpMethod.POST).tokenServices(tokenServices())
				.tokenStore(redisTokenStore).tokenEnhancer(tokenEnhancer)
				.authorizationCodeServices(authorizationCodeServices).authenticationManager(authenticationManagerBean)
				.reuseRefreshTokens(false).pathMapping("/oauth/confirm_access", "/token/confirm_access")
				.exceptionTranslator(new HzWebResponseExceptionTranslator());

		// ???????????????????????????
		setTokenGranter(endpoints);
	}

	/**
	 * ????????? APP ????????????
	 * @param endpoints AuthorizationServerEndpointsConfigurer
	 */
	private void setTokenGranter(AuthorizationServerEndpointsConfigurer endpoints) {
		// ????????????????????????
		TokenGranter tokenGranter = endpoints.getTokenGranter();
		ArrayList<TokenGranter> tokenGranters = new ArrayList<>(Arrays.asList(tokenGranter));
		ResourceOwnerCustomeAppTokenGranter resourceOwnerCustomeAppTokenGranter = new ResourceOwnerCustomeAppTokenGranter(
				authenticationManager, endpoints.getTokenServices(), endpoints.getClientDetailsService(),
				endpoints.getOAuth2RequestFactory());
		tokenGranters.add(resourceOwnerCustomeAppTokenGranter);
		CompositeTokenGranter compositeTokenGranter = new CompositeTokenGranter(tokenGranters);
		endpoints.tokenGranter(compositeTokenGranter);
	}

	@Bean
	public HzCustomTokenServices tokenServices() {
		HzCustomTokenServices tokenServices = new HzCustomTokenServices();
		tokenServices.setTokenStore(redisTokenStore);
		tokenServices.setSupportRefreshToken(true);
		tokenServices.setReuseRefreshToken(false);
		tokenServices.setClientDetailsService(hzClientDetailsServiceImpl);
		tokenServices.setTokenEnhancer(tokenEnhancer);
		return tokenServices;
	}

}
