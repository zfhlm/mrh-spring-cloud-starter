package org.lushen.mrh.cloud.gateway;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.lushen.mrh.cloud.gateway.logging.DefaultServerAttributeMatcher;
import org.lushen.mrh.cloud.gateway.logging.DefaultServerLoggingExecutor;
import org.lushen.mrh.cloud.gateway.logging.ServerAttributeMatcher;
import org.lushen.mrh.cloud.gateway.logging.ServerLoggingExecutor;
import org.lushen.mrh.cloud.gateway.logging.ServerRequestMatcher;
import org.lushen.mrh.cloud.gateway.logging.ServerResponseMatcher;
import org.reactivestreams.Publisher;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.NettyWriteResponseFilter;
import org.springframework.cloud.gateway.filter.factory.rewrite.CachedBodyOutputMessage;
import org.springframework.cloud.gateway.support.BodyInserterContext;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.codec.HttpMessageReader;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpRequestDecorator;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.http.server.reactive.ServerHttpResponseDecorator;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.HandlerStrategies;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.server.ServerWebExchange;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * 日志打印过滤器
 * 
 * @author hlm
 */
public class LoggingGatewayFilter implements GatewayFilter, Ordered {

	private final List<HttpMessageReader<?>> messageReaders = HandlerStrategies.withDefaults().messageReaders();

	private ServerAttributeMatcher attributeMatcher;

	private ServerRequestMatcher requestMatcher;

	private ServerResponseMatcher responseMatcher;

	private ServerLoggingExecutor loggingExecutor;

	public LoggingGatewayFilter(ServerRequestMatcher requestMatcher, ServerResponseMatcher responseMatcher) {
		this(new DefaultServerAttributeMatcher(), requestMatcher, responseMatcher, new DefaultServerLoggingExecutor());
	}

	public LoggingGatewayFilter(ServerAttributeMatcher attributeMatcher, ServerRequestMatcher requestMatcher, ServerResponseMatcher responseMatcher, ServerLoggingExecutor loggingExecutor) {
		super();
		this.attributeMatcher = attributeMatcher;
		this.requestMatcher = requestMatcher;
		this.responseMatcher = responseMatcher;
		this.loggingExecutor = loggingExecutor;
	}

	@Override
	public int getOrder() {
		return NettyWriteResponseFilter.WRITE_RESPONSE_FILTER_ORDER - 1;
	}

	@Override
	public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

		// 优先匹配上下文携带信息，如果不匹配，不往下执行日志打印操作
		if( ! getAttributeMatcher().matches(Collections.unmodifiableMap(exchange.getAttributes())) ) {
			return chain.filter(exchange);
		}

		ServerHttpRequest request = exchange.getRequest();
		ServerHttpResponse response = exchange.getResponse();

		// 打印请求信息
		getLoggingExecutor().line(request);

		// 包装响应对象
		ServerHttpResponse newResponse = new ServerHttpResponseDecorator(response) {
			@Override
			public Mono<Void> writeWith(Publisher<? extends DataBuffer> body) {
				if(getResponseMatcher().matches(request, response)) {
					return response.writeWith(bodyToFlux(body).buffer().map(dataBuffers -> {
						byte[] content = toByteArray(dataBuffers);
						// 打印响应body
						getLoggingExecutor().response(content);
						return response.bufferFactory().wrap(content);
					}));
				} else {
					return response.writeWith(body);
				}
			}
		};

		if(getRequestMatcher().matches(request)) {

			Mono<byte[]> modifiedBody = ServerRequest.create(exchange, getMessageReaders()).bodyToMono(byte[].class).flatMap(body -> {
				// 打印请求body
				getLoggingExecutor().request(body);
				return Mono.just(body);
			});

			HttpHeaders headers = new HttpHeaders();
			headers.putAll(request.getHeaders());
			CachedBodyOutputMessage outputMessage = new CachedBodyOutputMessage(exchange, headers);

			BodyInserterContext context = new BodyInserterContext();
			return BodyInserters.fromPublisher(modifiedBody, byte[].class).insert(outputMessage, context).then(Mono.defer(() -> {
				ServerHttpRequestDecorator newRequest = new ServerHttpRequestDecorator(request) {
					@Override
					public Flux<DataBuffer> getBody() {
						return outputMessage.getBody();
					}
				};
				return chain.filter(exchange.mutate().request(newRequest).response(newResponse).build());
			}));

		} else {

			return chain.filter(exchange.mutate().response(newResponse).build());

		}

	}

	protected List<HttpMessageReader<?>> getMessageReaders() {
		return messageReaders;
	}

	protected ServerAttributeMatcher getAttributeMatcher() {
		return attributeMatcher;
	}

	protected ServerRequestMatcher getRequestMatcher() {
		return requestMatcher;
	}

	protected ServerResponseMatcher getResponseMatcher() {
		return responseMatcher;
	}

	protected ServerLoggingExecutor getLoggingExecutor() {
		return loggingExecutor;
	}

	private Flux<? extends DataBuffer> bodyToFlux(Publisher<? extends DataBuffer> body) {
		if(body instanceof Flux) {
			return (Flux<? extends DataBuffer>)body;
		}
		else if(body instanceof Mono) {
			return ((Mono<? extends DataBuffer>)body).flux();
		}
		else {
			return Flux.empty();
		}
	}

	private byte[] toByteArray(List<? extends DataBuffer> dataBuffers) {
		List<byte[]> bufs = new ArrayList<byte[]>(dataBuffers.size());
		dataBuffers.forEach(dataBuffer -> {
			byte[] content = new byte[dataBuffer.readableByteCount()];
			dataBuffer.read(content);
			DataBufferUtils.release(dataBuffer);
			bufs.add(content);
		});
		return mergeArrays(bufs);
	}

	private byte[] mergeArrays(List<byte[]> arrays) {
		byte[] buffer = new byte[arrays.stream().mapToInt(e -> e.length).sum()];
		int offset = 0;
		for(byte[] array : arrays) {
			for(byte ele : array) {
				buffer[offset++] = ele;
			}
		}
		return buffer;
	}

}
