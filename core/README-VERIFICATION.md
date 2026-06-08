Verification code generation and storage
=====================================

What was added
--------------
- Maven dependencies in `core/pom.xml`: Jedis (Redis client) and Jakarta Mail.
- New classes under `com.irallyin.server.core.auth.verification`:
  - `VerificationCodeStore` (interface)
  - `InMemoryVerificationCodeStore` (single-node, reserves ~100MB, TTL cleanup)
  - `RedisVerificationCodeStore` (uses JedisPool, uses SETEX semantics)
  - `VerificationService` (generate 6-digit code, store, validate, send email)
  - `EmailSender` (reads `verification.properties` and sends via SMTP)
  - `ExampleRunner` (main method demo)

Key format
----------
- Registration scenario: key = ID + "register"
- Login scenario: key = ID + "login"

TTL
---
- Codes are stored with a 5 minute TTL by default (configurable in `VerificationService`).

Configuration
-------------
- `core/src/main/resources/irallyin-variables.properties` contains SMTP and Redis placeholders.
  - It currently includes the provided SMTP credentials (`irallyin@foxmail.com` with token `mypaeunchdwobceg`) — you can change these or secure them via environment-based configuration.
  - Example properties included: smtp.host, smtp.port, smtp.username, smtp.password, smtp.ssl, redis.host, redis.port

Usage examples
--------------
1) In-memory (single-node):

```java
InMemoryVerificationCodeStore store = new InMemoryVerificationCodeStore();
EmailSender sender = new EmailSender(); // optional
VerificationService svc = new VerificationService(store, sender);
String key = userId + "register";
String code = svc.generateAndStore(key); // returns 6-digit code
// optionally send:
svc.generateAndSendEmail(key, "recipient@example.com", null, "Your code: {code}");
```

2) Redis-backed (single Redis instance):

```java
// create JedisPool using redis.host / port / password from verification.properties
redis.clients.jedis.JedisPoolConfig cfg = new redis.clients.jedis.JedisPoolConfig();
redis.clients.jedis.JedisPool pool = new redis.clients.jedis.JedisPool(cfg, "127.0.0.1", 6379);
RedisVerificationCodeStore redisStore = new RedisVerificationCodeStore(pool);
VerificationService svc = new VerificationService(redisStore, sender);
```

Notes
-----
- The in-memory store reserves ~100MB as requested and performs background cleanup every 30 seconds.
- For production, do not store secrets in plain properties files; use environment variables or a secrets manager.
- The EmailSender uses Jakarta Mail directly to avoid relying on a Spring context; it reads `verification.properties` on the classpath.

