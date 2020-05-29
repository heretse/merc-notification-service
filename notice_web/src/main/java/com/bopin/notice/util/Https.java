/**
 * Project Name:bopin-message
 * File Name:Http.java
 * Package Name:com.bopin.msg.http
 * Date:2017年3月7日上午10:16:59
 * Copyright (c) 2017, www.bkclouds.com All Rights Reserved.
 */

package com.bopin.notice.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/*
 * 利用HttpClient进行post请求的工具类
 */
public class Https
{
    protected static final Logger LOG = LoggerFactory.getLogger(Https.class);
    private static final String UTF_8 = "UTF-8";

    /**
     * post:发送 post请求. <br/>
     *
     * @author YanHenghui
     * @param httpUrl
     *            Url 地址
     * @return
     * @since JDK 1.6
     */
    public static String post(String url)
    {
        HttpPost httpPost = new HttpPost(url);
        // 创建httpPost
        return doExecute(httpPost);
    }

    /**
     * post:发送 post请求. <br/>
     *
     * @author YanHenghui
     * @param httpUrl
     *            Url 地址
     * @return
     * @since JDK 1.6
     */
    public static String post(String url, Map<String, String> headers)
    {
        HttpPost httpPost = new HttpPost(url);
        for (String key : headers.keySet())
        {
            httpPost.setHeader(key, headers.get(key));
        }
        // 创建httpPost
        return doExecute(httpPost);
    }

    /**
     * POST 请求方法application/json Content-Type：application/json
     * 
     * @param url
     * @param jsonString
     * @return
     */
    public static String post(String url, String jsonString)
    {
        HttpPost httpPost = null;
        String result = null;
        try
        {
            httpPost = new HttpPost(url);
            StringEntity params = new StringEntity(jsonString, UTF_8);
            params.setContentType("application/json");
            params.setContentEncoding(UTF_8);
            httpPost.setEntity(params);
            result = doExecute(httpPost);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
        return result;
    }

    /**
     * POST 请求方法application/json Content-Type：application/json
     * 
     * @param url
     * @param jsonString
     * @return
     */
    public static String post(String url, String jsonString, Map<String, String> headers)
    {
        HttpPost httpPost = null;
        String result = null;
        try
        {
            httpPost = new HttpPost(url);
            StringEntity params = new StringEntity(jsonString, UTF_8);
            params.setContentType("application/json");
            params.setContentEncoding(UTF_8);
            for (String key : headers.keySet())
            {
                httpPost.setHeader(key, headers.get(key));
            }
            httpPost.setEntity(params);
            result = doExecute(httpPost);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
        return result;
    }

    /**
     * POST 请求方法 Content-Type：application/x-www-form-urlencoded
     * 
     * @param url
     * @param map
     * @return
     */
    public static String post(String url, Map<String, String> map, Map<String, String> headers)
    {
        HttpPost httpPost = null;
        String result = null;
        try
        {
            httpPost = new HttpPost(url);
            for (String key : headers.keySet())
            {
                httpPost.setHeader(key, headers.get(key));
            }
            // 设置参数
            List<NameValuePair> list = new ArrayList<NameValuePair>();
            Iterator<?> iterator = map.entrySet().iterator();
            while (iterator.hasNext())
            {
                @SuppressWarnings("unchecked")
                Entry<String, String> elem = (Entry<String, String>)iterator.next();
                list.add(new BasicNameValuePair(elem.getKey(), elem.getValue()));
            }
            if (list.size() > 0)
            {
                UrlEncodedFormEntity entity = new UrlEncodedFormEntity(list, UTF_8);
                httpPost.setEntity(entity);
            }
            result = doExecute(httpPost);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
        return result;
    }

    /**
     * GET 请求方法
     * 
     * @param url
     * @param map
     * @return
     */
    public static String get(String url)
    {
        HttpGet httpGet = null;
        String result = null;
        try
        {
            httpGet = new HttpGet(url);
            result = doExecute(httpGet);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
        return result;
    }

    /**
     * GET 请求方法
     * 
     * @param url
     * @param map
     * @return
     */
    public static String get(String url, Map<String, String> headers)
    {
        HttpGet httpGet = null;
        String result = null;
        try
        {
            httpGet = new HttpGet(url);
            for (String key : headers.keySet())
            {
                httpGet.setHeader(key, headers.get(key));
            }
            result = doExecute(httpGet);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
        return result;
    }

    /**
     * PUT 请求方法
     * 
     * @param url
     * @param map
     * @return
     */
    public static String put(String url, String jsonString)
    {
        HttpPut httpPut = null;
        String result = null;
        try
        {
            httpPut = new HttpPut(url);
            httpPut.setHeader("Content-type", "application/json");
            StringEntity params = new StringEntity(jsonString, UTF_8);
            httpPut.setEntity(params);
            result = doExecute(httpPut);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
        return result;
    }

    /**
     * PUT 请求方法
     * 
     * @param url
     * @param map
     * @return
     */
    public static String put(String url, String jsonString, Map<String, String> headers)
    {
        HttpPut httpPut = null;
        String result = null;
        try
        {
            httpPut = new HttpPut(url);
            for (String key : headers.keySet())
            {
                httpPut.setHeader(key, headers.get(key));
            }
            httpPut.setHeader("Content-type", "application/json");
            StringEntity params = new StringEntity(jsonString, UTF_8);
            httpPut.setEntity(params);
            result = doExecute(httpPut);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
        return result;
    }

    /**
     * Delete 请求方法
     * 
     * @param url
     * @return
     */
    public static String delete(String url)
    {
        HttpDelete httpDelete = null;
        String result = null;
        try
        {
            httpDelete = new HttpDelete(url);
            result = doExecute(httpDelete);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
        return result;
    }

    /**
     * Delete 请求方法
     * 
     * @param url
     * @return
     */
    public static String delete(String url, Map<String, String> headers)
    {
        HttpDelete httpDelete = null;
        String result = null;
        try
        {
            httpDelete = new HttpDelete(url);
            for (String key : headers.keySet())
            {
                httpDelete.setHeader(key, headers.get(key));
            }
            result = doExecute(httpDelete);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
        return result;
    }

    /**
     * doExecute:(执行URI请求). <br/>
     *
     * @author YanHenghui
     * @param HttpUriRequest
     * @return
     * @since JDK 1.6
     */
    private static String doExecute(HttpUriRequest request)
    {
        String result = null;
        CloseableHttpClient httpClients = null;
        try
        {
            httpClients = Clients.getClientsByNotSSL();
            HttpResponse response = httpClients.execute(request);
            if (response != null)
            {
                HttpEntity resEntity = response.getEntity();
                if (resEntity != null)
                {
                    result = EntityUtils.toString(resEntity, UTF_8);
                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
            LOG.error("Https execute error: ", e);
        }
        finally
        {
            try
            {
                // 关闭连接,释放资源
                if (httpClients != null)
                {
                    httpClients.close();
                }
            }
            catch (IOException e)
            {
                LOG.error("httpClients close error: ", e);
            }
        }
        return result;
    }

}
