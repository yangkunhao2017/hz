package com.anjiplus.template.gaea.business.modules.datasource.config;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.Header;
import org.apache.http.HeaderElement;
import org.apache.http.HeaderElementIterator;
import org.apache.http.HttpHost;
import org.apache.http.client.HttpClient;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.ConnectionKeepAliveStrategy;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.DefaultHttpRequestRetryHandler;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicHeaderElementIterator;
import org.apache.http.protocol.HTTP;
import org.apache.http.ssl.SSLContextBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import java.nio.charset.Charset;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.*;

/**
 * Created by raodeming on 2021/3/24.
 */
@Configuration
@Slf4j
public class HttpClientConfig {

	@Autowired
	private HttpClientPoolConfig httpClientPoolConfig;

	/**
	 * ??????HTTP???????????????
	 */
	@Bean(name = "clientHttpRequestFactory")
	public ClientHttpRequestFactory clientHttpRequestFactory() {
		/**
		 * maxTotalConnection ??? maxConnectionPerRoute ????????????
		 */
		if (httpClientPoolConfig.getMaxTotalConnect() <= 0) {
			throw new IllegalArgumentException(
					"invalid maxTotalConnection: " + httpClientPoolConfig.getMaxTotalConnect());
		}
		if (httpClientPoolConfig.getMaxConnectPerRoute() <= 0) {
			throw new IllegalArgumentException(
					"invalid maxConnectionPerRoute: " + httpClientPoolConfig.getMaxConnectPerRoute());
		}
		HttpComponentsClientHttpRequestFactory clientHttpRequestFactory = new HttpComponentsClientHttpRequestFactory(
				httpClient());
		// ????????????
		clientHttpRequestFactory.setConnectTimeout(httpClientPoolConfig.getConnectTimeout());
		// ??????????????????????????????SocketTimeout
		clientHttpRequestFactory.setReadTimeout(httpClientPoolConfig.getReadTimeout());
		// ???????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????
		clientHttpRequestFactory.setConnectionRequestTimeout(httpClientPoolConfig.getConnectionRequestTimout());
		return clientHttpRequestFactory;
	}

	/**
	 * ?????????RestTemplate,?????????spring???Bean????????????spring????????????
	 */
	@Bean(name = "dataSourceRestTemplate")
	public RestTemplate restTemplate(@Qualifier("clientHttpRequestFactory") ClientHttpRequestFactory factory) {
		return createRestTemplate(factory);
	}

	/**
	 * ??????httpClient
	 * @return
	 */
	@Bean
	public HttpClient httpClient() {
		HttpClientBuilder httpClientBuilder = HttpClientBuilder.create();
		try {
			// ????????????ssl??????
			SSLContext sslContext = new SSLContextBuilder().loadTrustMaterial(null, (arg0, arg1) -> true).build();

			httpClientBuilder.setSSLContext(sslContext);
			HostnameVerifier hostnameVerifier = NoopHostnameVerifier.INSTANCE;
			SSLConnectionSocketFactory sslConnectionSocketFactory = new SSLConnectionSocketFactory(sslContext,
					hostnameVerifier);
			Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder.<ConnectionSocketFactory>create()
					// ??????http???https??????
					.register("http", PlainConnectionSocketFactory.getSocketFactory())
					.register("https", sslConnectionSocketFactory).build();

			// ??????Httpclient????????????????????????(??????)???????????????netty???okHttp????????????http??????
			PoolingHttpClientConnectionManager poolingHttpClientConnectionManager = new PoolingHttpClientConnectionManager(
					socketFactoryRegistry);
			// ???????????????
			poolingHttpClientConnectionManager.setMaxTotal(httpClientPoolConfig.getMaxTotalConnect());
			// ??????????????????
			poolingHttpClientConnectionManager.setDefaultMaxPerRoute(httpClientPoolConfig.getMaxConnectPerRoute());
			// ???????????????
			httpClientBuilder.setConnectionManager(poolingHttpClientConnectionManager);
			// ????????????
			httpClientBuilder
					.setRetryHandler(new DefaultHttpRequestRetryHandler(httpClientPoolConfig.getRetryTimes(), true));

			// ?????????????????????
			List<Header> headers = getDefaultHeaders();
			httpClientBuilder.setDefaultHeaders(headers);
			// ???????????????????????????
			httpClientBuilder.setKeepAliveStrategy(connectionKeepAliveStrategy());
			return httpClientBuilder.build();
		}
		catch (KeyManagementException | NoSuchAlgorithmException | KeyStoreException e) {
			log.error("?????????HTTP???????????????", e);
		}
		return null;
	}

	/**
	 * ???????????????????????????
	 * @return
	 */
	public ConnectionKeepAliveStrategy connectionKeepAliveStrategy() {
		return (response, context) -> {
			// Honor 'keep-alive' header
			HeaderElementIterator it = new BasicHeaderElementIterator(response.headerIterator(HTTP.CONN_KEEP_ALIVE));
			while (it.hasNext()) {
				HeaderElement he = it.nextElement();
				log.info("HeaderElement:{}", JSON.toJSONString(he));
				String param = he.getName();
				String value = he.getValue();
				if (value != null && "timeout".equalsIgnoreCase(param)) {
					try {
						return Long.parseLong(value) * 1000;
					}
					catch (NumberFormatException ignore) {
						log.error("?????????????????????????????????", ignore);
					}
				}
			}
			HttpHost target = (HttpHost) context.getAttribute(HttpClientContext.HTTP_TARGET_HOST);
			// ????????????????????????,????????????????????????????????????,???????????????
			Optional<Map.Entry<String, Integer>> any = Optional
					.ofNullable(httpClientPoolConfig.getKeepAliveTargetHost()).orElseGet(HashMap::new).entrySet()
					.stream().filter(e -> e.getKey().equalsIgnoreCase(target.getHostName())).findAny();
			// ???????????????????????????????????????
			return any.map(en -> en.getValue() * 1000L).orElse(httpClientPoolConfig.getKeepAliveTime() * 1000L);
		};
	}

	/**
	 * ???????????????
	 * @return
	 */
	private List<Header> getDefaultHeaders() {
		List<Header> headers = new ArrayList<>();
		headers.add(new BasicHeader("User-Agent",
				"Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/31.0.1650.16 Safari/537.36"));
		headers.add(new BasicHeader("Accept-Encoding", "gzip,deflate"));
		headers.add(new BasicHeader("Accept-Language", "zh-CN"));
		headers.add(new BasicHeader("Connection", "Keep-Alive"));
		return headers;
	}

	private RestTemplate createRestTemplate(ClientHttpRequestFactory factory) {
		RestTemplate restTemplate = new RestTemplate(factory);

		// ????????????RestTemplate?????????MessageConverter
		// ????????????StringHttpMessageConverter????????????????????????????????????
		modifyDefaultCharset(restTemplate);

		// ?????????????????????
		restTemplate.setErrorHandler(new DefaultResponseErrorHandler());

		return restTemplate;
	}

	/**
	 * ?????????????????????????????????utf-8
	 * @param restTemplate
	 */
	private void modifyDefaultCharset(RestTemplate restTemplate) {
		List<HttpMessageConverter<?>> converterList = restTemplate.getMessageConverters();
		HttpMessageConverter<?> converterTarget = null;
		for (HttpMessageConverter<?> item : converterList) {
			if (StringHttpMessageConverter.class == item.getClass()) {
				converterTarget = item;
				break;
			}
		}
		if (null != converterTarget) {
			converterList.remove(converterTarget);
		}
		Charset defaultCharset = Charset.forName(httpClientPoolConfig.getCharset());
		converterList.add(1, new StringHttpMessageConverter(defaultCharset));
	}

}
