package com.wj.exception;

public class IdCenterException extends RuntimeException {

    private String errCode;

    private String errMsg;

    public IdCenterException(String errMsg) {
        super(errMsg);
        this.errMsg = errMsg;
    }

    public IdCenterException(String errMsg, Throwable e) {
        super(errMsg, e);
        this.errMsg = errMsg;
    }

    public IdCenterException(String errCode, String errMsg, Throwable e) {
        super(errMsg, e);
        this.errCode = errCode;
        this.errMsg = errMsg;
    }
}
