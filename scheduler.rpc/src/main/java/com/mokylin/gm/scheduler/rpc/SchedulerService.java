package com.mokylin.gm.scheduler.rpc;

import com.mokylin.gm.scheduler.rpc.exception.RPCException;

/**
 * @author 李朝(Li.Zhao)
 * @since 2015/1/8.
 */

public interface SchedulerService {

    long addScheduler(String job,String data,String cron,boolean disabled) throws RPCException;

    void stopJob(long id) throws RPCException;

    void startJob(long id) throws RPCException;

    void deleteScheduler(long id) throws RPCException;

    void updateJob(long id,String data,String cron,String job) throws RPCException;

}
