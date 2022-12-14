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

package com.yhwl.hz.pay.handler.impl;

import cn.hutool.core.util.CharsetUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.extra.servlet.ServletUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.alipay.api.AlipayApiException;
import com.alipay.api.domain.AlipayTradeWapPayModel;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.ijpay.alipay.AliPayApi;
import com.ijpay.alipay.AliPayApiConfig;
import com.ijpay.alipay.AliPayApiConfigKit;
import com.yhwl.hz.common.data.tenant.TenantContextHolder;
import com.yhwl.hz.pay.entity.PayChannel;
import com.yhwl.hz.pay.entity.PayGoodsOrder;
import com.yhwl.hz.pay.entity.PayTradeOrder;
import com.yhwl.hz.pay.mapper.PayChannelMapper;
import com.yhwl.hz.pay.mapper.PayGoodsOrderMapper;
import com.yhwl.hz.pay.mapper.PayTradeOrderMapper;
import com.yhwl.hz.pay.utils.ChannelPayApiConfigKit;
import com.yhwl.hz.pay.utils.OrderStatusEnum;
import com.yhwl.hz.pay.utils.PayChannelNameEnum;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author hz
 * @date 2019-05-31
 * <p>
 * ?????????????????????
 */
@Slf4j
@Service("ALIPAY_WAP")
@AllArgsConstructor
public class AlipayWapPayOrderHandler extends AbstractPayOrderHandler {

	private final PayTradeOrderMapper tradeOrderMapper;

	private final PayGoodsOrderMapper goodsOrderMapper;

	private final PayChannelMapper channelMapper;

	private final HttpServletRequest request;

	private final HttpServletResponse response;

	/**
	 * ??????????????????
	 */
	@Override
	public PayChannel preparePayParams() {
		PayChannel channel = channelMapper.selectOne(
				Wrappers.<PayChannel>lambdaQuery().eq(PayChannel::getChannelId, PayChannelNameEnum.ALIPAY_WAP.name()));

		if (channel == null) {
			throw new IllegalArgumentException("???????????????????????????????????????");
		}

		JSONObject params = JSONUtil.parseObj(channel.getParam());
		AliPayApiConfig aliPayApiConfig = AliPayApiConfig.builder().setAppId(channel.getAppId())
				.setPrivateKey(params.getStr("privateKey")).setCharset(CharsetUtil.UTF_8)
				.setAliPayPublicKey(params.getStr("publicKey")).setServiceUrl(params.getStr("serviceUrl"))
				.setSignType("RSA2").build();
		AliPayApiConfigKit.setThreadLocalAliPayApiConfig(aliPayApiConfig);
		return channel;
	}

	/**
	 * ??????????????????
	 * @param goodsOrder
	 * @return
	 */
	@Override
	public PayTradeOrder createTradeOrder(PayGoodsOrder goodsOrder) {
		PayTradeOrder tradeOrder = new PayTradeOrder();
		tradeOrder.setOrderId(goodsOrder.getPayOrderId());
		tradeOrder.setAmount(goodsOrder.getAmount());
		tradeOrder.setChannelId(PayChannelNameEnum.ALIPAY_WAP.getName());
		tradeOrder.setChannelMchId(AliPayApiConfigKit.getAliPayApiConfig().getAppId());
		tradeOrder.setClientIp(ServletUtil.getClientIP(request));
		tradeOrder.setCurrency("cny");
		tradeOrder.setExpireTime(30L);
		tradeOrder.setStatus(OrderStatusEnum.INIT.getStatus());
		tradeOrder.setBody(goodsOrder.getGoodsName());
		tradeOrderMapper.insert(tradeOrder);
		return tradeOrder;
	}

	/**
	 * ??????????????????
	 * @param goodsOrder ????????????
	 * @param tradeOrder ????????????
	 */
	@Override
	public PayTradeOrder pay(PayGoodsOrder goodsOrder, PayTradeOrder tradeOrder) {
		AlipayTradeWapPayModel model = new AlipayTradeWapPayModel();
		model.setBody(tradeOrder.getBody());
		model.setSubject(tradeOrder.getBody());
		model.setOutTradeNo(tradeOrder.getOrderId());
		model.setTimeoutExpress("30m");

		// ???????????? ??????????????????
		model.setTotalAmount(NumberUtil.div(tradeOrder.getAmount(), "100", 2).toString());
		model.setProductCode(goodsOrder.getGoodsId());
		model.setPassbackParams(String.valueOf(TenantContextHolder.getTenantId()));
		try {
			log.info("???????????????wap ???????????? {}", model);
			AliPayApi.wapPay(response, model, ChannelPayApiConfigKit.get().getReturnUrl(),
					ChannelPayApiConfigKit.get().getNotifyUrl() + "/pay/notify/ali/callbak");
		}
		catch (AlipayApiException e) {
			log.error("???????????????????????????", e);
			tradeOrder.setErrMsg(e.getErrMsg());
			tradeOrder.setErrCode(e.getErrCode());
			tradeOrder.setStatus(OrderStatusEnum.FAIL.getStatus());
			goodsOrder.setStatus(OrderStatusEnum.FAIL.getStatus());
		}
		catch (IOException e) {
			log.error("???????????????????????????", e);
			tradeOrder.setErrMsg(e.getMessage());
			tradeOrder.setStatus(OrderStatusEnum.FAIL.getStatus());
			goodsOrder.setStatus(OrderStatusEnum.FAIL.getStatus());
		}
		return tradeOrder;
	}

	/**
	 * ??????????????????
	 * @param goodsOrder ????????????
	 * @param tradeOrder ????????????
	 */
	@Override
	public void updateOrder(PayGoodsOrder goodsOrder, PayTradeOrder tradeOrder) {
		tradeOrderMapper.updateById(tradeOrder);
		goodsOrderMapper.updateById(goodsOrder);
	}

}
