package org.vivek.orderprocessing.application.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.vivek.orderprocessing.service.impl.ListOrdersServiceImpl;
import org.vivek.orderprocessing.service.exception.InvalidOrderStatusException;
import org.vivek.orderprocessing.controller.dto.OrderResponse;
import org.vivek.orderprocessing.domain.model.Order;
import org.vivek.orderprocessing.domain.model.OrderItem;
import org.vivek.orderprocessing.domain.model.OrderStatus;
import org.vivek.orderprocessing.domain.repository.OrderRepository;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ListOrdersServiceImplTest {

    private static final Instant NOW = Instant.parse("2026-06-20T12:00:00Z");

    @Mock
    private OrderRepository orderRepository;

    private ListOrdersServiceImpl listOrdersServiceImpl;

    @BeforeEach
    void setUp() {
        listOrdersServiceImpl = new ListOrdersServiceImpl(orderRepository);
    }

    @Test
    void listOrdersReturnsAllOrdersWhenStatusIsMissing() {
        when(orderRepository.findAll()).thenReturn(List.of(order(OrderStatus.PENDING)));

        List<OrderResponse> response = listOrdersServiceImpl.listOrders(null);

        assertThat(response).hasSize(1);
        assertThat(response.getFirst().status()).isEqualTo(OrderStatus.PENDING);
        verify(orderRepository).findAll();
    }

    @Test
    void listOrdersFiltersByStatusIgnoringCase() {
        when(orderRepository.findByStatus(OrderStatus.SHIPPED))
                .thenReturn(List.of(order(OrderStatus.SHIPPED)));

        List<OrderResponse> response = listOrdersServiceImpl.listOrders("shipped");

        assertThat(response).hasSize(1);
        assertThat(response.getFirst().status()).isEqualTo(OrderStatus.SHIPPED);
        verify(orderRepository).findByStatus(OrderStatus.SHIPPED);
    }

    @Test
    void listOrdersRejectsUnknownStatus() {
        assertThatThrownBy(() -> listOrdersServiceImpl.listOrders("UNKNOWN"))
                .isInstanceOf(InvalidOrderStatusException.class)
                .hasMessageContaining("Valid statuses are");

        verifyNoInteractions(orderRepository);
    }

    private Order order(OrderStatus status) {
        return new Order(
                UUID.fromString("7e6b5ad0-f0a1-46c8-9d73-bd4817bf7d2a"),
                "Jane Doe",
                "jane@example.com",
                status,
                NOW,
                NOW,
                List.of(new OrderItem(
                        UUID.fromString("0e1c4e15-490b-4282-8543-fb26f868ed48"),
                        "SKU-123",
                        "Keyboard",
                        2,
                        new BigDecimal("19.99")
                ))
        );
    }
}
