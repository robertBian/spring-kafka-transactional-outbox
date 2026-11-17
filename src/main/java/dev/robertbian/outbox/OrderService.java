package dev.robertbian.outbox;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OrderService {
    private final OrderRepository orders;
    private final OutboxRepository outbox;
    private final ObjectMapper mapper;

    public OrderService(OrderRepository orders, OutboxRepository outbox, ObjectMapper mapper) {
        this.orders = orders;
        this.outbox = outbox;
        this.mapper = mapper;
    }

    @Transactional
    public OrderEntity create(String idempotencyKey, String sku, int quantity) {
        var previous = orders.findByClientRequestId(idempotencyKey);
        if (previous.isPresent()) {
            OrderEntity saved = previous.get();
            if (!saved.sku.equals(sku) || saved.quantity != quantity) {
                throw new IllegalArgumentException("Idempotency-Key reused with different payload");
            }
            return saved;
        }
        OrderEntity order = orders.save(new OrderEntity(idempotencyKey, sku, quantity));
        var event = new OrderCreatedEvent(java.util.UUID.randomUUID(), order.id,
                order.sku, order.quantity, order.createdAt);
        try {
            // Both writes commit atomically in the same DB transaction.
            outbox.save(new OutboxEvent(event.eventId(), "orders.created", order.id.toString(),
                    mapper.writeValueAsString(event)));
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Cannot serialize order event", e);
        }
        return order;
    }
}
