/**
 * @FileName: UserLoginController.java
 * @PackageName com.bp.business.controller
 * @Description: TODO(用一句话描述该文件做什么)
 * @author MT
 * @date 2018年1月23日 下午5:16:08
 * @version
 */

package com.bp.notice.business.controller;

import org.apache.logging.log4j.LogManager;

import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.bp.notice.business.service.UserService;
import com.bp.notice.business.utils.ResultModel;

/**
 * @ClassName: UserLoginController
 * @Description: TODO 账号管理
 * @author MT
 * @date 2018年1月23日 下午5:16:08
 */
//@CrossOrigin(maxAge = 3600, methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE})
@Controller
@RequestMapping(value = "/account", produces = {"application/json;charset=UTF-8"})
public class UserController
{

    private Logger logger = LogManager.getLogger(LogManager.ROOT_LOGGER_NAME);
    @Autowired
    UserService UserService;

    @RequestMapping(value = "/queryByName", method = RequestMethod.POST)
    @ResponseBody
    //查询账号
    public ResultModel queryByName(@RequestBody String param)
    {
        logger.trace(param);
        if (param == null)
        {

            return ResultModel.fail(ResultModel.CODE_PARAM_MISSING, ResultModel.CODE_PARAM_MISSING_DESC);
        }

        return UserService.queryByName(param);
    }

    @RequestMapping(value = "/loginSuccess", method = RequestMethod.POST)
    @ResponseBody
    //登录成功回调接口存入token
    public ResultModel loginSuccess(@RequestBody String param)
    {
        logger.trace(param);
        if (param == null)
        {

            return ResultModel.fail(ResultModel.CODE_PARAM_MISSING, ResultModel.CODE_PARAM_MISSING_DESC);
        }

        return UserService.loginSuccess(param);
    }

    @RequestMapping(value = "/query", method = RequestMethod.POST)
    @ResponseBody
    //查询账号
    public ResultModel queryAccounts(@RequestBody String param)
    {
        logger.trace(param);
        if (param == null)
        {

            return ResultModel.fail(ResultModel.CODE_PARAM_MISSING, ResultModel.CODE_PARAM_MISSING_DESC);
        }

        return UserService.queryAccounts(param);
    }

    @RequestMapping(value = "/add", method = RequestMethod.POST)
    @ResponseBody

    //新增账号
    public ResultModel addUser(@RequestBody String param)
    {
        logger.trace(param);

        if (param == null)
        {

            return ResultModel.fail(ResultModel.CODE_PARAM_MISSING, ResultModel.CODE_PARAM_MISSING_DESC);
        }

        return UserService.addAccount(param);
    }

    @RequestMapping(value = "/edit", method = RequestMethod.POST)
    @ResponseBody

    //编辑账号
    public ResultModel editUser(@RequestBody String param)
    {
        logger.trace(param);

        if (param == null)
        {

            return ResultModel.fail(ResultModel.CODE_PARAM_MISSING, ResultModel.CODE_PARAM_MISSING_DESC);
        }

        return UserService.editAccount(param);
    }

    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    @ResponseBody

    //删除账号
    public ResultModel deleteUser(@RequestBody String param)
    {
        logger.trace(param);

        if (param == null)
        {

            return ResultModel.fail(ResultModel.CODE_PARAM_MISSING, ResultModel.CODE_PARAM_MISSING_DESC);
        }

        return UserService.deleteAccount(param);
    }

}
