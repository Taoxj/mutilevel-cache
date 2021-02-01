package com.xjt.mutilevel.cache;

import com.xjt.mutilevel.util.ExplUtils;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.core.annotation.Order;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

/**
 * 类说明
 *
 * @author 黄江南
 * @version V1.0 创建时间：2019年5月6日 下午3:56:28
 */


@Aspect
@Order(value = 1)
@Component
@Slf4j
public class MultiCacheInterceptor {

    @Autowired
    private InvocationRegistry invocationRegistry;

//	private static final LocalVariableTableParameterNameDiscoverer parameterNameDiscoverer = new LocalVariableTableParameterNameDiscoverer();


    @Around("@annotation(org.springframework.cache.annotation.Cacheable)")
    public Object refreshCache(ProceedingJoinPoint joinPoint) throws Throwable {
        try {
            Method targetMethod = this.getMethod(joinPoint);
            Object obj = joinPoint.proceed();
            if (obj != null) {
                Object[] arguments = joinPoint.getArgs();
                Cacheable cacheable = targetMethod.getAnnotation(Cacheable.class);
                String cacheName = null;
                String[] values = cacheable.value();
                if (values.length > 0) {
                    cacheName = values[0];
                }
                try {
                    StandardEvaluationContext standardEvaluationContext = new StandardEvaluationContext(joinPoint.getArgs());
                    standardEvaluationContext = setContextVariables(standardEvaluationContext, joinPoint);
                    String cacheKey = ExplUtils.generateKey(cacheable.key(), standardEvaluationContext);
                    invocationRegistry.registerInvocation(joinPoint.getTarget(), targetMethod, arguments, cacheName, cacheKey);
                } catch (Exception e) {
                    log.warn("key处理错误：{}", e);
                }
            }
            return obj;
        } catch (Exception e) {
            log.warn("缓存处理报错：{}", e);
            throw e;
        }

    }

    public Method getMethod(JoinPoint pjp) {
        Method method = null;
        MethodSignature signature = (MethodSignature) pjp.getSignature();
        method = signature.getMethod();
        return method;
    }

    private StandardEvaluationContext setContextVariables(StandardEvaluationContext standardEvaluationContext, ProceedingJoinPoint joinPoint) {
        Object[] args = joinPoint.getArgs();
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        Method targetMethod = methodSignature.getMethod();
        // 局部变量改为全局变量
//		LocalVariableTableParameterNameDiscoverer parameterNameDiscoverer = new LocalVariableTableParameterNameDiscoverer();
//		String[] parametersName = parameterNameDiscoverer.getParameterNames(targetMethod);
        String[] parametersName = methodSignature.getParameterNames();
        if (parametersName == null) {
            return standardEvaluationContext;
        }
        if (args == null || args.length <= 0) {
            return standardEvaluationContext;
        }
        for (int i = 0; i < args.length; i++) {
            if (parametersName[i] != null) {
                standardEvaluationContext.setVariable(parametersName[i], args[i]);
            }
        }
        return standardEvaluationContext;
    }

}

