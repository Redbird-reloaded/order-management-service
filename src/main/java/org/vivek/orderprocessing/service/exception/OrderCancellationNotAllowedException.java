package org.vivek.orderprocessing.service.exception;

import java.util.UUID;

public class OrderCancellationNotAllowedException
        extends RuntimeException {

    public OrderCancellationNotAllowedException(UUID orderId) {
        super("Order " + orderId +
                " cannot be cancelled because it is not in PENDING status");
    }
}
