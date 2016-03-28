package com.lucare.tsession;

import com.alibaba.fastjson.JSON;
import com.lucare.common.utils.CommonUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Lucare.Feng on 2016/3/24.
 */
public class TCache {
    private String key;
    private int expireSecond = 1800;
    private Map<String, Entry> attributes = new HashMap();

    public TCache() {
    }

    public TCache(String key) {
        this.key = key;
    }

    public TCache(String key, int expireSecond) {
        this.key = key;
        this.expireSecond = expireSecond;
    }

    public String getKey() {
        return this.key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public int getExpireSecond() {
        return this.expireSecond;
    }

    public void setExpireSecond(int expireSecond) {
        this.expireSecond = expireSecond;
    }

    public TCache setAttribute(String k, Object v) {
        if(k != null && v != null) {
            String value = JSON.toJSONString(v);
            String className = v.getClass().getName();
            TCache.Entry e = new TCache.Entry(k, value, className, System.currentTimeMillis());
            this.attributes.put(k, e);
            return this;
        } else {
            throw new NullPointerException("k=" + k + "or v=" + v + " is null.");
        }
    }

    public <T> T getAttribute(String key) {
        TCache.Entry e = (TCache.Entry)this.attributes.get(key);
        Object t = null;
        if(e != null) {
            String value = e.value;
            String className = e.valueClassName;
            Class klass = CommonUtils.classForName(className);
            t = JSON.parseObject(value, klass);
        }

        return (T) t;
    }

    public <T> T removeAttribute(String key) {
        return (T) this.attributes.remove(key);
    }

    public static class Entry {
        String key;
        String value;
        String valueClassName;
        long createtime;

        public Entry() {
        }

        public Entry(String key, String value, String valueClassName, long createtime) {
            this.key = key;
            this.value = value;
            this.valueClassName = valueClassName;
            this.createtime = createtime;
        }

        public String getKey() {
            return this.key;
        }

        public void setKey(String key) {
            this.key = key;
        }

        public String getValue() {
            return this.value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        public String getValueClassName() {
            return this.valueClassName;
        }

        public void setValueClassName(String valueClassName) {
            this.valueClassName = valueClassName;
        }

        public long getCreatetime() {
            return this.createtime;
        }

        public void setCreatetime(long createtime) {
            this.createtime = createtime;
        }
    }
}
