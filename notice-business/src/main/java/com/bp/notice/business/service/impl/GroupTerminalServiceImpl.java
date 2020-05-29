/**
 * @FileName: GroupTerminalServiceImpl.java
 * @PackageName com.bp.notice.business.service.impl
 * @Description: TODO(用一句话描述该文件做什么)
 * @author MT
 * @date 2018年2月1日 下午1:48:23
 * @version
 */

package com.bp.notice.business.service.impl;

import java.util.ArrayList;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.bp.notice.business.dao.GroupTerminalDao;
import com.bp.notice.business.entity.GroupTerminal;
import com.bp.notice.business.service.GroupTerminalService;
import com.bp.notice.business.utils.QueryParam;
import com.bp.notice.business.utils.ResultModel;

/**
 * @ClassName: GroupTerminalServiceImpl
 * @Description: TODO(这里用一句话描述这个类的作用)
 * @author MT
 * @date 2018年2月1日 下午1:48:23
 */
@Service
public class GroupTerminalServiceImpl implements GroupTerminalService
{

    private Logger logger = LogManager.getLogger(LogManager.ROOT_LOGGER_NAME);
    @Autowired

    GroupTerminalDao gtDao;

    /**
     * (非 Javadoc)
     * <p>
     * Title: addGroupTerminal
     * </p>
     * <p>
     * Description:新增终端组，需要插入终端组名称、终端组的备注、当前新增的用户ID
     * </p>
     * 
     * @param param
     * @return
     * @see com.bp.notice.business.service.GroupTerminalService#addGroupTerminal(java.lang.String)
     */
    @Override
    public ResultModel addGroupTerminal(String param)
    {

        QueryParam queryParam = JSON.parseObject(param, QueryParam.class);

        Object gtname = queryParam.getFilter().get("gtname");

        if (gtname == null || "".equals(gtname))
        {

            return ResultModel.fail(ResultModel.CODE_PARAM_MISSING, ResultModel.CODE_PARAM_MISSING_DESC);

        }

        if (queryParam.getUserId() == null)
        {

            return ResultModel.fail(ResultModel.CODE_PARAM_MISSING, ResultModel.CODE_PARAM_MISSING_DESC);

        }
        
        
        Map<String, Object> groupTerminalByName = gtDao.getGroupTerminalByName(gtname.toString());//终端组名称唯一性校验
        
        
        if(groupTerminalByName!=null)
        {
            return ResultModel.fail(ResultModel.CODE_DATA_REPEAT, ResultModel.CODE_DATA_REPEAT_DESC);
        }
        
        Map<String, Object> pmaps = new HashMap<>();

        pmaps.put("gtname", queryParam.getFilter().get("gtname"));

        pmaps.put("remark", queryParam.getFilter().get("remark"));

        pmaps.put("userID", queryParam.getUserId());

        Long result = gtDao.addGroupTerminal(pmaps);

        if (result > 0)
        {
            return ResultModel.success(result, null);
        }

        return ResultModel.fail(ResultModel.CODE_SYSTEM_ERROR, ResultModel.CODE_SYSTEM_ERROR_DESC);
    }

    /**
     * (非 Javadoc)
     * <p>
     * Title: editGroupTerminal
     * </p>
     * <p>
     * Description:编辑终端组信息，终端组名称和备注目前都是可是编辑的
     * </p>
     * 
     * @param param
     * @return
     * @see com.bp.notice.business.service.GroupTerminalService#editGroupTerminal(java.lang.String)
     */
    @Override
    public ResultModel editGroupTerminal(String param)
    {

        QueryParam queryParam = JSON.parseObject(param, QueryParam.class);

        Object gtname = queryParam.getFilter().get("gtname");
        Object gtID = queryParam.getFilter().get("gtId");

        if (gtID == null || "".equals(gtID))
        {

            return ResultModel.fail(ResultModel.CODE_PARAM_MISSING, ResultModel.CODE_PARAM_MISSING_DESC);

        }

        if (gtname == null || "".equals(gtname))
        {

            return ResultModel.fail(ResultModel.CODE_PARAM_MISSING, ResultModel.CODE_PARAM_MISSING_DESC);

        }

        if (queryParam.getUserId() == null)
        {

            return ResultModel.fail(ResultModel.CODE_PARAM_MISSING, ResultModel.CODE_PARAM_MISSING_DESC);

        }
        
        Map<String, Object> groupTerminalById = gtDao.getGroupTerminalById(gtID);
        
        Object oldGroupTerminalName = groupTerminalById.get("group_name");
        
        if(!oldGroupTerminalName.equals(gtname))//比较原先的终端组名称和修改的终端组名称，不一样的时候才做校验
        {
            Map<String, Object> groupTerminalByName = gtDao.getGroupTerminalByName(gtname.toString());//终端组名称唯一性校验
            
            
            if(groupTerminalByName!=null)
            {
                return ResultModel.fail(ResultModel.CODE_DATA_REPEAT, ResultModel.CODE_DATA_REPEAT_DESC);
            }
        }
        
        Map<String, Object> pmaps = new HashMap<>();

        pmaps.put("gtname", queryParam.getFilter().get("gtname"));

        pmaps.put("remark", queryParam.getFilter().get("remark"));

        pmaps.put("gtId", gtID);

        Long result = gtDao.editGroupTerminal(pmaps);

        if (result > 0)
        {
            return ResultModel.success(result, null);
        }

        return ResultModel.fail(ResultModel.CODE_DATA_NOT_EXIST, ResultModel.CODE_DATA_NOT_EXIST_DESC);

    }

    /**
     * (非 Javadoc)
     * <p>
     * Title: deleteGroupTerminal
     * </p>
     * <p>
     * Description:删除终端组（同时要把绑定在该终端组的终端的外键要清空）
     * </p>
     * 
     * @param param
     * @return
     * @see com.bp.notice.business.service.GroupTerminalService#deleteGroupTerminal(java.lang.String)
     */
    @Transactional
    @Override
    public ResultModel deleteGroupTerminal(String param)
    {

        QueryParam queryParam = JSON.parseObject(param, QueryParam.class);
        Object gtID = queryParam.getFilter().get("gtId");

        if (gtID == null || "".equals(gtID))
        {

            return ResultModel.fail(ResultModel.CODE_PARAM_MISSING, ResultModel.CODE_PARAM_MISSING_DESC);

        }

        Long result = gtDao.deleteGroupTerminal(gtID);

        if (result <= 0)
        {
            return ResultModel.fail(ResultModel.CODE_SYSTEM_ERROR, ResultModel.CODE_SYSTEM_ERROR_DESC);
        }
        Long deleteGroupRelation = gtDao.deleteGroupRelation(gtID);

        logger.trace(queryParam.getUserId() + "清除通报组和终端组关系表====清除条数为：" + deleteGroupRelation);

        Map<String, Object> params = new HashMap<>();
        params.put("gtId", gtID);
        List<Map<String, Object>> queryTerminalBygtID = gtDao.queryTerminalBygtID(params);

        if (queryTerminalBygtID == null || queryTerminalBygtID.size() <= 0)//终端组下面没有绑定终端可以直接删除
        {

            return ResultModel.success(result, null);

        }

        Long deleteBind = gtDao.deleteBind(gtID);

        if (deleteBind <= 0)
        {

            throw new RuntimeException("清除外键关系失败");

            //return ResultModel.fail(ResultModel.CODE_SYSTEM_ERROR, ResultModel.CODE_SYSTEM_ERROR_DESC);
        }

        return ResultModel.success(result, null);
    }

    /**
     * (非 Javadoc)
     * <p>
     * Title: queryGroupTerminals
     * </p>
     * <p>
     * Description:
     * </p>
     * 
     * @param param
     * @return
     * @see com.bp.notice.business.service.GroupTerminalService#queryGroupTerminals(java.lang.String)
     */
    @Override
    public ResultModel queryGroupTerminals(String param)
    {
        QueryParam queryParam = JSON.parseObject(param, QueryParam.class);

        Map<String, Object> pmaps = new HashMap<>();

        pmaps.put("gtname", queryParam.getFilter().get("gtname"));
        pmaps.put("page", queryParam.getPage());
        pmaps.put("size", queryParam.getSize());

        List<GroupTerminal> result = new ArrayList<>();
        List<Map<String, Object>> queryGroupTerminal = gtDao.queryGroupTerminal(pmaps);
        Long count = gtDao.countGroupTerminal(pmaps);

        if (queryGroupTerminal != null && queryGroupTerminal.size() > 0)
        {

            for (Map<String, Object> map : queryGroupTerminal)

            {
                GroupTerminal groupTerminal = new GroupTerminal();

                groupTerminal.setId((Long)map.get("id"));
                groupTerminal.setGtname(map.get("group_name") != null ? map.get("group_name").toString() : null);

                groupTerminal.setRemark(map.get("remark") != null ? map.get("remark").toString() : null);
                groupTerminal.setInTime(map.get("in_time") != null ? (Date)map.get("in_time") : null);

                List<Map<String, Object>> queryGroupBygtID = gtDao.queryGroupBygtID((Long)map.get("id"));
                groupTerminal.setNoticegroups(queryGroupBygtID);
                result.add(groupTerminal);

            }

        }

        return ResultModel.success(count, result);
    }

    /**
     * (非 Javadoc)
     * 根据通知组ID查询对应联系(多对多的关系)，此接口和通报组查询联系人公用
     * <p>
     * Description:
     * </p>
     * 
     * @param param
     * @return
     * @see com.bp.notice.business.service.GroupTerminalService#queryContactByGID(java.lang.String)
     */
    @Override
    public ResultModel queryContactByGID(String param)
    {

        QueryParam queryParam = JSON.parseObject(param, QueryParam.class);

        Map<String, Object> pmaps = new HashMap<>();

        if (queryParam.getFilter().get("gid") == null)
        {

            return ResultModel.fail(ResultModel.CODE_PARAM_MISSING, ResultModel.CODE_PARAM_MISSING_DESC);

        }
        pmaps.put("gid", queryParam.getFilter().get("gid"));
        pmaps.put("page", queryParam.getPage());
        pmaps.put("size", queryParam.getSize());

        List<Map<String, Object>> queryContactByGID = gtDao.queryContactByGID(pmaps);

        return ResultModel.success(gtDao.countContactByGID(pmaps), queryContactByGID);
    }

    /**
     * (非 Javadoc)
     * <p>
     * Title: queryTerminalBygtID
     * </p>
     * <p>
     * Description:
     * </p>
     * 
     * @param param
     * @return
     * @see com.bp.notice.business.service.GroupTerminalService#queryTerminalBygtID(java.lang.String)
     */
    @Override
    public ResultModel queryTerminalBygtID(String param)
    {
        QueryParam queryParam = JSON.parseObject(param, QueryParam.class);

        Map<String, Object> pmaps = new HashMap<>();

        if (queryParam.getFilter().get("gtId") == null)
        {

            return ResultModel.fail(ResultModel.CODE_PARAM_MISSING, ResultModel.CODE_PARAM_MISSING_DESC);

        }
        pmaps.put("gtId", queryParam.getFilter().get("gtId"));
        pmaps.put("page", queryParam.getPage());
        pmaps.put("size", queryParam.getSize());

        List<Map<String, Object>> queryTerminalBygtID = gtDao.queryTerminalBygtID(pmaps);

        List<Map<String, Object>> results = new ArrayList<>();

        if (queryTerminalBygtID != null && queryTerminalBygtID.size() > 0)
        {
            for (Map<String, Object> map : queryTerminalBygtID)

            {
                Map<String, Object> result = new HashMap<>();

                result.put("id", map.get("id"));

                result.put("mac", map.get("mac"));

                result.put("remark", map.get("remark"));
                result.put("inTime", map.get("in_time"));
                results.add(result);
            }

        }
        return ResultModel.success(gtDao.countTerminalBygtID(pmaps), results);
    }

    /**
     * (非 Javadoc)
     * <p>
     * Title: queryRelationMac
     * </p>
     * <p>
     * Description:
     * </p>
     * 
     * @param param
     * @return
     * @see com.bp.notice.business.service.GroupTerminalService#queryRelationMac(java.lang.String)
     */
    @Override
    public ResultModel queryRelationMac(String param)
    {

        QueryParam queryParam = JSON.parseObject(param, QueryParam.class);

        Map<String, Object> pmaps = new HashMap<>();

        if (queryParam.getFilter().get("gtId") == null)
        {

            return ResultModel.fail(ResultModel.CODE_PARAM_MISSING, ResultModel.CODE_PARAM_MISSING_DESC);

        }
        pmaps.put("gtId", queryParam.getFilter().get("gtId"));

        List<Map<String, Object>> querybinderMacs = gtDao.queryTerminalBygtID(pmaps);

        List<Map<String, Object>> binderMacs = new ArrayList<>();
        if (querybinderMacs != null && querybinderMacs.size() > 0)
        {
        		for (Map<String, Object> map : querybinderMacs)
              {
        			Map<String, Object> result = new HashMap<>();
        			result.put("mac", map.get("mac"));
        			result.put("remark", map.get("remark"));
        			binderMacs.add(result);
              }
         }
//        List<String> binderMacs = new ArrayList<>();
//        if (querybinderMacs != null && querybinderMacs.size() > 0)
//        {
//            for (Map<String, Object> map : querybinderMacs)
//
//            {
//
//                binderMacs.add(map.get("mac").toString());
//            }
//        }
        
        List<Map<String, Object>> queryunBindMacs = gtDao.unBindMacs(pmaps);

        queryunBindMacs.removeAll(querybinderMacs);
        
        List<Map<String, Object>> unbinderMacs = new ArrayList<>();
        if (queryunBindMacs != null && queryunBindMacs.size() > 0)
        {
        		for (Map<String, Object> map : queryunBindMacs)
              {
        			Map<String, Object> result = new HashMap<>();
        			result.put("mac", map.get("mac"));
        			result.put("remark", map.get("remark"));
        			unbinderMacs.add(result);
              }
         }
//        List<String> unbinderMacs = new ArrayList<>();
//        if (queryunBindMacs != null && queryunBindMacs.size() > 0)
//        {
//            for (Map<String, Object> map : queryunBindMacs)
//
//            {
//
//                unbinderMacs.add(map.get("mac").toString());
//            }
//        }

        List<Map<String, Object>> results = new ArrayList<>();

        Map<String, Object> result = new HashMap<>();

        result.put("bindMacs", binderMacs);

        result.put("unbindMacs", unbinderMacs);
        results.add(result);
        return ResultModel.success(results.size(), results);
    }

    /**
     * (非 Javadoc)
     * <p>
     * Title: bind
     * </p>
     * 终端和终端组绑定过程
     * 
     * @return
     * @throws Exception
     * @see com.bp.notice.business.service.GroupTerminalService#bind()
     */
    @Override
    public ResultModel bind(String param)
    {

        QueryParam queryParam = JSON.parseObject(param, QueryParam.class);

        Object object = queryParam.getFilter().get("gtId");
        if (object == null)
        {
            return ResultModel.fail(ResultModel.CODE_PARAM_MISSING, ResultModel.CODE_PARAM_MISSING_DESC);
        }

        Long gtID = ((Integer)object).longValue();
        Object objMacs = queryParam.getFilter().get("macs");

        if (objMacs == null)
        {
            return ResultModel.fail(ResultModel.CODE_PARAM_MISSING, ResultModel.CODE_PARAM_MISSING_DESC);
        }

        JSONArray macs = (JSONArray)objMacs;

        int errorcount = 0;
        List<String> errmacs = new ArrayList<>();

        Long deleteBind = gtDao.deleteBind(gtID);//清除之前的绑定关系

        logger.trace(queryParam.getUserId() + "清除之前的绑定关系" + deleteBind);
        for (int i = 0; i < macs.size(); i++)
        {

            String mac = macs.getString(i);

            Long result = gtDao.bind(gtID, mac);
            if (result <= 0)
            {
                errorcount++;
                errmacs.add(mac);
            }

        }

        return ResultModel.success(errorcount, errmacs);
    }

}
