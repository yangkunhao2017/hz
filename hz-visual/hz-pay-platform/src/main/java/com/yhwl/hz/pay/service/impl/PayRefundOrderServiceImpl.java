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
package com.yhwl.hz.pay.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yhwl.hz.pay.entity.PayRefundOrder;
import com.yhwl.hz.pay.mapper.PayRefundOrderMapper;
import com.yhwl.hz.pay.service.PayRefundOrderService;
import org.springframework.stereotype.Service;

/**
 * 退款
 *
 * @author hz
 * @date 2019-05-28 23:58:11
 */
@Service
public class PayRefundOrderServiceImpl extends ServiceImpl<PayRefundOrderMapper, PayRefundOrder>
		implements PayRefundOrderService {

}
