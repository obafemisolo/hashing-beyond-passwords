package dev.solob.hashing.cache;

import dev.solob.hashing.common.Sha256Hasher;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Locale;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Service
public class HashedCacheKeyService {

    private final Sha256Hasher hasher;
    private final ConcurrentMap<String, List<String>> cache = new ConcurrentHashMap<>();

    public HashedCacheKeyService(Sha256Hasher hasher) {
        this.hasher = hasher;
    }

    public SearchResponse search(String query, int page, int size) {
        String canonicalInput = "products|q=" + query.trim().toLowerCase(Locale.ROOT)
                + "|page=" + page
                + "|size=" + size;
        String cacheKey = "product-search:" + hasher.hash(canonicalInput);

        boolean cacheHit = cache.containsKey(cacheKey);
        List<String> results = cache.computeIfAbsent(cacheKey, ignored -> expensiveSearch(query, page, size));

        return new SearchResponse(cacheKey, cacheHit, results);
    }

    private List<String> expensiveSearch(String query, int page, int size) {
        return List.of(
                "Result for '%s' — page %d".formatted(query, page),
                "The response is stored under a fixed-length SHA-256 cache key",
                "Requested page size: " + size
        );
    }

    public record SearchResponse(String cacheKey, boolean cacheHit, List<String> results) {
    }
}
