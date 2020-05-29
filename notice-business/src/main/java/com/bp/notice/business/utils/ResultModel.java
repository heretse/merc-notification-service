/**
 * @FileName: ResultModelTools.java
 * @PackageName com.force4us.utils
 * @Description: TODO(用一句话描述该文件做什么)
 * @author MT
 * @date 2018年1月22日 上午10:48:18
 * @version
 */

package com.bp.notice.business.utils;

import java.util.List;

/**
 * @ClassName: ResultModelTools
 * @Description: 返回值工具类
 * @author MT
 * @date 2018年1月22日 上午10:48:18
 */

public class ResultModel
{

    public static Integer CODE_SUCCESS = 1000;
    public static String CODE_SUCCESS_DESC = "success";
    public static Integer CODE_FORMAT_ERROR = 1001;
    public static String CODE_FORMAT_ERROR_DESC = "invalid format";
    public static Integer CODE_PARAM_MISSING = 1002;
    public static String CODE_PARAM_MISSING_DESC = "missing  param";
    public static Integer CODE_DATA_NOT_EXIST = 1003;
    public static String CODE_DATA_NOT_EXIST_DESC = "no data";
    public static Integer CODE_DATA_REPEAT = 1004;
    public static String CODE_DATA_REPEAT_DESC = "the data exist";
    public static Integer CODE_SYSTEM_ERROR = 9999;
    public static String CODE_SYSTEM_ERROR_DESC = "system error";

    private Integer code;

    private String desc;

    private Object count;

    private List<?> data;

    public Integer getCode()
    {
        return code;
    }

    public void setCode(Integer code)
    {
        this.code = code;
    }

    public String getDesc()
    {
        return desc;
    }

    public void setDesc(String desc)
    {
        this.desc = desc;
    }

    public Object getCount()
    {
        return count;
    }

    public void setCount(Object count)
    {
        this.count = count;
    }

    public List<?> getData()
    {
        return data;
    }

    public void setData(List<?> data)
    {
        this.data = data;
    }

    public ResultModel()
    {
        super();
    }

    public ResultModel(Integer code, String desc, Object count, List<?> data)
    {
        super();
        this.code = code;
        this.desc = desc;
        this.count = count;
        this.data = data;
    }

    public static ResultModel fail(Integer code, String desc)
    {

        return new ResultModel(code, desc, 0, null);

    }

    public static ResultModel success(Object count, List<?> data)
    {

        return new ResultModel(CODE_SUCCESS, CODE_SUCCESS_DESC, count, data);

    }

}