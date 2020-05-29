/**
 * Project Name:woju
 * File Name:LogDetailAnnotation.java
 * Package Name:com.bopin.woju.interceptor
 * Date:2018年1月29日 上午9:43:28
 * Copyright (c) 2017, www.bkclouds.com All Rights Reserved.
 */
package com.bopin.notice.interceptor;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
/**
 * 
 * @ClassName: LogDetailAnnotation
 * @Description: TODO(这里用一句话描述这个类的作用)
 * @author ZhangXiurui
 * @date 2018年1月29日 上午9:43:28
 * 
 * @version
 * @since JDK 1.8
 * @see
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface LogDetailAnnotation
{
    String remark() default "";
}
 