package com.mokylin.gm.scheduler.rpc;

import com.mokylin.gm.scheduler.rpc.dto.Page;
import com.mokylin.gm.scheduler.rpc.dto.SchedulerDTO;
import com.mokylin.gm.scheduler.rpc.exception.RPCException;

import java.util.List;

/**
 * @author 李朝(Li.Zhao)
 * @since 2015/1/8.
 */

public interface SchedulerService {

    long addScheduler(String job,String data,String cron,boolean disabled) throws RPCException;

    void stopJob(long id) throws RPCException;

    void stopJob(String job,String cron,Boolean disabled,String data) throws RPCException;

    void startJob(long id) throws RPCException;

    void startJob(String job,String cron,Boolean disabled,String data) throws RPCException;

    void deleteScheduler(long id) throws RPCException;

    void deleteScheduler(String job,String cron,Boolean disabled,String data) throws RPCException;

    void updateJob(long id,String data,String cron,String job) throws RPCException;

    Page<SchedulerDTO> list(String job,String cron,Boolean disabled,String data,int pageSize,int pageNo) throws RPCException;

}
