package org.lushen.mrh.cloud.gateway.logging;

import java.util.Map;

import org.springframework.web.server.ServerWebExchange;

/**
 * {@link ServerWebExchange#getAttributes()} 匹配接口
 * 
 * @author hlm
 */
public interface ServerAttributeMatcher {

	/**
	 * 是否匹配
	 * 
	 * @param attributes
	 * @return
	 */
	public boolean matches(Map<String, Object> attributes);

}
