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

package com.yhwl.hz.common.data.mybatis;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.TenantLineInnerInterceptor;
import com.yhwl.hz.admin.api.feign.RemoteDataScopeService;
import com.yhwl.hz.common.data.config.HzMybatisProperties;
import com.yhwl.hz.common.data.datascope.*;
import com.yhwl.hz.common.data.resolver.SqlFilterArgumentResolver;
import com.yhwl.hz.common.data.tenant.HzTenantConfigProperties;
import com.yhwl.hz.common.data.tenant.HzTenantHandler;
import com.yhwl.hz.common.security.service.HzUser;
import org.apache.ibatis.mapping.DatabaseIdProvider;
import org.apache.ibatis.mapping.VendorDatabaseIdProvider;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.sql.DataSource;
import java.util.List;
import java.util.Properties;

/**
 * @author hz
 * @date 2020-02-08
 */
@Configuration
@ConditionalOnBean(DataSource.class)
@AutoConfigureAfter(DataSourceAutoConfiguration.class)
@EnableConfigurationProperties(HzMybatisProperties.class)
public class MybatisPlusConfiguration implements WebMvcConfigurer {

	/**
	 * ?????????????????????????????????????????????????????????SQL ??????
	 * @param resolverList
	 */
	@Override
	public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolverList) {
		resolverList.add(new SqlFilterArgumentResolver());
	}

	/**
	 * mybatis plus ???????????????
	 * @return HzDefaultDatascopeHandle
	 */
	@Bean
	public MybatisPlusInterceptor mybatisPlusInterceptor(TenantLineInnerInterceptor tenantLineInnerInterceptor,
			DataScopeInterceptor dataScopeInterceptor) {
		MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
		// ?????????????????????
		interceptor.addInnerInterceptor(tenantLineInnerInterceptor);
		// ????????????
		interceptor.addInnerInterceptor(dataScopeInterceptor);
		// ????????????
		PaginationInnerInterceptor paginationInnerInterceptor = new PaginationInnerInterceptor();
		paginationInnerInterceptor.setMaxLimit(1000L);
		interceptor.addInnerInterceptor(paginationInnerInterceptor);
		return interceptor;
	}

	/**
	 * ?????????????????????????????????
	 * @return ?????????????????????????????????
	 */
	@Bean
	@ConditionalOnMissingBean
	public TenantLineInnerInterceptor tenantLineInnerInterceptor(HzTenantConfigProperties tenantConfigProperties) {
		TenantLineInnerInterceptor tenantLineInnerInterceptor = new TenantLineInnerInterceptor();
		tenantLineInnerInterceptor.setTenantLineHandler(new HzTenantHandler(tenantConfigProperties));
		return tenantLineInnerInterceptor;
	}

	/**
	 * ?????????????????????
	 * @return DataScopeInterceptor
	 */
	@Bean
	@ConditionalOnMissingBean
	@ConditionalOnClass(HzUser.class)
	public DataScopeInterceptor dataScopeInterceptor(RemoteDataScopeService dataScopeService) {
		DataScopeInnerInterceptor dataScopeInnerInterceptor = new DataScopeInnerInterceptor();
		dataScopeInnerInterceptor.setDataScopeHandle(new HzDefaultDatascopeHandle(dataScopeService));
		return dataScopeInnerInterceptor;
	}

	/**
	 * ?????? mybatis-plus baseMapper ??????????????????
	 * @return
	 */
	@Bean
	@ConditionalOnBean(DataScopeInterceptor.class)
	public DataScopeSqlInjector dataScopeSqlInjector() {
		return new DataScopeSqlInjector();
	}

	/**
	 * SQL ???????????????
	 * @return DruidSqlLogFilter
	 */
	@Bean
	public DruidSqlLogFilter sqlLogFilter(HzMybatisProperties properties) {
		return new DruidSqlLogFilter(properties);
	}

	/**
	 * ????????????????????????
	 * @return {@link MetaObjectHandler}
	 */
	@Bean
	public MybatisPlusMetaObjectHandler mybatisPlusMetaObjectHandler() {
		return new MybatisPlusMetaObjectHandler();
	}

	/**
	 * ?????????????????????
	 * @return
	 */
	@Bean
	public DatabaseIdProvider databaseIdProvider() {
		VendorDatabaseIdProvider databaseIdProvider = new VendorDatabaseIdProvider();
		Properties properties = new Properties();
		properties.setProperty("SQL Server", "mssql");
		databaseIdProvider.setProperties(properties);
		return databaseIdProvider;
	}

}
