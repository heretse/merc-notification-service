package com.bp.freepp.notification.model;

import javax.persistence.*;
import java.util.Date;

@Entity
public class NotificationData {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "user_id", nullable=true)
    private Integer userId;

    @Column(name = "app_id", nullable=true)
    private String appId;

    @Column(name = "notification_group", nullable=true)
    private String notificationGroup;

    @Column(name = "terminal_group", nullable=true)
    private String terminalGroup;

    @Column(name = "mac", nullable=true)
    private String mac;

    @Column(name = "description", nullable=true)
    private String description;

    @Column(name = "extra", nullable=true)
    private String extra;

    @Column(name = "recv", nullable=true)
    private Date receiveTime;

    @Column(name = "delay", nullable=true)
    private Long delay;

    @Column(name = "delivery_time", nullable=true)
    private Date deliveryTime;

    @Column(name = "delivery_sucess", nullable=true)
    private Integer deliverySucess;

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

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getNotificationGroup() {
        return notificationGroup;
    }

    public void setNotificationGroup(String notificationGroup) {
        this.notificationGroup = notificationGroup;
    }

    public String getTerminalGroup() {
        return terminalGroup;
    }

    public void setTerminalGroup(String terminalGroup) {
        this.terminalGroup = terminalGroup;
    }

    public String getMac() {
        return mac;
    }

    public void setMac(String mac) {
        this.mac = mac;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getExtra() {
        return extra;
    }

    public void setExtra(String extra) {
        this.extra = extra;
    }

    public Date getReceiveTime() {
        return receiveTime;
    }

    public void setReceiveTime(Date receiveTime) {
        this.receiveTime = receiveTime;
    }

    public Long getDelay() {
        return delay;
    }

    public void setDelay(Long delay) {
        this.delay = delay;
    }

    public Date getDeliveryTime() {
        return deliveryTime;
    }

    public void setDeliveryTime(Date deliveryTime) {
        this.deliveryTime = deliveryTime;
    }

    public Integer getDeliverySucess() {
        return deliverySucess;
    }

    public void setDeliverySucess(Integer deliverySucess) {
        this.deliverySucess = deliverySucess;
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
