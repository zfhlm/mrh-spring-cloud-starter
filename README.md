## mrh-spring-cloud-starter

	自定义 spring cloud 微服务组件，基于 spring cloud 微服务组件进行扩展

#### spring-cloud-config-server 微服务配置中心

	配置中心不同配置文件，存在相同的配置项时配置繁琐，或者拆分之后客户端读取远程配置需要指定多个profiles
	
	配置中心通常的启用步骤，以本地资源的方式为例：
	
		①，maven主要依赖配置：
		
			<dependency>
				<groupId>org.springframework.boot</groupId>
				<artifactId>spring-boot-starter-web</artifactId>
			</dependency>
			<dependency>
				<groupId>org.springframework.cloud</groupId>
				<artifactId>spring-cloud-config-server</artifactId>
			</dependency>
		
		②，编写启动类：
		
			@SpringBootApplication
			@EnabledConfigServer
			public class ConfigServerStarter {
				public static void main(String[] args) {
					SpringApplication.run(ConfigServerStarter.class, args);
				}
			}
		
		③，配置文件 bootstrap.properties 配置：
		
			server.port=8888
			server.context-path=/
			spring.application.name=service-config
			spring.profiles.active=native
			spring.cloud.config.server.native.search-locations=classpath:/config
			
		④，放置配置文件 src/main/resources 目录：
		
			-- config
			   -- service-user-dev.properties
			   -- service-order-dev.properties
			   -- service-gateway-dev.properties
		
	基于配置中心加载文件方式，对资源查询织入后置处理切面，相关接口和切面：
	
		org.springframework.cloud.config.server.environment.EnvironmentRepository
		
		org.springframework.cloud.config.environment.Environment
		
		org.lushen.mrh.cloud.config.EnvronmentRepositoryBeanAdvisor
		
		org.lushen.mrh.cloud.config.EnvironmentProcessor
	
	目前实现了 config-locations 功能，引入步骤：
	
		①，配置中心必须存在以下依赖：
		
			<dependency>
				<groupId>org.springframework.boot</groupId>
				<artifactId>spring-boot-starter-aop</artifactId>
			</dependency>
			<dependency>
				<groupId>org.lushen.mrh</groupId>
				<artifactId>mrh-spring-cloud-starter</artifactId>
			</dependency>
		
		②，假设存在以下两个配置文件：
		
			service-user-dev.properties
			service-order-dev.properties
		
		③，都存在相同的配置，可以将相同配置提取为单独文件，例如：
		
			service-database-dev.properties
			service-redis-dev.properties
		
		④，删除原来配置文件中的相同配置，并在配置文件中加入以下配置引入公共配置：
		
			config-locations[0]=service-database-dev
			config-locations[1]=service-redis-dev
		
	如果需要扩展，可以实现接口 org.lushen.mrh.cloud.config.EnvironmentProcessor 并配置为配置中心 bean：
		
		①，例如想实现简单的打印配置功能：
		
			public class PrintEnvironmentProcessor implements EnvironmentProcessor {
				
				@Override
				public Environment process(EnvironmentRepository repository, Environment environment) {
					System.out.println(environment);
					return environment;
				}
			
			}
			
			@Bean
			public PrintEnvironmentProcessor printEnvironmentProcessor() {
				return new PrintEnvironmentProcessor();
			}
		 
		②，具体其他实现可以参考 org.lushen.mrh.cloud.config.ConfigLocationsEnvironmentProcessor

#### spring-cloud-discovery 微服务注册发现

	

#### spring-boot-admin 微服务监控

	微服务监控 spring-boot-admin 存在 context-path 不是根路径，会导致 actuator 监控失败。
	
	当前组件扩展了 spring-boot-admin 服务和 spring-cloud 服务注册和查找方式：
		
		①，服务注册时，获取上下文 context-path信息，添加到注册信息 metadatas 中，实现接口：
		
			org.lushen.mrh.cloud.discovery.DiscoveryMetadataConfigurer
		
		②，服务发现时，解析注册信息 metadatas 中的  context-path，重写监控url地址，实现接口：
		
			de.codecentric.boot.admin.server.cloud.discovery.ServiceInstanceConverter
		
	具体可查看实现类：
	
		org.lushen.mrh.cloud.admin.BootAdminServiceInstanceConverter
		
		org.lushen.mrh.cloud.admin.BootAdminAutoConfiguration

#### spring-cloud-openfeign 微服务远程调用

	












