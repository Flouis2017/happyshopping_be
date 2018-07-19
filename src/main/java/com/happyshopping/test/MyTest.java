package com.happyshopping.test;

import org.apache.commons.lang3.StringUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.happyshopping.dao.UserMapper;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={"file:src/main/resources/applicationContext.xml"})
public class MyTest {
	
	@Autowired
	private UserMapper userMapper;
	
	private int a;
	private Integer b;
	
	@Test
	public void test(){
		
		System.out.println(Thread.currentThread().getName());
		System.out.println(a);
		System.out.println(b);
	}
	
	@Test
	public void testBlankAndEmpty(){
		String a = null;
		String b = "hello world";
		String c = "";
		String d = "   ";
		System.out.println(StringUtils.isBlank(a));
		System.out.println(StringUtils.isBlank(b));
		System.out.println(StringUtils.isBlank(c));
		System.out.println(StringUtils.isBlank(d));
		
		System.out.println("====================");
		
		System.out.println(StringUtils.isEmpty(a));
		System.out.println(StringUtils.isEmpty(b));
		System.out.println(StringUtils.isEmpty(c));
		System.out.println(StringUtils.isEmpty(d));
	}
	
	@Test
	public void testCheckIsExisted(){
//		String sqlString = "select count(1) from hs_user where username = 'Tom'";
		int count = this.userMapper.checkUsername("Tom");
		System.out.println(count);
	}
	
}
