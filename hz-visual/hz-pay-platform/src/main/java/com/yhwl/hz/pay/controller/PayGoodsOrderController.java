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

package com.yhwl.hz.pay.controller;

import cn.hutool.core.util.URLUtil;
import cn.hutool.http.ContentType;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yhwl.hz.common.core.util.R;
import com.yhwl.hz.common.data.tenant.TenantContextHolder;
import com.yhwl.hz.common.log.annotation.SysLog;
import com.yhwl.hz.common.security.annotation.Inner;
import com.yhwl.hz.pay.entity.PayChannel;
import com.yhwl.hz.pay.entity.PayGoodsOrder;
import com.yhwl.hz.pay.service.PayChannelService;
import com.yhwl.hz.pay.service.PayGoodsOrderService;
import com.yhwl.hz.pay.utils.PayChannelNameEnum;
import com.yhwl.hz.pay.utils.PayConstants;
import io.swagger.annotations.Api;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.bean.oauth2.WxOAuth2AccessToken;
import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.api.impl.WxMpServiceImpl;
import me.chanjar.weixin.mp.config.impl.WxMpDefaultConfigImpl;
import org.springframework.http.HttpHeaders;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * ??????
 *
 * @author hz
 * @date 2019-05-28 23:58:27
 */
@Slf4j
@Controller
@AllArgsConstructor
@RequestMapping("/goods")
@Api(value = "goods", tags = "??????????????????")
public class PayGoodsOrderController {

	private final PayGoodsOrderService payGoodsOrderService;

	private final PayChannelService channelService;

	private final ObjectMapper objectMapper;

	/**
	 * ????????????
	 * @param goods ??????
	 * @param response
	 *
	 * AliPayApiConfigKit.setAppId WxPayApiConfigKit.setAppId shezhi
	 *
	 */
	@SneakyThrows
	@Inner(false)
	@GetMapping("/buy")
	@SysLog("????????????")
	public void buy(PayGoodsOrder goods, HttpServletRequest request, HttpServletResponse response) {
		String ua = request.getHeader(HttpHeaders.USER_AGENT);
		log.info("?????????????????? UA:{}", ua);

		if (ua.contains(PayConstants.MICRO_MESSENGER)) {
			PayChannel channel = channelService.getOne(
					Wrappers.<PayChannel>lambdaQuery().eq(PayChannel::getChannelId, PayChannelNameEnum.WEIXIN_MP),
					false);

			if (channel == null) {
				throw new IllegalArgumentException("??????????????????????????????");
			}

			String wxUrl = "https://open.weixin.qq.com/connect/oauth2/authorize?appid=%s"
					+ "&redirect_uri=%s&response_type=code&scope=snsapi_base&state=%s";

			String redirectUri = String.format("%s/pay/goods/wx?amount=%s&TENANT-ID=%s", channel.getNotifyUrl(),
					goods.getAmount(), TenantContextHolder.getTenantId());

			response.sendRedirect(
					String.format(wxUrl, channel.getAppId(), URLUtil.encode(redirectUri), channel.getAppId()));
		}

		if (ua.contains(PayConstants.ALIPAY)) {
			payGoodsOrderService.buy(goods, false);
		}

	}

	@SneakyThrows
	@Inner(false)
	@GetMapping("/merge/buy")
	@SysLog("????????????????????????")
	public void mergeBuy(PayGoodsOrder goods, HttpServletResponse response) {
		Map<String, Object> result = payGoodsOrderService.buy(goods, true);
		response.setContentType(ContentType.JSON.getValue());
		response.getWriter().print(objectMapper.writeValueAsString(result));
	}

	/**
	 * oauth
	 * @param goods ????????????
	 * @param code ??????code
	 * @param modelAndView
	 * @return
	 * @throws WxErrorException
	 */
	@Inner(false)
	@SneakyThrows
	@GetMapping("/wx")
	public ModelAndView wx(PayGoodsOrder goods, String code, ModelAndView modelAndView) {
		PayChannel channel = channelService.getOne(
				Wrappers.<PayChannel>lambdaQuery().eq(PayChannel::getChannelId, PayChannelNameEnum.WEIXIN_MP), false);

		if (channel == null) {
			throw new IllegalArgumentException("??????????????????????????????");
		}

		JSONObject params = JSONUtil.parseObj(channel.getParam());
		WxMpService wxMpService = new WxMpServiceImpl();
		WxMpDefaultConfigImpl storage = new WxMpDefaultConfigImpl();
		storage.setAppId(channel.getAppId());
		storage.setSecret(params.getStr("secret"));
		wxMpService.setWxMpConfigStorage(storage);

		WxOAuth2AccessToken accessToken = wxMpService.getOAuth2Service().getAccessToken(code);
		goods.setUserId(accessToken.getOpenId());
		goods.setAmount(goods.getAmount());
		modelAndView.setViewName("pay");
		modelAndView.addAllObjects(payGoodsOrderService.buy(goods, false));
		return modelAndView;
	}

	/**
	 * ????????????
	 * @param page ????????????
	 * @param payGoodsOrder ???????????????
	 * @return
	 */
	@ResponseBody
	@GetMapping("/page")
	public R getPayGoodsOrderPage(Page page, PayGoodsOrder payGoodsOrder) {
		return R.ok(payGoodsOrderService.page(page, Wrappers.query(payGoodsOrder)));
	}

	/**
	 * ??????id?????????????????????
	 * @param goodsOrderId id
	 * @return R
	 */
	@ResponseBody
	@GetMapping(value = "/{goodsOrderId}")
	public R getById(@PathVariable("goodsOrderId") Integer goodsOrderId) {
		return R.ok(payGoodsOrderService.getById(goodsOrderId));
	}

	/**
	 * ?????????????????????
	 * @param payGoodsOrder ???????????????
	 * @return R
	 */
	@SysLog("?????????????????????")
	@PostMapping
	@ResponseBody
	@PreAuthorize("@pms.hasPermission('generator_paygoodsorder_add')")
	public R save(@RequestBody PayGoodsOrder payGoodsOrder) {
		return R.ok(payGoodsOrderService.save(payGoodsOrder));
	}

	/**
	 * ?????????????????????
	 * @param payGoodsOrder ???????????????
	 * @return R
	 */
	@SysLog("?????????????????????")
	@PutMapping
	@ResponseBody
	@PreAuthorize("@pms.hasPermission('generator_paygoodsorder_edit')")
	public R updateById(@RequestBody PayGoodsOrder payGoodsOrder) {
		return R.ok(payGoodsOrderService.updateById(payGoodsOrder));
	}

	/**
	 * ??????id?????????????????????
	 * @param goodsOrderId id
	 * @return R
	 */
	@SysLog("?????????????????????")
	@ResponseBody
	@DeleteMapping("/{goodsOrderId}")
	@PreAuthorize("@pms.hasPermission('generator_paygoodsorder_del')")
	public R removeById(@PathVariable Integer goodsOrderId) {
		return R.ok(payGoodsOrderService.removeById(goodsOrderId));
	}

}
