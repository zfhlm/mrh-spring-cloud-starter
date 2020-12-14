package org.lushen.mrh.cloud.discovery;

import java.util.List;
import java.util.stream.Collectors;

import org.lushen.mrh.cloud.discovery.customizer.ConsulDiscoveryMetadataCustomizer;
import org.lushen.mrh.cloud.discovery.customizer.ZookeeperDiscoveryMetadataCustomizer;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ConditionalOnDiscoveryEnabled;
import org.springframework.cloud.consul.discovery.ConditionalOnConsulDiscoveryEnabled;
import org.springframework.cloud.zookeeper.discovery.ConditionalOnZookeeperDiscoveryEnabled;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * discovery 自动配置
 * 
 * @author hlm
 */
@Configuration(proxyBeanMethods=false)
@ConditionalOnDiscoveryEnabled
public class DiscoveryAutoConfiguration {

	@Configuration(proxyBeanMethods=false)
	@ConditionalOnZookeeperDiscoveryEnabled
	public static class ZookeeperDiscoveryAutoConfiguration {

		@Bean
		public ZookeeperDiscoveryMetadataCustomizer zookeeperDiscoveryMetadataCustomizer(
				@Autowired ObjectProvider<DiscoveryMetadataConfigurer> objectProvider) {
			List<DiscoveryMetadataConfigurer> configurers = objectProvider.orderedStream().collect(Collectors.toList());
			return new ZookeeperDiscoveryMetadataCustomizer(configurers);
		}

	}

	@Configuration(proxyBeanMethods=false)
	@ConditionalOnConsulDiscoveryEnabled
	public static class ConsulDiscoveryAutoConfiguration {

		@Bean
		public ConsulDiscoveryMetadataCustomizer consulDiscoveryMetadataCustomizer(
				@Autowired ObjectProvider<DiscoveryMetadataConfigurer> objectProvider) {
			List<DiscoveryMetadataConfigurer> configurers = objectProvider.orderedStream().collect(Collectors.toList());
			return new ConsulDiscoveryMetadataCustomizer(configurers);
		}

	}

}
