package org.vivek.orderprocessing.service;

import org.vivek.orderprocessing.controller.dto.CreateOrderRequest;
import org.vivek.orderprocessing.controller.dto.OrderResponse;

public interface CreateOrderService {

    OrderResponse createOrder(CreateOrderRequest request);
}
