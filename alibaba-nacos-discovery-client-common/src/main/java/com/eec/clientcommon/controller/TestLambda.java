package com.eec.clientcommon.controller;

public class TestLambda {
	
	public static void main(String[] args) {
		HelloWorld testHelloWorld = () -> System.out.println("HelloWorld!");
		testHelloWorld.print();
	}
	
	

}
@FunctionalInterface
interface HelloWorld {
	void print();
	default void print2() {
	}
}
