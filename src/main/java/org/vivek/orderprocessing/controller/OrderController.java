package org.vivek.orderprocessing.controller;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.vivek.orderprocessing.controller.dto.*;
import org.vivek.orderprocessing.service.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/orders")
public class OrderController {

    private static final Logger log = LoggerFactory.getLogger(OrderController.class);

    private final CreateOrderService createOrderService;
    private final ListOrdersService listOrdersService;
    private final RetrieveOrderDetailsService retrieveOrderDetailsService;
    private final CancelOrderService cancelOrderService;
    private final UpdateOrderStatusService updateOrderStatusService;

    public OrderController(
            CreateOrderService createOrderService,
            ListOrdersService listOrdersService,
            RetrieveOrderDetailsService retrieveOrderDetailsService,
            CancelOrderService cancelOrderService,
            UpdateOrderStatusService updateOrderStatusService
    ) {
        this.createOrderService = createOrderService;
        this.listOrdersService = listOrdersService;
        this.retrieveOrderDetailsService = retrieveOrderDetailsService;
        this.cancelOrderService = cancelOrderService;
        this.updateOrderStatusService = updateOrderStatusService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<OrderResponse>>> listOrders(
            @RequestParam(required = false) String status
    ) {
        log.info("Received list orders request with status={}", status);
        List<OrderResponse> response = listOrdersService.listOrders(status);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<ApiResponse<OrderDetailsResponse>> retrieveOrderDetails(
            @PathVariable UUID orderId
    ) {
        log.info("Received retrieve order details request for orderId={}", orderId);
        OrderDetailsResponse response = retrieveOrderDetailsService.retrieveOrderDetails(orderId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<OrderResponse>> createOrder(
            @Valid @RequestBody CreateOrderRequest request
    ) {
        log.info("Received create order request for productCode={}", request.productCode());
        OrderResponse response = createOrderService.createOrder(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(response));
    }

    @PatchMapping("/{orderId}/cancel")
    @Operation(
            summary = "Cancel an order",
            description = """
        Cancels an order that is currently in PENDING status.
        
        Orders are not physically deleted from the system. Cancellation is
        represented as a state transition to CANCELLED in order to preserve
        audit history and operational traceability.
        """
    )
    public ResponseEntity<Void> cancelOrder(
            @PathVariable UUID orderId) {

        cancelOrderService.cancel(orderId);

        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{orderId}/status")
    public ResponseEntity<ApiResponse<OrderResponse>> updateStatus(
            @PathVariable UUID orderId,
            @RequestBody UpdateOrderStatusRequest request) {

        OrderResponse response =
                updateOrderStatusService.update(
                        orderId,
                        request.status()
                );

        return ResponseEntity.ok(
                ApiResponse.success(response)
        );
    }
}
