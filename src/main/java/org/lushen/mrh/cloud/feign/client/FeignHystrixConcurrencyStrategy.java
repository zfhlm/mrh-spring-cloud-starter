package org.lushen.mrh.cloud.feign.client;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

import com.netflix.hystrix.HystrixThreadPoolKey;
import com.netflix.hystrix.HystrixThreadPoolProperties;
import com.netflix.hystrix.strategy.concurrency.HystrixConcurrencyStrategy;
import com.netflix.hystrix.strategy.concurrency.HystrixRequestVariable;
import com.netflix.hystrix.strategy.concurrency.HystrixRequestVariableLifecycle;
import com.netflix.hystrix.strategy.properties.HystrixProperty;

/**
 * feign hystrix 并发策略，线程池策略传递 {@link RequestAttributes} 
 * 
 * @author hlm
 */
public class FeignHystrixConcurrencyStrategy extends HystrixConcurrencyStrategy {

	private HystrixConcurrencyStrategy delegateStrategy;

	public FeignHystrixConcurrencyStrategy(HystrixConcurrencyStrategy delegateStrategy) {
		this.delegateStrategy = delegateStrategy;
	}

	@Override
	public BlockingQueue<Runnable> getBlockingQueue(int maxQueueSize) {
		if(this.delegateStrategy != null) {
			return this.delegateStrategy.getBlockingQueue(maxQueueSize);
		} else {
			return super.getBlockingQueue(maxQueueSize);
		}
	}

	@Override
	public <T> HystrixRequestVariable<T> getRequestVariable(HystrixRequestVariableLifecycle<T> rv) {
		if(this.delegateStrategy != null) {
			return this.delegateStrategy.getRequestVariable(rv);
		} else {
			return super.getRequestVariable(rv);
		}
	}

	@Override
	public ThreadPoolExecutor getThreadPool(HystrixThreadPoolKey threadPoolKey, HystrixProperty<Integer> corePoolSize, HystrixProperty<Integer> maximumPoolSize, HystrixProperty<Integer> keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue) {
		if(this.delegateStrategy != null) {
			return this.delegateStrategy.getThreadPool(threadPoolKey, corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue);
		} else {
			return super.getThreadPool(threadPoolKey, corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue);
		}
	}

	@Override
	public ThreadPoolExecutor getThreadPool(HystrixThreadPoolKey threadPoolKey, HystrixThreadPoolProperties threadPoolProperties) {
		if(this.delegateStrategy != null) {
			return this.delegateStrategy.getThreadPool(threadPoolKey, threadPoolProperties);
		} else {
			return super.getThreadPool(threadPoolKey, threadPoolProperties);
		}
	}

	@Override
	public <T> Callable<T> wrapCallable(Callable<T> callable) {
		RequestAttributes attributes = RequestContextHolder.getRequestAttributes();
		Callable<T> delegate = new FeignHystrixCallable<T>(callable, attributes);
		if(this.delegateStrategy != null) {
			return this.delegateStrategy.wrapCallable(delegate);
		} else {
			return super.wrapCallable(delegate);
		}
	}

	private class FeignHystrixCallable<T> implements Callable<T> {

		private Callable<T> delegate;

		private RequestAttributes attributes;

		private FeignHystrixCallable(Callable<T> delegate, RequestAttributes attributes) {
			super();
			this.delegate = delegate;
			this.attributes = attributes;
		}

		@Override
		public T call() throws Exception {
			try {
				RequestContextHolder.setRequestAttributes(this.attributes);
				return this.delegate.call();
			} finally {
				RequestContextHolder.resetRequestAttributes();
			}
		}

	}

}
