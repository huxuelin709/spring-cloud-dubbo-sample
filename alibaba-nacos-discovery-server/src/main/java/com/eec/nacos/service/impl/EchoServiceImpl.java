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
