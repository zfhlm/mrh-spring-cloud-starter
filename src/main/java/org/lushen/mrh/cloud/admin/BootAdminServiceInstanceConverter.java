package org.lushen.mrh.cloud.admin;

import java.net.URI;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;

import org.apache.commons.lang.StringUtils;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.web.util.UriBuilder;
import org.springframework.web.util.UriComponentsBuilder;

import de.codecentric.boot.admin.server.cloud.discovery.DefaultServiceInstanceConverter;
import de.codecentric.boot.admin.server.domain.values.Registration;

/**
 * spring-boot-admin 订阅服务信息转换器
 * 
 * @author hlm
 */
public class BootAdminServiceInstanceConverter extends DefaultServiceInstanceConverter {

	@Override
	public Registration convert(ServiceInstance instance) {

		Registration registration = super.convert(instance);

		// 尝试获取context-path
		Map<String, String> metadata = Optional.ofNullable(instance.getMetadata()).orElse(Collections.emptyMap());
		String contextPath = metadata.get(BootAdminAutoConfiguration.DISCOVERY_META_DATA_CONTEXT_PATH);

		// 重写服务监控信息，url统一添加context-path前缀
		if(StringUtils.isNotBlank(contextPath)) {
			Registration.Builder builder = Registration.builder();
			builder.name(registration.getName());
			builder.managementUrl(rebuildHttpUri(URI.create(registration.getManagementUrl()), contextPath).toString());
			builder.healthUrl(rebuildHttpUri(URI.create(registration.getHealthUrl()), contextPath).toString());
			builder.serviceUrl(rebuildHttpUri(URI.create(registration.getServiceUrl()), contextPath).toString());
			builder.metadata(registration.getMetadata());
			builder.source(registration.getSource());
			return builder.build();
		}

		return registration;
	}

	private URI rebuildHttpUri(URI httpUri, String contextPath) {
		UriBuilder builder = UriComponentsBuilder.fromUri(httpUri);   
		builder.replacePath("/");
		builder.path(contextPath);
		builder.path(httpUri.getPath());
		return builder.build();
	}

}
