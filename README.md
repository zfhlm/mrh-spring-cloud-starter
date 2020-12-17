# mrh-spring-cloud-starter

	spring cloud 相关组件扩展实现

### 微服务配置中心

	spring-cloud-config 扩展.
	
	基于配置中心加载文件方式，对资源查询织入后置处理切面，可以通过 config-locations 包含其他配置文件的配置信息：
	
		①，配置中心主要依赖：
		
			<dependency>
				<groupId>org.springframework.boot</groupId>
				<artifactId>spring-boot-starter-web</artifactId>
			</dependency>
			<dependency>
				<groupId>org.springframework.cloud</groupId>
				<artifactId>spring-cloud-config-server</artifactId>
			</dependency>
			<dependency>
				<groupId>org.springframework.boot</groupId>
				<artifactId>spring-boot-starter-aop</artifactId>
			</dependency>
			<dependency>
				<groupId>org.lushen.mrh</groupId>
				<artifactId>mrh-spring-cloud-starter</artifactId>
			</dependency>
		
		②，编写启动类：
		
			@SpringBootApplication
			@EnabledConfigServer
			public class ConfigServerStarter {
				public static void main(String[] args) {
					SpringApplication.run(ConfigServerStarter.class, args);
				}
			}
		
		③，本地存放配置文件 bootstrap.properties 配置：
		
			server.port=8888
			server.context-path=/
			spring.application.name=service-config
			spring.profiles.active=native
			spring.cloud.config.server.native.search-locations=classpath:/config
		
		④，放置配置文件 src/main/resources 目录 config 子目录，假设存在以下两个配置文件：
		
			service-user-dev.properties
			service-order-dev.properties
		
		⑤，都存在相同的配置，可以将相同配置提取为单独文件，例如：
		
			service-database-dev.properties
			service-redis-dev.properties
			...
		
		⑥，删除原来配置文件中的相同配置，并在配置文件中加入以下配置引入公共配置：
		
			config-locations[0]=service-database-dev
			config-locations[1]=service-redis-dev
			...

### 微服务注册发现

	spring-cloud-discovery 扩展.
	
	微服务注册信息 metadatas 前置处理：
	
		①，实现接口并添加为bean：
		
			org.lushen.mrh.cloud.discovery.DiscoveryMetadataConfigurer
		
		②，编写服务注册信息变更 customizer，目前 eureka、zookeeper、consul 注册中心已实现：
		
			org.lushen.mrh.cloud.discovery.customizer.ConsulDiscoveryMetadataCustomizer
			
			org.lushen.mrh.cloud.discovery.customizer.EurekaDiscoveryMetadataCustomizer
			
			org.lushen.mrh.cloud.discovery.customizer.ZookeeperDiscoveryMetadataCustomizer
		
		③，如果需要其他注册中心支持，请参考以上三个实现编写相关实现
	
	微服务灰度发布：
	
		(待完善)

### 微服务监控

	spring-boot-admin 扩展.
	
	处理微服务监控存在 context-path 非根路径导致监控失败问题 ：
		
		①，监控服务和业务服务引入依赖：
		
			<dependency>
				<groupId>org.lushen.mrh</groupId>
				<artifactId>mrh-spring-cloud-starter</artifactId>
			</dependency>
		
		②，基于(#微服务注册发现)注册信息 metadatas 前置处理，服务注册时，获取上下文 context-path信息，自动添加到注册信息 metadatas
		
		③，服务发现时，自动解析注册信息 metadatas 中的  context-path，并重写监控url地址

### 微服务网关

	spring-cloud-gateway 扩展.
	
	自定义过滤器：
	
		①，清除http请求 Request-Deliver-* 请求头过滤器：
	
			org.lushen.mrh.cloud.gateway.DeliverGatewayFilter
		
		②，请求响应信息日志打印过滤器：
		
			org.lushen.mrh.cloud.gateway.LoggingGatewayFilter
		
		③，接口白名单拦截过滤器：
		
			(待完善)
		
		④，用户登录拦截过滤器：
		
			(待完善)
		
		⑤，用户权限拦截过滤器：
		
			(待完善)

### 微服务调用

	spring-cloud-openfeign 扩展.












