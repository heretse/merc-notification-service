/**
 * @FileName: ContactDao.java
 * @PackageName com.bp.notice.business.dao
 * @Description: TODO(用一句话描述该文件做什么)
 * @author MT
 * @date 2018年2月5日 下午3:12:15
 * @version
 */

package com.bp.notice.business.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * @ClassName: ContactDao
 * @Description: 联系人数据库操作
 * @author MT
 * @date 2018年2月5日 下午3:12:15
 */
public interface ContactDao
{

    public List<Map<String, Object>> queryMethods();

    public String getMethodByid(Object id);

    public List<Map<String, Object>> query(Map<String, Object> maps);

    /**
     * @Title: queryMethodsByCID
     * @Description: 根据联系人ID查询具体某个联系人的联系方式(编辑的时候选中对应的选项)
     * @param @param cid
     * @param @return 设定文件
     * @return List<Map<String,Object>> 返回类型
     * @throws
     */
    public List<Map<String, Object>> queryMethodsByCID(Object cid);

    public Long count(Map<String, Object> maps);

    /**
     * @Title: add
     * @Description: 新增联系人
     * @param @param maps
     * @param @return 设定文件
     * @return Long 返回类型
     * @throws
     */
    public Long add(Map<String, Object> maps);

    /**
     * @Title: insertRelaiton
     * @Description: 新增联系人关系(批量)
     * @param @param maps
     * @param @return 设定文件
     * @return Long 返回类型
     * @throws
     */
    public Long insertRelaiton(List<Map<String, Object>> params);

    public Long edit(Map<String, Object> params);

    /**
     * @Title: deleteRelationByCID
     * @Description: 根据联系人ID清除 关系表记录（修改联系方式的时候情况和删除联系人的时候清空此表）
     * @param @param cid
     * @param @return 设定文件
     * @return Long 返回类型
     * @throws
     */
    public Long deleteRelationByCID(Object cid);

    /**
     * @Title: delete
     * @Description: 删除联系人
     * @param @param cid
     * @param @return 设定文件
     * @return Long 返回类型
     * @throws
     */
    public Long delete(Object cid);

    /**
     * @Title: deleteGroupRelation
     * @Description: 根据联系人ID清除通报组和联系人关系表
     * @param @param cid
     * @param @return 设定文件
     * @return Long 返回类型
     * @throws
     */
    public Long deleteGroupRelation(Object cid);
    
    /**
     * 
     * @Title: checkIsExistAccount
     * @Description: 根据账号检测唯一性（账号邮箱邮箱和微信企业ID）
     * @param @param account
     * @param @param method_id
     * @param @return    设定文件
     * @return Map<String,Object>    返回类型
     * @throws
     */
    @Select("SELECT * FROM object_method_relation WHERE notification_account =#{notification_account} and notification_method_id = #{notification_method_id}")
    public Map<String, Object> checkIsExistAccount(@Param("notification_account") String account, @Param("notification_method_id") Integer method_id);
    
}
