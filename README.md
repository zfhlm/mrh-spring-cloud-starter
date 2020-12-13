##### 自定义 spring-cloud-starter 组件

	mrh-spring-cloud-starter

##### 简单介绍

	①，对 spring-boot-admin 进行扩展：
	
		1，解决存在 context-path 服务监控失败问题
	
	②，对 spring-cloud-discovery 进行扩展：
	
		1，注册元信息 metadata 自动注入
		
		2，灰度发布
	
	③，对 spring-cloud-feign 进行扩展：
	
		1，异常解码器：自定义feign调用异常处理
		
		2，请求拦截器：自定义请求头信息自动传递，自动添加标识feign调用请求头
		
		3，响应切面：根据是否feign调用，对响应内容进行不同的处理逻辑
		
		4，异常切面：根据是否feign调用，对异常进行不同的处理逻辑
		
		5，异常解码器(hystrix)：自定义feign开启hystrix之后调用异常处理，以及自定义异常插件
		
	④，对 spring-cloud-gateway 进行扩展：
		
		1，请求响应内容日志filter
		
		2，自定义请求头清理filter
		
		3，登录处理filter
		
		4，鉴权处理filter
		
	⑤，对 spring-cloud-bus 进行扩展：
	
		1，自定义 bus 事件
		
	
	
	
		
	
