/**
 * @FileName: GroupService.java
 * @PackageName com.bp.notice.business.service
 * @Description: TODO(用一句话描述该文件做什么)
 * @author MT
 * @date 2018年2月6日 下午1:55:20
 * @version
 */

package com.bp.notice.business.service;

import com.bp.notice.business.utils.ResultModel;

/**
 * @ClassName: GroupService
 * @Description: TODO(这里用一句话描述这个类的作用)
 * @author MT
 * @date 2018年2月6日 下午1:55:20
 */
public interface GroupService
{
    public ResultModel query(String param);

    public ResultModel queryGTByID(String param);

    public ResultModel queryBindDatas(String param);

    public ResultModel bindData(String param);

    public ResultModel add(String param);

    public ResultModel edit(String param);

    public ResultModel delete(String param);
}
