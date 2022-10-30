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

package com.yhwl.hz.codegen.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yhwl.hz.codegen.entity.ColumnEntity;
import com.yhwl.hz.codegen.entity.GenConfig;

/**
 * 表字段管理
 *
 * @author hz
 * @date 2020-05-18
 */
public interface GenTableColumnService {

	/**
	 * 查询表的字段信息
	 * @param page
	 * @param genConfig 查询条件
	 * @return
	 */
	IPage<ColumnEntity> listTable(Page page, GenConfig genConfig);

}
