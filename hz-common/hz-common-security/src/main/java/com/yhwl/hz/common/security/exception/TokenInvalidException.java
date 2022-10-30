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

package com.yhwl.hz.common.security.exception;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.yhwl.hz.common.security.component.HzAuth2ExceptionSerializer;
import org.springframework.http.HttpStatus;

/**
 * @author hz
 * @date 2021-08-05
 * <p>
 * 令牌不合法
 */
@JsonSerialize(using = HzAuth2ExceptionSerializer.class)
public class TokenInvalidException extends HzAuth2Exception {

	public TokenInvalidException(String msg, Throwable t) {
		super(msg);
	}

	@Override
	public String getOAuth2ErrorCode() {
		return "invalid_token";
	}

	@Override
	public int getHttpErrorCode() {
		return HttpStatus.FAILED_DEPENDENCY.value();
	}

}
