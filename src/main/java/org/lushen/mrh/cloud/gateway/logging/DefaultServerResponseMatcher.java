package org.lushen.mrh.cloud.gateway.logging;

import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;

/**
 * 默认实现
 * 
 * @author hlm
 */
public class DefaultServerResponseMatcher implements ServerResponseMatcher {

	@Override
	public boolean matches(ServerHttpRequest serverHttpRequest, ServerHttpResponse serverHttpResponse) {

		// 不打印无body或超大body响应
		long contentLength = serverHttpResponse.getHeaders().getContentLength();
		if(contentLength <= 0 || contentLength > Integer.MAX_VALUE) {
			return false;
		}

		// 根据 Content-Type 确定是否打印
		MediaType mediaType = serverHttpResponse.getHeaders().getContentType();
		if(mediaType == null) {
			return false;
		} else {
			return MediaType.APPLICATION_JSON.includes(mediaType) || MediaType.APPLICATION_FORM_URLENCODED.includes(mediaType) || MediaType.APPLICATION_XML.includes(mediaType);
		}

	}

}
