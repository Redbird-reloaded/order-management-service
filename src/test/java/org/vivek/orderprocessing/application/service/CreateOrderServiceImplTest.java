package org.vivek.orderprocessing.application.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.vivek.orderprocessing.controller.dto.CreateOrderItemRequest;
import org.vivek.orderprocessing.controller.dto.CreateOrderRequest;
import org.vivek.orderprocessing.controller.dto.OrderResponse;
import org.vivek.orderprocessing.domain.model.Order;
import org.vivek.orderprocessing.domain.model.OrderItem;
import org.vivek.orderprocessing.domain.model.OrderStatus;
import org.vivek.orderprocessing.domain.repository.OrderRepository;
import org.vivek.orderprocessing.service.impl.CreateOrderServiceImpl;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CreateOrderServiceImplTest {

    private static final Instant NOW = Instant.parse("2026-06-20T12:00:00Z");
    private static final UUID ORDER_ID = UUID.fromString("7e6b5ad0-f0a1-46c8-9d73-bd4817bf7d2a");
    private static final UUID ITEM_ID = UUID.fromString("0e1c4e15-490b-4282-8543-fb26f868ed48");

    @Mock
    private OrderRepository orderRepository;

    private final Clock clock = Clock.fixed(NOW, ZoneOffset.UTC);

    private CreateOrderServiceImpl createOrderServiceImpl;

    @BeforeEach
    void setUp() {
        createOrderServiceImpl = new CreateOrderServiceImpl(orderRepository, clock);
    }

    @Test
    void createOrderPersistsPendingOrderWithMultipleItemsAndReturnsResponse() {
        CreateOrderRequest request = new CreateOrderRequest(
                "Jane Doe",
                "jane@example.com",
                null,
                null,
                null,
                List.of(
                        new CreateOrderItemRequest("SKU-123", "Keyboard", 2, new BigDecimal("19.99")),
                        new CreateOrderItemRequest("SKU-456", "Mouse", 1, new BigDecimal("9.99"))
                )
        );
        when(orderRepository.save(any(Order.class))).thenReturn(savedOrderWithMultipleItems());

        OrderResponse response = createOrderServiceImpl.createOrder(request);

        assertThat(response.id()).isEqualTo(ORDER_ID);
        assertThat(response.status()).isEqualTo(OrderStatus.PENDING);
        assertThat(response.totalAmount()).isEqualByComparingTo("49.97");
        assertThat(response.items()).hasSize(2);
        assertThat(response.items().getFirst().productName()).isEqualTo("Keyboard");
        assertThat(response.createdAt()).isEqualTo(NOW);
        verify(orderRepository).save(any(Order.class));
    }

    @Test
    void createOrderBuildsDomainOrderFromMultipleItemsRequest() {
        CreateOrderRequest request = new CreateOrderRequest(
                "Jane Doe",
                "jane@example.com",
                null,
                null,
                null,
                List.of(new CreateOrderItemRequest("SKU-123", "Keyboard", 3, new BigDecimal("10.50")))
        );
        when(orderRepository.save(any(Order.class))).thenReturn(savedOrderWithMultipleItems());
        ArgumentCaptor<Order> orderCaptor = ArgumentCaptor.forClass(Order.class);

        createOrderServiceImpl.createOrder(request);

        verify(orderRepository).save(orderCaptor.capture());
        Order order = orderCaptor.getValue();
        assertThat(order.id()).isNull();
        assertThat(order.customerName()).isEqualTo("Jane Doe");
        assertThat(order.customerEmail()).isEqualTo("jane@example.com");
        assertThat(order.items()).hasSize(1);
        assertThat(order.items().getFirst().productId()).isEqualTo("SKU-123");
        assertThat(order.items().getFirst().productName()).isEqualTo("Keyboard");
        assertThat(order.items().getFirst().quantity()).isEqualTo(3);
        assertThat(order.items().getFirst().price()).isEqualByComparingTo("10.50");
        assertThat(order.totalAmount()).isEqualByComparingTo("31.50");
        assertThat(order.status()).isEqualTo(OrderStatus.PENDING);
        assertThat(order.createdAt()).isEqualTo(NOW);
        assertThat(order.updatedAt()).isEqualTo(NOW);
    }

    @Test
    void createOrderStillSupportsLegacySingleItemRequest() {
        CreateOrderRequest request = new CreateOrderRequest(
                "Jane Doe",
                "jane@example.com",
                "SKU-123",
                2,
                new BigDecimal("19.99")
        );
        when(orderRepository.save(any(Order.class))).thenReturn(savedOrderWithSingleItem());
        ArgumentCaptor<Order> orderCaptor = ArgumentCaptor.forClass(Order.class);

        OrderResponse response = createOrderServiceImpl.createOrder(request);

        verify(orderRepository).save(orderCaptor.capture());
        Order order = orderCaptor.getValue();
        assertThat(order.items()).hasSize(1);
        assertThat(order.items().getFirst().productId()).isEqualTo("SKU-123");
        assertThat(order.items().getFirst().productName()).isEqualTo("SKU-123");
        assertThat(response.productCode()).isEqualTo("SKU-123");
        assertThat(response.quantity()).isEqualTo(2);
        assertThat(response.unitPrice()).isEqualByComparingTo("19.99");
    }

    private Order savedOrderWithMultipleItems() {
        return new Order(
                ORDER_ID,
                "Jane Doe",
                "jane@example.com",
                OrderStatus.PENDING,
                NOW,
                NOW,
                List.of(
                        new OrderItem(ITEM_ID, "SKU-123", "Keyboard", 2, new BigDecimal("19.99")),
                        new OrderItem(UUID.randomUUID(), "SKU-456", "Mouse", 1, new BigDecimal("9.99"))
                )
        );
    }

    private Order savedOrderWithSingleItem() {
        return new Order(
                ORDER_ID,
                "Jane Doe",
                "jane@example.com",
                OrderStatus.PENDING,
                NOW,
                NOW,
                List.of(new OrderItem(ITEM_ID, "SKU-123", "SKU-123", 2, new BigDecimal("19.99")))
        );
    }
}
