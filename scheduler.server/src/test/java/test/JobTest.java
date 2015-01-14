package test;

import com.mokylin.gm.scheduler.entity.CronScheduler;
import com.mokylin.gm.scheduler.jobloader.ClassHelper;
import com.mokylin.gm.scheduler.util.ConfigInfo;
import com.mokylin.gm.scheduler.util.Constant;
import com.mokylin.gm.scheduler.util.DBUtils;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;
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

public class JobTest {
    private static final Logger log = LoggerFactory.getLogger(JobTest.class);

    private static Scheduler scheduler;

    static {
        try {
            scheduler = StdSchedulerFactory.getDefaultScheduler();
            scheduler.start();
        } catch (SchedulerException e) {
            log.error(e.getMessage(), e);
        }
    }

    public void init() {

    }

    private void startupTask(CronScheduler cs) {
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
                    .withIdentity(getJobId(cs), "jobGroup")
                    .usingJobData(Constant.JOB_PARAMS, cs.getParams())
                    .build();

            CronExpression cron = new CronExpression(cs.getCron());
            CronScheduleBuilder cronScheduleBuilder = CronScheduleBuilder.cronSchedule(cron);
            TriggerBuilder<CronTrigger> triggerBuilder = TriggerBuilder.newTrigger()
                    .withIdentity(getTriggerID(cs), "cronGroup")
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
            scheduler.scheduleJob(job, trigger);
        } catch (Exception e) {
            log.error("invalid cron expression:{}", cs.getCron());
            return;
        }
    }

    private String getJobId(CronScheduler cs) {
        return cs.getId() + cs.getJob() + cs.getCron() + cs.getParams() + "_JobID";
    }

    private String getTriggerID(CronScheduler cs) {
        return cs.getId() + cs.getJob() + cs.getCron() + cs.getParams() + "_TriggerID";
    }

    public static void test1() throws SchedulerException, ParseException, IOException {


// define the job and tie it to our HelloJob class

        String jobClassStr = "";

        Class<? extends Job> jobClass = Job2.class;
        System.out.println("asdf");

        JobKey jobKey = JobKey.jobKey(jobClass.getName(), "group1");
        JobDetail job = JobBuilder.newJob(jobClass)
                .withIdentity(jobKey)
                .usingJobData("name", "tom")
                .usingJobData("addr", "aaddr")
                .build();

// Trigger the job to run now, and then repeat every 40 seconds
        CronExpression cron = new CronExpression("* * 10/2 * * ? ");
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

//        cron = new CronExpression("0/10 * * * * ?");
//        trigger = triggerBuilder.withSchedule(CronScheduleBuilder.cronSchedule(cron)).build();
        try {
            System.in.read();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println(scheduler.getMetaData().getNumberOfJobsExecuted());
//        scheduler.deleteJob(job.getKey());
//        scheduler.rescheduleJob(trigger.getKey(), trigger);
//        scheduler.scheduleJob(job,trigger);
        JobDetail hellojob = JobBuilder.newJob(HelloJob.class)
                .withIdentity(HelloJob.class.getName(), "group1")
                .usingJobData("name", "tom")
                .usingJobData("addr", "aaddr")
                .build();

        triggerBuilder = TriggerBuilder.newTrigger()
                .withIdentity("trigger2", "group1")
                .withSchedule(cronScheduleBuilder)
                .usingJobData("name", "jack")
//                .forJob(job)
                .startNow();
        triggerBuilder.forJob(job);
        cron = new CronExpression("0/10 * * * * ?");
        Trigger trigger2 = triggerBuilder.withSchedule(CronScheduleBuilder.cronSchedule(cron)).build();
        System.out.println(scheduler.checkExists(job.getKey()));

        List<? extends Trigger> triggersOfJob = scheduler.getTriggersOfJob(job.getKey());
        System.out.println(triggersOfJob.size());
        System.out.println(triggersOfJob.get(0).getKey());
        TriggerKey key = triggersOfJob.get(0).getKey();
//        scheduler.unscheduleJob(key);
        System.out.println("====="+scheduler.checkExists(key));


        jobKey = JobKey.jobKey(jobClass.getName(), "group1");
        scheduler.deleteJob(jobKey);
        System.in.read();
//        scheduler.rescheduleJob(triggersOfJob.get(0).getKey(),trigger);
//        scheduler.scheduleJob(job,trigger);
//        scheduler.rescheduleJob(trigger.getKey(),trigger);
        triggersOfJob = scheduler.getTriggersOfJob(job.getKey());
        System.out.println(triggersOfJob.size());
        for(Trigger t:triggersOfJob){
            System.out.println(t.getKey());
        }

//        scheduler.addJob(hellojob,false);
//        scheduler.resumeTrigger(trigger.getKey());

//        JobDetail jobDetail = JobBuilder.newJob(HelloJob.class).withIdentity(jobKey).build();

//        CronTrigger cronTrigger = TriggerBuilder.newTrigger().withIdentity(triggerKey).withSchedule(CronScheduleBuilder.cronSchedule(task.getCron())).build();
    }

    public static void main(String[] args) throws SchedulerException, ParseException, IOException {
        test1();
    }

    static void test2() throws SchedulerException, ParseException {
        SchedulerFactory schedFact = new StdSchedulerFactory();

        Scheduler sched = schedFact.getScheduler();
        JobKey jobKey = JobKey.jobKey("myJob", "group1");
        sched.start();
        // define the job and tie it to our HelloJob class
        JobDetail job = JobBuilder.newJob(HelloJob.class)
                .withIdentity(jobKey)
                .usingJobData("name", "tom")
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
        cron = new CronExpression("0/10 * * * * ?");
        trigger = triggerBuilder.withSchedule(CronScheduleBuilder.cronSchedule(cron)).build();
        try {
            System.in.read();
        } catch (IOException e) {
            e.printStackTrace();
        }
        sched.deleteJob(jobKey);
//        sched.scheduleJob(job, trigger);
        try {
            System.in.read();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
