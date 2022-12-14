/*
 *
 *      Copyright (c) 2018-2025, hz All rights reserved.
 *
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions are met:
 *
 * Redistributions of source code must retain the above copyright notice,
 *  this list of conditions and the following disclaimer.
 *  Redistributions in binary form must reproduce the above copyright
 *  notice, this list of conditions and the following disclaimer in the
 *  documentation and/or other materials provided with the distribution.
 *  Neither the name of the pig4cloud.com developer nor the names of its
 *  contributors may be used to endorse or promote products derived from
 *  this software without specific prior written permission.
 *  Author: hz
 *
 */

package com.yhwl.hz.admin.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yhwl.hz.admin.api.dto.UserDTO;
import com.yhwl.hz.admin.api.entity.SysUser;
import com.yhwl.hz.admin.api.vo.UserExcelVO;
import com.yhwl.hz.admin.service.SysUserService;
import com.yhwl.hz.common.core.exception.ErrorCodes;
import com.yhwl.hz.common.core.util.MsgUtils;
import com.yhwl.hz.common.core.util.R;
import com.yhwl.hz.common.excel.annotation.RequestExcel;
import com.yhwl.hz.common.excel.annotation.ResponseExcel;
import com.yhwl.hz.common.log.annotation.SysLog;
import com.yhwl.hz.common.security.annotation.Inner;
import com.yhwl.hz.common.security.util.SecurityUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * @author hz
 * @date 2018/12/16
 */
@RestController
@AllArgsConstructor
@RequestMapping("/user")
@Api(value = "user", tags = "??????????????????")
public class SysUserController {

	private final SysUserService userService;

	/**
	 * ??????????????????????????????
	 * @return ????????????
	 */
	@Inner
	@GetMapping("/info/{username}")
	public R info(@PathVariable String username) {
		SysUser user = userService.getOne(Wrappers.<SysUser>query().lambda().eq(SysUser::getUsername, username));
		if (user == null) {
			return R.failed(MsgUtils.getMessage(ErrorCodes.SYS_USER_USERINFO_EMPTY, username));
		}
		return R.ok(userService.findUserInfo(user));
	}

	/**
	 * ??????????????????????????????
	 * @return ????????????
	 */
	@GetMapping(value = { "/info" })
	public R info() {
		String username = SecurityUtils.getUser().getUsername();
		SysUser user = userService.getOne(Wrappers.<SysUser>query().lambda().eq(SysUser::getUsername, username));
		if (user == null) {
			return R.failed(MsgUtils.getMessage(ErrorCodes.SYS_USER_QUERY_ERROR));
		}
		return R.ok(userService.findUserInfo(user));
	}

	/**
	 * ??????ID??????????????????
	 * @param id ID
	 * @return ????????????
	 */
	@GetMapping("/{id}")
	public R user(@PathVariable Long id) {
		return R.ok(userService.selectUserVoById(id));
	}

	/**
	 * ?????????????????????????????????
	 * @param username ?????????
	 * @return
	 */
	@GetMapping("/details/{username}")
	public R user(@PathVariable String username) {
		SysUser condition = new SysUser();
		condition.setUsername(username);
		return R.ok(userService.getOne(new QueryWrapper<>(condition)));
	}

	/**
	 * ??????????????????
	 * @param id ID
	 * @return R
	 */
	@SysLog("??????????????????")
	@DeleteMapping("/{id}")
	@PreAuthorize("@pms.hasPermission('sys_user_del')")
	@ApiOperation(value = "????????????", notes = "??????ID????????????")
	@ApiImplicitParam(name = "id", value = "??????ID", required = true, dataType = "int", paramType = "path")
	public R userDel(@PathVariable Long id) {
		SysUser sysUser = userService.getById(id);
		return R.ok(userService.deleteUserById(sysUser));
	}

	/**
	 * ????????????
	 * @param userDto ????????????
	 * @return success/false
	 */
	@SysLog("????????????")
	@PostMapping
	@PreAuthorize("@pms.hasPermission('sys_user_add')")
	public R user(@RequestBody UserDTO userDto) {
		return R.ok(userService.saveUser(userDto));
	}

	/**
	 * ??????????????????
	 * @param userDto ????????????
	 * @return R
	 */
	@SysLog("??????????????????")
	@PutMapping
	@PreAuthorize("@pms.hasPermission('sys_user_edit')")
	public R updateUser(@Valid @RequestBody UserDTO userDto) {
		return R.ok(userService.updateUser(userDto));
	}

	/**
	 * ??????????????????
	 * @param page ?????????
	 * @param userDTO ??????????????????
	 * @return ????????????
	 */
	@GetMapping("/page")
	public R getUserPage(Page page, UserDTO userDTO) {
		return R.ok(userService.getUsersWithRolePage(page, userDTO));
	}

	/**
	 * ??????????????????
	 * @param userDto userDto
	 * @return success/false
	 */
	@SysLog("??????????????????")
	@PutMapping("/edit")
	public R updateUserInfo(@Valid @RequestBody UserDTO userDto) {
		return userService.updateUserInfo(userDto);
	}

	/**
	 * @param username ????????????
	 * @return ????????????????????????
	 */
	@GetMapping("/ancestor/{username}")
	public R listAncestorUsers(@PathVariable String username) {
		return R.ok(userService.listAncestorUsers(username));
	}

	/**
	 * ??????excel ??????
	 * @param userDTO ????????????
	 * @return
	 */
	@ResponseExcel
	@GetMapping("/export")
	@PreAuthorize("@pms.hasPermission('sys_user_export')")
	public List export(UserDTO userDTO) {
		return userService.listUser(userDTO);
	}

	/**
	 * ????????????
	 * @param excelVOList ????????????
	 * @param bindingResult ??????????????????
	 * @return R
	 */
	@PostMapping("/import")
	@PreAuthorize("@pms.hasPermission('sys_user_export')")
	public R importUser(@RequestExcel List<UserExcelVO> excelVOList, BindingResult bindingResult) {
		return userService.importUser(excelVOList, bindingResult);
	}

	/**
	 * ??????????????????
	 * @param username ?????????
	 * @return R
	 */
	@Inner
	@PutMapping("/lock/{username}")
	public R lockUser(@PathVariable String username) {
		return userService.lockUser(username);
	}

}
