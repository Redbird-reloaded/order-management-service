package org.vivek.orderprocessing.application.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.vivek.orderprocessing.service.impl.RetrieveOrderDetailsServiceImpl;
import org.vivek.orderprocessing.service.exception.OrderNotFoundException;
import org.vivek.orderprocessing.controller.dto.OrderDetailsResponse;
import org.vivek.orderprocessing.domain.model.Order;
import org.vivek.orderprocessing.domain.model.OrderItem;
import org.vivek.orderprocessing.domain.model.OrderStatus;
import org.vivek.orderprocessing.domain.repository.OrderRepository;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RetrieveOrderDetailsServiceImplTest {

    private static final UUID ORDER_ID = UUID.fromString("7e6b5ad0-f0a1-46c8-9d73-bd4817bf7d2a");
    private static final UUID ITEM_ID = UUID.fromString("0e1c4e15-490b-4282-8543-fb26f868ed48");
    private static final Instant NOW = Instant.parse("2026-06-20T12:00:00Z");

    @Mock
    private OrderRepository orderRepository;

    private RetrieveOrderDetailsServiceImpl retrieveOrderDetailsServiceImpl;

    @BeforeEach
    void setUp() {
        retrieveOrderDetailsServiceImpl = new RetrieveOrderDetailsServiceImpl(orderRepository);
    }

    @Test
    void retrieveOrderDetailsReturnsOrderWithItems() {
        when(orderRepository.findById(ORDER_ID)).thenReturn(Optional.of(order()));

        OrderDetailsResponse response = retrieveOrderDetailsServiceImpl.retrieveOrderDetails(ORDER_ID);

        assertThat(response.orderId()).isEqualTo(ORDER_ID);
        assertThat(response.status()).isEqualTo(OrderStatus.PENDING);
        assertThat(response.createdAt()).isEqualTo(NOW);
        assertThat(response.updatedAt()).isEqualTo(NOW);
        assertThat(response.items()).hasSize(1);
        assertThat(response.items().getFirst().productId()).isEqualTo("SKU-123");
        verify(orderRepository).findById(ORDER_ID);
    }

    @Test
    void retrieveOrderDetailsThrowsBusinessExceptionWhenOrderDoesNotExist() {
        when(orderRepository.findById(ORDER_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> retrieveOrderDetailsServiceImpl.retrieveOrderDetails(ORDER_ID))
                .isInstanceOf(OrderNotFoundException.class)
                .hasMessage("Order not found with id: " + ORDER_ID);

        verify(orderRepository).findById(ORDER_ID);
    }

    private Order order() {
        return new Order(
                ORDER_ID,
                "Jane Doe",
                "jane@example.com",
                OrderStatus.PENDING,
                NOW,
                NOW,
                List.of(new OrderItem(
                        ITEM_ID,
                        "SKU-123",
                        "Keyboard",
                        2,
                        new BigDecimal("19.99")
                ))
        );
    }
}
