package org.vivek.orderprocessing.controller.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import java.util.List;

public record CreateOrderRequest(
        @NotBlank(message = "customerName is required")
        String customerName,

        @NotBlank(message = "customerEmail is required")
        @Email(message = "customerEmail must be a valid email address")
        String customerEmail,

        String productCode,

        @Positive(message = "quantity must be greater than zero")
        Integer quantity,

        @DecimalMin(value = "0.01", message = "unitPrice must be greater than zero")
        BigDecimal unitPrice,

        @Valid
        List<@NotNull(message = "item is required") @Valid CreateOrderItemRequest> items
) {

    public CreateOrderRequest(
            String customerName,
            String customerEmail,
            String productCode,
            Integer quantity,
            BigDecimal unitPrice
    ) {
        this(customerName, customerEmail, productCode, quantity, unitPrice, null);
    }

    @AssertTrue(message = "items are required")
    public boolean isItemsOrLegacyItemPresent() {
        return hasItems() || hasLegacyItem();
    }

    private boolean hasItems() {
        return items != null && !items.isEmpty();
    }

    private boolean hasLegacyItem() {
        return productCode != null && !productCode.isBlank()
                && quantity != null && quantity > 0
                && unitPrice != null && unitPrice.compareTo(new BigDecimal("0.01")) >= 0;
    }
}
