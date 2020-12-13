package org.lushen.mrh.cloud.feign.client;

import java.nio.ByteBuffer;

import org.lushen.mrh.boot.autoconfigure.support.error.GenericException;
import org.lushen.mrh.boot.autoconfigure.support.error.GenericPayloadException;
import org.lushen.mrh.boot.autoconfigure.support.error.GenericStatus;
import org.lushen.mrh.cloud.feign.FeignErrorBody;
import org.springframework.beans.factory.annotation.Autowired;

import com.fasterxml.jackson.databind.ObjectMapper;

import feign.FeignException.InternalServerError;
import feign.Response;
import feign.RetryableException;
import feign.codec.ErrorDecoder;

/**
 * open-feign 客户端异常解码器
 * 
 * @author hlm
 */
public class FeignClientErrorDecoder extends ErrorDecoder.Default {

	@Autowired
	private ObjectMapper objectMapper;

	@Override
	public Exception decode(String methodKey, Response response) {
		Exception cause = super.decode(methodKey, response);
		if(cause instanceof RetryableException) {
			return cause;
		}
		if(cause instanceof InternalServerError) {
			try {
				byte[] body = ((InternalServerError)cause).responseBody().map(ByteBuffer::array).orElse(null);
				FeignErrorBody feignErrorBody = this.objectMapper.readValue(body, FeignErrorBody.class);
				FeignErrorBody.Error error = feignErrorBody.getError();
				return new GenericPayloadException(new GenericStatus(error.getErrcode(), error.getErrmsg()), feignErrorBody, cause);
			} catch (Exception ex) {}
		}
		return new GenericException(GenericStatus.ERROR, cause);
	}

}
