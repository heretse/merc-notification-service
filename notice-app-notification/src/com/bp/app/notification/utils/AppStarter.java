/**
 * @FileName: AppStart.java
 * @PackageName com.bp.app.notification.utils
 * @Description: TODO(用一句话描述该文件做什么)
 * @author MT
 * @date 2018年2月28日 上午11:22:18
 * @version
 */

package com.bp.app.notification.utils;

/**
 * @ClassName: AppStart
 * @Description:App接口调用初始化类
 * @author MT
 * @date 2018年2月28日 上午11:22:18
 */
public class AppStarter
{

    private static AppPush appPush;
    static
    {

        appPush = AppPush.getInstance();

    }

    /**
     * @Title: initApp
     * @Description: 应用初始化调用
     * @param 设定文件
     * @return void 返回类型
     * @throws
     */
    public static void initApp()
    {

        AppPush.SERVER_KEY = Tools.getValue("server_key");
        AppPush.sendMsgURL = Tools.getValue("sendMsgURL");
        AppPush.getTokenURL = Tools.getValue("getTokenURL");
        AppPush.checkTokenURL = Tools.getValue("checkTokenURL");
        AppPush.isProxy = Boolean.parseBoolean(Tools.getValue("isProxy"));
        AppPush.proxyHost = Tools.getValue("PROXY_HOST");
        AppPush.proxyPort = Integer.parseInt(Tools.getValue("PROXY_PORT"));

    }

}
