package org.lushen.mrh.cloud.feign.fallback;

import java.util.Arrays;
import java.util.Optional;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.ResolvableType;
import org.springframework.core.annotation.AnnotationUtils;

import feign.hystrix.FallbackFactory;

/**
 * feign fallback factory
 * 
 * @author hlm
 * @param <T>
 */
@SuppressWarnings("unchecked")
public abstract class IFallbackFactory<T> implements FallbackFactory<T>, InitializingBean, ApplicationContextAware {

	protected final Log log = LogFactory.getLog(getClass());

	private ApplicationContext applicationContext;

	private IFallbackProvider<T> iFallbackProvider;

	@Override
	public T create(Throwable cause) {
		return iFallbackProvider.create(cause);
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}

	@Override
	public void afterPropertiesSet() throws Exception {

		// 获取当前泛型类型
		Class<T> target = (Class<T>)Arrays.stream(ResolvableType.forClass(getClass()).getSuperType().resolveGenerics()).findFirst().orElse(null);
		if(target == null) {
			throw new IllegalArgumentException("Generic type can't be found for class : " + getClass());
		}
		if( ! target.isInterface() ) {
			throw new IllegalArgumentException("Generic type is not an interface : " + target);
		}
		if(AnnotationUtils.findAnnotation(target, FeignClient.class) == null) {
			throw new IllegalArgumentException("Generic type is not an @FeignClient : " + target);
		}

		// 获取当前泛型的 IFallbackProvider
		ResolvableType beanType = ResolvableType.forClassWithGenerics(IFallbackProvider.class, target);
		this.iFallbackProvider = Optional.ofNullable((IFallbackProvider<T>)this.applicationContext.getBeanProvider(beanType).getIfAvailable())
				.orElseGet(() -> new IFallbackProxyProvider<T>(target));
		log.info(String.format("Feign hystrix fallback provider [%s] for client [%s]", AopUtils.getTargetClass(this.iFallbackProvider), target));

	}

}
