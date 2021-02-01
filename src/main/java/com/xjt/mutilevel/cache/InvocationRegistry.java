package com.xjt.mutilevel.cache;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.xjt.mutilevel.cache.handler.CacheInit;
import com.xjt.mutilevel.cache.handler.CacheResp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

/**
 * 类说明
 *
 * @author 黄江南
 * @version V1.0 创建时间：2019年5月7日 下午3:26:48
 */

@Component
public class InvocationRegistry {

    public static final String KEY_FORMAT = "%s::%s";

    public static Cache<String, Long> keyTimeMap = CacheBuilder.newBuilder().maximumSize(10000).build();

    @Autowired
    private RefreshCacheService refreshCacheService;

    @Value("${cache.refresh.timeout:30}")
    private int cacheRefreshTimeout;

    @Autowired(required = false)
    private CacheInit cacheInit;


    public void registerInvocation(Object targetBean, Method targetMethod, Object[] arguments, String cacheName, String cacheKey) {
        CacheResp cacheResp = null;
        if (cacheInit != null) {
            cacheResp = cacheInit.getCache(cacheName);
            if (cacheResp == null || !cacheResp.isAsynFlag()) {
                return;
            }
        }

        final CachedInvocation invocation = new CachedInvocation(cacheName, cacheKey, targetBean, targetMethod, arguments);
        String timeKey = String.format(KEY_FORMAT, cacheName, cacheKey);
        Long time = keyTimeMap.getIfPresent(timeKey);
        Long now = System.currentTimeMillis();
        // 检查是否存在过
        if (time == null) {
            // 如果不存在，则插入
            keyTimeMap.put(timeKey, now);
        } else {
            // 如果存在，判断是否过期
            long subTime = now - time;
            long cacheLimitTime = getCacheLimitTime(cacheResp);
            // 如果过期则添加刷新任务，并且刷新过期时间
            if (subTime > cacheLimitTime) {
                keyTimeMap.put(timeKey, now);
                this.refreshCacheService.refresh(invocation, cacheResp.isL2CacheFlag(), timeKey);
            }
        }

    }

    public static long getCacheLimitTime(CacheResp cacheResp) {
        //缓存距离上次更新时间30秒
        if (cacheResp != null && cacheResp.getAsynTimeOut() != 0) {
            return cacheResp.getAsynTimeOut() * 1000;
        }
        return 1000 * 30;

    }
}
