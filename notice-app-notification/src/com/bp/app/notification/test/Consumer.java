
package com.bp.app.notification.test;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.bp.app.notification.utils.AppPush;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.QueueingConsumer;

public class Consumer
{
    Channel channel = null;
    private static String queue_name = "ul.jingdfh-test.app.que";
    public static final String DEFAULT_FORMAT = "yyyy-MM-dd HH:mm:ss";

    public static void main(String[] args) throws Exception
    {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("10.70.51.54");
        factory.setUsername("jingdfh-test");
        factory.setPassword("jingdfh-test");
        factory.setVirtualHost("/jingdfh-test");
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();

        //HashMap<String, Object> arguments = new HashMap<String, Object>();  
        //arguments.put("x-dead-letter-exchange", "amq.direct");  
        //arguments.put("x-dead-letter-routing-key", "message_ttl_routingKey");  
        //channel.queueDeclare("delay_queue", true, false, false, arguments);  

        // 声明队列  
        //channel.queueDeclare(queue_name, true, false, false, null);  
        // 绑定路由  
        //channel.queueBind(queue_name, "amq.direct", "message_ttl_routingKey");  
        QueueingConsumer consumer = new QueueingConsumer(channel);
        // 指定消费队列  
        channel.basicConsume(queue_name, true, consumer);
        while (true)
        {
            // nextDelivery是一个阻塞方法（内部实现其实是阻塞队列的take方法）
            QueueingConsumer.Delivery delivery = consumer.nextDelivery();
            String message = new String(delivery.getBody());
            Map<String, String> map = new HashMap<String, String>();
            map.put(message, "tianqifeng@bkclouds.com");
            String content = "你好, 今天" + date2Str(new Date(), DEFAULT_FORMAT) + "静电手环发生报警。望周知!";
            //WeChatPush.getInstance().sendMsg(allAccount, content);
            System.out.println("received message:" + message + ",date:" + System.currentTimeMillis());
        }
    }

    public static String date2Str(Date date, String format)
    {
        if (null == date)
        {
            return null;
        }
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        return sdf.format(date);
    }
}
