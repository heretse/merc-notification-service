/**
 * @FileName: AppInitStartListener.java
 * @PackageName com.bp.line.notification.amqp
 * @Description: TODO(用一句话描述该文件做什么)
 * @author MT
 * @date 2018年1月24日 上午10:09:25
 * @version
 */

package com.bp.line.notification.amqp;

import java.io.IOException;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import com.bp.line.notification.utils.RabbitMQUtils;
import com.bp.line.notification.utils.LineStarter;

/**
 * @ClassName: AmqpInitStartListener
 * @Description: 初始化监听器
 * @author MT
 * @date 2018年1月24日 上午10:09:25
 */
public class AmqpInitStartListener implements ServletContextListener
{

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
    public void contextDestroyed(ServletContextEvent arg0)
    {

        try
        {

            RabbitMQUtils.closeAMQP();
        }
        catch (IOException e)
        {
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
     * Description: 项目启动建立amqp链接
     * </p>
     * 
     * @param arg0
     * @see javax.servlet.ServletContextListener#contextInitialized(javax.servlet.ServletContextEvent)
     */
    @Override
    public void contextInitialized(ServletContextEvent arg0)
    {

        try
        {
            RabbitMQUtils.InitAMQP();
            LineStarter.initLine();
        }
        catch (Exception e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

}
