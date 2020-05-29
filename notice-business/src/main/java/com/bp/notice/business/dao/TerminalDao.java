/**
 * @FileName: TerminalDao.java
 * @PackageName com.bp.business.dao
 * @Description: TODO(用一句话描述该文件做什么)
 * @author MT
 * @date 2018年1月28日 下午3:03:45
 * @version
 */

package com.bp.notice.business.dao;

import java.util.List;

import java.util.Map;

import com.bp.notice.business.entity.Terminal;

/**
 * @ClassName: TerminalDao
 * @Description: TODO(这里用一句话描述这个类的作用)
 * @author MT
 * @date 2018年1月28日 下午3:03:45
 */
public interface TerminalDao
{

    public Long addTerminal(Terminal terminal);

    public Long deteTerminal(Long id);

    public List<Map<String, Object>> queryTerminal(Map<String, Object> param);

    public Long countTerminal(Map<String, Object> param);

    public Long updateTerminal(Map<String, Object> param);

    /**
     * @Title: insertGroup
     * @Description: TODO(这里用一句话描述这个方法的作用)
     * @param @param param
     * @param @return 设定文件
     * @return Long 返回类型
     * @throws
     */
    public Long insertGroup(Map<String, Object> param);

}
