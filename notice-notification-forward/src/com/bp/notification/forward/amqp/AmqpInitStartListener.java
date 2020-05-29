/**
 * @FileName: AppInitStartListener.java
 * @PackageName com.bp.data.app.init
 * @Description: TODO(用一句话描述该文件做什么)
 * @author MT
 * @date 2018年1月24日 上午10:09:25
 * @version
 */

package com.bp.notification.forward.amqp;

import java.io.IOException;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import com.bp.notification.forward.utils.RabbitMQUtils;

/**
 * @ClassName: AmqpInitStartListener
 * @Description: TODO(初始化监听器)
 * @date 2018年2月23日 下午4:01:55
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
        }
        catch (Exception e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

}
