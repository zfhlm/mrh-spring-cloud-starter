package org.lushen.mrh.cloud.feign;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.lushen.mrh.boot.autoconfigure.webmvc.advice.ExceptionAdvice;
import org.lushen.mrh.boot.autoconfigure.webmvc.advice.ResponseAdvice;
import org.lushen.mrh.cloud.feign.client.FeignClientErrorDecoder;
import org.lushen.mrh.cloud.feign.client.FeignClientRequestInterceptor;
import org.lushen.mrh.cloud.feign.client.FeignHystrixErrorDecoder;
import org.lushen.mrh.cloud.feign.plugin.HystrixBadRequestExceptionPlugin;
import org.lushen.mrh.cloud.feign.plugin.HystrixRuntimeExceptionPlugin;
import org.lushen.mrh.cloud.feign.plugin.HystrixTimeoutExceptionPlugin;
import org.lushen.mrh.cloud.feign.server.FeignServerExceptionAdvice;
import org.lushen.mrh.cloud.feign.server.FeignServerResponseAdvice;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;

import feign.Feign;
import feign.RequestInterceptor;
import feign.codec.ErrorDecoder;
import feign.hystrix.HystrixFeign;

/**
 * feign 自动配置
 * 
 * @author hlm
 */
@Configuration(proxyBeanMethods=false)
@ConditionalOnClass(Feign.class)
public class FeignAutoConfiguration {

	private final Log log = LogFactory.getLog(getClass());

	/**
	 * feign 服务端配置
	 * 
	 * @author hlm
	 */
	@Configuration(proxyBeanMethods=false)
	public class FeignServerAutoConfiguration {

		/**
		 * 注册服务异常处理器
		 */
		@Bean
		public ExceptionAdvice feignServerExceptionAdvice() {
			log.info(String.format("Initialize bean %s.", FeignServerExceptionAdvice.class));
			return new FeignServerExceptionAdvice();
		}

		/**
		 * 注册服务响应切面
		 */
		@Bean
		public ResponseAdvice feignServerResponseAdvice() {
			log.info(String.format("Initialize bean %s.", FeignServerResponseAdvice.class));
			return new FeignServerResponseAdvice();
		}

	}

	/**
	 * feign 客户端配置
	 * 
	 * @author hlm
	 */
	@Configuration(proxyBeanMethods=false)
	public class FeignClientAutoConfiguration {

		/**
		 * 注册请求拦截器
		 */
		@Bean
		public RequestInterceptor feignClientRequestInterceptor() {
			log.info(String.format("Initialize bean %s.", FeignClientRequestInterceptor.class));
			return new FeignClientRequestInterceptor();
		}

		/**
		 * 注册请求异常解码器
		 */
		@Bean
		@ConditionalOnMissingBean(ErrorDecoder.class)
		public ErrorDecoder feignClientErrorDecoder() {
			log.info(String.format("Initialize bean %s.", FeignClientErrorDecoder.class));
			return new FeignClientErrorDecoder();
		}

	}

	/**
	 * feign hystrix 配置
	 * 
	 * @author hlm
	 */
	@Configuration(proxyBeanMethods=false)
	@ConditionalOnBean(HystrixFeign.Builder.class)
	public class FeignHystrixAutoConfiguration {

		@Bean
		@Order(Ordered.HIGHEST_PRECEDENCE)
		@ConditionalOnMissingBean(ErrorDecoder.class)
		public ErrorDecoder feignHystrixErrorDecoder() {
			log.info(String.format("Initialize bean %s.", FeignHystrixErrorDecoder.class));
			return new FeignHystrixErrorDecoder();
		}

		@Bean
		public HystrixBadRequestExceptionPlugin hystrixBadRequestExceptionPlugin() {
			return new HystrixBadRequestExceptionPlugin();
		}

		@Bean
		public HystrixRuntimeExceptionPlugin hystrixRuntimeExceptionPlugin() {
			return new HystrixRuntimeExceptionPlugin();
		}

		@Bean
		public HystrixTimeoutExceptionPlugin hystrixTimeoutExceptionPlugin() {
			return new HystrixTimeoutExceptionPlugin();
		}

	}

}
