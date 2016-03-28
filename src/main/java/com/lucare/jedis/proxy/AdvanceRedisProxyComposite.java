package com.lucare.jedis.proxy;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.parser.Feature;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.lucare.common.utils.CommonUtils;
import com.lucare.common.utils.ReflectUtils;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.SortingParams;
import redis.clients.jedis.Transaction;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.nio.charset.Charset;
import java.util.*;

/**
 * Created by Lucare.Feng on 2016/3/27.
 */
public class AdvanceRedisProxyComposite implements RedisProxyApi, InvocationHandler {
    private static Charset UTF8 = Charset.forName("UTF-8");
    private JedisPool jedisPool;
    private Jedis jedis;
    private boolean isJedisOk = false;

    private static Map<String, Method> proxyApiMethodMap = new HashMap();

    private static void init() {
        Method[] methods = InvocationHandler.class.getMethods();
        for (Method m : methods) {
            Method impl = ReflectUtils.foundMethod(AdvanceRedisProxyComposite.class, m.getName(), new Class[0]);
            proxyApiMethodMap.put(m.getName(), impl);
        }

        methods = InvocationHandler.class.getMethods();
        for (Method m : methods) {
            Method impl = ReflectUtils.foundMethod(AdvanceRedisProxyComposite.class, m.getName(), new Class[0]);
            proxyApiMethodMap.put(m.getName(), impl);
        }
    }

    public AdvanceRedisProxyComposite(JedisPool jedisPool) {
        this.jedisPool = jedisPool;
    }

    public boolean isJedisOK() {
        return this.isJedisOk;
    }

    @Deprecated
    public Jedis getJedis() {
        return this.jedis;
    }

    public void release() {
        if ((this.jedis != null) && (getJedisPool() != null)) {
            boolean success = false;
            try {
                if (isJedisOK()) {
                    getJedisPool().returnResource(this.jedis);
                    success = true;
                }
            } catch (Exception ignore) {
            }
            if (!success)
                try {
                    getJedisPool().returnBrokenResource(this.jedis);
                }
                catch (Exception ignore) {
                }
        }
        this.jedis = null;
        this.isJedisOk = false;
    }

    private JedisPool getJedisPool() {
        return this.jedisPool;
    }

    private Jedis foundJedis() {
        if ((!isJedisOK()) && (this.jedis != null)) {
            release();
        }
        if (this.jedis == null) {
            this.jedis = ((Jedis)getJedisPool().getResource());
            this.isJedisOk = true;
        }
        return this.jedis;
    }

    public void putObject(String key, Object value) {
        if (value == null)
            throw new NullPointerException("Unspported bean=null");
        byte[] k = key2Bytes(key);
        byte[] v = JSON.toJSONBytes(value, new SerializerFeature[0]);
        foundJedis().set(k, v);
    }

    public <T> T getObject(String key, Class<T> klass) {
        byte[] k = key2Bytes(key);
        byte[] data = foundJedis().get(k);
        return data == null ? null : (T) JSON.parseObject(data, klass, new Feature[0]);
    }

    public <T> List<T> queryList(Class<T> klass, Object[] keys) {
        byte[][] keyss = new byte[keys.length][];
        for (int index = 0; index < keys.length; index++) {
            keyss[index] = key2Bytes(String.valueOf(keys[index]));
        }

        List<byte[]> list = foundJedis().mget(keyss);
        List result = new ArrayList(list.size());
        for (byte[] data : list) {
            Object e = JSON.parseObject(data, klass, new Feature[0]);
            result.add(e);
        }
        return result;
    }

    public <E> Map<String, E> getMap(List<String> keys, Class<E> klass) {
        Map dataMap = CommonUtils.stableMap(keys.size());
        byte[][] keyss = new byte[keys.size()][];
        for (int index = 0; index < keys.size(); index++) {
            keyss[index] = key2Bytes(String.valueOf(keys.get(index)));
        }

        List list = foundJedis().mget(keyss);

        for (int index = 0; index < keys.size(); index++) {
            String key = (String)keys.get(index);
            byte[] bytes = (byte[])list.get(index);

            Object e = bytes2Object(bytes, klass);
            dataMap.put(key, e);
        }
        return dataMap;
    }

    public void hashMoreSet(String key, Map<String, Object> map) {
        if ((CommonUtils.isEmpty(key)) || (CommonUtils.isEmpty(map)))
            return;
        Map hash = new HashMap(map.size(), 1.0F);
        for (String k : map.keySet()) {
            Object v = map.get(k);
            if ((k != null) && (v != null)) {
                byte[] kbytes = object2Bytes(k);
                byte[] vbytes = object2Bytes(v);
                hash.put(kbytes, vbytes);
            }
        }

        foundJedis().hmset(object2Bytes(key), hash);
    }

    public Map<String, Object> hashMoreGet(String key, String[] fields) {
        byte[][] fbytes = new byte[fields.length][];
        for (int i = 0; i < fields.length; i++) {
            fbytes[i] = object2Bytes(fields[i]);
        }

        List list = foundJedis().hmget(object2Bytes(key), fbytes);

        Map result = null;
        if (list != null) {
            result = new HashMap();
            for (int index = 0; index < fields.length; index++) {
                String f = fields[index];
                Object data = null;
                byte[] v = (byte[])list.get(index);
                if (v != null)
                    data = JSON.parse(v, new Feature[0]);
                result.put(f, data);
            }
        }
        return result;
    }

    public void putMap(Map<String, Object> dataMap) {
        if (!CommonUtils.isEmpty(dataMap)) {
            int index = 0;
            byte[][] mset = new byte[dataMap.size() * 2][];
            for (Map.Entry e : dataMap.entrySet()) {
                mset[(index++)] = key2Bytes((String)e.getKey());
                mset[(index++)] = object2Bytes(e.getValue());
            }
            foundJedis().mset(mset);
        }
    }

    public long setAdd(String key, Object value) {
        return foundJedis().sadd(key2Bytes(key), new byte[][] { object2Bytes(value) }).longValue();
    }

    public <T> List<T> sorting(Class<T> klass, String key, SortingParams sorting) {
        List<byte[]> byteList = foundJedis().sort(key2Bytes(key), sorting);
        List result = new ArrayList(byteList.size());
        for (byte[] data : byteList) {
            Object e = JSON.parseObject(data, klass, new Feature[0]);
            result.add(e);
        }
        return result;
    }

    public List<?> checkAndSet(String key, Object value) {
        return cas(key, value, -1);
    }

    public List<?> checkAndSetExpire(String key, Object value, int seconds) {
        return cas(key, value, seconds);
    }

    public void putObjectExpire(String key, Object value, int seconds) {
        byte[] k = key2Bytes(key);
        byte[] v = JSON.toJSONBytes(value, new SerializerFeature[0]);
        foundJedis().setex(k, seconds, v);
    }

    public long sortedsetAdd(String key, Object value, double score) {
        Long success = foundJedis().zadd(key2Bytes(key), score, object2Bytes(value));
        return success == null ? 0L : success.longValue();
    }

    public long sortedsetRem(String key, Object value) {
        Long success = foundJedis().zrem(key2Bytes(key), new byte[][] { object2Bytes(value) });
        return success == null ? 0L : success.longValue();
    }

    public <E> List<E> sortedsetRange(String key, int start, int end, Class<E> klass) {
        Set<byte[]> set = foundJedis().zrange(key2Bytes(key), start, end);
        List list = CommonUtils.emptyList();
        if (!CommonUtils.isEmpty(set)) {
            list = new ArrayList(set.size());
            for (byte[] bytes : set) {
                Object e = bytes2Object(bytes, klass);
                list.add(e);
            }
        }
        return list;
    }

    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        try {
            Method impl = (Method)proxyApiMethodMap.get(method.getName());
            return impl != null ? handleProxyApiMethod(impl, args) : handleJedisMethod(method, args);
        } catch (Throwable t) {
            release();
            throw CommonUtils.foundRealThrowable(t);
        }
    }

    private Object handleProxyApiMethod(Method method, Object[] args) throws Throwable {
        return method.invoke(this, args);
    }

    private Object handleJedisMethod(Method method, Object[] args) throws Throwable {
        return method.invoke(foundJedis(), args);
    }

    private List<?> cas(String key, Object value, int seconds) {
        byte[] k = key2Bytes(key);
        byte[] v = object2Bytes(value);
        for (int index = 0; index < 10; index++) {
            foundJedis().watch(new byte[][] { k });
            Transaction t = foundJedis().multi();

            if (seconds <= 0)
                t.set(k, v);
            else {
                t.setex(k, seconds, v);
            }
            List result = t.exec();
            if (result != null)
                return result;
        }
        return null;
    }

    private static byte[] key2Bytes(String key) {
        if (CommonUtils.isEmpty(key))
            throw new IllegalArgumentException("empty key=" + key);
        return filterNull(key).getBytes(UTF8);
    }

    private static byte[] object2Bytes(Object value) {
        if (value == null)
            throw new NullPointerException("value is null.");
        return JSON.toJSONBytes(value, new SerializerFeature[0]);
    }

    private static <T> T bytes2Object(byte[] bytes, Class<T> klass) {
        return bytes == null ? null : (T) JSON.parseObject(bytes, klass, new Feature[0]);
    }

    private static String filterNull(String key) {
        return key == null ? "null" : key;
    }

    static {
        init();
    }
}
