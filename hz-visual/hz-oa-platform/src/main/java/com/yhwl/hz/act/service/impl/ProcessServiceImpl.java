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

package com.yhwl.hz.act.service.impl;

import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yhwl.hz.act.dto.ProcessDefDTO;
import com.yhwl.hz.act.entity.LeaveBill;
import com.yhwl.hz.act.mapper.LeaveBillMapper;
import com.yhwl.hz.act.service.ProcessService;
import com.yhwl.hz.common.core.constant.PaginationConstants;
import com.yhwl.hz.common.core.constant.enums.ProcessStatusEnum;
import com.yhwl.hz.common.core.constant.enums.ResourceTypeEnum;
import com.yhwl.hz.common.core.constant.enums.TaskStatusEnum;
import com.yhwl.hz.common.data.tenant.TenantContextHolder;
import lombok.AllArgsConstructor;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.repository.ProcessDefinitionQuery;
import org.activiti.engine.runtime.ProcessInstance;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author hz
 * @date 2018/9/25
 */
@Service
@AllArgsConstructor
public class ProcessServiceImpl implements ProcessService {

	private final RepositoryService repositoryService;

	private final RuntimeService runtimeService;

	private final LeaveBillMapper leaveBillMapper;

	/**
	 * ??????????????????
	 * @param params
	 * @return
	 */
	@Override
	public IPage<ProcessDefDTO> getProcessByPage(Map<String, Object> params) {
		ProcessDefinitionQuery query = repositoryService.createProcessDefinitionQuery()
				.processDefinitionTenantId(String.valueOf(TenantContextHolder.getTenantId())).latestVersion();

		String category = MapUtil.getStr(params, "category");
		if (StrUtil.isNotBlank(category)) {
			query.processDefinitionCategory(category);
		}

		int page = MapUtil.getInt(params, PaginationConstants.CURRENT);
		int limit = MapUtil.getInt(params, PaginationConstants.SIZE);

		IPage result = new Page(page, limit);
		result.setTotal(query.count());

		List<ProcessDefDTO> deploymentList = query.listPage((page - 1) * limit, limit).stream()
				.map(processDefinition -> {
					Deployment deployment = repositoryService.createDeploymentQuery()
							.deploymentId(processDefinition.getDeploymentId()).singleResult();
					return ProcessDefDTO.toProcessDefDTO(processDefinition, deployment);
				}).collect(Collectors.toList());
		result.setRecords(deploymentList);
		return result;
	}

	/**
	 * ??????xml/image??????
	 * @param procDefId
	 * @param proInsId
	 * @param resType
	 * @return
	 */
	@Override
	public InputStream readResource(String procDefId, String proInsId, String resType) {

		if (StrUtil.isBlank(procDefId)) {
			ProcessInstance processInstance = runtimeService.createProcessInstanceQuery().processInstanceId(proInsId)
					.singleResult();
			procDefId = processInstance.getProcessDefinitionId();
		}
		ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery()
				.processDefinitionId(procDefId).singleResult();

		String resourceName = "";
		if (ResourceTypeEnum.IMAGE.getType().equals(resType)) {
			resourceName = processDefinition.getDiagramResourceName();
		}
		else if (ResourceTypeEnum.XML.getType().equals(resType)) {
			resourceName = processDefinition.getResourceName();
		}

		InputStream resourceAsStream = repositoryService.getResourceAsStream(processDefinition.getDeploymentId(),
				resourceName);
		return resourceAsStream;
	}

	/**
	 * ????????????
	 * @param status
	 * @param procDefId
	 * @return
	 */
	@Override
	public Boolean updateStatus(String status, String procDefId) {
		if (ProcessStatusEnum.ACTIVE.getStatus().equals(status)) {
			repositoryService.activateProcessDefinitionById(procDefId, true, null);
		}
		else if (ProcessStatusEnum.SUSPEND.getStatus().equals(status)) {
			repositoryService.suspendProcessDefinitionById(procDefId, true, null);
		}
		return Boolean.TRUE;
	}

	/**
	 * ????????????????????????????????????????????????
	 * @param deploymentId
	 * @return
	 */
	@Override
	public Boolean removeProcIns(String deploymentId) {
		repositoryService.deleteDeployment(deploymentId, true);
		return Boolean.TRUE;
	}

	/**
	 * ????????????????????????????????????
	 * @param leaveId
	 * @return
	 */
	@Override
	@Transactional(rollbackFor = Exception.class)
	public Boolean saveStartProcess(Long leaveId) {
		LeaveBill leaveBill = leaveBillMapper.selectById(leaveId);
		leaveBill.setState(TaskStatusEnum.CHECK.getStatus());

		String key = leaveBill.getClass().getSimpleName();
		String businessKey = key + "_" + leaveBill.getLeaveId();
		runtimeService.startProcessInstanceByKeyAndTenantId(key, businessKey,
				String.valueOf(TenantContextHolder.getTenantId()));
		leaveBillMapper.updateById(leaveBill);
		return Boolean.TRUE;
	}

}
