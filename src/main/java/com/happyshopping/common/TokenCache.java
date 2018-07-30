package com.happyshopping.common;

import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;


/**
 * @author Flouis
 * @date 2018年7月30日
 * @Description Guava本地缓存类(Guava local cache class)
 */
public class TokenCache {
	private static Logger logger = LoggerFactory.getLogger(TokenCache.class);

	public static final String TOKEN_PRIFIX = "token_";
	public static final String TOKEN_SUFFIX = "_"+System.currentTimeMillis();
	
	private static LoadingCache<String, String> localCache = 
			CacheBuilder.newBuilder().initialCapacity(1000).
			maximumSize(10000).expireAfterAccess(1, TimeUnit.HOURS).
			build(new CacheLoader<String, String>(){
				public String load(String s) throws Exception{
					return "null";
				}
			});
	
	public static void setCache(String key, String value){
		localCache.put(key, value);
	}
	
	public static String getCache(String key){
		String value = null;
		try {
			value = localCache.get(key);
			if ("null".equals(value)){
				return null;
			}
		} catch(Exception e){
			logger.error("localCache occurs error", e);
		}
		return value;
	}
	
	
	/*public static void main(String[] args) {
		System.out.println(TokenCache.TOKEN_SUFFIX);
	}*/
}
