package org.lushen.mrh.cloud.admin;

import java.util.Optional;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.lushen.mrh.cloud.discovery.DiscoveryMetadataConfigurer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.cloud.client.ConditionalOnDiscoveryEnabled;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import de.codecentric.boot.admin.server.cloud.discovery.ServiceInstanceConverter;
import de.codecentric.boot.admin.server.config.AdminServerMarkerConfiguration;

/**
 * spring-boot-admin 扩展配置
 * 
 * @author hlm
 */
@Configuration(proxyBeanMethods=false)
@ConditionalOnDiscoveryEnabled
public class BootAdminAutoConfiguration {

	private final Log log = LogFactory.getLog(BootAdminAutoConfiguration.class);

	public static final String DISCOVERY_META_DATA_CONTEXT_PATH = "context-path";

	@Bean
	@ConditionalOnBean(ServerProperties.class)
	public DiscoveryMetadataConfigurer contextPathMetadataConfigurer(@Autowired ServerProperties serverProperties) {
		log.info("Initialize spring-boot-admin discovery metadata configurer.");
		String contextPath = Optional.ofNullable(serverProperties.getServlet()).map(e -> e.getContextPath()).orElse("/");
		return (registry -> registry.put(DISCOVERY_META_DATA_CONTEXT_PATH, contextPath));
	}

	@Configuration(proxyBeanMethods=false)
	@ConditionalOnBean(AdminServerMarkerConfiguration.Marker.class)
	public class BootAdminServerAutoConfiguration {

		@Bean
		@ConditionalOnMissingBean(ServiceInstanceConverter.class)
		public ServiceInstanceConverter bootAdminServiceInstanceConverter() {
			log.info("Initialize spring-boot-admin service-instance-converter.");
			return new BootAdminServiceInstanceConverter();
		}

	}

}
