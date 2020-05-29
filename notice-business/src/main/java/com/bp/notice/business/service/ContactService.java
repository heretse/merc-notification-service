/**
 * @FileName: ContactService.java
 * @PackageName com.bp.notice.business.service
 * @Description: TODO(用一句话描述该文件做什么)
 * @author MT
 * @date 2018年2月5日 下午3:07:20
 * @version
 */

package com.bp.notice.business.service;

import com.bp.notice.business.utils.ResultModel;

/**
 * @ClassName: ContactService
 * @Description: 联系人服务类
 * @author MT
 * @date 2018年2月5日 下午3:07:20
 */
public interface ContactService
{

    public ResultModel queryMethods(String param);

    public ResultModel query(String param);

    public ResultModel add(String param);

    public ResultModel edit(String param);

    public ResultModel delete(String param);

}
