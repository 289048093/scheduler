package com.mokylin.gm.scheduler.rpc.dto;

import java.util.HashMap;
import java.util.Map;

/**
 * @author 李朝(Li.Zhao)
 * @since 2015/1/12.
 */

public enum JobStatus {
    /**
     * 正常
     */
    NORMAL(0),
    /**
     * 异常
     */
    ERROR(1);



    private int value;
    private JobStatus(int value){
        this.value=value;
    }

    private static Map<Integer,JobStatus> jobStatusMap = new HashMap<>();
    static {
        for(JobStatus js:JobStatus.values()){
            jobStatusMap.put(js.value,js);
        }
    }

    public int getValue() {
        return value;
    }

    public static JobStatus of(int value){
        return jobStatusMap.get(value);
    }
}
