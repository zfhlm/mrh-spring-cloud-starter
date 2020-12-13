package org.lushen.mrh.cloud.feign;

import java.util.function.Function;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.lushen.mrh.boot.autoconfigure.support.deliver.GenericDeliverHeaders;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.context.request.WebRequest;

/**
 * open-feign 请求上下文
 * 
 * @author hlm
 */
public final class FeignContext {

	// 请求头 name 用于标识feign请求
	public static final String REQUEST_DELIVER_FROM_FEIGN = GenericDeliverHeaders.REQUEST_DELIVER_HEADER_PREFIX + "From-Feign";

	// 请求头 value 用于标识feign请求
	public static final String REQUEST_DELIVER_FROM_FEIGN_VALUE = "true";

	private static final FeignContext FEIGN_CONTEXT = new FeignContext();

	/**
	 * 获取feign请求上下文对象
	 * 
	 * @return
	 */
	public static final FeignContext getContext() {
		return FEIGN_CONTEXT;
	}

	/**
	 * 是否feign请求，根据上下文识别
	 * 
	 * @return
	 */
	public boolean isFeignRequest() {
		RequestAttributes attributes = RequestContextHolder.getRequestAttributes();
		if(attributes instanceof ServletRequestAttributes) {
			return isFeignRequest(((ServletRequestAttributes)attributes).getRequest());
		}
		else if(attributes instanceof WebRequest) {
			return isFeignRequest((WebRequest)attributes);
		}
		return false;
	}

	/**
	 * 是否feign请求调用
	 * 
	 * @param request
	 * @return
	 */
	public static final boolean isFeignRequest(HttpServletRequest request) {
		return isFeignRequest(name -> request.getHeader(name));
	}

	/**
	 * 是否feign请求调用
	 * 
	 * @param request
	 * @return
	 */
	public static final boolean isFeignRequest(ServerHttpRequest request) {
		return isFeignRequest(name -> request.getHeaders().getFirst(name));
	}

	/**
	 * 是否feign请求调用
	 * 
	 * @param request
	 * @return
	 */
	public static final boolean isFeignRequest(WebRequest request) {
		return isFeignRequest(name -> request.getHeader(name));
	}

	/**
	 * 是否feign请求调用
	 * 
	 * @param valueForHeader
	 * @return
	 */
	private static final boolean isFeignRequest(Function<String, String> valueForHeader) {
		return StringUtils.equals(valueForHeader.apply(REQUEST_DELIVER_FROM_FEIGN), REQUEST_DELIVER_FROM_FEIGN_VALUE);
	}

}
