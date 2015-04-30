package com.mokylin.gm.scheduler.rpc.impl;

import com.alibaba.dubbo.common.json.JSON;
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
import com.mokylin.gm.scheduler.util.JsonUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;

import java.sql.SQLException;
import java.util.Date;
import java.util.LinkedList;
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
    public long addScheduler(SchedulerDTO dto) throws RPCException {
        if(log.isInfoEnabled()){
            log.info("addScheduler method,params:"+JsonUtils.json(dto));
        }
        CronScheduler cs = newCronScheduler();
        cs.setStartTime(dto.getStartTime());
        cs.setEndTime(dto.getEndTime());
        cs.setJob(dto.getJob());
        cs.setParams(dto.getParams());
        cs.setCron(dto.getCron());
        cs.setDisabled(dto.getDisabled() == null ? false : dto.getDisabled());
        cs.setCallbackUrl(dto.getCallbackURL());
        cs.setCallbackVersion(dto.getCallbackVersion());
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
        log.info("stopJob method,id:"+id);
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
    public void stopJob(String job, String cron, Boolean disabled, String data, String callbackUrl, String callbackVersion) throws RPCException {
        log.info("stopJob method,params : job:" + job + ",cron :" + cron + ", data:" + data + ", disabled:" + disabled + ", callbackUrl:" + callbackUrl + ", callbackVersion:" + callbackVersion);
        List<CronScheduler> list = null;
        try {
            list = dao.list(job, cron, disabled, data, callbackUrl, callbackVersion);
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
    public void startJob(String job, String cron, Boolean disabled, String data, String callbackUrl, String callbackVersion) throws RPCException {
        log.info("startJob method,params : job:" + job + ",cron :" + cron + ", data:" + data + ", disabled:" + disabled + ", callbackUrl:" + callbackUrl + ", callbackVersion:" + callbackVersion);
        List<CronScheduler> list = null;
        try {
            list = dao.list(job, cron, disabled, data, callbackUrl, callbackVersion);
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
        log.info("deleteScheduler method,params: id:"+id);
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
    public void deleteScheduler(String job, String cron, String data, Boolean disabled, String callbackUrl, String callbackVersion) throws RPCException {
//        log.info("delete scheduler method,params : job:" + job + ",cron :" + cron + ", data:" + data + ", disabled:" + disabled + ", callbackUrl:" + callbackUrl + ", callbackVersion:" + callbackVersion);
        List<CronScheduler> list = null;
        try {
            list = dao.list(job, cron, null, data, null, callbackVersion);
        } catch (SQLException e) {
            log.error(e.getMessage(), e);
            throw new RPCException("list db data error:" + e.getMessage(), e);
        }
        for (CronScheduler cs : list) {
            deleteScheduler(cs);
        }
    }

    private void deleteScheduler(CronScheduler cs) throws RPCException {
        if(log.isInfoEnabled()){
            log.debug("delete scheduler:"+ JsonUtils.json(cs));
        }
        try {
            dao.delete(cs);
        } catch (SQLException e) {
            log.error(e.getMessage(), e);
            throw new RPCException("get db data error:" + e.getMessage(), e);
        }
        jobManager.removeJob(cs);
    }

    @Override
    public void updateJobById(SchedulerDTO dto) throws RPCException {
        if(log.isInfoEnabled()){
            log.info("updateJobById method,param:"+JsonUtils.json(dto));
        }
        CronScheduler cs;
        try {
            cs = dao.get(dto.getId());
        } catch (SQLException e) {
            log.error(e.getMessage(), e);
            throw new RPCException("get db data error:" + e.getMessage(), e);
        }
        if (cs == null) {
            throw new RPCException("no found job by id:" + dto.getId());
        }
        updateJob(cs, dto.getParams(), dto.getCron(), dto.getJob(), dto.getStartTime(), dto.getEndTime(), dto.getDisabled());
    }

    @Override
    public void updateJobByJobAndData(SchedulerDTO dto) throws RPCException {
        if(log.isInfoEnabled()){
            log.info("updateJobByJobAndData method,param:"+JsonUtils.json(dto));
        }
        List<CronScheduler> list = null;
        try {
            list = dao.list(dto.getJob(), null, null, dto.getParams(), null, null);
        } catch (SQLException e) {
            log.error(e.getMessage(), e);
            throw new RPCException("list db data error:" + e.getMessage(), e);
        }
        if (list == null || list.isEmpty()) {
            throw new RPCException("no found job");
        }
        for (CronScheduler cs : list) {
            updateJob(cs, dto.getParams(), dto.getCron(), dto.getJob(), dto.getStartTime(), dto.getEndTime(), dto.getDisabled());
        }
    }

    @Override
    public void saveOrUpdateJobByJobAndData(SchedulerDTO dto) throws RPCException {
        if(log.isInfoEnabled()){
            log.info("saveOrUpdateJobByJobAndData method,param:"+JsonUtils.json(dto));
        }
        List<CronScheduler> list = null;
        try {
            list = dao.list(dto.getJob(), null, null, dto.getParams(), null, null);
        } catch (SQLException e) {
            log.error(e.getMessage(), e);
            throw new RPCException("list db data error:" + e.getMessage(), e);
        }
        if (list == null || list.isEmpty()) {
            addScheduler(dto);
            return;
        }
        for (CronScheduler cs : list) {
            updateJob(cs, dto.getParams(), dto.getCron(), dto.getJob(), dto.getStartTime(), dto.getEndTime(), dto.getDisabled());
        }
    }

    private void updateJob(CronScheduler cs, String data, String cron, String job, Date startTime, Date endTime, Boolean disabled) throws RPCException {
        try {
            CronScheduler tmp  = new CronScheduler();
            BeanUtils.copyProperties(cs,tmp);

            cs.setStartTime(startTime);
            cs.setEndTime(endTime);
            if (disabled != null) cs.setDisabled(disabled);
            if (StringUtils.isNotBlank(cron)) cs.setCron(cron);
            if (StringUtils.isNotBlank(job)) cs.setJob(job);
            if (StringUtils.isNotBlank(data)) cs.setParams(data);
            cs.setUpdateTime(new Date());
            cs.setStatus(JobStatus.NORMAL.getValue());

            if(tmp.equals(cs)){//不做修改
                log.info("update job method:cron scheduler no change,return");
                return;
            }

            jobManager.removeJob(cs);
            dao.update(cs);
        } catch (SQLException e) {
            log.error(e.getMessage(), e);
            throw new RPCException("get db data error:" + e.getMessage(), e);
        }
        try {
            jobManager.addJob(cs);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
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
    public Page<SchedulerDTO> list(SchedulerDTO dto, int pageSize, int pageNo) throws RPCException {
        try {
            return dao.pageList(dto.getJob(), dto.getCron(), dto.getDisabled(), dto.getParams(), dto.getStartTime(), dto.getEndTime(), pageSize, pageNo);
        } catch (SQLException e) {
            log.error(e.getMessage(), e);
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
