package dev.robertbian.outbox;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.Map;
import java.util.UUID;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class OrderController {
    private final OrderService service;
    private final OrderProjectionRepository projections;

    public OrderController(OrderService service, OrderProjectionRepository projections) {
        this.service = service;
        this.projections = projections;
    }

    @PostMapping("/orders")
    public ResponseEntity<Map<String, Object>> create(
            @RequestHeader("Idempotency-Key") @NotBlank String key,
            @Valid @RequestBody CreateOrder body) {
        if (key.length() > 100) throw new IllegalArgumentException("Idempotency-Key too long");
        OrderEntity order = service.create(key, body.sku(), body.quantity());
        return ResponseEntity.accepted().body(Map.of("orderId", order.id, "status", "ACCEPTED"));
    }

    @GetMapping("/projections/{orderId}")
    public ResponseEntity<OrderProjection> projection(@PathVariable UUID orderId) {
        return ResponseEntity.of(projections.findById(orderId));
    }

    public record CreateOrder(@NotBlank @Size(max=64) String sku, @Min(1) int quantity) {}

    @RestControllerAdvice
    static class Errors {
        @ExceptionHandler({IllegalArgumentException.class, DataIntegrityViolationException.class})
        @ResponseStatus(HttpStatus.CONFLICT)
        Map<String,String> conflict(Exception ex) {
            return Map.of("error", "Conflict or invalid idempotency request");
        }
    }
}
