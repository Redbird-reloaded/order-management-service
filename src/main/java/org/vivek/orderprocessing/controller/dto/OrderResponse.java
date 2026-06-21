package org.vivek.orderprocessing.controller.dto;

import org.vivek.orderprocessing.domain.model.Order;
import org.vivek.orderprocessing.domain.model.OrderStatus;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record OrderResponse(
        UUID id,
        String customerName,
        String customerEmail,
        String productCode,
        int quantity,
        BigDecimal unitPrice,
        BigDecimal totalAmount,
        OrderStatus status,
        Instant createdAt,
        Instant updatedAt,
        List<OrderItemResponse> items
) {

    public static OrderResponse from(Order order) {
        List<OrderItemResponse> itemResponses = order.items()
                .stream()
                .map(OrderItemResponse::from)
                .toList();

        return new OrderResponse(
                order.id(),
                order.customerName(),
                order.customerEmail(),
                firstProductId(itemResponses),
                firstQuantity(itemResponses),
                firstPrice(itemResponses),
                order.totalAmount(),
                order.status(),
                order.createdAt(),
                order.updatedAt(),
                itemResponses
        );
    }

    private static String firstProductId(List<OrderItemResponse> items) {
        return items.isEmpty() ? null : items.getFirst().productId();
    }

    private static int firstQuantity(List<OrderItemResponse> items) {
        return items.isEmpty() ? 0 : items.getFirst().quantity();
    }

    private static BigDecimal firstPrice(List<OrderItemResponse> items) {
        return items.isEmpty() ? null : items.getFirst().price();
    }
}
