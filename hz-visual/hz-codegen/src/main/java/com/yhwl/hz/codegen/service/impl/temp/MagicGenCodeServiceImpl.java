package com.yhwl.hz.codegen.service.impl.temp;

import com.yhwl.hz.codegen.entity.GenConfig;
import com.yhwl.hz.codegen.service.GenCodeService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * magic代码实现
 *
 * @author hz
 * @date 2022/7/11
 */
@Service("magic")
public class MagicGenCodeServiceImpl implements GenCodeService {

	/**
	 * 注入支持的模板列表
	 * @param config 用户输入
	 * @return ListString
	 */
	@Override
	public List<String> getTemplates(GenConfig config) {
		List<String> templates = GenCodeService.super.getTemplates(config);
		templates.add("template/avue/index.vue.vm");
		templates.add("template/avue/crud.js.vm");
		templates.add("template/avue/api.js.vm");
		templates.add("template/magic/query.magic.vm");
		templates.add("template/magic/del.magic.vm");
		templates.add("template/magic/add.magic.vm");
		templates.add("template/magic/update.magic.vm");
		return templates;
	}

}
