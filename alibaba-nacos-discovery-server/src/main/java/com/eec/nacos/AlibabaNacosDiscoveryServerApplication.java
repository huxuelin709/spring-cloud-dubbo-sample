package com.eec.nacos;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * @author huxuelin
 * @date
 */
@EnableDiscoveryClient
@EnableAutoConfiguration
public class AlibabaNacosDiscoveryServerApplication {

	public static void main(String[] args) {
		SpringApplication.run(AlibabaNacosDiscoveryServerApplication.class, args);
	}
	
}
