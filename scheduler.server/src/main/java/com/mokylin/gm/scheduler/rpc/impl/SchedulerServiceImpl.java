package com.mokylin.gm.scheduler.rpc.impl;

import com.mokylin.gm.scheduler.JobManager;
import com.mokylin.gm.scheduler.JobManagerImpl;
import com.mokylin.gm.scheduler.entity.CronScheduler;
import com.mokylin.gm.scheduler.persist.CronSchedulerDAO;
import com.mokylin.gm.scheduler.rpc.SchedulerService;
import com.mokylin.gm.scheduler.rpc.exception.RPCException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.util.Date;

/**
 * @author 李朝(Li.Zhao)
 * @since 2015/1/8.
 */

public class SchedulerServiceImpl implements SchedulerService {
    private final static Logger log = LoggerFactory.getLogger(SchedulerServiceImpl.class);

    private static CronSchedulerDAO dao = new CronSchedulerDAO();
    JobManager jobManager = new JobManagerImpl();

    @Override
    public long addScheduler(String job, String data, String cron, boolean disabled) throws RPCException {
        CronScheduler cs = newCronScheduler();
        cs.setJob(job);
        cs.setParams(data);
        cs.setCron(cron);
        cs.setDisabled(disabled);
        long id;
        try {
            id = dao.insert(cs);
        } catch (SQLException e) {
            log.error(e.getMessage(), e);
            throw new RPCException("insert data to db error:" + e.getMessage(), e);
        }
        jobManager.addOrUpdateJob(cs);
        return id;
    }

    @Override
    public void stopJob(long id) throws RPCException {
        CronScheduler cs;
        try {
            cs = dao.get(id);
            cs.setDisabled(true);
            cs.setUpdateTime(new Date());
            dao.update(cs);
        } catch (SQLException e) {
            log.error(e.getMessage(), e);
            throw new RPCException("get db data error:" + e.getMessage(), e);
        }
        jobManager.removeJob(cs);
    }

    @Override
    public void startJob(long id) throws RPCException {
        CronScheduler cs;
        try {
            cs = dao.get(id);
            cs.setDisabled(false);
            cs.setUpdateTime(new Date());
            dao.update(cs);
        } catch (SQLException e) {
            log.error(e.getMessage(), e);
            throw new RPCException("get db data error:" + e.getMessage(), e);
        }
        jobManager.addOrUpdateJob(cs);
    }

    @Override
    public void deleteScheduler(long id) throws RPCException {
        CronScheduler cs;
        try {
            cs = dao.get(id);
            dao.delete(cs);
        } catch (SQLException e) {
            log.error(e.getMessage(), e);
            throw new RPCException("get db data error:" + e.getMessage(), e);
        }
        jobManager.removeJob(cs);
    }

    @Override
    public void updateJob(long id, String data, String cron, String job) throws RPCException {
        CronScheduler cs;
        try {
            cs = dao.get(id);
            cs.setCron(cron);
            cs.setJob(job);
            cs.setParams(data);
            cs.setUpdateTime(new Date());
            dao.update(cs);
        } catch (SQLException e) {
            log.error(e.getMessage(), e);
            throw new RPCException("get db data error:" + e.getMessage(), e);
        }
        jobManager.addOrUpdateJob(cs);
    }

    private CronScheduler newCronScheduler() {
        CronScheduler cs = new CronScheduler();
        Date now = new Date();
        cs.setCreateTime(now);
        cs.setUpdateTime(now);
        return cs;
    }


}
