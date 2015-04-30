package com.mokylin.gm.scheduler.rpc;

import com.mokylin.gm.scheduler.rpc.exception.RPCException;

/**
 * @author 李朝(Li.Zhao)
 * @since 2015/1/12.
 */

public interface SchedulerCallBack {

    boolean call(String params)throws RPCException;

}
