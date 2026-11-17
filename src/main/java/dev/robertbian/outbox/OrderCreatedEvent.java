package dev.robertbian.outbox;

import java.time.Instant;
import java.util.UUID;

public record OrderCreatedEvent(UUID eventId, UUID orderId, String sku, int quantity, Instant occurredAt) {}
