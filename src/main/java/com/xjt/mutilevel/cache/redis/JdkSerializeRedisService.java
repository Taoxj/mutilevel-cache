package com.xjt.mutilevel.cache.redis;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

/**
 * 类说明
 *
 * @author sunney
 * @version V1.0 创建时间：2018年5月11日 下午3:26:03
 */

@Component
public class JdkSerializeRedisService {

    @Autowired
    private RedisTemplate<?, ?> redisTemplate;

    @SuppressWarnings("unchecked")
    public <T> T getCacheValue(String key, Class<T> targetClass) {
        final byte[] bkey = key.getBytes();
        byte[] result = redisTemplate.execute(new RedisCallback<byte[]>() {
            @Override
            public byte[] doInRedis(RedisConnection connection) throws DataAccessException {
                return connection.get(bkey);
            }
        });
        return (T) SerializeUtils.deserialize(result);
    }

    public boolean setCacheValue(String key, Object value, int expireTime) {
        final byte[] bkey = key.getBytes();
        final byte[] bvalue = SerializeUtils.serialize(value);
        return redisTemplate.execute(new RedisCallback<Boolean>() {
            @Override
            public Boolean doInRedis(RedisConnection connection) throws DataAccessException {
                connection.setEx(bkey, expireTime, bvalue);
                return true;
            }
        });
    }
}
