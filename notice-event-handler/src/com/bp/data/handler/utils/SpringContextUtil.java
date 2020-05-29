
package com.bp.data.handler.utils;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * @ClassName: SpringContextUtil
 * @Description: TODO(让Bean获取它所在的Spring容器)
 * @author pyt
 * @date 2018年1月23日 上午9:58:37
 */
public class SpringContextUtil implements ApplicationContextAware
{

    private static ApplicationContext applicationContext = null;

    /**
     * (非 Javadoc)
     * <p>
     * Title: setApplicationContext
     * </p>
     * <p>
     * Description:
     * </p>
     * 
     * @param arg0
     * @throws BeansException
     * @see org.springframework.context.ApplicationContextAware#setApplicationContext(org.springframework.context.ApplicationContext)
     */
    @SuppressWarnings("static-access")
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException
    {

        this.applicationContext = applicationContext;

    }

    public static ApplicationContext getApplicationContext()
    {
        return applicationContext;
    }

    public static <T> T getBean(Class<T> c)
    {
        return applicationContext.getBean(c);
    }

}
