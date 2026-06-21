package org.vivek.orderprocessing.service;

import org.vivek.orderprocessing.controller.dto.OrderResponse;
import java.util.List;

public interface ListOrdersService {

    List<OrderResponse> listOrders(String status);
}
