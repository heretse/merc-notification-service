/**
 * @FileName: ContactServiceImpl.java
 * @PackageName com.bp.notice.business.service.impl
 * @Description: TODO(用一句话描述该文件做什么)
 * @author MT
 * @date 2018年2月5日 下午3:08:20
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
import com.alibaba.fastjson.JSONObject;
import com.bp.notice.business.dao.ContactDao;
import com.bp.notice.business.service.ContactService;
import com.bp.notice.business.utils.QueryParam;
import com.bp.notice.business.utils.ResultModel;

/**
 * @ClassName: ContactServiceImpl
 * @Description: 联系人管理业务操作类
 * @author MT
 * @date 2018年2月5日 下午3:08:20
 */

@Service
public class ContactServiceImpl implements ContactService
{
    private Logger logger = LogManager.getLogger(LogManager.ROOT_LOGGER_NAME);

    @Autowired
    ContactDao dao;

    /**
     * (非 Javadoc)
     * <p>
     * Title: queryMethods
     * </p>
     * 查询联系方式
     * 
     * @return
     * @see com.bp.notice.business.service.ContactService#queryMethods()
     */
    @Override
    public ResultModel queryMethods(String param)
    {

        List<Map<String, Object>> queryMethods = dao.queryMethods();

        List<Map<String, Object>> results = new ArrayList<>();

        if (queryMethods != null && queryMethods.size() > 0)
        {

            for (Map<String, Object> map : queryMethods)
            {

                Map<String, Object> result = new HashMap<>();

                result.put("mid", map.get("id"));
                result.put("methodName", map.get("method"));
                results.add(result);
            }
        }

        return ResultModel.success(results.size(), results);
    }

    /**
     * (非 Javadoc)
     * <p>
     * Title: query
     * </p>
     * <p>
     * Description:
     * </p>
     * 
     * @param param
     * @return
     * @see com.bp.notice.business.service.ContactService#query(java.lang.String)
     */
    @Override
    public ResultModel query(String param)
    {

        QueryParam queryParam = JSON.parseObject(param, QueryParam.class);

        Map<String, Object> pmaps = new HashMap<>();

        pmaps.put("cname", queryParam.getFilter().get("cname"));
        pmaps.put("cid", queryParam.getFilter().get("cid"));
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
                result.put("contactName", map.get("object_name"));
//                result.put("contactMethod", map.get("notification_method"));
//                result.put("contactAccount", map.get("notification_account"));
                result.put("createName", map.get("user_name"));
                result.put("inTime", map.get("in_time"));
                result.put("remark", map.get("remark"));
                result.put("cmethods", dao.queryMethodsByCID(map.get("id")));
                results.add(result);
            }

        }

        Long count = dao.count(pmaps);

        return ResultModel.success(count, results);
    }

    /**
     * (非 Javadoc)
     * <p>
     * Title: add
     * </p>
     * 新增联系人和绑定联系方式(启用事务操作)
     * 
     * @param param
     * @return
     * @see com.bp.notice.business.service.ContactService#add(java.lang.String)
     */

    @Transactional
    @Override
    public ResultModel add(String param)
    {

        QueryParam queryParam = JSON.parseObject(param, QueryParam.class);

        Object cname = queryParam.getFilter().get("cname");

        if (cname == null || "".equals(cname))
        {
            return ResultModel.fail(ResultModel.CODE_PARAM_MISSING, ResultModel.CODE_PARAM_MISSING_DESC);

        }

        Object cmethods = queryParam.getFilter().get("cmethods");

        if (cmethods == null)//没有绑定联系方式
        {

            return ResultModel.fail(ResultModel.CODE_PARAM_MISSING, ResultModel.CODE_PARAM_MISSING_DESC);

        }
        if (queryParam.getUserId() == null)//用户未登录
        {

            return ResultModel.fail(ResultModel.CODE_PARAM_MISSING, ResultModel.CODE_PARAM_MISSING_DESC);

        }

        JSONArray jsonMethods = (JSONArray)cmethods;

        if (jsonMethods.size() == 0)//没有绑定联系方式
        {
            return ResultModel.fail(ResultModel.CODE_PARAM_MISSING, ResultModel.CODE_PARAM_MISSING_DESC);

        }
        Map<String, Object> pmaps = new HashMap<>();//插入联系人的参数信息，插入后要返回联系人的主键ID

        pmaps.put("cname", queryParam.getFilter().get("cname"));
        pmaps.put("remark", queryParam.getFilter().get("remark"));
        pmaps.put("userID", queryParam.getUserId());
        Integer userID = queryParam.getUserId();
        Long addResult = dao.add(pmaps);
        if (addResult > 0)
        {

            Long cid = (Long)pmaps.get("cid");//插入联系人的主键ID
            List<Map<String, Object>> params = new ArrayList<>();
            for (int i = 0; i < jsonMethods.size(); i++)
            {

                JSONObject jsonObject = jsonMethods.getJSONObject(i);
                
                Map<String, Object> checkIsExistAccount = dao.checkIsExistAccount(jsonObject.getString("caccount"), jsonObject.getInteger("mid"));
                if(checkIsExistAccount!=null)
                {
                    throw new RuntimeException("账号存在");
                }   
                Map<String, Object> paramInsert = new HashMap<>();

                paramInsert.put("userID", userID);
                paramInsert.put("cid", cid);

                paramInsert.put("mid", jsonObject.getLong("mid"));
                paramInsert.put("caccount", jsonObject.getString("caccount"));
                
                paramInsert.put("method", dao.getMethodByid(jsonObject.getLong("mid")));
                paramInsert.put("isEnabled", jsonObject.getInteger("isEnabled"));
                params.add(paramInsert);
            }

            Long insertRelaiton = dao.insertRelaiton(params);

            logger.trace("插入的条数" + insertRelaiton);
        }

        return ResultModel.success(1, null);
    }

    /**
     * (非 Javadoc)
     * <p>
     * Title: edit
     * </p>
     * 编辑联系人信息
     * 
     * @param param
     * @return
     * @see com.bp.notice.business.service.ContactService#edit(java.lang.String)
     */

    @Transactional
    @Override
    public ResultModel edit(String param)
    {

        QueryParam queryParam = JSON.parseObject(param, QueryParam.class);

        Object cid = queryParam.getFilter().get("cid");

        if (cid == null)
        {
            return ResultModel.fail(ResultModel.CODE_PARAM_MISSING, ResultModel.CODE_PARAM_MISSING_DESC);

        }

        Object cname = queryParam.getFilter().get("cname");

        if (cname == null || "".equals(cname))
        {
            return ResultModel.fail(ResultModel.CODE_PARAM_MISSING, ResultModel.CODE_PARAM_MISSING_DESC);

        }

        Object cmethods = queryParam.getFilter().get("cmethods");

        if (cmethods == null)//没有绑定联系方式
        {

            return ResultModel.fail(ResultModel.CODE_PARAM_MISSING, ResultModel.CODE_PARAM_MISSING_DESC);

        }
        if (queryParam.getUserId() == null)//用户未登录
        {

            return ResultModel.fail(ResultModel.CODE_PARAM_MISSING, ResultModel.CODE_PARAM_MISSING_DESC);

        }

        JSONArray jsonMethods = (JSONArray)cmethods;

        if (jsonMethods.size() == 0)//没有绑定联系方式
        {
            return ResultModel.fail(ResultModel.CODE_PARAM_MISSING, ResultModel.CODE_PARAM_MISSING_DESC);

        }
        Map<String, Object> pmaps = new HashMap<>();//
        pmaps.put("cid", cid);
        pmaps.put("cname", cname);
        pmaps.put("remark", queryParam.getFilter().get("remark"));

        Integer userID = queryParam.getUserId();
        Long editResult = dao.edit(pmaps);//更新联系人表的联系人姓名和备注信息

        if (editResult > 0)
        {

            List<Map<String, Object>> paramsInserts = new ArrayList<>();//保存要插入的集合（用户有可能新增联系方式）
            Long deleteRelationByCID = dao.deleteRelationByCID(cid);//维护联系方式表的时候需要先清空关系表
            logger.trace("删除的条数" + deleteRelationByCID, "删除的参数" + cid);
            for (int i = 0; i < jsonMethods.size(); i++)
            {

                JSONObject jsonObject = jsonMethods.getJSONObject(i);

                Map<String, Object> paramInsert = new HashMap<>();
                Map<String, Object> checkIsExistAccount = dao.checkIsExistAccount(jsonObject.getString("caccount"), jsonObject.getInteger("mid"));
                
                if(checkIsExistAccount!=null)
                {
                    throw new RuntimeException("账号存在");
                }   
                paramInsert.put("userID", userID);
                paramInsert.put("cid", cid);
                paramInsert.put("mid", jsonObject.getLong("mid"));
                paramInsert.put("caccount", jsonObject.getString("caccount"));
                paramInsert.put("method", dao.getMethodByid(jsonObject.getLong("mid")));
                paramInsert.put("isEnabled", jsonObject.getInteger("isEnabled"));
                paramsInserts.add(paramInsert);

            }

            if (paramsInserts.size() > 0)
            {
               
                Long insertRelaiton = dao.insertRelaiton(paramsInserts);
                logger.trace("插入的条数" + insertRelaiton + "插入参数" + paramsInserts);
            }

        }

        return ResultModel.success(1, null);

    }

    /**
     * (非 Javadoc)
     * <p>
     * Title: delete
     * </p>
     * 删除联系人同时清空联系方式表
     * 
     * @param param
     * @return
     * @see com.bp.notice.business.service.ContactService#delete(java.lang.String)
     */

    @Transactional
    @Override
    public ResultModel delete(String param)
    {

        QueryParam queryParam = JSON.parseObject(param, QueryParam.class);

        Object cid = queryParam.getFilter().get("cid");

        if (cid == null)
        {
            return ResultModel.fail(ResultModel.CODE_PARAM_MISSING, ResultModel.CODE_PARAM_MISSING_DESC);

        }

        Long delete = dao.delete(cid);

        if (delete <= 0)
        {
            logger.trace(queryParam.getUserId() + "删除联系人失败====" + cid);
            throw new RuntimeException("删除联系人失败");

        }

        Long deleteRelationByCID = dao.deleteRelationByCID(cid);//清除联系方式关系表

        logger.trace(queryParam.getUserId() + "清除联系方式关系表====清除条数为：" + deleteRelationByCID);

        Long deleteGroupRelation = dao.deleteGroupRelation(cid);

        logger.trace(queryParam.getUserId() + "根据联系人ID清除通报组和联系方式联关系表====清除条数为：" + deleteGroupRelation);

        return ResultModel.success(delete, null);
    }

}
