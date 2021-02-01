package com.xjt.mutilevel.cache;

import org.springframework.cache.Cache;

import java.util.concurrent.Callable;

/**
 * 类说明
 *
 * @author sunney-黄江南
 * @version V1.0  创建时间：2019年5月3日 下午2:42:22
 */
public class MultiCacheTemplate implements Cache {

    //当缓存很久没被更新超过5分钟，直接走数据库，再刷新
    private static int TIME_OUT = 1000 * 60 * 5;

    private MultiCacheManager multiCacheManager;

    private String name;


    public MultiCacheTemplate(MultiCacheManager multiCacheManager, String name) {
        super();
        this.multiCacheManager = multiCacheManager;
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Object getNativeCache() {
        return this;
    }


    @Override
    public ValueWrapper get(Object key) {
        Long time = InvocationRegistry.keyTimeMap.getIfPresent(key);
        if (time != null) {
            Long subTime = System.currentTimeMillis() - time;
            if (subTime > TIME_OUT) {
                return null;
            }
        }
        //检查缓存是否过期
        //检查是否是永久缓存
        //如果是永久缓存
        Cache guavaCache = multiCacheManager.getCaffeineCacheManager().getCache(getName());
        if (guavaCache != null) {
            ValueWrapper value = guavaCache.get(key);
            if (value != null) {
                return value;
            }
        }
        Cache redisCache = multiCacheManager.getRedisCacheManager().getCache(getName());
        if (redisCache != null) {
            ValueWrapper value = redisCache.get(key);
            if (value != null) {
                return value;
            }
        }
        return null;

    }

    @Override
    public <T> T get(Object key, Class<T> type) {
        Cache guavaCache = multiCacheManager.getCaffeineCacheManager().getCache(getName());
        if (guavaCache != null) {
            return guavaCache.get(key, type);
        }
        Cache redisCache = multiCacheManager.getRedisCacheManager().getCache(getName());
        if (redisCache != null) {
            return redisCache.get(key, type);
        }
        return null;
    }

    @Override
    public <T> T get(Object key, Callable<T> valueLoader) {
        Cache guavaCache = multiCacheManager.getCaffeineCacheManager().getCache(getName());
        if (guavaCache != null) {
            return guavaCache.get(key, valueLoader);
        }
        Cache redisCache = multiCacheManager.getRedisCacheManager().getCache(getName());
        if (redisCache != null) {
            return redisCache.get(key, valueLoader);
        }
        return null;
    }

    @Override
    public void put(Object key, Object value) {
        this.put(key, value, getName());
    }

    public void put(Object key, Object value, String cacheName) {
        if (cacheName == null) {
            cacheName = this.getName();
        }
        Cache guavaCache = multiCacheManager.getCaffeineCacheManager().getCache(cacheName);
        if (guavaCache != null) {
            guavaCache.put(key, value);
        }
        Cache redisCache = multiCacheManager.getRedisCacheManager().getCache(cacheName);
        if (redisCache != null) {
            redisCache.put(key, value);
        }
    }

    @Override
    public ValueWrapper putIfAbsent(Object key, Object value) {
        Cache guavaCache = multiCacheManager.getCaffeineCacheManager().getCache(getName());
        if (guavaCache != null) {
            return guavaCache.putIfAbsent(key, value);
        }
        Cache redisCache = multiCacheManager.getRedisCacheManager().getCache(getName());
        if (redisCache != null) {
            return redisCache.putIfAbsent(key, value);
        }
        return null;
    }

    @Override
    public void evict(Object key) {
        Cache guavaCache = multiCacheManager.getCaffeineCacheManager().getCache(getName());
        if (guavaCache != null) {
            guavaCache.evict(key);
        }
        Cache redisCache = multiCacheManager.getRedisCacheManager().getCache(getName());
        if (redisCache != null) {
            redisCache.evict(key);
        }
    }

    @Override
    public void clear() {
        Cache guavaCache = multiCacheManager.getCaffeineCacheManager().getCache(getName());
        if (guavaCache != null) {
            guavaCache.clear();
        }
        Cache redisCache = multiCacheManager.getRedisCacheManager().getCache(getName());
        if (redisCache != null) {
            redisCache.clear();
        }
    }

    public MultiCacheManager getMultiCacheManager() {
        return multiCacheManager;
    }

    public void setMultiCacheManager(MultiCacheManager multiCacheManager) {
        this.multiCacheManager = multiCacheManager;
    }

    public void setName(String name) {
        this.name = name;
    }


}
 