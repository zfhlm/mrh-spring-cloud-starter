package org.lushen.mrh.cloud.config;

import java.util.Arrays;
import java.util.List;

import org.aopalliance.aop.Advice;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.aop.ClassFilter;
import org.springframework.aop.MethodMatcher;
import org.springframework.aop.Pointcut;
import org.springframework.aop.PointcutAdvisor;
import org.springframework.cloud.config.environment.Environment;
import org.springframework.cloud.config.server.environment.CompositeEnvironmentRepository;
import org.springframework.cloud.config.server.environment.EnvironmentRepository;

/**
 * {@link EnvironmentRepository} bean 织入切面
 * 
 * @author hlm
 */
public class EnvronmentRepositoryBeanAdvisor implements MethodInterceptor, Pointcut, PointcutAdvisor, ClassFilter {

	private List<EnvironmentProcessor> processors;

	public EnvronmentRepositoryBeanAdvisor(EnvironmentProcessor processor) {
		this(Arrays.asList(processor));
	}

	public EnvronmentRepositoryBeanAdvisor(List<EnvironmentProcessor> processors) {
		super();
		this.processors = processors;
	}

	@Override
	public Object invoke(MethodInvocation invocation) throws Throwable {
		Object result = invocation.proceed();
		if(result instanceof Environment) {
			for(EnvironmentProcessor processor : this.processors) {
				result = processor.process((EnvironmentRepository)invocation.getThis(), (Environment)result);
			}
		}
		return result;
	}

	@Override
	public Advice getAdvice() {
		return this;
	}

	@Override
	public boolean isPerInstance() {
		return true;
	}

	@Override
	public Pointcut getPointcut() {
		return this;
	}

	@Override
	public ClassFilter getClassFilter() {
		return this;
	}

	@Override
	public MethodMatcher getMethodMatcher() {
		return MethodMatcher.TRUE;
	}

	@Override
	public boolean matches(Class<?> clazz) {
		if(CompositeEnvironmentRepository.class.isAssignableFrom(clazz)) {
			return false;
		}
		return EnvironmentRepository.class.isAssignableFrom(clazz);
	}

}
