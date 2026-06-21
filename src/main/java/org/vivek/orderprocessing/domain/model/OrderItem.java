package org.vivek.orderprocessing.domain.model;

import java.math.BigDecimal;
import java.util.UUID;

public record OrderItem(
        UUID id,
        String productId,
        String productName,
        int quantity,
        BigDecimal price
) {

    public static OrderItem create(
            String productId,
            String productName,
            int quantity,
            BigDecimal price
    ) {
        return new OrderItem(null, productId, productName, quantity, price);
    }

    public BigDecimal lineTotal() {
        return price.multiply(BigDecimal.valueOf(quantity));
    }
}
