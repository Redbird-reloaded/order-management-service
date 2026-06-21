package org.vivek.orderprocessing.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.vivek.orderprocessing.service.RetrieveOrderDetailsService;
import org.vivek.orderprocessing.service.exception.OrderNotFoundException;
import org.vivek.orderprocessing.controller.dto.OrderDetailsResponse;
import org.vivek.orderprocessing.domain.model.Order;
import org.vivek.orderprocessing.domain.repository.OrderRepository;
import java.util.UUID;

@Service
public class RetrieveOrderDetailsServiceImpl implements RetrieveOrderDetailsService {

    private final OrderRepository orderRepository;

    public RetrieveOrderDetailsServiceImpl(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public OrderDetailsResponse retrieveOrderDetails(UUID orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException(orderId));
        return OrderDetailsResponse.from(order);
    }
}
