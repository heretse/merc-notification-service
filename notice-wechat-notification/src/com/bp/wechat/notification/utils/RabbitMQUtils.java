
package com.bp.wechat.notification.utils;

import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.concurrent.TimeoutException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.bp.wechat.notification.amqp.BusinessProcess;
import com.bp.wechat.notification.jdbc.JDBCOperation;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Consumer;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;

/**
 * @ClassName: RabbitMQUtils
 * @Description: TODO(AMQP工作初始化)
 * @author tianqifeng
 * @date 2018年1月24日 下午1:44:15
 */
public class RabbitMQUtils
{
    private static Logger logger = LogManager.getLogger(LogManager.ROOT_LOGGER_NAME);
    private static Connection connection = null;
    public static Channel channel = null;
    public static Map<Object, Object> map = null;
    public static final String DEFAULT_FORMAT = "yyyy-MM-dd HH:mm:ss";

    /**
     * @Title: InitAMQP
     * @Description: TODO(建立AMQP链接信息)
     * @param @throws Exception 设定文件
     * @return void 返回类型
     * @throws
     */
    public static void InitAMQP() throws Exception
    {

        map = getOperConnInfo();
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
            channel.exchangeDeclare(map.get("exchange_name").toString(), "direct", true);
            if (map.containsKey("routingKey"))
            {
                channel.queueBind(map.get("qu_name_uldata").toString(), map.get("exchange_name").toString(), map.get("routingKey").toString());
            }
            else
            {
                logger.info("not found para :" + "routingKey");
                return;
            }
        }
        else
        {
            logger.info("not found para :" + "exchange_name or qu_name_uldata");
            return;
        }
        logger.info("notice wechat Ready to receive messages.");
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
                    logger.info("wechat amqp data : " + recv);
                    Map<String, Integer> deliveryIdMap = BusinessProcess.processData(recv);
                    if (!deliveryIdMap.isEmpty())
                    {
                        for (String key : deliveryIdMap.keySet())
                        {
                            Integer value = deliveryIdMap.get(key);
                            if (value.intValue() != 0)
                            {
                                String up_time = new SimpleDateFormat(DEFAULT_FORMAT).format(new Date());
                                JDBCOperation.updateContent(value.intValue(), 1, key, up_time);
                            }
                            else
                            {
                                logger.info("amqp报警消息未插入notification_data表");
                            }
                        }
                    }
                    else
                    {
                        logger.info("amqp报警消息未插入notification_data表");
                    }

                }
                catch (Exception e)
                {
                    logger.info("wechat amqp 接收异常" + e.getMessage());
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

    private static Map<Object, Object> getOperConnInfo()
    {

        Map<Object, Object> map = new HashMap<Object, Object>();
        try
        {

            InputStream stream = RabbitMQUtils.class.getResourceAsStream("/transmit.conf.properties");

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
                    logger.info(new Date() + "wechat amqp 参数如下**********************************\n" + para.toString());
                }
            }
        }
        catch (IOException e)
        {
            logger.info("wechat getInitConnInfo IOException" + e.getMessage());
            e.printStackTrace();
        }
        return map;
    }
}
