/**
 * @FileName: SpringContextUtil.java
 * @PackageName com.force4us.utils
 * @Description: TODO(用一句话描述该文件做什么)
 * @author MT
 * @date 2018年1月23日 上午9:58:37
 * @version
 */

package com.bp.notice.business.utils;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * @ClassName: SpringContextUtil
 * @Description: TODO(这里用一句话描述这个类的作用)
 * @author MT
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
