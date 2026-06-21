package org.vivek.orderprocessing.infrastructure.persistence.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import org.vivek.orderprocessing.domain.model.Order;
import org.vivek.orderprocessing.domain.model.OrderStatus;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "orders")
public class OrderEntity {

    @Id
    @Column(nullable = false, updatable = false)
    private UUID id;

    @Column(nullable = false, length = 100)
    private String customerName;

    @Column(nullable = false)
    private String customerEmail;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private OrderStatus status;

    @Column(nullable = false)
    private Instant createdAt;

    @Column(nullable = false)
    private Instant updatedAt;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItemEntity> items = new ArrayList<>();

    protected OrderEntity() {
    }

    private OrderEntity(Order order) {
        this.id = order.id() == null ? UUID.randomUUID() : order.id();
        this.customerName = order.customerName();
        this.customerEmail = order.customerEmail();
        this.status = order.status();
        this.createdAt = order.createdAt();
        this.updatedAt = order.updatedAt();
        replaceItems(order);
    }

    public static OrderEntity fromDomain(Order order) {
        return new OrderEntity(order);
    }

    public Order toDomain() {
        return new Order(
                id,
                customerName,
                customerEmail,
                status,
                createdAt,
                updatedAt,
                items.stream()
                        .map(OrderItemEntity::toDomain)
                        .toList()
        );
    }

    private void replaceItems(Order order) {
        items.clear();
        order.items()
                .stream()
                .map(item -> OrderItemEntity.fromDomain(item, this))
                .forEach(items::add);
    }
}
