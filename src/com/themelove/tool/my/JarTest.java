package com.themelove.tool.my;
/**
 *	@author:qingshanliao
 *  @date  :2017年4月13日
 */
public class JarTest {
	public static void main(String[] args) {
		for (int i = 0; i < args.length; i++) {
			System.out.println(args[i]);
		}
		System.out.println("this is jar test");
		methond_1();
		method_2();
		method_3();
	}
	
	private static void methond_1(){
		System.out.println("methond_1");
		return;
	}
	private static void method_2(){
		System.out.println("methond_2");
	}
	
	private static void method_3(){
		boolean equals = "#0".equals("0");
		if (equals) {
			System.out.println("true");
		}else{
			System.out.println("false");
		}
	}
}
