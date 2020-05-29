
package com.bp.wechat.notification.test;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeoutException;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.gson.Gson;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import net.sf.json.JSONArray;

public class Producer
{
    public static final String DEFAULT_FORMAT = "yyyy-MM-dd HH:mm:ss";
    private static String queue_name = "ul.jingdfh-test6.deliver.que";

    public static void main(String[] args) throws IOException, TimeoutException
    {
        test();
    }

    public static void test() throws IOException, TimeoutException
    {

        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("192.168.6.72");
        factory.setUsername("jingdfh-test6");
        factory.setPassword("jingdfh-test6");
        factory.setVirtualHost("/jingdfh-test6");
        // factory.setVirtualHost(RabbitMQInfo.VIRTUAL_HOST);
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();
        HashMap<String, Object> arguments = new HashMap<String, Object>();
        arguments.put("x-dead-letter-exchange", "amq.direct");
        arguments.put("x-dead-letter-routing-key", "message_ttl_routingKey");
        channel.queueDeclare("notice_delay_queue", true, false, false, arguments);
        channel.queueDeclare("notice_delay_queue2", true, false, false, arguments);
        // 声明队列
        channel.queueDeclare(queue_name, true, false, false, null);
        // 绑定路由
        channel.queueBind(queue_name, "amq.direct", "message_ttl_routingKey");
        String json = "{\r\n" + "    \"appId\": \"\",\r\n" + "    \"mac\": \"000000000700009c\",\r\n" + "    \"terGroup\": \"产线A\",\r\n"
                + "    \"recv\": \"2017-12-21 15:03:49\",\r\n" + "    \"description\": \"静电手环未佩戴\",\r\n" + "    \"notiGroup\": [{\r\n"
                + "            \"通报组1\": [{\r\n" + "                    \"田启锋\": [{\r\n" + "\"method\": \"eMail\",\r\n"
                + "                            \"account\": \"tianqifeng@bkclouds.com\"\r\n" + "                        }\r\n" + "                    ]\r\n"
                + "                }, {\r\n" + "                    \"李安安\": [{\r\n" + "\"method\": \"eMail\",\r\n"
                + "                            \"account\": \"lianan@bkclouds.com\"\r\n" + "                        }\r\n" + "                    ]\r\n"
                + "                }\r\n" + "            ]\r\n" + "        }, {\r\n" + "            \"通报组2\": [{\r\n" + "                    \"彭玉涛\": [{\r\n"
                + "\"method\": \"eMail\",\r\n" + "                            \"account\": \"pengyutao@bkclouds.com\"\r\n" + "                        }\r\n"
                + "                    ]\r\n" + "                }\r\n" + "            ]\r\n" + "        }\r\n" + "    ],\r\n" + "    \"extra\": {}\r\n" + "} ";
        String json2 = "{\r\n" + "        \"delay\": \"0\",\r\n" + "    \"appId\": \"\",\r\n" + "    \"mac\": \"000000000700009c\",\r\n"
                + "    \"terGroup\": \"产线A\",\r\n" + "    \"recv\": \"2017-12-21 15:03:49\",\r\n" + "    \"description\": \"静电手环未佩戴\",\r\n"
                + "    \"notiGroup\": [{\r\n" + "            \"通报组1\": [{\r\n" + "                    \"田启锋\": [{\r\n"
                + "                            \"method\": \"eMail\",\r\n" + "                            \"account\": \"tianqifeng@bkclouds.com\"\r\n"
                + "                        }\r\n" + "                    ]\r\n" + "                }\r\n" + "            ]\r\n" + "        }, {\r\n"
                + "            \"通报组2\": [{\r\n" + "                    \"upyxkg16890@chacuo.net\": [{\r\n"
                + "                            \"method\": \"eMail\",\r\n" + "                            \"account\": \"upyxkg16890@chacuo.net\"\r\n"
                + "                        }\r\n" + "                    ]\r\n" + "                }\r\n" + "            ]\r\n" + "        }\r\n" + "    ],\r\n"
                + "    \"extra\": {}\r\n" + "} ";
        String json3 = "{\r\n" + "        \"delay\": \"60\",\r\n" + "    \"appId\": \"\",\r\n" + "    \"mac\": \"000000000700009c\",\r\n"
                + "    \"terGroup\": \"产线A\",\r\n" + "    \"recv\": \"2017-12-21 15:03:49\",\r\n" + "    \"description\": \"静电手环未佩戴\",\r\n"
                + "    \"notiGroup\": [{\r\n" + "            \"通报组1\": [{\r\n" + "                    \"田启锋\": [{\r\n"
                + "                            \"method\": \"eMail\",\r\n" + "                            \"account\": \"tianqifeng@bkclouds.com\"\r\n"
                + "                        }\r\n" + "                    ]\r\n" + "                }\r\n" + "            ]\r\n" + "        }, {\r\n"
                + "            \"通报组2\": [{\r\n" + "                    \"upyxkg16890@chacuo.net\": [{\r\n"
                + "                            \"method\": \"eMail\",\r\n" + "                            \"account\": \"upyxkg16890@chacuo.net\"\r\n"
                + "                        }\r\n" + "                    ]\r\n" + "                }\r\n" + "            ]\r\n" + "        }\r\n" + "    ],\r\n"
                + "    \"extra\": {}\r\n" + "}";
        // 设置延时属性
        AMQP.BasicProperties.Builder builder = new AMQP.BasicProperties.Builder();
        AMQP.BasicProperties properties = builder.expiration("0").deliveryMode(2).build();
        // routingKey =delay_queue 进行转发
        channel.basicPublish("", "notice_delay_queue", properties, json2.getBytes());
        System.out.println("sent message: " + json2 + ",date:" + System.currentTimeMillis());

        AMQP.BasicProperties properties2 = builder.expiration("30000").deliveryMode(2).build();
        // routingKey =delay_queue 进行转发
        channel.basicPublish("", "notice_delay_queue2", properties2, json3.getBytes());
        System.out.println("sent message: " + json3 + ",date:" + System.currentTimeMillis());
        // 关闭频道和连接
        channel.close();
        connection.close();
    }

}