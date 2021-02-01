package com.xjt.mutilevel.cache.handler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 类说明
 *
 * @author sunney
 * @version V1.0 创建时间：2019年9月29日 下午6:06:45
 */
@Component
public class CacheInit {

    private Map<String, CacheResp> cachTimeConfigMap;

    @Autowired(required = false)
    @Qualifier("iCacheHandler")
    private ICacheHandler iCacheHandler;

    public void refresh() {
        if (iCacheHandler == null) {
            return;
        }
        List<CacheResp> list = this.iCacheHandler.setCache();
        Map<String, CacheResp> cachTimeConfigMap = new HashMap<String, CacheResp>(12);
        for (CacheResp cacheResp : list) {
            cachTimeConfigMap.put(cacheResp.getCacheName(), cacheResp);
        }
        this.cachTimeConfigMap = cachTimeConfigMap;
    }

    public Map<String, CacheResp> getCachTimeConfigMap() {
        return cachTimeConfigMap;
    }

    public CacheResp getCache(String cacheName) {
        if (cachTimeConfigMap == null) {
            return null;
        }
        return cachTimeConfigMap.get(cacheName);
    }

    public void setiCacheHandler(ICacheHandler iCacheHandler) {
        this.iCacheHandler = iCacheHandler;
        this.refresh();
    }

}
