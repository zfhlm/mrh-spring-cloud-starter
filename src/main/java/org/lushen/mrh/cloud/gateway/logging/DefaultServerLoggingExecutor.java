package org.lushen.mrh.cloud.gateway.logging;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.http.HttpRequest;

/**
 * 日志打印默认实现
 * 
 * @author hlm
 */
public class DefaultServerLoggingExecutor implements ServerLoggingExecutor {

	private final Log log = LogFactory.getLog("gateway");

	@Override
	public void line(HttpRequest request) {
		String method = request.getMethodValue();
		String path = request.getURI().getPath();
		String query = request.getURI().getRawQuery();
		if(query != null) {
			log.info(String.format("line：%s %s - %s", method, path, query));
		} else {
			log.info(String.format("line：%s %s", method, path));
		}
	}

	@Override
	public void request(byte[] requestBody) {
		log.info("request：" + new String(requestBody));
	}

	@Override
	public void response(byte[] responseBody) {
		log.info("response：" + new String(responseBody));
	}

}
