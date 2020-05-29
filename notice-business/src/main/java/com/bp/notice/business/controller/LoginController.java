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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONObject;
import com.bp.notice.business.service.UserService;
import com.bp.notice.business.utils.ResultModel;

/**
 * @ClassName: UserLoginController
 * @Description: TODO(这里用一句话描述这个类的作用)
 * @author MT
 * @date 2018年1月23日 下午5:16:08
 */
//@CrossOrigin(maxAge = 3600, methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE})
@Controller
@RequestMapping(value = "/rest", produces = {"application/json;charset=UTF-8"})
public class LoginController
{

    private Logger logger = LogManager.getLogger(LogManager.ROOT_LOGGER_NAME);
    @Autowired
    UserService UserService;

    @RequestMapping(value = "/login", method = RequestMethod.POST)
    @ResponseBody
    public ResultModel login(@RequestBody String param)
    {
        logger.trace(param);

        JSONObject jsonObject = JSONObject.parseObject(param);

        String uname = jsonObject.getString("uname");
        String pwd = jsonObject.getString("pwd");

        return UserService.login(uname, pwd);
    }

}
