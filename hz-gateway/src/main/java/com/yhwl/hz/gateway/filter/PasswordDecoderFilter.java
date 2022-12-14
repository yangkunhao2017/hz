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

import cn.hutool.core.util.CharsetUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.Mode;
import cn.hutool.crypto.Padding;
import cn.hutool.crypto.symmetric.AES;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.yhwl.hz.common.core.constant.CacheConstants;
import com.yhwl.hz.common.core.constant.CommonConstants;
import com.yhwl.hz.common.core.constant.SecurityConstants;
import com.yhwl.hz.common.core.constant.enums.EncFlagTypeEnum;
import com.yhwl.hz.common.core.util.WebUtils;
import com.yhwl.hz.gateway.config.GatewayConfigProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.cloud.gateway.filter.factory.rewrite.CachedBodyOutputMessage;
import org.springframework.cloud.gateway.support.BodyInserterContext;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.codec.HttpMessageReader;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpRequestDecorator;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserter;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.HandlerStrategies;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * @author hz
 * @date 2020/1/8 ?????????????????????
 * <p>
 * ?????? ModifyRequestBodyGatewayFilterFactory ??????
 */
@Slf4j
@Component
@RequiredArgsConstructor
@SuppressWarnings("all")
public class PasswordDecoderFilter extends AbstractGatewayFilterFactory {

	private final List<HttpMessageReader<?>> messageReaders = HandlerStrategies.withDefaults().messageReaders();

	private static final String PASSWORD = "password";

	private static final String KEY_ALGORITHM = "AES";

	private final RedisTemplate redisTemplate;

	private final GatewayConfigProperties gatewayConfig;

	@Override
	public GatewayFilter apply(Object config) {
		return (exchange, chain) -> {
			ServerHttpRequest request = exchange.getRequest();
			// 1. ???????????????????????????????????????
			if (!StrUtil.containsAnyIgnoreCase(request.getURI().getPath(), SecurityConstants.OAUTH_TOKEN_URL)) {
				return chain.filter(exchange);
			}

			// 2. ??????token???????????????????????????
			String grantType = request.getQueryParams().getFirst("grant_type");
			if (StrUtil.equals(SecurityConstants.REFRESH_TOKEN, grantType)) {
				return chain.filter(exchange);
			}

			// 3. ??????????????????????????????????????????????????????????????????
			if (!isEncClient(request)) {
				return chain.filter(exchange);
			}

			// 4. ??????????????????????????????
			Class inClass = String.class;
			Class outClass = String.class;
			ServerRequest serverRequest = ServerRequest.create(exchange, messageReaders);

			// ????????????????????????
			Mono<?> modifiedBody = serverRequest.bodyToMono(inClass).flatMap(decryptAES());

			BodyInserter bodyInserter = BodyInserters.fromPublisher(modifiedBody, outClass);
			HttpHeaders headers = new HttpHeaders();
			headers.putAll(exchange.getRequest().getHeaders());
			headers.remove(HttpHeaders.CONTENT_LENGTH);

			headers.set(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE);
			CachedBodyOutputMessage outputMessage = new CachedBodyOutputMessage(exchange, headers);
			return bodyInserter.insert(outputMessage, new BodyInserterContext()).then(Mono.defer(() -> {
				ServerHttpRequest decorator = decorate(exchange, headers, outputMessage);
				return chain.filter(exchange.mutate().request(decorator).build());
			}));
		};
	}

	/**
	 * ???????????????clientId ??????????????????????????????????????????
	 * @param request ???????????????
	 * @return true ???????????? ??? false ????????????
	 */
	private boolean isEncClient(ServerHttpRequest request) {
		String header = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
		String clientId = WebUtils.extractClientId(header).orElse(null);
		// ?????????????????????????????????key
		String tenantId = request.getHeaders().getFirst(CommonConstants.TENANT_ID);
		String key = String.format("%s:%s:%s", StrUtil.isBlank(tenantId) ? CommonConstants.TENANT_ID_1 : tenantId,
				CacheConstants.CLIENT_FLAG, clientId);

		redisTemplate.setKeySerializer(new StringRedisSerializer());
		Object val = redisTemplate.opsForValue().get(key);

		// ??????????????????????????????????????????
		if (val == null) {
			return true;
		}

		JSONObject information = JSONUtil.parseObj(val.toString());
		if (StrUtil.equals(EncFlagTypeEnum.NO.getType(), information.getStr(CommonConstants.ENC_FLAG))) {
			return false;
		}
		return true;
	}

	/**
	 * ????????????
	 * @return
	 */
	private Function decryptAES() {
		return s -> {
			// ????????????????????????AES ??????
			AES aes = new AES(Mode.CFB, Padding.NoPadding,
					new SecretKeySpec(gatewayConfig.getEncodeKey().getBytes(), KEY_ALGORITHM),
					new IvParameterSpec(gatewayConfig.getEncodeKey().getBytes()));

			// ???????????????????????????
			Map<String, String> inParamsMap = HttpUtil.decodeParamMap((String) s, CharsetUtil.CHARSET_UTF_8);
			if (inParamsMap.containsKey(PASSWORD)) {
				String password = aes.decryptStr(inParamsMap.get(PASSWORD));
				// ???????????????????????????
				inParamsMap.put(PASSWORD, password);
			}
			else {
				log.error("??????????????????:{}", s);
			}

			// ??????
			return Mono.just(HttpUtil.toParams(inParamsMap, Charset.defaultCharset(), true));
		};
	}

	/**
	 * ????????????
	 * @return
	 */
	private ServerHttpRequestDecorator decorate(ServerWebExchange exchange, HttpHeaders headers,
			CachedBodyOutputMessage outputMessage) {
		return new ServerHttpRequestDecorator(exchange.getRequest()) {
			@Override
			public HttpHeaders getHeaders() {
				long contentLength = headers.getContentLength();
				HttpHeaders httpHeaders = new HttpHeaders();
				httpHeaders.putAll(super.getHeaders());
				if (contentLength > 0) {
					httpHeaders.setContentLength(contentLength);
				}
				else {
					httpHeaders.set(HttpHeaders.TRANSFER_ENCODING, "chunked");
				}
				return httpHeaders;
			}

			@Override
			public Flux<DataBuffer> getBody() {
				return outputMessage.getBody();
			}
		};
	}

}
