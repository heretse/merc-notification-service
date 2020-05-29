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

import com.bp.notice.business.service.TerminalService;
import com.bp.notice.business.utils.ResultModel;

/**
 * @ClassName: TerminalController
 * @Description:终端管理
 * @author MT
 * @date 2018年1月28日 下午3:00:01
 */
//@CrossOrigin(maxAge = 3600, methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE})
@Controller
@RequestMapping(value = "/terminal", produces = {"application/json;charset=UTF-8"})
public class TerminalController
{

    @Autowired
    TerminalService terminalService;

    private Logger logger = LogManager.getLogger(LogManager.ROOT_LOGGER_NAME);

    @RequestMapping(value = "/add", method = RequestMethod.POST)
    @ResponseBody
    //新增终端（单个）
    public ResultModel addTerminal(@RequestBody String param)
    {

        logger.info(param);

        if (param == null)
        {

            return ResultModel.fail(ResultModel.CODE_PARAM_MISSING, ResultModel.CODE_PARAM_MISSING_DESC);
        }

        return terminalService.addTerminal(param);
    }

    @RequestMapping(value = "/addBatch", method = RequestMethod.POST)
    @ResponseBody
    //新增终端（批量）
    public ResultModel addBatchTerminal(@RequestBody String param)
    {

        logger.info(param);

        if (param == null)
        {

            return ResultModel.fail(ResultModel.CODE_PARAM_MISSING, ResultModel.CODE_PARAM_MISSING_DESC);
        }

        return terminalService.addBatchTerminal(param);
    }

    @RequestMapping(value = "/update", method = RequestMethod.POST)
    @ResponseBody
    //更新终端（单个）
    public ResultModel updateTerminal(@RequestBody String param)
    {

        logger.info(param);

        if (param == null)
        {

            return ResultModel.fail(ResultModel.CODE_PARAM_MISSING, ResultModel.CODE_PARAM_MISSING_DESC);
        }

        return terminalService.updateTerminal(param);
    }

    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    @ResponseBody
    //删除终端
    public ResultModel deleteTerminal(@RequestBody String param)
    {

        logger.info(param);

        if (param == null)
        {

            return ResultModel.fail(ResultModel.CODE_PARAM_MISSING, ResultModel.CODE_PARAM_MISSING_DESC);
        }

        return terminalService.deteTerminal(param);
    }

    @RequestMapping(value = "/query", method = RequestMethod.POST)
    @ResponseBody
    //查询终端
    public ResultModel queryTerminal(@RequestBody String param)
    {

        logger.info(param);

        return terminalService.queryTerminal(param);
    }

}
