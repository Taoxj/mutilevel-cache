package com.xjt.mutilevel.cache;

import com.xjt.mutilevel.util.CacheUtil;
import org.springframework.cache.interceptor.SimpleKeyGenerator;

import java.lang.reflect.Method;

/**
 * 类说明
 *
 * @author sunney-黄江南
 * @version V1.0  创建时间：2019年5月6日 下午6:32:34
 */
public class MultiKeyGenerator extends SimpleKeyGenerator {


    @Override
    public Object generate(Object target, Method method, Object... params) {
        String key = CacheUtil.getKey(params);
        String valueKey = String.format("%s:%s:%s", target.getClass().getName(), method.getName(), key);
        return valueKey;
    }
}
