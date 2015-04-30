package com.mokylin.gm.scheduler.entity;

import com.mokylin.gm.scheduler.persist.dbm.annotation.Column;
import com.mokylin.gm.scheduler.persist.dbm.annotation.ID;
import com.mokylin.gm.scheduler.persist.dbm.annotation.Table;

import java.util.Date;

/**
 *
 * @author 李朝(Li.Zhao)
 * @since 2015/1/4.
 */
@Table("t_cron_scheduler")
public class CronScheduler {

    @ID
    @Column
    private long id;

    @Column
    private String cron;

    @Column
    private String job;

    @Column
    private String params;

    @Column("create_time")
    private Date createTime;

    @Column("update_time")
    private Date updateTime;

    @Column("start_time")
    private Date startTime;

    @Column("end_time")
    private Date endTime;

    @Column
    private boolean disabled;

    @Column
    private int status;

    @Column("callback_url")
    private String callbackUrl;

    @Column("callback_version")
    private String callbackVersion;

    @Column("interval_minutes")
    private Long intervalMinutes;

    public Long getIntervalMinutes() {
        return intervalMinutes;
    }

    public void setIntervalMinutes(Long intervalMinutes) {
        this.intervalMinutes = intervalMinutes;
    }

    public String getCallbackVersion() {
        return callbackVersion;
    }

    public void setCallbackVersion(String callbackVersion) {
        this.callbackVersion = callbackVersion;
    }

    public String getCallbackUrl() {
        return callbackUrl;
    }

    public void setCallbackUrl(String callbackUrl) {
        this.callbackUrl = callbackUrl;
    }

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

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
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

    public boolean isDisabled() {
        return disabled;
    }

    public void setDisabled(boolean disabled) {
        this.disabled = disabled;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CronScheduler that = (CronScheduler) o;

        if (disabled != that.disabled) return false;
        if (id != that.id) return false;
        if (status != that.status) return false;
        if (callbackUrl != null ? !callbackUrl.equals(that.callbackUrl) : that.callbackUrl != null) return false;
        if (callbackVersion != null ? !callbackVersion.equals(that.callbackVersion) : that.callbackVersion != null)
            return false;
        if (createTime != null ? !createTime.equals(that.createTime) : that.createTime != null) return false;
        if (cron != null ? !cron.equals(that.cron) : that.cron != null) return false;
        if (endTime != null ? !endTime.equals(that.endTime) : that.endTime != null) return false;
        if (job != null ? !job.equals(that.job) : that.job != null) return false;
        if (params != null ? !params.equals(that.params) : that.params != null) return false;
        if (startTime != null ? !startTime.equals(that.startTime) : that.startTime != null) return false;
        if (updateTime != null ? !updateTime.equals(that.updateTime) : that.updateTime != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = (int) (id ^ (id >>> 32));
        result = 31 * result + (cron != null ? cron.hashCode() : 0);
        result = 31 * result + (job != null ? job.hashCode() : 0);
        result = 31 * result + (params != null ? params.hashCode() : 0);
        result = 31 * result + (createTime != null ? createTime.hashCode() : 0);
        result = 31 * result + (updateTime != null ? updateTime.hashCode() : 0);
        result = 31 * result + (startTime != null ? startTime.hashCode() : 0);
        result = 31 * result + (endTime != null ? endTime.hashCode() : 0);
        result = 31 * result + (disabled ? 1 : 0);
        result = 31 * result + status;
        result = 31 * result + (callbackUrl != null ? callbackUrl.hashCode() : 0);
        result = 31 * result + (callbackVersion != null ? callbackVersion.hashCode() : 0);
        return result;
    }
}
