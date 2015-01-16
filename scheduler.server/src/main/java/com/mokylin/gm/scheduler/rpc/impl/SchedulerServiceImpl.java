package com.mokylin.gm.scheduler.rpc.impl;

import com.mokylin.gm.scheduler.JobManager;
import com.mokylin.gm.scheduler.JobManagerImpl;
import com.mokylin.gm.scheduler.entity.CronScheduler;
import com.mokylin.gm.scheduler.jobloader.ClassHelper;
import com.mokylin.gm.scheduler.persist.CronSchedulerDAO;
import com.mokylin.gm.scheduler.rpc.SchedulerService;
import com.mokylin.gm.scheduler.rpc.dto.JobStatus;
import com.mokylin.gm.scheduler.rpc.dto.Page;
import com.mokylin.gm.scheduler.rpc.dto.SchedulerDTO;
import com.mokylin.gm.scheduler.rpc.exception.RPCException;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;

/**
 * @author 李朝(Li.Zhao)
 * @since 2015/1/8.
 */

public class SchedulerServiceImpl implements SchedulerService {
    private final static Logger log = LoggerFactory.getLogger(SchedulerServiceImpl.class);

    private static CronSchedulerDAO dao = CronSchedulerDAO.getInstance();
    private static JobManager jobManager = JobManagerImpl.getInstance();

    @Override
    public long addScheduler(String job, String data, String cron, Date startTime, Date endTime, boolean disabled) throws RPCException {
        CronScheduler cs = newCronScheduler();
        cs.setStartTime(startTime);
        cs.setEndTime(endTime);
        cs.setJob(job);
        cs.setParams(data);
        cs.setCron(cron);
        cs.setDisabled(disabled);
        cs.setStatus(JobStatus.NORMAL.getValue());
        long id;
        try {
            id = dao.insert(cs);
        } catch (SQLException e) {
            log.error(e.getMessage(), e);
            throw new RPCException("insert data to db error:" + e.getMessage(), e);
        }

        ClassHelper.reloadJobPath();

        try {
            jobManager.addJob(cs);
        } catch (Exception e) {
            cs.setStatus(JobStatus.ERROR.getValue());
            try {
                dao.update(cs);
            } catch (SQLException e1) {
                log.error(e.getMessage(), e);
                throw new RPCException("update db data error:" + e.getMessage(), e);
            }
        }
        return id;
    }

    @Override
    public void stopJob(long id) throws RPCException {
        CronScheduler cs;
        try {
            cs = dao.get(id);
            stopJob(cs);
        } catch (SQLException e) {
            log.error(e.getMessage(), e);
            throw new RPCException("get db data error:" + e.getMessage(), e);
        }
    }

    @Override
    public void stopJob(String job, String cron, Boolean disabled, String data) throws RPCException {
        List<CronScheduler> list = null;
        try {
            list = dao.list(job, cron, disabled, data);
        } catch (SQLException e) {
            log.error(e.getMessage(), e);
            throw new RPCException("list db data error:" + e.getMessage(), e);
        }
        for (CronScheduler cs : list) {
            stopJob(cs);
        }
    }

    private void stopJob(CronScheduler cs) throws RPCException {
        try {
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
            startJob(cs);
        } catch (SQLException e) {
            log.error(e.getMessage(), e);
            throw new RPCException("get db data error:" + e.getMessage(), e);
        }
    }

    @Override
    public void startJob(String job, String cron, Boolean disabled, String data) throws RPCException {
        List<CronScheduler> list = null;
        try {
            list = dao.list(job, cron, disabled, data);
        } catch (SQLException e) {
            log.error(e.getMessage(), e);
            throw new RPCException("list db data error:" + e.getMessage(), e);
        }
        ClassHelper.reloadJobPath();
        for (CronScheduler cs : list) {
            startJob(cs);
        }
    }

    private void startJob(CronScheduler cs) throws RPCException {
        try {
            cs.setDisabled(false);
            cs.setUpdateTime(new Date());
            dao.update(cs);
        } catch (SQLException e) {
            log.error(e.getMessage(), e);
            throw new RPCException("get db data error:" + e.getMessage(), e);
        }
        ClassHelper.reloadJobPath();
        try {
            jobManager.addJob(cs);
        } catch (Exception e) {
            cs.setStatus(JobStatus.ERROR.getValue());
            try {
                dao.update(cs);
            } catch (SQLException e1) {
                log.error(e.getMessage(), e);
                throw new RPCException("update db data error:" + e.getMessage(), e);
            }
        }
    }

    @Override
    public void deleteScheduler(long id) throws RPCException {
        CronScheduler cs;
        try {
            cs = dao.get(id);
            deleteScheduler(cs);
        } catch (SQLException e) {
            log.error(e.getMessage(), e);
            throw new RPCException("get db data error:" + e.getMessage(), e);
        }
    }

    @Override
    public void deleteScheduler(String job, String cron, Boolean disabled, String data) throws RPCException {
        List<CronScheduler> list = null;
        try {
            list = dao.list(job, cron, disabled, data);
        } catch (SQLException e) {
            log.error(e.getMessage(), e);
            throw new RPCException("list db data error:" + e.getMessage(), e);
        }
        for (CronScheduler cs : list) {
            deleteScheduler(cs);
        }
    }

    private void deleteScheduler(CronScheduler cs) throws RPCException {
        try {
            dao.delete(cs);
        } catch (SQLException e) {
            log.error(e.getMessage(), e);
            throw new RPCException("get db data error:" + e.getMessage(), e);
        }
        jobManager.removeJob(cs);
    }

    @Override
    public void updateJob(long id, String data, String cron,Date startTime,Date endTime, String job,Boolean disabled) throws RPCException {
        CronScheduler cs;
        try {
            cs = dao.get(id);
            updateJob(cs,data,cron,job,startTime,endTime,disabled);
        } catch (SQLException e) {
            log.error(e.getMessage(), e);
            throw new RPCException("get db data error:" + e.getMessage(), e);
        }
    }

    @Override
    public void updateJob(String data, String cron, Date startTime, Date endTime, String job, Boolean disabled) throws RPCException {
        List<CronScheduler> list = null;
        try {
            list = dao.list(job, cron, disabled, data);
        } catch (SQLException e) {
            log.error(e.getMessage(), e);
            throw new RPCException("list db data error:" + e.getMessage(), e);
        }
        for (CronScheduler cs : list) {
            updateJob(cs,data,cron,job,startTime,endTime,disabled);
        }
    }

    private void updateJob(CronScheduler cs,String data,String cron,String job,Date startTime,Date endTime,Boolean disabled) throws RPCException {
        try {
            jobManager.removeJob(cs);
            cs.setStartTime(startTime);
            cs.setEndTime(endTime);
            if(disabled!=null)cs.setDisabled(disabled);
            if(StringUtils.isNotBlank(cron))cs.setCron(cron);
            if(StringUtils.isNotBlank(job))cs.setJob(job);
            if(StringUtils.isNotBlank(data))cs.setParams(data);
            cs.setUpdateTime(new Date());
            dao.update(cs);
        } catch (SQLException e) {
            log.error(e.getMessage(), e);
            throw new RPCException("get db data error:" + e.getMessage(), e);
        }
        try {
            jobManager.addJob(cs);
        } catch (Exception e) {
            cs.setStatus(JobStatus.ERROR.getValue());
            try {
                dao.update(cs);
            } catch (SQLException e1) {
                log.error(e.getMessage(), e);
                throw new RPCException("update db data error:" + e.getMessage(), e);
            }
        }
    }

    @Override
    public Page<SchedulerDTO> list(String job, String cron, Boolean disabled, String data, int pageSize, int pageNo) throws RPCException {
        try {
            return dao.pageList(job, cron, disabled, data, pageSize, pageNo);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    private CronScheduler newCronScheduler() {
        CronScheduler cs = new CronScheduler();
        Date now = new Date();
        cs.setCreateTime(now);
        cs.setUpdateTime(now);
        cs.setStatus(JobStatus.NORMAL.getValue());
        return cs;
    }


}
