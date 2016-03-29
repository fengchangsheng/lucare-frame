package com.lucare.test.accessor;


import com.lucare.jedis.RedisProxyFactory;
import com.lucare.jedis.proxy.RedisProxy;

public class RedisAccessor {

	public static RedisProxy getDefaultClient() {
		return RedisProxyFactory.getProxy();
	}
	
	public static void releaseClient() {
		RedisProxyFactory.releaseProxy();
	}
}
