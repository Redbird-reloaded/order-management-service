package org.vivek.orderprocessing.service;

import org.vivek.orderprocessing.controller.dto.OrderDetailsResponse;
import java.util.UUID;

public interface RetrieveOrderDetailsService {

    OrderDetailsResponse retrieveOrderDetails(UUID orderId);
}
