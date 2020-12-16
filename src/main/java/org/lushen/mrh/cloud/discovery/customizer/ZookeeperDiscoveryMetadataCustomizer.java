package org.lushen.mrh.cloud.discovery.customizer;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.lushen.mrh.cloud.discovery.DiscoveryMetadataConfigurer;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.cloud.zookeeper.discovery.ZookeeperDiscoveryProperties;

/**
 * discovery metadata 自动注册
 * 
 * @author hlm
 */
public class ZookeeperDiscoveryMetadataCustomizer implements BeanPostProcessor {

	private final Log log = LogFactory.getLog(ZookeeperDiscoveryMetadataCustomizer.class);

	private List<DiscoveryMetadataConfigurer> configurers;

	public ZookeeperDiscoveryMetadataCustomizer(List<DiscoveryMetadataConfigurer> configurers) {
		super();
		this.configurers = Optional.ofNullable(configurers).orElse(Collections.emptyList());
	}

	@Override
	public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {

		if(bean instanceof ZookeeperDiscoveryProperties) {

			ZookeeperDiscoveryProperties properties = (ZookeeperDiscoveryProperties)bean;

			Map<String, String> metadatas = new LinkedHashMap<String, String>();
			if(properties.getMetadata() != null) {
				metadatas.putAll(properties.getMetadata());
			}
			for(DiscoveryMetadataConfigurer configurer : this.configurers) {
				Map<String, String> registry = new LinkedHashMap<String, String>();
				configurer.addMetadatas(registry);
				if( ! registry.isEmpty() ) {
					log.info("AutoRegister zookeeper discovery metadatas : " + registry);
					metadatas.putAll(metadatas);
				}
			}

			properties.setMetadata(metadatas);

		}

		return bean;

	}

}
