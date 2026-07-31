package dev.solob.hashing.bucket;

import dev.solob.hashing.common.Sha256Hasher;
import org.springframework.stereotype.Service;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

@Service
public class DeterministicBucketService {

    private final Sha256Hasher hasher;

    public DeterministicBucketService(Sha256Hasher hasher) {
        this.hasher = hasher;
    }

    public BucketResponse assign(String subjectId, int bucketCount) {
        byte[] digest = hasher.digest(subjectId.getBytes(StandardCharsets.UTF_8));
        long positiveValue = ByteBuffer.wrap(digest, 0, Long.BYTES).getLong() & Long.MAX_VALUE;
        int bucket = (int) (positiveValue % bucketCount);

        return new BucketResponse(subjectId, bucketCount, bucket, hasher.hash(subjectId).substring(0, 16));
    }

    public record BucketResponse(String subjectId, int bucketCount, int bucket, String hashPrefix) {
    }
}
