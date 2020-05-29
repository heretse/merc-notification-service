/**
 * @FileName: WeChatStart.java
 * @PackageName bin
 * @Description: TODO(用一句话描述该文件做什么)
 * @author MT
 * @date 2018年2月28日 上午11:22:18
 * @version
 */

package com.bp.wechat.notification.utils;

/**
 * @ClassName: WeChatStart
 * @Description:微信接口调用初始化类(1：启动获得一次Token,2:2小时去刷新一次Token)
 * @author MT
 * @date 2018年2月28日 上午11:22:18
 */
public class WeChatStarter
{

    private static WeChatPush weChatPush;
    static
    {

        weChatPush = WeChatPush.getInstance();

    }

    /**
     * @Title: initWechat
     * @Description: 应用初始化调用
     * @param 设定文件
     * @return void 返回类型
     * @throws
     */
    public static void initWechat()
    {

        WeChatPush.CorpID = Tools.getValue("CorpID");
        WeChatPush.Secret = Tools.getValue("Secret");
        WeChatPush.agentid = Tools.getValue("agentid");
        WeChatPush.sendMsgURL = Tools.getValue("sendMsgURL");
        WeChatPush.getTokenURL = Tools.getValue("getTokenURL");
        WeChatPush.isProxy = Boolean.parseBoolean(Tools.getValue("isProxy"));
        WeChatPush.proxyHost = Tools.getValue("PROXY_HOST");
        WeChatPush.proxyPort = Integer.parseInt(Tools.getValue("PROXY_PORT"));

        WeChatPush.TOKEN = weChatPush.getAccessToken();//应用初始化第一次先调用Token

    }

}
