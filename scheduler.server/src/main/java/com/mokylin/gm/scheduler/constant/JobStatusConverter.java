package com.mokylin.gm.scheduler.constant;

import com.mokylin.gm.scheduler.persist.dbm.util.CustomConverter;
import com.mokylin.gm.scheduler.rpc.dto.JobStatus;

/**
 * @author 李朝(Li.Zhao)
 * @since 2015/1/12.
 */

public class JobStatusConverter extends CustomConverter<JobStatus,Integer> {
    @Override
    public Integer serialize(JobStatus e) {
        return e.getValue();
    }

    @Override
    public JobStatus deSerialize(Integer o) {
        return JobStatus.of(o);
    }
}
