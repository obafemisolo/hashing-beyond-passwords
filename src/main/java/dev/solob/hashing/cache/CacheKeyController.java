package dev.solob.hashing.cache;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequestMapping("/api/hashing/cache")
public class CacheKeyController {

    private final HashedCacheKeyService cacheKeyService;

    public CacheKeyController(HashedCacheKeyService cacheKeyService) {
        this.cacheKeyService = cacheKeyService;
    }

    @GetMapping("/products/search")
    public HashedCacheKeyService.SearchResponse search(
            @RequestParam @NotBlank String q,
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "10") @Min(1) @Max(100) int size
    ) {
        return cacheKeyService.search(q, page, size);
    }
}
