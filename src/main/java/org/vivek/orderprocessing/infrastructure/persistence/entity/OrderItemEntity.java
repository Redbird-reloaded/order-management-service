package org.vivek.orderprocessing.infrastructure.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import org.vivek.orderprocessing.domain.model.OrderItem;
import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "order_items")
public class OrderItemEntity {

    @Id
    @Column(nullable = false, updatable = false)
    private UUID id;

    @Column(nullable = false, length = 100)
    private String productId;

    @Column(nullable = false, length = 255)
    private String productName;

    @Column(nullable = false)
    private int quantity;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal price;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "order_id", nullable = false)
    private OrderEntity order;

    protected OrderItemEntity() {
    }

    private OrderItemEntity(OrderItem item, OrderEntity order) {
        this.id = item.id() == null ? UUID.randomUUID() : item.id();
        this.productId = item.productId();
        this.productName = item.productName();
        this.quantity = item.quantity();
        this.price = item.price();
        this.order = order;
    }

    static OrderItemEntity fromDomain(OrderItem item, OrderEntity order) {
        return new OrderItemEntity(item, order);
    }

    OrderItem toDomain() {
        return new OrderItem(id, productId, productName, quantity, price);
    }
}
