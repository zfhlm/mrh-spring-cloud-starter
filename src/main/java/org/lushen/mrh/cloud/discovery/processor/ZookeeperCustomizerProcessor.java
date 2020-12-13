package org.lushen.mrh.cloud.discovery.processor;

import java.util.ArrayList;
import java.util.List;

import org.lushen.mrh.cloud.discovery.DiscoveryPropertiesCustomizer;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.cloud.zookeeper.discovery.ZookeeperDiscoveryProperties;

/**
 * zookeeper customizer 处理器
 * 
 * @author hlm
 */
public class ZookeeperCustomizerProcessor implements BeanPostProcessor {

	@Autowired(required=false)
	private List<DiscoveryPropertiesCustomizer<ZookeeperDiscoveryProperties>> customizers = new ArrayList<DiscoveryPropertiesCustomizer<ZookeeperDiscoveryProperties>>();

	@Override
	public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
		if(bean instanceof ZookeeperDiscoveryProperties) {
			ZookeeperDiscoveryProperties properties = (ZookeeperDiscoveryProperties)bean;
			this.customizers.forEach(customizer -> customizer.customize(properties));
		}
		return bean;
	}

}
