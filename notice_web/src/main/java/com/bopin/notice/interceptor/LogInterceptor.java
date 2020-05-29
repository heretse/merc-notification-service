/**
 * Project Name:woju
 * File Name:LogInterceptor.java
 * Package Name:com.bopin.woju.interceptor
 * Date:2018年1月29日 上午9:45:33
 * Copyright (c) 2017, www.bkclouds.com All Rights Reserved.
 */
package com.bopin.notice.interceptor;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;

import javax.servlet.http.HttpServletRequest;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.bopin.notice.common.Constant;
import com.bopin.notice.util.JwtUtils;
import com.bopin.notice.util.Tools;

import java.lang.reflect.Method;
/**
 * 
 * @ClassName: LogInterceptor
 * @Description: TODO(这里用一句话描述这个类的作用)
 * @author ZhangXiurui
 * @date 2018年1月29日 上午9:45:33
 * 
 * @version
 * @since JDK 1.8
 * @see
 */
@Component
@Aspect
public class LogInterceptor
{
    public LogInterceptor()
    {
    }

    /**
     * 切点
     */
    @Pointcut("execution(* com.bopin.woju.controller..*(..)) and @annotation(org.springframework.web.bind.annotation.RequestMapping)")
    public void methodCachePointcut()
    {
    }

    @After("methodCachePointcut()")
    public void after(JoinPoint point) throws Throwable
    {
        HttpServletRequest request = ((ServletRequestAttributes)RequestContextHolder.getRequestAttributes()).getRequest();
        String modular = null;
        String type = null;
        String account = null;
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Timestamp operTime = Timestamp.valueOf(format.format(System.currentTimeMillis()));
        String jwtToken = JwtUtils.resolveToken(request.getHeader(Constant.APP_HTTP_HEADER_AUTHORIZATION));
        if (null != jwtToken)
        {
            Authentication auth = JwtUtils.getAuthentication(jwtToken);
            account = auth.getName();
        }
        String urlString = request.getRequestURI().toString();
        String typeString = urlString.substring(urlString.lastIndexOf("/") + 1, urlString.length());
        String ip = Tools.getIpAddress(request);;
        String className = point.getTarget().getClass().getSimpleName();        
        String methodRemark = getMthodRemark(point); //获取注解

        if (typeString.endsWith("list"))
        {
            type = "一般";
        }
        else
        {
            type = "警告";
        }

        if (className.endsWith("LogController"))
        {
            modular = "系统日志";
        }
        if (className.endsWith("UserController"))
        {
            modular = "用户管理";
        }
        if (className.endsWith("RegisterController")||className.endsWith("ExcelController"))
        {
            modular = "开卡用户";
        }
        if (className.endsWith("CustomerController"))
        {
            modular = "客户管理";
        }

        /*Log log = new Log();
        log.setAccount(account);
        log.setIp(ip);
        log.setOperDetails(methodRemark);
        log.setOperTime(operTime);
        log.setOperType(type);
        log.setOperModule(modular);
        logServiceImpl.saveLog(log);*/
    }

    @SuppressWarnings("rawtypes")
    public static String getMthodRemark(JoinPoint joinPoint) throws Exception
    {
        String targetName = joinPoint.getTarget().getClass().getName();
        String methodName = joinPoint.getSignature().getName();
        Object[] arguments = joinPoint.getArgs();
        Class<?> targetClass = Class.forName(targetName);
        Method[] method = targetClass.getMethods();
        String methode = "";
        for (Method m : method)
        {
            if (m.getName().equals(methodName))
            {
                Class[] tmpCs = m.getParameterTypes();
                if (tmpCs.length == arguments.length)
                {
                    LogDetailAnnotation methodCache = m.getAnnotation(LogDetailAnnotation.class);
                    if (methodCache != null)
                    {
                        methode = methodCache.remark();
                    }
                    break;
                }
            }
        }
        return methode;
    }
}
