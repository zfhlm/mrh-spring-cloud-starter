package org.lushen.mrh.cloud.discovery.processor;

import java.util.ArrayList;
import java.util.List;

import org.lushen.mrh.cloud.discovery.DiscoveryPropertiesCustomizer;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.cloud.consul.discovery.ConsulDiscoveryProperties;

/**
 * consul customizer 处理器
 * 
 * @author hlm
 */
public class ConsulCustomizerProcessor implements BeanPostProcessor {

	@Autowired(required=false)
	private List<DiscoveryPropertiesCustomizer<ConsulDiscoveryProperties>> customizers = new ArrayList<DiscoveryPropertiesCustomizer<ConsulDiscoveryProperties>>();

	@Override
	public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
		if(bean instanceof ConsulDiscoveryProperties) {
			ConsulDiscoveryProperties properties = (ConsulDiscoveryProperties)bean;
			this.customizers.forEach(customizer -> customizer.customize(properties));
		}
		return bean;
	}

}
