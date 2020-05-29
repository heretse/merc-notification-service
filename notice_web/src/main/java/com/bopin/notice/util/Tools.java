/**
 * Project Name:woju
 * File Name:Tools.java
 * Package Name:com.bopin.woju.util
 * Date:2018年1月8日下午4:43:13
 * Copyright (c) 2018, www.bkclouds.com All Rights Reserved.
 */

package com.bopin.notice.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.RandomStringUtils;

/**
 * ClassName:Tools <br/>
 * Function: TODO ADD FUNCTION. <br/>
 * Reason: TODO ADD REASON. <br/>
 * Date: 2018年1月8日 下午4:43:13 <br/>
 * 
 * @author YanHenghui
 * @version
 * @since JDK 1.8
 * @see
 */
public class Tools
{
    /**
     * 获取用户真实IP地址，不使用request.getRemoteAddr();的原因是有可能用户使用了代理软件方式避免真实IP地址, 参考文章：
     * http://developer.51cto.com/art/201111/305181.htm
     * 可是，如果通过了多级反向代理的话，X-Forwarded-For的值并不止一个，而是一串IP值，究竟哪个才是真正的用户端的真实IP呢？
     * 答案是取X-Forwarded-For中第一个非unknown的有效IP字符串。 如：X-Forwarded-For：192.168.1.110,
     * 192.168.1.120, 192.168.1.130, 192.168.1.100 用户真实IP为： 192.168.1.110
     * 
     * @param request
     * @return
     */
    public static String getIpAddress(HttpServletRequest request)
    {
        String ip = request.getHeader("x-forwarded-for");
        if (ip != null && ip.length() != 0 && !"unknown".equalsIgnoreCase(ip))
        {
            if (ip.indexOf(",") != -1)
            {
                ip = ip.split(",")[0];
            }
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip))
        {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip))
        {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip))
        {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip))
        {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip))
        {
            ip = request.getHeader("X-Real-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip))
        {
            ip = request.getRemoteAddr();
        }
        return ip.equals("0:0:0:0:0:0:0:1") ? "127.0.0.1" : ip;
    }
    /**
     * 
     * @Title: createWebSucessJson
     * @Description: 创建成功状态码
     * @param o
     *              数据data
     * @return Map<String,Object>    返回类型
     * @throws
     */
    public static Map<String, Object> sucessJson(Object o)
    {
        HashMap<String, Object> map = new HashMap<>();
        map.put("code", 200);
        map.put("desc", "Request Successful");
        if (null != o)
        {
            map.put("data", o);
        }
        return map;
    }
    
    /**
     * 
     * @Title: createWebSucessJson
     * @Description: 创建成功状态码
     * @param  str 自定义返回信息
     * @return Map<String,Object>    返回类型
     * @throws
     */
    public static Map<String, Object> sucessJson(String str)
    {
        HashMap<String, Object> map = new HashMap<>();
        map.put("code", 200);
        map.put("desc", str);
        return map;
    }
    
    /**
     * 
     * @Title: failureJson
     * @Description: 创建失败状态码
     * @param reason
     *              失败原因
     * @return Map<String,Object>    返回类型
     * @throws
     */
    public static Map<String, Object> failureJson(String reason)
    {
        HashMap<String, Object> map = new HashMap<>();
        map.put("code", "9999");
        map.put("desc", reason);
        return map;
    }

    /**
     * 
     * @Title: failureJson
     * @Description: 创建失败状态码
     * @param code 
     *              状态码
     * @param reason
     *              失败原因
     * @return Map<String,Object>    返回类型
     * @throws
     */
    public static Map<String, Object> failureJson(int code, String reason)
    {
        HashMap<String, Object> map = new HashMap<>();
        map.put("code", code);
        map.put("desc", reason);
        return map;
    }
    /**
     * 
     * @Title: failureJson
     * @Description: 创建失败状态码
     * @return Map<String,String>    返回类型
     * @throws
     */
    public static Map<String, String> failureJson()
    {
        HashMap<String, String> map = new HashMap<>();
        map.put("code", "9999");
        map.put("desc", "Unknown Error!");
        return map;
    }
    
    /**
     * 采用加密算法加密字符串数据
     * 
     * @param str
     *            需要加密的数据
     * @param algorithm
     *            采用的加密算法
     * @return String
     */
    public static String EncryptionStrBytes(String str)
    {
        // 加密之后所得字符串
        StringBuffer sb = new StringBuffer();
        try
        {
            //获取MD5算法实例 得到一个md5的消息摘要
            MessageDigest md = MessageDigest.getInstance("MD5");
            //添加要进行计算摘要的信息
            md.update(str.getBytes());
            //得到该摘要
            byte[] bytes = md.digest();
            for (byte aByte : bytes)
            {
                String s = Integer.toHexString(0xff & aByte);
                if (s.length() == 1)
                {
                    sb.append("0" + s);
                }
                else
                {
                    sb.append(s);
                }
            }
        }
        catch (NoSuchAlgorithmException e)
        {
            e.printStackTrace();
        }
        return sb.toString();
    }
    
    //订单号生成计数器 
    private static long orderNumCount = 0L;
    //每毫秒生成订单号数量最大值 
    private static int maxPerMSECSize = 1000;
    /**
     * 生成非重复订单号，理论上限1毫秒1000个，可扩展
     * @Title: makeOrderNum
     * @Description: TODO(这里用一句话描述这个方法的作用)
     * @param @param tel   手机号码
     * @return void    返回类型
     * @throws
     */
    public static String makeOrderNum(String tel)
    {
        //锁对象，可以为任意对象 
        Object lockObj = "wojuRegistOrder";
        try
        {
            // 最终生成的订单号
            String orderNum = "";
            synchronized (lockObj)
            {
                // 取系统当前时间作为订单号变量前半部分，精确到毫秒
                long nowLong = Long.parseLong(new SimpleDateFormat("yyyyMMddHHmmss").format(System.currentTimeMillis()));
                // 计数器到最大值归零，可扩展更大，目前1毫秒处理峰值1000个，1秒100万
                if (orderNumCount >= maxPerMSECSize)
                {
                    orderNumCount = 0L;
                }
                //组装订单号
                String countStr = maxPerMSECSize + orderNumCount + "";
                orderNum = nowLong + tel + RandomStringUtils.randomNumeric(4) + countStr.substring(1);
                orderNumCount++;
                System.out.println(orderNum + "--" + Thread.currentThread().getName());
                // Thread.sleep(1000);
                return orderNum;
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return null;
        }
    }

    public static void main(String[] args)
    {
        // 测试多线程调用订单号生成工具
        try
        {
            for (int i = 0; i < 200; i++)
            {
                Thread t1 = new Thread(new Runnable()
                {
                    public void run()
                    {
                        Tools.makeOrderNum("18600000001");
                    }
                }, "at" + i);
                t1.start();

                Thread t2 = new Thread(new Runnable()
                {
                    public void run()
                    {
                        Tools.makeOrderNum("15800000001");
                    }
                }, "bt" + i);
                t2.start();
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

    }

}
