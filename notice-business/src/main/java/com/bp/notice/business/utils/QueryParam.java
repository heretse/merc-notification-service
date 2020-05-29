/**
 * @FileName: QueryParam.java
 * @PackageName com.bp.notice.business.utils
 * @Description: TODO(用一句话描述该文件做什么)
 * @author MT
 * @date 2018年1月30日 上午11:00:27
 * @version
 */

package com.bp.notice.business.utils;

import java.util.HashMap;
import java.util.Map;

/**
 * @ClassName: QueryParam
 * @Description: TODO(这里用一句话描述这个类的作用)
 * @author MT
 * @date 2018年1月30日 上午11:00:27
 */
public class QueryParam
{

    private Map<String, Object> filter = new HashMap<>();

    private Integer page;

    private Integer size;

    private Integer userId;

    public Map<String, Object> getFilter()
    {
        return filter;
    }

    public void setFilter(Map<String, Object> filter)
    {
        this.filter = filter;
    }

    public Integer getPage()
    {
        return page;
    }

    public void setPage(Integer page)
    {
        this.page = page;
    }

    public Integer getSize()
    {
        return size;
    }

    public void setSize(Integer size)
    {
        this.size = size;
    }

    public Integer getUserId()
    {
        return userId;
    }

    public void setUserId(Integer userId)
    {
        this.userId = userId;
    }

}
