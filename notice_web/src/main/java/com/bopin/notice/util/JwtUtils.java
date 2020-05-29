/**
 * Project Name:woju
 * File Name:JwtUtils.java
 * Package Name:com.bopin.woju.util
 * Date:2018年1月9日上午10:01:13
 * Copyright (c) 2018, www.bkclouds.com All Rights Reserved.
 */

package com.bopin.notice.util;

import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.util.StringUtils;

import com.bopin.notice.common.Constant;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;

/**
 * ClassName:JwtUtils <br/>
 * Function: TODO ADD FUNCTION. <br/>
 * Reason: TODO ADD REASON. <br/>
 * Date: 2018年1月9日 上午10:01:13 <br/>
 * 
 * @author YanHenghui
 * @version
 * @since JDK 1.8
 * @see
 */
public class JwtUtils
{

    private final static Logger log = LoggerFactory.getLogger(JwtUtils.class);

    /**
     * remove 'Bearer ' string
     *
     * @param authorizationHeader
     * @return
     */
    public static String getRawToken(String authorizationHeader)
    {
        return authorizationHeader.substring(Constant.APP_HTTP_HEADER_AUTHORIZATION_BEARER.length());
    }

    public static String getTokenHeader(String rawToken)
    {
        return Constant.APP_HTTP_HEADER_AUTHORIZATION_BEARER + rawToken;
    }

    public static boolean validate(String authorizationHeader)
    {
        return StringUtils.hasText(authorizationHeader) && authorizationHeader.startsWith(Constant.APP_HTTP_HEADER_AUTHORIZATION_BEARER);
    }

    public static String getAuthorizationHeaderPrefix()
    {
        return Constant.APP_HTTP_HEADER_AUTHORIZATION_BEARER;
    }

    /**
     * 创建Token
     * createToken:(创建Token). <br/>
     *
     * @author YanHenghui
     * @param authentication
     * @param rememberMe
     * @return
     * @since JDK 1.8
     */
    public static String createToken(Authentication authentication, Boolean rememberMe)
    {
        long now = (new Date()).getTime();                      //获取当前时间戳
        Date validity;                                          //存放过期时间
        if (rememberMe)
        {
            validity = new Date(now + Constant.APP_SECURITY_JWT_TOKEN_VALIDITY_REMEMBERME);
        }
        else
        {
            validity = new Date(now + Constant.APP_SECURITY_JWT_TOKEN_VALIDITY);
        }
        Claims claims = Jwts.claims()
//              .setId(String.valueOf(IdentityGenerator.generate()))
              .setSubject(authentication.getName())
              .setExpiration(validity)
              .setIssuedAt(new Date());
        String authorities = authentication.getAuthorities()
                .stream()       //获取用户的权限字符串，如 USER,ADMIN
                .map(GrantedAuthority::getAuthority)
                .map(String::toUpperCase)
                .collect(Collectors.joining(","));
        claims.put(Constant.APP_SECURITY_JWT_AUTHORITIES_KEY, authorities);

        return Jwts.builder().setClaims(claims).signWith(SignatureAlgorithm.HS512, Constant.APP_SECURITY_JWT_SECRET).compact();
        /*return Jwts.builder()                                   //创建Token令牌
                .setSubject(authentication.getName())           //设置面向用户
                .claim(Constant.APP_SECURITY_JWT_AUTHORITIES_KEY, authorities)                  //添加权限属性
                .setExpiration(validity)                        //设置失效时间
                .signWith(SignatureAlgorithm.HS512, Constant.APP_SECURITY_JWT_SECRET)  //生成签名
                .compact();*/
    }

    /**
     * 获取用户权限
     * getAuthentication:(获取用户权限). <br/>
     *
     * @author YanHenghui
     * @param jwtToken
     * @return
     * @since JDK 1.8
     */
    public static Authentication getAuthentication(String jwtToken)
    {
        try
        {
            Claims claims = Jwts.parser()                           //解析Token的payload
                    .setSigningKey(Constant.APP_SECURITY_JWT_SECRET)
                    .parseClaimsJws(jwtToken)
                    .getBody();

            Collection<? extends GrantedAuthority> authorities = Arrays.stream(claims.get(Constant.APP_SECURITY_JWT_AUTHORITIES_KEY).toString().split(","))         //获取用户权限字符串
                    .map(SimpleGrantedAuthority::new)
                    .collect(Collectors.toList());                                                  //将元素转换为GrantedAuthority接口集合

            User principal = new User(claims.getSubject(), "", authorities);
            return new UsernamePasswordAuthenticationToken(principal, "", authorities);
        }
        catch (ExpiredJwtException | SignatureException e)
        {
            throw new BadCredentialsException(e.getMessage(), e);
        }
    }
    

    /**
     * 验证Token是否正确
     * validateToken:(验证Token是否正确). <br/>
     *
     * @author YanHenghui
     * @param token
     * @return
     * @since JDK 1.8
     */
    public static boolean validateToken(String token)
    {
        try
        {
            Jwts.parser().setSigningKey(Constant.APP_SECURITY_JWT_SECRET).parseClaimsJws(token);   //通过密钥验证Token
            return true;
        }
        catch (SignatureException e)
        {                                     
            //签名异常
            log.info("Invalid JWT signature.");
            log.trace("Invalid JWT signature trace: {}", e);
        }
        catch (MalformedJwtException e)
        {                                 
            //JWT格式错误
            log.info("Invalid JWT token.");
            log.trace("Invalid JWT token trace: {}", e);
        }
        catch (ExpiredJwtException e)
        {                                   
            //JWT过期
            log.info("Expired JWT token.");
            log.trace("Expired JWT token trace: {}", e);
        }
        catch (UnsupportedJwtException e)
        {                               
            //不支持该JWT
            log.info("Unsupported JWT token.");
            log.trace("Unsupported JWT token trace: {}", e);
        }
        catch (IllegalArgumentException e)
        {                              
            //参数错误异常
            log.info("JWT token compact of handler are invalid.");
            log.trace("JWT token compact of handler are invalid trace: {}", e);
        }
        return false;
    }
    
    
    /**
     * 截取bearerToken
     * resolveToken:(这里用一句话描述这个方法的作用). <br/>
     *
     * @author YanHenghui
     * @param bearerToken
     * @return
     * @since JDK 1.8
     */
    public static String resolveToken(String bearerToken)
    {
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(Constant.APP_HTTP_HEADER_AUTHORIZATION_BEARER))
        {
            //返回Token字符串，去除Bearer
            return bearerToken.substring(7, bearerToken.length()); 
        }
        return null;
    }
}