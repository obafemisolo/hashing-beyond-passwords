package dev.solob.hashing.idempotency;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.validation.annotation.Validated;

@Validated
@RestController
@RequestMapping("/api/hashing/idempotency")
public class IdempotencyController {

    private final IdempotentOrderService orderService;

    public IdempotencyController(IdempotentOrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping("/orders")
    public IdempotentOrderService.OrderResponse createOrder(
            @RequestHeader("Idempotency-Key") @NotBlank String idempotencyKey,
            @Valid @RequestBody CreateOrderRequest request
    ) {
        return orderService.create(idempotencyKey.trim(), request);
    }
}
