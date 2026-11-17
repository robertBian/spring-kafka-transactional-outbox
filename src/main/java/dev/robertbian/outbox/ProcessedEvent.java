package dev.robertbian.outbox;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name="processed_events")
public class ProcessedEvent {
    @Id public UUID id;
    @Column(nullable=false) public Instant processedAt;
    protected ProcessedEvent() {}
    public ProcessedEvent(UUID id) { this.id = id; processedAt = Instant.now(); }
}
