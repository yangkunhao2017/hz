package com.yhwl.hz.common.sequence.range.impl.name;

import com.yhwl.hz.common.sequence.range.BizName;
import lombok.AllArgsConstructor;

/**
 * @author hz
 * @date 2019-05-26 根据传入返回bizname
 */
@AllArgsConstructor
public class DefaultBizName implements BizName {

	private String bizName;

	/**
	 * 生成空间名称
	 */
	@Override
	public String create() {
		return bizName;
	}

}
