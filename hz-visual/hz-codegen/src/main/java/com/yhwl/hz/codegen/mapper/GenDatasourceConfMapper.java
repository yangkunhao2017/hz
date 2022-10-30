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
package com.yhwl.hz.codegen.mapper;

import com.yhwl.hz.codegen.entity.GenDatasourceConf;
import com.yhwl.hz.common.data.datascope.HzBaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 数据源表
 *
 * @author hz
 * @date 2019-03-31 16:00:20
 */
@Mapper
public interface GenDatasourceConfMapper extends HzBaseMapper<GenDatasourceConf> {

}
