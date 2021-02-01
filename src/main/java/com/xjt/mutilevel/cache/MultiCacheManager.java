package com.xjt.mutilevel.cache;

import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.data.redis.cache.RedisCacheManager;

import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * 类说明
 *
 * @author sunney-黄江南
 * @version V1.0  创建时间：2019年5月3日 下午2:24:58
 */
public class MultiCacheManager implements CacheManager {

    private CaffeineCacheManager caffeineCacheManager;

    private RedisCacheManager redisCacheManager;


    private final ConcurrentMap<String, MultiCacheTemplate> cacheMap = new ConcurrentHashMap<String, MultiCacheTemplate>(16);


    @Override
    public Cache getCache(String name) {
        MultiCacheTemplate cache = this.cacheMap.get(name);
        if (cache == null) {
            synchronized (this.cacheMap) {
                cache = this.cacheMap.get(name);
                if (cache == null) {
                    cache = createMultiCache(name);
                    this.cacheMap.put(name, cache);
                    return cache;
                }
            }
        }
        return cache;

    }


    protected MultiCacheTemplate createMultiCache(String name) {
        caffeineCacheManager.getCache(name);
        redisCacheManager.getCache(name);
        return new MultiCacheTemplate(this, name);
    }

    @Override
    public Collection<String> getCacheNames() {
        return cacheMap.keySet();
    }


    public CaffeineCacheManager getCaffeineCacheManager() {
        return caffeineCacheManager;
    }


    public void setCaffeineCacheManager(CaffeineCacheManager caffeineCacheManager) {
        this.caffeineCacheManager = caffeineCacheManager;
    }


    public RedisCacheManager getRedisCacheManager() {
        return redisCacheManager;
    }

    public void setRedisCacheManager(RedisCacheManager redisCacheManager) {
        this.redisCacheManager = redisCacheManager;
    }


    public ConcurrentMap<String, MultiCacheTemplate> getCacheMap() {
        return cacheMap;
    }


}
 