package org.lushen.mrh.cloud.admin;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.lushen.mrh.cloud.discovery.DiscoveryPropertiesCustomizer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.cloud.client.ConditionalOnDiscoveryEnabled;
import org.springframework.cloud.consul.discovery.ConditionalOnConsulDiscoveryEnabled;
import org.springframework.cloud.consul.discovery.ConsulDiscoveryProperties;
import org.springframework.cloud.zookeeper.discovery.ConditionalOnZookeeperDiscoveryEnabled;
import org.springframework.cloud.zookeeper.discovery.ZookeeperDiscoveryProperties;
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
@ConditionalOnBean(AdminServerMarkerConfiguration.Marker.class)
@ConditionalOnDiscoveryEnabled
public class BootAdminAutoConfiguration {

	private static final Log log = LogFactory.getLog(BootAdminAutoConfiguration.class);

	public static final String DISCOVERY_META_DATA_CONTEXT_PATH = "context-path";

	@Bean
	@ConditionalOnMissingBean(ServiceInstanceConverter.class)
	public ServiceInstanceConverter bootAdminServiceInstanceConverter() {
		log.info("Initialize spring-boot-admin service-instance-converter.");
		return new BootAdminServiceInstanceConverter();
	}

	@Configuration(proxyBeanMethods=false)
	@ConditionalOnZookeeperDiscoveryEnabled
	@ConditionalOnBean(ServerProperties.class)
	public class ZookeeperBootAdminAutoConfiguration {

		@Bean
		public DiscoveryPropertiesCustomizer<ZookeeperDiscoveryProperties> contextPathMetadataCustomizer(@Autowired ServerProperties serverProperties) {
			log.info("Initialize spring-boot-admin zookeeper discovery-properties-customizer.");
			return (properties -> {
				Map<String, String> metadata = new HashMap<String, String>();
				metadata.putAll(Optional.ofNullable(properties.getMetadata()).orElse(Collections.emptyMap()));
				metadata.put(DISCOVERY_META_DATA_CONTEXT_PATH, Optional.ofNullable(serverProperties.getServlet()).map(e -> e.getContextPath()).orElse("/"));
				properties.setMetadata(metadata);
			});
		}

	}

	@Configuration(proxyBeanMethods=false)
	@ConditionalOnConsulDiscoveryEnabled
	@ConditionalOnBean(ServerProperties.class)
	public class ConsulBootAdminAutoConfiguration {

		@Bean
		public DiscoveryPropertiesCustomizer<ConsulDiscoveryProperties> contextPathMetadataCustomizer(@Autowired ServerProperties serverProperties) {
			log.info("Initialize spring-boot-admin consul discovery-properties-customizer.");
			return (properties -> {
				String contextPath = Optional.ofNullable(serverProperties.getServlet()).map(e -> e.getContextPath()).orElse("/");
				List<String> tags = new ArrayList<String>(properties.getTags());
				tags.add(StringUtils.join(DISCOVERY_META_DATA_CONTEXT_PATH, "=", contextPath));
				properties.setTags(tags);
			});
		}

	}

}
