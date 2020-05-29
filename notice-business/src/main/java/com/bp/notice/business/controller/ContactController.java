/**
 * @FileName: ContactController.java
 * @PackageName com.bp.notice.business.controller
 * @Description: TODO(用一句话描述该文件做什么)
 * @author MT
 * @date 2018年2月5日 下午3:04:49
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

import com.bp.notice.business.service.ContactService;
import com.bp.notice.business.utils.ResultModel;

/**
 * @ClassName: ContactController
 * @Description: 联系人管理类
 * @author MT
 * @date 2018年2月5日 下午3:04:49
 */
@Controller
@RequestMapping(value = "/contact", produces = {"application/json;charset=UTF-8"})
public class ContactController
{

    private Logger logger = LogManager.getLogger(LogManager.ROOT_LOGGER_NAME);

    @Autowired
    private ContactService service;

    @RequestMapping(value = "/queryMethods", method = RequestMethod.POST)
    @ResponseBody
    //查询联系方式（列表使用）
    public ResultModel queryMethods(@RequestBody String param)
    {

        logger.info(param);

        if (param == null)
        {

            return ResultModel.fail(ResultModel.CODE_PARAM_MISSING, ResultModel.CODE_PARAM_MISSING_DESC);
        }

        return service.queryMethods(param);
    }

    @RequestMapping(value = "/query", method = RequestMethod.POST)
    @ResponseBody
    //查询联系人
    public ResultModel query(@RequestBody String param)
    {

        logger.info(param);

        if (param == null)
        {

            return ResultModel.fail(ResultModel.CODE_PARAM_MISSING, ResultModel.CODE_PARAM_MISSING_DESC);
        }

        return service.query(param);
    }

    @RequestMapping(value = "/add", method = RequestMethod.POST)
    @ResponseBody
    //新增联系人
    public ResultModel add(@RequestBody String param)
    {

        logger.info(param);

        if (param == null)
        {

            return ResultModel.fail(ResultModel.CODE_PARAM_MISSING, ResultModel.CODE_PARAM_MISSING_DESC);
        }
        
        try
        {
           return  service.add(param);
        }
        catch (Exception e)
        {
            logger.info(e.getMessage());
            e.printStackTrace();
            return ResultModel.fail(ResultModel.CODE_DATA_REPEAT,e.getMessage());
        }
        
        
    }

    @RequestMapping(value = "/edit", method = RequestMethod.POST)
    @ResponseBody
    //编辑联系人
    public ResultModel edit(@RequestBody String param)
    {

        logger.info(param);

        if (param == null)
        {

            return ResultModel.fail(ResultModel.CODE_PARAM_MISSING, ResultModel.CODE_PARAM_MISSING_DESC);
        }
        
        try
        {
            return service.edit(param);
        }
        catch (Exception e)
        {
             e.printStackTrace();
             
             logger.info(e.getMessage());
             
             return ResultModel.fail(ResultModel.CODE_DATA_REPEAT,e.getMessage());
        }
        

        
    }

    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    @ResponseBody
    //删除联系人
    public ResultModel delete(@RequestBody String param)
    {

        logger.info(param);

        if (param == null)
        {

            return ResultModel.fail(ResultModel.CODE_PARAM_MISSING, ResultModel.CODE_PARAM_MISSING_DESC);
        }
        try
        {

            return service.delete(param);
        }
        catch (Exception e)
        {
            logger.trace(e.getMessage());
            return ResultModel.fail(ResultModel.CODE_SYSTEM_ERROR, ResultModel.CODE_SYSTEM_ERROR_DESC);
        }

    }
}
