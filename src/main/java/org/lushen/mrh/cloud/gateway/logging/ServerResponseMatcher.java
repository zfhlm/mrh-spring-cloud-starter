package org.lushen.mrh.cloud.gateway.logging;

import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;

/**
 * {@link ServerHttpResponse} 匹配接口
 * 
 * @author hlm
 */
public interface ServerResponseMatcher {

	/**
	 * 是否匹配
	 * 
	 * @param serverHttpRequest
	 * @param serverHttpResponse
	 * @return
	 */
	public boolean matches(ServerHttpRequest serverHttpRequest, ServerHttpResponse serverHttpResponse);

}
