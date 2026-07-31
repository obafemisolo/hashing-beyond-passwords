package dev.solob.hashing.common;

import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;

@Component
public class Sha256Hasher {

    private static final String ALGORITHM = "SHA-256";

    public String hash(String value) {
        return hash(value.getBytes(StandardCharsets.UTF_8));
    }

    public String hash(byte[] value) {
        return HexFormat.of().formatHex(digest(value));
    }

    public byte[] digest(byte[] value) {
        try {
            return MessageDigest.getInstance(ALGORITHM).digest(value);
        } catch (NoSuchAlgorithmException exception) {
            throw new IllegalStateException(ALGORITHM + " is not available", exception);
        }
    }

    public boolean matchesHex(String expected, String actual) {
        try {
            byte[] expectedBytes = HexFormat.of().parseHex(normalize(expected));
            byte[] actualBytes = HexFormat.of().parseHex(normalize(actual));
            return MessageDigest.isEqual(expectedBytes, actualBytes);
        } catch (IllegalArgumentException exception) {
            return false;
        }
    }

    private String normalize(String hash) {
        return hash == null ? "" : hash.trim().toLowerCase();
    }
}
