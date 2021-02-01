package com.xjt.mutilevel.datasource;

import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

/**
 * 类说明
 *
 * @author sunney
 * @version V1.0 创建时间：2019年4月18日 下午5:12:57
 */

public class DynamicDataSource extends AbstractRoutingDataSource {

    @Override
    protected Object determineCurrentLookupKey() {
        Object obj = DBContextHolder.getDBType();
        return obj;
    }

}
