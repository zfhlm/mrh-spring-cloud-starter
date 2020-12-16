package org.lushen.mrh.cloud.config;

import org.springframework.cloud.config.environment.Environment;
import org.springframework.cloud.config.server.environment.EnvironmentRepository;

/**
 * {@link Environment} 处理器
 * 
 * @author hlm
 */
public interface EnvironmentProcessor {

	public Environment process(EnvironmentRepository repository, Environment environment);

}
