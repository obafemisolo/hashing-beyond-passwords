package dev.solob.hashing.bucket;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequestMapping("/api/hashing/buckets")
public class BucketController {

    private final DeterministicBucketService bucketService;

    public BucketController(DeterministicBucketService bucketService) {
        this.bucketService = bucketService;
    }

    @GetMapping("/{subjectId}")
    public DeterministicBucketService.BucketResponse assign(
            @PathVariable String subjectId,
            @RequestParam(defaultValue = "4") @Min(2) @Max(100) int count
    ) {
        return bucketService.assign(subjectId, count);
    }
}
