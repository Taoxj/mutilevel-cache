package com.xjt.mutilevel.cache.redis;

import com.xjt.mutilevel.cache.InvocationRegistry;
import com.xjt.mutilevel.cache.MultiCacheManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.cache.Cache;
import org.springframework.cache.Cache.ValueWrapper;
import org.springframework.stereotype.Component;

/**
 * 类说明
 *
 * @author sunney
 * @version V1.0 创建时间：2020年3月16日 下午5:23:28
 */

@Slf4j
@ConditionalOnBean(name = "iCacheHandler")
@Component
public class MessageSubscriber {


    @Autowired
    private MultiCacheManager cacheManager;

    public void onMessage(RedisRefreshMessage message, String pattern) {
        try {
            String timeKey = String.format(InvocationRegistry.KEY_FORMAT, message.getCacheName(), message.getKey());
            this.toRefresh(message, timeKey);
        } catch (Exception e) {
            log.warn("缓存刷新异常:{}", e);
        }

    }

    private void toRefresh(RedisRefreshMessage message, String timeKey) {
        String cacheName = message.getCacheName();
        Cache redisCache = cacheManager.getRedisCacheManager().getCache(cacheName);
        if (redisCache != null) {
            Long time = InvocationRegistry.keyTimeMap.getIfPresent(timeKey);
            Long now = System.currentTimeMillis();
            if (time != null) {
                Long limitTime = now - time;
                if (limitTime < 1000) {
                    log.info("缓存刷新不需要:cacheId:{}", timeKey);
                    return;
                }
            }
            ValueWrapper value = redisCache.get(message.getKey());
            if (value != null) {
                Object obj = value.get();
                Cache cache = cacheManager.getCaffeineCacheManager().getCache(cacheName);
                if (cache != null && message.getKey() != null) {
                    cache.put(message.getKey(), obj);
                    InvocationRegistry.keyTimeMap.put(timeKey, now);
                }
            }
        }

    }

    public MultiCacheManager getCacheManager() {
        return cacheManager;
    }

    public void setCacheManager(MultiCacheManager cacheManager) {
        this.cacheManager = cacheManager;
    }


}
