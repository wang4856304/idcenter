package com.wj.annotation;

import java.lang.annotation.*;

/**
 * @author jun.wang
 * @title: Time
 * @projectName ownerpro
 * @description: TODO
 * @date 2019/10/10 10:44
 */

@Target({ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Time {
}
