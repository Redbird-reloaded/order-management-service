package org.vivek.orderprocessing.controller.dto;

import org.vivek.orderprocessing.domain.model.OrderStatus;

public record UpdateOrderStatusRequest(
        OrderStatus status
) {
}
