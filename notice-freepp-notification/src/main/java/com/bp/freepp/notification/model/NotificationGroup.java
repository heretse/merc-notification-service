package com.bp.freepp.notification.model;

import javax.persistence.*;
import java.util.Date;

@Entity
public class NotificationGroup {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "user_id", nullable=true)
    private Integer userId;

    @Column(name = "group_name", nullable=true)
    private String groupName;

    @Column(name = "delay", nullable=true)
    private Integer delay;

    @Column(name = "in_time", nullable=true)
    private Date inTime;

    @Column(name = "up_time", nullable=true)
    private Date upTime;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public Integer getDelay() {
        return delay;
    }

    public void setDelay(Integer delay) {
        this.delay = delay;
    }

    public Date getInTime() {
        return inTime;
    }

    public void setInTime(Date inTime) {
        this.inTime = inTime;
    }

    public Date getUpTime() {
        return upTime;
    }

    public void setUpTime(Date upTime) {
        this.upTime = upTime;
    }
}
