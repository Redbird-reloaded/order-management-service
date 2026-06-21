package org.vivek.orderprocessing.infrastructure.persistence;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.vivek.orderprocessing.domain.model.OrderStatus;
import org.vivek.orderprocessing.infrastructure.persistence.entity.OrderEntity;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

interface SpringDataOrderRepository extends JpaRepository<OrderEntity, UUID> {

    @EntityGraph(attributePaths = "items")
    Optional<OrderEntity> findWithItemsById(UUID id);

    @EntityGraph(attributePaths = "items")
    List<OrderEntity> findDistinctByOrderByCreatedAtDesc();

    @EntityGraph(attributePaths = "items")
    List<OrderEntity> findDistinctByStatusOrderByCreatedAtDesc(OrderStatus status);
}
