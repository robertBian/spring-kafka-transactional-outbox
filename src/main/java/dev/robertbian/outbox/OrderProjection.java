package dev.robertbian.outbox;

import jakarta.persistence.*;
import java.util.UUID;

@Entity
@Table(name="order_projections")
public class OrderProjection {
    @Id public UUID id;
    @Column(nullable=false) public String sku;
    @Column(nullable=false) public int quantity;
    @Column(nullable=false) public String state;
    protected OrderProjection() {}
    public OrderProjection(OrderCreatedEvent event) {
        id = event.orderId(); sku = event.sku(); quantity = event.quantity(); state = "RECEIVED";
    }
}
