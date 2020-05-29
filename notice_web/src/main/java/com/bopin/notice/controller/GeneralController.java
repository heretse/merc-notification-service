
package com.bopin.notice.controller;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.bopin.notice.common.Constant;
import com.bopin.notice.interceptor.LogDetailAnnotation;
import com.bopin.notice.util.Http;
import com.bopin.notice.util.Tools;

/**
 * @ClassName: GeneralController
 * @Description: Web端调用通知服务api通用处理接口
 * @date 2018年2月6日 下午9:09:33
 * @author Yan Henghui
 * @version
 * @since JDK 1.8
 * @see
 */
@RestController
@RequestMapping(value = "/api/general")
public class GeneralController
{

    /**
     * @Title: doPost
     * @Description: 调用后台post请求接口
     * @param paramJson json对象
     * @return Map<String,Object> 返回类型
     * @throws
     */
    @RequestMapping(value = "/post", method = RequestMethod.POST)
    @LogDetailAnnotation(remark = "Add")
    public Map<String, Object> doPost(@RequestBody String paramJson, @RequestHeader("Authorization") String auth)
    {
        Map<String, Object> result = null;
        JSONObject obj = JSON.parseObject(paramJson);
        String api = obj.getString("api");
        String data = obj.getString("data");
        String token = auth.replace(Constant.APP_HTTP_HEADER_AUTHORIZATION_BEARER, "");
        Map<String, String> header = new HashMap<String, String>();
        header.put(Constant.APP_HTTP_HEADER_AUTHORIZATION, Constant.APP_HTTP_HEADER_AUTHORIZATION_BEARER + Tools.EncryptionStrBytes(token));
        JSONObject res = JSON.parseObject(Http.post(Constant.APP_NOTICE_REQUEST_BASE_URL + api, data, header));
        String resCode = res.getString("code").toString();
        switch (resCode)
        {
            case "1000":
                Map<String, Object> map = new HashMap<String, Object>();
                map.put("count", res.getString("count"));
                map.put("data", JSON.parseArray(res.getString("data")));
                result = Tools.sucessJson(map);
                break;
            case "1001":
                result = Tools.failureJson(HttpServletResponse.SC_BAD_REQUEST, res.getString("desc"));
                break;
            case "1002":
                result = Tools.failureJson(HttpServletResponse.SC_BAD_REQUEST, res.getString("desc"));
                break;
            case "1003":
                result = Tools.failureJson(HttpServletResponse.SC_BAD_REQUEST, res.getString("desc"));
                break;
            case "1004":
                result = Tools.failureJson(1004, "数据重复");
                break;
            case "9999":
                result = Tools.failureJson(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, res.getString("desc"));
                break;
        }
        return result;
    }

    /**
     * @Title: doGet
     * @Description: 调用后台get请求接口
     * @param paramJson
     * @return Map<String,Object> 返回类型
     * @throws
     */
    @RequestMapping(value = "/get", method = RequestMethod.GET)
    @LogDetailAnnotation(remark = "Query")
    public Map<String, Object> doGet(@RequestParam String paramJson, @RequestHeader("Authorization") String auth) throws Exception
    {
        Map<String, Object> result = null;
        JSONObject param = JSON.parseObject(paramJson);
        String api = param.getString("api");
        param.remove("api");
        String token = auth.replace(Constant.APP_HTTP_HEADER_AUTHORIZATION_BEARER, "");
        Map<String, String> header = new HashMap<String, String>();
        header.put(Constant.APP_HTTP_HEADER_AUTHORIZATION, Constant.APP_HTTP_HEADER_AUTHORIZATION_BEARER + Tools.EncryptionStrBytes(token));
        JSONObject res = JSON.parseObject(Http.post(Constant.APP_NOTICE_REQUEST_BASE_URL + api, param.toJSONString(), header));
        String resCode = res.getString("code").toString();
        switch (resCode)
        {
            case "1000":
                Map<String, Object> map = new HashMap<String, Object>();
                map.put("count", res.getString("count"));
                map.put("data", JSON.parseArray(res.getString("data")));
                result = Tools.sucessJson(map);
                break;
            case "1001":
                result = Tools.failureJson(HttpServletResponse.SC_BAD_REQUEST, res.getString("desc"));
                break;
            case "1002":
                result = Tools.failureJson(HttpServletResponse.SC_BAD_REQUEST, res.getString("desc"));
                break;
            case "1003":
                result = Tools.failureJson(HttpServletResponse.SC_BAD_REQUEST, res.getString("desc"));
                break;
            case "9999":
                result = Tools.failureJson(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, res.getString("desc"));
                break;
        }
        return result;
    }

    /**
     * @Title: doDelete
     * @Description: 调用后台delete请求接口
     * @param paramJson
     * @return Map<String,Object> 返回类型
     * @throws
     */
    @RequestMapping(value = "/delete", method = RequestMethod.DELETE)
    @LogDetailAnnotation(remark = "Delete")
    public Map<String, Object> doDelete(@RequestParam String paramJson)
    {
        return null;
    }

    /**
     * @Title: doPut
     * @Description: 调用后台delete请求接口
     * @param paramJson
     * @return Map<String,Object> 返回类型
     * @throws
     */
    @RequestMapping(value = "/put", method = RequestMethod.PUT)
    @LogDetailAnnotation(remark = "Update")
    public Map<String, Object> doPut(@RequestBody String paramJson)
    {
        return null;
    }
}
