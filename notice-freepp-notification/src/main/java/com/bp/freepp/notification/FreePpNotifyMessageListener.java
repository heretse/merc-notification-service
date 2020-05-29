package com.bp.freepp.notification;

import com.bp.freepp.notification.model.NotificationData;
import com.bp.freepp.notification.model.NotificationGroup;
import com.bp.freepp.notification.repo.NotificationDataRepository;
import com.bp.freepp.notification.repo.NotificationGroupRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.AmqpRejectAndDontRequeueException;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.json.JsonParser;
import org.springframework.boot.json.JsonParserFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Message Listener for RabbitMQ
 */

@Service
public class FreePpNotifyMessageListener {

    private static final Logger logger = LoggerFactory.getLogger(FreePpNotifyMessageListener.class);

    @Autowired
    private NotificationGroupRepository groupRepository;

    @Autowired
    private NotificationDataRepository dataRepository;

    /**
     * Message listener for FreePP
     *
     */
    @RabbitListener(queues = "${freepp.notify.queue.name}")
    public void receiveMessage(byte[] bytes) {

        String json = new String(bytes);

        logger.info("Received message: {} from FreePP queue.", json);

        JsonParser springParser = JsonParserFactory.getJsonParser();

        try {
//            log.info("Making REST call to the API");
            int id = 0;
            String description = null;
            String notiGroup = "";
            List<String> tokenList = new ArrayList<String>();
            String mac = null;
            String terGroup = null;
            String recv = null;
            long delay = -1;
            Map<String, Object> parameterMap = new HashMap<String, Object>();
            parameterMap = springParser.parseMap(json);

            if (parameterMap.isEmpty()) {
                return;
            }
            if (parameterMap.containsKey("delay") && null != parameterMap.get("delay")) {
                delay = Long.parseLong(parameterMap.get("delay").toString());
            } else {
                logger.info("上報告警數據 delay 參數為空");
                return;
            }
            if (parameterMap.containsKey("notiGroup") && null != parameterMap.get("notiGroup")) {
                List list = (List) parameterMap.get("notiGroup");
                int flag = 0;
                int accountFlag = 0;
                for (int i = 0; i < list.size(); i++) {

                    Map<String, Object> map = (Map<String, Object>) list.get(i);
                    for (String groupkey : map.keySet()) {
                        if (delay != -1) {
                            long DBdelay = -1;
                            NotificationGroup group = groupRepository.findGroupByName(groupkey);
                            if (group != null && group.getDelay() != null) {
                                DBdelay = group.getDelay().longValue();
                            }

                            if (DBdelay != -1) {
                                if (delay == DBdelay) {
                                    flag++;
                                    if (flag >= 2) {
                                        notiGroup = notiGroup.concat("，");
                                    }
                                    notiGroup = notiGroup.concat(groupkey);
                                    List groupList = (List) map.get(groupkey);
                                    for (int j = 0; j < groupList.size(); j++) {
                                        Map<String, Object> groupMap = (Map<String, Object>) groupList.get(j);
                                        for (String namekey : groupMap.keySet()) {
                                            List objectList = (List) groupMap.get(namekey);
                                            for (int k = 0; k < objectList.size(); k++) {
                                                Map<String, Object> objectMap = (Map<String, Object>)objectList.get(k);
                                                if ("FreePP".equals(objectMap.get("method").toString())) {
                                                    tokenList.add(objectMap.get("account").toString());
                                                }
                                            }
                                        }
                                    }
                                } else {
                                    continue;
                                }
                            } else {
                                logger.info("根據通報組查詢延遲失敗");
                                return;
                            }
                        } else {
                            logger.info("上報告警數據未獲取到 delay 參數");
                            return;
                        }

                    }

                }
            } else {
                logger.info("上报告警数据 notiGroup 参数为空");
                return;
            }

            if (parameterMap.containsKey("description") && null != parameterMap.get("description")) {
                description = (String) parameterMap.get("description");
            } else {
                logger.info("上報告警數據 description 參數為空");
                return;
            }

            if (parameterMap.containsKey("mac") && null != parameterMap.get("mac")) {
                mac = (String) parameterMap.get("mac");
            } else {
                logger.info("上報告警數據 mac 參數為空");
                return;
            }

            if (parameterMap.containsKey("terGroup") && null != parameterMap.get("terGroup")) {
                terGroup = (String) parameterMap.get("terGroup");
            } else {
                logger.info("上報告警數據 terGroup 參數為空");
                return;
            }

            if (parameterMap.containsKey("recv") && null != parameterMap.get("recv")) {
                recv = (String) parameterMap.get("recv");
            } else {
                logger.info("上報告警數據 recv 參數為空");
                return;
            }

            if (!tokenList.isEmpty()) {
                String content = FreePpNotifyMessageListener.contentConcat(delay, notiGroup, description, mac, terGroup, recv);

                ObjectMapper mapper = new ObjectMapper();

                Date in = new Date();
                NotificationData newData = new NotificationData();
                if (parameterMap.get("notiGroup") != null) {
                    newData.setNotificationGroup(mapper.writeValueAsString(parameterMap.get("notiGroup")));
                }
                newData.setTerminalGroup(terGroup);
                newData.setMac(mac);
                newData.setDescription(description);
                newData.setReceiveTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(recv));
                newData.setDelay(Long.valueOf(delay));
                newData.setInTime(in);
                newData.setUpTime(in);

                dataRepository.save(newData);

                String delivery_time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
                for (String token : tokenList) {

                    boolean flag = FreePpNotifyMessageListener.pushMessage(token, content);

                    if (flag) {
                        logger.info("FreePP Notify 推送成功 with token:" + token);

                        Date up = new Date();
                        newData.setDeliverySucess(1);
                        newData.setDeliveryTime(up);
                        newData.setUpTime(up);

                        dataRepository.save(newData);
                    } else {
                        logger.info("FreePP Notify 推送失败 with token:" + token);
                    }
                }

                return;
            } else {
                logger.info("此延時沒有 FreePP Notify 的用戶");
            }
            return;
        } catch(Exception e) {
            logger.error("Internal server error occurred in python server. Bypassing message requeue {}", e);
            throw new AmqpRejectAndDontRequeueException(e);
        }
    }

    /**
     * @Title: contentConCat @Description: TODO(拼接推送 FreePP Notify 内容) @param @param
     * delay @param @param notiGroup @param @param description @param @param
     * mac @param @param terGroup @param @param recv @param @return 设定文件 @return
     * String 返回类型 @throws
     */
    public static String contentConcat(long delay, String notiGroup, String description, String mac, String terGroup,
                                       String recv) {
        String content = description;

        return content;
    }

    public static boolean pushMessage(String token, String content) {
        RestTemplate restTemplate = new RestTemplate();
        String url = new String("https://pro20.freepp.com/bot/v1/message/push");

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");
        headers.add("Authorization", "Bearer QQUnatOESm2XgCTPNKIscTcHBA8P9VLX83bXDPYE");
        HttpEntity<String> entity = new HttpEntity<>("{\"to\":\"" + token + "\",\"messages\":[{\"type\":\"Text\",\"value\":\"" + content + "\"}]}", headers);

        ResponseEntity<String> response = restTemplate.postForEntity(url, entity, String.class);

        if (response.getStatusCodeValue() == 200) {
            return true;
        } else {
            return false;
        }
    }
}
