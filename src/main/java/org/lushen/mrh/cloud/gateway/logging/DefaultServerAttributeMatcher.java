package org.lushen.mrh.cloud.gateway.logging;

import java.util.Map;

/**
 * 默认实现
 * 
 * @author hlm
 */
public class DefaultServerAttributeMatcher implements ServerAttributeMatcher {

	@Override
	public boolean matches(Map<String, Object> attributes) {
		return true;
	}

}
