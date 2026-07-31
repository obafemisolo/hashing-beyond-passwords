package dev.solob.hashing.common;

import org.springframework.stereotype.Component;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;

@Component
public class HmacSha256Signer {

    private static final String ALGORITHM = "HmacSHA256";

    public String sign(byte[] payload, String secret) {
        try {
            Mac mac = Mac.getInstance(ALGORITHM);
            mac.init(new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), ALGORITHM));
            return HexFormat.of().formatHex(mac.doFinal(payload));
        } catch (NoSuchAlgorithmException | InvalidKeyException exception) {
            throw new IllegalStateException("Unable to calculate HMAC-SHA256", exception);
        }
    }

    public boolean verify(byte[] payload, String secret, String suppliedSignature) {
        String expected = sign(payload, secret);
        String supplied = normalize(suppliedSignature);
        return MessageDigest.isEqual(
                expected.getBytes(StandardCharsets.US_ASCII),
                supplied.getBytes(StandardCharsets.US_ASCII)
        );
    }

    private String normalize(String signature) {
        if (signature == null) {
            return "";
        }
        return signature.trim().replaceFirst("^sha256=", "").toLowerCase();
    }
}
