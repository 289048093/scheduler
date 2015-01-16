package com.mokylin.gm.scheduler.rpc;

import com.mokylin.gm.scheduler.rpc.dto.Page;
import com.mokylin.gm.scheduler.rpc.dto.SchedulerDTO;
import com.mokylin.gm.scheduler.rpc.exception.RPCException;

import java.util.Date;
import java.util.List;

/**
 * @author 李朝(Li.Zhao)
 * @since 2015/1/8.
 */

public interface SchedulerService {

    long addScheduler(String job, String data, String cron, Date startTime, Date endTime, boolean disabled) throws RPCException;

    void stopJob(long id) throws RPCException;

    void stopJob(String job,String cron,Boolean disabled,String data) throws RPCException;

    void startJob(long id) throws RPCException;

    void startJob(String job,String cron,Boolean disabled,String data) throws RPCException;

    void deleteScheduler(long id) throws RPCException;

    void deleteScheduler(String job,String cron,Boolean disabled,String data) throws RPCException;

    /**
     * 通过id查找定时任务，然后更新
     * @param id  定时器定时任务ID 此id为添加定时任务时候返回的id
     * @param data
     * @param cron
     * @param startTime
     * @param endTime
     * @param job
     * @param disabled
     * @throws RPCException
     */
    void updateJob(long id,String data,String cron,Date startTime,Date endTime,String job,Boolean disabled) throws RPCException;

    /**
     * 通过data和job查找到定时任务，然后更新该定时任务的其他属性（cron,startTime,endTime,disabled）
     * @param data
     * @param cron
     * @param startTime
     * @param endTime
     * @param job
     * @param disabled
     * @throws RPCException
     */
    void updateJob(String data,String cron,Date startTime,Date endTime,String job,Boolean disabled) throws RPCException;

    Page<SchedulerDTO> list(String job,String cron,Boolean disabled,String data,int pageSize,int pageNo) throws RPCException;

}
