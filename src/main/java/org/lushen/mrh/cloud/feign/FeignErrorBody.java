package org.lushen.mrh.cloud.feign;

import feign.Request.HttpMethod;

/**
 * open-feign 调用服务异常信息
 * 
 * @author hlm
 */
public class FeignErrorBody {

	private String serviceId;		// 请求服务

	private HttpMethod method;		// 请求方法

	private String requestPath;		// 请求路径

	private byte[] requestBody;		// 请求参数

	private Error error;			// 负载信息

	public String getServiceId() {
		return serviceId;
	}

	public void setServiceId(String serviceId) {
		this.serviceId = serviceId;
	}

	public HttpMethod getMethod() {
		return method;
	}

	public void setMethod(HttpMethod method) {
		this.method = method;
	}

	public String getRequestPath() {
		return requestPath;
	}

	public void setRequestPath(String requestPath) {
		this.requestPath = requestPath;
	}

	public byte[] getRequestBody() {
		return requestBody;
	}

	public void setRequestBody(byte[] requestBody) {
		this.requestBody = requestBody;
	}

	public Error getError() {
		return error;
	}

	public void setError(Error error) {
		this.error = error;
	}

	public static class Error {

		private int errcode;		// 业务错误码

		private String errmsg;		// 业务错误信息

		private Object payload;		// 业务负载信息

		public int getErrcode() {
			return errcode;
		}

		public void setErrcode(int errcode) {
			this.errcode = errcode;
		}

		public String getErrmsg() {
			return errmsg;
		}

		public void setErrmsg(String errmsg) {
			this.errmsg = errmsg;
		}

		public Object getPayload() {
			return payload;
		}

		public void setPayload(Object payload) {
			this.payload = payload;
		}

	}

}
