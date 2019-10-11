package com.wj.annotation;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * @author jun.wang
 * @title: LoggerTimes
 * @projectName ownerpro
 * @description: TODO
 * @date 2019/10/10 10:46
 */

@Aspect
//@Component
public class LoggerTimes {

    private ThreadLocal<Long> startTime = new ThreadLocal<>();

    private Logger logger = LoggerFactory.getLogger(LoggerTimes.class);

    @Pointcut("@annotation(com.wj.annotation.Time)")
    public void timeAspect() {
    }

    @Before(value = "timeAspect()")
    public void doBefore(JoinPoint joinPoint) {
        long now = System.currentTimeMillis();
        startTime.set(now);
    }

    @After(value = "timeAspect()")
    public void doAfterReturning(JoinPoint joinPoint) {
        logger.info("interface cost times：" + (System.currentTimeMillis()-startTime.get()) + "ms");
    }
}
