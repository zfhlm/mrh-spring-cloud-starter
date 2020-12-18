package org.lushen.mrh.cloud.feign.fallback;

import java.lang.reflect.Method;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.core.annotation.AnnotationUtils;

import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommand.Setter;
import com.netflix.hystrix.HystrixCommandGroupKey;
import com.netflix.hystrix.HystrixCommandKey;

import feign.Target;
import feign.hystrix.HystrixFeign;
import feign.hystrix.SetterFactory;

/**
 * 自定义 CommandKey 生成规则，如果方法存在 {@link ICommandKey} 注解，使用注解的值作为 CommandKey
 * 
 * @author hlm
 */
public class ICommandKeySetterFactory implements SetterFactory, BeanPostProcessor {

	private SetterFactory delegate = new SetterFactory.Default();

	@Override
	public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
		if(bean instanceof HystrixFeign.Builder) {
			((HystrixFeign.Builder)bean).setterFactory(this);
		}
		return bean;
	}

	@Override
	public Setter create(Target<?> target, Method method) {
		ICommandKey annotation = AnnotationUtils.findAnnotation(method, ICommandKey.class);
		if(annotation != null && StringUtils.isNotBlank(annotation.value())) {
			return HystrixCommand.Setter
					.withGroupKey(HystrixCommandGroupKey.Factory.asKey(target.name()))
					.andCommandKey(HystrixCommandKey.Factory.asKey(annotation.value()));
		} else {
			return this.delegate.create(target, method);
		}
	}

}
