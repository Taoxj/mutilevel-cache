package com.xjt.mutilevel.config;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.github.benmanes.caffeine.cache.CacheLoader;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.google.common.collect.Lists;
import com.xjt.mutilevel.cache.MultiCacheManager;
import com.xjt.mutilevel.cache.handler.CacheInit;
import com.xjt.mutilevel.cache.handler.CacheResp;
import com.xjt.mutilevel.cache.handler.ICacheHandler;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.cache.RedisCacheWriter;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettucePoolingClientConfiguration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 类说明
 *
 * @author sunney
 * @version V1.0 创建时间：2018年5月11日 下午5:51:12
 */
@Configuration
public class CacheInitConfig extends CachingConfigurerSupport {


    @Autowired
    @Qualifier("iCacheHandler")
    private ICacheHandler iCacheHandler;

    @Autowired
    @Qualifier("cacheInit")
    private CacheInit cacheInit;

    @Autowired
    private RedisCacheManager redisCacheManager;

    @Autowired
    private CaffeineCacheManager caffeineCacheManager;


    @Bean(name = "cacheInit")
    public CacheInit getCacheInit() {
        CacheInit init = new CacheInit();
        init.setiCacheHandler(iCacheHandler);
        return init;
    }


    @Bean(name = "caffeineCacheManager")
    public CaffeineCacheManager guavaCacheManager() {
        Map<String, CacheResp> map = cacheInit.getCachTimeConfigMap();
        Collection<CacheResp> colletions = map.values();
        List<String> cacheNames = Lists.newArrayList();
        for (CacheResp cacheResp : colletions) {
            if (cacheResp.isL2CacheFlag()) {
                cacheNames.add(cacheResp.getCacheName());
            }
        }
        CaffeineCacheManager cacheManager = new CaffeineCacheManager();
        cacheManager.setCacheNames(cacheNames);
        Caffeine<Object, Object> caffeine = Caffeine.newBuilder();
        //caffeine.build(cacheLoader);
        caffeine.maximumSize(10000);
        //caffeine.refreshAfterWrite(1, TimeUnit.HOURS);
        cacheManager.setCaffeine(caffeine);
        return cacheManager;
    }

    @Bean
    public CacheLoader<Object, Object> cacheManagerWithAsyncCacheLoader() {
        CacheLoader<Object, Object> loadingCache = new CacheLoader<Object, Object>() {
            @Override
            public Object load(Object key) throws Exception {
                return key;
            }

        };
        return loadingCache;
    }

    @Bean(name = "reidsCacheManager")
    public RedisCacheManager redisCacheManager(@Autowired @Qualifier("redisFactory") RedisConnectionFactory redisConnectionFactory) {
        return new RedisCacheManager(RedisCacheWriter.nonLockingRedisCacheWriter(redisConnectionFactory), this.getRedisCacheConfigurationWithTtl(5), // 默认策略，未配置的
                this.getRedisCacheConfigurationMap() // 指定 key 策略
        );
    }


    private Map<String, RedisCacheConfiguration> getRedisCacheConfigurationMap() {
        Map<String, RedisCacheConfiguration> redisCacheConfigurationMap = new HashMap<>(16);
        // SsoCache和BasicDataCache进行过期时间配置
        Map<String, CacheResp> map = cacheInit.getCachTimeConfigMap();
        if (map != null) {
            Collection<CacheResp> colletions = map.values();
            for (CacheResp cacheResp : colletions) {
                redisCacheConfigurationMap.put(cacheResp.getCacheName(), this.getRedisCacheConfigurationWithTtl(cacheResp.getRedisCacheTimeOut()));
            }
        }
        return redisCacheConfigurationMap;
    }

    private RedisCacheConfiguration getRedisCacheConfigurationWithTtl(Integer seconds) {
        Jackson2JsonRedisSerializer<Object> jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer<>(Object.class);
        ObjectMapper om = new ObjectMapper();
        om.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        om.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);
        om.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        om.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        om.registerModule(new JavaTimeModule());
        jackson2JsonRedisSerializer.setObjectMapper(om);

        RedisCacheConfiguration redisCacheConfiguration = RedisCacheConfiguration.defaultCacheConfig();
        redisCacheConfiguration = redisCacheConfiguration.serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(jackson2JsonRedisSerializer))
                .entryTtl(Duration.ofSeconds(seconds));

        return redisCacheConfiguration;
    }

    @Bean
    @Primary
    public CacheManager multiCacheManager(@SuppressWarnings("rawtypes") RedisTemplate redisTemplate) {
        MultiCacheManager cacheManager = new MultiCacheManager();
        // 设置guava cache为预计缓存
        cacheManager.setCaffeineCacheManager(caffeineCacheManager);
        // 设置redis缓存为第二级缓存
        cacheManager.setRedisCacheManager(redisCacheManager);
        // 设置缓存
        return cacheManager;
    }

    @Bean
    @Primary
    @SuppressWarnings("all")
    public RedisTemplate<String, Object> redisTemplate(@Autowired @Qualifier("redisFactory") RedisConnectionFactory factory) {
        RedisTemplate<String, Object> template = new RedisTemplate<String, Object>();
        template.setConnectionFactory(factory);
        Jackson2JsonRedisSerializer jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer(Object.class);
        ObjectMapper om = new ObjectMapper();
        om.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        om.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);
        om.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        om.registerModule(new JavaTimeModule());
        jackson2JsonRedisSerializer.setObjectMapper(om);
        StringRedisSerializer stringRedisSerializer = new StringRedisSerializer();
        // key采用String的序列化方式
        template.setKeySerializer(stringRedisSerializer);
        // hash的key也采用String的序列化方式
        template.setHashKeySerializer(stringRedisSerializer);
        // value序列化方式采用jackson
        template.setValueSerializer(jackson2JsonRedisSerializer);
        // hash的value序列化方式采用jackson
        template.setHashValueSerializer(jackson2JsonRedisSerializer);
        template.afterPropertiesSet();
        return template;
    }


    @Bean(name = "redisConfig")
    @ConfigurationProperties(prefix = "spring.redis")
    public RedisStandaloneConfiguration redisConfig() {
        return new RedisStandaloneConfiguration();
    }

    @Bean(name = "redisConfig2")
    @ConfigurationProperties(prefix = "spring.redis2")
    public RedisStandaloneConfiguration redisConfig2() {
        return new RedisStandaloneConfiguration();
    }

    @Bean(name = "redisConfig3")
    @ConfigurationProperties(prefix = "spring.redis3")
    public RedisStandaloneConfiguration redisConfig3() {
        return new RedisStandaloneConfiguration();
    }

    @Bean(name = "redisPool")
    @Primary
    @ConfigurationProperties(prefix = "spring.redis.pool")
    public GenericObjectPoolConfig redisPool() {
        return new GenericObjectPoolConfig();
    }


    @Bean(name = "redisPool3")
    @ConfigurationProperties(prefix = "spring.redis3.pool")
    public GenericObjectPoolConfig redisPool3() {
        return new GenericObjectPoolConfig();
    }

    @Bean(name = "redisPool2")
    @ConfigurationProperties(prefix = "spring.redis2.pool")
    public GenericObjectPoolConfig redisPool2() {
        return new GenericObjectPoolConfig();
    }


    @Bean(name = "redisFactory")
    @Primary
    public LettuceConnectionFactory factory(@Autowired @Qualifier("redisPool") GenericObjectPoolConfig config, @Autowired @Qualifier("redisConfig") RedisStandaloneConfiguration redisConfig) {
        LettuceClientConfiguration clientConfiguration = LettucePoolingClientConfiguration.builder()
                //8秒超时
                .poolConfig(config).commandTimeout(Duration.ofMillis(8000)).build();
        return new LettuceConnectionFactory(redisConfig, clientConfiguration);
    }


    @Bean(name = "redisFactory2")
    public LettuceConnectionFactory factory2(@Autowired @Qualifier("redisPool2") GenericObjectPoolConfig config, @Autowired @Qualifier("redisConfig2") RedisStandaloneConfiguration redisConfig2) {
        LettuceClientConfiguration clientConfiguration = LettucePoolingClientConfiguration.builder()
                //8秒超时
                .poolConfig(config).commandTimeout(Duration.ofMillis(8000)).build();
        return new LettuceConnectionFactory(redisConfig2, clientConfiguration);
    }

    @Bean(name = "redisFactory3")
    public LettuceConnectionFactory factory3(@Autowired @Qualifier("redisPool3") GenericObjectPoolConfig config, @Autowired @Qualifier("redisConfig3") RedisStandaloneConfiguration redisConfig3) {
        LettuceClientConfiguration clientConfiguration = LettucePoolingClientConfiguration.builder()
                //8秒超时
                .poolConfig(config).commandTimeout(Duration.ofMillis(8000)).build();
        return new LettuceConnectionFactory(redisConfig3, clientConfiguration);
    }


    @SuppressWarnings({"rawtypes", "unchecked"})
    @Bean(name = "redisTemplate2")
    public RedisTemplate<String, Object> redisTemplateB(@Autowired @Qualifier("redisFactory2") LettuceConnectionFactory factoryB) {
        RedisTemplate<String, Object> template = new RedisTemplate<String, Object>();
        template.setConnectionFactory(factoryB);
        Jackson2JsonRedisSerializer jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer(Object.class);
        ObjectMapper om = new ObjectMapper();
        om.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        om.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);
        om.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        om.registerModule(new JavaTimeModule());
        jackson2JsonRedisSerializer.setObjectMapper(om);
        StringRedisSerializer stringRedisSerializer = new StringRedisSerializer();
        // key采用String的序列化方式
        template.setKeySerializer(stringRedisSerializer);
        // hash的key也采用String的序列化方式
        template.setHashKeySerializer(stringRedisSerializer);
        // value序列化方式采用jackson
        template.setValueSerializer(jackson2JsonRedisSerializer);
        // hash的value序列化方式采用jackson
        template.setHashValueSerializer(jackson2JsonRedisSerializer);
        template.afterPropertiesSet();
        return template;
    }

    @Bean(name = "redisTemplate3")
    public RedisTemplate<String, Object> redisTemplateC(@Autowired @Qualifier("redisFactory3") LettuceConnectionFactory factoryC) {
        RedisTemplate<String, Object> template = new RedisTemplate<String, Object>();
        template.setConnectionFactory(factoryC);
        Jackson2JsonRedisSerializer jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer(Object.class);
        ObjectMapper om = new ObjectMapper();
        om.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        om.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);
        om.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        om.registerModule(new JavaTimeModule());
        jackson2JsonRedisSerializer.setObjectMapper(om);
        StringRedisSerializer stringRedisSerializer = new StringRedisSerializer();
        // key采用String的序列化方式
        template.setKeySerializer(stringRedisSerializer);
        // hash的key也采用String的序列化方式
        template.setHashKeySerializer(stringRedisSerializer);
        // value序列化方式采用jackson
        template.setValueSerializer(jackson2JsonRedisSerializer);
        // hash的value序列化方式采用jackson
        template.setHashValueSerializer(jackson2JsonRedisSerializer);
        template.afterPropertiesSet();
        return template;
    }
}