package org.lushen.mrh.cloud.feign;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.lushen.mrh.boot.autoconfigure.webmvc.advice.ExceptionAdvice;
import org.lushen.mrh.boot.autoconfigure.webmvc.advice.ResponseAdvice;
import org.lushen.mrh.cloud.feign.client.FeignClientErrorDecoder;
import org.lushen.mrh.cloud.feign.client.FeignClientRequestInterceptor;
import org.lushen.mrh.cloud.feign.client.FeignHystrixConcurrencyStrategy;
import org.lushen.mrh.cloud.feign.client.FeignHystrixErrorDecoder;
import org.lushen.mrh.cloud.feign.fallback.ICommandKeySetterFactory;
import org.lushen.mrh.cloud.feign.plugin.HystrixBadRequestExceptionPlugin;
import org.lushen.mrh.cloud.feign.plugin.HystrixRuntimeExceptionPlugin;
import org.lushen.mrh.cloud.feign.plugin.HystrixTimeoutExceptionPlugin;
import org.lushen.mrh.cloud.feign.server.FeignServerExceptionAdvice;
import org.lushen.mrh.cloud.feign.server.FeignServerResponseAdvice;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.cloud.openfeign.FeignClientProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.plugin.core.Plugin;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import com.netflix.hystrix.strategy.HystrixPlugins;
import com.netflix.hystrix.strategy.concurrency.HystrixConcurrencyStrategy;
import com.netflix.hystrix.strategy.eventnotifier.HystrixEventNotifier;
import com.netflix.hystrix.strategy.executionhook.HystrixCommandExecutionHook;
import com.netflix.hystrix.strategy.metrics.HystrixMetricsPublisher;
import com.netflix.hystrix.strategy.properties.HystrixPropertiesStrategy;

import feign.RequestInterceptor;
import feign.codec.ErrorDecoder;

/**
 * feign 自动配置
 * 
 * @author hlm
 */
@Configuration(proxyBeanMethods=false)
public class FeignAutoConfiguration {

	private final Log log = LogFactory.getLog(getClass());

	/**
	 * feign 服务端配置
	 * 
	 * @author hlm
	 */
	@Configuration(proxyBeanMethods=false)
	@ConditionalOnWebApplication
	@ConditionalOnClass({ResponseBodyAdvice.class, ControllerAdvice.class, Plugin.class})
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
	@ConditionalOnClass(Plugin.class)
	@ConditionalOnBean(FeignClientProperties.class)
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
	@ConditionalOnBean(FeignClientAutoConfiguration.class)
	@ConditionalOnProperty(name = "feign.hystrix.enabled")
	public class FeignHystrixAutoConfiguration implements InitializingBean {

		@Override
		public void afterPropertiesSet() throws Exception {
			try {
				log.info(String.format("Register feign hystrix concurrency strategy bean %s.", FeignHystrixConcurrencyStrategy.class));
				HystrixConcurrencyStrategy delegateStrategy = HystrixPlugins.getInstance().getConcurrencyStrategy();
				if (  ! (delegateStrategy instanceof FeignHystrixConcurrencyStrategy) ) {
					HystrixCommandExecutionHook commandExecutionHook = HystrixPlugins.getInstance().getCommandExecutionHook();
					HystrixEventNotifier eventNotifier = HystrixPlugins.getInstance().getEventNotifier();
					HystrixMetricsPublisher metricsPublisher = HystrixPlugins.getInstance().getMetricsPublisher();
					HystrixPropertiesStrategy propertiesStrategy = HystrixPlugins.getInstance().getPropertiesStrategy();
					HystrixPlugins.reset();
					HystrixPlugins.getInstance().registerConcurrencyStrategy(new FeignHystrixConcurrencyStrategy(delegateStrategy));
					HystrixPlugins.getInstance().registerCommandExecutionHook(commandExecutionHook);
					HystrixPlugins.getInstance().registerEventNotifier(eventNotifier);
					HystrixPlugins.getInstance().registerMetricsPublisher(metricsPublisher);
					HystrixPlugins.getInstance().registerPropertiesStrategy(propertiesStrategy);
				}
			} catch (Exception e) {
				log.error(String.format("Fail to register feign hystrix concurrency strategy bean %s.", FeignHystrixConcurrencyStrategy.class));
				throw e;
			}
		}

		@Bean
		public ICommandKeySetterFactory iCommandKeySetterFactory() {
			log.info(String.format("Initialize bean %s.", ICommandKeySetterFactory.class));
			return new ICommandKeySetterFactory();
		}

		@Bean
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
