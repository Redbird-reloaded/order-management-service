package org.vivek.orderprocessing.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.vivek.orderprocessing.domain.repository.OrderRepository;
import org.vivek.orderprocessing.service.CancelOrderService;
import org.vivek.orderprocessing.service.exception.OrderNotFoundException;

import java.time.Clock;
import java.time.Instant;
import java.util.UUID;

@Service
@Slf4j
public class CancelOrderServiceImpl implements CancelOrderService {

    private final OrderRepository orderRepository;
    private final Clock clock;
    public CancelOrderServiceImpl(OrderRepository orderRepository, Clock clock) {
        this.orderRepository = orderRepository;
        this.clock = clock;
    }
    @Override
    public void cancel(UUID orderId) {
        var order = orderRepository.findById(orderId).orElseThrow(() ->
                new OrderNotFoundException(orderId));
        var cancelledOrder = order.cancel(Instant.now(clock));
        orderRepository.save(cancelledOrder);
    }
}
