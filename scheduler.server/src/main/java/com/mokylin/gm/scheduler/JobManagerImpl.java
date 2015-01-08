package com.mokylin.gm.scheduler;

import com.mokylin.gm.scheduler.entity.CronScheduler;
import com.mokylin.gm.scheduler.jobloader.ClassHelper;
import com.mokylin.gm.scheduler.util.Constant;
import com.mokylin.gm.scheduler.util.DBUtils;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.util.List;

/**
 * @author 李朝(Li.Zhao)
 * @since 2015/1/8.
 */

public class JobManagerImpl implements JobManager {
    private final static Logger log = LoggerFactory.getLogger(JobManagerImpl.class);

    private static Scheduler scheduler;

   private static List<CronScheduler> schedulers;

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

    @Override
    public void initLoad() {
        List<CronScheduler> schedulers;
        try {
            schedulers = DBUtils.listScheduler();
        } catch (SQLException e) {
            log.error(e.getMessage(), e);
            return;
        }
        for (CronScheduler cs : schedulers) {
            if (cs.isDisabled()) {
                continue;
            }
            addOrUpdateJob(cs);
        }
    }

    @Override
    public void addOrUpdateJob(CronScheduler cs) {
        if(cs.isDisabled()){
            return;
        }
        Class aClass;
        try {
            aClass = ClassHelper.forName(cs.getJob());
        } catch (ClassNotFoundException e) {
            log.error(e.getMessage(), e);
            return;
        }
        if (!Job.class.isAssignableFrom(aClass)) {
            log.warn("job:{} is not instance of QuartzJob", cs.getJob());
            return;
        }
        try {
            JobDetail job = JobBuilder.newJob(aClass)
                    .withIdentity(getJobKey(cs))
                    .usingJobData(Constant.JOB_PARAMS, cs.getParams())
                    .build();

            CronExpression cron = new CronExpression(cs.getCron());
            CronScheduleBuilder cronScheduleBuilder = CronScheduleBuilder.cronSchedule(cron);
            TriggerBuilder<CronTrigger> triggerBuilder = TriggerBuilder.newTrigger()
                    .withIdentity(getTriggerKey(cs))
                    .withSchedule(cronScheduleBuilder)
                    .forJob(job);
            Trigger trigger = triggerBuilder
                    .build();

            boolean jobExist = scheduler.checkExists(job.getKey());
            boolean triggerExist = scheduler.checkExists(trigger.getKey());
            if (jobExist && triggerExist) {
                scheduler.resumeJob(job.getKey());
                return;
            }
            if (jobExist && !triggerExist) {
//                scheduler.getTriggersOfJob()
                return;
            }
            scheduler.scheduleJob(job, trigger);
        } catch (Exception e) {
            log.error("invalid cron expression:{}", cs.getCron());
            return;
        }
    }

    @Override
    public void removeJob(CronScheduler cs) {

        try {
            scheduler.deleteJob(getJobKey(cs));
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
        }
    }

    private JobKey getJobKey(CronScheduler cs) {
        return JobKey.jobKey(cs.getId() + cs.getJob() + cs.getParams() + "_JobID", "jobGroup");
    }

    private TriggerKey getTriggerKey(CronScheduler cs) {
        return TriggerKey.triggerKey(cs.getId() + cs.getJob() + cs.getParams() + "_TriggerID", "cronGroup");
    }
}
