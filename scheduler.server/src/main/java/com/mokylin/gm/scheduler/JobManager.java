package com.mokylin.gm.scheduler;

import com.mokylin.gm.scheduler.entity.CronScheduler;

import java.util.List;
import java.util.Map;

/**
 * @author 李朝(Li.Zhao)
 * @since 2015/1/8.
 */

public interface JobManager {

    void initLoad();

    void addJob(CronScheduler cs);

    void removeJob(CronScheduler cs);

    void pauseJob(CronScheduler cs);

    void resumeJob(CronScheduler cs);

    List<Map<String,Object>> listRunning();
}
