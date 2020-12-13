package org.lushen.mrh.cloud.feign.plugin;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.lushen.mrh.boot.autoconfigure.support.error.GenericStatus;
import org.lushen.mrh.boot.autoconfigure.support.view.GenericResult;
import org.lushen.mrh.boot.autoconfigure.webmvc.advice.ExceptionPlugin;

import com.netflix.hystrix.exception.HystrixRuntimeException;
import com.netflix.hystrix.exception.HystrixRuntimeException.FailureType;

/**
 * {@link HystrixRuntimeException}
 * 
 * @author hlm
 */
public class HystrixRuntimeExceptionPlugin implements ExceptionPlugin {

	private final Log log = LogFactory.getLog(getClass().getSimpleName());

	@Override
	public boolean supports(Throwable cause) {
		return cause instanceof HystrixRuntimeException;
	}

	@Override
	public GenericResult handle(Throwable cause) {
		log.error(cause.getMessage(), cause);
		FailureType failureType = ((HystrixRuntimeException)cause).getFailureType();
		if(failureType == FailureType.BAD_REQUEST_EXCEPTION) {
			GenericStatus status = GenericStatus.BAD_REQUEST;
			return new GenericResult(status.getErrcode(), status.getErrmsg());
		}
		else if(failureType == FailureType.TIMEOUT) {
			GenericStatus status = GenericStatus.REQUEST_TIMEOUT;
			return new GenericResult(status.getErrcode(), status.getErrmsg());
		}
		else {
			GenericStatus status = GenericStatus.BUSINESS;
			return new GenericResult(status.getErrcode(), status.getErrmsg());
		}
	}

}
