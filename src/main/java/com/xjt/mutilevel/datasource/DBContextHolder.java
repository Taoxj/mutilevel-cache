package com.xjt.mutilevel.datasource;

/**
 * 类说明
 *
 * @author sunney
 * @version V1.0 创建时间：2019年4月18日 下午5:17:51
 */
public class DBContextHolder {

    private static final ThreadLocal<String> contextHolder = new ThreadLocal<String>();

    public static void setDBType(String dbType) {
        contextHolder.set(dbType);
    }

    public static String getDBType() {
        return contextHolder.get();
    }

    public static void clearDBType() {
        contextHolder.remove();
    }

}
