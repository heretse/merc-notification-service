
package com.bp.email.notification.amqp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import com.alibaba.fastjson.JSON;
import com.bp.email.notification.jdbc.JDBCOperation;
import com.bp.email.notification.mail.Mail;

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
        String mac = null;
        String deviceName = null;
        String terGroup = null;
        String recv = null;
        long delay = -1;
        Map<String, Object> parameterMap = new HashMap<String, Object>();
        Map<String, Object> extraMap = new HashMap<String, Object>();
        Map<String, Object> deviceMap = new HashMap<String, Object>();
        Map<String, String> user = new HashMap<String, String>();
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
            List list = new ArrayList(JSON.parseArray(parameterMap.get("notiGroup").toString()));
            int flag = 0;
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
                                List groupList = new ArrayList(JSON.parseArray(map.get(groupkey).toString()));
                                for (int j = 0; j < groupList.size(); j++)
                                {
                                    Map<String, Object> groupMap = (Map<String, Object>)groupList.get(j);
                                    for (String namekey : groupMap.keySet())
                                    {
                                        List objectList = new ArrayList(JSON.parseArray(groupMap.get(namekey).toString()));
                                        for (int k = 0; k < objectList.size(); k++)
                                        {
                                            Map<String, Object> objectMap = (Map<String, Object>)objectList.get(k);
                                            if ("eMail".equals(objectMap.get("method").toString()))
                                            {
                                                user.put(objectMap.get("account").toString(), namekey.toString());
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

        if (!user.isEmpty())
        {
            String content = contentConCat(delay, notiGroup, description, mac, terGroup, recv, deviceName);
            boolean templateFlg = false;
            int templateId = -1;
            if (parameterMap.containsKey("extra") && null != parameterMap.get("extra"))
            {
                extraMap = JSON.parseObject(parameterMap.get("extra").toString(), Map.class);
                if(extraMap.containsKey("devices") && null != extraMap.get("devices")) {
                  List list = new ArrayList(JSON.parseArray(extraMap.get("devices").toString()));
                  for(int i = 0 ; i < list.size() ; i++) {
                    Map<String, Object> deviceInfoMap = (Map<String, Object>)list.get(i);
                    deviceMap.put(deviceInfoMap.get("mac").toString(), deviceInfoMap.get("information"));
                  }	
                }
                if(extraMap.containsKey("template") && null != extraMap.get("template")) {
                  templateId = Integer.parseInt(extraMap.get("template").toString());
                }
            }
            id = JDBCOperation.insertNotificationContent(parameterMap);
            List<Map<String, Object>> template = JDBCOperation.getTemplateById(templateId);
            List<Map<String, Object>> attachs = JDBCOperation.getAttachmentById(templateId);
            Mail.getInstance();
            if(template != null && template.size() > 0) {
            	templateFlg = true;
            }
            
            @SuppressWarnings("static-access")
            String delivery_time = Mail.createMimeMessage(user, content, description, templateFlg, template, attachs, deviceMap);
            deliveryIdMap.put(delivery_time, new Integer(id));
            return deliveryIdMap;
        }
        else {
            logger.info("此延时没有邮件推送的用户");
        }
        return deliveryIdMap;
    }
    /**
     * @param extraMap 
     * 
     * @Title: contentConCat
     * @Description: TODO(拼接推送邮件信息)
     * @param @param delay
     * @param @param notiGroup
     * @param @param description
     * @param @param mac
     * @param @param terGroup
     * @param @param recv
     * @param @param deviceName
     * @param @return    设定文件
     * @return String    返回类型
     * @throws
     */
    public static String contentConCat(long delay, String notiGroup, String description, String mac, String terGroup, String recv, String deviceName)
    {
        String content = "你好，<br/><br/>";
        content = content.concat("&nbsp;&nbsp;推播裝置：" + deviceName + "<br/>");
        content = content.concat("&nbsp;&nbsp;推播时间：" + recv + "<br/>");
        content = content.concat("&nbsp;&nbsp;推播訊息如下<br/>");
        content = content.concat("&nbsp;&nbsp;" + description + "<br/>");
        content = content.concat("<br/>B.R.<br/>");
        /*
        content = content.concat("&nbsp;&nbsp;报警终端：" + mac + "<br/>");
        content = content.concat("&nbsp;&nbsp;报警时间：" + recv + "<br/>");
        content = content.concat("&nbsp;&nbsp;报警事件：" + description + "<br/>");
        content = content.concat("&nbsp;&nbsp;所属终端组：" + terGroup + "<br/>");
        content = content.concat("&nbsp;&nbsp;所属通报组：" + notiGroup + "<br/>");
        content = content.concat("&nbsp;&nbsp;发报延时：" + delay + "分钟<br/>");
        content = content.concat("<br/><br/><br/>Gemtek Technology Co., Ltd.<hr>");
        */
        return content;
    }
}
