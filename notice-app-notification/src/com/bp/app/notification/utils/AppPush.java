/**
 * @FileName: AppPush.java
 * @PackageName com.bp.app.notification.utils
 * @Description: TODO(用一句话描述该文件做什么)
 * @author MT
 * @date 2018年2月27日 下午5:50:35
 * @version
 */

package com.bp.app.notification.utils;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpEntity;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import org.apache.http.util.EntityUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

/**
 * @ClassName: AppPush
 * @Description: App 发送工具类
 * @author MT
 * @date 2018年2月27日 下午5:50:35
 */
public class AppPush
{

    private static AppPush instance;

    public static String SERVER_KEY;//firebase server key

    public static boolean isProxy;//是否透過Proxy Server
    
    public static String proxyHost;//Proxy Host
    
    public static int proxyPort;//Proxy Port

    public static String TOKEN;

    public static String sendMsgURL;//发送App推播调用URL

    public static String getTokenURL;//获取token调用URL
    
    public static String checkTokenURL;//check token调用URL

    private AppPush()
    {

    }

    public static synchronized AppPush getInstance()
    {
        if (instance == null)
        {
            return new AppPush();
        }

        return instance;
    }
    
    /**
     * @Title: sendMsg
     * @Description: 发送方法
     * @param @param account user token list
     * @param @param msg 发送的消息内容
     * @return void 返回类型
     * @throws
     */
    public Boolean sendMsg(List<String>  account, String msg)
    {

        Boolean finalresult = false;
        String url = sendMsgURL;
        JSONObject jsonObject = new JSONObject();

        Map<String, String> param = new HashMap<String, String>();
        param.put("message", msg);
        if(account.size() == 1) {
        	  finalresult = HttpClientUtil.send_FCM_Notification(url, SERVER_KEY, isProxy, proxyHost, proxyPort, account.get(0), msg );	
        }else {
        	  finalresult = HttpClientUtil.send_FCM_Notification(url, SERVER_KEY, isProxy, proxyHost, proxyPort, account, msg );
        }
        
        return finalresult;
    }

}
