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

import org.apache.ibatis.annotations.Select;

/**
 * @ClassName: GroupTerminalDao
 * @Description: 终端组服务类
 * @author MT
 * @date 2018年1月28日 下午3:03:45
 */
public interface GroupTerminalDao
{

    public List<Map<String, Object>> queryGroupTerminal(Map<String, Object> param);

    public Long countGroupTerminal(Map<String, Object> param);

    /**
     * @Title: queryGroupByPID
     * @Description: 根据终端组ID查询对应的群组关系(多对多的关系)
     * @param @param param
     * @param @return 设定文件
     * @return List<Map<String,Object>> 返回类型
     * @throws
     */
    public List<Map<String, Object>> queryGroupBygtID(Long gtID);

    /**
     * @Title: queryContactByGID
     * @Description: 根据通知组ID查询对应联系(多对多的关系)，此接口和通报组查询联系人公用
     * @param @param param
     * @param @return 设定文件
     * @return List<Map<String,Object>> 返回类型
     * @throws
     */
    public List<Map<String, Object>> queryContactByGID(Map<String, Object> param);

    /**
     * @Title: queryContactByGID
     * @Description: 根据通知组ID查询对应联系(多对多的关系)，此接口和通报组查询联系人公用
     * @param @param param
     * @param @return 设定文件
     * @return List<Map<String,Object>> 返回类型
     * @throws
     */
    public Long countContactByGID(Map<String, Object> param);

    /**
     * @Title: queryContactByGID
     * @Description: 根据通知组ID查询绑定的终端列表
     * @param @param param
     * @param @return 设定文件
     * @return List<Map<String,Object>> 返回类型
     * @throws
     */
    public List<Map<String, Object>> queryTerminalBygtID(Map<String, Object> param);

    /**
     * @Title: queryContactByGID
     * @Description: 根据通知组ID查询绑定的终端数目
     * @param @param param
     * @param @return 设定文件
     * @return List<Map<String,Object>> 返回类型
     * @throws
     */
    public Long countTerminalBygtID(Map<String, Object> param);

    /**
     * @Title: unBindMacs
     * @Description: 查询未绑定和剔除已经绑定的终端
     * @param @param param
     * @param @return 设定文件
     * @return List<Map<String,Object>> 返回类型
     * @throws
     */
    public List<Map<String, Object>> unBindMacs(Map<String, Object> param);

    /**
     * @Title: bind
     * @Description: 终端组和终端mac开始绑定
     * @param @param param
     * @param @return 设定文件
     * @return Long 返回类型
     * @throws
     */
    public Long bind(Long gtID, String mac);

    /**
     * @Title: addGroupTerminal
     * @Description: 新增终端组
     * @param @param param
     * @param @return 设定文件
     * @return Long 返回类型
     * @throws
     */
    public Long addGroupTerminal(Map<String, Object> param);

    /**
     * @Title: editGroupTerminal
     * @Description: 修改终端组（修改终端组的名称和备注信息）
     * @param @param param
     * @param @return 设定文件
     * @return Long 返回类型
     * @throws
     */
    public Long editGroupTerminal(Map<String, Object> param);

    /**
     * @Title: deleteGroupTerminal
     * @Description: 删除终端组
     * @param @param gtID
     * @param @return 设定文件
     * @return Long 返回类型
     * @throws
     */
    public Long deleteGroupTerminal(Object gtID);

    /**
     * @Title: deleteBind
     * @Description: 根据终端组ID将终端表关联的终端组ID置为null
     * @param @param gtID
     * @param @return 设定文件
     * @return Long 返回类型
     * @throws
     */
    public Long deleteBind(Object gtID);

    /**
     * @Title: deleteGroupRelation
     * @Description: 根据终端组ID清除终端组和通报组之间的关系
     * @param @param gtID
     * @param @return 设定文件
     * @return Long 返回类型
     * @throws
     */
    public Long deleteGroupRelation(Object gtID);
    
    
    @Select("SELECT * FROM terminal_group WHERE group_name=#{gtname}")
    public Map<String,Object> getGroupTerminalByName(String gtname);
    
    @Select("SELECT * FROM terminal_group WHERE id=#{id}")
    public Map<String,Object> getGroupTerminalById(Object id);
}
