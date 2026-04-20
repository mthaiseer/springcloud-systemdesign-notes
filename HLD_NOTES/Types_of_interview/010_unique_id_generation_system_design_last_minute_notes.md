# Unique ID Generation in Distributed Systems – Last-Minute Notes

> A one-stop reference for **ID generation in system design**: trade-offs, when to use each approach, and **working Spring Boot examples** for every major method.

---

## Why This Problem Matters
On one server, IDs are easy:
- keep a counter
- increment it
- done

At scale, it breaks.

If two servers generate IDs independently:

```text
Server A -> 1001
Server B -> 1001
Collision
```

This is why ID generation shows up often in system design interviews:
- coordination
- scalability
- fault tolerance
- storage/index performance
- security/predictability

---

# 1) Why Unique ID Generation Is Hard

## Single server is trivial
Database `AUTO_INCREMENT` or `SERIAL` works perfectly:
- atomic
- sequential
- compact
- easy to reason about

## Multi-server problem
As soon as you scale horizontally:
- duplicate IDs become possible
- coordination becomes necessary unless you avoid it by design

---

## The coordination dilemma
The obvious fix is a central authority.

### But coordination costs:
- **Latency** -> network round-trip
- **Single point of failure**
- **Throughput bottleneck**

### Core insight
```text
The best large-scale ID schemes avoid coordination on every request.
```

---

## Different systems need different properties

| Requirement | Meaning | Example |
|---|---|---|
| Uniqueness | no collisions | every system |
| Sortability | ordered by time | feeds, logs, time-series |
| Compactness | small footprint | DB PKs, URL shortener |
| Unpredictability | not guessable | public/user-facing IDs |
| High throughput | millions/sec | social/media/logging |
| No coordination | independent generation | microservices/serverless |

No single approach gives everything perfectly.

---

# 2) Approach 1: Database Auto-Increment

## Basic idea
Let the database assign IDs.

### SQL
```sql
CREATE TABLE users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255)
);
```

```sql
INSERT INTO users(name) VALUES ('Alice'); -- id = 1
INSERT INTO users(name) VALUES ('Bob');   -- id = 2
```

---

## Why it works
The DB maintains a single atomic counter.

### Pros
- simple
- sequential
- compact (64-bit integer)
- naturally sortable
- human-readable enough

### Cons
- DB round-trip required
- primary DB is bottleneck
- hard to scale horizontally
- predictable IDs
- problematic in multi-primary setups

---

## Multi-DB workaround
### Step-based pattern
- Server 1 -> odd IDs
- Server 2 -> even IDs

### Range-based pattern
- Server 1 -> 1 to 1,000,000
- Server 2 -> 1,000,001 to 2,000,000

These reduce collision risk, but add operational complexity.

---

## Best for
- small/medium systems
- one main DB
- internal systems
- when sequentiality matters more than horizontal independence

---

# 3) Approach 2: UUID

## What it is
A UUID is a 128-bit identifier designed to be globally unique.

Example:
```text
550e8400-e29b-41d4-a716-446655440000
```

---

## Common UUID versions

### UUID v1
- timestamp + MAC address
- sortable-ish
- exposes machine identity

### UUID v4
- random
- most common
- not sortable

### UUID v7
- timestamp + randomness
- sortable
- modern recommended UUID variant

---

## UUID collision risk
UUID v4 has 122 random bits.

Practical rule:
```text
Collision probability is effectively zero for real systems.
```

---

## Big downside: database index performance
Random UUIDs (especially v4) are bad for B-tree indexes:
- inserts happen all over the index
- page splits
- fragmentation
- poorer cache locality

### Sequential IDs
Good for indexes:
```text
1, 2, 3, 4 ...
```

### Random UUIDs
Bad for locality:
```text
a1f..., 91b..., 33e..., f8a...
```

---

## Pros / Cons

| Pros | Cons |
|---|---|
| no coordination | 128 bits (large) |
| client/server generation possible | UUID v4 not sortable |
| near-zero collision risk | poor DB index locality |
| widely supported | not compact / not human-friendly |

---

## Best for
- distributed systems
- client-generated IDs
- cross-service merging
- cases where coordination must be avoided

### Rule
- use **UUID v4** if order does not matter
- use **UUID v7** if time-sortable UUIDs are desired

---

# 4) Approach 3: Snowflake IDs

## Why Snowflake exists
Twitter needed IDs that were:
- unique
- 64-bit
- sortable by time
- generated independently by many servers

---

## Structure
A typical Snowflake layout:

```text
1 bit   -> sign
41 bits -> timestamp
10 bits -> machine ID
12 bits -> sequence
```

### Meaning
- sign bit = always 0
- timestamp = ms since custom epoch
- machine ID = unique server/node
- sequence = counter within same ms

---

## Capacity
Per machine:
```text
4096 IDs/ms = ~4 million/sec
```

Per 1024 machines:
```text
~4 billion IDs/sec
```

Timestamp range:
```text
~69 years from custom epoch
```

---

## Core idea
Each machine can generate IDs independently if machine IDs are unique.

### Pseudocode
```python
id = ((timestamp - epoch) << 22) | (machine_id << 12) | sequence
```

---

## Pros / Cons

| Pros | Cons |
|---|---|
| 64-bit compact | need unique machine IDs |
| time-sortable | depends on clock correctness |
| very high throughput | more complex |
| no per-request coordination | finite epoch lifetime |

---

## Clock problem
Snowflake relies on time:
- clock moving backward can break ordering or uniqueness
- clock skew across machines causes slight global ordering issues

### Mitigation
- NTP
- refuse generation when clock goes backward
- use a monotonic timestamp strategy if needed

---

## Best for
- large-scale services
- time-sorted IDs
- compact DB primary keys
- systems like Twitter/Discord/Instagram

---

# 5) Approach 4: ULID

## What it is
ULID = Universally Unique Lexicographically Sortable Identifier

### Structure
```text
48 bits  -> timestamp
80 bits  -> randomness
```

Encoded in Crockford Base32.

Example:
```text
01ARZ3NDEKTSV4RRFFQ69G5FAV
```

---

## Why ULID is attractive
If you sort ULIDs as strings, they sort by creation time.

That means:
- coordination-free
- sortable
- UUID-like safety
- shorter than UUID string form

---

## ULID vs UUID

| Property | UUID v4 | ULID |
|---|---|---|
| Size | 128 bits | 128 bits |
| Sortable | No | Yes |
| Timestamp extractable | No | Yes |
| String length | 36 chars | 26 chars |
| Coordination | None | None |

---

## Pros / Cons

| Pros | Cons |
|---|---|
| sortable | still 128-bit |
| no coordination | not as universally native as UUID |
| shorter string than UUID | still larger than Snowflake |
| URL-friendly enough | some libraries vary in monotonic guarantees |

---

## Best for
- modern distributed services
- sortable public/internal IDs
- replacing UUID v4 in many applications

---

# 6) Approach 5: MongoDB ObjectId

## Structure
Mongo ObjectId is 12 bytes (96 bits):

```text
4 bytes -> timestamp
5 bytes -> random/process info
3 bytes -> counter
```

Example:
```text
507f1f77bcf86cd799439011
```

---

## Properties
- roughly time-sortable
- compact-ish
- generated without coordination
- default for MongoDB

### Pros
- smaller than UUID
- built into MongoDB
- no extra infra

### Cons
- only second-level time granularity
- not ideal outside Mongo ecosystem
- not as standardized elsewhere

---

## Best for
- MongoDB applications

---

# 7) Approach 6: Ticket Server

## Idea
Use a central service to hand out **blocks** of IDs.

Instead of requesting one ID at a time:
- app server asks for 1000 IDs
- uses them locally
- requests next block when exhausted

### Example
- Server A gets 1–1000
- Server B gets 1001–2000

---

## Why it helps
It reduces coordination frequency dramatically.

### Pros
- sequential IDs
- simple concept
- works with any backing DB
- no per-ID coordination

### Cons
- central allocator still exists
- wasted IDs if server dies mid-block
- network hop for new ranges
- more ops complexity than pure local generation

---

## Best for
- systems that really need sequential-ish IDs
- existing DB-heavy systems
- manageable scale

---

# 8) Other Notable Schemes

## KSUID
- 160 bits
- timestamp + randomness
- sortable
- Base62 encoded
- good for logs/events/public IDs

## Sonyflake
- Snowflake-like
- longer lifespan
- more machine IDs
- lower throughput than Snowflake

## NanoID
- short, URL-friendly, configurable
- random
- great for public short IDs
- not naturally sortable

---

# 9) Decision Framework

## Quick decision tree

### Need sequential IDs?
- yes -> Auto-increment / Ticket server
- no -> continue

### Need no coordination?
- yes -> UUID / ULID / UUID v7 / Snowflake
- no -> Auto-increment / Ticket server acceptable

### Need time-sortable?
- yes -> Snowflake / ULID / UUID v7 / ObjectId
- no -> UUID v4 / NanoID

### Need 64-bit only?
- yes -> Snowflake / Auto-increment / Ticket server
- no -> UUID / ULID / KSUID / NanoID

### Need public unpredictable IDs?
- yes -> UUID v4 / ULID / NanoID
- avoid raw auto-increment

---

## Practical recommendations

| Need | Recommendation |
|---|---|
| simplest small-scale system | auto-increment |
| distributed, order not needed | UUID v4 |
| distributed + sortable | ULID or UUID v7 |
| high scale + 64-bit | Snowflake |
| MongoDB | ObjectId |
| short public IDs | NanoID |
| sequential blocks | Ticket server |

---

# 10) Comparison Table

| Approach | Size | Sortable | Coordination | Throughput | Best For |
|---|---:|---|---|---|---|
| Auto-Increment | 64-bit | Yes | Required | Medium | single DB |
| UUID v4 | 128-bit | No | None | High | distributed no-order |
| UUID v7 | 128-bit | Yes | None | High | modern sortable UUID |
| Snowflake | 64-bit | Yes | machine ID only | Very High | high-scale services |
| ULID | 128-bit | Yes | None | High | general distributed use |
| ObjectId | 96-bit | Roughly | None | High | MongoDB |
| Ticket Server | 64-bit | Yes | Block allocation | Medium | sequential IDs |

---

# 11) Production Considerations

## Clock synchronization
For Snowflake / UUID v7 / ULID-like sortable IDs:
- run NTP
- monitor drift
- decide what to do when clock goes backward

## Machine ID assignment
For Snowflake:
- config management
- instance metadata
- etcd / ZooKeeper / Consul
- Kubernetes node/pod strategy

## Collision handling
Even if collisions are unlikely:
- keep unique DB constraint
- log duplicate key errors
- alert on suspicious collision spikes

## Migration warning
```text
Changing ID schemes after launch is painful.
Choose early if possible.
```

If you must migrate:
- maintain old/new mapping
- support coexistence
- plan long transition periods

---

# 12) Interview Answer Template

```text
The right ID generation strategy depends on scale, sortability, storage size, predictability, and whether coordination is acceptable.

If I have a simple single-database system, I’d use auto-increment because it is compact and easy.
If I need distributed generation without coordination, I’d use UUID v4 if ordering does not matter, or ULID / UUID v7 if I want time-sortable IDs.
If I need 64-bit sortable IDs at very high scale, I’d use a Snowflake-style generator with timestamp, machine ID, and sequence.
If sequential IDs are required but I can tolerate some coordination, I’d consider a ticket server that allocates blocks of IDs.

I’d also think about database index behavior, clock drift for time-based schemes, and whether IDs are public-facing or should be unpredictable.
```

---

# 13) Spring Boot Examples – Working Implementations

Below are **practical Spring Boot examples** for each main key generation method.

---

## 13.1 Auto-Increment with Spring Boot + JPA

### Maven dependencies
```xml
<dependencies>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-data-jpa</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
    </dependency>
    <dependency>
        <groupId>com.h2database</groupId>
        <artifactId>h2</artifactId>
        <scope>runtime</scope>
    </dependency>
</dependencies>
```

### Entity
```java
import jakarta.persistence.*;

@Entity
public class ShortUrlEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String originalUrl;
    private String shortCode;

    public Long getId() { return id; }
    public String getOriginalUrl() { return originalUrl; }
    public void setOriginalUrl(String originalUrl) { this.originalUrl = originalUrl; }
    public String getShortCode() { return shortCode; }
    public void setShortCode(String shortCode) { this.shortCode = shortCode; }
}
```

### Repository
```java
import org.springframework.data.jpa.repository.JpaRepository;

public interface ShortUrlRepository extends JpaRepository<ShortUrlEntity, Long> {
}
```

### Service
```java
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AutoIncrementShortUrlService {

    private final ShortUrlRepository repository;

    public AutoIncrementShortUrlService(ShortUrlRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public ShortUrlEntity create(String originalUrl) {
        ShortUrlEntity entity = new ShortUrlEntity();
        entity.setOriginalUrl(originalUrl);

        entity = repository.save(entity); // DB assigns ID

        String shortCode = Base62.encode(entity.getId());
        entity.setShortCode(shortCode);

        return repository.save(entity);
    }
}
```

### Base62 helper
```java
public class Base62 {
    private static final String ALPHABET = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";

    public static String encode(long value) {
        if (value == 0) return "0";
        StringBuilder sb = new StringBuilder();
        while (value > 0) {
            sb.append(ALPHABET.charAt((int)(value % 62)));
            value /= 62;
        }
        return sb.reverse().toString();
    }
}
```

### Controller
```java
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auto")
public class AutoIncrementController {

    private final AutoIncrementShortUrlService service;

    public AutoIncrementController(AutoIncrementShortUrlService service) {
        this.service = service;
    }

    @PostMapping
    public ShortUrlEntity create(@RequestParam String url) {
        return service.create(url);
    }
}
```

---

## 13.2 UUID v4 with Spring Boot

### Service
```java
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class UuidV4Service {

    public String generate() {
        return UUID.randomUUID().toString();
    }
}
```

### Controller
```java
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/uuid-v4")
public class UuidV4Controller {

    private final UuidV4Service service;

    public UuidV4Controller(UuidV4Service service) {
        this.service = service;
    }

    @GetMapping
    public String generate() {
        return service.generate();
    }
}
```

### Notes
- easiest distributed option
- not sortable
- great when order doesn’t matter

---

## 13.3 UUID v7 with Spring Boot

Java standard library may not yet provide native UUID v7 depending on runtime, so a library is typical.

### Maven dependency
Example using `uuid-creator`:
```xml
<dependency>
    <groupId>com.github.f4b6a3</groupId>
    <artifactId>uuid-creator</artifactId>
    <version>6.0.0</version>
</dependency>
```

### Service
```java
import com.github.f4b6a3.uuid.UuidCreator;
import org.springframework.stereotype.Service;

@Service
public class UuidV7Service {

    public String generate() {
        return UuidCreator.getTimeOrderedEpoch().toString();
    }
}
```

### Controller
```java
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/uuid-v7")
public class UuidV7Controller {

    private final UuidV7Service service;

    public UuidV7Controller(UuidV7Service service) {
        this.service = service;
    }

    @GetMapping
    public String generate() {
        return service.generate();
    }
}
```

### Why use this
- coordination-free
- sortable
- modern UUID choice

---

## 13.4 Snowflake with Spring Boot

### Snowflake generator
```java
import org.springframework.stereotype.Component;

@Component
public class SnowflakeGenerator {

    private static final long CUSTOM_EPOCH = 1704067200000L; // 2024-01-01
    private static final long MACHINE_ID_BITS = 10L;
    private static final long SEQUENCE_BITS = 12L;

    private static final long MAX_MACHINE_ID = ~(-1L << MACHINE_ID_BITS); // 1023
    private static final long MAX_SEQUENCE = ~(-1L << SEQUENCE_BITS);     // 4095

    private final long machineId;
    private long lastTimestamp = -1L;
    private long sequence = 0L;

    public SnowflakeGenerator() {
        this.machineId = 1L; // configure uniquely per instance
        if (machineId < 0 || machineId > MAX_MACHINE_ID) {
            throw new IllegalArgumentException("Invalid machine ID");
        }
    }

    public synchronized long nextId() {
        long currentTimestamp = timestamp();

        if (currentTimestamp < lastTimestamp) {
            throw new IllegalStateException("Clock moved backwards");
        }

        if (currentTimestamp == lastTimestamp) {
            sequence = (sequence + 1) & MAX_SEQUENCE;
            if (sequence == 0) {
                currentTimestamp = waitNextMillis(currentTimestamp);
            }
        } else {
            sequence = 0;
        }

        lastTimestamp = currentTimestamp;

        return ((currentTimestamp - CUSTOM_EPOCH) << (MACHINE_ID_BITS + SEQUENCE_BITS))
                | (machineId << SEQUENCE_BITS)
                | sequence;
    }

    private long waitNextMillis(long currentTimestamp) {
        while (currentTimestamp == lastTimestamp) {
            currentTimestamp = timestamp();
        }
        return currentTimestamp;
    }

    private long timestamp() {
        return System.currentTimeMillis();
    }
}
```

### Service
```java
import org.springframework.stereotype.Service;

@Service
public class SnowflakeService {

    private final SnowflakeGenerator generator;

    public SnowflakeService(SnowflakeGenerator generator) {
        this.generator = generator;
    }

    public long generate() {
        return generator.nextId();
    }
}
```

### Controller
```java
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/snowflake")
public class SnowflakeController {

    private final SnowflakeService service;

    public SnowflakeController(SnowflakeService service) {
        this.service = service;
    }

    @GetMapping
    public long generate() {
        return service.generate();
    }
}
```

### Notes
- set unique machine ID per server
- perfect for compact sortable 64-bit IDs
- monitor clock drift

---

## 13.5 ULID with Spring Boot

### Maven dependency
Example using `ulid-creator`:
```xml
<dependency>
    <groupId>com.github.f4b6a3</groupId>
    <artifactId>ulid-creator</artifactId>
    <version>5.2.3</version>
</dependency>
```

### Service
```java
import com.github.f4b6a3.ulid.UlidCreator;
import org.springframework.stereotype.Service;

@Service
public class UlidService {

    public String generate() {
        return UlidCreator.getUlid().toString();
    }
}
```

### Controller
```java
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/ulid")
public class UlidController {

    private final UlidService service;

    public UlidController(UlidService service) {
        this.service = service;
    }

    @GetMapping
    public String generate() {
        return service.generate();
    }
}
```

### Why use this
- sortable
- no machine ID coordination
- great general default for modern distributed systems

---

## 13.6 MongoDB ObjectId with Spring Boot

### Maven dependency
```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-mongodb</artifactId>
</dependency>
```

### Document
```java
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document("short_urls")
public class ShortUrlDocument {

    @Id
    private String id; // Mongo ObjectId by default

    private String originalUrl;
    private String shortCode;

    public String getId() { return id; }
    public String getOriginalUrl() { return originalUrl; }
    public void setOriginalUrl(String originalUrl) { this.originalUrl = originalUrl; }
    public String getShortCode() { return shortCode; }
    public void setShortCode(String shortCode) { this.shortCode = shortCode; }
}
```

### Repository
```java
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ShortUrlMongoRepository extends MongoRepository<ShortUrlDocument, String> {
}
```

### Service
```java
import org.springframework.stereotype.Service;

@Service
public class ObjectIdService {

    private final ShortUrlMongoRepository repository;

    public ObjectIdService(ShortUrlMongoRepository repository) {
        this.repository = repository;
    }

    public ShortUrlDocument create(String originalUrl) {
        ShortUrlDocument doc = new ShortUrlDocument();
        doc.setOriginalUrl(originalUrl);
        return repository.save(doc); // Mongo generates ObjectId
    }
}
```

### Controller
```java
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/objectid")
public class ObjectIdController {

    private final ObjectIdService service;

    public ObjectIdController(ObjectIdService service) {
        this.service = service;
    }

    @PostMapping
    public ShortUrlDocument create(@RequestParam String url) {
        return service.create(url);
    }
}
```

---

## 13.7 Ticket Server with Spring Boot

### Concept
A central service allocates blocks of IDs.

### Table
```sql
CREATE TABLE ticket_allocations (
    stub VARCHAR(20) PRIMARY KEY,
    next_id BIGINT NOT NULL
);

INSERT INTO ticket_allocations(stub, next_id) VALUES ('default', 1);
```

### JPA entity
```java
import jakarta.persistence.*;

@Entity
@Table(name = "ticket_allocations")
public class TicketAllocation {

    @Id
    private String stub;

    private Long nextId;

    public String getStub() { return stub; }
    public void setStub(String stub) { this.stub = stub; }

    public Long getNextId() { return nextId; }
    public void setNextId(Long nextId) { this.nextId = nextId; }
}
```

### Repository
```java
import org.springframework.data.jpa.repository.JpaRepository;

public interface TicketAllocationRepository extends JpaRepository<TicketAllocation, String> {
}
```

### Allocator service
```java
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TicketServerService {

    private final TicketAllocationRepository repository;
    private static final long BLOCK_SIZE = 1000L;

    public TicketServerService(TicketAllocationRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public IdBlock allocateBlock() {
        TicketAllocation allocation = repository.findById("default")
                .orElseThrow();

        long start = allocation.getNextId();
        long end = start + BLOCK_SIZE - 1;

        allocation.setNextId(end + 1);
        repository.save(allocation);

        return new IdBlock(start, end);
    }

    public record IdBlock(long start, long end) {}
}
```

### Controller
```java
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/ticket-server")
public class TicketServerController {

    private final TicketServerService service;

    public TicketServerController(TicketServerService service) {
        this.service = service;
    }

    @PostMapping("/allocate")
    public TicketServerService.IdBlock allocate() {
        return service.allocateBlock();
    }
}
```

### Client-side local block usage idea
```java
import org.springframework.stereotype.Component;

@Component
public class LocalBlockIdGenerator {

    private final TicketServerService ticketServerService;

    private long current = 0;
    private long max = -1;

    public LocalBlockIdGenerator(TicketServerService ticketServerService) {
        this.ticketServerService = ticketServerService;
    }

    public synchronized long nextId() {
        if (current > max) {
            TicketServerService.IdBlock block = ticketServerService.allocateBlock();
            current = block.start();
            max = block.end();
        }
        return current++;
    }
}
```

---

## 13.8 NanoID with Spring Boot

### Maven dependency
```xml
<dependency>
    <groupId>com.aventrix.jnanoid</groupId>
    <artifactId>jnanoid</artifactId>
    <version>2.0.0</version>
</dependency>
```

### Service
```java
import com.aventrix.jnanoid.jnanoid.NanoIdUtils;
import org.springframework.stereotype.Service;

@Service
public class NanoIdService {

    public String generate() {
        return NanoIdUtils.randomNanoId();
    }

    public String generateShort(int size) {
        return NanoIdUtils.randomNanoId(NanoIdUtils.DEFAULT_NUMBER_GENERATOR,
                NanoIdUtils.DEFAULT_ALPHABET, size);
    }
}
```

### Controller
```java
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/nanoid")
public class NanoIdController {

    private final NanoIdService service;

    public NanoIdController(NanoIdService service) {
        this.service = service;
    }

    @GetMapping
    public String generate() {
        return service.generate();
    }

    @GetMapping("/short")
    public String generateShort(@RequestParam(defaultValue = "10") int size) {
        return service.generateShort(size);
    }
}
```

### Best for
- short public IDs
- URL-friendly tokens
- compact random IDs

---

# 14) What I’d Actually Choose

## For a URL shortener
If public short IDs matter:
- **counter + Base62** for short compact codes
- or **NanoID** if unpredictability matters more than sequence

## For a typical modern microservice
- **ULID** or **UUID v7**

## For a very high-scale platform with 64-bit constraints
- **Snowflake**

## For MongoDB
- **ObjectId**

## For simple monolith / internal admin app
- **Auto-increment**

---

# 15) Polished Key Takeaways

- **Single-node ID generation is easy; distributed generation is not**  
  The problem starts when multiple machines must generate IDs independently.

- **Coordination solves uniqueness but creates bottlenecks**  
  Centralized solutions add latency, reduce throughput, and introduce failure risk.

- **UUIDs remove coordination entirely**  
  They are great for distributed systems, but UUID v4 hurts database index locality.

- **Time-sortable IDs are often better in practice**  
  ULID, UUID v7, and Snowflake improve storage behavior and operational debugging.

- **Snowflake is the standard answer for compact, sortable, high-scale 64-bit IDs**  
  It is proven in production but depends on machine IDs and healthy clocks.

- **ULID and UUID v7 are excellent modern defaults**  
  They avoid coordination while preserving chronological ordering.

- **Ticket servers are a practical middle ground**  
  If you need sequential IDs, allocate ranges instead of coordinating every request.

- **The best choice depends on your constraints**  
  Size, sortability, public visibility, throughput, and operational simplicity all matter.

- **Choose early**  
  Migrating ID schemes later is painful and risky.

---

## Final 1-Line Shortcut
```text
Simple app -> auto-increment | Distributed default -> ULID/UUIDv7 | High-scale 64-bit -> Snowflake
```
