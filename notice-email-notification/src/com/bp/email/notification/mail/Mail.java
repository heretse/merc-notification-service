
package com.bp.email.notification.mail;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.mail.Authenticator;
import javax.mail.Message.RecipientType;
import javax.mail.MessagingException;
import javax.mail.NoSuchProviderException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.util.ByteArrayDataSource;

import com.sendgrid.*;

import org.apache.http.HttpHost;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import com.alibaba.fastjson.JSON;
import com.bp.email.notification.utils.ImageUtils;

/**
 * JavaMail 版本: 1.6.0 JDK 版本: JDK 1.7 以上（必须）
 */
public class Mail {
	private static Logger logger = LogManager.getLogger(LogManager.ROOT_LOGGER_NAME);
	public static final String DEFAULT_FORMAT = "yyyy-MM-dd HH:mm:ss";

	private Mail() {
	}

	private static Mail single = null;

	// 静态工厂方法
	public static Mail getInstance() {
		if (single == null) {
			synchronized (Mail.class) {
				if (single == null) {
					single = new Mail();
				}
			}
		}
		return single;
	}

	/**
	 * @param attachs 
	 * @param template 
	 * @param templateFlg 
	 * @param extraMap 
	 * @Title: createMimeMessage @Description: TODO(创建一封只包含文本的简单邮件) @param @param
	 *         user @param @param content @param @return 设定文件 @return String
	 *         返回类型 @throws
	 */
	public static String createMimeMessage(Map<String, String> user, String content, String description, boolean templateFlg, List<Map<String, Object>> template, List<Map<String, Object>> attachs, Map<String, Object> deviceMap) {
		Properties prop = new Properties();
		String delivery_time = new SimpleDateFormat(DEFAULT_FORMAT).format(new Date());

		// 读取属性文件mail.properties
		try {
			InputStream in = Mail.class.getResourceAsStream("/mail.properties");
			BufferedReader bf = new BufferedReader(new InputStreamReader(in, "UTF-8"));
			prop.load(bf); /// 加载属性列表

			String protocol = prop.getProperty("mail.transport.protocol");
			logger.info("mail protocal:"+protocol);
			// smtp setting
			if (protocol.equalsIgnoreCase("smtp")) {
				// 1. 创建参数配置, 用于连接邮件服务器的参数配置
				Properties props = new Properties(); // 参数配置
				
				props.setProperty("mail.transport.protocol", prop.getProperty("mail.transport.protocol")); // 使用的协议（JavaMail规范要求）
				props.setProperty("mail.smtp.host", prop.getProperty("mail.smtp.host")); // 发件人的邮箱的 SMTP 服务器地址
				props.setProperty("mail.smtp.auth", "true"); // 需要请求认证
				props.setProperty("mail.smtp.port", prop.getProperty("mail.port"));
				props.setProperty("mail.smtp.socketFactory.fallback", "false");
				props.setProperty("mail.smtp.socketFactory.port", prop.getProperty("mail.port"));

				// PS: 某些邮箱服务器要求 SMTP 连接需要使用 SSL 安全认证 (为了提高安全性, 邮箱支持SSL连接, 也可以自己开启),
				// 如果无法连接邮件服务器, 仔细查看控制台打印的 log, 如果有有类似 “连接失败, 要求 SSL 安全连接” 等错误,
				// 打开下面 /* ... */ 之间的注释代码, 开启 SSL 安全连接。
				/*
				 * // SMTP 服务器的端口 (非 SSL 连接的端口一般默认为 25, 可以不添加, 如果开启了 SSL 连接, // 需要改为对应邮箱的 SMTP
				 * 服务器的端口, 具体可查看对应邮箱服务的帮助, // QQ邮箱的SMTP(SLL)端口为465或587, 其他邮箱自行去查看) final String
				 */
				// final String smtpPort = "465";
				// props.setProperty("mail.smtp.port", smtpPort);
				// props.setProperty("mail.smtp.socketFactory.class",
				// "javax.net.ssl.SSLSocketFactory");
				// props.setProperty("mail.smtp.socketFactory.fallback", "false");
				// props.setProperty("mail.smtp.socketFactory.port", smtpPort);
				// 2. 根据配置创建会话对象, 用于和邮件服务器交互
				Session session;
				if(prop.getProperty("mail.smtp.host").indexOf("gmail") > 0) {
					props.setProperty("mail.smtp.starttls.enable", "true");
					props.setProperty("mail.from", prop.getProperty("mail.username"));
					final PasswordAuthentication usernamePassword = new PasswordAuthentication(prop.getProperty("mail.username"), prop.getProperty("mail.password"));
					Authenticator auth = new Authenticator() {
						    protected PasswordAuthentication getPasswordAuthentication() {
						      return usernamePassword;
						    }
						  };
					session = Session.getInstance(props, auth);
				} else {
					props.setProperty("mail.smtp.socketFactory.class", prop.getProperty("mail.ssl.factory"));
					session = Session.getInstance(props);	
				}
				session.setDebug(true); // 设置为debug模式, 可以查看详细的发送 log
				// 3. 创建一封邮件
				MimeMessage message = new MimeMessage(session);
				// 4. From: 发件人
				message.setFrom(new InternetAddress(prop.getProperty("mail.username"),
						prop.getProperty("mail.filetitle"), "UTF-8"));

				// 5. To: 收件人（可以增加多个收件人、抄送、密送）
				if (user.size() < 50) {
					int flag = 0;
					if (!user.isEmpty()) {
						for (String key : user.keySet()) {
							flag += 1;
							if (flag == 1) {
								message.setRecipient(RecipientType.TO,
										new InternetAddress(key, key, "UTF-8"));
							} else {
								// To: 增加收件人（可选）
								message.addRecipient(RecipientType.TO,
										new InternetAddress(key, key, "UTF-8"));
							}
						}
						String subject = prop.getProperty("mail.subject");
						if(templateFlg) {
							MimeMultipart templateContent = new MimeMultipart();
							// get attachment
							logger.info("attachs.size:"+attachs.size());
							for(int i = 0 ; i < attachs.size() ; i++) {
								MimeBodyPart imagePart = new MimeBodyPart();
								logger.info("url:"+attachs.get(i).get("src").toString());
								logger.info("cid:"+attachs.get(i).get("cid").toString());
								DataSource ds;
							    ds = new ByteArrayDataSource(ImageUtils.decodeImageFromUrl(attachs.get(i).get("src").toString()), "image/*");
							    imagePart.setDataHandler(new DataHandler(ds));
							    imagePart.setFileName(attachs.get(i).get("cid").toString()+".png");
								imagePart.setHeader("Content-ID", "<" + attachs.get(i).get("cid").toString() + ">");
								imagePart.setContentID("<" + attachs.get(i).get("cid").toString() + ">");
								imagePart.setDisposition(MimeBodyPart.INLINE);
								templateContent.addBodyPart(imagePart);
							}
							
							MimeBodyPart textPart = new MimeBodyPart();
							content = replacePara(template.get(0).get("content").toString(), deviceMap);
							textPart.setText(content, "UTF-8", "html");
							templateContent.addBodyPart(textPart);
							subject = template.get(0).get("subject").toString();
							message.setContent(templateContent);
							
						} else {
							// 6. Content: 邮件正文（可以使用html标签）
							message.setContent(content, "text/html;charset=UTF-8");	
						}
						// 7. Subject: 邮件主题
						message.setSubject(subject, "UTF-8");
						// 8. 设置发件时间
						message.setSentDate(new Date());
						// 9. 保存设置
						message.saveChanges();
						// 10. 根据 Session 获取邮件传输对象
						Transport transport = session.getTransport();

						// 11. 使用 邮箱账号 和 密码 连接邮件服务器, 这里认证的邮箱必须与 message 中的发件人邮箱一致, 否则报错
						// PS_01: 成败的判断关键在此一句, 如果连接服务器失败, 都会在控制台输出相应失败原因的 log,
						// 仔细查看失败原因, 有些邮箱服务器会返回错误码或查看错误类型的 链接, 根据给出的错误
						// 类型到对应邮件服务器的帮助网站上查看具体失败原因。
						//
						// PS_02: 连接失败的原因通常为以下几点, 仔细检查代码:
						// (1) 邮箱没有开启 SMTP 服务;
						// (2) 邮箱密码错误, 例如某些邮箱开启了独立密码;
						// (3) 邮箱服务器要求必须要使用 SSL 安全连接;
						// (4) 请求过于频繁或其他原因, 被邮件服务器拒绝服务;
						// (5) 如果以上几点都确定无误, 到邮件服务器网站查找帮助。
						//
						// PS_03: 仔细看log, 认真看log, 看懂log, 错误原因都在log已说明。
						transport.connect(prop.getProperty("mail.username"), prop.getProperty("mail.password"));
						// 13. 发送邮件, 发到所有的收件地址, message.getAllRecipients() 获取到的是在创建邮件对象时添加的所有收件人, 抄送人,
						// 密送人
						delivery_time = new SimpleDateFormat(DEFAULT_FORMAT).format(new Date());
						transport.sendMessage(message, message.getAllRecipients());
						logger.info("邮件发送成功");
						// 14. 关闭连接
						transport.close();
					}
				} else {
					logger.info("由于邮件推送人数过多，无法推送");
				}
				in.close();
			}
			// send grid mode
			else {
				SendGrid sg = null;
				if (Boolean.getBoolean(prop.getProperty("isProxy"))) {
				  //设置代理IP、端口
				  HttpHost proxy = new HttpHost(prop.getProperty("PROXY_HOST"), Integer.parseInt(prop.getProperty("PROXY_PORT")));
		      	  //把代理设置到请求配置
			      RequestConfig defaultRequestConfig = RequestConfig.custom().setProxy(proxy).build();
			        //实例化CloseableHttpClient对象
			      CloseableHttpClient httpclient = HttpClients.custom().setDefaultRequestConfig(defaultRequestConfig).build();
			      Client client = new Client(httpclient);
			      sg = new SendGrid(prop.getProperty("mail.sendgrid.api.key"), client);
				} else {
				  sg = new SendGrid(prop.getProperty("mail.sendgrid.api.key"));	
				}
				String receiver = "";
				String subject = prop.getProperty("mail.subject");
				int keyCnt = 0;
				for (String key : user.keySet()) {
					logger.info("user key:"+key);
					logger.info("user val:"+user.get(key).toString());
					receiver = receiver.concat("{\"email\":\"" + key + "\"}");
					if(keyCnt < user.size() -1) {
					  receiver = receiver.concat(",");	
					}
					keyCnt++;
				}
				String attchObj = "";				
				if(templateFlg) {
				  // get attachment
				  logger.info("attachs.size:"+attachs.size());
				  for(int i = 0 ; i < attachs.size() ; i++) {
					  logger.info("url:"+attachs.get(i).get("src").toString());
					  logger.info("cid:"+attachs.get(i).get("cid").toString());
					  String attachEncode = ImageUtils.encodeImageFromUrl(attachs.get(i).get("src").toString());
					  attchObj = attchObj.concat("{\"content\":\"" + attachEncode + "\",\"filename\":\"" + attachs.get(i).get("cid").toString() + "\",\"content_id\":\"" + attachs.get(i).get("cid").toString() + "\", \"disposition\":\"inline\"}");
						if(i < attachs.size() -1) {
							attchObj = attchObj.concat(",");	
						}
				  }
				  
				  content = replacePara(template.get(0).get("content").toString(), deviceMap);
				  subject = template.get(0).get("subject").toString();
				  logger.info("content:"+content);
				  logger.info("subject:"+subject);
				}
				logger.info("attchObj:"+attchObj);
				String body = "{\"personalizations\":[{\"to\":[" + receiver + "],\"subject\":\""+subject+"\"}],\"from\":{\"email\":\""+prop.getProperty("mail.sendgrid.from")+"\"},\"content\":[{\"type\":\"text/html\",\"value\": \""+content+"\"}]}";
				if(!attchObj.isEmpty()){
				  body = "{\"personalizations\":[{\"to\":[" + receiver + "],\"subject\":\""+subject+"\"}],\"from\":{\"email\":\""+prop.getProperty("mail.sendgrid.from")+"\"},\"content\":[{\"type\":\"text/html\",\"value\": \""+content+"\"}],\"attachments\":["+attchObj+"]}";
				}
				logger.info("body:"+body);
				Request request = new Request();
				request.setMethod(Method.POST);
				request.setEndpoint("mail/send");
				request.setBody(body);
				Response response = sg.api(request);
				logger.info("sendgrid statusCode:"+response.getStatusCode());
				logger.info("sendgrid body:"+response.getBody());

			}
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			logger.info("不支持的编码异常");
		} catch (NoSuchProviderException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			logger.info("Provider异常");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			logger.info("读取mail.properties文件异常");
		} catch (MessagingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			logger.info("邮件发送通信异常");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			logger.info("附件解析失敗");
		}
		return delivery_time;
	}
	private static String replacePara(String content, Map<String, Object> deviceMap) {
		if(!deviceMap.isEmpty()) {
		  int stPos = 0;
		  int edPos = 0;
		  String subStr = "";
		  String replaceWord = "";
		  while(content.indexOf("{{") != -1 && content.indexOf("}}") != -1) {
	        stPos = content.indexOf("{{");
	        edPos = content.indexOf("}}");
	        subStr = content.substring(stPos+2, edPos);
	    	logger.info("subStr:" + subStr);
	        if(subStr.indexOf(":") != -1) {
	          String[] deviceReplace = subStr.split(":");
	          if(deviceReplace.length == 2) {
	        	logger.info("mac:" + deviceMap.get(deviceReplace[0]));
	            replaceWord = getVal(deviceReplace[1], deviceMap.get(deviceReplace[0]));    
	          }
	        }
	        content = content.replace("{{"+subStr+"}}", replaceWord);
		  }
		}
		return content;
	}

	private static String getVal(String subStr, Object object) {
		String result = "";
		logger.info("macObj" +object.toString());
		Map tempMap = JSON.parseObject(object.toString(), Map.class);
		if(subStr.indexOf(".") > -1) {
		  String[] valueArray = subStr.split(".");
		  for(int i = 0 ; i < valueArray.length ; i++) {
		    if(i < valueArray.length -1){
		    	tempMap = JSON.parseObject(tempMap.get(valueArray[i]).toString(), Map.class);
		    }else{
		    	result = tempMap.get(valueArray[i]).toString();
		    }
		  }
		}else {
		  result = tempMap.get(subStr).toString();
		}
		return result;
	}
}
