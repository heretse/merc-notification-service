/**
 * @FileName: Terminal.java
 * @PackageName com.bp.business.entity
 * @Description: TODO(用一句话描述该文件做什么)
 * @author MT
 * @date 2018年1月28日 下午3:05:44
 * @version
 */

package com.bp.notice.business.entity;

import java.util.Date;

/**
 * @ClassName: Terminal
 * @Description: TODO(这里用一句话描述这个类的作用)
 * @author MT
 * @date 2018年1月28日 下午3:05:44
 */
public class Terminal
{

    private Long id;

    private String mac;

    private Date createTime;
    private Date upTime;

    private String remark;

    public Date getUpTime()
    {
        return upTime;
    }

    public void setUpTime(Date upTime)
    {
        this.upTime = upTime;
    }

    private Integer userID;

    public Integer getUserID()
    {
        return userID;
    }

    public void setUserID(Integer userID)
    {
        this.userID = userID;
    }

    public String getRemark()
    {
        return remark;
    }

    public void setRemark(String remark)
    {
        this.remark = remark;
    }

    public Long getId()
    {
        return id;
    }

    public void setId(Long id)
    {
        this.id = id;
    }

    public String getMac()
    {
        return mac;
    }

    public void setMac(String mac)
    {
        this.mac = mac;
    }

    public Date getCreateTime()
    {
        return createTime;
    }

    public void setCreateTime(Date createTime)
    {
        this.createTime = createTime;
    }

    public Terminal(Long id, String mac, String remark, Integer userID, Date createTime)
    {
        super();
        this.id = id;
        this.mac = mac;
        this.remark = remark;
        this.userID = userID;
        this.createTime = createTime;
    }

    public Terminal()
    {
        super();
    }

}
