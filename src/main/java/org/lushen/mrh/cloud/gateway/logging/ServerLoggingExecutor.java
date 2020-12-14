package org.lushen.mrh.cloud.gateway.logging;

import org.springframework.http.HttpRequest;

/**
 * 日志打印接口
 * 
 * @author hlm
 */
public interface ServerLoggingExecutor {

	/**
	 * 打印请求基本信息
	 * 
	 * @param request
	 */
	public void line(HttpRequest request);

	/**
	 * 打印请求body
	 * 
	 * @param requestBody
	 */
	public void request(byte[] requestBody);

	/**
	 * 打印响应body
	 * 
	 * @param responseBody
	 */
	public void response(byte[] responseBody);

}
