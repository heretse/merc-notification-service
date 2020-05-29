
package com.bp.wechat.notification.timer;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

public class TimerInitStartListener implements ServletContextListener
{

    /**
     * (非 Javadoc)
     * <p>
     * Title: contextDestroyed
     * </p>
     * <p>
     * 项目终止销毁定时器
     * </p>
     * 
     * @param arg0
     * @see javax.servlet.ServletContextListener#contextDestroyed(javax.servlet.ServletContextEvent)
     */
    @Override
    public void contextDestroyed(ServletContextEvent arg0)
    {
        ChannelTimer.getInstance().closeTimer();
        SendTimer.getInstance().closeTimer();
    }

    /**
     * (非 Javadoc)
     * <p>
     * Title: contextInitialized
     * </p>
     * <p>
     * Description: 项目启动启动定时器
     * </p>
     * 
     * @param arg0
     * @see javax.servlet.ServletContextListener#contextInitialized(javax.servlet.ServletContextEvent)
     */
    @Override
    public void contextInitialized(ServletContextEvent arg0)
    {
        ChannelTimer.getInstance().startTimer();
        SendTimer.getInstance().startTimer();
    }

}
