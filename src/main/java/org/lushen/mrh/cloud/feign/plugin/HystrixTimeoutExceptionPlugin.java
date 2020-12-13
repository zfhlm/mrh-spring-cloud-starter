package org.lushen.mrh.cloud.feign.plugin;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.lushen.mrh.boot.autoconfigure.support.error.GenericStatus;
import org.lushen.mrh.boot.autoconfigure.support.view.GenericResult;
import org.lushen.mrh.boot.autoconfigure.webmvc.advice.ExceptionPlugin;

import com.netflix.hystrix.exception.HystrixTimeoutException;

/**
 * {@link HystrixTimeoutException}
 * 
 * @author hlm
 */
public class HystrixTimeoutExceptionPlugin implements ExceptionPlugin {

	private final Log log = LogFactory.getLog(getClass().getSimpleName());

	@Override
	public boolean supports(Throwable cause) {
		return cause instanceof HystrixTimeoutException;
	}

	@Override
	public GenericResult handle(Throwable cause) {
		log.error(cause.getMessage(), cause);
		GenericStatus status = GenericStatus.REQUEST_TIMEOUT;
		return new GenericResult(status.getErrcode(), status.getErrmsg());
	}

}
