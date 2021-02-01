package com.xjt.mutilevel.cache;

import com.xjt.mutilevel.cache.redis.RedisRefreshMessage;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.MethodInvoker;

import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.TimeUnit;

/**
 * 类说明
 *
 * @author sunney-黄江南
 * @version V1.0 创建时间：2019年5月7日 下午4:19:05
 */
@Service
@Slf4j
public class RefreshCacheService {

    @Autowired
    private CacheManager cacheManager;

    @Autowired
    private RedissonClient redissonClient;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;


    @Value("${spring.application.name:'notInit'}")
    private String appName;

    private static ChannelTopic TOPIC = null;

    public ChannelTopic getTopic() {
        if (TOPIC == null) {
            TOPIC = new ChannelTopic(String.format("%s:%s", appName, RedisRefreshMessage.MESSAGE_KEY));
        }
        return TOPIC;

    }

    @Async
    public void refresh(CachedInvocation invocation, boolean isL2CacheFlag, String timeKey) {
        RLock lock = redissonClient.getLock(this.getClass().getName() + invocation.getKey());
        boolean flag = false;
        try {
            flag = lock.tryLock(100, 5000, TimeUnit.MILLISECONDS);
            if (!flag) {
                return;
            }
            Object obj = this.invoke(invocation);
            if (obj != null) {
                if (cacheManager instanceof MultiCacheManager) {
                    MultiCacheManager multiCacheManager = (MultiCacheManager) cacheManager;
                    MultiCacheTemplate multiCacheTemplate = multiCacheManager.getCacheMap().get(invocation.getCacheName());
                    if (multiCacheTemplate != null) {
                        multiCacheTemplate.put(invocation.getKey().toString(), obj, invocation.getCacheName());
                        InvocationRegistry.keyTimeMap.put(timeKey, System.currentTimeMillis());
                    }
                    RedisRefreshMessage message = new RedisRefreshMessage();
                    message.setKey(invocation.getKey().toString());
                    message.setCacheName(invocation.getCacheName());
                    if (isL2CacheFlag) {
                        redisTemplate.convertAndSend(getTopic().getTopic(), message);
                    }

                }
            }
        } catch (Exception e) {
            log.warn("缓存刷新错误:{}", e);
        } finally {
            if (flag) {
                lock.unlock();
            }
        }
    }

    private Object invoke(CachedInvocation invocation) {
        Object obj = null;
        MethodInvoker invoker = new MethodInvoker();
        invoker.setTargetObject(invocation.getTargetBean());
        invoker.setArguments(invocation.getArguments());
        invoker.setTargetMethod(invocation.getTargetMethod().getName());
        try {
            invoker.prepare();
            obj = invoker.invoke();
        } catch (ClassNotFoundException | NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return obj;
    }
}
