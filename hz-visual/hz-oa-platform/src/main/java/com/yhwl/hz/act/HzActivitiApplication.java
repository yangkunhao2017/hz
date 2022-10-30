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

package com.yhwl.hz.act;

import com.yhwl.hz.common.feign.annotation.EnableHzFeignClients;
import com.yhwl.hz.common.security.annotation.EnableHzResourceServer;
import com.yhwl.hz.common.swagger.annotation.EnableHzSwagger2;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * @author hz
 * @date 2018/9/25 工作流管理模块
 */
@EnableHzSwagger2
@EnableHzFeignClients
@EnableHzResourceServer
@EnableDiscoveryClient
@SpringBootApplication
public class HzActivitiApplication {

	public static void main(String[] args) {
		SpringApplication.run(HzActivitiApplication.class, args);
	}

}
