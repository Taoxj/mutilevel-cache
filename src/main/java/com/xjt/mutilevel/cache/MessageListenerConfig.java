package com.xjt.mutilevel.cache;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.xjt.mutilevel.cache.redis.MessageSubscriber;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;

/**
 * 类说明
 *
 * @author sunney
 * @version V1.0  创建时间：2020年3月16日 下午5:21:57
 */

@Slf4j
@Configuration
public class MessageListenerConfig {

    @Autowired
    private RefreshCacheService refreshCacheService;

    /**
     * 将订阅器绑定到容器
     *
     * @param connectionFactory
     * @param listener
     * @return
     */
    @Bean
    @ConditionalOnBean(name = "iCacheHandler")
    public RedisMessageListenerContainer container(@Autowired @Qualifier("redisFactory") RedisConnectionFactory connectionFactory,
                                                   MessageListenerAdapter listener) {
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        container.addMessageListener(listener, refreshCacheService.getTopic());
        log.info("缓存刷新定时器初始化：{}", "成功");
        return container;
    }

    @Bean
    @ConditionalOnBean(name = "iCacheHandler")
    public MessageListenerAdapter listener(
            MessageSubscriber subscriber) {
        MessageListenerAdapter adapter = new MessageListenerAdapter(subscriber, "onMessage");
        Jackson2JsonRedisSerializer<Object> jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer<Object>(Object.class);
        ObjectMapper om = new ObjectMapper();
        om.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        om.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);
        om.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        om.registerModule(new JavaTimeModule());
        om.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);//忽略无法识别的字段
        jackson2JsonRedisSerializer.setObjectMapper(om);
        adapter.setSerializer(jackson2JsonRedisSerializer);
        adapter.afterPropertiesSet();
        return adapter;
    }


}
 