package com.lucare.jedis.proxy;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.SortingParams;

import java.util.List;
import java.util.Map;

/**
 * Created by Lucare.Feng on 2016/3/24.
 */
public interface RedisProxyApi {
    boolean isJedisOK();

    /** @deprecated */
    @Deprecated
    Jedis getJedis();

    void release();

    void putObject(String var1, Object var2);

    void putObjectExpire(String var1, Object var2, int var3);

    <T> T getObject(String var1, Class<T> var2);

    <T> List<T> queryList(Class<T> var1, Object... var2);

    <E> Map<String, E> getMap(List<String> var1, Class<E> var2);

    void putMap(Map<String, Object> var1);

    void hashMoreSet(String var1, Map<String, Object> var2);

    Map<String, Object> hashMoreGet(String var1, String[] var2);

    long setAdd(String var1, Object var2);

    <T> List<T> sorting(Class<T> var1, String var2, SortingParams var3);

    List<?> checkAndSet(String var1, Object var2);

    List<?> checkAndSetExpire(String var1, Object var2, int var3);

    long sortedsetAdd(String var1, Object var2, double var3);

    long sortedsetRem(String var1, Object var2);

    <E> List<E> sortedsetRange(String var1, int var2, int var3, Class<E> var4);
}
