
package com.bopin.notice.common;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * API常量定义
 * spring boot不允许/不支持把值注入到静态变量中,可以利用非静态set方法注入静态变量
 * @author YanHenghui
 */
@Component
public class Constant
{
    public static String APP_HTTP_HEADER_AUTHORIZATION;
    
    public static String APP_HTTP_HEADER_AUTHORIZATION_BEARER;
    
    public static String APP_SECURITY_LOGIN_USERNAME_KEY;
    
    public static String APP_SECURITY_LOGIN_PASSWORD_KEY;
    
    public static String APP_SECURITY_JWT_AUTHORITIES_KEY;

    public static String APP_SECURITY_JWT_SECRET;                        //签名密钥
    
    public static long APP_SECURITY_JWT_TOKEN_VALIDITY;                  //失效日期

    public static long APP_SECURITY_JWT_TOKEN_VALIDITY_REMEMBERME;       //（记住我）失效日期
    
    public static String APP_NOTICE_REQUEST_BASE_URL;
    

    @Value("${app.http.header.authorization:Authorization}")
    public void setAPP_HTTP_HEADER_AUTHORIZATION(String aPP_HTTP_HEADER_AUTHORIZATION)
    {
        Constant.APP_HTTP_HEADER_AUTHORIZATION = aPP_HTTP_HEADER_AUTHORIZATION;
    }

    @Value("${app.http.header.authorization.bearer:Bearer }")
    public void setAPP_HTTP_HEADER_AUTHORIZATION_BEARER(String aPP_HTTP_HEADER_AUTHORIZATION_BEARER)
    {
        Constant.APP_HTTP_HEADER_AUTHORIZATION_BEARER = aPP_HTTP_HEADER_AUTHORIZATION_BEARER;
    }
    
    @Value("${app.security.login.username.key}")
    public void setAPP_SECURITY_LOGIN_USERNAME_KEY(String aPP_SECURITY_LOGIN_USERNAME_KEY)
    {
        APP_SECURITY_LOGIN_USERNAME_KEY = aPP_SECURITY_LOGIN_USERNAME_KEY;
    }
    
    @Value("${app.security.login.password.key}")
    public void setAPP_SECURITY_LOGIN_PASSWORD_KEY(String aPP_SECURITY_LOGIN_PASSWORD_KEY)
    {
        Constant.APP_SECURITY_LOGIN_PASSWORD_KEY = aPP_SECURITY_LOGIN_PASSWORD_KEY;
    }

    @Value("${app.security.jwt.authorities.key}")
    public void setAPP_SECURITY_JWT_AUTHORITIES_KEY(String aPP_SECURITY_JWT_AUTHORITIES_KEY)
    {
        APP_SECURITY_JWT_AUTHORITIES_KEY = aPP_SECURITY_JWT_AUTHORITIES_KEY;
    }

    @Value("${app.security.jwt.secret}")
    public void setAPP_SECURITY_JWT_SECRET(String aPP_SECURITY_JWT_SECRET)
    {
        Constant.APP_SECURITY_JWT_SECRET = aPP_SECURITY_JWT_SECRET;
    }
    
    @Value("${app.security.jwt.token.validity}")
    public void setAPP_SECURITY_JWT_TOKEN_VALIDITY(long aPP_SECURITY_JWT_TOKEN_VALIDITY)
    {
        APP_SECURITY_JWT_TOKEN_VALIDITY = aPP_SECURITY_JWT_TOKEN_VALIDITY;
    }

    @Value("${app.security.jwt.token.validity.rememberme}")
    public void setAPP_SECURITY_JWT_TOKEN_VALIDITY_REMEMBERME(long aPP_SECURITY_JWT_TOKEN_VALIDITY_REMEMBERME)
    {
        APP_SECURITY_JWT_TOKEN_VALIDITY_REMEMBERME = aPP_SECURITY_JWT_TOKEN_VALIDITY_REMEMBERME;
    }

    @Value("${app.notice.request.base.url}")
    public void setAPP_NOTICE_REQUEST_BASE_URL(String aPP_NOTICE_REQUEST_BASE_URL)
    {
        APP_NOTICE_REQUEST_BASE_URL = aPP_NOTICE_REQUEST_BASE_URL;
    }
}