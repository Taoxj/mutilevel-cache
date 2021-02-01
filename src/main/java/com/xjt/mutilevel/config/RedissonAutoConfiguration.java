package com.xjt.mutilevel.config;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.redisson.config.SingleServerConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

/**
 * 类说明
 *
 * @author sunney
 * @version V1.0 创建时间：2018年5月11日 下午7:50:30
 */

@Configuration
public class RedissonAutoConfiguration {

    @Value(value = "${spring.redis.host}")
    private String host;

    @Value(value = "${spring.redis.port}")
    private String port;

    @Value(value = "${spring.redis.password}")
    private String password;

    @Value(value = "#{${spring.redis.database}}")
    private int database;

    @Bean
    public RedissonClient getRedissonClient() {
        Config config = new Config();
        String format = "redis://%s:%s";
        String address = String.format(format, host, port);
        SingleServerConfig singleConfig = config.useSingleServer().setAddress(address).setConnectionPoolSize(10);
        if (!StringUtils.isEmpty(password)) {
            singleConfig.setPassword(password);
        }
        singleConfig.setDatabase(database);
        RedissonClient client = Redisson.create(config);
        return client;
    }

}
