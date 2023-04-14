package com.study.aop.order;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;

    public void orderItem(String orderItem) {
        sleep(orderItem);
        orderRepository.save(orderItem);
    }

    private void sleep(int millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private void sleep(String millis) {
        try {
            Thread.sleep(Integer.parseInt(millis));
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
