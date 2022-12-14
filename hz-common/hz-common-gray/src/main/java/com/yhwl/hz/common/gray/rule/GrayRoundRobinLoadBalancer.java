package com.yhwl.hz.common.gray.rule;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.cloud.nacos.NacosServiceInstance;
import com.alibaba.nacos.client.naming.utils.Chooser;
import com.alibaba.nacos.client.naming.utils.Pair;
import com.yhwl.hz.common.core.constant.CommonConstants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.*;
import org.springframework.cloud.loadbalancer.core.NoopServiceInstanceListSupplier;
import org.springframework.cloud.loadbalancer.core.RoundRobinLoadBalancer;
import org.springframework.cloud.loadbalancer.core.ServiceInstanceListSupplier;
import org.springframework.http.HttpHeaders;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author hz
 * @date 2020/11/20
 */
@Slf4j
public class GrayRoundRobinLoadBalancer extends RoundRobinLoadBalancer {

	private ObjectProvider<ServiceInstanceListSupplier> serviceInstanceListSupplierProvider;

	private String serviceId;

	/**
	 * @param serviceInstanceListSupplierProvider a provider of
	 * {@link ServiceInstanceListSupplier} that will be used to get available instances
	 * @param serviceId id of the service for which to choose an instance
	 */
	public GrayRoundRobinLoadBalancer(ObjectProvider<ServiceInstanceListSupplier> serviceInstanceListSupplierProvider,
			String serviceId) {
		super(serviceInstanceListSupplierProvider, serviceId);
		this.serviceInstanceListSupplierProvider = serviceInstanceListSupplierProvider;
		this.serviceId = serviceId;
	}

	@Override
	public Mono<Response<ServiceInstance>> choose(Request request) {
		ServiceInstanceListSupplier supplier = serviceInstanceListSupplierProvider
				.getIfAvailable(NoopServiceInstanceListSupplier::new);
		return supplier.get(request).next().map(serviceInstances -> getInstanceResponse(serviceInstances, request));

	}

	Response<ServiceInstance> getInstanceResponse(List<ServiceInstance> instances, Request request) {

		// ??????????????????????????? ????????????
		if (CollUtil.isEmpty(instances)) {
			log.warn("No instance available serviceId: {}", serviceId);
			return new EmptyResponse();
		}

		if (request == null || request.getContext() == null) {
			return super.choose(request).block();
		}

		DefaultRequestContext requestContext = (DefaultRequestContext) request.getContext();
		if (!(requestContext.getClientRequest() instanceof RequestData)) {
			return super.choose(request).block();
		}

		RequestData clientRequest = (RequestData) requestContext.getClientRequest();
		HttpHeaders headers = clientRequest.getHeaders();

		String reqVersion = headers.getFirst(CommonConstants.VERSION);
		if (StrUtil.isBlank(reqVersion)) {
			// ???????????????VERSION??????
			List<ServiceInstance> versionInstanceList = instances.stream()
					.filter(instance -> !instance.getMetadata().containsKey(CommonConstants.VERSION))
					.collect(Collectors.toList());
			if (CollUtil.isEmpty(versionInstanceList)) {
				// ????????????????????????
				return new DefaultResponse(randomByWeight(instances));
			}
			// ????????????????????????
			return new DefaultResponse(randomByWeight(versionInstanceList));
		}

		// ?????????????????????????????????????????????????????????
		List<ServiceInstance> serviceInstanceList = instances.stream().filter(instance -> {
			NacosServiceInstance nacosInstance = (NacosServiceInstance) instance;
			Map<String, String> metadata = nacosInstance.getMetadata();
			String targetVersion = MapUtil.getStr(metadata, CommonConstants.VERSION);
			return reqVersion.equalsIgnoreCase(targetVersion);
		}).collect(Collectors.toList());

		// ?????? ????????????
		if (CollUtil.isNotEmpty(serviceInstanceList)) {
			ServiceInstance instance = randomByWeight(serviceInstanceList);

			log.debug("gray instance available serviceId: {} , instanceId: {}", serviceId, instance.getInstanceId());
			return new DefaultResponse(instance);
		}
		else {
			// ?????????,?????????????????????????????????
			return super.choose(request).block();
		}
	}

	/**
	 * ??????nacos???????????????????????????
	 * @param serviceInstances ??????????????????
	 * @return ???????????????ServiceInstance
	 */
	protected ServiceInstance randomByWeight(final List<ServiceInstance> serviceInstances) {
		if (serviceInstances.size() == 1) {
			return serviceInstances.get(0);
		}
		else {
			List<Pair<ServiceInstance>> hostsWithWeight = new ArrayList<>();
			for (ServiceInstance serviceInstance : serviceInstances) {
				if ("true".equals(serviceInstance.getMetadata().getOrDefault("nacos.healthy", "true"))) {
					hostsWithWeight.add(new Pair<>(serviceInstance,
							Double.parseDouble(serviceInstance.getMetadata().getOrDefault("nacos.weight", "1"))));
				}
			}
			Chooser<String, ServiceInstance> vipChooser = new Chooser<>("www.taobao.com", hostsWithWeight);
			return vipChooser.randomWithWeight();
		}
	}

}
