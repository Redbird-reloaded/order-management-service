package org.vivek.orderprocessing.controller.dto;

import java.time.OffsetDateTime;

public record ErrorResponse(
        OffsetDateTime timestamp,
        int status,
        String message,
        String path
) {
}
