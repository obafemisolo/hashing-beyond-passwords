package dev.solob.hashing.etag;

import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Service
public class ProfileService {

    private final ConcurrentMap<UUID, Profile> profiles = new ConcurrentHashMap<>();

    public Profile get(UUID id) {
        return profiles.computeIfAbsent(id, key -> new Profile(key, "Solomon", "Backend Engineer", Instant.now()));
    }

    public Profile update(UUID id, UpdateProfileRequest request) {
        Profile updated = new Profile(id, request.name(), request.role(), Instant.now());
        profiles.put(id, updated);
        return updated;
    }

    public record Profile(UUID id, String name, String role, Instant updatedAt) {
    }

    public record UpdateProfileRequest(String name, String role) {
    }
}
