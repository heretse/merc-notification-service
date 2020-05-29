/**
 * @FileName: GroupController.java
 * @PackageName com.bp.notice.business.controller
 * @Description: TODO(用一句话描述该文件做什么)
 * @author MT
 * @date 2018年2月6日 下午1:53:28
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

import com.bp.notice.business.service.GroupService;
import com.bp.notice.business.utils.ResultModel;

/**
 * @ClassName: GroupController
 * @Description: 通报组管理
 * @author MT
 * @date 2018年2月6日 下午1:53:28
 */
//@CrossOrigin(maxAge = 3600, methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE})
@Controller
@RequestMapping(value = "/group", produces = {"application/json;charset=UTF-8"})
public class GroupController
{
    private Logger logger = LogManager.getLogger(LogManager.ROOT_LOGGER_NAME);

    @Autowired
    GroupService service;

    @RequestMapping(value = "/query", method = RequestMethod.POST)
    @ResponseBody
    //查询通报组信息
    public ResultModel query(@RequestBody String param)
    {

        logger.info(param);

        if (param == null)
        {

            return ResultModel.fail(ResultModel.CODE_PARAM_MISSING, ResultModel.CODE_PARAM_MISSING_DESC);
        }

        return service.query(param);
    }

    @RequestMapping(value = "/queryGTByID", method = RequestMethod.POST)
    @ResponseBody
    //查询通报组信息
    public ResultModel queryGTByID(@RequestBody String param)
    {

        logger.info(param);

        if (param == null)
        {

            return ResultModel.fail(ResultModel.CODE_PARAM_MISSING, ResultModel.CODE_PARAM_MISSING_DESC);
        }

        return service.queryGTByID(param);
    }

    @RequestMapping(value = "/queryBindDatas", method = RequestMethod.POST)
    @ResponseBody
    //查询通报组绑定和未绑定的终端组信息和联系人信息
    public ResultModel queryBindDatas(@RequestBody String param)
    {

        logger.info(param);

        if (param == null)
        {

            return ResultModel.fail(ResultModel.CODE_PARAM_MISSING, ResultModel.CODE_PARAM_MISSING_DESC);
        }

        return service.queryBindDatas(param);
    }

    @RequestMapping(value = "/bindData", method = RequestMethod.POST)
    @ResponseBody
    //根据通报组ID开始绑定联系人和终端组相关信息
    public ResultModel bindData(@RequestBody String param)
    {

        logger.info(param);

        if (param == null)
        {

            return ResultModel.fail(ResultModel.CODE_PARAM_MISSING, ResultModel.CODE_PARAM_MISSING_DESC);
        }

        return service.bindData(param);
    }

    @RequestMapping(value = "/add", method = RequestMethod.POST)
    @ResponseBody
    //新增通报组信息
    public ResultModel add(@RequestBody String param)
    {

        logger.info(param);

        if (param == null)
        {

            return ResultModel.fail(ResultModel.CODE_PARAM_MISSING, ResultModel.CODE_PARAM_MISSING_DESC);
        }

        return service.add(param);
    }

    @RequestMapping(value = "/edit", method = RequestMethod.POST)
    @ResponseBody
    //修改通报组信息
    public ResultModel edit(@RequestBody String param)
    {

        logger.info(param);

        if (param == null)
        {

            return ResultModel.fail(ResultModel.CODE_PARAM_MISSING, ResultModel.CODE_PARAM_MISSING_DESC);
        }

        return service.edit(param);
    }

    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    @ResponseBody
    //删除通报组信息
    public ResultModel delete(@RequestBody String param)
    {

        logger.info(param);

        if (param == null)
        {

            return ResultModel.fail(ResultModel.CODE_PARAM_MISSING, ResultModel.CODE_PARAM_MISSING_DESC);
        }

        return service.delete(param);
    }

}
