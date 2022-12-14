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

package com.yhwl.hz.auth.handler;

import cn.hutool.core.util.StrUtil;
import com.yhwl.hz.admin.api.dto.SysLogDTO;
import com.yhwl.hz.admin.api.feign.RemoteLogService;
import com.yhwl.hz.common.core.constant.CommonConstants;
import com.yhwl.hz.common.core.constant.SecurityConstants;
import com.yhwl.hz.common.core.util.KeyStrResolver;
import com.yhwl.hz.common.core.util.WebUtils;
import com.yhwl.hz.common.log.util.LogTypeEnum;
import com.yhwl.hz.common.log.util.SysLogUtils;
import com.yhwl.hz.common.security.handler.AuthenticationFailureHandler;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author hz
 * @date 2018/10/8
 */
@Slf4j
@Component
@AllArgsConstructor
public class HzAuthenticationFailureLogEventHandler implements AuthenticationFailureHandler {

	private final RemoteLogService logService;

	private final KeyStrResolver tenantKeyStrResolver;

	/**
	 * ?????????????????????????????????
	 * <p>
	 * @param authenticationException ?????????authentication ??????
	 * @param authentication ?????????authenticationException ??????
	 * @param request ??????
	 * @param response ??????
	 */
	@Async
	@Override
	@SneakyThrows
	public void handle(AuthenticationException authenticationException, Authentication authentication,
			HttpServletRequest request, HttpServletResponse response) {
		String username = authentication.getName();
		SysLogDTO sysLog = SysLogUtils.getSysLog(request, username);

		String startTimeStr = request.getHeader(CommonConstants.REQUEST_START_TIME);
		if (StrUtil.isNotBlank(startTimeStr)) {
			Long startTime = Long.parseLong(startTimeStr);
			Long endTime = System.currentTimeMillis();
			sysLog.setTime(endTime - startTime);
		}
		sysLog.setTitle(username + "????????????");
		sysLog.setLogType(LogTypeEnum.ERROR.getType());
		sysLog.setParams(username);
		sysLog.setException(authenticationException.getLocalizedMessage());
		String header = request.getHeader(HttpHeaders.AUTHORIZATION);
		sysLog.setServiceId(WebUtils.extractClientId(header).orElse("N/A"));
		sysLog.setTenantId(Long.parseLong(tenantKeyStrResolver.key()));

		logService.saveLog(sysLog, SecurityConstants.FROM_IN);

		log.info("?????????{} ????????????????????????{}", username, authenticationException.getLocalizedMessage());
	}

}
