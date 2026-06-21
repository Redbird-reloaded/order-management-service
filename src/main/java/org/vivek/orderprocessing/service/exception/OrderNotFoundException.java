package org.vivek.orderprocessing.service.exception;

import java.util.UUID;

public class OrderNotFoundException extends RuntimeException {

    public OrderNotFoundException(UUID orderId) {
        super("Order not found with id: %s".formatted(orderId));
    }
}
