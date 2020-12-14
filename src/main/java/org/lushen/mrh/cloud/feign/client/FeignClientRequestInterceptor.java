package org.lushen.mrh.cloud.feign.client;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.lushen.mrh.boot.autoconfigure.support.deliver.GenericDeliverUtils;
import org.lushen.mrh.cloud.feign.FeignContext;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.context.request.WebRequest;

import feign.Feign;
import feign.RequestInterceptor;
import feign.RequestTemplate;

/**
 * feign 客户端请求拦截器
 * 
 * @author hlm
 */
public class FeignClientRequestInterceptor implements RequestInterceptor, BeanPostProcessor {

	private final Log log = LogFactory.getLog(getClass());

	@Override
	public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
		if(bean instanceof Feign.Builder) {
			((Feign.Builder)bean).requestInterceptor(this);
		}
		return bean;
	}

	@Override
	public void apply(RequestTemplate template) {

		// 获取所有 deliver header
		Map<String, Collection<String>> headers = new HashMap<String, Collection<String>>();
		try {
			RequestAttributes attributes = RequestContextHolder.getRequestAttributes();
			if(attributes instanceof ServletRequestAttributes) {
				HttpServletRequest request = ((ServletRequestAttributes)attributes).getRequest();
				headers.putAll(GenericDeliverUtils.collectDeliverHeader(request));
			}
			else if(attributes instanceof WebRequest) {
				headers.putAll(GenericDeliverUtils.collectDeliverHeader((WebRequest)attributes));
			}
		} catch (Exception e) {
			log.warn("Fail to reader http deliver headers, cause by " + e.getMessage());
		}

		// feign deliver header
		headers.put(FeignContext.REQUEST_DELIVER_FROM_FEIGN, Collections.singleton(FeignContext.REQUEST_DELIVER_FROM_FEIGN_VALUE));

		// 添加 deliver header 到请求
		template.headers(headers);

	}

}
