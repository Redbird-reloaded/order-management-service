package org.vivek.orderprocessing.domain.repository;

import org.vivek.orderprocessing.domain.model.Order;
import org.vivek.orderprocessing.domain.model.OrderStatus;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface OrderRepository {

    Order save(Order order);

    Optional<Order> findById(UUID orderId);

    List<Order> findAll();

    List<Order> findByStatus(OrderStatus status);

    void deleteById(UUID orderId);

}
