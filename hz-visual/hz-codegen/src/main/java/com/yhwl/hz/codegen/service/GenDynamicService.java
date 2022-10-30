package com.yhwl.hz.codegen.service;

import com.yhwl.hz.codegen.entity.GenConfig;
import com.yhwl.hz.codegen.util.SqlDto;

import java.util.LinkedHashMap;
import java.util.List;

/**
 * @author hz
 * @date 2022/7/9
 */
public interface GenDynamicService {

	/**
	 * 执行代码
	 * @param genConfig 请求参数
	 * @return
	 */
	Object run(GenConfig genConfig);

	/**
	 * 动态查询
	 * @param sqlDto 请求参数
	 * @return ListMap
	 */
	List<LinkedHashMap<String, Object>> dynamicQuery(SqlDto sqlDto);

}
