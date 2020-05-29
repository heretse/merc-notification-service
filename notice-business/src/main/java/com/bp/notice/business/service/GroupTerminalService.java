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
 * @ClassName: GroupTerminalService
 * @Description:终端组业务逻辑类
 * @author MT
 * @date 2018年1月23日 下午5:32:47
 */
public interface GroupTerminalService
{
    public ResultModel addGroupTerminal(String param);

    public ResultModel editGroupTerminal(String param);

    public ResultModel deleteGroupTerminal(String param);

    /**
     * @Title: queryGroupTerminals
     * @Description: 查询终端组，包含终端组绑定的通报组
     * @param @param param
     * @param @return 设定文件
     * @return ResultModel 返回类型
     * @throws
     */
    public ResultModel queryGroupTerminals(String param);

    public ResultModel queryContactByGID(String param);

    /**
     * @Title: queryTerminalByPID
     * @Description: 根据终端组ID查看已经绑定在该终端组的终端
     * @param @param param
     * @param @return 设定文件
     * @return ResultModel 返回类型
     * @throws
     */
    public ResultModel queryTerminalBygtID(String param);

    /**
     * @Title: queryRelationMac
     * @Description: 关联终端的时候的时候，根据终端组ID查询出绑定和未绑定的终端列表
     * @param @param param
     * @param @return 设定文件
     * @return ResultModel 返回类型
     * @throws
     */
    public ResultModel queryRelationMac(String param);

    /**
     * @Title: bind
     * @Description: 终端和终端组绑定操作
     * @param @return 设定文件
     * @return ResultModel 返回类型
     * @throws
     */
    public ResultModel bind(String param);

}
