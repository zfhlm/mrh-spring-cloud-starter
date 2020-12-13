package org.lushen.mrh.cloud.discovery;

import org.lushen.mrh.cloud.discovery.processor.ConsulCustomizerProcessor;
import org.lushen.mrh.cloud.discovery.processor.ZookeeperCustomizerProcessor;
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
		public ZookeeperCustomizerProcessor zookeeperCustomizerProcessor() {
			return new ZookeeperCustomizerProcessor();
		}

	}

	@Configuration(proxyBeanMethods=false)
	@ConditionalOnConsulDiscoveryEnabled
	public static class ConsulDiscoveryAutoConfiguration {

		@Bean
		public ConsulCustomizerProcessor consulCustomizerProcessor() {
			return new ConsulCustomizerProcessor();
		}

	}

}
