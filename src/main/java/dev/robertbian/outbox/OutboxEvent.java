package dev.robertbian.outbox;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name="outbox_events")
public class OutboxEvent {
    @Id public UUID id;
    @Column(nullable=false, length=80) public String topic;
    @Column(nullable=false, length=80) public String eventKey;
    @Column(nullable=false, columnDefinition="TEXT") public String payload;
    @Column(nullable=false) public Instant createdAt;
    public Instant publishedAt;

    protected OutboxEvent() {}
    public OutboxEvent(UUID id, String topic, String eventKey, String payload) {
        this.id = id;
        this.topic = topic;
        this.eventKey = eventKey;
        this.payload = payload;
        this.createdAt = Instant.now();
    }
    public void markPublished() { publishedAt = Instant.now(); }
}
