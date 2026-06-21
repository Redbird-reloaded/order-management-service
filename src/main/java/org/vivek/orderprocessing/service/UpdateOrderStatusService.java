package org.vivek.orderprocessing.service;

import org.vivek.orderprocessing.controller.dto.OrderResponse;
import org.vivek.orderprocessing.domain.model.OrderStatus;

import java.util.UUID;

public interface UpdateOrderStatusService {
    OrderResponse update(UUID orderId, OrderStatus status
    );
}
