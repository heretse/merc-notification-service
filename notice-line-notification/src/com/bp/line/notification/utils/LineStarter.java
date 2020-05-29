/**
 * @FileName: LineStart.java
 * @PackageName com.bp.line.notification.utils
 * @Description: TODO(用一句话描述该文件做什么)
 * @author MT
 * @date 2018年2月28日 上午11:22:18
 * @version
 */

package com.bp.line.notification.utils;

/**
 * @ClassName: LineStart
 * @Description:LINE接口调用初始化类(1：启动获得一次Token,2:2小时去刷新一次Token)
 * @author MT
 * @date 2018年2月28日 上午11:22:18
 */
public class LineStarter
{

    private static LinePush linePush;
    static
    {

        linePush = LinePush.getInstance();

    }

    /**
     * @Title: initWechat
     * @Description: 应用初始化调用
     * @param 设定文件
     * @return void 返回类型
     * @throws
     */
    public static void initLine()
    {

        LinePush.ID = Tools.getValue("client_id");
        LinePush.Secret = Tools.getValue("client_secret");
        LinePush.sendMsgURL = Tools.getValue("sendMsgURL");
        LinePush.getTokenURL = Tools.getValue("getTokenURL");
        LinePush.checkTokenURL = Tools.getValue("checkTokenURL");
        LinePush.isProxy = Boolean.parseBoolean(Tools.getValue("isProxy"));
        LinePush.proxyHost = Tools.getValue("PROXY_HOST");
        LinePush.proxyPort = Integer.parseInt(Tools.getValue("PROXY_PORT"));

    }

}
