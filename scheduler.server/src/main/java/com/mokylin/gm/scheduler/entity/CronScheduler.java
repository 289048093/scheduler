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
}
