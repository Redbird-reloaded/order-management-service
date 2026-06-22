package org.vivek.orderprocessing.service.impl;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.vivek.orderprocessing.controller.dto.OrderResponse;
import org.vivek.orderprocessing.domain.model.OrderStatus;
import org.vivek.orderprocessing.domain.repository.OrderRepository;
import org.vivek.orderprocessing.service.UpdateOrderStatusService;
import org.vivek.orderprocessing.service.exception.OrderNotFoundException;

import java.time.Clock;
import java.time.Instant;
import java.util.UUID;

@Service
@Slf4j
public class UpdateOrderStatusServiceImpl implements UpdateOrderStatusService {

    private final OrderRepository orderRepository;
    private final Clock clock;

    public UpdateOrderStatusServiceImpl(OrderRepository orderRepository, Clock clock){
        this.orderRepository = orderRepository;
        this.clock = clock;
    }

    @Override
    @Transactional
    public OrderResponse update(UUID orderId, OrderStatus status) {
        var order = orderRepository.findById(orderId)
                .orElseThrow(() ->
                        new OrderNotFoundException(orderId));

        var updatedOrder =
                order.updateStatus(
                        status,
                        Instant.now(clock)
                );

        return OrderResponse.from(
                orderRepository.save(updatedOrder)
        );
    }
}
