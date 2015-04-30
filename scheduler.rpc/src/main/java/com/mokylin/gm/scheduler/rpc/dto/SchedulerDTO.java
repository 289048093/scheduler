package com.mokylin.gm.scheduler.rpc.dto;

import java.io.Serializable;
import java.util.Date;

/**
 * @author 李朝(Li.Zhao)
 * @since 2015/1/12.
 */

public class SchedulerDTO implements Serializable {

    private Long id;

    private String cron;

    private String job;

    private String params;

    private Date createTime;

    private Date updateTime;

    private Date startTime;

    private Date endTime;

    private Boolean disabled;

    private JobStatus status;

    private String callbackURL;

    private String callbackVersion;

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public String getCallbackURL() {
        return callbackURL;
    }

    public void setCallbackURL(String callbackURL) {
        this.callbackURL = callbackURL;
    }

    public String getCallbackVersion() {
        return callbackVersion;
    }

    public void setCallbackVersion(String callbackVersion) {
        this.callbackVersion = callbackVersion;
    }

    public JobStatus getStatus() {
        return status;
    }

    public void setStatus(JobStatus status) {
        this.status = status;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCron() {
        return cron;
    }

    public void setCron(String cron) {
        this.cron = cron;
    }

    public String getJob() {
        return job;
    }

    public void setJob(String job) {
        this.job = job;
    }

    public String getParams() {
        return params;
    }

    public void setParams(String params) {
        this.params = params;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public Boolean getDisabled() {
        return disabled;
    }

    public void setDisabled(Boolean disabled) {
        this.disabled = disabled;
    }
}
