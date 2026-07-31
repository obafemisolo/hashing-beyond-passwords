package dev.solob.hashing.common;

import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;

class HmacSha256SignerTest {

    private final HmacSha256Signer signer = new HmacSha256Signer();

    @Test
    void shouldVerifyAnUntamperedPayload() {
        byte[] payload = "{\"event\":\"payment.completed\"}".getBytes(StandardCharsets.UTF_8);
        String signature = signer.sign(payload, "secret");

        assertThat(signer.verify(payload, "secret", signature)).isTrue();
        assertThat(signer.verify("changed".getBytes(StandardCharsets.UTF_8), "secret", signature)).isFalse();
    }
}
