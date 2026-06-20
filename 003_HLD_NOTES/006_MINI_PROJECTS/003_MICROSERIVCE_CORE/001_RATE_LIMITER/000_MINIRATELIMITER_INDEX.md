# MiniRateLimiter — Complete Learning Roadmap

---

# MiniRateLimiter Journey

```text
MiniRateLimiter/
│
├── 001_Fixed_Window_Counter.md
│   Basic fixed time-window limiter.
│   Learn:
│   - HashMap counters
│   - Window calculation
│   - Basic request limiting
│
├── 002_Sliding_Window_Log.md
│   Accurate sliding window limiter.
│   Learn:
│   - Queue/Deque
│   - Timestamp cleanup
│   - Sliding window logic
│
├── 003_Sliding_Window_Counter.md
│   Optimized sliding window.
│   Learn:
│   - Previous/current window blending
│   - Weighted estimation
│   - Memory optimization
│
├── 004_Token_Bucket.md
│   Burst-friendly limiter.
│   Learn:
│   - Token refill
│   - Time-based state
│   - Burst traffic handling
│
├── 005_Leaky_Bucket.md
│   Traffic smoothing limiter.
│   Learn:
│   - FIFO queue
│   - Producer-consumer
│   - Fixed drain rate
│
├── 006_Thread_Safe_RateLimiter.md
│   Concurrent-safe limiter.
│   Learn:
│   - ReentrantLock
│   - ConcurrentHashMap
│   - Race condition prevention
│
├── 007_Redis_Distributed_RateLimiter.md
│   Multi-instance distributed limiter.
│   Learn:
│   - Redis shared state
│   - Distributed counters
│   - Global consistency
│
├── 008_Redis_Lua_Atomic_RateLimiter.md
│   Production-grade atomic Redis limiter.
│   Learn:
│   - Redis Lua scripts
│   - Atomic operations
│   - INCR + EXPIRE safety
│
├── 009_Rate_Limit_Policy_Model.md
│   Dynamic configurable policies.
│   Learn:
│   - Strategy pattern
│   - Policy abstraction
│   - Config-driven systems
│
├── 010_HTTP_Rate_Limit_Headers.md
│   Standard HTTP rate-limit headers.
│   Learn:
│   - Retry-After
│   - HTTP metadata
│   - API response headers
│
├── 011_Spring_Boot_Filter_Integration.md
│   Integrate limiter into Spring Boot.
│   Learn:
│   - Servlet filters
│   - Middleware design
│   - Request interception
│
├── 012_API_Gateway_Rate_Limiter.md
│   Gateway-level rate limiting.
│   Learn:
│   - API gateway flow
│   - Centralized limiting
│   - Distributed APIs
│
├── 013_Per_User_And_Per_IP_Limits.md
│   Multiple limiter scopes.
│   Learn:
│   - Composite keys
│   - Scope-based policies
│   - Identity modeling
│
├── 014_Redis_Sliding_Window.md
│   Sliding window using Redis.
│   Learn:
│   - Redis sorted sets
│   - Distributed sliding logs
│   - Time-window cleanup
│
├── 015_Redis_Token_Bucket.md
│   Distributed token bucket.
│   Learn:
│   - Distributed refill logic
│   - Shared token state
│   - Redis atomic updates
│
├── 016_Distributed_Locking_And_Consistency.md
│   Strong distributed consistency.
│   Learn:
│   - Distributed locks
│   - Consistency tradeoffs
│   - Multi-node coordination
│
├── 017_Metrics_And_Monitoring.md
│   Observability integration.
│   Learn:
│   - Metrics collection
│   - Prometheus basics
│   - Monitoring dashboards
│
├── 018_Rate_Limiter_Dashboard.md
│   Visual monitoring dashboard.
│   Learn:
│   - Grafana-style metrics
│   - Realtime dashboards
│   - System visibility
│
├── 019_Load_Testing_With_k6.md
│   Performance/load testing.
│   Learn:
│   - k6 load testing
│   - RPS analysis
│   - Bottleneck detection
│
└── 020_Production_Grade_Rate_Limiter.md
    Final production-ready system.
    Learn:
    - Scaling strategies
    - High availability
    - Production architecture
```

---

# Learning Evolution

```text
Basic Counter
    ->
Sliding Window
    ->
Burst Handling
    ->
Traffic Smoothing
    ->
Thread Safety
    ->
Distributed Redis
    ->
Atomic Operations
    ->
Policy Driven Design
    ->
HTTP Middleware
    ->
Gateway Integration
    ->
Monitoring
    ->
Production System
```

---

# Core Concepts Covered

```text
✔ HashMap
✔ Queue / Deque
✔ Sliding Window
✔ Token Bucket
✔ Leaky Bucket
✔ Concurrency
✔ Thread Safety
✔ Distributed Systems
✔ Redis
✔ Lua Scripting
✔ API Gateway
✔ Spring Boot Middleware
✔ Metrics
✔ Monitoring
✔ Load Testing
✔ Production Architecture
```

---

# Real Systems Using These Ideas

```text
✔ Nginx
✔ Kong
✔ Envoy
✔ AWS API Gateway
✔ Cloudflare
✔ Spring Cloud Gateway
✔ Redis-based API systems
✔ Uber-scale backend systems
```

---

# Final Outcome

After completing all 20 phases you can:

```text
✔ Build production-grade rate limiter
✔ Explain tradeoffs deeply
✔ Design distributed API systems
✔ Understand gateway internals
✔ Discuss Redis atomicity confidently
✔ Build startup-grade backend systems
✔ Handle FAANG system design discussions
```
