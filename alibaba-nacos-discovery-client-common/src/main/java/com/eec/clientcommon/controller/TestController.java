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
		return echoService.echo(name);
	}
}
