/**
 * @FileName: UserServiceImpl.java
 * @PackageName com.bp.business.service.impl
 * @Description: TODO(用一句话描述该文件做什么)
 * @author MT
 * @date 2018年1月23日 下午5:34:07
 * @version
 */

package com.bp.notice.business.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.bp.notice.business.dao.TerminalDao;
import com.bp.notice.business.entity.Terminal;
import com.bp.notice.business.service.TerminalService;
import com.bp.notice.business.utils.DateUtils;
import com.bp.notice.business.utils.QueryParam;
import com.bp.notice.business.utils.ResultModel;

/**
 * @ClassName: UserServiceImpl
 * @Description: 用户操作类（包含登录）
 * @author MT
 * @date 2018年1月23日 下午5:34:07
 */

@Service
public class TerminalServiceImpl implements TerminalService
{

    @Autowired
    TerminalDao dao;

    /**
     * (非 Javadoc)
     * <p>
     * Title: addTerminal
     * </p>
     * <p>
     * Description:
     * </p>
     * 
     * @param terminal
     * @return
     * @see com.bp.notice.business.service.TerminalService#addTerminal(com.bp.notice.business.entity.Terminal)
     */
    @Override
    public ResultModel addTerminal(String param)
    {

        QueryParam queryParam = JSON.parseObject(param, QueryParam.class);

        Object mac = queryParam.getFilter().get("mac");

        if (mac == null || "".equals(mac))
        {

            return ResultModel.fail(ResultModel.CODE_PARAM_MISSING, ResultModel.CODE_PARAM_MISSING_DESC);

        }
        
        Object remark = queryParam.getFilter().get("remark");

        Terminal terminal = new Terminal();

        terminal.setMac(mac.toString().toLowerCase());
        terminal.setRemark(remark != null ? remark.toString() : null);
        terminal.setCreateTime(new Date());
        terminal.setUserID(queryParam.getUserId());
        terminal.setUpTime(new Date());
        Long addTerminal = dao.addTerminal(terminal);

        if (addTerminal > 0)
        {

            return ResultModel.success(addTerminal, null);

        }

        return ResultModel.fail(ResultModel.CODE_SYSTEM_ERROR, ResultModel.CODE_SYSTEM_ERROR_DESC);
    }

    /**
     * (非 Javadoc)
     * <p>
     * Title: addBatchTerminal
     * </p>
     * <p>
     * Description:
     * </p>
     * 
     * @param parm
     * @return
     * @see com.bp.notice.business.service.TerminalService#addBatchTerminal(java.lang.String)
     */
    @Override
    public ResultModel addBatchTerminal(String param)
    {

        QueryParam queryParam = JSON.parseObject(param, QueryParam.class);

        Object obj = queryParam.getFilter().get("terminals");

        if (obj == null)
        {
            return ResultModel.fail(ResultModel.CODE_PARAM_MISSING, ResultModel.CODE_PARAM_MISSING_DESC);
        }

        JSONArray macArray = (JSONArray)obj;

        if (macArray == null || macArray.size() == 0)
        {
            return ResultModel.fail(ResultModel.CODE_PARAM_MISSING, ResultModel.CODE_PARAM_MISSING_DESC);

        }
        int error_export_count = 0;

        List<String> errormacs = new ArrayList<>();//报错批量插入失败的mac地址
        for (int i = 0; i < macArray.size(); i++)
        {

            JSONObject terminalJson = macArray.getJSONObject(i);

            if (terminalJson.getString("mac") == null)
            {
                error_export_count++;
                continue;
            }

            if (terminalJson.getString("mac") != null && terminalJson.getString("mac").length() != 16)
            {
                error_export_count++;
                errormacs.add(terminalJson.getString("mac"));
                continue;
            }

            Terminal terminal = new Terminal();

            terminal.setMac(terminalJson.getString("mac").toLowerCase());
            terminal.setRemark(terminalJson.getString("remark"));
            terminal.setUserID(queryParam.getUserId());
            terminal.setCreateTime(new Date());
            terminal.setUpTime(new Date());
            Long addTerminal = dao.addTerminal(terminal);
            if (addTerminal == 0)
            {
                error_export_count++;
                errormacs.add(terminalJson.getString("mac"));
            }

        }

        /*
         * List<Map<String,Integer>> maps = new ArrayList<>();
         * Map<String,Integer> result = new HashMap<>();
         * result.put("success_count",macArray.size()-error_export_count);
         * result.put("error_count",error_export_count);
         * maps.add(result);
         */
        return ResultModel.success(error_export_count, errormacs);
    }

    /**
     * (非 Javadoc)
     * <p>
     * Title: deteTerminal
     * </p>
     * <p>
     * Description:
     * </p>
     * 
     * @param param
     * @return
     * @see com.bp.notice.business.service.TerminalService#deteTerminal(java.lang.String)
     */
    @Override
    public ResultModel deteTerminal(String param)
    {

        JSONObject object = JSON.parseObject(param);

        JSONObject filter = object.getJSONObject("filter");

        Long id = filter.getLong("id");

        if (id == null)
        {
            return ResultModel.fail(ResultModel.CODE_PARAM_MISSING, ResultModel.CODE_PARAM_MISSING_DESC);
        }

        Long result = dao.deteTerminal(id);

        if (result > 0)
        {

            return ResultModel.success(1, null);
        }

        return ResultModel.fail(ResultModel.CODE_SYSTEM_ERROR, ResultModel.CODE_SYSTEM_ERROR_DESC);
    }

    /**
     * (非 Javadoc)
     * <p>
     * Title: queryTerminal
     * </p>
     * <p>
     * Description:
     * </p>
     * 
     * @param param
     * @return
     * @see com.bp.notice.business.service.TerminalService#queryTerminal(java.util.Map)
     */
    @Override
    public ResultModel queryTerminal(String param)

    {

        QueryParam queryParam = JSON.parseObject(param, QueryParam.class);

        //JSONObject object = JSON.parseObject(param);

        Map<String, Object> pmaps = new HashMap<>();

        //JSONObject filter = object.getJSONObject("filter");
        /*
         * if(object.getJSONObject("filter")!=null)
         * {
         * pmaps.put("mac",filter.getString("mac"));
         * }
         */
        pmaps.put("mac", queryParam.getFilter().get("mac"));

        pmaps.put("start", DateUtils.convertDate(queryParam.getFilter().get("start")));
        pmaps.put("end", DateUtils.convertDate(queryParam.getFilter().get("end")));
        pmaps.put("page", queryParam.getPage());
        pmaps.put("size", queryParam.getSize());

        List<Map<String, Object>> queryTerminal = dao.queryTerminal(pmaps);

        List<Map<String, Object>> results = new ArrayList<>();

        if (queryTerminal != null && queryTerminal.size() > 0)
        {

            for (Map<String, Object> terminal : queryTerminal)
            {
                Map<String, Object> result = new HashMap<>();

                result.put("id", terminal.get("id"));
                result.put("mac", terminal.get("mac"));
                result.put("remark", terminal.get("remark"));
                result.put("inTime", terminal.get("in_time"));
                results.add(result);
            }

        }

        Long countTerminal = dao.countTerminal(pmaps);

        return ResultModel.success(countTerminal, results);
    }

    @Override
	public ResultModel updateTerminal(String param) {
		QueryParam queryParam = JSON.parseObject(param, QueryParam.class);

        Object mac = queryParam.getFilter().get("mac");

        if (mac == null || "".equals(mac))
        {

            return ResultModel.fail(ResultModel.CODE_PARAM_MISSING, ResultModel.CODE_PARAM_MISSING_DESC);

        }
        
        Map<String, Object> pmaps = new HashMap<>();

        pmaps.put("mac", queryParam.getFilter().get("mac"));

        pmaps.put("remark", queryParam.getFilter().get("remark"));


        Long result = dao.updateTerminal(pmaps);

        if (result > 0)
        {
            return ResultModel.success(result, null);
        }

        return ResultModel.fail(ResultModel.CODE_DATA_NOT_EXIST, ResultModel.CODE_DATA_NOT_EXIST_DESC);
	}

}
