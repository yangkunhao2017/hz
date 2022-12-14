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

package com.yhwl.hz.admin.config;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONUtil;
import com.yhwl.hz.admin.service.SysRouteConfService;
import com.yhwl.hz.common.core.constant.CacheConstants;
import com.yhwl.hz.common.gateway.support.DynamicRouteInitEvent;
import com.yhwl.hz.common.gateway.vo.RouteDefinitionVo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.web.context.WebServerInitializedEvent;
import org.springframework.cloud.gateway.filter.FilterDefinition;
import org.springframework.cloud.gateway.handler.predicate.PredicateDefinition;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.scheduling.annotation.Async;

import java.net.URI;
import java.util.Map;

/**
 * @author hz
 * @date 2018/10/31
 * <p>
 * ?????????????????????????????????????????????????????????Redis
 */
@Slf4j
@Configuration
@RequiredArgsConstructor
public class DynamicRouteInitRunner implements InitializingBean {

	private final RedisTemplate redisTemplate;

	private final SysRouteConfService routeConfService;

	private final RedisMessageListenerContainer listenerContainer;

	@Async
	@Order
	@EventListener({ WebServerInitializedEvent.class, DynamicRouteInitEvent.class })
	public void initRoute() {
		redisTemplate.delete(CacheConstants.ROUTE_KEY);
		log.info("???????????????????????????");

		routeConfService.list().forEach(route -> {
			RouteDefinitionVo vo = new RouteDefinitionVo();
			vo.setRouteName(route.getRouteName());
			vo.setId(route.getRouteId());
			vo.setUri(URI.create(route.getUri()));
			vo.setOrder(route.getSortOrder());

			JSONArray filterObj = JSONUtil.parseArray(route.getFilters());
			vo.setFilters(filterObj.toList(FilterDefinition.class));
			JSONArray predicateObj = JSONUtil.parseArray(route.getPredicates());
			vo.setPredicates(predicateObj.toList(PredicateDefinition.class));
			vo.setMetadata(JSONUtil.toBean(route.getMetadata(), Map.class));
			log.info("????????????ID???{},{}", route.getRouteId(), vo);
			redisTemplate.setHashValueSerializer(new Jackson2JsonRedisSerializer<>(RouteDefinitionVo.class));
			redisTemplate.opsForHash().put(CacheConstants.ROUTE_KEY, route.getRouteId(), vo);
		});

		// ????????????????????????
		redisTemplate.convertAndSend(CacheConstants.ROUTE_JVM_RELOAD_TOPIC, "????????????,??????????????????");
		log.debug("??????????????????????????? ");
	}

	/**
	 * redis ????????????,?????? upms_redis_route_reload_topic,????????????Redis
	 */
	@Override
	public void afterPropertiesSet() {
		listenerContainer.addMessageListener((message, bytes) -> {
			log.warn("???????????????Redis ????????????????????????");
			initRoute();
		}, new ChannelTopic(CacheConstants.ROUTE_REDIS_RELOAD_TOPIC));
	}

}
