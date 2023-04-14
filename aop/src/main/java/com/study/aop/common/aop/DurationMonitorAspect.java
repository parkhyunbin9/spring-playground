package com.study.aop.common.aop;

import com.study.aop.common.annotation.DurationMonitor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicLong;

@Slf4j
@Aspect
@Component
public class DurationMonitorAspect {

    @Around("@annotation(durationMonitor)")
    public Object doMonitor(ProceedingJoinPoint joinPoint, DurationMonitor durationMonitor) throws Throwable {
        Long start =  System.currentTimeMillis();
        Object result = joinPoint.proceed();
        Long endTime =System.currentTimeMillis();

        Long duration = endTime - start;

        log.info("warn={}", durationMonitor.w());
        log.info("error={}", durationMonitor.e());

        if(duration > durationMonitor.e() ){
            log.info("[error] API is too slow. duration : {}", duration / 1000);
        }
        if(duration > durationMonitor.w() ){
            log.info("[warn] API is slow. duration : {}", duration / 1000);
        }

        return result;
    }
}
