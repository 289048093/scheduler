package com.mokylin.gm.scheduler.rpc.exception;

/**
 * @author 李朝(Li.Zhao)
 * @since 2015/1/8.
 */

public class RPCException extends Exception {

    public RPCException() {
        super();
    }

    public RPCException(String message) {
        super(message);
    }

    public RPCException(String message, Throwable cause) {
        super(message, cause);
    }

    public RPCException(Throwable cause) {
        super(cause);
    }
}
