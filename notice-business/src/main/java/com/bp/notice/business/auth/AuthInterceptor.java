/**
 * @FileName: LoginInterceptor.java
 * @PackageName com.force4us.web
 * @Description: TODO(用一句话描述该文件做什么)
 * @author MT
 * @date 2018年1月18日 下午6:11:03
 * @version
 */

package com.bp.notice.business.auth;

import java.io.IOException;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import com.bp.notice.business.service.UserService;
import com.bp.notice.business.utils.SpringContextUtil;

/**
 * @ClassName: LoginInterceptor
 * @Description: 接口api调用权限认证
 * @author MT
 * @date 2018年1月18日 下午6:11:03
 */
public class AuthInterceptor implements HandlerInterceptor
{
    /**
     * Handler执行完成之后调用这个方法
     */
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception exc) throws Exception
    {

    }

    /**
     * Handler执行之后，ModelAndView返回之前调用这个方法
     */
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception
    {
    }

    /**
     * Handler执行之前调用这个方法
     */
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception
    {
        //获取请求的URL
        List<String> noAuthURLS = new ArrayList<>();
        noAuthURLS.add("/notice-business/account/queryByName");//不进行权限校验的地址
        noAuthURLS.add("/notice-business/account/loginSuccess");//不进行权限校验的地址
        
        
        String url = request.getRequestURI();

        if (noAuthURLS.contains(url))//根据用户名查询相关信息的不做任何权限校验
        {
            return true;
        }

        UserService userService = SpringContextUtil.getBean(UserService.class);
        String token = request.getHeader("Authorization");
        if (request.getHeader("Authorization") == null || !userService.isAuth(token))//调用接口权限认证
        {
            response.setCharacterEncoding("UTF-8");
            response.setContentType("application/json; charset=utf-8");

            String jsonStr = "{\"code\":100001,\"desc\":\"没有访问权限\"}";
            PrintWriter out = null;
            try
            {
                out = response.getWriter();
                out.write(jsonStr);
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
            finally
            {
                if (out != null)
                {
                    out.close();
                }
            }
            return false;
        }

        return true;

    }
}
