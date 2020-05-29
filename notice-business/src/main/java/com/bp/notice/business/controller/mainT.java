/**   
* @FileName: mainT.java
* @PackageName com.bp.notice.business.controller
* @Description: TODO(用一句话描述该文件做什么)
* @author MT   
* @date 2018年2月2日 下午6:22:55
* @version    
*/
package com.bp.notice.business.controller;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.bp.notice.business.utils.DateUtils;
import com.bp.notice.business.utils.QueryParam;
import com.sun.org.apache.bcel.internal.generic.NEW;

/**
 * @ClassName: mainT
 * @Description: TODO(这里用一句话描述这个类的作用)
 * @author MT
 * @date 2018年2月2日 下午6:22:55
 */
public class mainT
{
   
    /**
     * @Title: main
     * @Description: TODO(这里用一句话描述这个方法的作用)
     * @param @param args    设定文件
     * @return void    返回类型
     * @throws
     */
    public static void main(String[] args)
    {
        System.out.println("测试");
        
       //List<Map<String,Object>> aList= getAList();
        
       
        /*String strULR ="http://localhost:8080/notice-business/account/add";
        
        
        QueryParam param = new QueryParam();
        
        Map<String,Object> maps = new HashMap<>();
        maps.put("uname","马腾3");
        maps.put("pwd","123");
        param.setFilter(maps);
        
        param.setUserId(10);
        
        String string = JSON.toJSONString(param);
        
        System.out.println(jsonPost(strULR, string));*/
        
        
        List<Map<String, Object>> aList = new ArrayList<>();
        
        Map<String, Object> amap1 = new HashMap<>();
        amap1.put("id",1);
        
        aList.add(amap1);
        Map<String, Object> amap2 = new HashMap<>();
        amap2.put("id",2);
        
        aList.add(amap2);
        
        
     List<Map<String, Object>> bList = new ArrayList<>();
        
        Map<String, Object> bmap1 = new HashMap<>();
        bmap1.put("id",1);
        
        bList.add(bmap1);
        Map<String, Object> bmap2 = new HashMap<>();
        bmap2.put("id",3);
        
        bList.add(bmap2);
        
        System.out.println(aList);
        System.out.println(aList.removeAll(bList));
        System.out.println(aList);
    }
    public static String jsonPost(String strURL,String param) {  
        try {  
            URL url = new URL(strURL);// 创建连接  
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();  
            connection.setDoOutput(true);  
            connection.setDoInput(true);  
            connection.setUseCaches(false);  
            connection.setInstanceFollowRedirects(true);  
            connection.setRequestMethod("POST"); // 设置请求方式  
            connection.setRequestProperty("Accept", "application/json"); // 设置接收数据的格式  
            connection.setRequestProperty("Content-Type", "application/json"); // 设置发送数据的格式  
            connection.connect();  
            OutputStreamWriter out = new OutputStreamWriter(connection.getOutputStream(), "UTF-8"); // utf-8编码  
            out.append(param);  
            out.flush();  
            out.close();  
  
            int code = connection.getResponseCode();  
            InputStream is = null;  
            if (code == 200) {  
                is = connection.getInputStream();  
            } else {  
                is = connection.getErrorStream();  
            }  
  
            // 读取响应  
            int length = (int) connection.getContentLength();// 获取长度  
            if (length != -1) {  
                byte[] data = new byte[length];  
                byte[] temp = new byte[512];  
                int readLen = 0;  
                int destPos = 0;  
                while ((readLen = is.read(temp)) > 0) {  
                    System.arraycopy(temp, 0, data, destPos, readLen);  
                    destPos += readLen;  
                }  
                String result = new String(data, "UTF-8"); // utf-8编码  
                return result;  
            }  
  
        } catch (IOException e) {  
            e.printStackTrace();
        }  
        return "error"; // 自定义错误信息  
    }  
    
    
}
