package com.xjt.mutilevel.config;

import com.google.common.collect.Lists;
import com.xjt.mutilevel.cache.handler.CacheResp;
import com.xjt.mutilevel.cache.handler.ICacheHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * 实现该类开启缓存
 *
 * @author 黄江南
 * @version V1.0 创建时间：2019年9月30日 上午10:08:21
 */
@Configuration
public class CacheConfig {


    public static final String INDEX = "index";

    public static final String DETAIL = "detail";

    public static final String STATIC = "static";

    public static final String HOME_PAGE = "homePage";

    public static final String OPERATION_SIDE = "operationSide";

    public static final String LOGISTICS = "logistics";

    public static final String SEC_KILL = "secKill";

    public static final String TB = "tb";

    public static final String SBT = "SBT";

    public static final String USER = "user";

    public static final String BANNER = "banner";

    public static final String GOODS_TYPE = "goodsType";

    public static final String MESSAGE = "message";

    public static final String INDEX_BANNER = "indexBanner";

    public static final String CHANNEL = "channel";


    @Bean(name = "iCacheHandler")
    public ICacheHandler getCacheHandler() {
        ICacheHandler init = new ICacheHandler() {

            @Override
            public List<CacheResp> setCache() {
                List<CacheResp> respList = Lists.newArrayList();
                respList.add(new CacheResp(INDEX)
                        .setRedisCacheTimeOut(2 * 60) // redis缓存过期时间
                        .setL2CacheFlag(true)     // 是否开启二级缓存，默认只有redis缓存，堆缓存永远不过期，最大10000
                        .setAsynFlag(true)        // 是否开启异步刷新缓存
                        .setAsynTimeOut(30));     // 异步用户触发缓存过期时间
                respList.add(new CacheResp(DETAIL)
                        .setRedisCacheTimeOut(2 * 60)
                        .setL2CacheFlag(false)
                        .setAsynFlag(true)
                        .setAsynTimeOut(30));
                respList.add(new CacheResp(STATIC)
                        .setRedisCacheTimeOut(24 * 60 * 60 * 365)
                        .setL2CacheFlag(true)
                        .setAsynFlag(true)
                        .setAsynTimeOut(30));
                respList.add(new CacheResp(HOME_PAGE)
                        .setRedisCacheTimeOut(2 * 60)
                        .setL2CacheFlag(true)
                        .setAsynFlag(true)
                        .setAsynTimeOut(30));
                respList.add(new CacheResp(OPERATION_SIDE)
                        .setRedisCacheTimeOut(5 * 60)
                        .setL2CacheFlag(false)
                        .setAsynFlag(true)
                        .setAsynTimeOut(30));
                respList.add(new CacheResp(LOGISTICS)
                        .setRedisCacheTimeOut(1 * 60 * 60)
                        .setL2CacheFlag(false)
                        .setAsynFlag(true)
                        .setAsynTimeOut(30));
                respList.add(new CacheResp(SEC_KILL)
                        .setRedisCacheTimeOut(60 * 60 * 4)
                        .setL2CacheFlag(false)
                        .setAsynFlag(true)
                        .setAsynTimeOut(30));
                respList.add(new CacheResp(TB)
                        .setRedisCacheTimeOut(60 * 60 * 4)
                        .setL2CacheFlag(false)
                        .setAsynFlag(true)
                        .setAsynTimeOut(30));
                respList.add(new CacheResp(SBT)
                        .setRedisCacheTimeOut(60 * 30)
                        .setL2CacheFlag(false)
                        .setAsynFlag(true)
                        .setAsynTimeOut(30));
                respList.add(new CacheResp(USER)
                        .setRedisCacheTimeOut(60 * 10)
                        .setL2CacheFlag(false)
                        .setAsynFlag(true)
                        .setAsynTimeOut(30));
                respList.add(new CacheResp(BANNER)
                        .setRedisCacheTimeOut(60 * 5)
                        .setL2CacheFlag(false)
                        .setAsynFlag(true)
                        .setAsynTimeOut(30));
                respList.add(new CacheResp(INDEX_BANNER)
                        .setRedisCacheTimeOut(60 * 2)
                        .setL2CacheFlag(false)
                        .setAsynFlag(true)
                        .setAsynTimeOut(30));
                respList.add(new CacheResp(GOODS_TYPE)
                        .setRedisCacheTimeOut(2 * 60)
                        .setL2CacheFlag(false)
                        .setAsynFlag(false)
                        .setAsynTimeOut(30));
                respList.add(new CacheResp(MESSAGE)
                        .setRedisCacheTimeOut(2 * 60)
                        .setL2CacheFlag(false)
                        .setAsynFlag(false)
                        .setAsynTimeOut(30));
                respList.add(new CacheResp(CHANNEL)
                        .setRedisCacheTimeOut(60 * 60 * 24)
                        .setL2CacheFlag(false)
                        .setAsynFlag(true)
                        .setAsynTimeOut(30));
                return respList;
            }
        };
        return init;

    }


}
