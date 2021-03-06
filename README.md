# spring-cloud-dubbo-sample

## 使用说明
 
 这是一个基于spring-cloud-alibaba-example的例子做的例子,[原地址](https://github.com/spring-cloud-incubator/spring-cloud-alibaba/tree/master/spring-cloud-alibaba-examples/spring-cloud-alibaba-dubbo-examples)


---

1. 安装nacos

安装及启动参考[官网](https://nacos.io/zh-cn/docs/quick-start.html),我的是linux服务器，使用
`bash startup.sh -m standalone`开启，然后用浏览器打开首页`http://127.0.0.1:8848/nacos/`，能打开就是安装成功了，新版本的登录用户密码都是nacos。


2. 建立普通工程，随便写一个service，如下
```
package service;

public interface EchoService {

	String echo(String message);
}
```
然后打包成jar包，包名随便取个service.jar即可,进入service.jar目录，然后使用
`mvn install:install-file -Dfile=service.jar -DgroupId=org.springframework.cloud -DartifactId=spring-cloud-dubbo-sample-api -Dversion=0.0.1 -Dpackaging=jar`把servcie包安装到maven目录

3. 在springboot[官网](https://start.spring.io/)上建立server的maven工程

添加依赖
```
<!-- Sample API -->
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-dubbo-sample-api</artifactId>
    <version>${project.version}</version>
</dependency>

<!-- Spring Boot dependencies -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-actuator</artifactId>
</dependency>

<!-- Dubbo Spring Cloud Starter -->
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-dubbo</artifactId>
</dependency>

<!-- Spring Cloud Nacos Service Discovery -->
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-alibaba-nacos-discovery</artifactId>
</dependency>
```

在dependencyManagement的dependencies注入springcloud依赖
```
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-dependencies</artifactId>
    <version>${spring-cloud.version}</version>
    <type>pom</type>
    <scope>import</scope>
</dependency>
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-alibaba-dependencies</artifactId>
    <version>0.9.0.RELEASE</version>
    <type>pom</type>
    <scope>import</scope>
</dependency>
```

设置dubbo的配置文件bootstrap.yml
```
dubbo:
  scan:
    # dubbo 服务扫描基准包
    base-packages: com.eec.nacos
  protocol:
    # dubbo 协议
    name: dubbo
    # dubbo 协议端口（ -1 表示自增端口，从 20880 开始）
    port: -1
  registry:
    # 挂载到 Spring Cloud 注册中心
    address: spring-cloud://localhost
    
spring:
  application:
    # Dubbo 应用名称
    name: spring-cloud-alibaba-dubbo-server
  main:
    # Spring Boot 2.1 需要设定
    allow-bean-definition-overriding: true
  cloud:
    nacos:
      # Nacos 服务发现与注册配置
      discovery:
        server-addr: 127.0.0.1:8848
```

创建服务器启动配置application.yml
```
server:
    port: 8002
```

创建service的实现类
```
package com.eec.nacos.service.impl;

import org.apache.dubbo.config.annotation.Service;

import service.EchoService;


@Service
public class EchoServiceImpl implements EchoService {

	@Override
	public String echo(String message) {
		return "[echo] Hello" + message;
	}

}
```

给启动类添加annotation
```
@EnableDiscoveryClient
@EnableAutoConfiguration
```

鼠标右键 run as application ,启动成功，查看nacos启动网站`http://127.0.0.1:8848/nacos/ `，在`服务列表`里有spring-cloud-alibaba-dubbo-server，就说明服务注册成功了，还可以修改服务的端口启动服务，可以看见多个服务，nacos会自动负载均衡。


4. 在springboot[官网](https://start.spring.io/)上建立消费者的maven工程

在pom.xml里添加依赖

```
<!-- Sample API -->
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-dubbo-sample-api</artifactId>
    <version>${project.version}</version>
</dependency>

<!-- Spring Boot dependencies -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
</dependency>

<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-actuator</artifactId>
</dependency>

<!-- Dubbo Spring Cloud Starter -->
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-dubbo</artifactId>
</dependency>

<!-- Spring Cloud Nacos Service Discovery -->
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-alibaba-nacos-discovery</artifactId>
</dependency>
```
添加springcloud依赖
```
<dependencyManagement>
    <dependencies>
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-dependencies</artifactId>
            <version>${spring-cloud.version}</version>
            <type>pom</type>
            <scope>import</scope>
        </dependency>
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-alibaba-dependencies</artifactId>
            <version>0.9.0.RELEASE</version>
            <type>pom</type>
            <scope>import</scope>
        </dependency>
    </dependencies>
</dependencyManagement>
```
添加bootstrap.yml配置dubbo的配置
```
dubbo:
  registry:
    # 挂载到 Spring Cloud 注册中心
    address: spring-cloud://localhost
  cloud:
    subscribed-services: spring-cloud-alibaba-dubbo-server
  application:
    qos:
      port: 33333
      enable: true
      accept-foreign-ip: false
    
spring:
  application:
    # Dubbo 应用名称
    name: spring-cloud-alibaba-dubbo-client
  main:
    # Spring Boot 2.1 需要设定
    allow-bean-definition-overriding: true
  cloud:
    nacos:
      # Nacos 服务发现与注册配置
      discovery:
        server-addr: 127.0.0.1:8848
```
由于是同一台机器上，添加了qos配置，不配置也没关系，不太影响

设置服务端口application.yml
```
server:
    port: 9000
```

写请求文件
```
package com.eec.clientcommon.controller;

import org.apache.dubbo.config.annotation.Reference;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import service.EchoService;

@RestController
public class TestController {

	@Reference
	private EchoService echoService;

	@GetMapping("/test")
	public String test(String name) {
		Long t1 = System.currentTimeMillis();
		String string = echoService.echo(name);
		Long t2 = System.currentTimeMillis();
		System.out.println(t2 - t1);
		return string;
	}
}
```

在启动类添加annotation
```
package com.eec.clientcommon;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@EnableDiscoveryClient
@EnableAutoConfiguration
@SpringBootApplication
public class AlibabaNacosDiscoveryClientCommonApplication {

	public static void main(String[] args) {
		SpringApplication.run(AlibabaNacosDiscoveryClientCommonApplication.class, args);
	}

}
```
鼠标右键run as application ,启动服务，在`http://127.0.0.1:8848/nacos/`的`服务列表`里能够看到消费者服务spring-cloud-alibaba-dubbo-client

5. 打开命令行窗口,输入`curl http://localhost:9000/test?name=ceshi`，返回：`[echo] Helloceshi`



 