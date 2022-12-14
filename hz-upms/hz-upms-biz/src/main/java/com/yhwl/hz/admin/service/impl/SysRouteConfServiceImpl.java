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

package com.yhwl.hz.admin.service.impl;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yhwl.hz.admin.api.entity.SysRouteConf;
import com.yhwl.hz.admin.mapper.SysRouteConfMapper;
import com.yhwl.hz.admin.service.SysRouteConfService;
import com.yhwl.hz.common.core.constant.CacheConstants;
import com.yhwl.hz.common.core.constant.CommonConstants;
import com.yhwl.hz.common.gateway.support.DynamicRouteInitEvent;
import com.yhwl.hz.common.gateway.vo.RouteDefinitionVo;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.FilterDefinition;
import org.springframework.cloud.gateway.handler.predicate.PredicateDefinition;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author hz
 * @date 2018???11???06???10:27:55
 * <p>
 * ?????????????????????
 */
@Slf4j
@AllArgsConstructor
@Service("sysRouteConfService")
public class SysRouteConfServiceImpl extends ServiceImpl<SysRouteConfMapper, SysRouteConf>
		implements SysRouteConfService {

	private final RedisTemplate redisTemplate;

	private final ApplicationEventPublisher applicationEventPublisher;

	/**
	 * ??????????????????
	 * @param routes ????????????
	 * @return
	 */
	@Override
	@Transactional(rollbackFor = Exception.class)
	public Mono<Void> updateRoutes(JSONArray routes) {
		// ??????Redis ??????
		Boolean result = redisTemplate.delete(CacheConstants.ROUTE_KEY);
		log.info("?????????????????? {} ", result);

		// ???????????????routes????????????Redis
		List<RouteDefinitionVo> routeDefinitionVoList = new ArrayList<>();

		try {
			routes.forEach(value -> {
				log.info("???????????? ->{}", value);
				RouteDefinitionVo vo = new RouteDefinitionVo();
				Map<String, Object> map = (Map) value;

				Object id = map.get("routeId");
				if (id != null) {
					vo.setId(String.valueOf(id));
				}

				Object routeName = map.get("routeName");
				if (routeName != null) {
					vo.setRouteName(String.valueOf(routeName));
				}

				Object predicates = map.get("predicates");
				if (predicates != null) {
					JSONArray predicatesArray = (JSONArray) predicates;
					List<PredicateDefinition> predicateDefinitionList = predicatesArray
							.toList(PredicateDefinition.class);
					vo.setPredicates(predicateDefinitionList);
				}

				Object filters = map.get("filters");
				if (filters != null) {
					JSONArray filtersArray = (JSONArray) filters;
					List<FilterDefinition> filterDefinitionList = filtersArray.toList(FilterDefinition.class);
					vo.setFilters(filterDefinitionList);
				}

				Object uri = map.get("uri");
				if (uri != null) {
					vo.setUri(URI.create(String.valueOf(uri)));
				}

				Object order = map.get("order");
				if (order != null) {
					vo.setOrder(Integer.parseInt(String.valueOf(order)));
				}

				Object metadata = map.get("metadata");
				if (metadata != null) {
					Map<String, Object> metadataMap = JSONUtil.toBean(String.valueOf(metadata), Map.class);
					vo.setMetadata(metadataMap);
				}

				redisTemplate.setHashValueSerializer(new Jackson2JsonRedisSerializer<>(RouteDefinitionVo.class));
				redisTemplate.opsForHash().put(CacheConstants.ROUTE_KEY, vo.getId(), vo);
				routeDefinitionVoList.add(vo);
			});

			// ??????????????????
			SysRouteConf condition = new SysRouteConf();
			condition.setDelFlag(CommonConstants.STATUS_NORMAL);
			this.remove(new UpdateWrapper<>(condition));

			// ??????????????????
			List<SysRouteConf> routeConfList = routeDefinitionVoList.stream().map(vo -> {
				SysRouteConf routeConf = new SysRouteConf();
				routeConf.setRouteId(vo.getId());
				routeConf.setRouteName(vo.getRouteName());
				routeConf.setFilters(JSONUtil.toJsonStr(vo.getFilters()));
				routeConf.setPredicates(JSONUtil.toJsonStr(vo.getPredicates()));
				routeConf.setSortOrder(vo.getOrder());
				routeConf.setUri(vo.getUri().toString());
				routeConf.setMetadata(JSONUtil.toJsonStr(vo.getMetadata()));
				return routeConf;
			}).collect(Collectors.toList());
			this.saveBatch(routeConfList);
			log.debug("???????????????????????? ");

			// ????????????????????????
			redisTemplate.convertAndSend(CacheConstants.ROUTE_JVM_RELOAD_TOPIC, "????????????,??????????????????");
		}
		catch (Exception e) {
			log.error("????????????????????????", e);
			// ?????????????????????????????????
			this.applicationEventPublisher.publishEvent(new DynamicRouteInitEvent(this));
			// ????????????
			throw new RuntimeException(e);
		}
		return Mono.empty();
	}

}
