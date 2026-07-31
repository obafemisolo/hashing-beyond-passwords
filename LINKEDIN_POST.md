# LinkedIn post draft

When people hear **hashing**, their mind usually goes straight to passwords.

That is fair. Most of us first meet hashing when learning how passwords should be stored.

But hashing is doing much more work in the backend than we normally talk about.

A hash gives data a fixed-length fingerprint. Give it the same input and you get the same result. Change the input, even slightly, and the fingerprint changes.

Here are a few useful places hashing shows up beyond passwords:

**1. File integrity**

Before storing or transferring a file, you can calculate its hash. When the file arrives, calculate it again. If the values are different, something changed along the way.

**2. Preventing duplicate requests**

Imagine a payment or order request is sent twice because the user clicked again or the network retried it. A hash of the request payload can help you recognise that you have already processed the same data.

For a proper idempotency flow, I would still use an idempotency key, then attach the payload hash to it. If the same key comes back with different data, reject it.

**3. Creating cache keys**

Some request URLs and query parameters can become too long or messy to use directly as cache keys. Hash the important parts and you get a short, fixed-length key that will remain the same for the same request.

**4. ETags and change detection**

An API can hash a response and send the value as an ETag. The client returns that ETag on the next request. If the data has not changed, the server can respond with `304 Not Modified` instead of sending the full response again.

**5. Stable routing and bucketing**

Hashing can consistently assign a user to a server, shard, worker, feature rollout, or A/B test group. The same user ID keeps producing the same assignment.

**6. Verifying webhooks**

With HMAC-SHA256, a provider can sign the raw webhook body using a shared secret. Your backend calculates the signature again. If both signatures match, you know the payload was not changed and it came from someone who has the secret.

One important warning: this does not mean plain SHA-256 should be used for passwords. Passwords need slow, adaptive algorithms such as BCrypt or Argon2.

The point is that hashing is much bigger than password storage.

I built a small Spring Boot project with a working example for each use case—not theory-only snippets.

GitHub: https://github.com/obafemisolo/spring-boot-hashing-beyond-passwords

What other practical use of hashing have you worked with?
