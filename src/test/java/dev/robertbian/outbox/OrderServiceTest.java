package dev.robertbian.outbox;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Transactional
class OrderServiceTest {
    @Autowired OrderService service;
    @Autowired OrderRepository orders;
    @Autowired OutboxRepository outbox;

    @Test void duplicateRequestCreatesOneOrderAndOneOutboxEvent() {
        long before = outbox.count();
        var first = service.create("same-key", "BOOK-123", 2);
        var repeated = service.create("same-key", "BOOK-123", 2);
        assertThat(first.id).isEqualTo(repeated.id);
        assertThat(outbox.count() - before).isEqualTo(1);
    }

    @Test void rejectsIdempotencyKeyReusedForDifferentBody() {
        service.create("other-key", "BOOK-111", 2);
        assertThatThrownBy(() -> service.create("other-key", "BOOK-999", 3))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
