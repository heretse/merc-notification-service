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
import java.util.List;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * ClassName:Http <br/>
 * Function: TODO ADD FUNCTION. <br/>
 * Reason: TODO ADD REASON. <br/>
 * Date: 2017年3月7日 上午10:16:59 <br/>
 * 
 * @author YanHenghui
 * @version
 * @since JDK 1.6
 * @see
 */
public class Http
{
    protected static final Logger LOG = LoggerFactory.getLogger(Http.class);

    private static final String UTF_8 = "UTF-8";

    private static RequestConfig requestConfig = RequestConfig.custom()
            .setSocketTimeout(15000)
            .setConnectTimeout(15000)
            .setConnectionRequestTimeout(15000)
            .build();

    /**
     * post:发送 post请求. <br/>
     *
     * @author YanHenghui
     * @param httpUrl
     *            Url 地址
     * @return
     * @since JDK 1.6
     */
    public static String post(String httpUrl)
    {
        HttpPost httpPost = new HttpPost(httpUrl);
        httpPost.setConfig(requestConfig);
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
    public static String post(String httpUrl, Map<String, String> headers)
    {
        HttpPost httpPost = new HttpPost(httpUrl);
        httpPost.setConfig(requestConfig);
        for (String key : headers.keySet())
        {
            httpPost.setHeader(key, headers.get(key));
        }
        // 创建httpPost
        return doExecute(httpPost);
    }

    /**
     * post:发送 post请求. <br/>
     *
     * @author YanHenghui
     * @param httpUrl
     *            Url 地址
     * @param params
     *            参数 json 字符串
     * @return
     * @since JDK 1.6
     */
    public static String post(String httpUrl, String jsonParams)
    {
        // 创建httpPost
        HttpPost httpPost = new HttpPost(httpUrl);
        httpPost.setConfig(requestConfig);
        try
        {
            // 设置参数
            StringEntity stringEntity = new StringEntity(jsonParams, UTF_8);
            stringEntity.setContentType("application/json");
            httpPost.setEntity(stringEntity);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return doExecute(httpPost);
    }

    /**
     * post:发送 post请求. <br/>
     *
     * @author YanHenghui
     * @param httpUrl
     *            Url 地址
     * @param params
     *            参数 json 字符串
     * @return
     * @since JDK 1.6
     */
    public static String post(String httpUrl, String jsonParams, Map<String, String> headers)
    {
        // 创建httpPost
        HttpPost httpPost = new HttpPost(httpUrl);
        httpPost.setConfig(requestConfig);
        try
        {
            for (String key : headers.keySet())
            {
                httpPost.setHeader(key, headers.get(key));
            }
            // 设置参数
            StringEntity stringEntity = new StringEntity(jsonParams, UTF_8);
            stringEntity.setContentType("application/json");
            httpPost.setEntity(stringEntity);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return doExecute(httpPost);
    }

    /**
     * post:发送 post请求. <br/>
     *
     * @author YanHenghui
     * @param httpUrl
     * @param maps
     * @return
     * @since JDK 1.6
     */
    public static String post(String httpUrl, Map<String, String> maps, Map<String, String> headers)
    {
        // 创建httpPost
        HttpPost httpPost = new HttpPost(httpUrl);
        httpPost.setConfig(requestConfig);
        try
        {
            for (String key : headers.keySet())
            {
                httpPost.setHeader(key, headers.get(key));
            }
            // 创建参数队列
            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
            for (String key : maps.keySet())
            {
                nameValuePairs.add(new BasicNameValuePair(key, maps.get(key)));
            }
            httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs, UTF_8));
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return doExecute(httpPost);
    }

    /**
     * get:(发送 get请求). <br/>
     *
     * @author YanHenghui
     * @param httpUrl
     * @return
     * @since JDK 1.6
     */
    public static String get(String httpUrl)
    {
        // 创建get请求
        HttpGet httpGet = new HttpGet(httpUrl);
        httpGet.setConfig(requestConfig);
        return doExecute(httpGet);
    }

    /**
     * get:(发送 get请求). <br/>
     *
     * @author YanHenghui
     * @param httpUrl
     * @return
     * @since JDK 1.6
     */
    public static String get(String httpUrl, Map<String, String> headers)
    {
        // 创建get请求
        HttpGet httpGet = new HttpGet(httpUrl);
        httpGet.setConfig(requestConfig);
        for (String key : headers.keySet())
        {
            httpGet.setHeader(key, headers.get(key));
        }
        return doExecute(httpGet);
    }

    /**
     * delete:(发送 delete请求). <br/>
     *
     * @author YanHenghui
     * @param httpUrl
     * @return
     * @since JDK 1.6
     */
    public static String delete(String httpUrl)
    {
        HttpDelete httpDelete = new HttpDelete();
        httpDelete.setConfig(requestConfig);
        return doExecute(httpDelete);
    }

    /**
     * delete:(发送 delete请求). <br/>
     *
     * @author YanHenghui
     * @param httpUrl
     * @return
     * @since JDK 1.6
     */
    public static String delete(String httpUrl, Map<String, String> headers)
    {
        HttpDelete httpDelete = new HttpDelete();
        httpDelete.setConfig(requestConfig);
        for (String key : headers.keySet())
        {
            httpDelete.setHeader(key, headers.get(key));
        }
        return doExecute(httpDelete);
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
        CloseableHttpClient httpClient = null;
        CloseableHttpResponse response = null;
        HttpEntity entity = null;
        String responseContent = null;
        try
        {
            // 创建默认的httpClient实例.
            httpClient = HttpClients.createDefault();
            // 执行请求
            response = httpClient.execute(request);
            entity = response.getEntity();
            if (entity != null)
            {
                responseContent = EntityUtils.toString(entity, UTF_8);
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
            LOG.error("httpClient execute error: ", e);
        }
        finally
        {
            try
            {
                // 关闭连接,释放资源
                if (response != null)
                {
                    response.close();
                }
                if (httpClient != null)
                {
                    httpClient.close();
                }
            }
            catch (IOException e)
            {
                LOG.error("httpClient close error: ", e);
            }
        }
        return responseContent;
    }
}
