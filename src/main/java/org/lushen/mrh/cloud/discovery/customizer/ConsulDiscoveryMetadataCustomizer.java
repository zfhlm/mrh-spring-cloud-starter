package org.lushen.mrh.cloud.discovery.customizer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.lushen.mrh.cloud.discovery.DiscoveryMetadataConfigurer;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.cloud.consul.discovery.ConsulDiscoveryProperties;

/**
 * discovery metadata 自动注册
 * 
 * @author hlm
 */
public class ConsulDiscoveryMetadataCustomizer implements BeanPostProcessor {

	private final Log log = LogFactory.getLog(ConsulDiscoveryMetadataCustomizer.class);

	private List<DiscoveryMetadataConfigurer> configurers;

	public ConsulDiscoveryMetadataCustomizer(List<DiscoveryMetadataConfigurer> configurers) {
		super();
		this.configurers = Optional.ofNullable(configurers).orElse(Collections.emptyList());
	}

	@Override
	public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {

		if(bean instanceof ConsulDiscoveryProperties) {

			ConsulDiscoveryProperties properties = (ConsulDiscoveryProperties)bean;

			Set<String> tags = new LinkedHashSet<String>();
			if(properties.getTags() != null) {
				tags.addAll(properties.getTags());
			}
			for(DiscoveryMetadataConfigurer configurer : this.configurers) {
				Map<String, String> registry = new HashMap<String, String>();
				configurer.addMetadatas(registry);
				if( ! registry.isEmpty() ) {
					log.info("AutoRegister consul discovery metadatas : " + registry);
				}
				registry.forEach((name, value) -> tags.add(StringUtils.join(name, "=", value)));
			}

			properties.setTags(new ArrayList<String>(tags));

		}

		return bean;

	}

}
