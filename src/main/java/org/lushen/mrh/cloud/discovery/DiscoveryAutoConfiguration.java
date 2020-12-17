package org.lushen.mrh.cloud.discovery;

import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.lushen.mrh.cloud.discovery.customizer.ConsulDiscoveryMetadataCustomizer;
import org.lushen.mrh.cloud.discovery.customizer.EurekaDiscoveryMetadataCustomizer;
import org.lushen.mrh.cloud.discovery.customizer.ZookeeperDiscoveryMetadataCustomizer;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.cloud.client.ConditionalOnDiscoveryEnabled;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.consul.ConditionalOnConsulEnabled;
import org.springframework.cloud.consul.ConsulProperties;
import org.springframework.cloud.netflix.eureka.EurekaClientAutoConfiguration;
import org.springframework.cloud.zookeeper.ConditionalOnZookeeperEnabled;
import org.springframework.cloud.zookeeper.ZookeeperProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * discovery 自动配置
 * 
 * @author hlm
 */
@Configuration(proxyBeanMethods=false)
@ConditionalOnDiscoveryEnabled
@ConditionalOnClass(DiscoveryClient.class)
public class DiscoveryAutoConfiguration {

	private final Log log = LogFactory.getLog(DiscoveryAutoConfiguration.class);

	@Configuration(proxyBeanMethods=false)
	@ConditionalOnZookeeperEnabled
	@ConditionalOnClass(ZookeeperProperties.class)
	public class ZookeeperDiscoveryAutoConfiguration {

		@Bean
		public ZookeeperDiscoveryMetadataCustomizer zookeeperDiscoveryMetadataCustomizer(
				@Autowired ObjectProvider<DiscoveryMetadataConfigurer> objectProvider) {
			log.info(String.format("Initialize bean %s.", ZookeeperDiscoveryMetadataCustomizer.class));
			List<DiscoveryMetadataConfigurer> configurers = objectProvider.orderedStream().collect(Collectors.toList());
			return new ZookeeperDiscoveryMetadataCustomizer(configurers);
		}

	}

	@Configuration(proxyBeanMethods=false)
	@ConditionalOnConsulEnabled
	@ConditionalOnClass(ConsulProperties.class)
	public class ConsulDiscoveryAutoConfiguration {

		@Bean
		public ConsulDiscoveryMetadataCustomizer consulDiscoveryMetadataCustomizer(
				@Autowired ObjectProvider<DiscoveryMetadataConfigurer> objectProvider) {
			log.info(String.format("Initialize bean %s.", ConsulDiscoveryMetadataCustomizer.class));
			List<DiscoveryMetadataConfigurer> configurers = objectProvider.orderedStream().collect(Collectors.toList());
			return new ConsulDiscoveryMetadataCustomizer(configurers);
		}

	}

	@Configuration(proxyBeanMethods=false)
	@ConditionalOnBean(EurekaClientAutoConfiguration.class)
	public class EurekaDiscoveryAutoConfiguration {

		@Bean
		public EurekaDiscoveryMetadataCustomizer eurekaDiscoveryMetadataCustomizer(
				@Autowired ObjectProvider<DiscoveryMetadataConfigurer> objectProvider) {
			log.info(String.format("Initialize bean %s.", EurekaDiscoveryMetadataCustomizer.class));
			List<DiscoveryMetadataConfigurer> configurers = objectProvider.orderedStream().collect(Collectors.toList());
			return new EurekaDiscoveryMetadataCustomizer(configurers);
		}

	}

}
