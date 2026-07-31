package dev.solob.hashing.webhook;

import dev.solob.hashing.common.HmacSha256Signer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/hashing/webhooks")
public class WebhookController {

    private final HmacSha256Signer signer;
    private final String webhookSecret;

    public WebhookController(
            HmacSha256Signer signer,
            @Value("${demo.webhook-secret}") String webhookSecret
    ) {
        this.signer = signer;
        this.webhookSecret = webhookSecret;
    }

    @PostMapping(value = "/verify", consumes = MediaType.ALL_VALUE)
    public VerificationResponse verify(
            @RequestHeader("X-Webhook-Signature") String signature,
            @RequestBody byte[] rawBody
    ) {
        return new VerificationResponse(signer.verify(rawBody, webhookSecret, signature));
    }

    @PostMapping(value = "/sign-for-demo", consumes = MediaType.ALL_VALUE)
    public SignatureResponse signForDemo(@RequestBody byte[] rawBody) {
        return new SignatureResponse("sha256=" + signer.sign(rawBody, webhookSecret));
    }

    public record VerificationResponse(boolean verified) {
    }

    public record SignatureResponse(String signature) {
    }
}
