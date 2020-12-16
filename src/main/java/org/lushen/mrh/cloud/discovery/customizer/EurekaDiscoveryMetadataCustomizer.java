package org.lushen.mrh.cloud.discovery.customizer;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.lushen.mrh.cloud.discovery.DiscoveryMetadataConfigurer;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.cloud.netflix.eureka.EurekaInstanceConfigBean;

/**
 * discovery metadata 自动注册
 * 
 * @author hlm
 */
public class EurekaDiscoveryMetadataCustomizer implements BeanPostProcessor {

	private final Log log = LogFactory.getLog(EurekaDiscoveryMetadataCustomizer.class);

	private List<DiscoveryMetadataConfigurer> configurers;

	public EurekaDiscoveryMetadataCustomizer(List<DiscoveryMetadataConfigurer> configurers) {
		super();
		this.configurers = Optional.ofNullable(configurers).orElse(Collections.emptyList());
	}

	@Override
	public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {

		if(bean instanceof EurekaInstanceConfigBean) {

			EurekaInstanceConfigBean configBean = (EurekaInstanceConfigBean)bean;

			Map<String, String> metadatas = new LinkedHashMap<String, String>();
			if(configBean.getMetadataMap() != null) {
				metadatas.putAll(configBean.getMetadataMap());
			}
			for(DiscoveryMetadataConfigurer configurer : this.configurers) {
				Map<String, String> registry = new HashMap<String, String>();
				configurer.addMetadatas(registry);
				if( ! registry.isEmpty() ) {
					log.info("AutoRegister eureka discovery metadatas : " + registry);
					metadatas.putAll(registry);
				}
			}

			configBean.setMetadataMap(metadatas);

		}

		return bean;

	}

}
