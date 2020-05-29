/**
 * @FileName: DateUtils.java
 * @PackageName com.bp.notice.business.utils
 * @Description: TODO(用一句话描述该文件做什么)
 * @author MT
 * @date 2018年1月1日 下午6:42:54
 * @version
 */

package com.bp.notice.business.utils;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;

/**
 * @ClassName: DateUtils
 * @Description: 日期工具类
 * @author MT
 * @date 2018年1月1日 下午6:42:54
 */
public class DateUtils
{
    /**
     * @Title: convertDate
     * @Description: Long 类型转换成 yyyy-MM-dd HH:mm:ss 方便数据库查询匹配
     * @param @param time
     * @param @return 设定文件
     * @return String 返回类型
     * @throws
     */
    public static String convertDate(Object time)
    {
        if (time == null)
        {

            return null;
        }

        Long now1 = Long.valueOf(String.valueOf(time));
        Timestamp now = new Timestamp(now1);
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String str = df.format(now);
        return str;
    }

}
