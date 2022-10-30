package com.yhwl.hz.mp.entity.vo;

import com.yhwl.hz.mp.entity.WxAccountFans;
import com.yhwl.hz.mp.entity.WxAccountTag;
import lombok.Data;

import java.util.List;

/**
 * 粉丝Vo 对象
 *
 * @author hz
 * @date 2022/1/5
 */
@Data
public class WxAccountFansVo extends WxAccountFans {

	/**
	 * 标签名称列表
	 */
	private List<WxAccountTag> tagList;

}
