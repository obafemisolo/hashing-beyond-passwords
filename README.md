# Hashing Beyond Passwords — Spring Boot Examples

Most developers first meet hashing through password storage. That is important, but hashing also solves many everyday backend problems.

This project contains practical Spring Boot examples for:

1. **File integrity** — create a SHA-256 fingerprint and verify that a file has not changed.
2. **Idempotency and duplicate-request detection** — fingerprint a canonical request body and prevent one idempotency key from being reused with different data.
3. **Cache keys** — turn long or awkward request parameters into fixed-length, deterministic keys.
4. **ETags and HTTP cache validation** — hash a response representation so a client can avoid downloading unchanged data.
5. **Deterministic bucketing** — consistently assign a user or resource to a shard, worker, rollout group, or experiment bucket.
6. **Webhook verification with HMAC-SHA256** — confirm both the integrity and authenticity of a raw request body.

## Important distinction

Do **not** use plain SHA-256 for password storage. Passwords need slow, adaptive password-hashing algorithms such as BCrypt, Argon2, scrypt, or PBKDF2.

Use SHA-256 when you need a deterministic content fingerprint. Use HMAC-SHA256 when the sender and receiver share a secret and you need to verify authenticity as well as integrity.

Also, hashing an email address or phone number does not automatically make it anonymous. Low-entropy values can often be guessed. Use encryption when the original value must be recovered, or a properly managed keyed HMAC when you need a stable lookup token without storing the plaintext lookup value.

## Stack

- Java 21
- Spring Boot 3.5.16
- Maven

## Run

```bash
mvn spring-boot:run
```

The API runs on `http://localhost:8080`.

## 1. File integrity

Create a fingerprint:

```bash
curl -X POST http://localhost:8080/api/hashing/files/fingerprint \
  -F "file=@README.md"
```

Verify a file against a known hash:

```bash
curl -X POST "http://localhost:8080/api/hashing/files/verify?expectedHash=PASTE_HASH_HERE" \
  -F "file=@README.md"
```

A one-byte change produces a different SHA-256 value.

## 2. Idempotency and request fingerprinting

First request:

```bash
curl -X POST http://localhost:8080/api/hashing/idempotency/orders \
  -H "Content-Type: application/json" \
  -H "Idempotency-Key: order-request-1001" \
  -d '{
    "customerId": "customer-42",
    "productId": "proxy-plan-20gb",
    "quantity": 1,
    "amount": 1500.00
  }'
```

Send the exact request again with the same key. The same order is returned with `replayed: true`.

Change the payload while keeping the same key. The server returns `409 Conflict` because the payload hash no longer matches the one originally attached to that key.

> Production note: store idempotency records in a durable database or Redis, add an expiry policy, scope keys to the authenticated client, and save the complete original response.

## 3. Hashed cache keys

```bash
curl "http://localhost:8080/api/hashing/cache/products/search?q=Static%20UK%20Proxy&page=0&size=10"
```

Call it twice. The second response returns `cacheHit: true` and uses the same fixed-length SHA-256 cache key.

## 4. ETag and conditional GET

Use any UUID:

```bash
PROFILE_ID=550e8400-e29b-41d4-a716-446655440000
curl -i "http://localhost:8080/api/hashing/etag/profiles/$PROFILE_ID"
```

Copy the returned `ETag` header, including its quotes, and send it back:

```bash
curl -i "http://localhost:8080/api/hashing/etag/profiles/$PROFILE_ID" \
  -H 'If-None-Match: "PASTE_ETAG_HERE"'
```

When the representation has not changed, the server returns `304 Not Modified` without sending the body again.

Update the profile and request it again:

```bash
curl -X PUT "http://localhost:8080/api/hashing/etag/profiles/$PROFILE_ID" \
  -H "Content-Type: application/json" \
  -d '{"name":"Solomon Obafemi","role":"Full-Stack Engineer"}'
```

The updated representation gets a new ETag.

## 5. Deterministic bucketing

```bash
curl "http://localhost:8080/api/hashing/buckets/user-123?count=4"
```

The same subject always enters the same bucket while the bucket count stays unchanged. This is useful for stable rollouts, worker assignment, basic sharding, or A/B-test grouping.

> Production note: changing the number of buckets can remap many subjects. For systems that need minimal movement when nodes are added or removed, use a proper consistent-hashing or rendezvous-hashing implementation.

## 6. HMAC webhook verification

Generate a signature locally through the demo-only helper:

```bash
curl -X POST http://localhost:8080/api/hashing/webhooks/sign-for-demo \
  -H "Content-Type: application/json" \
  -d '{"event":"payment.completed","amount":5000}'
```

Use the returned signature against the exact same raw body:

```bash
curl -X POST http://localhost:8080/api/hashing/webhooks/verify \
  -H "Content-Type: application/json" \
  -H "X-Webhook-Signature: sha256=PASTE_SIGNATURE_HERE" \
  -d '{"event":"payment.completed","amount":5000}'
```

Even whitespace changes alter the raw bytes and invalidate the signature. Real webhook providers normally give you the signing secret and define the exact signature-header format.

## Why `MessageDigest.isEqual`?

The examples use constant-time comparison for security-sensitive digest checks. A normal string comparison may return as soon as it finds the first different character, which can leak small timing differences.

## Project structure

```text
src/main/java/dev/solob/hashing
├── bucket          # deterministic assignment
├── cache           # fixed-length cache keys
├── common          # SHA-256, HMAC-SHA256, canonical JSON
├── etag            # conditional HTTP responses
├── file            # checksums and integrity verification
├── idempotency     # duplicate-request protection
└── webhook         # request authenticity verification
```

## Suggested GitHub repository name

`spring-boot-hashing-beyond-passwords`

## License

MIT
