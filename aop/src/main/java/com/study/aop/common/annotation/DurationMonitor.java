package com.study.aop.common.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface DurationMonitor {

    int warn() default 1000;
    int error() default 3000;
    int errorLimit() default 3;
    int warnLimit() default 10;


}
