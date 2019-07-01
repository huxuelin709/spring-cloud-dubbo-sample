# spring-cloud-dubbo-sample

## 使用说明
 
 这是一个基于spring-cloud-alibaba-example的例子做的例子,[原地址](https://github.com/spring-cloud-incubator/spring-cloud-alibaba/tree/master/spring-cloud-alibaba-examples/spring-cloud-alibaba-dubbo-examples)


---

1. 安装nacos

安装及启动参考[官网](https://nacos.io/zh-cn/docs/quick-start.html),我的是linux服务器，使用
`bash startup.sh -m standalone`开启，然后用浏览器打开[首页](http://127.0.0.1:8848/nacos/#/serviceManagement?dataId=&group=&appName=&namespace= ) ，能打开就是安装成功了，新版本的登录用户密码都是nacos。


2. 建立普通工程，随便写一个service，如下
```
package service;

public interface EchoService {

	String echo(String message);
}
```
然后打包成jar包，包名随便取个service.jar即可,进入service.jar目录，然后使用
`mvn install:install-file -Dfile=service.jar -DgroupId=org.springframework.cloud -DartifactId=spring-cloud-dubbo-sample-api -Dversion=0.0.1 -Dpackaging=jar`把servcie包安装到maven目录

3. 在start.spring.io上建立server的maven工程

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
 