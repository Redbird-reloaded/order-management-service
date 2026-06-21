package org.vivek.orderprocessing.scheduler.service;

import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import org.vivek.orderprocessing.domain.model.Order;
import org.vivek.orderprocessing.domain.model.OrderStatus;
import org.vivek.orderprocessing.domain.repository.OrderRepository;

import java.time.Clock;
import java.time.Instant;
import java.util.List;

@Service
public class UpdatePendingOrdersService {

    private final OrderRepository orderRepository;
    private final Clock clock;

    UpdatePendingOrdersService(OrderRepository orderRepository,
                                 Clock clock){
        this.orderRepository = orderRepository;
        this.clock = clock;

    }
    @Transactional
    public void processPendingOrders() {

        List<Order> pendingOrders =
                orderRepository.findByStatus(OrderStatus.PENDING);

        pendingOrders.stream()
                .map(order ->
                        order.updateStatus(
                                OrderStatus.PROCESSING,
                                Instant.now(clock)
                        )
                )
                .forEach(orderRepository::save);
    }
}
