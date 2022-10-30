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

package com.yhwl.hz.act.listener;

import cn.hutool.core.collection.CollUtil;
import com.yhwl.hz.admin.api.entity.SysUser;
import com.yhwl.hz.admin.api.feign.RemoteUserService;
import com.yhwl.hz.common.core.util.R;
import com.yhwl.hz.common.core.util.SpringContextHolder;
import com.yhwl.hz.common.security.util.SecurityUtils;
import com.yhwl.hz.common.websocket.distribute.MessageDO;
import com.yhwl.hz.common.websocket.distribute.RedisMessageDistributor;
import lombok.extern.slf4j.Slf4j;
import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.TaskListener;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author hz
 * @date 2018/9/27 请假流程监听器
 */
@Slf4j
public class LeaveProcessTaskListener implements TaskListener {

	/**
	 * 查询提交人的上级
	 * @param delegateTask
	 */
	@Override
	public void notify(DelegateTask delegateTask) {
		RedisMessageDistributor messageDistributor = SpringContextHolder.getBean(RedisMessageDistributor.class);
		RemoteUserService userService = SpringContextHolder.getBean(RemoteUserService.class);

		R<List<SysUser>> result = userService.ancestorUsers(SecurityUtils.getUser().getUsername());
		List<Object> remindUserList = new ArrayList<>();

		if (CollUtil.isEmpty(result.getData())) {
			log.info("用户 {} 不存在上级,任务单由当前用户审批", SecurityUtils.getUser().getUsername());
			delegateTask.addCandidateUser(SecurityUtils.getUser().getUsername());
			remindUserList.add(SecurityUtils.getUser().getId());
		}
		else {
			List<String> userList = result.getData().stream().map(SysUser::getUsername).collect(Collectors.toList());
			List<Long> userIdList = result.getData().stream().map(SysUser::getUserId).collect(Collectors.toList());
			log.info("当前任务 {}，由 {}处理", delegateTask.getId(), userList);
			delegateTask.addCandidateUsers(userList);
			remindUserList.addAll(userIdList);
		}

		// websocket 发送消息
		MessageDO messageDO = new MessageDO();
		messageDO.setNeedBroadcast(Boolean.FALSE);
		messageDO.setSessionKeys(remindUserList);
		messageDO.setMessageText(String.format("协同办公 %s 的任务需要您处理", delegateTask.getName()));
		messageDistributor.distribute(messageDO);
	}

}
