package com.mokylin.gm.scheduler;

import com.alibaba.dubbo.common.json.JSON;
import com.mokylin.gm.scheduler.entity.CronScheduler;
import com.mokylin.gm.scheduler.jobloader.ClassHelper;
import com.mokylin.gm.scheduler.persist.CronSchedulerDAO;
import com.mokylin.gm.scheduler.rpc.dto.JobStatus;
import com.mokylin.gm.scheduler.util.Constant;
import org.apache.commons.lang3.time.DateUtils;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;
import org.quartz.impl.matchers.GroupMatcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author 李朝(Li.Zhao)
 * @since 2015/1/8.
 */

public class JobManagerImpl implements JobManager {
    private final static Logger log = LoggerFactory.getLogger(JobManagerImpl.class);
    private CronSchedulerDAO dao = CronSchedulerDAO.getInstance();

    private static Scheduler scheduler;

    static {
        try {
            scheduler = StdSchedulerFactory.getDefaultScheduler();
            scheduler.start();
            Runtime.getRuntime().addShutdownHook(new Thread() {
                @Override
                public void run() {
                    try {
                        scheduler.shutdown();
                    } catch (SchedulerException e) {
                        log.error(e.getMessage(), e);
                    }
                }
            });
        } catch (SchedulerException e) {
            log.error(e.getMessage(), e);
        }
    }

    private static JobManagerImpl jobManager = new JobManagerImpl();

    private JobManagerImpl() {
    }

    public static JobManagerImpl getInstance() {
        return jobManager;
    }

    @Override
    public void initLoad() {
        List<CronScheduler> schedulers;
        try {
            schedulers = dao.findAll();
        } catch (SQLException e) {
            log.error(e.getMessage(), e);
            return;
        }
        for (CronScheduler cs : schedulers) {
            if (cs.isDisabled()) {
                continue;
            }
            addJob(cs);
        }
    }

    @Override
    public void addJob(CronScheduler cs) {

        ClassHelper.reloadJobPath();

        Class aClass;
        try {
            aClass = ClassHelper.forName(cs.getJob());
        } catch (ClassNotFoundException e) {
            log.error(e.getMessage(), e);
            return;
        }
        if (!Job.class.isAssignableFrom(aClass)) {
            log.error("job:{} is not instance of QuartzJob", cs.getJob());
            return;
        }
        try {
            JobDetail job = JobBuilder.newJob(aClass)
                    .withIdentity(getJobKey(cs))
                    .usingJobData(Constant.JOB_PARAMS, cs.getParams())
                    .usingJobData(Constant.JOB_CALLBACK_VERSION, cs.getCallbackVersion())
                    .build();

            CronExpression cron = new CronExpression(cs.getCron());
            CronScheduleBuilder cronScheduleBuilder = CronScheduleBuilder.cronSchedule(cron);
            TriggerBuilder<CronTrigger> triggerBuilder = TriggerBuilder.newTrigger()
                    .withIdentity(getTriggerKey(cs))
                    .withSchedule(cronScheduleBuilder)
                    .forJob(job);
            Date now = new Date();
            Date startTime = cs.getStartTime();
            if (startTime != null) {
                if (cs.getStartTime().before(now)) {
//                    startTime = cron.getNextValidTimeAfter(now);
                    startTime = now;
                    triggerBuilder.startNow();
                } else {
                    triggerBuilder.startAt(startTime);
                }
            }
            if (cs.getEndTime() != null) {
                if (cs.getEndTime().before(now)) {
                    log.warn("end time before now,job will never fire,id:{},params:{}", cs.getId(), cs.getParams());
                    return;
                }
                if (startTime != null && cs.getEndTime().before(startTime)) {
                    log.warn("end time before startTime,job will never fire,id:{},params:{}", cs.getId(), cs.getParams());
                    return;
                }
                triggerBuilder.endAt(cs.getEndTime());
            }
            Trigger trigger = triggerBuilder
                    .build();

            boolean jobExist = scheduler.checkExists(job.getKey());
//            boolean triggerExist = scheduler.checkExists(trigger.getKey());
            if (jobExist) {
                if (cs.isDisabled()) {
                    scheduler.deleteJob(job.getKey());
                } else {
                    scheduler.resumeJob(job.getKey());
                }
                return;
            }
            if (cs.isDisabled()) return;
            scheduler.scheduleJob(job, trigger);
        } catch (Exception e) {
            try {
                log.error("CronSchedulerEntity:" + JSON.json(cs), e.getMessage(), e);
            } catch (IOException e1) {
                log.error(e.getMessage(), e);
            }
            cs.setStatus(JobStatus.ERROR.getValue());
            try {
                dao.update(cs);
            } catch (SQLException e1) {
                log.error(e.getMessage(), e);
            }
            return;
        }
    }

    @Override
    public void removeJob(CronScheduler cs) {
        try {
            JobKey jobKey = getJobKey(cs);
            scheduler.unscheduleJob(getTriggerKey(cs));
            scheduler.deleteJob(jobKey);
        } catch (SchedulerException e) {
            log.error(e.getMessage(), e);
        }
    }


    @Override
    public void pauseJob(CronScheduler cs) {
        try {
            scheduler.pauseJob(getJobKey(cs));
        } catch (SchedulerException e) {
            log.error(e.getMessage(), e);
        }
    }

    @Override
    public void resumeJob(CronScheduler cs) {
        try {
            scheduler.resumeJob(getJobKey(cs));
        } catch (SchedulerException e) {
            log.error(e.getMessage(), e);
            cs.setStatus(JobStatus.ERROR.getValue());
            try {
                dao.update(cs);
            } catch (SQLException e1) {
                log.error(e.getMessage(), e);
            }
        }
    }

    @Override
    public List<Map<String, Object>> listRunning() {
        try {
            List<Map<String, Object>> list = new ArrayList<>();
            for (String groupName : scheduler.getJobGroupNames()) {
                for (JobKey jk : scheduler.getJobKeys(GroupMatcher.jobGroupEquals(groupName))) {
                    JobDetail jobDetail = scheduler.getJobDetail(jk);
                    JobDataMap jobDataMap = jobDetail.getJobDataMap();
                    List<? extends Trigger> triggers = scheduler.getTriggersOfJob(jk);
                    Trigger trigger = triggers.get(0);
                    Date nextFireTime = trigger.getNextFireTime();

                    Map<String, Object> res = new HashMap<>();
                    res.put("Job Class", jobDetail.getJobClass().getCanonicalName());
                    res.put("content", JSON.json(jobDataMap));
                    res.put("next execute time", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(nextFireTime));
                    if (trigger instanceof CronTrigger) {
                        res.put("schedule cron", ((CronTrigger) trigger).getCronExpression());
                    }
                    list.add(res);
                }
            }
            return list;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return null;
    }

    private JobKey getJobKey(CronScheduler cs) {
        return JobKey.jobKey(cs.getId() + cs.getJob() + cs.getParams() + "_JobID", "jobGroup");
    }

    private TriggerKey getTriggerKey(CronScheduler cs) {
        return TriggerKey.triggerKey(cs.getId() + cs.getJob() + cs.getParams() + "_TriggerID", "cronGroup");
    }
}
