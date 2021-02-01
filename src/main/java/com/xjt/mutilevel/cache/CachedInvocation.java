package com.xjt.mutilevel.cache;

import java.lang.reflect.Method;
import java.util.Arrays;

/**
 * 类说明
 *
 * @author 黄江南
 * @version V1.0 创建时间：2019年5月7日 下午3:21:01
 */
public class CachedInvocation {

    private String cacheName;

    private Object key;

    private Object targetBean;

    private Method targetMethod;

    private Object[] arguments;


    public CachedInvocation(String cacheName, Object key, Object targetBean, Method targetMethod, Object[] arguments) {
        this.key = key;
        this.targetBean = targetBean;
        this.targetMethod = targetMethod;
        this.cacheName = cacheName;
        if (arguments != null && arguments.length != 0) {
            this.arguments = Arrays.copyOf(arguments, arguments.length);
        }
    }

    public Object getKey() {
        return key;
    }

    public void setKey(Object key) {
        this.key = key;
    }

    public Object getTargetBean() {
        return targetBean;
    }

    public void setTargetBean(Object targetBean) {
        this.targetBean = targetBean;
    }

    public Method getTargetMethod() {
        return targetMethod;
    }

    public void setTargetMethod(Method targetMethod) {
        this.targetMethod = targetMethod;
    }

    public Object[] getArguments() {
        return arguments;
    }

    public void setArguments(Object[] arguments) {
        this.arguments = arguments;
    }

    public String getCacheName() {
        return cacheName;
    }

    public void setCacheName(String cacheName) {
        this.cacheName = cacheName;
    }

}
