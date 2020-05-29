/**
 * @FileName: Tools.java
 * @PackageName com.bp.wechat.utils
 * @Description: TODO(用一句话描述该文件做什么)
 * @author MT
 * @date 2018年2月27日 下午5:58:35
 * @version
 */

package com.bp.wechat.notification.utils;

import java.io.IOException;
import java.util.Properties;

/**
 * @ClassName: Tools
 * @Description: 读取配置文件工具类
 * @author MT
 * @date 2018年2月27日 下午5:58:35
 */
public class Tools
{
    private static Properties p = new Properties();

    /**
     * 读取properties配置文件信息
     */
    static
    {
        try
        {
            p.load(Tools.class.getClassLoader().getResourceAsStream("wechat.conf.properties"));
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    /**
     * 根据key得到value的值
     */
    public static String getValue(String key)
    {
        return p.getProperty(key);
    }
}
