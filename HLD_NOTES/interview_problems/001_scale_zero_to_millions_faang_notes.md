# Scale From Zero To Millions Of Users

## FAANG System Design + Implementation Notes

These notes summarize how to evolve a system from a single server to a highly scalable architecture that can support millions of users. They are optimized for system design interviews and implementation planning.

---

## 1. Single Server Setup

At the beginning, everything runs on one server:

- Web application
- API server
- Database
- Cache
- Static files
- Background jobs

### Simple Diagram

```text
User
 ├── Web Browser: www.mysite.com
 └── Mobile App : api.mysite.com
          |
          v
        DNS
          |
          v
   Single Web Server
   ├── Web App
   ├── API
   ├── Database
   ├── Cache
   └── Static Files
```

### Request Flow

```text
1. User enters api.mysite.com
2. DNS returns IP address
3. Client sends HTTP request to server
4. Server returns HTML or JSON response
```

### Interview Notes

- DNS is usually managed by a third-party provider.
- Web clients receive HTML, CSS, JavaScript, and assets.
- Mobile clients usually communicate through HTTP APIs.
- JSON is commonly used for API responses.
- Single server is simple but has no scalability or availability guarantees.

### Implementation Notes

- Start with a monolith if the product is early-stage.
- Keep code modular so it can later be split into services.
- Use environment variables for configuration.
- Add basic logging from day one.

---

## 2. Separate Web Tier and Database Tier

As traffic grows, move the database to a separate server.

### Simple Diagram

```text
User
  |
 DNS
  |
  v
Web/API Server
  |
  | read/write/update
  v
Database Server
```

### Why Separate Them?

- Web tier and data tier can scale independently.
- Database resources are isolated from application traffic.
- Better security and operational control.

---

## 3. Choosing the Database

### Relational Database / SQL

Examples:

- MySQL
- PostgreSQL
- Oracle

Best for:

- Structured data
- Strong consistency
- Transactions
- Joins
- Financial or relational data models

### NoSQL Database

Examples:

- DynamoDB
- Cassandra
- MongoDB
- CouchDB
- HBase
- Neo4j

Best for:

- Very large scale
- Low latency access
- Semi-structured or unstructured data
- Key-value access patterns
- Flexible schema

### FAANG Interview Rule of Thumb

Use SQL when:

- Data is relational.
- You need transactions.
- Query patterns are complex.

Use NoSQL when:

- Data volume is massive.
- Access pattern is simple and predictable.
- You need horizontal scalability.
- Low latency is more important than complex joins.

---

## 4. Vertical Scaling vs Horizontal Scaling

### Vertical Scaling

Scale up by adding more resources to one server.

```text
Single Server
   |
   v
Bigger Server
More CPU / RAM / Disk
```

Pros:

- Simple
- Easy to manage initially
- No distributed system complexity

Cons:

- Hardware limit
- Expensive
- Single point of failure
- No true redundancy

### Horizontal Scaling

Scale out by adding more servers.

```text
        Load Balancer
        /     |     \
       v      v      v
   Server1 Server2 Server3
```

Pros:

- Better availability
- Easier to scale traffic
- Failure isolation
- Common approach for large systems

Cons:

- More operational complexity
- Requires load balancing
- Requires stateless services or shared state

### Interview Answer

For early systems, vertical scaling is acceptable. For large-scale systems, prefer horizontal scaling because it provides better availability, redundancy, and elasticity.

---

## 5. Load Balancer

A load balancer distributes incoming traffic across multiple servers.

### Simple Diagram

```text
Users
  |
  v
DNS -> Load Balancer: public IP
          |
          | private network
          v
   -----------------
   |       |       |
   v       v       v
Server1 Server2 Server3
```

### Benefits

- Prevents one server from being overloaded.
- Improves availability.
- Enables horizontal scaling.
- Removes direct public access to web servers.
- Routes traffic only to healthy servers.

### Failure Handling

```text
If Server1 fails:
Load Balancer routes all traffic to Server2 and Server3.
```

### Implementation Notes

- Use health checks.
- Keep web servers stateless.
- Use private IPs between load balancer and web servers.
- Use autoscaling groups when deployed on cloud platforms.

### FAANG Talking Points

Mention:

- Layer 4 vs Layer 7 load balancing
- Health checks
- TLS termination
- Round-robin, least connections, weighted routing
- Sticky sessions only when unavoidable

---

## 6. Database Replication

Replication copies data from one database server to another.

Common pattern:

- Master/Primary handles writes.
- Slaves/Replicas handle reads.

### Simple Diagram

```text
             Web Servers
             /        \
            /          \
       writes          reads
          |              |
          v              v
      Master DB ---> Replica DB1
          |
          +-------> Replica DB2
          |
          +-------> Replica DB3
```

### Benefits

- Better read performance
- High availability
- Data redundancy
- Disaster recovery support

### Failure Scenarios

#### Replica Failure

```text
Replica fails
  -> Route reads to other replicas
  -> Replace failed replica
  -> Re-sync data
```

#### Master Failure

```text
Master fails
  -> Promote replica to new master
  -> Redirect writes
  -> Create new replica
  -> Recover missing data if replication lag exists
```

### Interview Notes

- Most applications have more reads than writes.
- Replication lag can cause stale reads.
- Promotion of a new master is not always trivial.
- Multi-master replication is more complex and can introduce conflicts.

---

## 7. Load Balancer + Database Replication Architecture

### Simple Diagram

```text
User
 |
DNS
 |
v
Load Balancer
 |
 +-------------------+
 |                   |
 v                   v
Web Server 1     Web Server 2
 |                   |
 | writes            | writes
 v                   v
Master Database <----+
 |
 | replication
 v
Replica Database
 ^
 |
 reads from web servers
```

### Request Flow

```text
1. DNS returns load balancer IP.
2. Client connects to load balancer.
3. Load balancer routes request to a web server.
4. Web server reads from replica database.
5. Web server writes, updates, and deletes through master database.
```

---

## 8. Cache Layer

Cache stores frequently accessed or expensive-to-compute data in memory.

Examples:

- Redis
- Memcached

### Simple Diagram

```text
Web Server
   |
   | 1. Check cache
   v
 Cache
   |
   | cache miss
   v
Database
   |
   | store result in cache
   v
 Cache -> Web Server -> User
```

### Read-Through Cache Flow

```text
Request arrives
  -> Check cache
      -> Cache hit: return data
      -> Cache miss: query DB
             -> Store result in cache
             -> Return data
```

### Cache Example

```python
SECONDS = 1
cache.set("myKey", "hi there", 3600 * SECONDS)
cache.get("myKey")
```

### When to Use Cache

Use cache when:

- Data is read frequently.
- Data is modified infrequently.
- Query computation is expensive.
- Low latency is required.

Avoid cache when:

- Data changes constantly.
- Strong consistency is required.
- Data must be durable.

### Cache Considerations

#### Expiration Policy

- Use TTL to remove stale data.
- Too short TTL causes frequent DB hits.
- Too long TTL may return stale data.

#### Consistency

- Cache and database can become inconsistent.
- Cache invalidation is difficult.
- Common techniques: write-through, write-around, write-back, cache-aside.

#### Failure Handling

- Avoid a single cache server as a single point of failure.
- Use multiple cache nodes.
- Overprovision memory.
- Handle cache misses gracefully.

#### Eviction Policy

Common policies:

- LRU: least recently used
- LFU: least frequently used
- FIFO: first in first out

### FAANG Talking Points

Mention:

- Cache hit ratio
- Cache invalidation
- TTL
- Hot keys
- Cache stampede
- Distributed cache
- Write-through vs cache-aside

---

## 9. Content Delivery Network - CDN

A CDN serves static content from geographically distributed edge servers.

Static content includes:

- Images
- Videos
- CSS
- JavaScript
- Fonts
- Static downloads

### Simple Diagram

```text
User
 |
 | request static asset
 v
Nearest CDN Edge
 |
 | cache miss
 v
Origin Server / Object Storage
```

### CDN Workflow

```text
1. User requests image.png from CDN URL.
2. CDN checks if image exists in edge cache.
3. If not found, CDN fetches image from origin.
4. CDN stores image with TTL.
5. CDN returns image to user.
6. Future users get image directly from CDN.
```

### Benefits

- Lower latency
- Reduced origin load
- Better global performance
- Improved static asset delivery

### CDN Considerations

- Cost for data transfer
- Cache expiry and TTL
- CDN fallback strategy
- File invalidation
- Asset versioning

### Asset Versioning Example

```text
image.png?v=2
app.bundle.js?v=2026-04-26
```

### FAANG Talking Points

Mention:

- Edge locations
- Origin server
- Cache invalidation
- TTL
- Signed URLs for private content
- CDN fallback
- Static vs dynamic content caching

---

## 10. Architecture With CDN and Cache

### Simple Diagram

```text
              +------ CDN ------+
              |  static assets  |
              +-----------------+
                      |
User -> DNS -> Load Balancer
              /              \
             v                v
       Web Server 1      Web Server 2
             |                |
             +------ Cache ---+
             |                |
             +------ DB ------+
                    |
              Master -> Replica
```

### Improvements

- Static assets are served from CDN.
- Dynamic data is accelerated using cache.
- Database load is reduced.
- Web tier is horizontally scalable.

---

## 11. Stateful vs Stateless Web Tier

### Stateful Architecture

A stateful server stores client-specific data locally.

```text
User A -> Server 1 contains User A session
User B -> Server 2 contains User B session
User C -> Server 3 contains User C session
```

Problem:

```text
If User A request goes to Server 2, authentication may fail.
```

### Issues With Stateful Servers

- Requires sticky sessions.
- Harder to add or remove servers.
- Harder to recover from server failure.
- Limits horizontal scaling.

---

### Stateless Architecture

Web servers do not store client state locally.

```text
User A   User B   User C
  |        |        |
  v        v        v
Load Balancer
  |
  v
Any Web Server
  |
  v
Shared Session Store / DB / Redis / NoSQL
```

### Benefits

- Any request can go to any server.
- Easier autoscaling.
- Better fault tolerance.
- Simpler load balancing.

### Implementation Notes

Store session/state in:

- Redis
- Database
- NoSQL store
- Distributed session store
- JWT, when appropriate

### FAANG Talking Points

Mention:

- Stateless APIs
- Externalized sessions
- Horizontal scaling
- Autoscaling
- Sticky sessions as a tradeoff
- JWT vs server-side session store

---

## 12. Autoscaling

Autoscaling automatically adds or removes servers based on traffic or resource usage.

### Simple Diagram

```text
Traffic increases
       |
       v
Autoscaling Policy
       |
       v
Add More Web Servers
```

### Common Scaling Signals

- CPU usage
- Memory usage
- Request count
- Latency
- Queue depth
- Error rate

### Implementation Notes

- Use stateless web servers.
- Use health checks.
- Use load balancer integration.
- Set minimum, desired, and maximum instance counts.
- Avoid aggressive scaling to prevent instability.

---

## 13. Multiple Data Centers

For global users, use multiple data centers or regions.

### Simple Diagram

```text
Global Users
     |
     v
GeoDNS / Global Traffic Manager
     |
     +-------------------+
     |                   |
     v                   v
Data Center 1        Data Center 2
US-East              US-West
 |                   |
Web + Cache + DB     Web + Cache + DB
```

### Normal Operation

```text
Users are routed to the closest healthy data center.
```

### Failover Operation

```text
If DC2 fails:
100% traffic -> DC1
```

### Challenges

#### Traffic Redirection

- Use GeoDNS or global load balancing.
- Route users based on location and health.

#### Data Synchronization

- Replicate data across data centers.
- Handle replication lag.
- Decide between active-active and active-passive.

#### Deployment Consistency

- Automate deployment.
- Test from different regions.
- Keep configuration consistent.

### FAANG Talking Points

Mention:

- Multi-region deployment
- GeoDNS
- Active-active vs active-passive
- Disaster recovery
- RPO and RTO
- Data consistency tradeoffs
- Latency vs correctness

---

## 14. Message Queue

A message queue enables asynchronous communication between services.

Examples:

- Kafka
- RabbitMQ
- Amazon SQS
- Google Pub/Sub
- Azure Service Bus

### Simple Diagram

```text
Producer
   |
   | publish message
   v
Message Queue
   |
   | consume message
   v
Consumer / Worker
```

### Why Use a Queue?

- Decouples services
- Handles traffic spikes
- Improves reliability
- Enables asynchronous processing
- Allows producers and consumers to scale independently

### Photo Processing Example

```text
Web Servers
   |
   | publish photo processing job
   v
Message Queue
   |
   | workers consume jobs
   v
Photo Processing Workers
   |
   v
Object Storage / NoSQL / Database
```

### Implementation Notes

- Use retries for failed jobs.
- Use dead-letter queues.
- Make consumers idempotent.
- Monitor queue depth.
- Scale workers based on queue size.

### FAANG Talking Points

Mention:

- At-least-once delivery
- At-most-once delivery
- Exactly-once semantics as difficult/expensive
- Idempotency
- Retry policy
- Dead-letter queue
- Backpressure
- Queue depth based autoscaling

---

## 15. Logging, Metrics, Monitoring, and Automation

At large scale, observability and automation are mandatory.

### Logging

Use centralized logging to collect logs from all servers.

Examples:

- ELK stack
- Splunk
- CloudWatch Logs
- Datadog Logs

Track:

- Errors
- Exceptions
- Request logs
- Audit logs
- Security events

### Metrics

Important metrics:

#### Host-Level Metrics

- CPU
- Memory
- Disk I/O
- Network I/O

#### Service-Level Metrics

- Request rate
- Latency
- Error rate
- Saturation
- Queue depth

#### Business Metrics

- Daily active users
- Retention
- Revenue
- Conversion rate

### Monitoring

Monitor:

- Availability
- Latency
- Error rate
- Database health
- Cache hit ratio
- Queue lag

### Automation

Automate:

- Build
- Test
- Deployment
- Rollback
- Infrastructure provisioning
- Scaling

### FAANG Talking Points

Mention:

- SLIs, SLOs, and SLAs
- Alerting thresholds
- Dashboards
- Distributed tracing
- CI/CD
- Canary deployment
- Blue-green deployment
- Rollbacks

---

## 16. Database Scaling

There are two main approaches:

- Vertical scaling
- Horizontal scaling / sharding

---

## 17. Database Vertical Scaling

Add more CPU, RAM, disk, or IOPS to one database server.

### Simple Diagram

```text
Small DB Server
      |
      v
Bigger DB Server
More CPU / RAM / Disk
```

### Pros

- Simple
- No application-level partitioning
- Easier joins and transactions

### Cons

- Hardware limits
- Expensive
- Single point of failure
- Not enough for very large user bases

---

## 18. Database Horizontal Scaling / Sharding

Sharding splits a large database into smaller databases called shards.

### Simple Diagram

```text
              user_id % 4
                  |
    +-------------+-------------+
    |             |             |
    v             v             v
 Shard 0       Shard 1       Shard 2       Shard 3
 users 0,4     users 1,5     users 2,6     users 3,7
```

### Example Sharding Function

```text
shard_id = user_id % number_of_shards
```

### Sharding Key

A sharding key determines where data is stored.

Good sharding key properties:

- High cardinality
- Even distribution
- Frequently used in queries
- Avoids hotspots

Examples:

- user_id
- tenant_id
- region_id
- organization_id

### Sharding Benefits

- Handles large data volume
- Distributes load
- Improves write scalability
- Reduces data size per machine

### Sharding Challenges

#### Resharding

Needed when:

- A shard becomes too large.
- Data distribution becomes uneven.
- Number of shards changes.

Solution:

- Use consistent hashing.
- Use virtual shards.
- Rebalance data gradually.

#### Celebrity / Hotspot Problem

Problem:

```text
A popular user causes one shard to receive too much traffic.
```

Solutions:

- Split hot users into separate shards.
- Add caching.
- Use read replicas.
- Partition celebrity data further.

#### Joins Across Shards

Problem:

```text
Cross-shard joins are expensive.
```

Solutions:

- Denormalize data.
- Query by shard key.
- Use application-level joins only when needed.
- Use search/indexing systems for global queries.

### FAANG Talking Points

Mention:

- Sharding key selection
- Consistent hashing
- Hot partitions
- Cross-shard transactions
- Denormalization
- Data rebalancing
- Read/write patterns

---

## 19. Full Scalable Architecture

### Simple End-to-End Diagram

```text
                         +----------------+
                         |      CDN       |
                         | Static Assets  |
                         +----------------+
                                  |
Users -> DNS / GeoDNS -> Global Load Balancer
                                  |
                    +-------------+-------------+
                    |                           |
                    v                           v
              Data Center 1               Data Center 2
                    |                           |
              Load Balancer               Load Balancer
                    |                           |
          +---------+---------+       +---------+---------+
          |                   |       |                   |
          v                   v       v                   v
     Web Server 1        Web Server 2 Web Server 3    Web Server 4
          |                   |       |                   |
          +---------+---------+-------+---------+---------+
                    |
          +---------+---------+
          |                   |
          v                   v
        Cache            Message Queue
          |                   |
          v                   v
   Sharded Databases       Workers
          |                   |
          v                   v
       Replicas            NoSQL / Object Storage

Observability:
Logs + Metrics + Monitoring + Alerts + CI/CD + Automation
```

---

## 20. Final Scaling Summary

To scale from zero to millions of users:

1. Start with a simple single-server architecture.
2. Separate web tier and database tier.
3. Add a load balancer.
4. Add multiple web servers.
5. Add database replication.
6. Add cache layer.
7. Move static assets to CDN.
8. Make the web tier stateless.
9. Add autoscaling.
10. Support multiple data centers.
11. Use message queues for asynchronous work.
12. Add logging, metrics, monitoring, and automation.
13. Scale the database using sharding.
14. Split large systems into smaller services when needed.

---

## 21. FAANG Interview Checklist

When answering a system design question, cover these areas:

### Functional Requirements

- What should the system do?
- Who are the users?
- What are the main APIs?

### Non-Functional Requirements

- Scale
- Latency
- Availability
- Consistency
- Durability
- Security
- Cost

### Capacity Estimation

Mention estimates for:

- Daily active users
- Requests per second
- Reads vs writes
- Storage growth
- Bandwidth
- Cache size

### API Design

Example:

```http
GET /users/{user_id}
POST /users
PUT /users/{user_id}
DELETE /users/{user_id}
```

### Data Model

Mention:

- Tables or collections
- Primary keys
- Indexes
- Sharding key
- Relationships

### High-Level Design

Include:

- Clients
- DNS
- CDN
- Load balancer
- Web servers
- Cache
- Database
- Queue
- Workers
- Monitoring

### Deep Dives

Be ready to discuss:

- Cache invalidation
- Database replication
- Sharding
- Hot keys
- Failure handling
- Rate limiting
- Consistency model
- Multi-region strategy

### Bottlenecks

Identify likely bottlenecks:

- Database writes
- Hot cache keys
- Queue backlog
- Network latency
- Cross-region replication
- Large fanout reads/writes

### Tradeoffs

Always explain tradeoffs:

```text
SQL gives transactions and joins, but may be harder to scale horizontally.
NoSQL scales well, but may sacrifice joins and strong consistency.
Cache improves latency, but introduces consistency challenges.
Multi-region improves availability, but increases data synchronization complexity.
```

---

## 22. Common Interview Phrases

Use these phrases during interviews:

- “I will keep the web tier stateless so we can scale horizontally.”
- “I will use a load balancer to distribute traffic and improve availability.”
- “I will use read replicas because read traffic is usually higher than write traffic.”
- “I will add cache for frequently accessed and rarely updated data.”
- “I will use CDN for static assets to reduce latency and origin load.”
- “I will use a message queue to decouple slow asynchronous processing.”
- “I will choose the sharding key carefully to avoid hot partitions.”
- “I will add monitoring, logging, alerts, and automation for production readiness.”
- “This introduces a tradeoff between consistency, availability, latency, and operational complexity.”

---

## 23. Common Pitfalls

Avoid these mistakes in interviews:

- Making every component distributed too early.
- Ignoring database bottlenecks.
- Forgetting cache invalidation.
- Forgetting failure handling.
- Forgetting monitoring and alerts.
- Using sticky sessions without explaining the tradeoff.
- Choosing a poor sharding key.
- Ignoring hot keys and celebrity users.
- Ignoring deployment and rollback strategy.
- Ignoring cost.

---

## 24. Implementation Roadmap

### Phase 1: MVP

```text
Single server + SQL database + basic logging
```

### Phase 2: Early Growth

```text
Separate web server and database
Add load balancer
Add multiple web servers
```

### Phase 3: Performance

```text
Add Redis/Memcached cache
Move static assets to CDN
Add database read replicas
```

### Phase 4: Reliability

```text
Make web tier stateless
Add autoscaling
Add centralized logging and monitoring
Add backups and failover
```

### Phase 5: Large Scale

```text
Add message queues
Add workers
Shard database
Add NoSQL where appropriate
Support multi-region or multi-data-center deployment
```

### Phase 6: Production Maturity

```text
CI/CD
Canary deployment
Blue-green deployment
Distributed tracing
SLO-based alerting
Disaster recovery testing
Cost optimization
```

---

## 25. One-Minute Interview Summary

A scalable architecture starts simple with one server. As traffic grows, separate the web and database tiers, add a load balancer, and run multiple stateless web servers. Use database replication to improve read scalability and availability. Add cache for frequently accessed data and CDN for static assets. For global users, use multiple data centers with GeoDNS and replication. Use message queues to decouple slow tasks and workers. As data grows, shard the database and move suitable workloads to NoSQL. Finally, production systems require logging, metrics, monitoring, alerting, automation, and reliable deployment pipelines.

---

## 26. Final Golden Rules

```text
Keep web tier stateless.
Build redundancy at every layer.
Cache aggressively but carefully.
Use CDN for static assets.
Use queues for asynchronous work.
Scale reads with replicas.
Scale writes with sharding.
Choose sharding keys carefully.
Design for failure.
Monitor everything.
Automate deployments.
Explain tradeoffs clearly.
```
