package com.lucare.jedis;


import com.lucare.jedis.proxy.AdvanceJedisPoolManager;
import com.lucare.common.manager.ResourcesManager;
import com.lucare.common.utils.CommonUtils;
import com.lucare.jedis.proxy.AdvanceRedisProxyComposite;
import com.lucare.jedis.proxy.RedisProxy;
import redis.clients.jedis.JedisPool;

import java.lang.reflect.Proxy;

/**
 * Created by Lucare.Feng on 2016/3/24.
 */
public class RedisProxyFactory {
    private static boolean started = false;
    private static ResourcesManager<RedisProxy> redisProxyManager;

    public RedisProxyFactory() {
    }

    public static synchronized void start() {
        if(!started) {
            AdvanceJedisPoolManager.start();
            ResourcesManager.ResourceFactory redisProxyFactory = new ResourcesManager.ResourceFactory() {
                public RedisProxy getResource(String name) {
                    JedisPool jedisPool = AdvanceJedisPoolManager.getJedisPool(name);
                    if(jedisPool == null) {
                        throw CommonUtils.illegalStateException("JedisPoolNotFound[name=" + name + "]");
                    } else {
                        return (RedisProxy) Proxy.newProxyInstance(RedisProxy.class.getClassLoader(), new Class[]{RedisProxy.class}, new AdvanceRedisProxyComposite(jedisPool));
                    }
                }

                public void returnResource(String paramString, Object paramT) {

                }

                public void returnResource(String name, RedisProxy resource) {
                    resource.release();
                }
            };
            redisProxyManager = new ResourcesManager(redisProxyFactory);
            started = true;
            System.out.println("RedisProxyFactory starting...");
        }

        System.out.println("RedisProxyFactory has been started...");
    }

    public static synchronized void shutdown() {
        if(started) {
            AdvanceJedisPoolManager.shutdown();
        }

    }

    public static RedisProxy getProxy() {
        return getProxy("default");
    }

    public static RedisProxy getProxy(String name) {
        return redisProxyManager == null?null:(RedisProxy)redisProxyManager.get(name);
    }

    public static void releaseProxy() {
        if(redisProxyManager != null) {
            redisProxyManager.release();
        }

    }

    public static void main(String[] args) {
        start();
        RedisProxy proxy = getProxy();
        proxy.set("foo", "bar");
        System.out.println(proxy.isJedisOK());
        System.out.println(proxy.getJedis());
        System.out.println(proxy.get("foo"));
        releaseProxy();
    }
}
