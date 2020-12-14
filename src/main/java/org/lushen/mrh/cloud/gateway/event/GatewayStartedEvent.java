package org.lushen.mrh.cloud.gateway.event;

import org.springframework.context.ApplicationEvent;

/**
 * 网关启动事务
 * 
 * @author hlm
 */
public class GatewayStartedEvent extends ApplicationEvent {

	private static final long serialVersionUID = -6089054926459032621L;

	public GatewayStartedEvent(Object source) {
		super(source);
	}

}
