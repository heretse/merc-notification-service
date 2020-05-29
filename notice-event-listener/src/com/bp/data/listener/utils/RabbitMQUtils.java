package com.bp.data.listener.utils;

import java.io.IOException;

import java.io.InputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.alibaba.fastjson.JSONObject;
import com.bp.data.listener.dao.EventSerialDao;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Consumer;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;
import com.rabbitmq.client.MessageProperties;

/**
 * *
 * 
 * @ClassName: RabbitMQUtils
 * @Description: TODO(事件接收解析)
 * @author Administrator
 * @date 2018年2月5日 上午11:28:06
 */
public class RabbitMQUtils {

	private static final Logger logger = LogManager.getRootLogger();
	private static Connection connection = null;
	private static Channel channel = null;
	public static Map<Object, Object> map = null;
	private static String receiverConfFile = "rabbitmq.receiver.properties";
	private static String senderConfFile = "rabbitmq.sender.properties";

	/**
	 * 
	 * @Title: InitAMQP @Description: 建立AMQP链接信息 @param @throws Exception
	 *         设定文件 @return void 返回类型 @throws
	 */
	public static void InitAMQP() throws Exception {

		map = getOperConnInfo(receiverConfFile);
		ConnectionFactory factory = new ConnectionFactory();

		if (map.containsKey("user_name")) {
			factory.setUsername(map.get("user_name").toString());
		} else {
			System.out.println("not found para :" + "user_name");
			return;
		}
		if (map.containsKey("passwd")) {
			factory.setPassword(map.get("passwd").toString());
		} else {
			System.out.println("not found para :" + "passwd");
			return;
		}
		if (map.containsKey("vhost")) {
			factory.setVirtualHost(map.get("vhost").toString());
		} else {
			System.out.println("not found para :" + "vhost");
			return;
		}
		if (map.containsKey("host")) {
			factory.setHost(map.get("host").toString());
		} else {
			System.out.println("not found para :" + "host");
			return;
		}

		if (map.containsKey("ssl_port")) {
			factory.setPort(Integer.valueOf(map.get("ssl_port").toString()));
		} else {
			System.out.println("not found para :" + "ssl_port");
			return;
		}
		factory.setAutomaticRecoveryEnabled(true);
		connection = factory.newConnection();
		channel = connection.createChannel();

		if (map.containsKey("exchange_name") && map.containsKey("qu_name_uldata")) {
			channel.exchangeDeclare(map.get("exchange_name").toString(), "direct", true);
			channel.queueBind(map.get("qu_name_uldata").toString(), map.get("exchange_name").toString(), "");
		} else {
			System.out.println("not found para :" + "exchange_name");
			return;
		}

		System.out.println("notice-event-listener Ready to receive messages.");
		recvMessage(channel);

	}

	private static void recvMessage(final Channel channel) throws Exception {
		channel.basicQos(100);
		Consumer consumer = new DefaultConsumer(channel) {
			@Override
			public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties,
					byte[] body) throws IOException {
				String recv = new String(body, "UTF-8");
				channel.basicAck(envelope.getDeliveryTag(), false);
				try {
					System.out.println("notice-event-listener amqp data : " + recv);
					// 解析数据
					JSONObject jsonObject = JSONObject.parseObject(recv);
					Map<String, Object> map = new HashMap<String, Object>();
					if (jsonObject.get("mac") != null && jsonObject.get("mac") != "") {
						map.put("mac", jsonObject.get("mac"));
					} else {
						System.out.println("miss parameter mac");
						return;
					}
					map.put("appId", jsonObject.get("appId"));
					map.put("description", jsonObject.get("description"));
					map.put("recv", jsonObject.get("recv"));
					if (jsonObject.get("extra") != null && jsonObject.get("extra") != "") {
						map.put("extra", jsonObject.get("extra").toString());
					}
					map.put("terGroup", jsonObject.get("terGroup"));
					if (jsonObject.get("notiGroup") != null && jsonObject.get("notiGroup") != "") {
						if(jsonObject.get("notiGroup").toString().length() > 20000)
						  map.put("notiGroup", jsonObject.get("notiGroup").toString().substring(0, 20000));
						else
							map.put("notiGroup", jsonObject.get("notiGroup").toString());
					}
					// 数据入库，存入事件流水表
					SpringContextUtil.getApplicationContext().getBean(EventSerialDao.class).add(map);
					// 转发
					eventListenerSender(recv);

				} catch (Exception e) {
					System.out.println("notice-event-listener amqp 接收异常" + e.getMessage());
				}
			}
		};
		// Set consumer on this channel.
		channel.basicConsume(map.get("qu_name_uldata").toString(), false, consumer);

	}

	/**
	 * 
	 * @Title: closeAMQP @Description: 关闭AMQP信息 @param @throws IOException
	 *         设定文件 @return void 返回类型 @throws
	 */
	public static void closeAMQP() throws IOException {
		if (channel != null) {
			channel.close();
		}
		if (connection != null) {
			connection.close();
		}

	}

	private static Map<Object, Object> getOperConnInfo(String fileName) {

		Map<Object, Object> map = new HashMap<Object, Object>();
		try {

			InputStream stream = RabbitMQUtils.class.getResourceAsStream("/" + fileName);

			Properties prop = new Properties();
			if (stream != null) {
				prop.load(stream);
				Iterator<Entry<Object, Object>> it = prop.entrySet().iterator();
				StringBuffer para = new StringBuffer();
				while (it.hasNext()) {
					Entry<Object, Object> entry = it.next();
					Object key = entry.getKey();
					Object value = entry.getValue();
					para.append(key + "=" + value + "\n");
					map.put(key.toString(), value.toString());
				}
				stream.close();
				if (para.length() > 0) {
					logger.info(new Date() + fileName + "AMQP参数如下**********************************\n"
							+ para.toString());
					logger.info(new Date() + " map: " + map);
				}
			}
		} catch (IOException e) {
			logger.info("notice-event-listener getInitConnInfo IOException" + e.getMessage());
			e.printStackTrace();
		}
		return map;
	}

	/**
	 * * @Title: Sender @Description: TODO(数据转发) @param @param message 设定文件 @return
	 * void 返回类型 @throws
	 */
	private static void eventListenerSender(String message) {

		try {
			Map<Object, Object> newmap = getOperConnInfo(senderConfFile);

			ConnectionFactory connFac = new ConnectionFactory();
			if (newmap.containsKey("user_name")) {
				connFac.setUsername(newmap.get("user_name").toString());
			} else {
				System.out.println("not found para :" + "user_name");
				return;
			}
			if (newmap.containsKey("passwd")) {
				connFac.setPassword(newmap.get("passwd").toString());
			} else {
				System.out.println("not found para :" + "passwd");
				return;
			}
			if (newmap.containsKey("vhost")) {
				connFac.setVirtualHost(newmap.get("vhost").toString());
			} else {
				System.out.println("not found para :" + "setVirtualHost");
				return;
			}
			if (newmap.containsKey("ssl_port")) {
				connFac.setPort(Integer.parseInt(newmap.get("ssl_port").toString()));
			} else {
				System.out.println("not found para :" + "ssl_port");
				return;
			}
			if (newmap.containsKey("host")) {
				connFac.setHost(newmap.get("host").toString());
			} else {
				System.out.println("not found para :" + "host");
				return;
			}
			connFac.setAutomaticRecoveryEnabled(true); // 网络故障自动回复使能
			connFac.setNetworkRecoveryInterval(1000 * 10); // 网络故障恢复间隔 10秒
			Connection conn = null;
			conn = connFac.newConnection();
			// creating a channel
			Channel channel = conn.createChannel();
			// channel.basicQos(100);
			if (newmap.containsKey("exchange_name")) {
				channel.exchangeDeclare(newmap.get("exchange_name").toString(), "direct", true);
			} else {
				System.out.println("not found para :" + "exchange_name");
				return;
			}

			Map<String, Object> args1 = new HashMap<String, Object>();
			args1.put("x-message-ttl", 28800000);
			if (newmap.containsKey("qu_name_uldata")) {
				channel.queueDeclare(newmap.get("qu_name_uldata").toString(), true, false, false, args1);
			} else {
				System.out.println("not found para :" + "qu_name_uldata");
				return;
			}

			channel.queueBind(newmap.get("qu_name_uldata").toString(), newmap.get("exchange_name").toString(), "");
			byte[] mesg = message.getBytes();
			if (null != channel) {

				channel.basicPublish(newmap.get("exchange_name").toString(), "",
						MessageProperties.PERSISTENT_TEXT_PLAIN, mesg);
				System.out.println("AMQPSender basicPublish success");
				// Thread.sleep(5);
				channel.close();
				conn.close();
			}
		} catch (IOException e) {
			System.out.println("eventListener amqp 转发异常" + e.getMessage());
		}
	}

}
