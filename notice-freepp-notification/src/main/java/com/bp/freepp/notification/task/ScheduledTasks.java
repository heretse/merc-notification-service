package com.bp.freepp.notification.task;

import com.bp.freepp.notification.FreePpNotifyMessageListener;
import com.bp.freepp.notification.model.NotificationData;
import com.bp.freepp.notification.model.NotificationGroup;
import com.bp.freepp.notification.repo.NotificationDataRepository;
import com.bp.freepp.notification.repo.NotificationGroupRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.json.JsonParser;
import org.springframework.boot.json.JsonParserFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Component
public class ScheduledTasks {

    private static final Logger logger = LoggerFactory.getLogger(ScheduledTasks.class);

    private static final SimpleDateFormat dateTimeFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    @Autowired
    private NotificationGroupRepository groupRepository;

    @Autowired
    private NotificationDataRepository dataRepository;

    @Scheduled(fixedDelay = 10 * 60 * 1000, initialDelay = 5 * 60 * 1000)
    public void scheduleTaskWithFixedDelay() {
        logger.info("Fixed Delay Task :: Execution Time - {}", dateTimeFormatter.format(new Date()));

        JsonParser springParser = JsonParserFactory.getJsonParser();

        List<NotificationData> unsentData = dataRepository.findAllByStatus(new Integer(0));

        for (NotificationData data : unsentData) {
            String description = null;
            String notiGroup = "";
            List<String> tokenList = new ArrayList<String>();
            String terminal_group = null;
            String allAccount = "";
            String recv = null;
            String mac = null;
            long delay = -1;


            if (data.getDelay() != null)
            {
                delay = data.getDelay();
            }
            else
            {
                logger.info("未推送數據 delay 欄位為空");
                continue;
            }

            if (data.getNotificationGroup() != null)
            {
                List lists = springParser.parseList(data.getNotificationGroup());

//                List lists = new ArrayList(JSONArray.parseArray(parameterMap.get("notification_group").toString()));
                int flag = 0;
                int accountFlag = 0;
                for (int n = 0; n < lists.size(); n++)
                {
                    Map<String, Object> map = (Map<String, Object>)lists.get(n);
                    for (String groupkey : map.keySet())
                    {
                        if (delay != -1)
                        {
                            long DBdelay = -1;
                            NotificationGroup group = groupRepository.findGroupByName(groupkey);
                            if (group != null && group.getDelay() != null) {
                                DBdelay = group.getDelay().longValue();
                            }

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
                                    List groupList = (List) map.get(groupkey);

                                    for (int j = 0; j < groupList.size(); j++)
                                    {
                                        Map<String, Object> groupMap = (Map<String, Object>)groupList.get(j);
                                        for (String namekey : groupMap.keySet())
                                        {
                                            List objectList = (List) groupMap.get(namekey);
                                            for (int k = 0; k < objectList.size(); k++)
                                            {
                                                Map<String, Object> objectMap = (Map<String, Object>)objectList.get(k);
                                                if ("FreePP".equals(objectMap.get("method").toString()))
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
                                logger.info("根據通報組查詢延遲失敗");
                                return;
                            }
                        }
                        else
                        {
                            logger.info("上報告警數據未獲取到delay參數");
                            return;
                        }

                    }

                }
            }
            else
            {
                logger.info("未推送數據 notification_group 欄位為空");
                return;
            }
            if (data.getDescription() != null)
            {
                description = data.getDescription();
            }
            else
            {
                logger.info("未推送數據 description 欄位為空");
                return;
            }
            if (data.getTerminalGroup() != null)
            {
                terminal_group = data.getTerminalGroup();
            }
            else
            {
                logger.info("未推送數據 terminal_group 欄位為空");
                return;
            }
            if (data.getMac() != null)
            {
                mac = data.getMac();
            }
            else
            {
                logger.info("未推送數據 mac 欄位為空");
                return;
            }
            if (data.getReceiveTime() != null)
            {
                recv = dateTimeFormatter.format(data.getReceiveTime());
            }
            else
            {
                logger.info("未推送數據 recv 欄位為空");
                return;
            }

            if (!tokenList.isEmpty()) {
                String tempRecv = recv.substring(0, 19);
                String content = FreePpNotifyMessageListener.contentConcat(delay, notiGroup, description, mac, terminal_group, tempRecv);
                String delivery_time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());

                for (String token : tokenList) {

                    boolean flag = FreePpNotifyMessageListener.pushMessage(token, content);
                    if (flag) {
                        logger.info("FreePP Notify 推送成功 token:" + token);
                        Date up_time = new Date();
                        data.setDeliverySucess(new Integer(1));
                        data.setDeliveryTime(up_time);
                        data.setUpTime(up_time);

                        dataRepository.save(data);
                    } else {
                        logger.info("FreePP Notify 推送失敗 token:" + token);
                    }
                }
            }
        }
    }
}
