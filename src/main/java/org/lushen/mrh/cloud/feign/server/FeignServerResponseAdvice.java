package org.lushen.mrh.cloud.feign.server;

import org.lushen.mrh.boot.autoconfigure.webmvc.advice.ResponseAdvice;
import org.lushen.mrh.boot.autoconfigure.webmvc.advice.ResponseRestAdvice;
import org.lushen.mrh.cloud.feign.FeignContext;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;

/**
 * feign 服务响应body切面
 * 
 * @author hlm
 */
public class FeignServerResponseAdvice extends ResponseRestAdvice implements ResponseAdvice {

	@Override
	public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType selectedContentType, Class<? extends HttpMessageConverter<?>> selectedConverterType, ServerHttpRequest request, ServerHttpResponse response) {
		// 如果是使用 feign 服务调用，不包装为状态码对象
		if(FeignContext.isFeignRequest(request)) {
			return body;
		} else {
			return super.beforeBodyWrite(body, returnType, selectedContentType, selectedConverterType, request, response);
		}
	}

}
