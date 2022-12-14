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

import com.yhwl.hz.common.security.component.HzDaoAuthenticationProvider;
import com.yhwl.hz.common.security.grant.CustomAppAuthenticationProvider;
import com.yhwl.hz.common.security.handler.FormAuthenticationFailureHandler;
import com.yhwl.hz.common.security.handler.MobileLoginSuccessHandler;
import com.yhwl.hz.common.security.handler.SsoLogoutSuccessHandler;
import com.yhwl.hz.common.security.handler.TenantSavedRequestAwareAuthenticationSuccessHandler;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;

/**
 * @author hz
 * @date 2018/6/22 ??????????????????
 */
@Primary
@Order(90)
@Configuration
@RequiredArgsConstructor
public class WebSecurityConfigurer extends WebSecurityConfigurerAdapter {

	@Override
	@SneakyThrows
	protected void configure(HttpSecurity http) {
		http.formLogin().loginPage("/token/login").loginProcessingUrl("/token/form")
				.successHandler(tenantSavedRequestAwareAuthenticationSuccessHandler())
				.failureHandler(authenticationFailureHandler()).and().logout()
				.logoutSuccessHandler(logoutSuccessHandler()).deleteCookies("JSESSIONID").invalidateHttpSession(true)
				.and().authorizeRequests().antMatchers("/token/**", "/actuator/**", "/mobile/**").permitAll()
				.anyRequest().authenticated().and().csrf().disable();
	}

	/**
	 * ????????? provider ????????????
	 * @param auth AuthenticationManagerBuilder
	 */
	@Override
	protected void configure(AuthenticationManagerBuilder auth) {
		HzDaoAuthenticationProvider daoAuthenticationProvider = new HzDaoAuthenticationProvider();
		daoAuthenticationProvider.setPasswordEncoder(passwordEncoder());

		// ?????????????????????????????????
		auth.authenticationProvider(daoAuthenticationProvider);
		// ????????????????????????
		auth.authenticationProvider(new CustomAppAuthenticationProvider());
	}

	/**
	 * ?????????????????????
	 * @param web
	 */
	@Override
	public void configure(WebSecurity web) {
		web.ignoring().antMatchers("/favicon.ico", "/css/**", "/error");
	}

	@Bean
	@Override
	@SneakyThrows
	public AuthenticationManager authenticationManagerBean() {
		return super.authenticationManagerBean();
	}

	@Bean
	public AuthenticationFailureHandler authenticationFailureHandler() {
		return new FormAuthenticationFailureHandler();
	}

	@Bean
	public LogoutSuccessHandler logoutSuccessHandler() {
		return new SsoLogoutSuccessHandler();
	}

	@Bean
	public AuthenticationSuccessHandler mobileLoginSuccessHandler() {
		return new MobileLoginSuccessHandler();
	}

	/**
	 * ????????????????????????
	 * @return AuthenticationSuccessHandler
	 */
	@Bean
	public AuthenticationSuccessHandler tenantSavedRequestAwareAuthenticationSuccessHandler() {
		return new TenantSavedRequestAwareAuthenticationSuccessHandler();
	}

	/**
	 * ???????????????
	 * @return ????????????????????? {??????}??????
	 */
	@Bean
	public PasswordEncoder passwordEncoder() {
		return PasswordEncoderFactories.createDelegatingPasswordEncoder();
	}

}
