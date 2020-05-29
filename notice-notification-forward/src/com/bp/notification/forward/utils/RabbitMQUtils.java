
package com.bp.notification.forward.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.concurrent.TimeoutException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Consumer;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;
import com.rabbitmq.client.MessageProperties;

/**
 * @ClassName: RabbitMQUtils
 * @Description: TODO(AMQP工作初始化)
 * @author tianqifeng
 * @date 2018年1月24日 下午1:44:15
 */
public class RabbitMQUtils
{
    private static Logger logger = LogManager.getLogger(LogManager.ROOT_LOGGER_NAME);
    private static ConnectionFactory factory = new ConnectionFactory();
    private static Connection connection = null;
    public static Channel channel = null;
    public static Map<Object, Object> map = null;
    private static String receiverConfFile = "rabbitmq.receiver.properties";
    private static String senderConfFile = "rabbitmq.sender.properties";

    /**
     * @Title: InitAMQP
     * @Description: TODO(建立AMQP链接信息)
     * @param @throws Exception 设定文件
     * @return void 返回类型
     * @throws
     */
    public static void InitAMQP() throws Exception
    {

        map = getOperConnInfo(receiverConfFile);
        ConnectionFactory factory = new ConnectionFactory();

        if (map.containsKey("user_name"))
        {
            factory.setUsername(map.get("user_name").toString());
        }
        else
        {
            logger.info("not found para :" + "user_name");
            return;
        }
        if (map.containsKey("passwd"))
        {
            factory.setPassword(map.get("passwd").toString());
        }
        else
        {
            logger.info("not found para :" + "passwd");
            return;
        }
        if (map.containsKey("vhost"))
        {
            factory.setVirtualHost(map.get("vhost").toString());
        }
        else
        {
            logger.info("not found para :" + "vhost");
            return;
        }
        if (map.containsKey("host"))
        {
            factory.setHost(map.get("host").toString());
        }
        else
        {
            logger.info("not found para :" + "host");
            return;
        }

        if (map.containsKey("ssl_port"))
        {
            factory.setPort(Integer.valueOf(map.get("ssl_port").toString()));
        }
        else
        {
            logger.info("not found para :" + "ssl_port");
            return;
        }
        factory.setAutomaticRecoveryEnabled(true);
        connection = factory.newConnection();
        channel = connection.createChannel();

        if (map.containsKey("exchange_name") && map.containsKey("qu_name_uldata"))
        {
            channel.exchangeDeclare(map.get("exchange_name").toString(), "topic", true);
            channel.queueBind(map.get("qu_name_uldata").toString(), map.get("exchange_name").toString(), "");
        }
        else
        {
            logger.info("not found para :" + "exchange_name");
            return;
        }
        logger.info("notice Ready to receive messages.");
        recvMessage(channel);

    }

    private static void recvMessage(final Channel channel) throws Exception
    {
        channel.basicQos(100);
        Consumer consumer = new DefaultConsumer(channel)
        {
            //Delivery是一个阻塞方法（内部实现其实是阻塞队列的take方法）
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException
            {
                String recv = new String(body, "UTF-8");
                channel.basicAck(envelope.getDeliveryTag(), false);
                try
                {
                    logger.info("notice amqp data : " + recv);
                    JSONObject jsonObject = JSONObject.parseObject(recv);
                    if (jsonObject.get("mac") != null && jsonObject.get("mac") != "")
                    {
                        map.put("mac", jsonObject.get("mac"));
                    }
                    else
                    {
                        logger.info("miss parameter mac");
                        return;
                    }
                    processData(recv);

                }
                catch (Exception e)
                {
                    logger.info("notice amqp 接收异常" + e.getMessage());
                }
            }
        };
        // Set consumer on this channel.
        channel.basicConsume(map.get("qu_name_uldata").toString(), false, consumer);

    }

    /**
     * @Title: closeAMQP
     * @Description: TODO(关闭AMQP信息)
     * @param @throws IOException 设定文件
     * @return void 返回类型
     * @throws
     */
    public static void closeAMQP() throws IOException
    {
        if (channel != null)
        {
            try
            {
                channel.close();
            }
            catch (TimeoutException e)
            {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        if (connection != null)
        {
            connection.close();
        }

    }

    private static Map<Object, Object> getOperConnInfo(String propertiesFile)
    {

        Map<Object, Object> map = new HashMap<Object, Object>();
        try
        {

            InputStream stream = RabbitMQUtils.class.getResourceAsStream("/" + propertiesFile);

            Properties prop = new Properties();
            if (stream != null)
            {
                prop.load(stream);
                Iterator<Entry<Object, Object>> it = prop.entrySet().iterator();
                StringBuffer para = new StringBuffer();
                while (it.hasNext())
                {
                    Entry<Object, Object> entry = it.next();
                    Object key = entry.getKey();
                    Object value = entry.getValue();
                    para.append(key + "=" + value + "\n");
                    map.put(key.toString(), value.toString());
                }
                stream.close();
                if (para.length() > 0)
                {
                    logger.info(new Date() + " forward amqp 参数如下**********************************\n" + para.toString());
                    logger.info(new Date() + " map: " + map);
                }
            }
        }
        catch (IOException e)
        {
            logger.info("NOTICE getInitConnInfo IOException" + e.getMessage());
            e.printStackTrace();
        }
        return map;
    }

    /**
     * @Title: forwardSender
     * @Description: TODO(推送forward后的amqp消息)
     * @param @param message 设定文件
     * @return void 返回类型
     * @throws
     */
    private static void forwardSender(Map<Integer, String> allMessage)
    {
        try
        {
            Map<Object, Object> newmap = getOperConnInfo(senderConfFile);
            String routingKey = null;
            ConnectionFactory connFac = new ConnectionFactory();
            if (newmap.containsKey("user_name"))
            {
                connFac.setUsername(newmap.get("user_name").toString());
            }
            else
            {
                logger.info("not found para :" + "user_name");
                return;
            }
            if (newmap.containsKey("passwd"))
            {
                connFac.setPassword(newmap.get("passwd").toString());
            }
            else
            {
                logger.info("not found para :" + "passwd");
                return;
            }
            if (newmap.containsKey("vhost"))
            {
                connFac.setVirtualHost(newmap.get("vhost").toString());
            }
            else
            {
                logger.info("not found para :" + "setVirtualHost");
                return;
            }
            if (newmap.containsKey("ssl_port"))
            {
                connFac.setPort(Integer.parseInt(newmap.get("ssl_port").toString()));
            }
            else
            {
                logger.info("not found para :" + "ssl_port");
                return;
            }
            if (newmap.containsKey("host"))
            {
                connFac.setHost(newmap.get("host").toString());
            }
            else
            {
                logger.info("not found para :" + "host");
                return;
            }
            if (newmap.containsKey("host"))
            {
                connFac.setHost(newmap.get("host").toString());
            }
            else
            {
                logger.info("not found para :" + "host");
                return;
            }
            if (newmap.containsKey("routingKey"))
            {
                routingKey = newmap.get("routingKey").toString();
            }
            else
            {
                logger.info("not found para :" + "routingKey");
                return;
            }
            connFac.setAutomaticRecoveryEnabled(true); // 网络故障自动回复使能
            connFac.setNetworkRecoveryInterval(1000 * 10); // 网络故障恢复间隔 10秒
            Connection conn = null;
            conn = connFac.newConnection();
            // creating a channel
            Channel channel = conn.createChannel();
            // channel.basicQos(100);
            if (newmap.containsKey("exchange_name"))
            {
                channel.exchangeDeclare(newmap.get("exchange_name").toString(), "direct", true);
            }
            else
            {
                logger.info("not found para :" + "exchange_name");
                return;
            }

            Map<String, Object> args1 = new HashMap<String, Object>();
            args1.put("x-message-ttl", 28800000);
            logger.info("allMessage :" + allMessage.toString());
            for (Integer key : allMessage.keySet())
            {
                if (newmap.containsKey("qu_name_uldata"))
                {
                    channel.queueDeclare(newmap.get("qu_name_uldata").toString().split(",")[key], true, false, false, args1);
                }
                else
                {
                    logger.info("not found para :" + "qu_name_uldata" + key);
                    return;
                }
                channel.queueBind(newmap.get("qu_name_uldata").toString().split(",")[key], newmap.get("exchange_name").toString(), routingKey.split(",")[key]);
                byte[] mesg = allMessage.get(key).getBytes();
                if (null != channel && null != routingKey)
                {

                    channel.basicPublish(newmap.get("exchange_name").toString(), routingKey.split(",")[key], MessageProperties.PERSISTENT_TEXT_PLAIN, mesg);
                    logger.info("AMQPSender basicPublish success");
                    // Thread.sleep(5);

                }

            }
            channel.close();
            conn.close();
        }
        catch (NumberFormatException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        catch (IOException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        catch (TimeoutException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /**
     * @Title: processData
     * @Description: TODO(告警json数据转换)
     * @param @param json 设定文件
     * @return void 返回类型
     * @throws
     */
    public static void processData(String json)
    {
        Map<Object, Object> newmap = getOperConnInfo(senderConfFile);
        String[] method = null;
        Map<String, Object> parameterMap = new HashMap<String, Object>();
        parameterMap = JSON.parseObject(json, Map.class);
        Map<Integer, String> allMap = new HashMap<Integer, String>();
        if (parameterMap.isEmpty())
        {
            return;
        }
        if (newmap.containsKey("routingKey"))
        {
            method = newmap.get("routingKey").toString().split(",");
        }
        else
        {
            logger.info("not found para :" + "routingKey");
            return;
        }
        if (parameterMap.containsKey("notiGroup") && null != parameterMap.get("notiGroup"))
        {
            //存放的value为整个notiGroup的值
            Map<String, List<Map<String, Object>>> methodMap = new HashMap<String, List<Map<String, Object>>>();
            List list = new ArrayList(JSONArray.parseArray(parameterMap.get("notiGroup").toString()));
            for (int i = 0; i < method.length; i++)
            {
                List<Map<String, Object>> methodList = new ArrayList<Map<String, Object>>();
                methodMap.put(method[i], methodList);
            }
            for (int i = 0; i < list.size(); i++)
            {
                //methodMap2的value存放的是通报组的value
                Map<String, Map<String, Object>> methodMap2 = new HashMap<String, Map<String, Object>>();
              //methodMap3的value存放的是所有通报组的value的list集合
                Map<String, List<Map<String, Object>>> methodMap3 = new HashMap<String, List<Map<String, Object>>>();
                Map<String, Object> map = (Map<String, Object>)list.get(i);
                String tmpMethod = "";
                String groupkey2 = "";
                for (String groupkey : map.keySet())
                {

                    groupkey2 = groupkey;
                    for (int n = 0; n < method.length; n++)
                    {
                        Map<String, Object> map2 = new HashMap<String, Object>();
                        methodMap2.put(method[n], map2);
                        List<Map<String, Object>> methodList = new ArrayList<Map<String, Object>>();
                        methodMap3.put(method[n], methodList);
                    }
                    List groupList = new ArrayList(JSONArray.parseArray(map.get(groupkey).toString()));
                    for (int j = 0; j < groupList.size(); j++)
                    {
                        Map<String, Object> groupMap = (Map<String, Object>)groupList.get(j);
                        for (String namekey : groupMap.keySet())
                        {
                            //methodMap4的value存放的是通报组里的每一个对象
                            Map<String, Map<String, Object>> methodMap4 = new HashMap<String, Map<String, Object>>();
                            //methodMap5的value存放的是对象的account和method
                            Map<String, List<Map<String, Object>>> methodMap5 = new HashMap<String, List<Map<String, Object>>>();
                            for (int n = 0; n < method.length; n++)
                            {
                                Map<String, Object> map2 = new HashMap<String, Object>();
                                methodMap4.put(method[n], map2);
                                List<Map<String, Object>> methodList = new ArrayList<Map<String, Object>>();
                                methodMap5.put(method[n], methodList);
                            }
                            List objectList = new ArrayList(JSONArray.parseArray(groupMap.get(namekey).toString()));
                            for (int k = 0; k < objectList.size(); k++)
                            {
                                Map<String, Object> objectMap = (Map<String, Object>)objectList.get(k);
                                for (int n = 0; n < method.length; n++)
                                {
                                    if (method[n].equals(objectMap.get("method").toString()))
                                    {
                                        tmpMethod = objectMap.get("method").toString();
                                        methodMap5.get(method[n]).add(objectMap);
                                        methodMap4.get(method[n]).put(namekey, methodMap5.get(method[n]));
                                        break;
                                    }
                                }
                            }

                            for (int n = 0; n < method.length; n++)
                            {
                                if (method[n].equals(tmpMethod))
                                {
                                    methodMap3.get(method[n]).add(methodMap4.get(method[n]));
                                    tmpMethod = "";
                                    break;
                                }

                            }
                        }
                    }

                }
                for (int n = 0; n < method.length; n++)
                {
                    if (methodMap3.get(method[n]).size() > 0)
                    {
                        methodMap2.get(method[n]).put(groupkey2, methodMap3.get(method[n]));
                        methodMap.get(method[n]).add(methodMap2.get(method[n]));
                    }
                }
            }
            for (int n = 0; n < method.length; n++)
            {
                if (methodMap.get(method[n]).size() > 0)
                {
                    parameterMap.put("notiGroup", methodMap.get(method[n]));
                    allMap.put(n, JSON.toJSONString(parameterMap, SerializerFeature.DisableCircularReferenceDetect));
                }

            }
            forwardSender(allMap);
        }
        else
        {
            logger.info("上报告警数据notiGroup参数为空");
            return;
        }
    }
}
