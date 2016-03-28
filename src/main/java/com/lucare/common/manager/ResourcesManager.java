package com.lucare.common.manager;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Lucare.Feng on 2016/3/27.
 */
public class ResourcesManager<T> {
    private ThreadLocal<Map<String, T>> __resources__context__ = new ThreadLocal();
    private ResourceFactory<T> __resource__factory__;

    public ResourcesManager(ResourceFactory<T> resourceFactory)
    {
        this.__resource__factory__ = resourceFactory;
    }

    public T get(String name) {
        Map container = (Map)this.__resources__context__.get();
        if (container == null) {
            container = new HashMap(8, 1.0F);
            this.__resources__context__.set(container);
        }

        Object resource = container.get(name);
        if (resource == null) {
            resource = this.__resource__factory__.getResource(name);
            if (resource != null)
                container.put(name, resource);
        }
        return (T) resource;
    }

    public void release() {
        Map<String,Object> container = (Map)this.__resources__context__.get();
        if (container != null) {
            for (String name : container.keySet()) {
                Object resource = container.get(name);
                if (resource != null) {
                    try {
                        this.__resource__factory__.returnResource(name, (T) resource);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
            container.clear();
            this.__resources__context__.remove();
        }
    }

    public static abstract interface ResourceFactory<T>
    {
        public abstract T getResource(String paramString);

        public abstract void returnResource(String paramString, T paramT);
    }
}
