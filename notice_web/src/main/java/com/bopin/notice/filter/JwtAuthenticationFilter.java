/**
 * Project Name:woju
 * File Name:JwtAuthenticationFilter.java
 * Package Name:com.bopin.woju.filter
 * Date:2018年1月9日上午9:52:29
 * Copyright (c) 2018, www.bkclouds.com All Rights Reserved.
 */

package com.bopin.notice.filter;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.util.StringUtils;

import com.alibaba.fastjson.JSON;
import com.bopin.notice.common.Constant;
import com.bopin.notice.util.JwtUtils;
import com.bopin.notice.util.Tools;

import io.jsonwebtoken.ExpiredJwtException;

/**
 * ClassName:JwtAuthenticationFilter <br/>
 * Function: TODO ADD FUNCTION. <br/>
 * Reason: TODO ADD REASON. <br/>
 * Date: 2018年1月9日 上午9:52:29 <br/>
 * 
 * @author YanHenghui
 * @version
 * @since JDK 1.8
 * @see
 */
public class JwtAuthenticationFilter extends BasicAuthenticationFilter
{
    private final Logger log = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    public JwtAuthenticationFilter(AuthenticationManager authenticationManager)
    {
        super(authenticationManager);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws IOException, ServletException
    {
        //String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+request.getContextPath()+"/"; 
        String[] path = request.getServletPath().split("/");
        if (path.length == 0 || (path.length > 0 && !"api".equals(path[1])))
        {
            filterChain.doFilter(request, response);
            return;
        }
        try
        {
            response.setCharacterEncoding("UTF-8");
            response.setContentType("application/json; charset=utf-8");
            String header = request.getHeader(Constant.APP_HTTP_HEADER_AUTHORIZATION);
            if (header == null || !header.startsWith(Constant.APP_HTTP_HEADER_AUTHORIZATION_BEARER))
            {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write(JSON.toJSONString(Tools.failureJson(HttpServletResponse.SC_UNAUTHORIZED, "authentication failure")));
                response.getWriter().flush();
                response.getWriter().close();
                //SecurityContextHolder.clearContext();
                filterChain.doFilter(request, response);
                return;
            }
            String jwt = JwtUtils.resolveToken(header);
            //验证JWT是否正确
            if (null != jwt && StringUtils.hasText(jwt) && JwtUtils.validateToken(jwt))
            {
                Authentication authentication = JwtUtils.getAuthentication(jwt);                //获取用户认证信息
                SecurityContextHolder.getContext().setAuthentication(authentication);           //将用户保存到SecurityContext
                filterChain.doFilter(request, response);
            }
            else
            {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write(JSON.toJSONString(Tools.failureJson(HttpServletResponse.SC_UNAUTHORIZED, "authentication failure")));
                response.getWriter().flush();
                response.getWriter().close();
                return;
            }
        }
        catch (ExpiredJwtException e)
        {   //JWT失效
            log.info("Security exception for user {} - {}", e.getClaims().getSubject(), e.getMessage());
            log.trace("Security exception trace: {}", e);
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write(JSON.toJSONString(Tools.failureJson()));
            response.getWriter().flush();
            response.getWriter().close();
        }
    }
}
