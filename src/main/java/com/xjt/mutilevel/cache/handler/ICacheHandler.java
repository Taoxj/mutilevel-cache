package com.xjt.mutilevel.cache.handler;

import java.util.List;

/**
 * 类说明
 *
 * @author sunney
 * @version V1.0  创建时间：2019年10月8日 下午6:26:49
 */
public interface ICacheHandler {

    /**
     * 设置缓存
     *
     * @return
     */
    public abstract List<CacheResp> setCache();

}
 