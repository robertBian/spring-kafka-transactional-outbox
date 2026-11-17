package dev.robertbian.outbox;

import java.util.concurrent.TimeUnit;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/** Single-instance polling demo. See README for horizontal scaling/claiming strategy. */
@Component
@ConditionalOnProperty(prefix="app.publisher", name="enabled", havingValue="true", matchIfMissing=true)
public class OutboxPublisher {
    private final OutboxRepository outbox;
    private final KafkaTemplate<String, String> kafka;

    public OutboxPublisher(OutboxRepository outbox, KafkaTemplate<String,String> kafka) {
        this.outbox = outbox;
        this.kafka = kafka;
    }

    @Transactional
    @Scheduled(fixedDelayString="${app.publisher.delay-ms:2000}")
    public void publish() throws Exception {
        for (OutboxEvent event : outbox.findTop25ByPublishedAtIsNullOrderByCreatedAtAsc()) {
            // Ack before DB update gives at-least-once, not exactly-once, delivery.
            kafka.send(event.topic, event.eventKey, event.payload).get(10, TimeUnit.SECONDS);
            event.markPublished();
        }
    }
}
