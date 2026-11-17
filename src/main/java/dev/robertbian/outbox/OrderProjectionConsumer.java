package dev.robertbian.outbox;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class OrderProjectionConsumer {
    private final ProcessedEventRepository processed;
    private final OrderProjectionRepository projections;
    private final ObjectMapper mapper;

    public OrderProjectionConsumer(ProcessedEventRepository processed,
                                   OrderProjectionRepository projections, ObjectMapper mapper) {
        this.processed = processed;
        this.projections = projections;
        this.mapper = mapper;
    }

    @KafkaListener(topics="orders.created", groupId="order-projection")
    @Transactional
    public void apply(String payload) throws JsonProcessingException {
        var event = mapper.readValue(payload, OrderCreatedEvent.class);
        if (processed.existsById(event.eventId())) return;
        // The DB transaction covers the inbox marker and projection update.
        // A DB uniqueness constraint on the marker protects against competing consumers.
        projections.save(new OrderProjection(event));
        processed.save(new ProcessedEvent(event.eventId()));
    }
}
