/**
 * @FileName: UserService.java
 * @PackageName com.bp.business.service
 * @Description: TODO(用一句话描述该文件做什么)
 * @author MT
 * @date 2018年1月23日 下午5:32:47
 * @version
 */

package com.bp.notice.business.service;

import com.bp.notice.business.utils.ResultModel;

/**
 * @ClassName: UserService
 * @Description: TODO(这里用一句话描述这个类的作用)
 * @author MT
 * @date 2018年1月23日 下午5:32:47
 */
public interface UserService
{

    /**
     * @Title: login
     * @Description: 账号登录
     * @param @param uname
     * @param @param pwd
     * @param @return 设定文件
     * @return ResultModel 返回类型
     * @throws
     */
    public ResultModel login(String uname, String pwd);

    public ResultModel queryAccounts(String param);

    /**
     * @Title: addAccount
     * @Description: 新增账号
     * @param @param param
     * @param @return 设定文件
     * @return ResultModel 返回类型
     * @throws
     */
    public ResultModel addAccount(String param);

    /**
     * @Title: editAccount
     * @Description: 修改账号
     * @param @param param
     * @param @return 设定文件
     * @return ResultModel 返回类型
     * @throws
     */
    public ResultModel editAccount(String param);

    /**
     * @Title: editAccount
     * @Description: 修改账号
     * @param @param param
     * @param @return 设定文件
     * @return ResultModel 返回类型
     * @throws
     */
    public ResultModel deleteAccount(String param);

    /**
     * @Title: queryByName
     * @Description: 根据用户名获取用户所有信息
     * @param @param param
     * @param @return 设定文件
     * @return ResultModel 返回类型
     * @throws
     */
    public ResultModel queryByName(String param);

    public boolean isAuth(String token);

    public ResultModel loginSuccess(String param);
}
