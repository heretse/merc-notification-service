/**
 * @FileName: LinePush.java
 * @PackageName com.bp.line.notification.utils
 * @Description: TODO(用一句话描述该文件做什么)
 * @author MT
 * @date 2018年2月27日 下午5:50:35
 * @version
 */

package com.bp.line.notification.utils;

import java.io.IOException;
import java.util.HashMap;
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
 * @ClassName: LinePush
 * @Description: WeChat 发送工具类
 * @author MT
 * @date 2018年2月27日 下午5:50:35
 */
public class LinePush
{

    private static LinePush instance;

    public static String Secret;//应用秘钥

    public static String ID;//企业代号

    public static boolean isProxy;//是否透過Proxy Server
    
    public static String proxyHost;//Proxy Host
    
    public static int proxyPort;//Proxy Port

    public static String TOKEN;

    public static String sendMsgURL;//发送微信消息调用URL

    public static String getTokenURL;//获取token调用URL
    
    public static String checkTokenURL;//check token调用URL

    private LinePush()
    {

    }

    public static synchronized LinePush getInstance()
    {
        if (instance == null)
        {
            return new LinePush();
        }

        return instance;
    }

    /**
     * @Title: sendMsg
     * @Description: 发送方法
     * @param @param account 微信和平台绑定的账号
     * @param @param msg 发送的消息内容
     * @return void 返回类型
     * @throws
     */
    public Boolean sendMsg(String account, String msg)
    {

        Boolean finalresult = false;
        String url = sendMsgURL;
        JSONObject jsonObject = new JSONObject();

        Map<String, String> param = new HashMap<String, String>();
        param.put("message", msg);
		String doPostJson = HttpClientUtil.doBearerPost(url, account, param );

        JSONObject result = JSON.parseObject(doPostJson);

        int errcode = result.getIntValue("status");

        if (errcode != 200)
        {
            System.out.println("LINE Notify接口调用错误" + doPostJson);
            return finalresult;
        }
        finalresult = true;
        return finalresult;
    }

    /**
     * @Title: getAccessToken
     * @Description: 根据Secret和CorpID获得Token（2小时过期）这个地方每次发送的时候都去调用，以此来获得最新的Token
     * @param @return 设定文件
     * @return String 返回类型
     * @throws
     */
    public String getAccessToken()
    {

        String token = null;
        CloseableHttpClient httpclient = null;
        // 创建参数队列
        CloseableHttpResponse response = null;
        try
        {
            //参数转换为字符串

            String urlparam = getTokenURL + "?corpid=" + ID + "&corpsecret=" + Secret + "";
            
            // 创建httpget.
            HttpGet httpget = new HttpGet(urlparam);
            httpclient = HttpClients.createDefault();
            // 执行get请求.
            response = httpclient.execute(httpget);

            // 获取响应实体
            HttpEntity entity = response.getEntity();
            // 打印响应状态
            if (response.getStatusLine().getStatusCode() == 200)
            {

                if (entity != null)
                {

                    String result = EntityUtils.toString(entity);

                    JSONObject jsonObject = JSON.parseObject(result);

                    Integer errcode = jsonObject.getInteger("errcode");

                    if (errcode != 0)
                    {
                        System.out.println("调用获得Token接口错误,errcode：" + errcode);
                        return null;
                    }

                    return jsonObject.getString("access_token");
                }

            }

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
            // 关闭连接,释放资源

            if (response != null)
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

            try
            {

                httpclient.close();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }

        return token;
    }

}
