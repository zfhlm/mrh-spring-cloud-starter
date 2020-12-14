package org.lushen.mrh.cloud.feign.fallback;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * 动态代理实现，直接抛出fallback异常
 * 
 * @author hlm
 * @param <T>
 */
@SuppressWarnings("unchecked")
public class IFallbackProxyProvider<T> implements IFallbackProvider<T> {

	private Class<T> target;

	public IFallbackProxyProvider(Class<T> target) {
		super();
		this.target = target;
	}

	@Override
	public T create(Throwable cause) {
		return (T)Proxy.newProxyInstance(getClass().getClassLoader(), new Class<?>[]{this.target}, new InvocationHandler() {
			@Override
			public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
				throw cause;
			}
		});
	}

}
