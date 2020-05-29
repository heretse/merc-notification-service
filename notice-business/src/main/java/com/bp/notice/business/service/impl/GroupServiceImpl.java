/**
 * @FileName: GroupServiceImpl.java
 * @PackageName com.bp.notice.business.service.impl
 * @Description: TODO(用一句话描述该文件做什么)
 * @author MT
 * @date 2018年2月6日 下午1:56:25
 * @version
 */

package com.bp.notice.business.service.impl;

import java.util.ArrayList;

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
import com.bp.notice.business.dao.GroupDao;
import com.bp.notice.business.dao.GroupTerminalDao;
import com.bp.notice.business.service.GroupService;
import com.bp.notice.business.utils.QueryParam;
import com.bp.notice.business.utils.ResultModel;

/**
 * @ClassName: GroupServiceImpl
 * @Description: 通报组管理服务类
 * @author MT
 * @date 2018年2月6日 下午1:56:25
 */
@Service
public class GroupServiceImpl implements GroupService
{

    private Logger logger = LogManager.getLogger(LogManager.ROOT_LOGGER_NAME);

    @Autowired
    GroupDao dao;

    @Autowired

    GroupTerminalDao gtdao;

    /**
     * (非 Javadoc)
     * <p>
     * Title: query
     * </p>
     * 查询通报组列表(带分页)
     * 
     * @param param
     * @return
     * @see com.bp.notice.business.service.GroupService#query(java.lang.String)
     */
    @Override
    public ResultModel query(String param)
    {

        QueryParam queryParam = JSON.parseObject(param, QueryParam.class);

        Map<String, Object> pmaps = new HashMap<>();

        pmaps.put("gname", queryParam.getFilter().get("gname"));
        pmaps.put("page", queryParam.getPage());
        pmaps.put("size", queryParam.getSize());

        List<Map<String, Object>> query = dao.query(pmaps);

        List<Map<String, Object>> results = new ArrayList<>();

        if (query != null && query.size() > 0)
        {
            for (Map<String, Object> map : query)
            {
                Map<String, Object> result = new HashMap<>();

                result.put("id", map.get("id"));
                result.put("gname", map.get("group_name"));
                result.put("inTime", map.get("in_time"));
                result.put("delayTime", map.get("delay"));
                result.put("createUser", map.get("user_name"));
                results.add(result);
            }

        }

        Long count = dao.count(pmaps);

        return ResultModel.success(count, results);

    }

    /**
     * (非 Javadoc)
     * <p>
     * Title: queryGTByID
     * </p>
     * 根据通报组id获得关联的终端组（多对多关系）
     * 
     * @param param
     * @return
     * @see com.bp.notice.business.service.GroupService#queryGTByID(java.lang.String)
     */
    @Override
    public ResultModel queryGTByID(String param)
    {

        QueryParam queryParam = JSON.parseObject(param, QueryParam.class);

        Map<String, Object> pmaps = new HashMap<>();

        Object gid = queryParam.getFilter().get("gid");

        if (gid == null)
        {

            return ResultModel.fail(ResultModel.CODE_PARAM_MISSING, ResultModel.CODE_PARAM_MISSING_DESC);
        }

        pmaps.put("gid", gid);
        pmaps.put("page", queryParam.getPage());
        pmaps.put("size", queryParam.getSize());

        List<Map<String, Object>> query = dao.queryGTByID(pmaps);

        List<Map<String, Object>> results = new ArrayList<>();

        if (query != null && query.size() > 0)
        {
            for (Map<String, Object> map : query)
            {
                Map<String, Object> result = new HashMap<>();

                result.put("id", map.get("id"));
                result.put("gtname", map.get("group_name"));
                result.put("remark", map.get("remark"));
                result.put("inTime", map.get("in_time"));
                results.add(result);
            }

        }

        Long count = dao.countqueryGTByID(pmaps);

        return ResultModel.success(count, results);
    }

    /**
     * (非 Javadoc)
     * <p>
     * Title: queryBindDatas
     * </p>
     * <p>
     * Description:
     * </p>
     * 
     * @param param
     * @return
     * @see com.bp.notice.business.service.GroupService#queryBindDatas(java.lang.String)
     */
    @Override
    public ResultModel queryBindDatas(String param)
    {

        QueryParam queryParam = JSON.parseObject(param, QueryParam.class);

        Object gid = queryParam.getFilter().get("gid");

        if (gid == null)
        {

            return ResultModel.fail(ResultModel.CODE_PARAM_MISSING, ResultModel.CODE_PARAM_MISSING_DESC);
        }

        Map<String, Object> bindpmaps = new HashMap<>();
        bindpmaps.put("gid", gid);
        //开始查询绑定终端组-start
        List<Map<String, Object>> bindGroupTerminals = dao.queryGTByID(bindpmaps);

        List<Map<String, Object>> resultsbindGroupTerminals = new ArrayList<>();

        if (bindGroupTerminals != null && bindGroupTerminals.size() > 0)
        {
            for (Map<String, Object> map : bindGroupTerminals)
            {
                Map<String, Object> result = new HashMap<>();

                result.put("gtId", map.get("id"));
                result.put("gtname", map.get("group_name"));
                resultsbindGroupTerminals.add(result);
            }

        }
        //开始查询绑定终端组-end

        List<Map<String, Object>> resultUnbindTerminals = getUnbinderTerminals(bindpmaps);//查询未绑定的终端组

        //开始查询绑定联系人-start
        List<Map<String, Object>> queryContactByGID = gtdao.queryContactByGID(bindpmaps);//查询绑定的联系人

        List<Map<String, Object>> resultsbindContacts = new ArrayList<>();

        if (queryContactByGID != null && queryContactByGID.size() > 0)
        {
            for (Map<String, Object> map : queryContactByGID)
            {
                Map<String, Object> result = new HashMap<>();

                result.put("cid", map.get("id"));
                result.put("cname", map.get("contactName"));
                resultsbindContacts.add(result);
            }

        }
        //开始查询绑定联系人-end

        List<Map<String, Object>> resultUnBindContacts = getUnbinderContacts(bindpmaps);//查询未绑定的联系人

        List<Map<String, Object>> finalresultLists = new ArrayList<>();//拼装返回结果

        Map<String, Object> finalresult = new HashMap<>();

        finalresult.put("bindGroupTerminals", resultsbindGroupTerminals);
        finalresult.put("unbindGroupTerminals", resultUnbindTerminals);
        finalresult.put("bindContacts", resultsbindContacts);
        finalresult.put("unbindContacts", resultUnBindContacts);

        finalresultLists.add(finalresult);

        return ResultModel.success(finalresultLists.size(), finalresultLists);
    }

    /**
     * @Title: getUnbinderTerminals
     * @Description: 查询总的终端组，然后把当前绑定的终端组给去除
     * @param @param bindTermials
     * @param @return 设定文件
     * @return List<Map<String,Object>> 返回类型
     * @throws
     */
    private List<Map<String, Object>> getUnbinderTerminals(Map<String, Object> unbindpmaps)
    {

        List<Map<String, Object>> queryUnGroupTerminal = dao.queryUNGTByID(unbindpmaps);

        return queryUnGroupTerminal;
    }

    /**
     * @Title: getUnbinderTerminals
     * @Description: 查询总的终端组，然后把当前绑定的终端组给去除
     * @param @param bindTermials
     * @param @return 设定文件
     * @return List<Map<String,Object>> 返回类型
     * @throws
     */
    private List<Map<String, Object>> getUnbinderContacts(Map<String, Object> unbindpmaps)
    {

        List<Map<String, Object>> queryUnBindContacts = dao.queryUNBindContactsByID(unbindpmaps);

        return queryUnBindContacts;
    }

    /**
     * (非 Javadoc)
     * <p>
     * Title: bindData
     * </p>
     * //根据通报组ID开始绑定联系人和终端组相关信息(启用事务操作)
     * 
     * @param param
     * @return
     * @see com.bp.notice.business.service.GroupService#bindData(java.lang.String)
     */

    @Transactional
    @Override
    public ResultModel bindData(String param)
    {

        QueryParam queryParam = JSON.parseObject(param, QueryParam.class);

        Object gid = queryParam.getFilter().get("gid");

        if (gid == null)
        {

            return ResultModel.fail(ResultModel.CODE_PARAM_MISSING, ResultModel.CODE_PARAM_MISSING_DESC);
        }

        Object gtids = queryParam.getFilter().get("gtids");
        Object cids = queryParam.getFilter().get("cids");

        if (gtids != null)//维护通报组和终端组的关系
        {

            JSONArray jsongtids = (JSONArray)gtids;
            Long deleteGroupTerminalRelelation = dao.deleteGroupTerminalRelelation(gid);//step 1:清除维护关系

            logger.trace(queryParam.getUserId() + "清除维护关系" + "通报组ID为" + gid + "删除通报和终端组关系记录条数：" + deleteGroupTerminalRelelation);
            if (jsongtids.size() > 0)//step2: 重新维护通报组和终端组的关系
            {

                for (int i = 0; i < jsongtids.size(); i++)
                {
                    Long insertGroupTerminalRelelation = dao.insertGroupTerminalRelelation(gid, jsongtids.getLong(i));
                    if (insertGroupTerminalRelelation > 0)
                    {
                        logger.trace(queryParam.getUserId() + "重新维护终端组关系成功!" + "通报组ID为：" + gid + ";终端组ID：" + jsongtids.getLong(i));
                    }
                    else
                    {
                        logger.trace(queryParam.getUserId() + "重新维护终端组关系失败!" + "通报组ID为：" + gid + ";终端组ID：" + jsongtids.getLong(i));
                    }

                }

            }
        }

        if (cids != null)//维护通报组和联系人的关系
        {

            JSONArray jsoncids = (JSONArray)cids;
            Long deleteContactRelelation = dao.deleteContactRelelation(gid);//step 1:清除维护关系

            logger.trace(queryParam.getUserId() + "清除维护关系" + "通报组ID为：" + gid + "删除通报和联系人关系记录条数：" + deleteContactRelelation);
            if (jsoncids.size() > 0)//step2: 重新维护通报组和联系人的关系
            {

                for (int i = 0; i < jsoncids.size(); i++)
                {
                    Long insertContactRelelation = dao.insertContactRelelation(gid, jsoncids.getLong(i));
                    if (insertContactRelelation > 0)
                    {
                        logger.trace(queryParam.getUserId() + "重新维护联系人关系成功!" + "通报组ID为" + gid + ";联系人ID：" + jsoncids.getLong(i));
                    }
                    else
                    {
                        logger.trace(queryParam.getUserId() + "重新维护联系人关系失败!" + "通报组ID为" + gid + ";联系人ID：" + jsoncids.getLong(i));
                    }

                }

            }
        }

        return ResultModel.success(1, null);
    }

    /**
     * (非 Javadoc)
     * <p>
     * Title: add
     * </p>
     * 新增通报组
     * 
     * @param param
     * @return
     * @see com.bp.notice.business.service.GroupService#add(java.lang.String)
     */
    @Override
    public ResultModel add(String param)
    {

        QueryParam queryParam = JSON.parseObject(param, QueryParam.class);

        Object gname = queryParam.getFilter().get("gname");

        if (gname == null || "".equals(gname))
        {

            return ResultModel.fail(ResultModel.CODE_PARAM_MISSING, ResultModel.CODE_PARAM_MISSING_DESC);

        }
        
        Map<String, Object> groupByName = dao.getGroupByName(gname.toString());//通报组名称唯一性校验
        
        
        if(groupByName!=null)
        {
            return ResultModel.fail(ResultModel.CODE_DATA_REPEAT, ResultModel.CODE_DATA_REPEAT_DESC);
        }
        
        //如果延迟时间不传入为null则默认时间为0
        Integer delaytime = queryParam.getFilter().get("delaytime") != null ? (Integer)queryParam.getFilter().get("delaytime") : 0;

        Map<String, Object> params = new HashMap<>();
        params.put("gname", gname);
        params.put("userID", queryParam.getUserId());
        params.put("delaytime", delaytime);
        Long add = dao.add(params);

        if (add > 0)
        {
            logger.trace(queryParam.getUserId() + "新增一条通报组成功;参数为：" + params);

            return ResultModel.success(add, null);

        }
        else
        {
            logger.trace(queryParam.getUserId() + "新增一条通报组失败;参数为：" + params);
            return ResultModel.fail(ResultModel.CODE_SYSTEM_ERROR, ResultModel.CODE_SYSTEM_ERROR_DESC);
        }

    }

    /**
     * (非 Javadoc)
     * <p>
     * Title: edit
     * </p>
     * 修改通报组信息
     * 
     * @param param
     * @return
     * @see com.bp.notice.business.service.GroupService#edit(java.lang.String)
     */
    @Override
    public ResultModel edit(String param)
    {

        QueryParam queryParam = JSON.parseObject(param, QueryParam.class);

        Object gid = queryParam.getFilter().get("gid");

        if (gid == null || "".equals(gid))
        {

            return ResultModel.fail(ResultModel.CODE_PARAM_MISSING, ResultModel.CODE_PARAM_MISSING_DESC);

        }

        Object gname = queryParam.getFilter().get("gname");

        if (gname == null || "".equals(gname))
        {

            return ResultModel.fail(ResultModel.CODE_PARAM_MISSING, ResultModel.CODE_PARAM_MISSING_DESC);

        }
        
        Map<String, Object> groupById = dao.getGroupById(gid);
        
        Object oldGroupName =groupById.get("group_name");
        

        if(!oldGroupName.equals(gname))//比较原先的通报组名称和修改的通报组名称，不一样的时候才做校验
        {
            Map<String, Object> groupByName = dao.getGroupByName(gname.toString());//通报组名称唯一性校验
            
            if(groupByName!=null)
            {
                return ResultModel.fail(ResultModel.CODE_DATA_REPEAT, ResultModel.CODE_DATA_REPEAT_DESC);
            } 
        }
        
        //如果延迟时间不传入为null则默认时间为0
        Integer delaytime = queryParam.getFilter().get("delaytime") != null ? (Integer)queryParam.getFilter().get("delaytime") : 0;

        Map<String, Object> params = new HashMap<>();
        params.put("gid", gid);
        params.put("gname", gname);
        params.put("userID", queryParam.getUserId());
        params.put("delaytime", delaytime);
        Long edit = dao.edit(params);

        if (edit > 0)
        {
            logger.trace(queryParam.getUserId() + "编辑一条通报组成功;参数为：" + params);

            return ResultModel.success(edit, null);

        }
        else
        {
            logger.trace(queryParam.getUserId() + "编辑一条通报组失败;参数为：" + params);
            return ResultModel.fail(ResultModel.CODE_DATA_NOT_EXIST, ResultModel.CODE_DATA_NOT_EXIST_DESC);
        }

    }

    /**
     * (非 Javadoc)
     * <p>
     * Title: delete
     * </p>
     * 删除通报组的时候同时清除终端组和联系人的关系
     * 
     * @param param
     * @return
     * @see com.bp.notice.business.service.GroupService#delete(java.lang.String)
     */
    @Transactional
    @Override
    public ResultModel delete(String param)
    {
        QueryParam queryParam = JSON.parseObject(param, QueryParam.class);

        Object gid = queryParam.getFilter().get("gid");

        if (gid == null || "".equals(gid))
        {

            return ResultModel.fail(ResultModel.CODE_PARAM_MISSING, ResultModel.CODE_PARAM_MISSING_DESC);

        }

        Long delete = dao.delete(gid);//删除通报组

        if (delete > 0)//通报组删除成功才会去清除联系人和终端组的关系
        {

            Long deleteContactRelelation = dao.deleteContactRelelation(gid);//清空联系人关系

            Long deleteGroupTerminalRelelation = dao.deleteGroupTerminalRelelation(gid);//清空终端组关系

            logger.trace(queryParam.getUserId() + "删除通报组为gid:" + gid + "记录;并且清空联系人关系表,清空记录为：" + deleteContactRelelation + ";清空终端组关系表，清空记录为："
                    + deleteGroupTerminalRelelation);

            return ResultModel.success(delete, null);
        }
        else
        {

            return ResultModel.fail(ResultModel.CODE_DATA_NOT_EXIST, ResultModel.CODE_DATA_NOT_EXIST_DESC);

        }

    }

}
