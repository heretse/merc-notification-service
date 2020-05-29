/**
 * @FileName: UserServiceImpl.java
 * @PackageName com.bp.business.service.impl
 * @Description: TODO(用一句话描述该文件做什么)
 * @author MT
 * @date 2018年1月23日 下午5:34:07
 * @version
 */

package com.bp.notice.business.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.bp.notice.business.dao.UserDao;
import com.bp.notice.business.model.LoginModel;
import com.bp.notice.business.service.UserService;
import com.bp.notice.business.utils.QueryParam;
import com.bp.notice.business.utils.ResultModel;

/**
 * @ClassName: UserServiceImpl
 * @Description: 用户操作类（包含登录）
 * @author MT
 * @date 2018年1月23日 下午5:34:07
 */

@Service
public class UserServiceImpl implements UserService
{
    private Logger logger = LogManager.getLogger(LogManager.ROOT_LOGGER_NAME);
    @Autowired
    private UserDao userDao;

    /**
     * (非 Javadoc)
     * <p>
     * Title: login
     * </p>
     * <p>
     * Description:
     * </p>
     * 
     * @param uname
     *            用户名
     * @param pwd
     *            密码
     * @return
     * @see com.bp.notice.business.service.UserService#login(java.lang.String,
     *      java.lang.String)
     */
    @Override
    public ResultModel login(String uname, String pwd)
    {

        if (uname == null || "".equals(uname))
        {

            return ResultModel.fail(ResultModel.CODE_PARAM_MISSING, ResultModel.CODE_PARAM_MISSING_DESC);
        }

        if (pwd == null && "".equals(pwd))
        {

            return ResultModel.fail(ResultModel.CODE_PARAM_MISSING, ResultModel.CODE_PARAM_MISSING_DESC);
        }

        List<LoginModel> loginUser = userDao.login(uname, pwd);
        System.out.println(loginUser);
        if (loginUser != null && loginUser.size() == 0)
        {
            return ResultModel.fail(ResultModel.CODE_SYSTEM_ERROR, "login fail");
        }

        return ResultModel.success(loginUser.size(), loginUser);
    }

    /**
     * 用户列表展示页面
     * <p>
     * Title: queryAccounts
     * </p>
     * <p>
     * Description:
     * </p>
     * 
     * @param param
     * @return
     * @see com.bp.notice.business.service.UserService#queryAccounts(java.lang.String)
     */
    @Override
    public ResultModel queryAccounts(String param)
    {

        if (param == null)
        {
            return ResultModel.fail(ResultModel.CODE_PARAM_MISSING, ResultModel.CODE_PARAM_MISSING_DESC);

        }

        QueryParam queryParam = JSON.parseObject(param, QueryParam.class);

        Map<String, Object> pmaps = new HashMap<>();
        pmaps.put("uname", queryParam.getFilter().get("uname"));
        pmaps.put("page", queryParam.getPage());
        pmaps.put("size", queryParam.getSize());

        List<Map<String, Object>> queryAccount = userDao.queryAccount(pmaps);

        List<Map<String, Object>> results = new ArrayList<>();

        if (queryAccount != null && queryAccount.size() > 0)
        {

            for (Map<String, Object> map : queryAccount)
            {

                Map<String, Object> result = new HashMap<>();
                result.put("id", map.get("id"));
                result.put("uname", map.get("user_name"));
                result.put("pwd", map.get("pwd"));
                result.put("inTime", map.get("in_time"));
                results.add(result);
            }
        }

        Long countAccount = userDao.countAccount(pmaps);

        return ResultModel.success(countAccount, results);
    }

    /**
     * (非 Javadoc)
     * <p>
     * Title: addAccount
     * </p>
     * 新增账号
     * 
     * @param param
     * @return
     * @see com.bp.notice.business.service.UserService#addAccount(java.lang.String)
     */
    @Override
    public ResultModel addAccount(String param)
    {

        QueryParam queryParam = JSON.parseObject(param, QueryParam.class);

        Map<String, Object> pmaps = new HashMap<>();

        Object uname = queryParam.getFilter().get("uname");
        if (uname == null || uname.equals(""))
        {

            return ResultModel.fail(ResultModel.CODE_PARAM_MISSING, ResultModel.CODE_PARAM_MISSING_DESC);
        }
        List<Map<String, Object>> queryByName = userDao.queryByName(uname.toString());

        if (queryByName != null && queryByName.size() > 0)
        {
            return ResultModel.fail(ResultModel.CODE_SYSTEM_ERROR, "用户名不能重复！");

        }

        Object pwd = queryParam.getFilter().get("pwd");

        if (pwd == null || pwd.equals(""))
        {

            return ResultModel.fail(ResultModel.CODE_PARAM_MISSING, ResultModel.CODE_PARAM_MISSING_DESC);
        }

        pmaps.put("uname", uname);
        pmaps.put("pwd", pwd);
        pmaps.put("userID", queryParam.getUserId());

        Long addAccount = userDao.addAccount(pmaps);

        if (addAccount <= 0)
        {

            return ResultModel.fail(ResultModel.CODE_SYSTEM_ERROR, "账号存在");
        }

        return ResultModel.success(addAccount, null);
    }

    /**
     * (非 Javadoc)
     * <p>
     * Title: editAccount
     * </p>
     * <p>
     * Description:
     * </p>
     * 
     * @param param
     * @return
     * @see com.bp.notice.business.service.UserService#editAccount(java.lang.String)
     */
    @Override
    public ResultModel editAccount(String param)
    {

        QueryParam queryParam = JSON.parseObject(param, QueryParam.class);

        Map<String, Object> pmaps = new HashMap<>();

        Object uname = queryParam.getFilter().get("uname");
        if (uname == null || uname.equals(""))
        {

            return ResultModel.fail(ResultModel.CODE_PARAM_MISSING, ResultModel.CODE_PARAM_MISSING_DESC);
        }

          
        Object uid = queryParam.getFilter().get("uid");

        if (uid == null)
        {
            return ResultModel.fail(ResultModel.CODE_PARAM_MISSING, ResultModel.CODE_PARAM_MISSING_DESC);

        }
        
        Map<String, Object> userById = userDao.getUserById(uid);
        
        
        Object oldUserName = userById.get("user_name");
        
       
        
        if(!oldUserName.equals(uname))//比较原先的用户名称和修改的用户名称，不一样的时候才做校验
        {
           
            List<Map<String, Object>> queryByName = userDao.queryByName(uname.toString());
            if(queryByName!=null&&queryByName.size()>0)
            {
                return ResultModel.fail(ResultModel.CODE_DATA_REPEAT, ResultModel.CODE_DATA_REPEAT_DESC);
            }
            
        }
        
       
        Object pwd = queryParam.getFilter().get("pwd");

        if (pwd == null || pwd.equals(""))
        {

            return ResultModel.fail(ResultModel.CODE_PARAM_MISSING, ResultModel.CODE_PARAM_MISSING_DESC);
        }
        pmaps.put("uname", uname);
        pmaps.put("pwd", pwd);
        pmaps.put("uid", uid);

        Long editAccount = userDao.editAccount(pmaps);

        if (editAccount <= 0)
        {

            return ResultModel.fail(ResultModel.CODE_SYSTEM_ERROR, ResultModel.CODE_SYSTEM_ERROR_DESC);
        }

        return ResultModel.success(editAccount, null);

    }

    /**
     * (非 Javadoc)
     * <p>
     * Title: deleteAccount
     * </p>
     * 删除用户
     * 
     * @param param
     * @return
     * @see com.bp.notice.business.service.UserService#deleteAccount(java.lang.String)
     */
    @Override
    public ResultModel deleteAccount(String param)
    {
        QueryParam queryParam = JSON.parseObject(param, QueryParam.class);

        Object uid = queryParam.getFilter().get("uid");

        if (uid == null)
        {
            return ResultModel.fail(ResultModel.CODE_PARAM_MISSING, ResultModel.CODE_PARAM_MISSING_DESC);

        }

        Long deleteAccount = userDao.deleteAccount(uid);

        if (deleteAccount <= 0)
        {
            return ResultModel.fail(ResultModel.CODE_SYSTEM_ERROR, ResultModel.CODE_SYSTEM_ERROR_DESC);

        }

        return ResultModel.success(deleteAccount, null);
    }

    /**
     * (非 Javadoc)
     * <p>
     * Title: queryByName
     * </p>
     * 根据用户名获取用户所有信息
     * 
     * @param param
     * @return
     * @see com.bp.notice.business.service.UserService#queryByName(java.lang.String)
     */
    @Override
    public ResultModel queryByName(String param)
    {

        QueryParam queryParam = JSON.parseObject(param, QueryParam.class);

        Object userName = queryParam.getFilter().get("userName");

        if (userName == null || userName.equals(""))
        {
            return ResultModel.fail(ResultModel.CODE_PARAM_MISSING, ResultModel.CODE_PARAM_MISSING_DESC);

        }

        List<Map<String, Object>> queryByName = userDao.queryByName((String)userName);

        List<Map<String, Object>> results = new ArrayList<>();

        if (queryByName == null || queryByName.size() == 0)
        {
            return ResultModel.fail(ResultModel.CODE_DATA_NOT_EXIST, ResultModel.CODE_DATA_NOT_EXIST_DESC);

        }

        for (Map<String, Object> map : queryByName)
        {

            Map<String, Object> result = new HashMap<>();

            result.put("id", map.get("id"));
            result.put("pwd", map.get("pwd"));
            result.put("uname", map.get("user_name"));
            result.put("remark", map.get("remark"));
            result.put("inTime", map.get("in_time"));
            result.put("token", map.get("token"));
            results.add(result);
        }

        return ResultModel.success(results.size(), results);
    }

    /**
     * (非 Javadoc)
     * <p>
     * Title: isAuth
     * </p>
     * 接口调用传入的token进行校验
     * 
     * @param token
     * @return
     * @see com.bp.notice.business.service.UserService#isAuth(java.lang.String)
     */
    @Override
    public boolean isAuth(String token)
    {

        List<Map<String, Object>> accountByToken = userDao.getAccountByToken(token.replace("Bearer ", ""));

        if (accountByToken != null && accountByToken.size() > 0)
        {
            return true;
        }

        return false;
    }

    /**
     * (非 Javadoc)
     * <p>
     * Title: loginSuccess
     * </p>
     * <p>
     * Description:
     * </p>
     * 
     * @param param
     * @return
     * @see com.bp.notice.business.service.UserService#loginSuccess(java.lang.String)
     */
    @Override
    public ResultModel loginSuccess(String param)
    {

        QueryParam queryParam = JSON.parseObject(param, QueryParam.class);

        Object userName = queryParam.getFilter().get("userName");

        if (userName == null || userName.equals(""))
        {
            return ResultModel.fail(ResultModel.CODE_PARAM_MISSING, ResultModel.CODE_PARAM_MISSING_DESC);

        }
        Object token = queryParam.getFilter().get("token");

        if (token == null || token.equals(""))
        {
            return ResultModel.fail(ResultModel.CODE_PARAM_MISSING, ResultModel.CODE_PARAM_MISSING_DESC);

        }

        List<Map<String, Object>> queryByName = userDao.queryByName((String)userName);

        if (queryByName == null || queryByName.size() == 0)
        {
            return ResultModel.fail(ResultModel.CODE_DATA_NOT_EXIST, ResultModel.CODE_DATA_NOT_EXIST_DESC);

        }

        Map<String, Object> params = new HashMap<>();

        params.put("token", token);
        params.put("uname", userName);
        Long updateTokenByuname = userDao.updateTokenByuname(params);

        if (updateTokenByuname > 0)
        {
            logger.trace(userName + "更新token成功；更新参数为：" + params);
            return ResultModel.success(updateTokenByuname, null);
        }

        return ResultModel.fail(ResultModel.CODE_SYSTEM_ERROR, ResultModel.CODE_SYSTEM_ERROR_DESC);
    }

}
