package dev.solob.hashing.idempotency;

import dev.solob.hashing.common.CanonicalJson;
import dev.solob.hashing.common.Sha256Hasher;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Service
public class IdempotentOrderService {

    private final CanonicalJson canonicalJson;
    private final Sha256Hasher hasher;
    private final ConcurrentMap<String, StoredOrder> ordersByIdempotencyKey = new ConcurrentHashMap<>();

    public IdempotentOrderService(CanonicalJson canonicalJson, Sha256Hasher hasher) {
        this.canonicalJson = canonicalJson;
        this.hasher = hasher;
    }

    public OrderResponse create(String idempotencyKey, CreateOrderRequest request) {
        String payloadHash = hasher.hash(canonicalJson.write(request));
        StoredOrder candidate = new StoredOrder(UUID.randomUUID(), payloadHash, Instant.now());
        StoredOrder existing = ordersByIdempotencyKey.putIfAbsent(idempotencyKey, candidate);

        if (existing == null) {
            return toResponse(candidate, false);
        }

        if (!hasher.matchesHex(existing.payloadHash(), payloadHash)) {
            throw new IdempotencyConflictException(
                    "This idempotency key was already used with a different request payload."
            );
        }

        return toResponse(existing, true);
    }

    private OrderResponse toResponse(StoredOrder order, boolean replayed) {
        return new OrderResponse(
                order.orderId(),
                "CREATED",
                replayed,
                order.payloadHash(),
                order.createdAt()
        );
    }

    private record StoredOrder(UUID orderId, String payloadHash, Instant createdAt) {
    }

    public record OrderResponse(
            UUID orderId,
            String status,
            boolean replayed,
            String payloadHash,
            Instant createdAt
    ) {
    }
}
