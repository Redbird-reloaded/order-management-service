package org.vivek.orderprocessing.controller.dto;

import org.vivek.orderprocessing.domain.model.Order;
import org.vivek.orderprocessing.domain.model.OrderStatus;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record OrderDetailsResponse(
        UUID orderId,
        OrderStatus status,
        Instant createdAt,
        Instant updatedAt,
        List<OrderItemResponse> items
) {

    public static OrderDetailsResponse from(Order order) {
        return new OrderDetailsResponse(
                order.id(),
                order.status(),
                order.createdAt(),
                order.updatedAt(),
                order.items()
                        .stream()
                        .map(OrderItemResponse::from)
                        .toList()
        );
    }
}
