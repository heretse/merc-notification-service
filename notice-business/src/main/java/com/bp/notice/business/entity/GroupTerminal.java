/**
 * @FileName: GroupTerminal.java
 * @PackageName com.bp.notice.business.entity
 * @Description: TODO(用一句话描述该文件做什么)
 * @author MT
 * @date 2018年2月3日 上午10:42:54
 * @version
 */

package com.bp.notice.business.entity;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @ClassName: GroupTerminal
 * @Description:终端组类
 * @author MT
 * @date 2018年2月3日 上午10:42:54
 */
public class GroupTerminal
{

    private Long id;

    private String gtname;

    private Date inTime;

    private String remark;

    private List<Map<String, Object>> noticegroups;

    public Long getId()
    {
        return id;
    }

    public void setId(Long id)
    {
        this.id = id;
    }

    public String getGtname()
    {
        return gtname;
    }

    public void setGtname(String gtname)
    {
        this.gtname = gtname;
    }

    public Date getInTime()
    {
        return inTime;
    }

    public void setInTime(Date inTime)
    {
        this.inTime = inTime;
    }

    public String getRemark()
    {
        return remark;
    }

    public void setRemark(String remark)
    {
        this.remark = remark;
    }

    public List<Map<String, Object>> getNoticegroups()
    {
        return noticegroups;
    }

    public void setNoticegroups(List<Map<String, Object>> noticegroups)
    {
        this.noticegroups = noticegroups;
    }

}
