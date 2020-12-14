package org.lushen.mrh.cloud.discovery;

import java.util.Map;

/**
 * discovery metadata 配置接口
 * 
 * @author hlm
 */
public interface DiscoveryMetadataConfigurer {

	/**
	 * 添加 metadata
	 * 
	 * @param registry
	 */
	public void addMetadatas(Map<String, String> registry);

}
