##### 自定义 spring-cloud-starter 组件

	mrh-spring-cloud-starter

##### 简单介绍

	①，对 spring-boot-admin 服务注册发现进行扩展，解决存在 context-path 服务监控失败问题
	
	②，对 spring-cloud-discovery 进行扩展：
	
		1，注册元信息 metadata 自动注入
		
		2，灰度发布
	
	③，对 spring-cloud-feign 进行扩展：
	
		1，自定义异常编解码处理器
		
		2，请求拦截器、响应内容切面、响应异常切面
		
	