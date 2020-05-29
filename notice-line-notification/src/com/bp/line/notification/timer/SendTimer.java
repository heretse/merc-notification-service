
package com.bp.line.notification.timer;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.alibaba.fastjson.JSONArray;
import com.bp.line.notification.jdbc.JDBCOperation;
import com.bp.line.notification.utils.LinePush;

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
        }, 5 * 60 * 1000, 10 * 60 * 1000);
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
     * @Description: TODO(查询是否有未发送的微信，然后发送)
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
                String description = null;
                String notiGroup = "";
                List<String> tokenList = new ArrayList();
                String terminal_group = null;
                String allAccount = "";
                String recv = null;
                String mac = null;
                long delay = -1;
                Map<String, Object> parameterMap = list.get(i);
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
                    List lists = new ArrayList(JSONArray.parseArray(parameterMap.get("notification_group").toString()));
                    int flag = 0;
                    int accountFlag = 0;
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
                                                    if ("LINE".equals(objectMap.get("method").toString()))
                                                    {
                                                    	tokenList.add(objectMap.get("account").toString());
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
                
                if (!tokenList.isEmpty()) {
                	String tempRecv = recv.substring(0, 19);
        			String content = contentConCat(delay, notiGroup, description, mac, terminal_group, tempRecv);
        			String delivery_time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        			boolean updateFlg = true;
        			for (String token : tokenList) {
        				boolean flag = LinePush.getInstance().sendMsg(token, content);
        				if (flag == true) {
        					logger.info("LINE Notify推送成功 token:" + token);
        					} else {
        					logger.info("LINE Notify推送失败 token:" + token);
        					updateFlg = false;
        					}
        				}
        			if(updateFlg) {
        				String up_time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        				JDBCOperation.updateContent(id, 1, delivery_time, up_time);
        			}
        		}
            }
        }
    }

    /**
     * @Title: contentConCat
     * @Description: TODO(拼接推送微信信息)
     * @param @param delay
     * @param @param notiGroup
     * @param @param description
     * @param @param mac
     * @param @param terGroup
     * @param @param recv
     * @param @return 设定文件
     * @return String 返回类型
     * @throws
     */
    public String contentConCat(long delay, String notiGroup, String description, String mac, String terGroup, String recv)
    {
    		String content = "\n推播终端：" + mac + "\n";
		content = content.concat("  推播时间：" + recv + "\n");
		content = content.concat("  推播事件：" + description + "\n");
		content = content.concat("  所属终端组：" + terGroup + "\n");
		content = content.concat("  所属通报组：" + notiGroup + "\n");
		content = content.concat("  推播延時：" + delay + "分钟\n");
        return content;
    }
}
