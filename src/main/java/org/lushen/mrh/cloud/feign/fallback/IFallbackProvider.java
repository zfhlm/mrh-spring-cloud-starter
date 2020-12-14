package org.lushen.mrh.cloud.feign.fallback;

/**
 * feign fallback provider
 * 
 * @author hlm
 * @param <T>
 */
public interface IFallbackProvider<T> {

	/**
	 * 创建 fallback 服务实例
	 * 
	 * @param cause
	 * @return
	 */
	public T create(Throwable cause);

}
