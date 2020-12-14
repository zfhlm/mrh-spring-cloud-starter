package org.lushen.mrh.cloud.feign.client;

import java.nio.ByteBuffer;

import org.lushen.mrh.cloud.feign.FeignErrorBody;
import org.springframework.beans.factory.annotation.Autowired;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.netflix.hystrix.exception.HystrixBadRequestException;

import feign.FeignException.InternalServerError;
import feign.Response;
import feign.codec.ErrorDecoder;

/**
 * feign hystrix 客户端异常解码器
 * 
 * @author hlm
 */
public class FeignHystrixErrorDecoder extends ErrorDecoder.Default {

	@Autowired
	private ObjectMapper objectMapper;

	@Override
	public Exception decode(String methodKey, Response response) {
		Exception cause = super.decode(methodKey, response);
		if(cause instanceof InternalServerError) {
			try {
				byte[] body = ((InternalServerError)cause).responseBody().map(ByteBuffer::array).orElse(null);
				FeignErrorBody feignErrorBody = this.objectMapper.readValue(body, FeignErrorBody.class);
				return new FeignHystrixBadRequestException(feignErrorBody, cause.getMessage(), cause);
			} catch (Exception ex) {}
		}
		return cause;
	}

	// 自定义 hystrix 异常
	public static class FeignHystrixBadRequestException extends HystrixBadRequestException {

		private static final long serialVersionUID = 7211412212850319446L;

		private FeignErrorBody feignErrorBody;

		public FeignHystrixBadRequestException(FeignErrorBody feignErrorBody, String message, Throwable cause) {
			super(message, cause);
			this.feignErrorBody = feignErrorBody;
		}

		public FeignHystrixBadRequestException(FeignErrorBody feignErrorBody, String message) {
			super(message);
			this.feignErrorBody = feignErrorBody;
		}

		public FeignErrorBody getFeignErrorBody() {
			return feignErrorBody;
		}

		@Override
		public String getMessage() {
			StringBuilder builder = new StringBuilder();
			builder.append("Feign hystrix bad request, message: ");
			builder.append(super.getMessage());
			builder.append(", body: ");
			builder.append(this.feignErrorBody);
			return builder.toString();
		}

	}

}
