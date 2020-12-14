package org.lushen.mrh.cloud.gateway;

import java.util.Iterator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.lushen.mrh.boot.autoconfigure.support.deliver.GenericDeliverHeaders;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.core.Ordered;
import org.springframework.web.server.ServerWebExchange;

import reactor.core.publisher.Mono;

/**
 * deliver 请求头清除过滤器
 * 
 * @author hlm
 */
public class DeliverGatewayFilter implements GatewayFilter, Ordered {

	private final Log log = LogFactory.getLog("deliver-filter");

	@Override
	public int getOrder() {
		return HIGHEST_PRECEDENCE;
	}

	@Override
	public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
		return chain.filter(exchange.mutate().request(exchange.getRequest().mutate().headers(httpHeaders -> {
			Iterator<String> iterator = httpHeaders.keySet().iterator();
			while (iterator.hasNext()) {
				String name = iterator.next();
				if(GenericDeliverHeaders.isRequestDeliverHeader(name)) {
					log.warn(String.format("clean deliver header [%s=%s] .", name, httpHeaders.getFirst(name)));
					httpHeaders.remove(name);
				}
			}
		}).build()).build());
	}

}
