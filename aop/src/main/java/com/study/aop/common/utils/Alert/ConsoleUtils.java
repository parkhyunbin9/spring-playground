package com.study.aop.common.utils.Alert;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ConsoleUtils {

    public void sendAlert(String message) {
        log.info("[CONSOLE] notice - {}", message);
    }
}
