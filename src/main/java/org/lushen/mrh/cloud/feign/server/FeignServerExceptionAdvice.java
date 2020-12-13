package org.lushen.mrh.cloud.feign.server;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.lushen.mrh.boot.autoconfigure.support.view.GenericResult;
import org.lushen.mrh.boot.autoconfigure.support.view.ValidationResult;
import org.lushen.mrh.boot.autoconfigure.webmvc.advice.ExceptionAdvice;
import org.lushen.mrh.boot.autoconfigure.webmvc.advice.ExceptionPluginAdvice;
import org.lushen.mrh.cloud.feign.FeignContext;
import org.lushen.mrh.cloud.feign.FeignErrorBody;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import feign.Request.HttpMethod;

/**
 * open-feign 异常处理切面
 * 
 * @author hlm
 */
public class FeignServerExceptionAdvice extends ExceptionPluginAdvice implements ExceptionAdvice {

	@Autowired
	private ApplicationContext applicationContext;

	@Override
	public ResponseEntity<byte[]> errorHandler(Throwable cause, HttpServletRequest request, HttpServletResponse response) throws Exception {

		// 非feign调用
		if( ! FeignContext.isFeignRequest(request) ) {
			return super.errorHandler(cause, request, response);
		}

		// 转换为状态码对象
		GenericResult result = this.registry.getPluginFor(cause).orElseThrow(RuntimeException::new).handle(cause);

		// 生成负载信息
		FeignErrorBody.Error error = new FeignErrorBody.Error();
		error.setErrcode(result.getErrcode());
		error.setErrmsg(result.getErrmsg());
		if(result instanceof ValidationResult) {
			error.setPayload(((ValidationResult)result).getMessages());
		}

		// 生成响应body
		FeignErrorBody body = new FeignErrorBody();
		body.setServiceId(this.applicationContext.getApplicationName());
		body.setMethod(HttpMethod.valueOf(StringUtils.upperCase(request.getMethod())));
		body.setRequestPath(request.getRequestURI());
		body.setError(error);

		// 响应body状态码500，feign客户端接收为 feign.FeignException.InternalServerError
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.ALL);
		headers.setAcceptCharset(Arrays.asList(StandardCharsets.UTF_8));
		return new ResponseEntity<byte[]>(this.objectMapper.writeValueAsBytes(body), headers, HttpStatus.INTERNAL_SERVER_ERROR);
	}

}
