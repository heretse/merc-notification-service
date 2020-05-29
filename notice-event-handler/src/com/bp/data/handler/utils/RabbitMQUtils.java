
package com.bp.data.handler.utils;

import java.io.IOException;

import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.alibaba.fastjson.JSONObject;
import com.bp.data.handler.dao.MessageDao;
import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.util.JSON;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Consumer;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;

/**
 * @ClassName: RabbitMQUtils
 * @Description: AMQP工作初始化
 * @author pyt
 * @date 2018年1月24日 上午10:11:36
 */
public class RabbitMQUtils {
	private static final Logger logger = LogManager.getRootLogger();
	private static Connection connection = null;
	private static Channel channel = null;
	public static Map<Object, Object> map = null;
	private static String receiverConfFile = "rabbitmq.receiver.properties";
	private static String senderConfFile = "rabbitmq.sender.properties";

	/**
	 * @Title: InitAMQP @Description: 建立AMQP链接信息 @param @throws Exception
	 *         设定文件 @return void 返回类型 @throws
	 */
	public static void initRabbitMQReceiver() throws Exception {
		map = getRabbitMQInfo(receiverConfFile);
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

		System.out.println("notice-event-handler Ready to receive messages.");
		recvMessage(channel);

	}

	private static void recvMessage(final Channel channel) throws Exception {
		channel.basicQos(100);
		Consumer consumer = new DefaultConsumer(channel) {
			@SuppressWarnings("rawtypes")
			@Override
			public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties,
					byte[] body) throws IOException {
				String recv = new String(body, "UTF-8");
				channel.basicAck(envelope.getDeliveryTag(), false);
				try {
					// System.out.println("notice-event-handler amqp data : " + recv);
					// 日志记录
					logger.info(recv);
					JSONObject jsonObject = JSONObject.parseObject(recv);
					String macAddr = (String) jsonObject.get("mac");
					System.out.println("现在报警的终端mac为" + macAddr);
					// 调用接口查询mac对应的报警时间、人员、通讯、延迟信息
					List<Map> list = SpringContextUtil.getApplicationContext().getBean(MessageDao.class)
							.synMess(macAddr);
					System.out.println(list);
					// 取延迟时间
					List<Long> lsst = new ArrayList<Long>();
					List<String> glsst = new ArrayList<String>();
					for (Map map : list) {
						if ((null != map.get("delay")) && ("" != map.get("delay"))) {
							lsst.add((Long) map.get("delay"));
							glsst.add((String) map.get("group_name"));
						}
					}
					Set<Long> set = new HashSet<Long>();
					List<Long> dList = new ArrayList<Long>();
					set.addAll(lsst);
					dList.addAll(set);
					System.out.println(dList);
					// 推送
					for (int i = 0; i < dList.size(); i++) {
						int msg = dList.get(i).intValue() / 10;
						// 拼装
						@SuppressWarnings("static-access")
						Map<String, Object> map = jsonObject.parseObject(recv);
						map.put("delay", msg * 10);
						System.out.println(
								"报警发送" + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS").format(new Date().getTime()));
						eventHandlerSender(msg, map.toString());

					}
				} catch (Exception e) {
					System.out.println("notice-event-handler amqp 接收异常" + e.getMessage());
				}
			}
		};
		// Set consumer on this channel.
		channel.basicConsume(map.get("qu_name_uldata").toString(), false, consumer);

	}

	/**
	 * @Title: closeAMQP @Description: 关闭AMQP信息 @param @throws IOException
	 *         设定文件 @return void 返回类型 @throws
	 */
	public static void closeAMQP() throws IOException {
		if (null != channel) {
			channel.close();
		}
		if (null != connection) {
			connection.close();
		}

	}

	/**
	 * @Title: getRabbitMQInfo @Description: TODO(这里用一句话描述这个方法的作用) @param @param
	 * fileName @param @return 设定文件 @return Map<Object,Object> 返回类型 @throws
	 */
	private static Map<Object, Object> getRabbitMQInfo(String fileName) {
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
					logger.info(new Date() + fileName + " AMQP参数如下**********************************\n"
							+ para.toString());
					/*
					 * System.out.println(new Date() +
					 * " AMQP参数如下**********************************\n" + para.toString());
					 * System.out.println(new Date() + " map: " + map);
					 */
					logger.info(new Date() + " map: " + map);
				}
			}
		} catch (IOException e) {
			logger.info("notice-event-handler getInitConnInfo IOException" + e.getMessage());
			e.printStackTrace();
		}
		return map;
	}

	/**
	 * * @Title: jsonToMap @Description: TODO(json转map) @param @param
	 * doc @param @return 设定文件 @return Map<String,Object> 返回类型 @throws
	 */
	public static Map<String, Object> jsonToMap(String doc) {
		Map<String, Object> map = new HashMap<String, Object>();
		if ((null == doc) || ("".equals(doc)) || (doc.length() > 1000)) {
			System.out.println("json doc is not '' or null ,the length must less than 1000");
			return map;
		}
		try {
			Object obj = (Object) JSON.parse(doc);
			if (obj instanceof BasicDBObject) {
				BasicDBObject basicDBObject = (BasicDBObject) obj;
				if (basicDBObject.size() > 0) {
					for (Object object : basicDBObject.keySet()) {
						try {
							String ob = object.toString();
							Object bB = (Object) basicDBObject.get(ob);
							if (bB instanceof BasicDBObject) {
								map.putAll(jsonToMap(bB.toString()));
							} else if (bB instanceof BasicDBList) {
								List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
								for (Object obj2 : (BasicDBList) bB) {
									Map<String, Object> tmp = jsonToMap(obj2.toString());
									if (null != tmp) {
										list.add(tmp);
									}
								}
								if ((null != list) && (list.size() > 0)) {
									map.put(ob, list);
								}
							} else {
								map.put(ob, basicDBObject.get(ob));
							}
						} catch (Exception e) {
							System.out.println("key = " + object + " exception");
							System.out.println(e.getMessage());
						}

					}
				}
			}
			return map;
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		return map;
	}

	/**
	 * * @Title: mapToJson @Description: TODO(map转json) @param @param
	 * map @param @return 设定文件 @return JSONObject 返回类型 @throws
	 */
	public static JSONObject mapToJson(Map<String, Object> map) {
		JSONObject json = new JSONObject();
		Set<String> set = map.keySet();
		for (Iterator<String> it = set.iterator(); it.hasNext();) {
			String key = it.next();
			json.put(key, map.get(key));
		}
		return json;
	}

	/**
	 * * @Title: eventHandlerSender @Description: TODO(数据推送) @param @param
	 * msg @param @param json 设定文件 @return void 返回类型 @throws
	 */
	private static void eventHandlerSender(int msg, String json) {
		try {
			Map<Object, Object> newMap = getRabbitMQInfo(senderConfFile);
			if ((!newMap.containsKey("ulqueue0")) || (!newMap.containsKey("ulqueue10"))
					|| (!newMap.containsKey("ulqueue20")) || (!newMap.containsKey("ulqueue30"))
					|| (!newMap.containsKey("deliverqueue"))) {
				return;
			}
			ConnectionFactory factory = new ConnectionFactory();
			if (newMap.containsKey("user_name")) {
				factory.setUsername(newMap.get("user_name").toString());
			} else {
				System.out.println("not found para :" + "user_name");
				return;
			}
			if (newMap.containsKey("passwd")) {
				factory.setPassword(newMap.get("passwd").toString());
			} else {
				System.out.println("not found para :" + "passwd");
				return;
			}
			if (newMap.containsKey("vhost")) {
				factory.setVirtualHost(newMap.get("vhost").toString());
			} else {
				System.out.println("not found para :" + "vhost");
				return;
			}
			if (newMap.containsKey("host")) {
				factory.setHost(newMap.get("host").toString());
			} else {
				System.out.println("not found para :" + "host");
				return;
			}

			Connection connection = factory.newConnection();
			Channel channel = connection.createChannel();
			HashMap<String, Object> arguments = new HashMap<String, Object>();
			if (newMap.containsKey("dlx_name")) {
				arguments.put("x-dead-letter-exchange", newMap.get("dlx_name").toString());
			} else {
				System.out.println("not found para :" + "dlx_name");
				return;
			}
			if (newMap.containsKey("dlk_name")) {
				arguments.put("x-dead-letter-routing-key", newMap.get("dlk_name").toString());
			} else {
				System.out.println("not found para :" + "dlk_name");
				return;
			}

			switch (msg) {
			case 0: {
				channel.queueDeclare(newMap.get("ulqueue0").toString(), true, false, false, arguments);
				break;
			}
			case 1: {
				channel.queueDeclare(newMap.get("ulqueue10").toString(), true, false, false, arguments);
				break;
			}
			case 2: {
				channel.queueDeclare(newMap.get("ulqueue20").toString(), true, false, false, arguments);
				break;
			}
			case 3: {
				channel.queueDeclare(newMap.get("ulqueue30").toString(), true, false, false, arguments);
				break;
			}
			default:
				break;
			}

			// 声明队列
			channel.queueDeclare(newMap.get("deliverqueue").toString(), true, false, false, null);
			// 绑定路由
			channel.queueBind(newMap.get("deliverqueue").toString(), newMap.get("dlx_name").toString(),
					newMap.get("dlk_name").toString());
			// 设置延时属性
			AMQP.BasicProperties.Builder builder = new AMQP.BasicProperties.Builder();
			// 进行转发
			switch (msg) {
			case 0: {
				// 持久性 non-persistent (1) or persistent (2)
				AMQP.BasicProperties properties = builder.expiration(newMap.get("queue0_delay0").toString())
						.deliveryMode(2).build();
				channel.basicPublish("", newMap.get("ulqueue0").toString(), properties, json.getBytes());
				break;
			}
			case 1: {
				AMQP.BasicProperties properties = builder.expiration(newMap.get("queue10_delay10").toString())
						.deliveryMode(2).build();
				channel.basicPublish("", newMap.get("ulqueue10").toString(), properties, json.getBytes());
				break;
			}
			case 2: {
				AMQP.BasicProperties properties = builder.expiration(newMap.get("queue20_delay20").toString())
						.deliveryMode(2).build();
				channel.basicPublish("", newMap.get("ulqueue20").toString(), properties, json.getBytes());
				break;
			}
			case 3: {
				AMQP.BasicProperties properties = builder.expiration(newMap.get("queue30_delay30").toString())
						.deliveryMode(2).build();
				channel.basicPublish("", newMap.get("ulqueue30").toString(), properties, json.getBytes());
				break;
			}
			default:
				break;
			}
			// 关闭频道和连接
			channel.close();
			connection.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
