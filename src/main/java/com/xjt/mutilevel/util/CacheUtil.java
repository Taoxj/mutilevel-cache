package com.xjt.mutilevel.util;

import com.alibaba.fastjson.JSON;
import org.springframework.util.DigestUtils;

/**
 * 类说明
 *
 * @author sunney
 * @version V1.0 创建时间：2019年2月28日 下午2:24:07
 */
public class CacheUtil {

    public static String getKey(Object... obj) {
        String key = JSON.toJSONString(obj).trim();
        String sign = DigestUtils.md5DigestAsHex(key.getBytes());
        return sign;
    }
}
