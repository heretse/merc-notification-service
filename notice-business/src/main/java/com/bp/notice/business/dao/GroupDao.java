/**
 * @FileName: GroupDao.java
 * @PackageName com.bp.notice.business.dao
 * @Description: TODO(用一句话描述该文件做什么)
 * @author MT
 * @date 2018年2月6日 下午2:01:14
 * @version
 */

package com.bp.notice.business.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Select;

/**
 * @ClassName: GroupDao
 * @Description: TODO(这里用一句话描述这个类的作用)
 * @author MT
 * @date 2018年2月6日 下午2:01:14
 */
public interface GroupDao
{
    /**
     * @Title: query
     * @Description: 通报列表显示
     * @param @param maps
     * @param @return 设定文件
     * @return List<Map<String,Object>> 返回类型
     * @throws
     */
    public List<Map<String, Object>> query(Map<String, Object> maps);

    /**
     * @Title: count
     * @Description:通报列表总条数
     * @param @param maps
     * @param @return 设定文件
     * @return Long 返回类型
     * @throws
     */
    public Long count(Map<String, Object> maps);

    /**
     * @Title: query
     * @Description: 根据通报组ID查询所关联的终端组
     * @param @param maps
     * @param @return 设定文件
     * @return List<Map<String,Object>> 返回类型
     * @throws
     */
    public List<Map<String, Object>> queryGTByID(Map<String, Object> maps);

    /**
     * @Title: query
     * @Description: 根据通报组ID查询未关联的终端组
     * @param @param maps
     * @param @return 设定文件
     * @return List<Map<String,Object>> 返回类型
     * @throws
     */
    public List<Map<String, Object>> queryUNGTByID(Map<String, Object> maps);

    /**
     * @Title: query
     * @Description: 根据通报组ID查询未关联的联系人
     * @param @param maps
     * @param @return 设定文件
     * @return List<Map<String,Object>> 返回类型
     * @throws
     */
    public List<Map<String, Object>> queryUNBindContactsByID(Map<String, Object> maps);

    /**
     * @Title: count
     * @Description:根据通报组ID查询所关联的终端组
     * @param @param maps
     * @param @return 设定文件
     * @return Long 返回类型
     * @throws
     */
    public Long countqueryGTByID(Map<String, Object> maps);

    /**
     * @Title: deleteGroupTerminalRelelation
     * @Description: 通报组绑定终端组的时候先要清空关联表的关系
     * @param @param gid 通报组ID
     * @param @return 设定文件
     * @return Long 返回类型
     * @throws
     */
    public Long deleteGroupTerminalRelelation(Object gid);

    /**
     * @Title: insertGroupTerminalRelelation
     * @Description: 维护通报组和终端组的关系
     * @param @param gid
     * @param @param gtid
     * @param @return 设定文件
     * @return Long 返回类型
     * @throws
     */
    public Long insertGroupTerminalRelelation(Object gid, Object gtid);

    /**
     * @Title: deleteGroupTerminalRelelation
     * @Description: 通报组绑定联系人的时候先要清空关联表的关系
     * @param @param gid 通报组ID
     * @param @return 设定文件
     * @return Long 返回类型
     * @throws
     */
    public Long deleteContactRelelation(Object gid);

    /**
     * @Title: insertGroupTerminalRelelation
     * @Description: 维护通报组和联系人的关系
     * @param @param gid
     * @param @param gtid
     * @param @return 设定文件
     * @return Long 返回类型
     * @throws
     */
    public Long insertContactRelelation(Object gid, Object cid);

    /**
     * @Title: add
     * @Description: 新增通报组
     * @param @param params
     * @param @return 设定文件
     * @return Long 返回类型
     * @throws
     */
    public Long add(Map<String, Object> params);

    /**
     * @Title: edit
     * @Description: 编辑通报组
     * @param @param params
     * @param @return 设定文件
     * @return Long 返回类型
     * @throws
     */
    public Long edit(Map<String, Object> params);

    /**
     * @Title: delete
     * @Description: 删除通报组
     * @param @param params
     * @param @return 设定文件
     * @return Long 返回类型
     * @throws
     */
    public Long delete(Object gid);
    @Select("SELECT * FROM notification_group WHERE group_name=#{gname}")
    public Map<String,Object> getGroupByName(String gname);
    @Select("SELECT * FROM notification_group WHERE id=#{id}")
    public Map<String,Object> getGroupById(Object id);
}
