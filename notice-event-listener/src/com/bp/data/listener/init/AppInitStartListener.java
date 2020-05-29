package com.bp.data.listener.init;

import java.io.IOException;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.bp.data.listener.utils.RabbitMQUtils;

/**
 * *
 * 
 * @ClassName: AppInitStartListener
 * @Description: TODO(监听)
 * @author Administrator
 * @date 2018年2月5日 上午11:21:36
 */
public class AppInitStartListener implements ServletContextListener {

	private static final Logger logger = LogManager.getLogger(LogManager.ROOT_LOGGER_NAME);

	/**
	 * (非 Javadoc)
	 * <p>
	 * Title: contextDestroyed
	 * </p>
	 * <p>
	 * 项目终止销毁amqp链接
	 * </p>
	 * 
	 * @param arg0
	 * @see javax.servlet.ServletContextListener#contextDestroyed(javax.servlet.ServletContextEvent)
	 */
	@Override
	public void contextDestroyed(ServletContextEvent arg0) {

		logger.error("the application is closed");
		try {

			RabbitMQUtils.closeAMQP();

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	/**
	 * (非 Javadoc)
	 * <p>
	 * Title: contextInitialized
	 * </p>
	 * <p>
	 * 项目启动建立amqp链接
	 * </p>
	 * 
	 * @param arg0
	 * @see javax.servlet.ServletContextListener#contextInitialized(javax.servlet.ServletContextEvent)
	 */
	@Override
	public void contextInitialized(ServletContextEvent arg0) {

		try {
			RabbitMQUtils.InitAMQP();

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
