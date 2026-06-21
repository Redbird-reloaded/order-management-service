package org.vivek.orderprocessing.domain.model;

import org.vivek.orderprocessing.service.exception.InvalidOrderStatusException;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public record Order(
        UUID id,
        String customerName,
        String customerEmail,
        OrderStatus status,
        Instant createdAt,
        Instant updatedAt,
        List<OrderItem> items
) {

    public Order {
        items = List.copyOf(Objects.requireNonNull(items, "items is required"));
    }

    public static Order create(
            String customerName,
            String customerEmail,
            List<OrderItem> items,
            Instant createdAt
    ) {
        return new Order(
                null,
                customerName,
                customerEmail,
                OrderStatus.PENDING,
                createdAt,
                createdAt,
                items
        );
    }

    public BigDecimal totalAmount() {
        return items.stream()
                .map(OrderItem::lineTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public Order cancel(Instant cancelledAt) {

        if (status != OrderStatus.PENDING) {
            throw new IllegalStateException(
                    "Only PENDING orders can be cancelled"
            );
        }

        return new Order(
                id,
                customerName,
                customerEmail,
                OrderStatus.CANCELLED,
                createdAt,
                cancelledAt,
                items
        );
    }

    public Order updateStatus(
            OrderStatus newStatus,
            Instant updatedAt
    ) {

        boolean validTransition =
                switch (status) {

                    case PENDING ->
                            newStatus == OrderStatus.PROCESSING;

                    case PROCESSING ->
                            newStatus == OrderStatus.SHIPPED;

                    case SHIPPED ->
                            newStatus == OrderStatus.DELIVERED;

                    default -> false;
                };

        if (!validTransition) {
            throw new InvalidOrderStatusException(
                    "Invalid status transition from "
                            + status + " to " + newStatus
            );
        }
        return new Order(
                id,
                customerName,
                customerEmail,
                newStatus,
                createdAt,
                updatedAt,
                items
        );
    }
}
