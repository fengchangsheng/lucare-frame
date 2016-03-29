package com.lucare.jedis.proxy;

import com.lucare.common.utils.CommonUtils;
import com.lucare.common.utils.DomNode;
import com.lucare.common.utils.ReflectUtils;
import com.lucare.common.utils.TypesUtils;
import org.apache.commons.pool2.impl.AbandonedConfig;
import org.apache.commons.pool2.impl.GenericObjectPool;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Lucare.Feng on 2016/3/27.
 */
public class AdvanceJedisPoolManager {
    private static final Map<String, JedisPool> __pool__container__ = new HashMap();

    private static boolean started = false;

    public static boolean isStarted() {
        return started;
    }

    public static synchronized void start() {
        if (isStarted()) {
            throw CommonUtils.illegalStateException(AdvanceJedisPoolManager.class.getSimpleName() + " has been started.");
        }

        DomNode root = DomNode.getRootFromClassPath("redis-proxy.xml");
        DomNode sources = root.element("sources");
        List<DomNode> list = sources.elements("source");
        for (DomNode source : list) {
            String sourceName = source.attributeValue("name");

            DomNode node = source.element("host");
            String host = node.getElementText();

            node = source.element("port");
            int port = ((Integer) TypesUtils.cashFor(node.getElementText(), Integer.TYPE)).intValue();

            node = source.element("maxTotal");
            int maxTotal = ((Integer)TypesUtils.cashFor(node.getElementText(), Integer.TYPE)).intValue();

            node = source.element("maxIdle");
            int maxIdle = ((Integer)TypesUtils.cashFor(node.getElementText(), Integer.TYPE)).intValue();

            node = source.element("minIdle");
            int minIdle = ((Integer)TypesUtils.cashFor(node.getElementText(), Integer.TYPE)).intValue();

            node = source.element("maxWaitMillis");
            int maxWaitMillis = ((Integer)TypesUtils.cashFor(node.getElementText(), Integer.TYPE)).intValue();

            node = source.element("minEvictableIdleTimeMillis");
            int minEvictableIdleTimeMillis = ((Integer)TypesUtils.cashFor(node.getElementText(), Integer.TYPE)).intValue();

            node = source.element("softMinEvictableIdleTimeMillis");
            int softMinEvictableIdleTimeMillis = ((Integer)TypesUtils.cashFor(node.getElementText(), Integer.TYPE)).intValue();

            node = source.element("numTestsPerEvictionRun");
            int numTestsPerEvictionRun = ((Integer)TypesUtils.cashFor(node.getElementText(), Integer.TYPE)).intValue();

            node = source.element("testOnCreate");
            boolean testOnCreate = false;
            if (node != null) {
                testOnCreate = ((Boolean)TypesUtils.cashFor(node.getElementText(), Boolean.TYPE)).booleanValue();
            }
            node = source.element("testOnBorrow");
            boolean testOnBorrow = ((Boolean)TypesUtils.cashFor(node.getElementText(), Boolean.TYPE)).booleanValue();

            node = source.element("testOnReturn");
            boolean testOnReturn = ((Boolean)TypesUtils.cashFor(node.getElementText(), Boolean.TYPE)).booleanValue();

            node = source.element("testWhileIdle");
            boolean testWhileIdle = ((Boolean)TypesUtils.cashFor(node.getElementText(), Boolean.TYPE)).booleanValue();

            node = source.element("timeBetweenEvictionRunsMillis");
            int timeBetweenEvictionRunsMillis = ((Integer)TypesUtils.cashFor(node.getElementText(), Integer.TYPE)).intValue();

            node = source.element("blockWhenExhausted");
            boolean blockWhenExhausted = ((Boolean)TypesUtils.cashFor(node.getElementText(), Boolean.TYPE)).booleanValue();

            JedisPoolConfig config = new JedisPoolConfig();
            config.setMaxTotal(maxTotal);
            config.setMaxIdle(maxIdle);
            config.setMinIdle(minIdle);
            config.setMaxWaitMillis(maxWaitMillis);
            config.setMinEvictableIdleTimeMillis(minEvictableIdleTimeMillis);
            config.setSoftMinEvictableIdleTimeMillis(softMinEvictableIdleTimeMillis);
            config.setNumTestsPerEvictionRun(numTestsPerEvictionRun);
            config.setTestOnCreate(testOnCreate);
            config.setTestOnBorrow(testOnBorrow);
            config.setTestOnReturn(testOnReturn);
            config.setTestWhileIdle(testWhileIdle);
            config.setTimeBetweenEvictionRunsMillis(timeBetweenEvictionRunsMillis);
            config.setBlockWhenExhausted(blockWhenExhausted);

            JedisPool jedisPool = new JedisPool(config, host, port);
            setAbandonedConfig(jedisPool, source);

            __pool__container__.put(sourceName, jedisPool);
        }
        started = true;
    }

    public static synchronized void shutdown() {
        if (isStarted()) {
            for (JedisPool pool : __pool__container__.values())
                try {
                    pool.destroy();
                } catch (Throwable ignore) {
                }
            __pool__container__.clear();
            started = false;
        }
    }

    public static JedisPool getJedisPool(String name) {
        return (JedisPool)__pool__container__.get(name);
    }

    private static void setAbandonedConfig(JedisPool jedisPool, DomNode source) {
        AbandonedConfig abandonedConfig = buildAbandonedConfig(source);
        try {
            Field field = ReflectUtils.foundField(JedisPool.class, "internalPool");
            Method method = ReflectUtils.foundMethod(GenericObjectPool.class, "setAbandonedConfig", new Class[] { AbandonedConfig.class });

            GenericObjectPool objectPool = (GenericObjectPool)field.get(jedisPool);
            ReflectUtils.invokeMethod(method, objectPool, new Object[] { abandonedConfig });
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    private static AbandonedConfig buildAbandonedConfig(DomNode source) {
        AbandonedConfig abandonedConfig = new AbandonedConfig();
        DomNode node = source.element("logAbandoned");
        if (node != null) {
            boolean logAbandoned = ((Boolean)TypesUtils.cashFor(node.getElementText(), Boolean.TYPE)).booleanValue();
            abandonedConfig.setLogAbandoned(logAbandoned);
        }
        node = source.element("removeAbandonedOnBorrow");
        if (node != null) {
            boolean removeAbandonedOnBorrow = ((Boolean)TypesUtils.cashFor(node.getElementText(), Boolean.TYPE)).booleanValue();

            abandonedConfig.setRemoveAbandonedOnBorrow(removeAbandonedOnBorrow);
        }
        node = source.element("removeAbandonedOnMaintenance");
        if (node != null) {
            boolean removeAbandonedOnMaintenance = ((Boolean)TypesUtils.cashFor(node.getElementText(), Boolean.TYPE)).booleanValue();

            abandonedConfig.setRemoveAbandonedOnMaintenance(removeAbandonedOnMaintenance);
        }
        node = source.element("removeAbandonedTimeout");
        if (node != null) {
            int removeAbandonedTimeout = ((Integer)TypesUtils.cashFor(node.getElementText(), Integer.TYPE)).intValue();
            abandonedConfig.setRemoveAbandonedTimeout(removeAbandonedTimeout);
        }
        node = source.element("useUsageTracking");
        if (node != null) {
            boolean useUsageTracking = ((Boolean)TypesUtils.cashFor(node.getElementText(), Boolean.TYPE)).booleanValue();

            abandonedConfig.setUseUsageTracking(useUsageTracking);
        }
        return abandonedConfig;
    }
}
