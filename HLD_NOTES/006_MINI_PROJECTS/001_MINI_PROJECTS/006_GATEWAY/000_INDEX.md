# MiniGateway — 000_INDEX.md

## Clickable Tree Index

```text
MiniGateway/
├── 000_INDEX.md
├── 002_Request_Routing.md
├── 003_Path_Based_Routing.md
├── 004_Load_Balancer_Round_Robin.md
├── 005_Least_Connections_LoadBalancer.md
├── 006_Health_Checks.md
├── 007_Service_Registry_Integration.md
├── 008_Dynamic_Route_Refresh.md
├── 009_JWT_Authentication_Filter.md
├── 010_API_Key_Authentication.md
├── 011_RBAC_Authorization.md
├── 012_Rate_Limiter_Filter.md
├── 013_Redis_Distributed_RateLimiter.md
├── 014_Request_Logging.md
├── 015_Request_Tracing.md
├── 016_Correlation_ID.md
├── 017_Request_Response_Modification.md
├── 018_CORS_Filter.md
├── 019_Request_Validation.md
├── 020_Idempotency_Key_Filter.md
├── 021_Circuit_Breaker.md
├── 022_Retry_Filter.md
├── 023_Timeout_Handling.md
├── 024_Bulkhead_Isolation.md
├── 025_Backpressure_Handling.md
├── 026_Response_Caching.md
├── 027_GZip_Compression.md
├── 028_WebSocket_Proxy.md
├── 029_HTTP2_Basics.md
├── 030_TLS_HTTPS_Termination.md
├── 031_Gateway_Metrics.md
├── 032_Distributed_Tracing.md
├── 033_Kafka_Audit_Events.md
├── 034_Config_Server_Integration.md
├── 035_Blue_Green_Routing.md
├── 036_Canary_Deployment_Routing.md
├── 037_Multi_Region_Gateway.md
├── 038_Load_Testing_With_k6.md
├── 039_Kubernetes_Ingress_Mapping.md
└── 040_Production_MiniGateway.md
```

## Phase Links

- [000_INDEX.md](./000_INDEX.md) — MiniGateway Master Index
- [002_Request_Routing.md](./002_Request_Routing.md) — Request Routing
- [003_Path_Based_Routing.md](./003_Path_Based_Routing.md) — Path Based Routing
- [004_Load_Balancer_Round_Robin.md](./004_Load_Balancer_Round_Robin.md) — Load Balancer Round Robin
- [005_Least_Connections_LoadBalancer.md](./005_Least_Connections_LoadBalancer.md) — Least Connections Load Balancer
- [006_Health_Checks.md](./006_Health_Checks.md) — Health Checks
- [007_Service_Registry_Integration.md](./007_Service_Registry_Integration.md) — Service Registry Integration
- [008_Dynamic_Route_Refresh.md](./008_Dynamic_Route_Refresh.md) — Dynamic Route Refresh
- [009_JWT_Authentication_Filter.md](./009_JWT_Authentication_Filter.md) — JWT Authentication Filter
- [010_API_Key_Authentication.md](./010_API_Key_Authentication.md) — API Key Authentication
- [011_RBAC_Authorization.md](./011_RBAC_Authorization.md) — RBAC Authorization
- [012_Rate_Limiter_Filter.md](./012_Rate_Limiter_Filter.md) — Rate Limiter Filter
- [013_Redis_Distributed_RateLimiter.md](./013_Redis_Distributed_RateLimiter.md) — Redis Distributed RateLimiter
- [014_Request_Logging.md](./014_Request_Logging.md) — Request Logging
- [015_Request_Tracing.md](./015_Request_Tracing.md) — Request Tracing
- [016_Correlation_ID.md](./016_Correlation_ID.md) — Correlation ID
- [017_Request_Response_Modification.md](./017_Request_Response_Modification.md) — Request Response Modification
- [018_CORS_Filter.md](./018_CORS_Filter.md) — CORS Filter
- [019_Request_Validation.md](./019_Request_Validation.md) — Request Validation
- [020_Idempotency_Key_Filter.md](./020_Idempotency_Key_Filter.md) — Idempotency Key Filter
- [021_Circuit_Breaker.md](./021_Circuit_Breaker.md) — Circuit Breaker
- [022_Retry_Filter.md](./022_Retry_Filter.md) — Retry Filter
- [023_Timeout_Handling.md](./023_Timeout_Handling.md) — Timeout Handling
- [024_Bulkhead_Isolation.md](./024_Bulkhead_Isolation.md) — Bulkhead Isolation
- [025_Backpressure_Handling.md](./025_Backpressure_Handling.md) — Backpressure Handling
- [026_Response_Caching.md](./026_Response_Caching.md) — Response Caching
- [027_GZip_Compression.md](./027_GZip_Compression.md) — GZip Compression
- [028_WebSocket_Proxy.md](./028_WebSocket_Proxy.md) — WebSocket Proxy
- [029_HTTP2_Basics.md](./029_HTTP2_Basics.md) — HTTP2 Basics
- [030_TLS_HTTPS_Termination.md](./030_TLS_HTTPS_Termination.md) — TLS HTTPS Termination
- [031_Gateway_Metrics.md](./031_Gateway_Metrics.md) — Gateway Metrics
- [032_Distributed_Tracing.md](./032_Distributed_Tracing.md) — Distributed Tracing
- [033_Kafka_Audit_Events.md](./033_Kafka_Audit_Events.md) — Kafka Audit Events
- [034_Config_Server_Integration.md](./034_Config_Server_Integration.md) — Config Server Integration
- [035_Blue_Green_Routing.md](./035_Blue_Green_Routing.md) — Blue Green Routing
- [036_Canary_Deployment_Routing.md](./036_Canary_Deployment_Routing.md) — Canary Deployment Routing
- [037_Multi_Region_Gateway.md](./037_Multi_Region_Gateway.md) — Multi Region Gateway
- [038_Load_Testing_With_k6.md](./038_Load_Testing_With_k6.md) — Load Testing With k6
- [039_Kubernetes_Ingress_Mapping.md](./039_Kubernetes_Ingress_Mapping.md) — Kubernetes Ingress Mapping
- [040_Production_MiniGateway.md](./040_Production_MiniGateway.md) — Production MiniGateway

---

## Phase Grouping

```text
Phase 1: Reverse Proxy Foundation
├── 001_Basic_Reverse_Proxy.md
├── 002_Request_Routing.md
└── 003_Path_Based_Routing.md

Phase 2: Load Balancing And Discovery
├── 004_Load_Balancer_Round_Robin.md
├── 005_Least_Connections_LoadBalancer.md
├── 006_Health_Checks.md
├── 007_Service_Registry_Integration.md
└── 008_Dynamic_Route_Refresh.md

Phase 3: Security Filters
├── 009_JWT_Authentication_Filter.md
├── 010_API_Key_Authentication.md
└── 011_RBAC_Authorization.md

Phase 4: Traffic Protection
├── 012_Rate_Limiter_Filter.md
├── 013_Redis_Distributed_RateLimiter.md
├── 020_Idempotency_Key_Filter.md
├── 021_Circuit_Breaker.md
├── 022_Retry_Filter.md
├── 023_Timeout_Handling.md
├── 024_Bulkhead_Isolation.md
└── 025_Backpressure_Handling.md

Phase 5: Observability And Request Handling
├── 014_Request_Logging.md
├── 015_Request_Tracing.md
├── 016_Correlation_ID.md
├── 017_Request_Response_Modification.md
├── 018_CORS_Filter.md
├── 019_Request_Validation.md
├── 031_Gateway_Metrics.md
├── 032_Distributed_Tracing.md
└── 033_Kafka_Audit_Events.md

Phase 6: Protocol And Edge Features
├── 026_Response_Caching.md
├── 027_GZip_Compression.md
├── 028_WebSocket_Proxy.md
├── 029_HTTP2_Basics.md
└── 030_TLS_HTTPS_Termination.md

Phase 7: Deployment And Production Routing
├── 034_Config_Server_Integration.md
├── 035_Blue_Green_Routing.md
├── 036_Canary_Deployment_Routing.md
├── 037_Multi_Region_Gateway.md
├── 038_Load_Testing_With_k6.md
├── 039_Kubernetes_Ingress_Mapping.md
└── 040_Production_MiniGateway.md
```

---

## What MiniGateway Teaches

```text
reverse proxy
routing
load balancing
health checks
service discovery
JWT authentication
API keys
RBAC
rate limiting
circuit breaker
retry
timeout
bulkhead
backpressure
caching
compression
TLS
WebSockets
metrics
tracing
audit events
blue-green routing
canary routing
multi-region failover
Kubernetes ingress
```

---

## Real-World Mapping

```text
NGINX
Kong
Envoy
Zuul
Spring Cloud Gateway
AWS API Gateway
Kubernetes Ingress
Cloudflare edge proxy
```
