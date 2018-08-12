package com.happyshopping.util;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Properties;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PropertiesUtil {

	private static Logger logger = LoggerFactory.getLogger(PropertiesUtil.class);
	
	private static Properties props;
	
	static {
		String fileName = "happyshopping.properties";
		props = new Properties();
		try{
			props.load(new InputStreamReader(PropertiesUtil.class.getClassLoader().getResourceAsStream(fileName),"UTF-8"));
		} catch(IOException e){
			logger.error("Configuration file reading exception",e);
		}
	}
	
	public static String getProperty(String key){
		String value = props.getProperty(key.trim());
		if (StringUtils.isBlank(value)){
			return null;
		}
		return value.trim();
	}
	
	public static String getProperty(String key, String defaultValue){
		String value = props.getProperty(key.trim());
		if (StringUtils.isBlank(value)){
			value = defaultValue;
		}
		return value.trim();
	}
	
	/*public static void main(String[] args) {
		System.out.println(PropertiesUtil.getProperty("ftp.server.ip"));
	}*/
}
