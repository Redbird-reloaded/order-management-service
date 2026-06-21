package org.vivek.orderprocessing.service;

import java.util.UUID;

public interface CancelOrderService {

    void cancel(UUID orderId);
}
