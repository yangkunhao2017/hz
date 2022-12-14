
package com.anjiplus.template.gaea.business.modules.dashboard.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.anji.plus.gaea.curd.mapper.GaeaBaseMapper;
import com.anji.plus.gaea.exception.BusinessExceptionBuilder;
import com.anji.plus.gaea.utils.GaeaAssert;
import com.anji.plus.gaea.utils.GaeaBeanUtils;
import com.anjiplus.template.gaea.business.code.ResponseCode;
import com.anjiplus.template.gaea.business.modules.dashboard.controller.dto.ChartDto;
import com.anjiplus.template.gaea.business.modules.dashboard.controller.dto.ReportDashboardDto;
import com.anjiplus.template.gaea.business.modules.dashboard.controller.dto.ReportDashboardObjectDto;
import com.anjiplus.template.gaea.business.modules.dashboard.dao.ReportDashboardMapper;
import com.anjiplus.template.gaea.business.modules.dashboard.dao.entity.ReportDashboard;
import com.anjiplus.template.gaea.business.modules.dashboard.service.ChartStrategy;
import com.anjiplus.template.gaea.business.modules.dashboard.service.ReportDashboardService;
import com.anjiplus.template.gaea.business.modules.dashboardwidget.controller.dto.ReportDashboardWidgetDto;
import com.anjiplus.template.gaea.business.modules.dashboardwidget.controller.dto.ReportDashboardWidgetValueDto;
import com.anjiplus.template.gaea.business.modules.dashboardwidget.dao.entity.ReportDashboardWidget;
import com.anjiplus.template.gaea.business.modules.dashboardwidget.service.ReportDashboardWidgetService;
import com.anjiplus.template.gaea.business.modules.dataset.controller.dto.DataSetDto;
import com.anjiplus.template.gaea.business.modules.dataset.controller.dto.OriginalDataDto;
import com.anjiplus.template.gaea.business.modules.dataset.service.DataSetService;
import com.anjiplus.template.gaea.business.modules.file.entity.GaeaFile;
import com.anjiplus.template.gaea.business.modules.file.service.GaeaFileService;
import com.anjiplus.template.gaea.business.modules.file.util.FileUtils;
import com.anjiplus.template.gaea.business.util.DateUtil;
import com.anjiplus.template.gaea.business.util.FileUtil;
import com.anjiplus.template.gaea.business.util.UuidUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author Raod
 * @desc ReportDashboard ????????????????????????
 * @date 2021-04-12 14:52:21.761
 **/
@Service
@Slf4j
// @RequiredArgsConstructor
public class ReportDashboardServiceImpl implements ReportDashboardService, InitializingBean, ApplicationContextAware {

	@Autowired
	private ReportDashboardMapper reportDashboardMapper;

	@Autowired
	private ReportDashboardWidgetService reportDashboardWidgetService;

	@Autowired
	private DataSetService dataSetService;

	@Autowired
	private GaeaFileService gaeaFileService;

	@Value("${customer.file.downloadPath:''}")
	private String fileDownloadPath;

	@Value("${customer.file.dist-path:''}")
	private String dictPath;

	private final static String ZIP_PATH = "/zip/";

	private final static String JSON_PATH = "dashboard.json";

	private Map<String, ChartStrategy> queryServiceImplMap = new HashMap<>();

	private ApplicationContext applicationContext;

	@Override
	public GaeaBaseMapper<ReportDashboard> getMapper() {
		return reportDashboardMapper;
	}

	@Override
	public ReportDashboardObjectDto getDetail(String reportCode) {
		ReportDashboardObjectDto result = new ReportDashboardObjectDto();
		ReportDashboardDto reportDashboardDto = new ReportDashboardDto();
		ReportDashboard reportDashboard = this.selectOne("report_code", reportCode);
		if (null == reportDashboard) {
			return new ReportDashboardObjectDto();
		}
		GaeaBeanUtils.copyAndFormatter(reportDashboard, reportDashboardDto);

		List<ReportDashboardWidget> list = reportDashboardWidgetService.list(
				new QueryWrapper<ReportDashboardWidget>().lambda().eq(ReportDashboardWidget::getReportCode, reportCode)
						.orderByAsc(ReportDashboardWidget::getSort));
		List<ReportDashboardWidgetDto> reportDashboardWidgetDtoList = new ArrayList<>();
		list.forEach(reportDashboardWidget -> {
			ReportDashboardWidgetDto reportDashboardWidgetDto = new ReportDashboardWidgetDto();
			ReportDashboardWidgetValueDto value = new ReportDashboardWidgetValueDto();
			value.setSetup(StringUtils.isNotBlank(reportDashboardWidget.getSetup())
					? JSONObject.parseObject(reportDashboardWidget.getSetup()) : new JSONObject());
			value.setData(StringUtils.isNotBlank(reportDashboardWidget.getData())
					? JSONObject.parseObject(reportDashboardWidget.getData()) : new JSONObject());
			value.setPosition(StringUtils.isNotBlank(reportDashboardWidget.getPosition())
					? JSONObject.parseObject(reportDashboardWidget.getPosition()) : new JSONObject());
			value.setCollapse(StringUtils.isNotBlank(reportDashboardWidget.getCollapse())
					? JSONObject.parseObject(reportDashboardWidget.getCollapse()) : new JSONObject());

			// ?????????????????????
			analysisData(value);
			reportDashboardWidgetDto.setType(reportDashboardWidget.getType());
			reportDashboardWidgetDto.setValue(value);
			reportDashboardWidgetDto.setOptions(JSONObject.parseObject(reportDashboardWidget.getOptions()));
			reportDashboardWidgetDtoList.add(reportDashboardWidgetDto);
		});
		reportDashboardDto.setWidgets(reportDashboardWidgetDtoList);
		result.setDashboard(reportDashboardDto);
		result.setReportCode(reportCode);
		return result;
	}

	/***
	 * ??????????????????
	 * @param dto
	 */
	@Override
	@Transactional(rollbackFor = Exception.class)
	public void insertDashboard(ReportDashboardObjectDto dto) {
		String reportCode = dto.getReportCode();
		GaeaAssert.notEmpty(reportCode, ResponseCode.PARAM_IS_NULL, "reportCode");
		// ??????ReportDashboard
		ReportDashboard reportDashboard = this.selectOne("report_code", reportCode);
		ReportDashboard dashboard = new ReportDashboard();
		GaeaBeanUtils.copyAndFormatter(dto.getDashboard(), dashboard);
		BeanUtils.copyProperties(dto.getDashboard(), dashboard);
		dashboard.setReportCode(reportCode);
		if (null == reportDashboard) {
			// ??????
			this.insert(dashboard);
		}
		else {
			// ??????
			dashboard.setId(reportDashboard.getId());
			dashboard.setVersion(null);
			this.update(dashboard);
		}

		// ??????reportDashboardWidget
		reportDashboardWidgetService.delete(new QueryWrapper<ReportDashboardWidget>().lambda()
				.eq(ReportDashboardWidget::getReportCode, reportCode));
		List<ReportDashboardWidgetDto> widgets = dto.getWidgets();

		// List<ReportDashboardWidget> reportDashboardWidgetList = new ArrayList<>();
		for (int i = 0; i < widgets.size(); i++) {
			ReportDashboardWidget reportDashboardWidget = new ReportDashboardWidget();
			ReportDashboardWidgetDto reportDashboardWidgetDto = widgets.get(i);
			String type = reportDashboardWidgetDto.getType();
			ReportDashboardWidgetValueDto value = reportDashboardWidgetDto.getValue();
			reportDashboardWidget.setReportCode(reportCode);
			reportDashboardWidget.setType(type);
			reportDashboardWidget.setSetup(value.getSetup() != null ? JSONObject.toJSONString(value.getSetup()) : "");
			reportDashboardWidget.setData(value.getData() != null ? JSONObject.toJSONString(value.getData()) : "");
			reportDashboardWidget
					.setPosition(value.getPosition() != null ? JSONObject.toJSONString(value.getPosition()) : "");
			reportDashboardWidget
					.setCollapse(value.getCollapse() != null ? JSONObject.toJSONString(value.getCollapse()) : "");
			reportDashboardWidget.setOptions(reportDashboardWidgetDto.getOptions() != null
					? JSONObject.toJSONString(reportDashboardWidgetDto.getOptions()) : "");
			reportDashboardWidget.setEnableFlag(1);
			reportDashboardWidget.setDeleteFlag(0);
			reportDashboardWidget.setSort((long) (i + 1));

			// ????????????????????????????????????
			reportDashboardWidgetService.insert(reportDashboardWidget);

			// reportDashboardWidgetList.add(reportDashboardWidget);
		}
		// reportDashboardWidgetService.insertBatch(reportDashboardWidgetList);

	}

	@Override
	public Object getChartData(ChartDto dto) {
		// String chartType = dto.getChartType();
		DataSetDto setDto = new DataSetDto();
		setDto.setSetCode(dto.getSetCode());
		setDto.setContextData(dto.getContextData());
		OriginalDataDto result = dataSetService.getData(setDto);
		List<JSONObject> data = result.getData();
		// ???????????????
		List<JSONObject> resultData = buildTimeLine(data, dto);
		return resultData;
		// return getTarget(chartType).transform(dto, result.getData());
	}

	/**
	 * ???????????????zip??????
	 * @param request
	 * @param response
	 * @param reportCode
	 * @return
	 */
	@Override
	public ResponseEntity<byte[]> exportDashboard(HttpServletRequest request, HttpServletResponse response,
			String reportCode, Integer showDataSet) {
		String userAgent = request.getHeader("User-Agent");
		boolean isIeBrowser = userAgent.indexOf("MSIE") > 0;

		ReportDashboardObjectDto detail = getDetail(reportCode);
		List<ReportDashboardWidgetDto> widgets = detail.getDashboard().getWidgets();
		detail.setWidgets(widgets);
		detail.getDashboard().setWidgets(null);

		// 1.??????????????????,/app/disk/upload/zip/???????????????
		String path = dictPath + ZIP_PATH + UuidUtil.generateShortUuid();

		// ????????????????????????????????????1.????????????2.?????????????????????
		String backgroundImage = detail.getDashboard().getBackgroundImage();
		zipLoadImage(backgroundImage, path);
		detail.getWidgets().stream()
				.filter(reportDashboardWidgetDto -> "widget-image".equals(reportDashboardWidgetDto.getType()))
				.forEach(reportDashboardWidgetDto -> {
					String imageAddress = reportDashboardWidgetDto.getValue().getSetup().getString("imageAdress");
					zipLoadImage(imageAddress, path);
				});

		// showDataSet == 0 ????????????????????????
		if (0 == showDataSet) {
			detail.getWidgets().forEach(reportDashboardWidgetDto -> {
				ReportDashboardWidgetValueDto value = reportDashboardWidgetDto.getValue();
				JSONObject data = value.getData();
				if (null != data && data.containsKey("dataType")) {
					reportDashboardWidgetDto.getValue().getData().put("dataType", "staticData");
				}
			});
		}

		// 2.?????????????????????json????????????
		String jsonPath = path + "/" + JSON_PATH;
		FileUtil.WriteStringToFile(jsonPath, JSONObject.toJSONString(detail));

		// ???path???????????????zip
		String zipPath = path + ".zip";
		FileUtil.compress(path, zipPath);

		File file = new File(zipPath);
		ResponseEntity.BodyBuilder builder = ResponseEntity.ok();
		builder.contentLength(file.length());
		// application/octet-stream ????????????????????????????????????????????????
		builder.contentType(MediaType.APPLICATION_OCTET_STREAM);
		if (isIeBrowser) {
			builder.header("Content-Disposition", "attachment; filename=" + reportCode + ".zip");
		}
		else {
			builder.header("Content-Disposition", "attacher; filename*=UTF-8''" + reportCode + ".zip");
		}

		ResponseEntity<byte[]> body = builder.body(FileUtils.readFileToByteArray(file));

		// ??????zip??????
		file.delete();
		// ??????path???????????????
		FileUtil.delete(path);
		log.info("?????????????????????{}???{}", zipPath, path);

		return body;
	}

	/**
	 * ????????????zip
	 * @param file
	 * @param reportCode
	 * @return
	 */
	@Override
	@Transactional(rollbackFor = Exception.class)
	public void importDashboard(MultipartFile file, String reportCode) {
		log.info("????????????,{}", reportCode);
		// 1.??????????????????,/app/disk/upload/zip/???????????????
		String path = dictPath + ZIP_PATH + UuidUtil.generateShortUuid();
		// 2.??????
		FileUtil.decompress(file, path);
		// path/uuid/
		File parentPath = new File(path);
		// ??????????????????????????????
		File firstFile = parentPath.listFiles()[0];

		File[] files = firstFile.listFiles();

		// ??????map
		Map<String, String> fileMap = new HashMap<>();
		String content = "";

		for (int i = 0; i < files.length; i++) {
			File childFile = files[i];
			if (JSON_PATH.equals(childFile.getName())) {
				// json??????
				content = FileUtil.readFile(childFile);
			}
			else if ("image".equals(childFile.getName())) {
				File[] imageFiles = childFile.listFiles();
				// ???????????????????????????
				for (File imageFile : imageFiles) {
					// ?????????????????????image
					String fileName = imageFile.getName().split("\\.")[0];
					// ??????fileId??????gaea_file?????????filePath
					LambdaQueryWrapper<GaeaFile> queryWrapper = Wrappers.lambdaQuery();
					queryWrapper.eq(GaeaFile::getFileId, fileName);
					GaeaFile gaeaFile = gaeaFileService.selectOne(queryWrapper);
					String uploadPath;
					if (null == gaeaFile) {
						GaeaFile upload = gaeaFileService.upload(imageFile, fileName);
						log.info("????????????: {}", upload.getFilePath());
						uploadPath = upload.getUrlPath();
					}
					else {
						uploadPath = gaeaFile.getUrlPath();
					}
					fileMap.put(fileName, uploadPath);
				}
			}

		}

		// ??????cotent
		ReportDashboardObjectDto detail = JSONObject.parseObject(content, ReportDashboardObjectDto.class);
		// ????????????????????????????????????1.????????????2.?????????????????????
		String backgroundImage = detail.getDashboard().getBackgroundImage();
		detail.getDashboard().setBackgroundImage(replaceUrl(backgroundImage, fileMap));
		detail.getWidgets().stream()
				.filter(reportDashboardWidgetDto -> "widget-image".equals(reportDashboardWidgetDto.getType()))
				.forEach(reportDashboardWidgetDto -> {
					String imageAddress = reportDashboardWidgetDto.getValue().getSetup().getString("imageAdress");
					String address = replaceUrl(imageAddress, fileMap);
					reportDashboardWidgetDto.getValue().getSetup().put("imageAdress", address);
					reportDashboardWidgetDto.getOptions().getJSONArray("setup").getJSONObject(4).put("value", address);
				});
		// ???????????????????????????
		detail.setReportCode(reportCode);

		// ????????????????????????????????????
		FileUtil.delete(path);

		log.info("????????????????????????????????????...");
		insertDashboard(detail);
	}

	private String replaceUrl(String imageAddress, Map<String, String> fileMap) {
		if (StringUtils.isBlank(imageAddress)) {
			return "";
		}
		String fileId = imageAddress.substring(imageAddress.trim().length() - 36);
		String orDefault = fileMap.getOrDefault(fileId, null);
		if (StringUtils.isBlank(orDefault)) {
			return imageAddress;
		}
		return orDefault;
	}

	/**
	 * ????????????????????????????????????????????????
	 * @param imageAddress
	 * @param path
	 */
	private void zipLoadImage(String imageAddress, String path) {
		// http://10.108.26.197:9095/file/download/1d9bcd35-82a1-4f08-9465-b66b930b6a8d
		if (imageAddress.trim().startsWith(fileDownloadPath)) {
			// ???fileDownloadPath????????????????????????????????????
			String fileName = imageAddress.substring(fileDownloadPath.length() + 1);
			// ??????fileId??????gaea_file?????????filePath
			LambdaQueryWrapper<GaeaFile> queryWrapper = Wrappers.lambdaQuery();
			queryWrapper.eq(GaeaFile::getFileId, fileName);
			GaeaFile gaeaFile = gaeaFileService.selectOne(queryWrapper);
			if (null != gaeaFile) {
				String fileType = gaeaFile.getFileType();
				path = path + "/image/" + fileName + "." + fileType;
				// path = /app/disk/upload/zip/UUID/image

				// ?????????????????????
				String filePath = gaeaFile.getFilePath();
				FileUtil.copyFileUsingFileChannels(filePath, path);
			}
		}

	}

	public ChartStrategy getTarget(String type) {
		for (String s : queryServiceImplMap.keySet()) {
			if (s.contains(type)) {
				return queryServiceImplMap.get(s);
			}
		}
		throw BusinessExceptionBuilder.build(ResponseCode.RULE_CONTENT_NOT_EXIST);
	}

	@Override
	public void afterPropertiesSet() {
		Map<String, ChartStrategy> beanMap = applicationContext.getBeansOfType(ChartStrategy.class);
		// ?????????????????????????????????????????????map???
		for (ChartStrategy serviceImpl : beanMap.values()) {
			queryServiceImplMap.put(serviceImpl.type(), serviceImpl);
		}
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}

	/**
	 * ??????????????????
	 * @param dto
	 */
	public void analysisData(ReportDashboardWidgetValueDto dto) {
		// if (StringUtils.isBlank(reportDashboardWidgetDto.getSetCode())) {
		// return;
		// }
		// DataSetDto dto = new DataSetDto();
		// dto.setSetCode(reportDashboardWidgetDto.getSetCode());
		// if (reportDashboardWidgetDto.getContextData() != null &&
		// reportDashboardWidgetDto.getContextData().size() > 0) {
		// dto.setContextData(reportDashboardWidgetDto.getContextData());
		// }
		// OriginalDataDto data = dataSetService.getData(dto);
		// reportDashboardWidgetDto.setData(JSONObject.toJSONString(data.getData()));
	}

	public List<JSONObject> buildTimeLine(List<JSONObject> data, ChartDto dto) {
		Map<String, String> chartProperties = dto.getChartProperties();
		if (null == chartProperties || chartProperties.size() < 1) {
			return data;
		}
		Map<String, Object> contextData = dto.getContextData();
		if (null == contextData || contextData.size() < 1) {
			return data;
		}
		if (contextData.containsKey("startTime") && contextData.containsKey("endTime")) {
			dto.setStartTime(contextData.get("startTime").toString());
			dto.setEndTime(contextData.get("endTime").toString());
		}
		if (StringUtils.isBlank(dto.getStartTime()) || StringUtils.isBlank(dto.getEndTime())) {
			return data;
		}
		// ?????????????????????????????????????????????

		for (Map.Entry<String, String> entry : chartProperties.entrySet()) {
			String key = entry.getKey();
			String value = entry.getValue();
			dto.setParticles(value);
			setTimeLineFormat(dto);
			if (StringUtils.isNotBlank(dto.getDataTimeFormat())) {
				dto.setTimeLineFiled(key);
				break;
			}

		}

		if (StringUtils.isBlank(dto.getDataTimeFormat())) {
			return data;
		}

		Date beginTime = DateUtil.parseHmsTime(dto.getStartTime());
		Date endTime = DateUtil.parseHmsTime(dto.getEndTime());
		SimpleDateFormat showFormat = new SimpleDateFormat(dto.getTimeLineFormat());
		SimpleDateFormat dataFormat = new SimpleDateFormat(dto.getDataTimeFormat());

		Calendar calendar = Calendar.getInstance();
		calendar.setTime(beginTime);

		Calendar calendarEnd = Calendar.getInstance();
		calendarEnd.setTime(endTime);

		List<String> timeLine = new ArrayList<>();
		List<String> dataTimeline = new ArrayList<>();
		timeLine.add(showFormat.format(calendar.getTime()));
		dataTimeline.add(dataFormat.format(calendar.getTime()));

		// ?????????????????????
		while (true) {
			calendar.add(dto.getTimeUnit(), 1);
			timeLine.add(showFormat.format(calendar.getTime()));
			dataTimeline.add(dataFormat.format(calendar.getTime()));
			if (showFormat.format(calendar.getTime()).equals(showFormat.format(calendarEnd.getTime()))) {
				break;
			}
		}

		// ?????????????????????????????????????????????????????????????????????
		List<JSONObject> result = new ArrayList<>();
		JSONObject jsonDemo = data.get(0);
		String timeLineFiled = dto.getTimeLineFiled();
		for (String dateFormat : dataTimeline) {
			boolean flag = true;
			for (JSONObject datum : data) {
				if (datum.containsKey(timeLineFiled) && datum.getString(timeLineFiled).equals(dateFormat)) {
					result.add(datum);
					flag = false;
				}
			}
			if (flag) {
				// ?????????
				JSONObject json = new JSONObject();
				jsonDemo.forEach((s, o) -> {
					if (s.equals(timeLineFiled)) {
						json.put(timeLineFiled, dateFormat);
					}
					else {
						json.put(s, 0);
					}
				});
				result.add(json);
			}

		}
		return result;
	}

	// ??????????????????
	private void setTimeLineFormat(ChartDto dto) {
		String particles = dto.getParticles();
		if ("xAxis-hour".equals(particles)) {
			dto.setDataTimeFormat("yyyy-MM-dd HH");
			dto.setTimeLineFormat("MM-dd HH");
			dto.setTimeUnit(Calendar.HOUR);
		}
		else if ("xAxis-day".equals(particles)) {
			dto.setDataTimeFormat("yyyy-MM-dd");
			dto.setTimeLineFormat("yyyy-MM-dd");
			dto.setTimeUnit(Calendar.DATE);
		}
		else if ("xAxis-month".equals(particles)) {
			dto.setDataTimeFormat("yyyy-MM");
			dto.setTimeLineFormat("yyyy-MM");
			dto.setTimeUnit(Calendar.MONTH);
		}
		else if ("xAxis-year".equals(particles)) {
			dto.setDataTimeFormat("yyyy");
			dto.setTimeLineFormat("yyyy");
			dto.setTimeUnit(Calendar.YEAR);
		}
	}

}
