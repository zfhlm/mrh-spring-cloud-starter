package org.lushen.mrh.cloud.gateway.logging;

import org.springframework.http.server.reactive.ServerHttpRequest;

/**
 * {@link ServerHttpRequest} 匹配接口
 * 
 * @author hlm
 */
public interface ServerRequestMatcher {

	/**
	 * 是否匹配
	 * 
	 * @param serverHttpRequest
	 * @return
	 */
	public boolean matches(ServerHttpRequest serverHttpRequest);

}
