package com.happyshopping.test;

import org.junit.Test;

import com.happyshopping.util.BigDecimalUtil;

public class BigDecimalTest {

	@Test
	public void test1(){
		System.out.println(0.05 + 0.01);
		System.out.println(1.0 - 0.34);
		System.out.println(4.105 * 100);
		System.out.println(123.3 / 100);
	}
	
	@Test
	public void test2(){
		System.out.println(BigDecimalUtil.add(0.05, 0.01));
		System.out.println(BigDecimalUtil.sub(1.0, 0.34));
		System.out.println(BigDecimalUtil.mul(4.105, 100));
		System.out.println(BigDecimalUtil.div(123.3, 100));
	}
	
}
