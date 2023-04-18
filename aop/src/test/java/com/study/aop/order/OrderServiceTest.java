package com.study.aop.order;

import com.study.aop.common.aop.DurationMonitorAspect;
import com.study.aop.common.utils.Alert.ConsoleUtils;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;


@Slf4j
@Import(DurationMonitorAspect.class)
@SpringBootTest
class OrderServiceTest {

    @Autowired
    OrderService orderService;

    @MockBean
    ConsoleUtils mockConsoleUtils;

    @Test
    void success() {
        assertThat(AopUtils.isAopProxy(orderService)).isTrue();
        orderService.orderItem("1000");
    }

    @Test
    @DisplayName("1초이상 소요되면 warn log")
    void warnLog() {
        orderService.orderItem("900");
        orderService.orderItem("1000");
        orderService.orderItem("1100");
    }

    @Test
    @DisplayName("3초이상 소요되면 error log")
    void errorLog() {
        orderService.orderItem("2900");
        orderService.orderItem("3000");
        orderService.orderItem("3100");
    }
    @Test
    @DisplayName("1초이상 소요되는 같은 메서드가 10회이상 호출되면 warn Type의 알림 호출")
    void warnAlert() {

        orderService.orderItem("900");
        orderService.orderItem("1000");
        orderService.orderItem("1100");
        orderService.orderItem("1200");
        orderService.orderItem("1300");
        orderService.orderItem("1400");
        orderService.orderItem("1500");
        orderService.orderItem("1600");
        orderService.orderItem("1700");
        orderService.orderItem("1800");
        verify(mockConsoleUtils, never()).sendAlert(any());

        orderService.orderItem("1900");
        verify(mockConsoleUtils, times(1)).sendAlert(any());
    }

    @Test
    @DisplayName("3초이상 소요되는 같은 메서드가 3회이상 호출되면 Error Type의 알림 호출")
    void errorAlert() {

        orderService.orderItem("2900");
        orderService.orderItem("3000");
        orderService.orderItem("3100");
        verify(mockConsoleUtils, never()).sendAlert(any());

        orderService.orderItem("3200");
        verify(mockConsoleUtils, times(1)).sendAlert(any());
    }

    @Test
    @DisplayName("알림 호출 후 해당 메서드 카운트 초기화")
    void errorAlertInit() {

        orderService.orderItem("2900");
        orderService.orderItem("3000");
        orderService.orderItem("3100");
        orderService.orderItem("3200");
        verify(mockConsoleUtils, times(1)).sendAlert(any());
        orderService.orderItem("3200");
        verify(mockConsoleUtils, times(1)).sendAlert(any());
        orderService.orderItem("3300");
        verify(mockConsoleUtils, times(1)).sendAlert(any());
        orderService.orderItem("3400");
        verify(mockConsoleUtils, times(2)).sendAlert(any());
    }
}