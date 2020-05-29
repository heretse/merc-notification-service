
package com.bp.email.notification.timer;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.bp.email.notification.jdbc.JDBCOperation;
import com.bp.email.notification.mail.Mail;

public class SendTimer
{
    private Logger logger = LogManager.getLogger(LogManager.ROOT_LOGGER_NAME);
    private static SendTimer singleton = null;
    final Timer timer = new Timer();

    private SendTimer()
    {
    }

    /**
     * @Title: getInstance
     * @Description: TODO(创建定时器单例对象)
     * @param @return 设定文件
     * @return ChannelTimer 返回类型
     * @throws
     */
    public static SendTimer getInstance()
    {
        if (singleton == null)
        {
            synchronized (SendTimer.class)
            {
                if (singleton == null)
                {    //这里进行第二次判断，不能省略，否则会线程不安全。  
                    singleton = new SendTimer();
                }
            }
        }
        return singleton;
    }

    /**
     * @Title: startTimer
     * @Description: TODO(启动定时器)
     * @param 设定文件
     * @return void 返回类型
     * @throws
     */
    public void startTimer()
    {
        timer.schedule(new TimerTask()
        {
            @Override
            public void run()
            {
                timerProcess();
            }
        }, 5* 60 * 1000, 10 * 60 * 1000);
    }

    /**
     * @Title: closeTimer
     * @Description: TODO(关闭定时器)
     * @param 设定文件
     * @return void 返回类型
     * @throws
     */
    public void closeTimer()
    {
        timer.cancel();
    }

    /**
     * @Title: process
     * @Description: TODO(查询是否有未发送的邮件，然后发送)
     * @param 设定文件
     * @return void 返回类型
     * @throws
     */
    public void timerProcess()
    {
        List<Map<String, Object>> list = JDBCOperation.getContentNoSuccess();
        if (list.size() > 0)
        {
            for (int i = 0; i < list.size(); i++)
            {
                Map<String, String> user = new HashMap<String, String>();
                String description = null;
                String notiGroup = "";
                String terminal_group = null;
                String recv = null;
                String mac = null;
                long delay = -1;
                Map<String, Object> parameterMap = list.get(i);
                Map<String, Object> extraMap = new HashMap<String, Object>();
                Map<String, Object> deviceMap = new HashMap<String, Object>();
                if (parameterMap.containsKey("delay") && null != parameterMap.get("delay"))
                {
                    delay = Long.parseLong(parameterMap.get("delay").toString());
                }
                else
                {
                    logger.info("未推送数据delay字段为空");
                    return;
                }
                if (parameterMap.containsKey("notification_group") && null != parameterMap.get("notification_group"))
                {
                    List lists = new ArrayList(JSON.parseArray(parameterMap.get("notification_group").toString()));
                    int flag = 0;
                    for (int n = 0; n < lists.size(); n++)
                    {
                        Map<String, Object> map = (Map<String, Object>)lists.get(n);
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
                                    return;
                                }
                            }
                            else
                            {
                                logger.info("上报告警数据未获取到delay参数");
                                return;
                            }

                        }

                    }
                }
                else
                {
                    logger.info("未推送数据notification_group字段为空");
                    return;
                }
                if (parameterMap.containsKey("description") && null != parameterMap.get("description"))
                {
                    description = (String)parameterMap.get("description");
                }
                else
                {
                    logger.info("未推送数据description字段为空");
                    return;
                }
                if (parameterMap.containsKey("terminal_group") && null != parameterMap.get("terminal_group"))
                {
                    terminal_group = (String)parameterMap.get("terminal_group");
                }
                else
                {
                    logger.info("未推送数据terminal_group字段为空");
                    return;
                }
                if (parameterMap.containsKey("mac") && null != parameterMap.get("mac"))
                {
                    mac = (String)parameterMap.get("mac");
                }
                else
                {
                    logger.info("未推送数据mac字段为空");
                    return;
                }
                if (parameterMap.containsKey("recv") && null != parameterMap.get("recv"))
                {
                    recv = (String)parameterMap.get("recv");
                }
                else
                {
                    logger.info("未推送数据recv字段为空");
                    return;
                }
                int id = (int)parameterMap.get("id");

                if (!user.isEmpty())
                {
                    String tempRecv = recv.substring(0, 19);
                    String content = contentConCat(delay, notiGroup, description, mac, terminal_group, tempRecv);
                    boolean templateFlg = false;
                    List<Map<String, Object>> template = JDBCOperation.getTemplateById(1);
                    List<Map<String, Object>> attachs = JDBCOperation.getAttachmentById(1);
                    Mail.getInstance();
                    if (parameterMap.containsKey("extra") && null != parameterMap.get("extra"))
                    {
                        extraMap = JSON.parseObject(parameterMap.get("extra").toString(), Map.class);
                        List deviceList = new ArrayList(JSON.parseArray(extraMap.get("devices").toString()));
                        for(int j = 0 ; j < list.size() ; j++) {
                        	Map<String, Object> deviceInfoMap = (Map<String, Object>)deviceList.get(j);
                            deviceMap.put(deviceInfoMap.get("mac").toString(), deviceInfoMap.get("information"));
                        }
                    }
					String delivery_time = Mail.createMimeMessage(user, content, description, templateFlg, template, attachs, extraMap);
                    String up_time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
                    JDBCOperation.updateContent(id, 1, delivery_time, up_time);
                }
            }
        }
    }
    /**
     * 
     * @Title: contentConCat
     * @Description: TODO(拼接推送邮件信息)
     * @param @param delay
     * @param @param notiGroup
     * @param @param description
     * @param @param mac
     * @param @param terGroup
     * @param @param recv
     * @param @return    设定文件
     * @return String    返回类型
     * @throws
     */
    public String contentConCat(long delay, String notiGroup, String description, String mac, String terGroup, String recv)
    {
        String content = "你好，<br/><br/>";
        content = content.concat("&nbsp;&nbsp;报警终端：" + mac + "<br/>");
        content = content.concat("&nbsp;&nbsp;报警时间：" + recv + "<br/>");
        content = content.concat("&nbsp;&nbsp;报警事件：" + description + "<br/>");
        content = content.concat("&nbsp;&nbsp;所属终端组：" + terGroup + "<br/>");
        content = content.concat("&nbsp;&nbsp;所属通报组：" + notiGroup + "<br/>");
        content = content.concat("&nbsp;&nbsp;发报延时：" + delay + "分钟<br/>");
        content = content.concat("<br/><br/><br/>Gemtek Technology Co., Ltd.<hr>");
        return content;
    }
}
