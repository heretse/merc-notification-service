
package com.bp.line.notification.utils;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpHost;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

/**
 * @ClassName: HttpClientUtil
 * @Description:HTTPCLIENT工具类
 * @author MT
 * @date 2018年2月28日 上午9:53:04
 */
public class HttpClientUtil
{
    public static String doGet(String url, Map<String, String> param)
    {

        // 创建Httpclient对象
        CloseableHttpClient httpclient = getHttpClient();

        String resultString = "";
        CloseableHttpResponse response = null;
        try
        {
            // 创建uri
            URIBuilder builder = new URIBuilder(url);
            if (param != null)
            {
                for (String key : param.keySet())
                {
                    builder.addParameter(key, param.get(key));
                }
            }
            URI uri = builder.build();

            // 创建http GET请求
            HttpGet httpGet = new HttpGet(uri);

            // 执行请求
            response = httpclient.execute(httpGet);
            // 判断返回状态是否为200
            if (response.getStatusLine().getStatusCode() == 200)
            {
                resultString = EntityUtils.toString(response.getEntity(), "UTF-8");
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
            try
            {
                if (response != null)
                {
                    response.close();
                }
                httpclient.close();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
        return resultString;
    }

    public static String doGet(String url)
    {
        return doGet(url, null);
    }

    public static String doPost(String url, Map<String, String> param)
    {
        // 创建Httpclient对象
        CloseableHttpClient httpClient = getHttpClient();
        CloseableHttpResponse response = null;
        String resultString = "";
        try
        {
            // 创建Http Post请求
            HttpPost httpPost = new HttpPost(url);
            // 创建参数列表
            if (param != null)
            {
                List<NameValuePair> paramList = new ArrayList<>();
                for (String key : param.keySet())
                {
                    paramList.add(new BasicNameValuePair(key, param.get(key)));
                }
                // 模拟表单
                UrlEncodedFormEntity entity = new UrlEncodedFormEntity(paramList);
                httpPost.setEntity(entity);
            }
            // 执行http请求
            response = httpClient.execute(httpPost);
            resultString = EntityUtils.toString(response.getEntity(), "utf-8");
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
            try
            {
                response.close();
            }
            catch (IOException e)
            {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        return resultString;
    }

    public static String doPost(String url)
    {
        return doPost(url, null);
    }
    
    public static String doBearerPost(String url, String token, Map<String, String> param)
    {
        // 创建Httpclient对象
        CloseableHttpClient httpClient = getHttpClient();
        CloseableHttpResponse response = null;
        String resultString = "";
        try
        {
            // 创建Http Post请求
            HttpPost httpPost = new HttpPost(url);
            //add header
            httpPost.addHeader("Authorization", "Bearer "+token);
            // 创建参数列表
            if (param != null)
            {
                List<NameValuePair> paramList = new ArrayList<>();
                for (String key : param.keySet())
                {
                    paramList.add(new BasicNameValuePair(key, param.get(key)));
                }
                //將資料參數放到HttpPost的Entity裡面，並指定編碼
                UrlEncodedFormEntity entity = new UrlEncodedFormEntity(paramList, HTTP.UTF_8);
                httpPost.setEntity(entity);
            }
            // 执行http请求
            response = httpClient.execute(httpPost);
            resultString = EntityUtils.toString(response.getEntity(), "utf-8");
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
            try
            {
                response.close();
            }
            catch (IOException e)
            {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        return resultString;
    }

    public static String doPostJson(String url, String json)
    {
        // 创建Httpclient对象
        CloseableHttpClient httpClient = getHttpClient();
        CloseableHttpResponse response = null;
        String resultString = "";
        try
        {
            // 创建Http Post请求
            HttpPost httpPost = new HttpPost(url);
            // 创建请求内容
            StringEntity entity = new StringEntity(json, ContentType.APPLICATION_JSON);
            httpPost.setEntity(entity);
            // 执行http请求
            response = httpClient.execute(httpPost);
            resultString = EntityUtils.toString(response.getEntity(), "utf-8");
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
            try
            {
                response.close();
            }
            catch (IOException e)
            {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        return resultString;
    }
    private static CloseableHttpClient getHttpClient() {
    	CloseableHttpClient httpclient = null;
    	
    	if (Boolean.parseBoolean(Tools.getValue("isProxy"))) {
      	  //设置代理IP、端口
      	  HttpHost proxy = new HttpHost(Tools.getValue("PROXY_HOST"), Integer.parseInt(Tools.getValue("PROXY_PORT")));
      	  //把代理设置到请求配置
        RequestConfig defaultRequestConfig = RequestConfig.custom()
                .setProxy(proxy)
                .build();
        //实例化CloseableHttpClient对象
        httpclient = HttpClients.custom().setDefaultRequestConfig(defaultRequestConfig).build();
      } else {
      	  httpclient = HttpClients.createDefault();	
      }
    	return httpclient;
    }
}
