package com.wj.exception;

/**
 * @author jun.wang
 * @title: RedisRuntimeException
 * @projectName ownerpro
 * @description: TODO
 * @date 2019/5/7 10:09
 */
public class RedisRuntimeException extends RuntimeException {

    public RedisRuntimeException(String message) {
        super(message);
    }

    public RedisRuntimeException(String message, Throwable throwable) {
        super(message, throwable);
    }

    public RedisRuntimeException(Throwable throwable) {
        super(throwable);
    }
}
