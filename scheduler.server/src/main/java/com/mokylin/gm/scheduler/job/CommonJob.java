package com.mokylin.gm.scheduler.job;

import com.alibaba.dubbo.common.json.JSON;
import com.alibaba.dubbo.common.json.JSONObject;
import com.alibaba.dubbo.common.json.ParseException;
import com.alibaba.dubbo.config.ApplicationConfig;
import com.alibaba.dubbo.config.ReferenceConfig;
import com.alibaba.dubbo.config.RegistryConfig;
import com.alibaba.dubbo.config.utils.ReferenceConfigCache;
import com.mokylin.gm.scheduler.Global;
import com.mokylin.gm.scheduler.rpc.SchedulerCallBack;
import org.apache.commons.lang3.StringUtils;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author 李朝(Li.Zhao)
 * @since 2015/1/19.
 */

public class CommonJob implements Job {

    private static final Logger log = LoggerFactory.getLogger(CommonJob.class);

    private String params;

    private String callbackVersion;

    public String getCallbackVersion() {
        return callbackVersion;
    }

    public void setCallbackVersion(String callbackVersion) {
        this.callbackVersion = callbackVersion;
    }

    public String getParams() {
        return params;
    }

    public void setParams(String params) {
        this.params = params;
    }

    private ReferenceConfig<SchedulerCallBack> getRefCfg() {
        ApplicationConfig application = Global.getApplicationConfig();
        ReferenceConfig<SchedulerCallBack> reference = new ReferenceConfig<SchedulerCallBack>(); // 此实例很重，封装了与注册中心的连接以及与提供者的连接，请自行缓存，否则可能造成内存和连接泄漏
        reference.setApplication(application);
        reference.setRegistry(Global.getContext().getBean(RegistryConfig.class)); // 多个注册中心可以用setRegistries()
        reference.setVersion(callbackVersion);
        reference.setInterface(SchedulerCallBack.class);
        return reference;
    }


    private SchedulerCallBack getCallBack() {
        JSONObject parse = null;
        try {
            parse = (JSONObject) JSON.parse(params);
        } catch (ParseException e) {
            log.error(e.getMessage(), e);
        }
        if (parse == null) return null;
        Object invoke_identify = parse.get("invoke_identify");
        String group = invoke_identify == null ? null : invoke_identify.toString();
        if (StringUtils.isBlank(group)) return null;
        ReferenceConfig<SchedulerCallBack> ref = getRefCfg();
        ref.setGroup(group);
        ReferenceConfigCache cache = ReferenceConfigCache.getCache();
        //        if (schedulerCallBack == null) {
//            cache.destroy(ref);
//            schedulerCallBack = cache.get(ref);
//        }
        return cache.get(ref);
    }


    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        log.info("execute commonJob,params:"+params);
        try {
            SchedulerCallBack callBack = getCallBack();
            if (callBack == null) return;
            if (callBack.call(params)) {
                log.warn("scheduler callback return false,callback version:{},params{}",  callbackVersion, params);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

}
