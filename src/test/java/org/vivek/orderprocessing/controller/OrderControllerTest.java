package org.vivek.orderprocessing.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.vivek.orderprocessing.service.*;
import org.vivek.orderprocessing.service.exception.InvalidOrderStatusException;
import org.vivek.orderprocessing.service.exception.OrderNotFoundException;
import org.vivek.orderprocessing.controller.dto.CreateOrderRequest;
import org.vivek.orderprocessing.controller.dto.OrderDetailsResponse;
import org.vivek.orderprocessing.controller.dto.OrderItemResponse;
import org.vivek.orderprocessing.controller.dto.OrderResponse;
import org.vivek.orderprocessing.domain.model.OrderStatus;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(OrderController.class)
@Import(GlobalExceptionHandler.class)
class OrderControllerTest {

    private static final UUID ORDER_ID = UUID.fromString("7e6b5ad0-f0a1-46c8-9d73-bd4817bf7d2a");
    private static final Instant NOW = Instant.parse("2026-06-20T12:00:00Z");

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CreateOrderService createOrderService;

    @MockBean
    private ListOrdersService listOrdersService;

    @MockBean
    private RetrieveOrderDetailsService retrieveOrderDetailsService;

    @MockBean
    private CancelOrderService cancelOrderService;

    @MockBean
    private UpdateOrderStatusService updateOrderStatusService;

    @Test
    void retrieveOrderDetailsReturnsOrderWithItems() throws Exception {
        when(retrieveOrderDetailsService.retrieveOrderDetails(ORDER_ID))
                .thenReturn(orderDetailsResponse());

        mockMvc.perform(get("/api/v1/orders/{orderId}", ORDER_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("success"))
                .andExpect(jsonPath("$.data.orderId").value(ORDER_ID.toString()))
                .andExpect(jsonPath("$.data.status").value("PENDING"))
                .andExpect(jsonPath("$.data.createdAt").value("2026-06-20T12:00:00Z"))
                .andExpect(jsonPath("$.data.updatedAt").value("2026-06-20T12:00:00Z"))
                .andExpect(jsonPath("$.data.items[0].productId").value("SKU-123"))
                .andExpect(jsonPath("$.data.items[0].productName").value("Keyboard"));

        verify(retrieveOrderDetailsService).retrieveOrderDetails(ORDER_ID);
    }

    @Test
    void retrieveOrderDetailsReturnsNotFoundWhenOrderDoesNotExist() throws Exception {
        when(retrieveOrderDetailsService.retrieveOrderDetails(ORDER_ID))
                .thenThrow(new OrderNotFoundException(ORDER_ID));

        mockMvc.perform(get("/api/v1/orders/{orderId}", ORDER_ID))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("Order not found with id: " + ORDER_ID))
                .andExpect(jsonPath("$.path").value("/api/v1/orders/" + ORDER_ID));

        verify(retrieveOrderDetailsService).retrieveOrderDetails(ORDER_ID);
    }

    @Test
    void listOrdersReturnsAllOrders() throws Exception {
        when(listOrdersService.listOrders(null)).thenReturn(List.of(orderResponse()));

        mockMvc.perform(get("/api/v1/orders"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("success"))
                .andExpect(jsonPath("$.data[0].id").value(ORDER_ID.toString()))
                .andExpect(jsonPath("$.data[0].status").value("PENDING"))
                .andExpect(jsonPath("$.data[0].items[0].productName").value("Keyboard"));

        verify(listOrdersService).listOrders(null);
    }

    @Test
    void listOrdersPassesOptionalStatusFilter() throws Exception {
        when(listOrdersService.listOrders(eq("PENDING"))).thenReturn(List.of(orderResponse()));

        mockMvc.perform(get("/api/v1/orders").param("status", "PENDING"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("success"))
                .andExpect(jsonPath("$.data[0].status").value("PENDING"));

        verify(listOrdersService).listOrders("PENDING");
    }

    @Test
    void listOrdersReturnsBadRequestWhenStatusIsInvalid() throws Exception {
        when(listOrdersService.listOrders(eq("UNKNOWN")))
                .thenThrow(new InvalidOrderStatusException("UNKNOWN", List.of("PENDING")));

        mockMvc.perform(get("/api/v1/orders").param("status", "UNKNOWN"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("Invalid order status 'UNKNOWN'. Valid statuses are: PENDING"))
                .andExpect(jsonPath("$.path").value("/api/v1/orders"));

        verify(listOrdersService).listOrders("UNKNOWN");
    }

    @Test
    void createOrderReturnsCreatedOrderForMultipleItems() throws Exception {
        when(createOrderService.createOrder(any(CreateOrderRequest.class))).thenReturn(orderResponse());

        mockMvc.perform(post("/api/v1/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "customerName": "Jane Doe",
                                  "customerEmail": "jane@example.com",
                                  "items": [
                                    {
                                      "productId": "SKU-123",
                                      "productName": "Keyboard",
                                      "quantity": 2,
                                      "price": 19.99
                                    }
                                  ]
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message").value("success"))
                .andExpect(jsonPath("$.data.id").value(ORDER_ID.toString()))
                .andExpect(jsonPath("$.data.status").value("PENDING"))
                .andExpect(jsonPath("$.data.totalAmount").value(39.98))
                .andExpect(jsonPath("$.data.items[0].productId").value("SKU-123"))
                .andExpect(jsonPath("$.data.items[0].productName").value("Keyboard"));

        verify(createOrderService).createOrder(any(CreateOrderRequest.class));
    }

    @Test
    void createOrderStillAcceptsLegacySingleItemPayload() throws Exception {
        when(createOrderService.createOrder(any(CreateOrderRequest.class))).thenReturn(orderResponse());

        mockMvc.perform(post("/api/v1/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "customerName": "Jane Doe",
                                  "customerEmail": "jane@example.com",
                                  "productCode": "SKU-123",
                                  "quantity": 2,
                                  "unitPrice": 19.99
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message").value("success"))
                .andExpect(jsonPath("$.data.productCode").value("SKU-123"))
                .andExpect(jsonPath("$.data.quantity").value(2))
                .andExpect(jsonPath("$.data.unitPrice").value(19.99))
                .andExpect(jsonPath("$.data.items[0].productName").value("Keyboard"));

        verify(createOrderService).createOrder(any(CreateOrderRequest.class));
    }

    @Test
    void createOrderReturnsBadRequestWhenRequestIsInvalid() throws Exception {
        mockMvc.perform(post("/api/v1/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "customerName": "",
                                  "customerEmail": "invalid-email",
                                  "productCode": "",
                                  "quantity": 0,
                                  "unitPrice": 0
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.path").value("/api/v1/orders"));

        verifyNoInteractions(createOrderService);
    }

    @Test
    void cancelOrderReturnsNoContent() throws Exception {

        mockMvc.perform(
                        patch("/api/v1/orders/{orderId}/cancel", ORDER_ID)
                )
                .andExpect(status().isNoContent());

        verify(cancelOrderService).cancel(ORDER_ID);
    }

    @Test
    void updateStatusReturnsUpdatedOrder() throws Exception {

        when(updateOrderStatusService.update(
                eq(ORDER_ID),
                eq(OrderStatus.SHIPPED)
        )).thenReturn(shippedOrderResponse());

        mockMvc.perform(
                        patch("/api/v1/orders/{orderId}/status", ORDER_ID)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                {
                                  "status": "SHIPPED"
                                }
                                """)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("success"))
                .andExpect(jsonPath("$.data.status").value("SHIPPED"));

        verify(updateOrderStatusService)
                .update(ORDER_ID, OrderStatus.SHIPPED);
    }

    private OrderResponse orderResponse() {
        return new OrderResponse(
                ORDER_ID,
                "Jane Doe",
                "jane@example.com",
                "SKU-123",
                2,
                new BigDecimal("19.99"),
                new BigDecimal("39.98"),
                OrderStatus.PENDING,
                NOW,
                NOW,
                List.of(new OrderItemResponse(
                        UUID.fromString("0e1c4e15-490b-4282-8543-fb26f868ed48"),
                        "SKU-123",
                        "Keyboard",
                        2,
                        new BigDecimal("19.99"),
                        new BigDecimal("39.98")
                ))
        );
    }

    private OrderDetailsResponse orderDetailsResponse() {
        return new OrderDetailsResponse(
                ORDER_ID,
                OrderStatus.PENDING,
                NOW,
                NOW,
                List.of(new OrderItemResponse(
                        UUID.fromString("0e1c4e15-490b-4282-8543-fb26f868ed48"),
                        "SKU-123",
                        "Keyboard",
                        2,
                        new BigDecimal("19.99"),
                        new BigDecimal("39.98")
                ))
        );
    }

    private OrderResponse shippedOrderResponse() {

        return new OrderResponse(
                ORDER_ID,
                "Jane Doe",
                "jane@example.com",
                "SKU-123",
                2,
                new BigDecimal("19.99"),
                new BigDecimal("39.98"),
                OrderStatus.SHIPPED,
                NOW,
                NOW,
                List.of(
                        new OrderItemResponse(
                                UUID.fromString(
                                        "0e1c4e15-490b-4282-8543-fb26f868ed48"
                                ),
                                "SKU-123",
                                "Keyboard",
                                2,
                                new BigDecimal("19.99"),
                                new BigDecimal("39.98")
                        )
                )
        );
    }
}
