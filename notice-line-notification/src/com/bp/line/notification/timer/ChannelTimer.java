
package com.bp.line.notification.timer;

import java.util.Timer;
import java.util.TimerTask;

import com.bp.line.notification.utils.RabbitMQUtils;

public class ChannelTimer
{
    private static ChannelTimer singleton = null;
    final Timer timer = new Timer();

    private ChannelTimer()
    {
    }

    /**
     * @Title: getInstance
     * @Description: TODO(创建定时器单例对象)
     * @param @return 设定文件
     * @return ChannelTimer 返回类型
     * @throws
     */
    public static ChannelTimer getInstance()
    {
        if (singleton == null)
        {
            synchronized (ChannelTimer.class)
            {
                if (singleton == null)
                {    //这里进行第二次判断，不能省略，否则会线程不安全。  
                    singleton = new ChannelTimer();
                }
            }
        }
        return singleton;
    }

    /**
     * @Title: startTimer
     * @Description: TODO(启动定时器)
     * @param 设定文件
     * @return void 返回类型
     * @throws
     */
    public void startTimer()
    {
        timer.schedule(new TimerTask()
        {
            @Override
            public void run()
            {
                if (!RabbitMQUtils.channel.isOpen())
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
        }, 3 * 60 * 1000, 1 * 60 * 1000);
    }

    /**
     * @Title: closeTimer
     * @Description: TODO(关闭定时器)
     * @param 设定文件
     * @return void 返回类型
     * @throws
     */
    public void closeTimer()
    {
        timer.cancel();
    }

}
