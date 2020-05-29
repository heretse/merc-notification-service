/**
 * @FileName: UserLoginDao.java
 * @PackageName com.bp.business.dao
 * @Description: TODO(用一句话描述该文件做什么)
 * @author MT
 * @date 2018年1月23日 下午5:20:37
 * @version
 */

package com.bp.notice.business.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Select;

import com.bp.notice.business.model.LoginModel;

/**
 * @ClassName: UserDao
 * @Description: TODO(这里用一句话描述这个类的作用)
 * @author MT
 * @date 2018年1月23日 下午5:20:37
 */
public interface UserDao
{

    public List<LoginModel> login(String uname, String pwd);

    public List<Map<String, Object>> queryByName(String uname);

    public List<Map<String, Object>> queryAccount(Map<String, Object> param);

    public List<Map<String, Object>> getAccountByToken(String token);

    public Long countAccount(Map<String, Object> param);

    public Long addAccount(Map<String, Object> param);

    public Long editAccount(Map<String, Object> param);

    public Long deleteAccount(Object uid);

    public Long updateTokenByuname(Map<String, Object> param);
    @Select("select * from user where id=#{id}")
    public Map<String,Object> getUserById(Object id);
}
