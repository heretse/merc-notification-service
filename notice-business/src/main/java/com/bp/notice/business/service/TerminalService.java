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
public interface TerminalService
{
    public ResultModel addTerminal(String param);

    public ResultModel addBatchTerminal(String param);

    public ResultModel updateTerminal(String param);

    public ResultModel deteTerminal(String param);

    public ResultModel queryTerminal(String param);
}
