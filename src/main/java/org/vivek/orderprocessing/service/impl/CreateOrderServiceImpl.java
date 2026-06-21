package org.vivek.orderprocessing.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.vivek.orderprocessing.controller.dto.CreateOrderRequest;
import org.vivek.orderprocessing.controller.dto.OrderResponse;
import org.vivek.orderprocessing.domain.model.Order;
import org.vivek.orderprocessing.domain.model.OrderItem;
import org.vivek.orderprocessing.domain.repository.OrderRepository;
import org.vivek.orderprocessing.service.CreateOrderService;

import java.time.Clock;
import java.time.Instant;
import java.util.List;

@Service
public class CreateOrderServiceImpl implements CreateOrderService {

    private static final Logger log = LoggerFactory.getLogger(CreateOrderServiceImpl.class);

    private final OrderRepository orderRepository;
    private final Clock clock;

    public CreateOrderServiceImpl(OrderRepository orderRepository, Clock clock) {
        this.orderRepository = orderRepository;
        this.clock = clock;
    }

    @Override
    @Transactional
    public OrderResponse createOrder(CreateOrderRequest request) {
        Instant now = Instant.now(clock);
        Order order = Order.create(
                request.customerName(),
                request.customerEmail(),
                toOrderItems(request),
                now
        );

        Order savedOrder = orderRepository.save(order);
        log.info("Created order id={} status={}", savedOrder.id(), savedOrder.status());
        return OrderResponse.from(savedOrder);
    }

    private List<OrderItem> toOrderItems(CreateOrderRequest request) {
        if (request.items() != null && !request.items().isEmpty()) {
            return request.items()
                    .stream()
                    .map(item -> OrderItem.create(
                            item.productId(),
                            item.productName(),
                            item.quantity(),
                            item.price()
                    ))
                    .toList();
        }

        return List.of(OrderItem.create(
                request.productCode(),
                request.productCode(),
                request.quantity(),
                request.unitPrice()
        ));
    }
}
