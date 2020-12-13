package org.lushen.mrh.cloud.discovery;

/**
 * 服务注册发现配置 customizer
 * 
 * @author hlm
 * @param <T>
 */
public interface DiscoveryPropertiesCustomizer<T> {

	public void customize(T properties);

}
