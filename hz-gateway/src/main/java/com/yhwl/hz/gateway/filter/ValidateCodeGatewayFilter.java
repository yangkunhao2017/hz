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

package com.yhwl.hz.gateway.filter;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.anji.captcha.model.vo.CaptchaVO;
import com.anji.captcha.service.CaptchaService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yhwl.hz.common.core.constant.CacheConstants;
import com.yhwl.hz.common.core.constant.CommonConstants;
import com.yhwl.hz.common.core.constant.SecurityConstants;
import com.yhwl.hz.common.core.constant.enums.CaptchaFlagTypeEnum;
import com.yhwl.hz.common.core.exception.ValidateCodeException;
import com.yhwl.hz.common.core.util.R;
import com.yhwl.hz.common.core.util.SpringContextHolder;
import com.yhwl.hz.common.core.util.WebUtils;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

/**
 * @author hz
 * @date 2020/5/19 ???????????????????????????
 */
@Slf4j
@Component
@AllArgsConstructor
@SuppressWarnings("all")
public class ValidateCodeGatewayFilter extends AbstractGatewayFilterFactory {

	private final ObjectMapper objectMapper;

	private final RedisTemplate redisTemplate;

	@Override
	public GatewayFilter apply(Object config) {
		return (exchange, chain) -> {
			ServerHttpRequest request = exchange.getRequest();

			// ????????????????????????????????????
			if (!StrUtil.containsAnyIgnoreCase(request.getURI().getPath(), SecurityConstants.OAUTH_TOKEN_URL)) {
				return chain.filter(exchange);
			}

			// ??????token?????????????????????
			String grantType = request.getQueryParams().getFirst("grant_type");
			if (StrUtil.equals(SecurityConstants.REFRESH_TOKEN, grantType)) {
				return chain.filter(exchange);
			}

			// ?????????????????????????????????
			boolean smsFlag = StrUtil.contains(request.getQueryParams().getFirst(SecurityConstants.GRANT_MOBILE),
					"SMS");
			// ?????????????????????????????????
			if (!isCheckCaptchaClient(request) && !smsFlag) {
				return chain.filter(exchange);
			}

			try {
				// ???????????????
				checkCode(request);
			}
			catch (Exception e) {
				ServerHttpResponse response = exchange.getResponse();
				response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
				response.setStatusCode(HttpStatus.PRECONDITION_REQUIRED);
				try {
					return response.writeWith(Mono.just(
							response.bufferFactory().wrap(objectMapper.writeValueAsBytes(R.failed(e.getMessage())))));
				}
				catch (JsonProcessingException e1) {
					log.error("??????????????????", e1);
				}
			}

			return chain.filter(exchange);
		};
	}

	/**
	 * ????????????????????????????????????client ?????????????????????
	 * @param request ??????
	 * @return true ??????????????? false ???????????????
	 */
	private boolean isCheckCaptchaClient(ServerHttpRequest request) {
		String header = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
		String clientId = WebUtils.extractClientId(header).orElse(null);
		// ?????????????????????????????????key
		String tenantId = request.getHeaders().getFirst(CommonConstants.TENANT_ID);
		String key = String.format("%s:%s:%s", StrUtil.isBlank(tenantId) ? CommonConstants.TENANT_ID_1 : tenantId,
				CacheConstants.CLIENT_FLAG, clientId);

		redisTemplate.setKeySerializer(new StringRedisSerializer());
		Object val = redisTemplate.opsForValue().get(key);

		// ????????????????????????????????????
		if (val == null) {
			return false;
		}

		JSONObject information = JSONUtil.parseObj(val.toString());
		if (StrUtil.equals(CaptchaFlagTypeEnum.OFF.getType(), information.getStr(CommonConstants.CAPTCHA_FLAG))) {
			return false;
		}
		return true;
	}

	/**
	 * ??????code
	 * @param request
	 */
	@SneakyThrows
	private void checkCode(ServerHttpRequest request) {
		String code = request.getQueryParams().getFirst("code");

		if (StrUtil.isBlank(code)) {
			throw new ValidateCodeException("?????????????????????");
		}

		String randomStr = request.getQueryParams().getFirst("randomStr");

		// ??????????????????
		if (CommonConstants.IMAGE_CODE_TYPE.equalsIgnoreCase(randomStr)) {
			CaptchaService captchaService = SpringContextHolder.getBean(CaptchaService.class);
			CaptchaVO vo = new CaptchaVO();
			vo.setCaptchaVerification(code);
			vo.setCaptchaType(CommonConstants.IMAGE_CODE_TYPE);
			if (!captchaService.verification(vo).isSuccess()) {
				throw new ValidateCodeException("?????????????????????");
			}
			return;
		}

		// https://gitee.com/log4j/pig/issues/IWA0D
		String mobile = request.getQueryParams().getFirst("mobile");
		if (StrUtil.isNotBlank(mobile)) {
			randomStr = mobile;
		}

		String key = CacheConstants.DEFAULT_CODE_KEY + randomStr;
		redisTemplate.setKeySerializer(new StringRedisSerializer());

		if (!redisTemplate.hasKey(key)) {
			throw new ValidateCodeException("??????????????????");
		}

		Object codeObj = redisTemplate.opsForValue().get(key);

		if (codeObj == null) {
			throw new ValidateCodeException("??????????????????");
		}

		String saveCode = codeObj.toString();
		if (StrUtil.isBlank(saveCode)) {
			redisTemplate.delete(key);
			throw new ValidateCodeException("??????????????????");
		}

		if (!StrUtil.equals(saveCode, code)) {
			redisTemplate.delete(key);
			throw new ValidateCodeException("??????????????????");
		}

		redisTemplate.delete(key);
	}

}
