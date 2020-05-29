/**
 * Project Name:woju
 * File Name:CustomAuthenticationFilter.java
 * Package Name:com.bopin.woju.filter
 * Date:2018年1月8日上午9:35:25
 * Copyright (c) 2018, www.bkclouds.com All Rights Reserved.
 */

package com.bopin.notice.filter;

import java.io.BufferedReader;
import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.bopin.notice.common.Constant;
import com.bopin.notice.model.SubUserDetails;
import com.bopin.notice.util.Http;
import com.bopin.notice.util.JwtUtils;
import com.bopin.notice.util.Tools;

/**
 * ClassName:CustomAuthenticationFilter <br/>
 * Function: AuthenticationFilter that supports rest login(json login) and form
 * login. <br/>
 * Reason: TODO ADD REASON. <br/>
 * Date: 2018年1月8日 上午9:35:25 <br/>
 * 
 * @author YanHenghui
 * @version
 * @since JDK 1.8
 * @see
 */
public class LoginAuthenticationFilter extends UsernamePasswordAuthenticationFilter
{
    private AuthenticationManager authenticationManager;

    /**
     * Creates a new instance of CustomLoginAuthenticationFilter.
     *
     * @param authenticationManager
     */
    public LoginAuthenticationFilter(AuthenticationManager authenticationManager)
    {
        this.authenticationManager = authenticationManager;
    }

    @SuppressWarnings("finally")
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException
    {
        if (!request.getMethod().equals("POST"))
        {
            throw new AuthenticationServiceException("Authentication method not supported: " + request.getMethod());
        }
        UsernamePasswordAuthenticationToken authRequest = null;
        //attempt Authentication when Content-Type is json
        if (request.getContentType().replace(" ", "").toLowerCase().equals(MediaType.APPLICATION_JSON_UTF8_VALUE.toLowerCase())
                || request.getContentType().replace(" ", "").toLowerCase().equals(MediaType.APPLICATION_JSON_VALUE.toLowerCase()))
        {
            try
            {
                StringBuffer sb = new StringBuffer();
                String line = null;
                BufferedReader reader = request.getReader();
                while ((line = reader.readLine()) != null)
                {
                    sb.append(line);
                }
                JSONObject json = JSON.parseObject(sb.toString());
                //验证用户是否被启用
                authRequest = new UsernamePasswordAuthenticationToken(json.get(Constant.APP_SECURITY_LOGIN_USERNAME_KEY),
                        json.get(Constant.APP_SECURITY_LOGIN_PASSWORD_KEY));
            }
            catch (IOException e)
            {
                e.printStackTrace();
                authRequest = new UsernamePasswordAuthenticationToken("", "");
            }
            finally
            {
                //验证用户是否被启用
                setDetails(request, authRequest);
                //运行UserDetailsService的loadUserByUsername 再次封装Authentication
                return this.authenticationManager.authenticate(authRequest);
            }
        }
        else if (request.getContentType().toLowerCase().indexOf(MediaType.APPLICATION_FORM_URLENCODED_VALUE.toLowerCase()) > -1)
        {
            String username = request.getParameter(Constant.APP_SECURITY_LOGIN_USERNAME_KEY);
            String password = request.getParameter(Constant.APP_SECURITY_LOGIN_PASSWORD_KEY);
            try
            {
                authRequest = new UsernamePasswordAuthenticationToken(username, password);
            }
            catch (AuthenticationException e)
            {
                e.printStackTrace();
                authRequest = new UsernamePasswordAuthenticationToken("", "");
            }
            finally
            {
                setDetails(request, authRequest);
                return this.authenticationManager.authenticate(authRequest);
            }
        }
        else
        {
            return super.attemptAuthentication(request, response);
        }
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication auth)
            throws IOException, ServletException
    {
        //通过 AuthenticationManager（默认实现为ProviderManager）的authenticate方法验证 Authentication 对象
        //Authentication authentication = authenticationManager.authenticate(auth);
        //将 Authentication 绑定到 SecurityContext
        SecurityContextHolder.getContext().setAuthentication(auth);
        //生成Token
        String token = JwtUtils.createToken(auth, false);
        
        //将Token写入到Http头部
        response.addHeader(Constant.APP_HTTP_HEADER_AUTHORIZATION, Constant.APP_HTTP_HEADER_AUTHORIZATION_BEARER + token);
        String account = ((SubUserDetails)auth.getPrincipal()).getUsername();
        String userId = ((SubUserDetails)auth.getPrincipal()).getUserId();
        
        //token写入后台
        String url = Constant.APP_NOTICE_REQUEST_BASE_URL + "account/loginSuccess";
        String param = "{\"filter\":{\"userName\":\"" + account +"\",\"token\":\"" + Tools.EncryptionStrBytes(token) +"\"},\"userID\":\"" + userId + "\"}";
        JSONObject res = JSON.parseObject(Http.post(url, param));
        Integer resCode = res.getInteger("code");
        if(1000 == resCode)
        {
            JSONObject json = new JSONObject();
            json.put("uname", account);
            json.put("id", userId);
            JSONObject jsonObj = new JSONObject();
            jsonObj.put("message", "SUCCESS");
            jsonObj.put("code", HttpServletResponse.SC_OK);
            jsonObj.put(Constant.APP_HTTP_HEADER_AUTHORIZATION, Constant.APP_HTTP_HEADER_AUTHORIZATION_BEARER + token);
            jsonObj.put("data", json);
            response.setStatus(HttpServletResponse.SC_OK);
            response.setCharacterEncoding("UTF-8");
            response.setContentType("application/json; charset=utf-8");
            response.getWriter().write(jsonObj.toJSONString());
            response.getWriter().flush();
            response.getWriter().close();
        }
        else
        {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setCharacterEncoding("UTF-8");
            response.setContentType("application/json; charset=utf-8");
            JSONObject jsonObj = new JSONObject();
            jsonObj.put("message", res.getString("desc"));
            jsonObj.put("code", HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write(jsonObj.toJSONString());
            response.getWriter().flush();
            response.getWriter().close();
        }
    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception)
            throws IOException, ServletException
    {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json; charset=utf-8");
        JSONObject jsonObj = new JSONObject();
        jsonObj.put("message", "exception.getMessage()");
        jsonObj.put("code", HttpServletResponse.SC_UNAUTHORIZED);
        response.getWriter().write(jsonObj.toJSONString());
        response.getWriter().flush();
        response.getWriter().close();
    }
}
