
package com.bp.wechat.notification.amqp;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.bp.wechat.notification.jdbc.JDBCOperation;
import com.bp.wechat.notification.utils.WeChatPush;

public class BusinessProcess
{
    public static Logger logger = LogManager.getLogger(LogManager.ROOT_LOGGER_NAME);

    /**
     * @Title: processData
     * @Description: TODO(告警json数据解析)
     * @param @param json
     * @param @return 设定文件
     * @return Map<String,Integer> 返回类型
     * @throws
     */
    public static Map<String, Integer> processData(String json)
    {
        int id = 0;
        String description = null;
        String notiGroup = "";
        String allAccount = "";
        String mac = null;
        String deviceName = null;
        String terGroup = null;
        String recv = null;
        long delay = -1;
        Map<String, Object> parameterMap = new HashMap<String, Object>();
        Map<String, Integer> deliveryIdMap = new HashMap<String, Integer>();
        parameterMap = JSON.parseObject(json, Map.class);
        if (parameterMap.isEmpty())
        {
            return deliveryIdMap;
        }
        if (parameterMap.containsKey("delay") && null != parameterMap.get("delay"))
        {
            delay = Long.parseLong(parameterMap.get("delay").toString());
        }
        else
        {
            logger.info("上报告警数据delay参数为空");
            return deliveryIdMap;
        }
        if (parameterMap.containsKey("notiGroup") && null != parameterMap.get("notiGroup"))
        {
            List list = new ArrayList(JSONArray.parseArray(parameterMap.get("notiGroup").toString()));
            int flag = 0;
            int accountFlag = 0;
            for (int i = 0; i < list.size(); i++)
            {

                Map<String, Object> map = (Map<String, Object>)list.get(i);
                for (String groupkey : map.keySet())
                {
                    if (delay != -1)
                    {
                        long DBdelay = JDBCOperation.getDelayByGroupName(groupkey);
                        if (DBdelay != -1)
                        {
                            if (delay == DBdelay)
                            {
                                flag++;
                                if (flag >= 2)
                                {
                                    notiGroup = notiGroup.concat("，");
                                }
                                notiGroup = notiGroup.concat(groupkey);
                                List groupList = new ArrayList(JSONArray.parseArray(map.get(groupkey).toString()));
                                for (int j = 0; j < groupList.size(); j++)
                                {
                                    Map<String, Object> groupMap = (Map<String, Object>)groupList.get(j);
                                    for (String namekey : groupMap.keySet())
                                    {
                                        List objectList = new ArrayList(JSONArray.parseArray(groupMap.get(namekey).toString()));
                                        for (int k = 0; k < objectList.size(); k++)
                                        {
                                            Map<String, Object> objectMap = (Map<String, Object>)objectList.get(k);
                                            if ("WeChat".equals(objectMap.get("method").toString()))
                                            {
                                                accountFlag++;
                                                if (1 == accountFlag)
                                                {

                                                    allAccount = allAccount.concat(objectMap.get("account").toString());
                                                }
                                                else
                                                {
                                                    allAccount = allAccount.concat("|" + objectMap.get("account").toString());
                                                }

                                            }
                                        }
                                    }
                                }
                            }
                            else
                            {
                                continue;
                            }
                        }
                        else
                        {
                            logger.info("根据通报组查询延迟失败");
                            return deliveryIdMap;
                        }
                    }
                    else
                    {
                        logger.info("上报告警数据未获取到delay参数");
                        return deliveryIdMap;
                    }

                }

            }
        }
        else
        {
            logger.info("上报告警数据notiGroup参数为空");
            return deliveryIdMap;
        }
        if (parameterMap.containsKey("description") && null != parameterMap.get("description"))
        {
            description = (String)parameterMap.get("description");
        }
        else
        {
            logger.info("上报告警数据description参数为空");
            return deliveryIdMap;
        }
        if (parameterMap.containsKey("mac") && null != parameterMap.get("mac"))
        {
            mac = (String)parameterMap.get("mac");
            deviceName = JDBCOperation.getRemarkByMac(mac);
        }
        else
        {
            logger.info("上报告警数据mac参数为空");
            return deliveryIdMap;
        }
        if (parameterMap.containsKey("terGroup") && null != parameterMap.get("terGroup"))
        {
            terGroup = (String)parameterMap.get("terGroup");
        }
        else
        {
            logger.info("上报告警数据terGroup参数为空");
            return deliveryIdMap;
        }
        if (parameterMap.containsKey("recv") && null != parameterMap.get("recv"))
        {
            recv = (String)parameterMap.get("recv");
        }
        else
        {
            logger.info("上报告警数据recv参数为空");
            return deliveryIdMap;
        }

        if (!"".equals(allAccount))
        {
            String content = contentConCat(delay, notiGroup, description, mac, terGroup, recv, deviceName);
            id = JDBCOperation.insertNotificationContent(parameterMap);
            String delivery_time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
            boolean flag = WeChatPush.getInstance().sendMsg(allAccount, content);
            if (flag == true)
            {
                logger.info("微信消息推送成功");
            }
            else
            {
                logger.info("微信消息推送失败");
            }
            deliveryIdMap.put(delivery_time, new Integer(id));
            return deliveryIdMap;
        }
        else
        {
            logger.info("此延时没有微信推送的用户");
        }
        return deliveryIdMap;
    }

    /**
     * @Title: contentConCat
     * @Description: TODO(拼接推送微信推送内容)
     * @param @param delay
     * @param @param notiGroup
     * @param @param description
     * @param @param mac
     * @param @param terGroup
     * @param @param recv
     * @param @param deviceName
     * @param @return 设定文件
     * @return String 返回类型
     * @throws
     */
    public static String contentConCat(long delay, String notiGroup, String description, String mac, String terGroup, String recv, String deviceName)
    {
        String content = ""; 
        content = content.concat("  推播裝置：" + deviceName + "\n");
        content = content.concat("  推播时间：" + recv + "\n");
        content = content.concat("  推播訊息：" +  description + "\n");
        /*
        content = content.concat("  报警终端：" + mac + "\n");
        content = content.concat("  报警时间：" + recv + "\n");
        content = content.concat("  报警事件：" +  description + "\n");
        content = content.concat("  所属终端组：" + terGroup + "\n");
        content = content.concat("  所属通报组：" + notiGroup + "\n");
        content = content.concat("  发报延时：" + delay + "分钟\n");
        */
        return content;
    }
}
