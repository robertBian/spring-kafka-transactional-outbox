package dev.robertbian.outbox;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "orders", uniqueConstraints = @UniqueConstraint(name="uk_order_request",columnNames="client_request_id"))
public class OrderEntity {
    @Id public UUID id;
    @Column(name="client_request_id", nullable=false, length=100) public String clientRequestId;
    @Column(nullable=false, length=64) public String sku;
    @Column(nullable=false) public int quantity;
    @Column(nullable=false) public Instant createdAt;

    protected OrderEntity() {}
    public OrderEntity(String requestId, String sku, int quantity) {
        id = UUID.randomUUID();
        clientRequestId = requestId;
        this.sku = sku;
        this.quantity = quantity;
        createdAt = Instant.now();
    }
}
