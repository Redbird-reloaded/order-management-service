package org.vivek.orderprocessing.controller.dto;

import org.vivek.orderprocessing.domain.model.OrderItem;
import java.math.BigDecimal;
import java.util.UUID;

public record OrderItemResponse(
        UUID id,
        String productId,
        String productName,
        int quantity,
        BigDecimal price,
        BigDecimal lineTotal
) {

    public static OrderItemResponse from(OrderItem item) {
        return new OrderItemResponse(
                item.id(),
                item.productId(),
                item.productName(),
                item.quantity(),
                item.price(),
                item.lineTotal()
        );
    }
}
