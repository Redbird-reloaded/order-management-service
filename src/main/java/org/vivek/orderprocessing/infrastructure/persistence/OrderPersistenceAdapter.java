package org.vivek.orderprocessing.infrastructure.persistence;

import org.springframework.stereotype.Repository;
import org.vivek.orderprocessing.domain.model.Order;
import org.vivek.orderprocessing.domain.model.OrderStatus;
import org.vivek.orderprocessing.domain.repository.OrderRepository;
import org.vivek.orderprocessing.infrastructure.persistence.entity.OrderEntity;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class OrderPersistenceAdapter implements OrderRepository {

    private final SpringDataOrderRepository springDataOrderRepository;

    public OrderPersistenceAdapter(SpringDataOrderRepository springDataOrderRepository) {
        this.springDataOrderRepository = springDataOrderRepository;
    }

    @Override
    public Order save(Order order) {
        OrderEntity entity = OrderEntity.fromDomain(order);
        return springDataOrderRepository.save(entity).toDomain();
    }

    @Override
    public Optional<Order> findById(UUID orderId) {
        return springDataOrderRepository.findWithItemsById(orderId)
                .map(OrderEntity::toDomain);
    }

    @Override
    public List<Order> findAll() {
        return springDataOrderRepository.findDistinctByOrderByCreatedAtDesc()
                .stream()
                .map(OrderEntity::toDomain)
                .toList();
    }

    @Override
    public List<Order> findByStatus(OrderStatus status) {
        return springDataOrderRepository.findDistinctByStatusOrderByCreatedAtDesc(status)
                .stream()
                .map(OrderEntity::toDomain)
                .toList();
    }

    @Override
    public void deleteById(UUID orderId) {
        springDataOrderRepository.deleteById(orderId);
    }

//    @Override
//    public List<Order> findByStatus(OrderStatus status) {
//        return springDataOrderRepository.findByStatus(status)
//                .stream()
//                .map(orderMapper::toDomain)
//                .toList();
//    }
}
