package org.vivek.orderprocessing.service.exception;

import java.util.List;

public class InvalidOrderStatusException extends RuntimeException {

    public InvalidOrderStatusException(String status, List<String> validStatuses) {
        super("Invalid order status '%s'. Valid statuses are: %s".formatted(
                status,
                String.join(", ", validStatuses)
        ));
    }

    public InvalidOrderStatusException(String status) {
        super("Invalid status transition from "
                + status + " to " + status);
    }
}
