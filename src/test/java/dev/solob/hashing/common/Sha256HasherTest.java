package dev.solob.hashing.common;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class Sha256HasherTest {

    private final Sha256Hasher hasher = new Sha256Hasher();

    @Test
    void shouldHashUsingSha256() {
        assertThat(hasher.hash("abc"))
                .isEqualTo("ba7816bf8f01cfea414140de5dae2223b00361a396177a9cb410ff61f20015ad");
    }

    @Test
    void shouldCompareHashesSafely() {
        String hash = hasher.hash("same value");
        assertThat(hasher.matchesHex(hash, hash)).isTrue();
        assertThat(hasher.matchesHex(hash, hasher.hash("different value"))).isFalse();
    }
}
