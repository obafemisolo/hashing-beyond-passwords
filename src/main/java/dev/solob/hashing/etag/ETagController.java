package dev.solob.hashing.etag;

import dev.solob.hashing.common.CanonicalJson;
import dev.solob.hashing.common.Sha256Hasher;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/hashing/etag/profiles")
public class ETagController {

    private final ProfileService profileService;
    private final CanonicalJson canonicalJson;
    private final Sha256Hasher hasher;

    public ETagController(ProfileService profileService, CanonicalJson canonicalJson, Sha256Hasher hasher) {
        this.profileService = profileService;
        this.canonicalJson = canonicalJson;
        this.hasher = hasher;
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> get(
            @PathVariable UUID id,
            @RequestHeader(value = HttpHeaders.IF_NONE_MATCH, required = false) String ifNoneMatch
    ) {
        ProfileService.Profile profile = profileService.get(id);
        String etag = quote(hasher.hash(canonicalJson.write(profile)));

        if (etag.equals(ifNoneMatch)) {
            return ResponseEntity.status(HttpStatus.NOT_MODIFIED).eTag(etag).build();
        }

        return ResponseEntity.ok().eTag(etag).body(profile);
    }

    @PutMapping("/{id}")
    public ProfileService.Profile update(
            @PathVariable UUID id,
            @RequestBody ProfileService.UpdateProfileRequest request
    ) {
        return profileService.update(id, request);
    }

    private String quote(String value) {
        return "\"" + value + "\"";
    }
}
