package com.study.aop.common.aop;

import com.study.aop.common.annotation.DurationMonitor;
import com.study.aop.common.utils.Alert.ConsoleUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class DurationMonitorAspect {

    private static final Map<String, Integer> OVER_TIME_DURATION_API_ERROR_MAP = new ConcurrentHashMap<>();
    private static final Map<String, Integer> OVER_TIME_DURATION_API_WARN_MAP = new ConcurrentHashMap<>();
    private final ConsoleUtils consoleUtils;

    /**
     *  warn, error 소요시간 설정 가능
     *  default : warn 1s / error 3s
     *  해당 시간 이상 소요시 로깅
     *  TODO :: 같은 API 호출, warn 10번 error 3번 이상 로깅시 알림 기능 추가
     */
    @Around("execution(* com.study.aop..*(..)) && @target(durationMonitor)")
    public Object doMonitor(ProceedingJoinPoint joinPoint, DurationMonitor durationMonitor) throws Throwable {


        String methodName= joinPoint.getSignature().toString();

        Long start =  System.currentTimeMillis();
        Object result = joinPoint.proceed();
        Long endTime =System.currentTimeMillis();

        Long duration = endTime - start;

        log.info("warn={}", durationMonitor.warn());
        log.info("error={}", durationMonitor.error());

        if(duration > durationMonitor.error() ){
            log.info("[error] API is too slow. duration : {}s", duration / 1000);
            checkDelayApiCount(OVER_TIME_DURATION_API_ERROR_MAP, methodName, durationMonitor.errorLimit());
        }
        else if(duration > durationMonitor.warn() ){
            log.info("[warn] API is slow. duration : {}s", duration / 1000);
            checkDelayApiCount(OVER_TIME_DURATION_API_WARN_MAP, methodName, durationMonitor.warnLimit());
        }

        return result;
    }

    private void checkDelayApiCount(Map<String, Integer> apiMap, String apiName, int limit) {
        Integer apiCount = apiMap.compute(apiName, (api, count) -> count == null ? 1 : count + 1);
        log.info("apiCount/limit :  {}/{}", apiCount, limit);
        if (apiCount >= limit) {
            sendAlarm(apiName);
            apiMap.computeIfPresent(apiName, (api, count) -> 0);
        }
    }

    public void sendAlarm(String apiName) {
        consoleUtils.sendAlert(apiName);
    }
}

