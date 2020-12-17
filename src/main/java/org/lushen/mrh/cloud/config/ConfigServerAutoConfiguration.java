package org.lushen.mrh.cloud.config;

import java.util.stream.Collectors;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.cloud.config.server.config.ConfigServerConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * spring cloud config server 自动配置
 * 
 * @author hlm
 */
@Configuration(proxyBeanMethods=false)
@ConditionalOnBean(ConfigServerConfiguration.class)
@ConditionalOnClass({org.springframework.aop.Advisor.class, org.aspectj.weaver.Advice.class})
public class ConfigServerAutoConfiguration {

	private final Log log = LogFactory.getLog(getClass());

	/**
	 * 注册 config-locations 处理器
	 */
	@Bean
	public ConfigLocationsEnvironmentProcessor configLocationsEnvironmentProcessor() {
		log.info(String.format("Initialize bean %s.", ConfigLocationsEnvironmentProcessor.class));
		return new ConfigLocationsEnvironmentProcessor();
	}

	/**
	 * 织入处理器切面
	 */
	@Bean
	public EnvronmentRepositoryBeanAdvisor envronmentRepositoryBeanAdvisor(@Autowired ObjectProvider<EnvironmentProcessor> objectProvider) {
		log.info(String.format("Initialize bean %s.", EnvronmentRepositoryBeanAdvisor.class));
		return new EnvronmentRepositoryBeanAdvisor(objectProvider.orderedStream().collect(Collectors.toList()));
	}

}
