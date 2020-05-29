/**
 * Project Name:woju
 * File Name:AuthUserServiceImpl.java
 * Package Name:com.bopin.woju.service
 * Date:2017年12月26日下午4:38:35
 * Copyright (c) 2017, www.bkclouds.com All Rights Reserved.
 */

package com.bopin.notice.service.impl;

import java.util.Collection;
import java.util.HashSet;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.bopin.notice.common.Constant;
import com.bopin.notice.model.SubUserDetails;
import com.bopin.notice.util.Http;

/**
 * ClassName:AuthUserServiceImpl <br/>
 * Function: TODO ADD FUNCTION. <br/>
 * Reason: TODO ADD REASON. <br/>
 * Date: 2017年12月26日 下午4:38:35 <br/>
 * 
 * @author YanHenghui
 * @version
 * @since JDK 1.8
 * @see
 */
@Service
public class AuthUserDetailsServiceImpl implements UserDetailsService
{
    
    @Override
    public UserDetails loadUserByUsername(String account) throws UsernameNotFoundException
    {
        String url = Constant.APP_NOTICE_REQUEST_BASE_URL + "account/queryByName";
        String param = "{\"filter\":{\"userName\":\"" + account +"\"}}";
        JSONObject res = JSON.parseObject(Http.post(url, param));
        if (null==res || 1000 != (Integer)res.get("code"))
        {
            throw new UsernameNotFoundException("The user(" + account +") does not exist.");
        }
        Collection<GrantedAuthority> collection = new HashSet<GrantedAuthority>();
        collection.add(new SimpleGrantedAuthority("ROLE_admin"));
        //解析data
        JSONArray arr = res.getJSONArray("data");
        JSONObject user = (JSONObject)arr.get(0);
        SubUserDetails userDetails = new SubUserDetails();
        userDetails.setUsername(account);
        userDetails.setPassword(user.getString("pwd"));
        userDetails.setUserId(user.getString("id"));
        userDetails.setAuthorities(collection);
        return userDetails;
    }
}
