package com.xjt.mutilevel.datasource;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

/**
 * 类说明
 *
 * @author sunney
 * @version V1.0 创建时间：2019年4月18日 下午5:20:17
 */

@Aspect
@Order(value = 1)
@Component
public class DataSourceContextAop {

    @Around("@annotation(com.xjt.mutilevel.datasource.DataSource)")
    public Object setDynamicDataSource(ProceedingJoinPoint pjp) throws Throwable {
        boolean clear = true;
        try {
            Method method = this.getMethod(pjp);
            DataSource dataSource = method.getAnnotation(DataSource.class);
            clear = dataSource.clear();
            if (dataSource != null) {
                DBContextHolder.setDBType(dataSource.value().name());
            }
            return pjp.proceed();
        } finally {
            if (clear) {
                DBContextHolder.clearDBType();
            }

        }
    }

    public Method getMethod(JoinPoint pjp) {
        Method method = null;
        MethodSignature signature = (MethodSignature) pjp.getSignature();
        method = signature.getMethod();
        return method;
    }

}
