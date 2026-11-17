package dev.robertbian.outbox;

import java.util.*;
import org.springframework.data.jpa.repository.JpaRepository;

interface OrderRepository extends JpaRepository<OrderEntity, UUID> {
    Optional<OrderEntity> findByClientRequestId(String clientRequestId);
}

interface OutboxRepository extends JpaRepository<OutboxEvent, UUID> {
    List<OutboxEvent> findTop25ByPublishedAtIsNullOrderByCreatedAtAsc();
}

interface ProcessedEventRepository extends JpaRepository<ProcessedEvent, UUID> {}
interface OrderProjectionRepository extends JpaRepository<OrderProjection, UUID> {}
