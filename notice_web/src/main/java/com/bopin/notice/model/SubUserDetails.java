/**
 * Project Name:woju
 * File Name:CustomUserDetails.java
 * Package Name:com.bopin.woju.model
 * Date:2018年1月8日上午11:42:56
 * Copyright (c) 2018, www.bkclouds.com All Rights Reserved.
 */

package com.bopin.notice.model;

import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * ClassName:CustomUserDetails <br/>
 * Function: TODO ADD FUNCTION. <br/>
 * Reason: TODO ADD REASON. <br/>
 * Date: 2018年1月8日 上午11:42:56 <br/>
 * 
 * @author YanHenghui
 * @version
 * @since JDK 1.8
 * @see
 */
public class SubUserDetails implements UserDetails
{

    private static final long serialVersionUID = 1L;

    private String username;
    private String password;
    private String userId;
    private Collection<GrantedAuthority> authorities;//用户证书是否有效

    @Override
    public String getUsername()
    {
        return username;
    }

    public void setUsername(String username)
    {
        this.username = username;
    }

    @Override
    public String getPassword()
    {
        return password;
    }

    public void setPassword(String password)
    {
        this.password = password;
    }

    public String getUserId()
    {
        return userId;
    }

    public void setUserId(String userId)
    {
        this.userId = userId;
    }

    @Override
    public Collection<GrantedAuthority> getAuthorities()
    {
        return authorities;
    }

    public void setAuthorities(Collection<GrantedAuthority> authorities)
    {
        this.authorities = authorities;
    }

    @Override
    public boolean isAccountNonExpired()
    {
        //过去 用户只有在不过期、没被锁定、没被禁用的情况下才能登录成功
        return true;
    }

    @Override
    public boolean isAccountNonLocked()
    {
        //锁定 用户只有在不过期、没被锁定、没被禁用的情况下才能登录成功
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired()
    {
        //证书 用户只有在不过期、没被锁定、没被禁用的情况下才能登录成功
        return true;
    }

    @Override
    public boolean isEnabled()
    {
        //禁用 用户只有在不过期、没被锁定、没被禁用的情况下才能登录成功
        return true;
    }

}
