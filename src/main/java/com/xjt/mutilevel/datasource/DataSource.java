package com.xjt.mutilevel.datasource;


import java.lang.annotation.*;


/**
 * 类说明
 *
 * @author sunney
 * @version V1.0  创建时间：2019年4月18日 下午5:18:32
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Documented
public @interface DataSource {

    Source value() default Source.master;

    boolean clear() default true;

}
