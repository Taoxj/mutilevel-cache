package com.xjt.mutilevel.service;

import com.xjt.mutilevel.config.CacheConfig;
import com.xjt.mutilevel.domain.User;
import com.xjt.mutilevel.mapper.UserMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

/**
 * @author kevin
 * @date 2019-11-20 10:16
 */
@Service
@Slf4j
public class UserService {

    @Autowired
    private UserMapper userMapper;

    @Cacheable(value = CacheConfig.DETAIL, key = "'getUserById:'+#userId")
    public User getUserById(Long userId) {
        log.info("查询用户信息，id:{}", userId);
        return userMapper.selectById(userId);
    }

}
