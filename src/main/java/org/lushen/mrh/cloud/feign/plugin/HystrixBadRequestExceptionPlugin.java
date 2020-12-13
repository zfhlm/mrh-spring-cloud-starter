package org.lushen.mrh.cloud.feign.plugin;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.lushen.mrh.boot.autoconfigure.support.error.GenericStatus;
import org.lushen.mrh.boot.autoconfigure.support.view.GenericResult;
import org.lushen.mrh.boot.autoconfigure.webmvc.advice.ExceptionPlugin;
import org.lushen.mrh.cloud.feign.FeignErrorBody;
import org.lushen.mrh.cloud.feign.client.FeignHystrixErrorDecoder.FeignHystrixBadRequestException;

import com.netflix.hystrix.exception.HystrixBadRequestException;

/**
 * {@link HystrixBadRequestException}
 * 
 * @author hlm
 */
public class HystrixBadRequestExceptionPlugin implements ExceptionPlugin {

	private final Log log = LogFactory.getLog(getClass().getSimpleName());

	@Override
	public boolean supports(Throwable cause) {
		return cause instanceof HystrixBadRequestException;
	}

	@Override
	public GenericResult handle(Throwable cause) {
		log.error(cause.getMessage(), cause);
		if(cause instanceof FeignHystrixBadRequestException) {
			FeignErrorBody feignErrorBody = ((FeignHystrixBadRequestException)cause).getFeignErrorBody();
			FeignErrorBody.Error error = feignErrorBody.getError();
			return new GenericResult(error.getErrcode(), error.getErrmsg());
		} else {
			GenericStatus status = GenericStatus.BAD_REQUEST;
			return new GenericResult(status.getErrcode(), status.getErrmsg());
		}
	}

}
