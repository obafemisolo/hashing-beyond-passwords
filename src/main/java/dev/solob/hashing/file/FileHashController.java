package dev.solob.hashing.file;

import dev.solob.hashing.common.Sha256Hasher;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/hashing/files")
public class FileHashController {

    private final Sha256Hasher hasher;

    public FileHashController(Sha256Hasher hasher) {
        this.hasher = hasher;
    }

    @PostMapping(value = "/fingerprint", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public FileFingerprintResponse fingerprint(@RequestParam("file") MultipartFile file) throws IOException {
        return new FileFingerprintResponse(
                file.getOriginalFilename(),
                file.getSize(),
                "SHA-256",
                hasher.hash(file.getBytes())
        );
    }

    @PostMapping(value = "/verify", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public FileVerificationResponse verify(
            @RequestParam("file") MultipartFile file,
            @RequestParam String expectedHash
    ) throws IOException {
        String actualHash = hasher.hash(file.getBytes());
        return new FileVerificationResponse(
                file.getOriginalFilename(),
                expectedHash,
                actualHash,
                hasher.matchesHex(expectedHash, actualHash)
        );
    }

    public record FileFingerprintResponse(
            String filename,
            long size,
            String algorithm,
            String hash
    ) {
    }

    public record FileVerificationResponse(
            String filename,
            String expectedHash,
            String actualHash,
            boolean matches
    ) {
    }
}
