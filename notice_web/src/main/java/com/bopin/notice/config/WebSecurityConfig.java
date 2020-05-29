/**
 * Project Name:woju
 * File Name:WebSecurityConfig.java
 * Package Name:com.bopin.woju.config
 * Date:2017年12月26日下午4:27:39
 * Copyright (c) 2017, www.bkclouds.com All Rights Reserved.
 */

package com.bopin.notice.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.bopin.notice.filter.JwtAuthenticationFilter;
import com.bopin.notice.filter.LoginAuthenticationFilter;
import com.bopin.notice.service.impl.AuthUserDetailsServiceImpl;

/**
 * security配置
 * ClassName:WebSecurityConfig <br/>
 * Function: TODO ADD FUNCTION. <br/>
 * Reason: TODO ADD REASON. <br/>
 * Date: 2017年12月26日 下午4:27:39 <br/>
 * 
 * @author YanHenghui
 * @version
 * @since JDK 1.8
 * @see
 */
//@EnableWebSecurity: 禁用Boot的默认Security配置，配合@Configuration启用自定义配置
//@EnableGlobalMethodSecurity(prePostEnabled = true): 启用Security注解，例如最常用的@PreAuthorize
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)//允许进入页面方法前检验
public class WebSecurityConfig extends WebSecurityConfigurerAdapter
{
    
    @Bean
    public AuthUserDetailsServiceImpl authUserDetailsServiceImpl()
    {
        return new AuthUserDetailsServiceImpl();
    }

    /**
     * 
     * @param auth
     * @throws Exception
     */
    @Autowired
    public void globalUserDetails(AuthenticationManagerBuilder auth) throws Exception
    {
        auth.userDetailsService(authUserDetailsServiceImpl());
    }

    /**
     * 定义URL路径应该受到保护，哪些不应该, 设置获取token的url
     * TODO 简单描述该方法的实现功能（可选）.
     * @see org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter#configure(org.springframework.security.config.annotation.web.builders.HttpSecurity)
     */
    @Override
    protected void configure(HttpSecurity http) throws Exception
    {
        http.csrf().disable()       //关闭CSRF,防止重复重定向
//            .anonymous().disable()
            .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)    //由于使用Token，所以不需要Session
            .and()
            .authorizeRequests().antMatchers(HttpMethod.GET, "/*", "/*/page/**", "/vendor/**", "/app/**", "/index**", "/login**").permitAll()      //主页和登录端点被明确排除 HttpMethod.OPTIONS
            .anyRequest().authenticated()
            .and()
            .exceptionHandling();
        //这里必须要写formLogin()，不然原有的UsernamePasswordAuthenticationFilter不会出现，也就无法配置我们重新的UsernamePasswordAuthenticationFilter
        http.formLogin().loginPage("/#/page/login").permitAll()
            .and()
            .logout().permitAll();
        // 禁用缓存
        http.headers().cacheControl();
        //用重写的Filter替换掉原有的UsernamePasswordAuthenticationFilter
        http.addFilterBefore(customAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);
        http.addFilter(new JwtAuthenticationFilter(authenticationManager()));
    }

    /**
     * 注册自定义的UsernamePasswordAuthenticationFilter
     * customAuthenticationFilter:注册自定义的UsernamePasswordAuthenticationFilter. <br/>
     *
     * @author YanHenghui
     * @return
     * @throws Exception
     * @since JDK 1.8
     */
    @Bean
    LoginAuthenticationFilter customAuthenticationFilter() throws Exception {
        LoginAuthenticationFilter filter = new LoginAuthenticationFilter(authenticationManager());
        filter.setFilterProcessesUrl("/login");
        //这句很关键，重用WebSecurityConfigurerAdapter配置的AuthenticationManager，不然要自己组装AuthenticationManager
        filter.setAuthenticationManager(authenticationManagerBean());
        return filter;
    }
    
    @Override
    @Bean
    public AuthenticationManager authenticationManagerBean() throws Exception
    {
        return super.authenticationManagerBean();
    }
}