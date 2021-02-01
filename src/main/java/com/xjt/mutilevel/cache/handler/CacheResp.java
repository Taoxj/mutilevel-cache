package com.xjt.mutilevel.cache.handler;

/**
 * 类说明
 *
 * @author sunney
 * @version V1.0  创建时间：2019年9月30日 上午10:09:57
 */
public class CacheResp {


    public CacheResp() {

    }

    public CacheResp(String cacheName) {
        super();
        this.cacheName = cacheName;
    }

    /**
     * 缓存标志
     */
    private String cacheName;

    /**
     * 是否使用二级缓存
     */
    private boolean l2CacheFlag;

    /**
     * redis缓存过期时间
     */
    private int redisCacheTimeOut;
    /**
     * 是否开启异步刷新缓存
     */
    private boolean asynFlag = Boolean.TRUE;
    /**
     * 用户触发异步刷新缓存时间间隔
     */
    private int asynTimeOut;

    public String getCacheName() {
        return cacheName;
    }

    public boolean isL2CacheFlag() {
        return l2CacheFlag;
    }

    public CacheResp setL2CacheFlag(boolean l2CacheFlag) {
        this.l2CacheFlag = l2CacheFlag;
        return this;
    }

    public int getRedisCacheTimeOut() {
        return redisCacheTimeOut;
    }

    public CacheResp setRedisCacheTimeOut(int redisCacheTimeOut) {
        this.redisCacheTimeOut = redisCacheTimeOut;
        return this;
    }

    public boolean isAsynFlag() {
        return asynFlag;
    }

    public CacheResp setAsynFlag(boolean asynFlag) {
        this.asynFlag = asynFlag;
        return this;

    }

    public int getAsynTimeOut() {
        return asynTimeOut;
    }

    public CacheResp setAsynTimeOut(int asynTimeOut) {
        this.asynTimeOut = asynTimeOut;
        return this;
    }


}