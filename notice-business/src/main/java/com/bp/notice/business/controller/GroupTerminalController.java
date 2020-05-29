/**
 * @FileName: TerminalController.java
 * @PackageName com.bp.business.controller
 * @Description: TODO(用一句话描述该文件做什么)
 * @author MT
 * @date 2018年1月28日 下午3:00:01
 * @version
 */

package com.bp.notice.business.controller;

import org.apache.logging.log4j.LogManager;

import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.bp.notice.business.service.GroupTerminalService;
import com.bp.notice.business.utils.ResultModel;

/**
 * @ClassName: GroupTerminalController
 * @Description:终端组管理
 * @author MT
 * @date 2018年1月28日 下午3:00:01
 */
//@CrossOrigin(maxAge = 3600, methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE})
@Controller
@RequestMapping(value = "/groupTerminal", produces = {"application/json;charset=UTF-8"})
public class GroupTerminalController
{

    @Autowired
    GroupTerminalService Service;

    private Logger logger = LogManager.getLogger(LogManager.ROOT_LOGGER_NAME);

    @RequestMapping(value = "/add", method = RequestMethod.POST)
    @ResponseBody
    //新增终端组（单个）
    public ResultModel addGroupTerminal(@RequestBody String param)
    {

        logger.info(param);

        if (param == null)
        {

            return ResultModel.fail(ResultModel.CODE_PARAM_MISSING, ResultModel.CODE_PARAM_MISSING_DESC);
        }

        return Service.addGroupTerminal(param);
    }

    @RequestMapping(value = "/edit", method = RequestMethod.POST)
    @ResponseBody
    //编辑终端组
    public ResultModel editGroupTerminal(@RequestBody String param)
    {

        logger.info(param);

        if (param == null)
        {

            return ResultModel.fail(ResultModel.CODE_PARAM_MISSING, ResultModel.CODE_PARAM_MISSING_DESC);
        }

        return Service.editGroupTerminal(param);
    }

    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    @ResponseBody
    //删除终端组
    public ResultModel deleteGroupTerminal(@RequestBody String param)
    {

        logger.info(param);

        if (param == null)
        {

            return ResultModel.fail(ResultModel.CODE_PARAM_MISSING, ResultModel.CODE_PARAM_MISSING_DESC);
        }

        try
        {
            return Service.deleteGroupTerminal(param);
        }
        catch (Exception e)
        {

            logger.info(e.getMessage());
            return ResultModel.fail(ResultModel.CODE_SYSTEM_ERROR, e.getMessage());
        }

    }

    @RequestMapping(value = "/query", method = RequestMethod.POST)
    @ResponseBody
    //查询终端组
    public ResultModel queryGroupTerminals(@RequestBody String param)
    {

        logger.info(param);

        return Service.queryGroupTerminals(param);
    }

    @RequestMapping(value = "/queryContactByGID", method = RequestMethod.POST)
    @ResponseBody
    //根据通报组id获得组下面的联系人信息
    public ResultModel queryContactByGID(@RequestBody String param)
    {

        logger.info(param);

        return Service.queryContactByGID(param);
    }

    @RequestMapping(value = "/queryTerminalBygtID", method = RequestMethod.POST)
    @ResponseBody
    //根据终端组id获得该终端产线下面所属的终端
    public ResultModel queryTerminalBygtID(@RequestBody String param)
    {

        logger.info(param);

        return Service.queryTerminalBygtID(param);
    }

    @RequestMapping(value = "/queryRelationMac", method = RequestMethod.POST)
    @ResponseBody
    //用户点击绑定按钮的时候 查询 未绑定的终端和已经绑定的终端(根据终端组GTID)
    public ResultModel queryRelationMac(@RequestBody String param)
    {

        logger.info(param);

        return Service.queryRelationMac(param);
    }

    @RequestMapping(value = "/bind", method = RequestMethod.POST)
    @ResponseBody
    //根据终端组ID和MAC地址开始执行绑定操作（事务操作）
    public ResultModel bind(@RequestBody String param)
    {

        logger.info(param);

        return Service.bind(param);

    }

}
