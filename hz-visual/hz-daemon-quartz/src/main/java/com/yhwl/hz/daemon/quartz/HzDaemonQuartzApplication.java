package com.yhwl.hz.daemon.quartz;

import com.yhwl.hz.common.feign.annotation.EnableHzFeignClients;
import com.yhwl.hz.common.security.annotation.EnableHzResourceServer;
import com.yhwl.hz.common.swagger.annotation.EnableHzSwagger2;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * @author frwcloud
 * @date 2019/01/23 定时任务模块
 */
@EnableHzSwagger2
@EnableHzFeignClients
@EnableHzResourceServer
@EnableDiscoveryClient
@SpringBootApplication
public class HzDaemonQuartzApplication {

	public static void main(String[] args) {
		SpringApplication.run(HzDaemonQuartzApplication.class, args);
	}

}
