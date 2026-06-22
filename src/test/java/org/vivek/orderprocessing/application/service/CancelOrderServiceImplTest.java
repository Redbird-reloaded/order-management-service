package org.vivek.orderprocessing.application.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.vivek.orderprocessing.domain.model.Order;
import org.vivek.orderprocessing.domain.model.OrderItem;
import org.vivek.orderprocessing.domain.model.OrderStatus;
import org.vivek.orderprocessing.domain.repository.OrderRepository;
import org.vivek.orderprocessing.service.exception.OrderNotFoundException;
import org.vivek.orderprocessing.service.impl.CancelOrderServiceImpl;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CancelOrderServiceImplTest {

    private static final Instant NOW =
            Instant.parse("2026-06-20T12:00:00Z");

    private static final UUID ORDER_ID =
            UUID.fromString("7e6b5ad0-f0a1-46c8-9d73-bd4817bf7d2a");

    @Mock
    private OrderRepository orderRepository;

    private final Clock clock =
            Clock.fixed(NOW, ZoneOffset.UTC);

    private CancelOrderServiceImpl service;

    @BeforeEach
    void setUp() {
        service = new CancelOrderServiceImpl(
                orderRepository,
                clock
        );
    }

    @Test
    void cancelPendingOrderSuccessfully() {

        Order pendingOrder = pendingOrder();

        when(orderRepository.findById(ORDER_ID))
                .thenReturn(Optional.of(pendingOrder));

        service.cancel(ORDER_ID);

        ArgumentCaptor<Order> captor =
                ArgumentCaptor.forClass(Order.class);

        verify(orderRepository).save(captor.capture());

        Order cancelledOrder = captor.getValue();

        assertThat(cancelledOrder.status())
                .isEqualTo(OrderStatus.CANCELLED);

        assertThat(cancelledOrder.updatedAt())
                .isEqualTo(NOW);
    }

    @Test
    void throwsWhenOrderNotFound() {

        when(orderRepository.findById(ORDER_ID))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.cancel(ORDER_ID))
                .isInstanceOf(OrderNotFoundException.class);

        verify(orderRepository, never()).save(any());
    }

    @Test
    void throwsWhenOrderAlreadyShipped() {

        when(orderRepository.findById(ORDER_ID))
                .thenReturn(Optional.of(shippedOrder()));

        assertThatThrownBy(() -> service.cancel(ORDER_ID))
                .isInstanceOf(RuntimeException.class);

        verify(orderRepository, never()).save(any());
    }

    private Order pendingOrder() {
        return new Order(
                ORDER_ID,
                "Jane Doe",
                "jane@test.com",
                OrderStatus.PENDING,
                NOW,
                NOW,
                List.of(
                        new OrderItem(
                                UUID.randomUUID(),
                                "SKU-1",
                                "Keyboard",
                                1,
                                BigDecimal.TEN
                        )
                )
        );
    }

    private Order shippedOrder() {
        return new Order(
                ORDER_ID,
                "Jane Doe",
                "jane@test.com",
                OrderStatus.SHIPPED,
                NOW,
                NOW,
                List.of(
                        new OrderItem(
                                UUID.randomUUID(),
                                "SKU-1",
                                "Keyboard",
                                1,
                                BigDecimal.TEN
                        )
                )
        );
    }
}
