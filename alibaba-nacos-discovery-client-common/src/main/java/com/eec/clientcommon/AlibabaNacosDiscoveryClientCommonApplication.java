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
