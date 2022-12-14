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
package com.yhwl.hz.mp.service.impl;

import cn.binarywang.tools.generator.ChineseNameGenerator;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.BooleanUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yhwl.hz.common.data.tenant.TenantBroker;
import com.yhwl.hz.mp.config.WxMpInitConfigRunner;
import com.yhwl.hz.mp.entity.WxAccount;
import com.yhwl.hz.mp.entity.WxAccountFans;
import com.yhwl.hz.mp.entity.WxAccountTag;
import com.yhwl.hz.mp.entity.vo.WxAccountFansVo;
import com.yhwl.hz.mp.mapper.WxAccountFansMapper;
import com.yhwl.hz.mp.mapper.WxAccountMapper;
import com.yhwl.hz.mp.mapper.WxAccountTagMapper;
import com.yhwl.hz.mp.service.WxAccountFansService;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.api.WxMpUserService;
import me.chanjar.weixin.mp.api.WxMpUserTagService;
import me.chanjar.weixin.mp.bean.result.WxMpUser;
import me.chanjar.weixin.mp.bean.result.WxMpUserList;
import org.springframework.beans.BeanUtils;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author hz
 * @date 2019-03-26 22:08:08
 * <p>
 * ?????????????????????
 */
@Slf4j
@Service
@AllArgsConstructor
public class WxAccountFansServiceImpl extends ServiceImpl<WxAccountFansMapper, WxAccountFans>
		implements WxAccountFansService {

	private static final int SIZE = 100;

	private final WxAccountTagMapper wxAccountTagMapper;

	private final WxAccountMapper wxAccountMapper;

	/**
	 * ??????????????????
	 * @param page ??????
	 * @param wxAccountFans ????????????
	 * @return
	 */
	@Override
	public IPage getFansWithTagPage(Page page, WxAccountFans wxAccountFans) {
		// ??????????????????????????????
		List<WxAccountTag> wxAccountTags = wxAccountTagMapper.selectList(Wrappers.emptyWrapper());

		// ??????????????????
		IPage<WxAccountFans> fansPage = baseMapper.selectPage(page, Wrappers.query(wxAccountFans));
		List<WxAccountFansVo> voList = fansPage.getRecords().stream().map(fans -> {//
			WxAccountFansVo vo = new WxAccountFansVo();
			BeanUtils.copyProperties(fans, vo);
			// ?????? TAG_NAME
			if (ArrayUtil.isNotEmpty(fans.getTagIds())) {
				List<WxAccountTag> tagList = wxAccountTags.stream()
						.filter(tag -> tag.getWxAccountAppid().endsWith(fans.getWxAccountAppid()) //
								&& ArrayUtil.contains(fans.getTagIds(), tag.getTagId()))
						.collect(Collectors.toList());
				vo.setTagList(tagList);
			}
			return vo;
		}).collect(Collectors.toList());

		page.setRecords(voList);
		page.setTotal(fansPage.getTotal());
		return page;
	}

	/**
	 * ??????????????????
	 * @param wxAccountFans ??????
	 * @return
	 */
	@Override
	@SneakyThrows
	@Transactional(rollbackFor = Exception.class)
	public Boolean updateFans(WxAccountFans wxAccountFans) {
		baseMapper.updateById(wxAccountFans);

		// ???????????????????????????
		WxMpService wxMpService = WxMpInitConfigRunner.getMpServices().get(wxAccountFans.getWxAccountAppid());

		if (StrUtil.isNotBlank(wxAccountFans.getRemark())) {
			WxMpUserService wxMpUserService = wxMpService.getUserService();
			wxMpUserService.userUpdateRemark(wxAccountFans.getOpenid(), wxAccountFans.getRemark());

		}

		// ??????????????????
		WxMpUserTagService userTagService = wxMpService.getUserTagService();
		if (ArrayUtil.isNotEmpty(wxAccountFans.getTagIds())) {
			// ??????????????????
			List<Long> oldTag = userTagService.userTagList(wxAccountFans.getOpenid());
			for (Long tagId : oldTag) {
				userTagService.batchUntagging(tagId, new String[] { wxAccountFans.getOpenid() });
			}

			// ???????????????
			for (Long tagId : wxAccountFans.getTagIds()) {

				userTagService.batchTagging(tagId, new String[] { wxAccountFans.getOpenid() });
			}
		}
		return Boolean.TRUE;
	}

	/**
	 * ??????????????????????????????????????????
	 * @param appId
	 * @return
	 */
	@Async
	@Override
	public Boolean syncAccountFans(String appId) {
		WxAccount wxAccount = wxAccountMapper
				.selectOne(Wrappers.<WxAccount>query().lambda().eq(WxAccount::getAppid, appId));

		// ???????????????????????????
		WxMpService wxMpService = WxMpInitConfigRunner.getMpServices().get(appId);
		WxMpUserService wxMpUserService = wxMpService.getUserService();
		// ????????????????????????????????????openid ????????????????????? (?????????????????????????????????)
		String finalNextOpenId = queryNextOpenId(appId);
		TenantBroker.runAs(() -> WxMpInitConfigRunner.getTenants().get(appId),
				(id) -> fetchUser(finalNextOpenId, wxAccount, wxMpUserService));

		log.info("????????? {} ??????????????????", wxAccount.getName());
		return Boolean.TRUE;
	}

	/**
	 * ??????????????????
	 * @param nextOpenid ??????????????????openid
	 * @param wxAccount ???????????????
	 * @param wxMpUserService mp?????????
	 */
	private void fetchUser(String nextOpenid, WxAccount wxAccount, WxMpUserService wxMpUserService) {
		try {
			WxMpUserList wxMpUserList = wxMpUserService.userList(nextOpenid);
			// openId ?????? ?????? 100??? openid
			List<List<String>> openIdsList = CollUtil.split(wxMpUserList.getOpenids(), SIZE).stream()
					.filter(CollUtil::isNotEmpty).collect(Collectors.toList());
			// ??????????????????. ????????????????????????
			for (List<String> openIdList : openIdsList) {
				log.info("?????????????????????????????? {}", openIdList);
				List<WxAccountFans> wxAccountFansList = new ArrayList<>();
				wxMpUserService.userInfoList(openIdList).forEach(wxMpUser -> {
					WxAccountFans wxAccountFans = buildDbUser(wxAccount, wxMpUser);
					wxAccountFansList.add(wxAccountFans);
				});
				this.saveOrUpdateBatch(wxAccountFansList);
				log.info("?????????????????????????????? {}", openIdList);
			}
			// ??????nextOpenId ???????????????????????????
			if (StrUtil.isNotBlank(wxMpUserList.getNextOpenid())) {
				fetchUser(wxMpUserList.getNextOpenid(), wxAccount, wxMpUserService);
			}
		}
		catch (Exception e) {
			log.warn("????????????????????? {} ???????????? {}", wxAccount.getName(), e.getMessage());
			// ??????????????????
			String finalNextOpenId = queryNextOpenId(wxAccount.getAppid());
			fetchUser(finalNextOpenId, wxAccount, wxMpUserService);
		}

	}

	/**
	 * ???????????????????????????
	 * @param wxAccount ???????????????
	 * @param wxMpUser ??????????????????
	 */
	private WxAccountFans buildDbUser(WxAccount wxAccount, WxMpUser wxMpUser) {
		WxAccountFans wxAccountFans = new WxAccountFans();
		wxAccountFans.setOpenid(wxMpUser.getOpenId());
		wxAccountFans.setSubscribeStatus(String.valueOf(BooleanUtil.toInt(wxMpUser.getSubscribe())));

		// 2020-11-25 ??????????????????????????????????????????????????????
		if (ObjectUtil.isNotEmpty(wxMpUser.getSubscribeTime())) {
			wxAccountFans.setSubscribeTime(LocalDateTime
					.ofInstant(Instant.ofEpochMilli(wxMpUser.getSubscribeTime() * 1000L), ZoneId.systemDefault()));
		}

		// ???????????????????????????????????????????????????
		String generatedName = ChineseNameGenerator.getInstance().generate();
		wxAccountFans.setNickname(generatedName);
		wxAccountFans.setLanguage(wxMpUser.getLanguage());
		wxAccountFans.setHeadimgUrl(wxMpUser.getHeadImgUrl());
		wxAccountFans.setRemark(wxMpUser.getRemark());
		wxAccountFans.setTagIds(wxAccountFans.getTagIds());
		wxAccountFans.setWxAccountId(wxAccount.getId());
		wxAccountFans.setWxAccountAppid(wxAccount.getAppid());
		wxAccountFans.setWxAccountName(wxAccount.getName());
		return wxAccountFans;
	}

	/**
	 * ????????????????????????????????????openId
	 * @param appId ???????????????
	 * @return openid / null
	 */
	private String queryNextOpenId(String appId) {
		Page<WxAccountFans> queryPage = new Page<>(0, 1);
		Page<WxAccountFans> fansPage = baseMapper.selectPage(queryPage, Wrappers.<WxAccountFans>query().lambda()
				.eq(WxAccountFans::getWxAccountAppid, appId).orderByDesc(WxAccountFans::getCreateTime));
		String nextOpenId = null;
		if (fansPage.getTotal() > 0) {
			nextOpenId = fansPage.getRecords().get(0).getOpenid();
		}
		return nextOpenId;
	}

}
