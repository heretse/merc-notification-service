
package com.bp.app.notification.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.Proxy;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.http.NameValuePair;
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
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;


/**
 * @ClassName: HttpClientUtil
 * @Description:HTTPCLIENT工具类
 * @author MT
 * @date 2018年2月28日 上午9:53:04
 */
public class HttpClientUtil
{
	private static Logger logger = LogManager.getLogger(LogManager.ROOT_LOGGER_NAME);
    public static String doGet(String url, Map<String, String> param)
    {

        // 创建Httpclient对象
        CloseableHttpClient httpclient = HttpClients.createDefault();

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
        CloseableHttpClient httpClient = HttpClients.createDefault();
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
        CloseableHttpClient httpClient = HttpClients.createDefault();
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
    
    public static boolean send_FCM_Notification(String FCM_URL, String server_key, boolean isProxy, String host, int port, String token, String message)
    {
    	boolean flg = false;
    	try{
    		// Create URL instance.
    		URL pushUrl = new URL(FCM_URL);
    		// create connection.
    		HttpURLConnection conn;
    		if (isProxy) {
    		  Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(host, port));
        	  conn = (HttpURLConnection) pushUrl.openConnection(proxy);	
    		} else {
    		  conn = (HttpURLConnection) pushUrl.openConnection();		
    		}
    		conn.setUseCaches(false);
    		conn.setDoInput(true);
    		conn.setDoOutput(true);
    		//set method as POST or GET
    		conn.setRequestMethod("POST");
    		//pass FCM server key
    		conn.setRequestProperty("Authorization","key="+server_key);
    		//Specify Message Format
    		conn.setRequestProperty("Content-Type","application/json");
    		//Create JSON Object & pass value
    		JSONObject infoJson = new JSONObject();

    		infoJson.put("title","MERC Push Service");
    		infoJson.put("body", message);

    		JSONObject json = new JSONObject();
    		json.put("to",token.trim());
    		json.put("notification", infoJson);
    		logger.info("FCM json :" +json.toString());
    		logger.info("FCM infoJson :" +infoJson.toString());
    		OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
    		wr.write(json.toString());
    		wr.flush();
    		int status = 0;
    		if( null != conn ){
    		status = conn.getResponseCode();
    		}
    		if( status != 0){

    		if( status == 200 ){
    		//SUCCESS message
    		BufferedReader reader = new BufferedReader(new 
    		InputStreamReader(conn.getInputStream()));
    		logger.info("Notification Response : " + reader.readLine());
    		flg = true;
    		}else if(status == 401){
    		//client side error
    		logger.info("Notification Response : TokenId : " + token + " Error occurred :");
    		}else if(status == 501){
    		//server side error
    		logger.info("Notification Response : [ errorCode=ServerError ] TokenId : " + token);
    		}else if( status == 503){
    		//server side error
    		logger.info("Notification Response : FCM Service is Unavailable  TokenId : " + token);
    		}
    		}
    		}catch(MalformedURLException mlfexception){
    		// Prototcal Error
    		logger.info("Error occurred while sending push Notification!.." + mlfexception.getMessage());
    		}catch(Exception mlfexception){
    		//URL problem
    		logger.info("Reading URL, Error occurred while sending push Notification!.." + mlfexception.getMessage());
    		}
    	return flg;
    }
    
    public static boolean send_FCM_Notification(String FCM_URL, String server_key, boolean isProxy, String host, int port, List<String> token, String message)
    {
    	boolean flg = false;
        try{
            // Create URL instance.
    		URL pushUrl = new URL(FCM_URL);
    		// create connection.
    		HttpURLConnection conn;
    		if (isProxy) {
    		  Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(host, port));
        	  conn = (HttpURLConnection) pushUrl.openConnection(proxy);	
    		} else {
    		  conn = (HttpURLConnection) pushUrl.openConnection();		
    		}
            conn.setUseCaches(false);
            conn.setDoInput(true);
            conn.setDoOutput(true);
            //set method as POST or GET
            conn.setRequestMethod("POST");
            //pass FCM server key
            conn.setRequestProperty("Authorization","key="+server_key);
            //Specify Message Format
            conn.setRequestProperty("Content-Type","application/json");
            //Create JSON Object & pass value

                JSONArray regId = null;
                JSONObject objData = null;
                JSONObject data = null;
                JSONObject notif = null;

                    regId = new JSONArray();
                    regId.addAll(token);
                    data = new JSONObject();
                    data.put("message", message);
                    notif = new JSONObject();
                    notif.put("title", "MERC Push Service");
                    notif.put("body", message);

                    objData = new JSONObject();
                    objData.put("registration_ids", regId);
                    objData.put("data", data);
                    objData.put("notification", notif);
                    logger.info("passing msg by group:>"+ objData.toString());


            OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());

            wr.write(objData.toString());
            wr.flush();
            int status = 0;
            if( null != conn ){
            status = conn.getResponseCode();
            }
            if( status != 0){
                if( status == 200 ){
            //SUCCESS message
            BufferedReader reader = new BufferedReader(new 
            InputStreamReader(conn.getInputStream()));
            logger.info("Android Notification Response : " + 
            reader.readLine());
            flg = true;
            }else if(status == 401){
            //client side error
            	logger.info("Notification Response : TokenId : " + objData.toString() + " Error occurred :");
            }else if(status == 501){
            //server side error
            	logger.info("Notification Response : [ errorCode=ServerError ] TokenId : " + objData.toString());
            }else if( status == 503){
            //server side error
            	logger.info("Notification Response : FCM Service is Unavailable  TokenId : " + objData.toString());
            }
            }
            }catch(MalformedURLException mlfexception){
            // Prototcal Error
            	logger.info("Error occurred while sending push Notification!.." + mlfexception.getMessage());
            }catch(IOException mlfexception){
            //URL problem
            	logger.info("Reading URL, Error occurred while sending push Notification!.." + mlfexception.getMessage());
            }catch (Exception exception) {
            //General Error or exception.
            	logger.info("Error occurred while sending push Notification!.." + exception.getMessage());
            }
    	return flg;
    }

    public static String doPostJson(String url, String json)
    {
        // 创建Httpclient对象
        CloseableHttpClient httpClient = HttpClients.createDefault();
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
}
