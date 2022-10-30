package com.yhwl.hz.admin.controller;

import com.yhwl.hz.admin.api.entity.SysDept;
import com.yhwl.hz.admin.service.ConnectService;
import com.yhwl.hz.admin.service.SysDeptService;
import com.yhwl.hz.common.core.util.R;
import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 钉钉、微信 互联
 *
 * @author hz
 * @date 2022/4/22
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/connect")
@Api(value = "connect", tags = "开放互联")
public class ConnectController {

	private final ConnectService connectService;

	private final SysDeptService deptService;

	/**
	 * 同步钉钉用户
	 * @return
	 */
	@PostMapping("/sync/ding/user")
	@PreAuthorize("@pms.hasPermission('sys_connect_sync')")
	public R syncUser() {
		for (SysDept sysDept : deptService.list()) {
			connectService.syncDingUser(sysDept.getDeptId());
		}
		return R.ok();
	}

	/**
	 * 同步钉钉部门
	 * @return
	 */
	@PostMapping("/sync/ding/dept")
	@PreAuthorize("@pms.hasPermission('sys_connect_sync')")
	public R syncDept() {
		return R.ok(connectService.syncDingDept());
	}

	/**
	 * 同步企微用户
	 * @return
	 */
	@PostMapping("/sync/cp/user")
	@PreAuthorize("@pms.hasPermission('sys_connect_sync')")
	public R syncCpUser() {
		return connectService.syncCpUser();
	}

	/**
	 * 同步企微部门
	 * @return
	 */
	@PostMapping("/sync/cp/dept")
	@PreAuthorize("@pms.hasPermission('sys_connect_sync')")
	public R syncCpDept() {
		return connectService.syncCpDept();
	}

}
