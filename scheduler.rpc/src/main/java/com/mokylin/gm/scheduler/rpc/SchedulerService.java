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

    long addScheduler(SchedulerDTO dto) throws RPCException;

    void stopJob(long id) throws RPCException;

    void stopJob(String job,String cron,Boolean disabled,String data,String callbackURL,String callbackVersion) throws RPCException;

    void startJob(long id) throws RPCException;

    /**
     * 通过参数查询到job，然后启动，参数为null时则代表不做此参数条件
     * @param job
     * @param cron
     * @param disabled
     * @param data
     * @param callbackURL
     * @param callbackVersion
     * @throws RPCException
     */
    void startJob(String job,String cron,Boolean disabled,String data,String callbackURL,String callbackVersion) throws RPCException;

    void deleteScheduler(long id) throws RPCException;

    /**
     * 通过参数查找到job，然后删除，为空时，则代表无此参数条件
     * @param job
     * @param cron
     * @param data
     * @param disabled
     * @param callbackURL
     * @param callbackVersion
     * @throws RPCException
     */
    void deleteScheduler(String job,String cron,String data,Boolean disabled,String callbackURL,String callbackVersion) throws RPCException;

    /**
     * 通过id查找定时任务，然后更新
     * @param dto
     * @throws RPCException
     */
    void updateJobById(SchedulerDTO dto) throws RPCException;

    /**
     * 通过data和job查找到定时任务，然后更新该定时任务的其他属性（cron,startTime,endTime,disabled）
     * @param dto
     * @throws RPCException
     */
    void updateJobByJobAndData(SchedulerDTO dto) throws RPCException;


    void saveOrUpdateJobByJobAndData(SchedulerDTO dto) throws RPCException;

    Page<SchedulerDTO> list(SchedulerDTO dto,int pageSize,int pageNo) throws RPCException;

}
