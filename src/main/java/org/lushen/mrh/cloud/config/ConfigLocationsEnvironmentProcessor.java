package org.lushen.mrh.cloud.config;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.StringTokenizer;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.cloud.config.environment.Environment;
import org.springframework.cloud.config.environment.PropertySource;
import org.springframework.cloud.config.server.environment.EnvironmentRepository;
import org.springframework.core.env.Profiles;

/**
 * 配置文件 config-locations 引入其他配置文件支持实现
 * 
 * @author hlm
 */
public class ConfigLocationsEnvironmentProcessor implements EnvironmentProcessor {

	private static final String PROFILE_DEFAULT = "default";

	public static final String CONFIG_LOCATIONS = "config-locations";

	private static final Pattern CONFIG_NAME_PATTERN = Pattern.compile("config\\-locations(\\[\\d\\])?");

	private static final Pattern CONFIG_VALUE_PATTERN = Pattern.compile("[a-zA-Z]+(\\-[a-zA-Z]+)+");

	private final Log log = LogFactory.getLog(getClass());

	@Override
	public Environment process(EnvironmentRepository repository, Environment environment) {

		// 创建配置上下文
		Environment complete = new Environment(environment.getName(), environment.getProfiles());
		complete.setLabel(environment.getLabel());
		complete.setState(environment.getState());
		complete.setVersion(environment.getVersion());
		complete.addAll(environment.getPropertySources());

		// 获取所有 config-locations 配置名称
		String[] locations = getLocations(environment.getPropertySources());

		// 加载所有 config-locations 配置文件
		for(String configLocation : locations) {
			Config config = getConfig(environment.getProfiles(), environment.getLabel(), configLocation);
			log.info("Prepare to load property source : " + config);
			complete.addAll(repository.findOne(config.getApplication(), config.getProfile(), config.getLabel()).getPropertySources());
		}

		return complete;
	}

	/**
	 * 获取键值配置 根路径的所有 config-locations 合法值
	 * 
	 * @param propertySources
	 * @return
	 */
	private String[] getLocations(List<PropertySource> propertySources) {
		List<String> configLocations = new ArrayList<String>();
		for(PropertySource propertySource : propertySources) {
			for(Entry<?, ?> source : propertySource.getSource().entrySet()) {
				String name = String.valueOf(source.getKey());
				String value = String.valueOf(source.getValue());
				if(CONFIG_NAME_PATTERN.matcher(name).matches()) {
					if(CONFIG_VALUE_PATTERN.matcher(value).matches()) {
						log.info("Find [" + name + ": " + value + "] in property source [" + propertySource.getName() + "]");
						configLocations.add(value);
					} else {
						log.warn("[config-locations: " + value + "] don't matches pattern [" + CONFIG_VALUE_PATTERN + "].");
					}
				}
			}
		}
		return configLocations.stream().toArray(length -> new String[length]);
	}

	/**
	 * 根据 config-locations 元素值，获取配置信息
	 * 
	 * @param profiles
	 * @param label
	 * @param configLocation
	 * @return
	 */
	private Config getConfig(String[] profiles, String label, String configLocation) {

		// 拆分文件名称
		List<String> parts = new ArrayList<String>();
		StringTokenizer tokenizer = new StringTokenizer(configLocation, "-", false);
		while(tokenizer.hasMoreTokens()) {
			parts.add(tokenizer.nextToken());
		}

		// 匹配已存在的profiles
		Profiles p = Profiles.of(profiles);
		for(int index=parts.size()-1; index > 0; index--) {
			String profile = StringUtils.join(parts.subList(index, parts.size()), "-");
			if(p.matches(e -> StringUtils.equals(e, profile))) {
				String application = StringUtils.join(parts.subList(0, index), "-");
				return new Config(application, profile, label);
			}
		}

		// 不存在profile部分
		if(parts.size() == 1) {
			return new Config(configLocation, PROFILE_DEFAULT, label);
		}
		// 最末尾部分作为profile
		else {
			String application = StringUtils.join(parts.subList(0, parts.size()-1), "-");
			String profile = parts.get(parts.size()-1);
			return new Config(application, profile, label);
		}
	}

	private class Config {

		private String application;

		private String profile;

		private String label;

		public Config(String application, String profile, String label) {
			super();
			this.application = application;
			this.profile = profile;
			this.label = label;
		}

		public String getApplication() {
			return application;
		}

		public String getProfile() {
			return profile;
		}

		public String getLabel() {
			return label;
		}

		@Override
		public String toString() {
			StringBuilder builder = new StringBuilder();
			builder.append("[application=");
			builder.append(application);
			builder.append(", profile=");
			builder.append(profile);
			builder.append(", label=");
			builder.append(label);
			builder.append("]");
			return builder.toString();
		}

	}

}