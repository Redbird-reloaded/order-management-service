package org.vivek.orderprocessing.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.vivek.orderprocessing.service.ListOrdersService;
import org.vivek.orderprocessing.service.exception.InvalidOrderStatusException;
import org.vivek.orderprocessing.controller.dto.OrderResponse;
import org.vivek.orderprocessing.domain.model.OrderStatus;
import org.vivek.orderprocessing.domain.repository.OrderRepository;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

@Service
public class ListOrdersServiceImpl implements ListOrdersService {

    private final OrderRepository orderRepository;

    public ListOrdersServiceImpl(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderResponse> listOrders(String status) {
        return resolveStatus(status)
                .map(orderRepository::findByStatus)
                .orElseGet(orderRepository::findAll)
                .stream()
                .map(OrderResponse::from)
                .toList();
    }

    private java.util.Optional<OrderStatus> resolveStatus(String status) {
        if (status == null || status.isBlank()) {
            return java.util.Optional.empty();
        }

        try {
            return java.util.Optional.of(OrderStatus.valueOf(status.toUpperCase(Locale.ROOT)));
        } catch (IllegalArgumentException exception) {
            throw new InvalidOrderStatusException(status, validStatuses());
        }
    }

    private List<String> validStatuses() {
        return Arrays.stream(OrderStatus.values())
                .map(Enum::name)
                .toList();
    }
}
