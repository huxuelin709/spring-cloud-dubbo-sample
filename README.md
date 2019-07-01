# spring-cloud-dubbo-sample

## 使用说明
 
 这是一个基于spring-cloud-alibaba-example的例子做的例子,[原地址]
 (https://github.com/spring-cloud-incubator/spring-cloud-alibaba/tree/master/spring-cloud-alibaba-examples/spring-cloud-alibaba-dubbo-examples)


---

1. 安装nacos

安装及启动参考[官网](https://nacos.io/zh-cn/docs/quick-start.html),我的是linux服务器，使用
`bash startup.sh -m standalone`开启，然后用浏览器打开http://127.0.0.1:8848/nacos/#/serviceManagement?dataId=&group=&appName=&namespace=，能打开就是安装成功了，新版本的登录用户密码都是nacos。


2. 建立普通工程，随便写一个service，如下
```
package service;

public interface EchoService {

	String echo(String message);
}
```
然后打包成jar包，包名随便取个service.jar即可

 