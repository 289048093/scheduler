package com.mokylin.gm.scheduler;

import com.mokylin.gm.scheduler.entity.CronScheduler;
import com.mokylin.gm.scheduler.util.ConfigInfo;
import com.mokylin.gm.scheduler.util.DBUtils;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;
import org.quartz.spi.JobStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.List;


/**
 * @author 李朝(Li.Zhao)
 * @since 2015/1/4.
 */

public class Main {
    private static final Logger log = LoggerFactory.getLogger(Main.class);

    private   static Scheduler scheduler;
    static{
        try {
            scheduler = StdSchedulerFactory.getDefaultScheduler();
            scheduler.start();
        } catch (SchedulerException e) {
           log.error(e.getMessage(),e);
        }
    }

    public void init() {
        ConfigInfo.setConfigPath("scheduler.properties");
        List<CronScheduler> schedulers;
        try {
            schedulers = DBUtils.listScheduler();
        } catch (SQLException e) {
            log.error(e.getMessage(), e);
            return;
        }
        for (CronScheduler cs : schedulers){
            if(cs.isDisabled()){
                continue;
            }

        }
    }

    private void startupTask(CronScheduler cs){
        
    }

    public static void test1() throws SchedulerException, ParseException {



// define the job and tie it to our HelloJob class

        String jobClassStr = "";

        Class<? extends Job> jobClass = Job2.class;
        System.out.println("asdf");

        JobDetail job = JobBuilder.newJob(jobClass)
                .withIdentity(jobClass.getName(), "group1")
                .usingJobData("name","tom")
               .usingJobData("addr","aaddr")
                .build();

// Trigger the job to run now, and then repeat every 40 seconds
        CronExpression cron = new CronExpression("0/2 * * * * ?");
        CronScheduleBuilder cronScheduleBuilder = CronScheduleBuilder.cronSchedule(cron);
        SimpleScheduleBuilder simpleScheduleBuilder = SimpleScheduleBuilder.simpleSchedule()
                .withIntervalInSeconds(40)
                .repeatForever();


        TriggerBuilder<CronTrigger> triggerBuilder = TriggerBuilder.newTrigger()
                .withIdentity("trigger1", "group1")
                .withSchedule(cronScheduleBuilder)
                .usingJobData("name", "jack")
                .forJob(job)
                .startNow();
        Trigger trigger = triggerBuilder
                .build();
// Tell quartz to schedule the job using our trigger

        scheduler.scheduleJob(job, trigger);

        cron =  new CronExpression("0/10 * * * * ?");
        trigger = triggerBuilder.withSchedule(CronScheduleBuilder.cronSchedule(cron)).build();
        try {
            System.in.read();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println(scheduler.getMetaData().getNumberOfJobsExecuted());
//        scheduler.deleteJob(job.getKey());
        scheduler.rescheduleJob(trigger.getKey(), trigger);
//        JobDetail jobDetail = JobBuilder.newJob(HelloJob.class).withIdentity(jobKey).build();

//        CronTrigger cronTrigger = TriggerBuilder.newTrigger().withIdentity(triggerKey).withSchedule(CronScheduleBuilder.cronSchedule(task.getCron())).build();
    }

    public static void main(String[] args) throws SchedulerException, ParseException {
        test1();
    }

    static void test2() throws SchedulerException, ParseException {
        SchedulerFactory schedFact = new org.quartz.impl.StdSchedulerFactory();

        Scheduler sched = schedFact.getScheduler();

        sched.start();
        // define the job and tie it to our HelloJob class
        JobDetail job = JobBuilder.newJob(HelloJob.class)
                .withIdentity("myJob", "group1")
                .usingJobData("name","tom")
                .build();

        // Trigger the job to run now, and then every 40 seconds
//        Trigger trigger = TriggerBuilder.newTrigger()
//                .withIdentity("myTrigger", "group1")
//                .startNow()
//                .withSchedule(SimpleScheduleBuilder.simpleSchedule()
//                        .withIntervalInSeconds(5)
//                        .repeatForever())
//                .build();
        CronExpression cron = new CronExpression("0/2 * * * * ?");
        CronScheduleBuilder cronScheduleBuilder = CronScheduleBuilder.cronSchedule(cron);
        TriggerBuilder<CronTrigger> triggerBuilder = TriggerBuilder.newTrigger()
                .withIdentity("trigger1", "group1")
                .withSchedule(cronScheduleBuilder)
//                .forJob(job)
                .startNow();
        Trigger trigger = triggerBuilder
                .build();

        // Tell quartz to schedule the job using our trigger
        sched.scheduleJob(job, trigger);
        cron =  new CronExpression("0/10 * * * * ?");
        trigger = triggerBuilder.withSchedule(CronScheduleBuilder.cronSchedule(cron)).build();
        try {
            System.in.read();
        } catch (IOException e) {
            e.printStackTrace();
        }
        sched.deleteJob(job.getKey());
        sched.scheduleJob(job,trigger);


    }
}
