
package com.bp.data.handler.dao;

import java.util.List;
import java.util.Map;

/**
 * @ClassName: MessageDao
 * @Description: TODO(通报消息体)
 * @author pyt
 * @date 2018年2月1日 上午10:22:26
 */
public interface MessageDao
{
    /**
     * *
     * @Title: synMess
     * @Description: TODO(根据mac获取延迟信息)
     * @param @param mac
     * @param @return 设定文件
     * @return List<Map> 返回类型
     * @throws
     */
    @SuppressWarnings("rawtypes")
    public List<Map> synMess(String mac);

    /**
     * *
     * @Title: synDelay
     * @Description: TODO(根据通报组获取延迟时间)
     * @param @param ngroup 通报组名称
     * @param @return 设定文件
     * @return long 返回类型
     * @throws
     */
    public long synDelay(String ngroup);
}
