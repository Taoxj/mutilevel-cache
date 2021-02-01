package com.xjt.mutilevel.cache.redis;

/**
 * 类说明
 *
 * @author sunney
 * @version V1.0  创建时间：2020年3月16日 下午5:15:50
 */
public class RedisRefreshMessage {


    public static final String MESSAGE_KEY = "/redis/cache/refresh";

    private String key;

    private String cacheName;

    private Long sendTime;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getCacheName() {
        return cacheName;
    }

    public void setCacheName(String cacheName) {
        this.cacheName = cacheName;
    }

    public Long getSendTime() {
        return sendTime;
    }

    public void setSendTime(Long sendTime) {
        this.sendTime = sendTime;
    }


}
 