package com.yhwl.hz.codegen.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.CharUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.dynamic.datasource.toolkit.DynamicDataSourceContextHolder;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.yhwl.hz.codegen.entity.GenConfig;
import com.yhwl.hz.codegen.entity.GenFormConf;
import com.yhwl.hz.codegen.mapper.GenDynamicMapper;
import com.yhwl.hz.codegen.mapper.GenFormConfMapper;
import com.yhwl.hz.codegen.mapper.GeneratorMapper;
import com.yhwl.hz.codegen.service.GenCodeService;
import com.yhwl.hz.codegen.service.GenDynamicService;
import com.yhwl.hz.codegen.util.SqlDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.ssssssss.magicapi.core.model.ApiInfo;
import org.ssssssss.magicapi.core.model.Group;
import org.ssssssss.magicapi.core.service.MagicResourceService;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 动态无代码实现类
 *
 * @author hz
 * @date 2022/7/9
 */
@Service
@RequiredArgsConstructor
public class GenDynamicServiceImpl implements GenDynamicService {

	private final MagicResourceService magicResourceService;

	private final GenFormConfMapper genFormConfMapper;

	private final GenDynamicMapper dynamicMapper;

	private final GenCodeService magic;

	/**
	 * 动态查询
	 * @param sqlDto 请求参数
	 * @return ListMap
	 */
	@Override
	public List<LinkedHashMap<String, Object>> dynamicQuery(SqlDto sqlDto) {
		DynamicDataSourceContextHolder.clear();
		// 切换线程， 注意清空避免污染
		DynamicDataSourceContextHolder.push(sqlDto.getDsName());
		List<LinkedHashMap<String, Object>> linkedHashMaps = dynamicMapper.dynamicQuerySql(sqlDto.getSql());
		DynamicDataSourceContextHolder.clear();
		return linkedHashMaps;
	}

	@Override
	public Map<String, String> run(GenConfig genConfig) {
		// 根据tableName 查询最新的表单配置
		List<GenFormConf> formConfList = genFormConfMapper.selectList(Wrappers.<GenFormConf>lambdaQuery()
				.eq(GenFormConf::getTableName, genConfig.getTableName()).orderByDesc(GenFormConf::getCreateTime));

		String tableNames = genConfig.getTableName();
		String dsName = genConfig.getDsName();

		GeneratorMapper mapper = magic.getMapper(genConfig.getDsName());

		Map<String, String> templates = new HashMap<>();
		for (String tableName : StrUtil.split(tableNames, StrUtil.DASHED)) {
			// 查询表信息
			Map<String, String> table = mapper.queryTable(tableName, dsName);
			// 查询列信息
			List<Map<String, String>> columns = mapper.selectMapTableColumn(tableName, dsName);
			// 生成代码

			if (CollUtil.isNotEmpty(formConfList)) {
				templates = magic.gen(genConfig, table, columns, null, formConfList.get(0));
			}
			else {
				templates = magic.gen(genConfig, table, columns, null, null);
			}

			// 分组
			Group group = new Group();
			String id = dsName + "_" + StrUtil.toSymbolCase(tableName, CharUtil.UNDERLINE);
			group.setId(id);
			group.setParentId("0");
			group.setName(id);
			group.setType("api");
			group.setPath("/api/dynamic/" + dsName + "/" + StrUtil.toSymbolCase(tableName, CharUtil.UNDERLINE));
			magicResourceService.saveGroup(group);

			ApiInfo apiInfo = new ApiInfo();
			apiInfo.setId(id + "_query");
			String script = templates.get("template/magic/query.magic.vm");
			apiInfo.setScript(script);
			apiInfo.setName("分页");
			apiInfo.setPath("/list");
			apiInfo.setMethod("post");
			apiInfo.setGroupId(id);
			magicResourceService.saveFile(apiInfo);

			ApiInfo delApiInfo = new ApiInfo();
			delApiInfo.setId(id + "_del");
			String delScript = templates.get("template/magic/del.magic.vm");
			delApiInfo.setScript(delScript);
			delApiInfo.setName("删除");
			delApiInfo.setPath("/delete");
			delApiInfo.setMethod("delete");
			delApiInfo.setGroupId(id);
			magicResourceService.saveFile(delApiInfo);

			ApiInfo addApiInfo = new ApiInfo();
			addApiInfo.setId(id + "_add");
			String addScript = templates.get("template/magic/add.magic.vm");
			addApiInfo.setScript(addScript);
			addApiInfo.setName("添加");
			addApiInfo.setPath("/save");
			addApiInfo.setMethod("post");
			addApiInfo.setGroupId(id);
			magicResourceService.saveFile(addApiInfo);

			ApiInfo updateApiInfo = new ApiInfo();
			updateApiInfo.setId(id + "_update");
			String updateScript = templates.get("template/magic/update.magic.vm");
			updateApiInfo.setScript(updateScript);
			updateApiInfo.setName("修改");
			updateApiInfo.setPath("/update");
			updateApiInfo.setMethod("put");
			updateApiInfo.setGroupId(id);
			magicResourceService.saveFile(updateApiInfo);
		}

		return templates;
	}

}
