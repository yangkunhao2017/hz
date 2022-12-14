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

package com.yhwl.hz.admin.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.tree.Tree;
import cn.hutool.core.lang.tree.TreeNode;
import cn.hutool.core.lang.tree.TreeUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yhwl.hz.admin.api.entity.SysMenu;
import com.yhwl.hz.admin.api.entity.SysRoleMenu;
import com.yhwl.hz.admin.mapper.SysMenuMapper;
import com.yhwl.hz.admin.mapper.SysRoleMenuMapper;
import com.yhwl.hz.admin.service.SysMenuService;
import com.yhwl.hz.common.core.constant.CacheConstants;
import com.yhwl.hz.common.core.constant.CommonConstants;
import com.yhwl.hz.common.core.constant.enums.MenuTypeEnum;
import com.yhwl.hz.common.core.exception.ErrorCodes;
import com.yhwl.hz.common.core.util.MsgUtils;
import com.yhwl.hz.common.core.util.R;
import lombok.AllArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.constraints.NotNull;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * <p>
 * ??????????????? ???????????????
 * </p>
 *
 * @author hz
 * @since 2017-10-29
 */
@Service
@AllArgsConstructor
public class SysMenuServiceImpl extends ServiceImpl<SysMenuMapper, SysMenu> implements SysMenuService {

	private final SysRoleMenuMapper sysRoleMenuMapper;

	@Override
	@Cacheable(value = CacheConstants.MENU_DETAILS, key = "#roleId", unless = "#result.isEmpty()")
	public List<SysMenu> findMenuByRoleId(Long roleId) {
		return baseMapper.listMenusByRoleId(roleId);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	@CacheEvict(value = CacheConstants.MENU_DETAILS, allEntries = true)
	public R removeMenuById(Long id) {
		// ???????????????????????????????????????
		List<SysMenu> menuList = this.list(Wrappers.<SysMenu>query().lambda().eq(SysMenu::getParentId, id));
		if (CollUtil.isNotEmpty(menuList)) {
			return R.failed(MsgUtils.getMessage(ErrorCodes.SYS_MENU_DELETE_EXISTING));
		}

		sysRoleMenuMapper.delete(Wrappers.<SysRoleMenu>query().lambda().eq(SysRoleMenu::getMenuId, id));
		// ?????????????????????????????????
		return R.ok(this.removeById(id));
	}

	@Override
	@CacheEvict(value = CacheConstants.MENU_DETAILS, allEntries = true)
	public Boolean updateMenuById(SysMenu sysMenu) {
		return this.updateById(sysMenu);
	}

	/**
	 * ??????????????? 1. ???????????????????????????????????? 2. ?????????????????????parentId ?????? 2.1 ???????????????????????????ID -1
	 * @param parentId ?????????ID
	 * @param menuName ????????????
	 * @return
	 */
	@Override
	public List<Tree<Long>> treeMenu(Long parentId, String menuName) {
		Long parent = parentId == null ? CommonConstants.MENU_TREE_ROOT_ID : parentId;

		List<TreeNode<Long>> collect = baseMapper
				.selectList(
						Wrappers.<SysMenu>lambdaQuery().like(StrUtil.isNotBlank(menuName), SysMenu::getName, menuName)
								.orderByAsc(SysMenu::getSortOrder))
				.stream().map(getNodeFunction()).collect(Collectors.toList());

		// ???????????? ?????????????????? ???????????? ??????????????????
		if (StrUtil.isNotBlank(menuName)) {
			return collect.stream().map(node -> {
				Tree<Long> tree = new Tree<>();
				tree.putAll(node.getExtra());
				BeanUtils.copyProperties(node, tree);
				return tree;
			}).collect(Collectors.toList());
		}

		return TreeUtil.build(collect, parent);
	}

	/**
	 * ????????????
	 * @param all ????????????
	 * @param type ??????
	 * @param parentId ?????????ID
	 * @return
	 */
	@Override
	public List<Tree<Long>> filterMenu(Set<SysMenu> all, String type, Long parentId) {
		List<TreeNode<Long>> collect = all.stream().filter(menuTypePredicate(type)).map(getNodeFunction())
				.collect(Collectors.toList());
		Long parent = parentId == null ? CommonConstants.MENU_TREE_ROOT_ID : parentId;
		return TreeUtil.build(collect, parent);
	}

	@NotNull
	private Function<SysMenu, TreeNode<Long>> getNodeFunction() {
		return menu -> {
			TreeNode<Long> node = new TreeNode<>();
			node.setId(menu.getMenuId());
			node.setName(menu.getName());
			node.setParentId(menu.getParentId());
			node.setWeight(menu.getSortOrder());
			// ????????????
			Map<String, Object> extra = new HashMap<>();
			extra.put("icon", menu.getIcon());
			extra.put("path", menu.getPath());
			extra.put("menuType", menu.getMenuType());
			extra.put("permission", menu.getPermission());
			extra.put("label", menu.getName());
			extra.put("sortOrder", menu.getSortOrder());
			extra.put("keepAlive", menu.getKeepAlive());
			node.setExtra(extra);
			return node;
		};
	}

	/**
	 * menu ????????????
	 * @param type ??????
	 * @return Predicate
	 */
	private Predicate<SysMenu> menuTypePredicate(String type) {
		return vo -> {
			if (MenuTypeEnum.TOP_MENU.getDescription().equals(type)) {
				return MenuTypeEnum.TOP_MENU.getType().equals(vo.getMenuType());
			}
			// ???????????? ?????? + ??????
			return !MenuTypeEnum.BUTTON.getType().equals(vo.getMenuType());
		};
	}

}
