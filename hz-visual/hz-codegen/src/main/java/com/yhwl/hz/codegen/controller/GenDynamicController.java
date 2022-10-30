package com.yhwl.hz.codegen.controller;

import com.yhwl.hz.codegen.entity.GenConfig;
import com.yhwl.hz.codegen.service.GenDynamicService;
import com.yhwl.hz.codegen.util.SqlDto;
import com.yhwl.hz.common.core.util.R;
import com.yhwl.hz.common.security.annotation.Inner;
import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.List;

/**
 * @author hz
 * @date 2022/7/9
 *
 * 动态数据 无代码
 */
@RestController
@Inner(value = false)
@RequiredArgsConstructor
@RequestMapping("/dynamic")
@Api(value = "dynamic", tags = "无代码管理")
public class GenDynamicController {

	private final GenDynamicService dynamicService;

	@GetMapping("/gen")
	public R genCode(GenConfig genConfig) {
		return R.ok(dynamicService.run(genConfig));
	}

	@PostMapping("/dynamic-query")
	public R dynamicQuery(@RequestBody SqlDto sqlDto) {
		List<LinkedHashMap<String, Object>> linkedHashMaps = dynamicService.dynamicQuery(sqlDto);
		return R.ok(linkedHashMaps);
	}

}
