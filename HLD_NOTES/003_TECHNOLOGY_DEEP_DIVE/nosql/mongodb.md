
# MongoDB System Design Notes
*Interview-ready, implementation-oriented, and practical for projects from ~100 RPS to ~50K RPS.*

## How to use these notes
This guide is designed to help you do two things:
1. explain MongoDB well in a system design interview
2. build a real Spring Boot service on top of MongoDB

For each major topic, you get:
- when to use it
- the design rule
- MongoDB query examples
- Spring Boot examples
- scaling notes

---

## 1. Why MongoDB shows up in system design

MongoDB is a strong choice when your data is naturally **document-shaped**, your schema changes often, and you want to scale reads and writes horizontally without designing around a rigid relational schema.

Typical good fits:
- user profiles
- carts
- product catalogs
- content metadata
- activity feeds
- event logs with flexible fields

MongoDB is less attractive when:
- your system is highly relational
- you need frequent cross-entity joins
- you rely on strict relational constraints
- most operations are multi-entity transactions

### Interview answer
Use MongoDB when the main unit of access is a document, not a normalized graph of tables.

### Example
A product catalog is a good MongoDB fit because a laptop, shoe, and chair all have different attributes, but can still live in one `products` collection.

---

## 2. MongoDB architecture overview

In a sharded cluster:

- applications connect to `mongos`
- `mongos` routes queries using metadata from config servers
- each shard is usually a replica set
- writes go to the primary of the target shard
- reads can go to primary or secondaries depending on read preference

### Core components
- **mongos**: stateless query router
- **config servers**: store chunk and shard metadata
- **shards**: store actual data
- **replica set members**: primary + secondaries for HA

### Request flow
- write with shard key → routed to one shard
- read with shard key → targeted query
- read without shard key → scatter-gather across shards

### Interview answer
Sharding gives horizontal scale. Replica sets give availability. `mongos` hides routing complexity from the application.

---

## 3. When to choose MongoDB

### Choose MongoDB when you have
- flexible or evolving schemas
- nested or hierarchical data
- rich document queries
- need for rapid iteration
- built-in horizontal scaling needs

### Avoid MongoDB when you need
- heavy relational joins
- strict relational integrity
- frequent multi-document transactions
- pure key-value performance at massive scale
- OLAP/data warehouse style analytics

### Common systems
- CMS
- e-commerce product catalog
- social profile/post systems
- gaming user state
- mobile app backends
- real-time dashboards

### Example
A CMS with articles, galleries, embedded media blocks, and optional SEO fields is easier in MongoDB than in a rigid relational schema.

---

## 4. Data modeling: embedding vs referencing

This is the most important MongoDB modeling decision.

### Rule of thumb
- **Embed** when data is read together and grows in a bounded way
- **Reference** when data is shared, queried independently, or grows unbounded
- **Hybrid** is often best in production

---

## 4.1 Embedding

Embed related data in one document.

### When to use
- parent and child are read together
- child does not need its own lifecycle
- child list is bounded
- you want atomic updates in one document

### MongoDB example
```javascript
db.orders.insertOne({
  _id: "order_123",
  userId: "user_1",
  items: [
    { productId: "p1", name: "Laptop", price: 999, quantity: 1 },
    { productId: "p2", name: "Mouse", price: 29, quantity: 2 }
  ],
  shippingAddress: {
    city: "San Francisco",
    zip: "94102"
  },
  total: 1057,
  status: "PLACED"
})
```

### Read example
```javascript
db.orders.findOne({ _id: "order_123" })
```

### Spring Boot model
```java
package com.example.mongo.order;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.util.List;

@Document("orders")
public class Order {
    @Id
    private String id;
    private String userId;
    private List<OrderItem> items;
    private Address shippingAddress;
    private BigDecimal total;
    private String status;

    // getters/setters
}

class OrderItem {
    private String productId;
    private String name;
    private BigDecimal price;
    private int quantity;

    // getters/setters
}

class Address {
    private String city;
    private String zip;

    // getters/setters
}
```

### Spring Boot repository
```java
package com.example.mongo.order;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface OrderRepository extends MongoRepository<Order, String> {
    List<Order> findByUserId(String userId);
}
```

### Why embedding works here
Orders are usually fetched with their items. You want one read and one atomic document write.

### Scaling note
Embedding is great for read-heavy APIs because it avoids joins and extra network round trips.

---

## 4.2 Referencing

Store related data in separate collections and link by ID.

### When to use
- child documents are large or unbounded
- child data is reused in many places
- child is queried independently
- many-to-many relationships exist

### MongoDB example
```javascript
db.orders.insertOne({
  _id: "order_123",
  customerId: "customer_456",
  itemIds: ["item_1", "item_2"],
  total: 1057,
  status: "SHIPPED"
})

db.customers.insertOne({
  _id: "customer_456",
  name: "John Doe",
  email: "john@example.com"
})
```

### Lookup example
```javascript
db.orders.aggregate([
  { $match: { _id: "order_123" } },
  {
    $lookup: {
      from: "customers",
      localField: "customerId",
      foreignField: "_id",
      as: "customer"
    }
  }
])
```

### Spring Boot model
```java
@Document("customers")
public class Customer {
    @Id
    private String id;
    private String name;
    private String email;
}
```

### Design warning
If most requests need order + customer + items together, overusing references can make MongoDB feel like a worse relational database.

---

## 4.3 Hybrid approach

This is often the best real-world choice.

### Idea
Reference canonical data, but embed a snapshot for fast reads.

### MongoDB example
```javascript
db.orders.insertOne({
  _id: "order_123",
  customerId: "customer_456",
  customerSnapshot: {
    name: "John Doe",
    email: "john@example.com"
  },
  items: [
    { productId: "p1", name: "Laptop", price: 999, quantity: 1 }
  ],
  total: 999
})
```

### Why this works
- `customerId` points to the source of truth
- `customerSnapshot` makes order-history reads fast
- line items stay atomic with the order

### Interview answer
Use hybrid when display speed matters but you still want a canonical source of truth elsewhere.

---

## 5. Schema design patterns

These patterns show real MongoDB maturity.

---

## 5.1 Attribute pattern

Use when documents have many sparse or dynamic fields.

### Before
```javascript
{
  name: "Laptop",
  ram: "16GB",
  storage: "512GB SSD",
  color: null,
  material: null
}
```

### After
```javascript
{
  name: "Laptop",
  attributes: [
    { key: "ram", value: "16GB" },
    { key: "storage", value: "512GB SSD" }
  ]
}
```

### Query example
```javascript
db.products.find({
  attributes: {
    $elemMatch: { key: "ram", value: "16GB" }
  }
})
```

### Index example
```javascript
db.products.createIndex({
  "attributes.key": 1,
  "attributes.value": 1
})
```

### Spring Boot example
```java
public class ProductAttribute {
    private String key;
    private String value;
}

@Document("products")
public class Product {
    @Id
    private String id;
    private String name;
    private List<ProductAttribute> attributes;
}
```

### Best use case
Product catalogs with different fields per category.

---

## 5.2 Bucket pattern

Use for high-frequency time-series style data.

### Example document
```javascript
db.sensor_readings.insertOne({
  sensorId: "S1",
  bucketStart: ISODate("2024-01-15T10:00:00Z"),
  bucketEnd: ISODate("2024-01-15T11:00:00Z"),
  readings: [
    { offsetSec: 0, value: 23.5 },
    { offsetSec: 1, value: 23.6 },
    { offsetSec: 2, value: 23.4 }
  ],
  count: 3,
  min: 23.4,
  max: 23.6
})
```

### Query example
```javascript
db.sensor_readings.find({
  sensorId: "S1",
  bucketStart: { $gte: ISODate("2024-01-15T10:00:00Z") }
})
```

### Spring Boot model
```java
@Document("sensor_readings")
public class SensorBucket {
    @Id
    private String id;
    private String sensorId;
    private Instant bucketStart;
    private Instant bucketEnd;
    private List<Reading> readings;
    private int count;
    private double min;
    private double max;
}

class Reading {
    private int offsetSec;
    private double value;
}
```

### Why it helps
Fewer documents, fewer index entries, better write efficiency.

---

## 5.3 Outlier pattern

Use when most documents are small but a few become huge.

### Example
```javascript
db.posts.insertOne({
  _id: "post_1",
  text: "viral post",
  commentsPreview: [
    { user: "u1", text: "nice" },
    { user: "u2", text: "wow" }
  ],
  hasOverflowComments: true
})

db.post_comments_overflow.insertOne({
  postId: "post_1",
  comments: [
    { user: "u3", text: "great" },
    { user: "u4", text: "amazing" }
  ]
})
```

### Why it helps
Normal posts stay compact while only outliers pay the cost of overflow storage.

---

## 5.4 Computed pattern

Precompute expensive read values.

### Example document
```javascript
db.products.insertOne({
  _id: "p1",
  name: "Laptop",
  computed: {
    reviewCount: 4,
    averageRating: 4.25
  }
})
```

### Incremental update example
```javascript
db.products.updateOne(
  { _id: "p1" },
  {
    $inc: {
      "computed.reviewCount": 1
    },
    $set: {
      "computed.lastUpdated": new Date()
    }
  }
)
```

### Spring Boot service example
```java
@Service
public class ProductRatingService {

    private final MongoTemplate mongoTemplate;

    public ProductRatingService(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    public void incrementReviewCount(String productId) {
        Query query = Query.query(Criteria.where("_id").is(productId));
        Update update = new Update()
                .inc("computed.reviewCount", 1)
                .set("computed.lastUpdated", Instant.now());

        mongoTemplate.updateFirst(query, update, "products");
    }
}
```

### Best use case
Dashboards, ratings, counters, leaderboards.

---

## 5.5 Extended reference pattern

Copy a few common fields from a referenced document.

### Example
```javascript
db.orders.insertOne({
  _id: "order_123",
  customerId: "customer_456",
  customerName: "John Doe",
  customerEmail: "john@example.com",
  total: 1057
})
```

### Why it helps
Most order screens can render without a join or `$lookup`.

---

## 6. Shard key selection

Your shard key is one of the most important long-term decisions.

### A good shard key should have
- high cardinality
- even distribution
- alignment with query patterns
- low mutability
- good write distribution

---

## 6.1 Hashed shard key

### Example
```javascript
sh.shardCollection("app.users", { userId: "hashed" })
```

### Good for
- point lookups
- evenly spreading writes
- monotonically increasing IDs

### Bad for
- range queries by shard key

### Interview answer
Use hashed keys when write distribution matters more than range locality.

---

## 6.2 Ranged shard key

### Example
```javascript
sh.shardCollection("app.orders", { orderDate: 1 })
```

### Good for
- range queries
- time-window reads

### Bad for
- hot shards if new writes all land in the newest range

---

## 6.3 Compound shard key

### Example
```javascript
sh.shardCollection("app.events", { tenantId: 1, createdAt: 1 })
```

### Better multi-tenant example
```javascript
sh.shardCollection("app.orders", { customerId: "hashed", orderDate: 1 })
```

### Why this often works well
- customer dimension distributes writes
- date field still helps ordered access within shard

### Anti-patterns
- low-cardinality fields like `status`
- mutable fields
- monotonically increasing-only keys without hashing
- keys not present in common queries

---

## 7. Indexing for performance

Indexes make MongoDB usable at scale.

### Key idea
Every useful index speeds up some reads and slows down every write.

---

## 7.1 Single-field index

### Example
```javascript
db.users.createIndex({ email: 1 }, { unique: true })
```

### Query
```javascript
db.users.findOne({ email: "john@example.com" })
```

### Spring Boot annotation
```java
import org.springframework.data.mongodb.core.index.Indexed;

@Document("users")
public class User {
    @Id
    private String id;

    @Indexed(unique = true)
    private String email;

    private String name;
}
```

---

## 7.2 Compound index

### Example
```javascript
db.orders.createIndex({ customerId: 1, orderDate: -1 })
```

### Query
```javascript
db.orders.find({ customerId: "c1" }).sort({ orderDate: -1 })
```

### Spring Boot compound index
```java
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;

@Document("orders")
@CompoundIndex(name = "customer_date_idx", def = "{'customerId': 1, 'orderDate': -1}")
public class OrderView {
    @Id
    private String id;
    private String customerId;
    private Instant orderDate;
}
```

### When to use
Filter + sort queries you run often.

---

## 7.3 ESR rule

For compound indexes:
- **E**quality first
- **S**ort second
- **R**ange last

### Query
```javascript
db.orders.find({
  status: "SHIPPED",
  total: { $gt: 100 }
}).sort({ orderDate: -1 })
```

### Good index
```javascript
db.orders.createIndex({
  status: 1,
  orderDate: -1,
  total: 1
})
```

---

## 7.4 Multikey index

Use for arrays.

### Example
```javascript
db.products.createIndex({ tags: 1 })
db.products.find({ tags: "mongodb" })
```

### Example document
```javascript
{
  name: "Book",
  tags: ["database", "mongodb", "backend"]
}
```

---

## 7.5 Text index

### Example
```javascript
db.articles.createIndex({ title: "text", content: "text" })

db.articles.find({
  $text: { $search: "mongodb performance" }
})
```

### Best use case
Basic built-in search without external search infrastructure.

---

## 7.6 TTL index

Useful for sessions, OTPs, temporary records.

### Example
```javascript
db.sessions.createIndex(
  { createdAt: 1 },
  { expireAfterSeconds: 3600 }
)
```

### Spring Boot example
```java
@Document("sessions")
public class SessionDocument {
    @Id
    private String id;
    private Instant createdAt;
    private String userId;
}
```

### Note
Spring Boot does not create TTL behavior just from a field. You still need the MongoDB TTL index.

---

## 7.7 Covered queries

A covered query is served from the index alone.

### Example
```javascript
db.users.createIndex({ name: 1, email: 1 })

db.users.find(
  { name: "John" },
  { _id: 0, name: 1, email: 1 }
)
```

### Why it matters
No document fetch, lower I/O, faster reads.

---

## 7.8 Explain plan

Always verify indexes with `explain`.

### Example
```javascript
db.orders.find({ customerId: "c1" }).sort({ orderDate: -1 }).explain("executionStats")
```

### What you want
- targeted index usage
- low docs examined
- low keys examined
- no collection scan unless intentional

---

## 8. Read concern, write concern, read preference

MongoDB lets you tune consistency.

---

## 8.1 Write concern

Controls how many nodes acknowledge a write.

### Examples
```javascript
db.orders.insertOne(
  { _id: "o1", total: 99 },
  { writeConcern: { w: 1 } }
)
```

```javascript
db.orders.insertOne(
  { _id: "o2", total: 199 },
  { writeConcern: { w: "majority", j: true } }
)
```

### Practical guidance
- `w:1` for lower latency, normal app flows
- `w:"majority"` for stronger durability
- add `j:true` when journaling durability matters

---

## 8.2 Read concern

Controls what kind of committed data you can read.

### Common levels
- `local`: fastest, default
- `majority`: only majority-committed data
- `linearizable`: strongest, slowest
- `snapshot`: transactions

### Java example
```java
MongoTemplate template = new MongoTemplate(mongoDatabaseFactory);
template.setReadConcern(com.mongodb.ReadConcern.MAJORITY);
```

---

## 8.3 Read preference

Controls where reads go.

### Common values
- `primary`
- `primaryPreferred`
- `secondary`
- `secondaryPreferred`
- `nearest`

### Connection string example
```yaml
spring:
  data:
    mongodb:
      uri: mongodb://localhost:27017/app?readPreference=secondaryPreferred
```

### Interview guidance
- primary for user-critical read-after-write consistency
- secondaryPreferred for dashboards, analytics, catalog browsing
- never route critical read-after-write flows to stale secondaries blindly

---

## 9. Transactions and consistency

MongoDB supports ACID transactions, but you should still design around **single-document atomicity first**.

---

## 9.1 Single-document atomicity

MongoDB guarantees atomic updates to one document.

### Example
```javascript
db.accounts.updateOne(
  { _id: "account_123", balance: { $gte: 100 } },
  {
    $inc: { balance: -100 },
    $push: {
      transactions: {
        type: "withdrawal",
        amount: 100,
        at: new Date()
      }
    }
  }
)
```

### Why this is great
One atomic write, no multi-document transaction needed.

### Spring Boot example
```java
@Service
public class AccountService {

    private final MongoTemplate mongoTemplate;

    public AccountService(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    public boolean withdraw(String accountId, int amount) {
        Query query = Query.query(
                Criteria.where("_id").is(accountId)
                        .and("balance").gte(amount)
        );

        Update update = new Update()
                .inc("balance", -amount)
                .push("transactions", new TransactionEntry("withdrawal", amount, Instant.now()));

        UpdateResult result = mongoTemplate.updateFirst(query, update, "accounts");
        return result.getModifiedCount() == 1;
    }
}

class TransactionEntry {
    private String type;
    private int amount;
    private Instant at;

    public TransactionEntry(String type, int amount, Instant at) {
        this.type = type;
        this.amount = amount;
        this.at = at;
    }
}
```

---

## 9.2 Multi-document transactions

Use only when truly necessary.

### Mongo shell / Node-style example
```javascript
const session = db.getMongo().startSession()
const accounts = session.getDatabase("app").accounts
const transfers = session.getDatabase("app").transfers

try {
  session.startTransaction({
    readConcern: { level: "snapshot" },
    writeConcern: { w: "majority" }
  })

  accounts.updateOne(
    { _id: "A" },
    { $inc: { balance: -500 } }
  )

  accounts.updateOne(
    { _id: "B" },
    { $inc: { balance: 500 } }
  )

  transfers.insertOne({
    from: "A",
    to: "B",
    amount: 500,
    at: new Date()
  })

  session.commitTransaction()
} catch (e) {
  session.abortTransaction()
  throw e
} finally {
  session.endSession()
}
```

### Spring Boot transaction configuration
```java
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.MongoDatabaseFactory;
import org.springframework.data.mongodb.MongoTransactionManager;

@Configuration
public class MongoTxConfig {

    @Bean
    MongoTransactionManager transactionManager(MongoDatabaseFactory factory) {
        return new MongoTransactionManager(factory);
    }
}
```

### Spring Boot transactional service
```java
@Service
public class TransferService {

    private final MongoTemplate mongoTemplate;

    public TransferService(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Transactional
    public void transfer(String fromId, String toId, int amount) {
        Query fromQuery = Query.query(Criteria.where("_id").is(fromId));
        Query toQuery = Query.query(Criteria.where("_id").is(toId));

        mongoTemplate.updateFirst(fromQuery, new Update().inc("balance", -amount), "accounts");
        mongoTemplate.updateFirst(toQuery, new Update().inc("balance", amount), "accounts");

        Document transfer = new Document()
                .append("from", fromId)
                .append("to", toId)
                .append("amount", amount)
                .append("createdAt", Instant.now());

        mongoTemplate.insert(transfer, "transfers");
    }
}
```

### Limitations to mention
- slower than single-document writes
- cross-shard transactions are more expensive
- long transactions increase contention
- avoid using transactions as a default design habit

---

## 10. Change streams

Use change streams for real-time reactions without polling.

### Good use cases
- notification triggers
- cache invalidation
- Elasticsearch sync
- audit trails
- event-driven workflows
- WebSocket dashboard updates

### Basic example
```javascript
const changeStream = db.orders.watch()

changeStream.on("change", (change) => {
  printjson(change)
})
```

### Filtered example
```javascript
const pipeline = [
  {
    $match: {
      operationType: "update",
      "fullDocument.status": "SHIPPED"
    }
  }
]

const changeStream = db.orders.watch(pipeline)
```

### Spring Boot example
```java
import org.bson.Document;
import org.springframework.data.mongodb.core.ChangeStreamEvent;
import org.springframework.data.mongodb.core.messaging.ChangeStreamRequest;
import org.springframework.data.mongodb.core.messaging.MessageListener;
import org.springframework.data.mongodb.core.messaging.Subscription;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;

@Component
public class OrderChangeListener {

    private final MongoTemplate mongoTemplate;

    public OrderChangeListener(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @PostConstruct
    public void listen() {
        MessageListener<ChangeStreamDocument<Document>, Order> listener = message -> {
            ChangeStreamEvent<Order> event = ChangeStreamEvent.of(message.getRaw(), Order.class, mongoTemplate.getConverter());
            System.out.println("Order changed: " + event.getBody());
        };

        ChangeStreamRequest<Order> request = ChangeStreamRequest.builder(listener)
                .collection("orders")
                .build();

        Subscription subscription = mongoTemplate.changeStream("app", request, Order.class);
    }
}
```

### Why it matters for scale
Polling does repeated wasted reads. Change streams are better for near-real-time systems.

---

## 11. Idempotency

Critical for retries in distributed systems.

### Pattern
Store a unique request ID.

### MongoDB example
```javascript
db.orders.createIndex({ requestId: 1 }, { unique: true })

db.orders.insertOne({
  requestId: "req_123",
  userId: "u1",
  total: 99
})
```

### Spring Boot model
```java
@Document("orders")
public class IdempotentOrder {
    @Id
    private String id;

    @Indexed(unique = true)
    private String requestId;

    private String userId;
    private BigDecimal total;
}
```

### Why it matters
If the client retries, the unique index prevents duplicate order creation.

---

## 12. Pagination

Never rely on large `skip()` at scale.

### Bad
```javascript
db.posts.find().sort({ _id: 1 }).skip(100000).limit(20)
```

### Good: cursor pagination
```javascript
db.posts.find({ _id: { $gt: ObjectId("65a000000000000000000000") } })
        .sort({ _id: 1 })
        .limit(20)
```

### Spring Boot example
```java
public List<Post> nextPage(String lastSeenId) {
    Query query = new Query();
    query.addCriteria(Criteria.where("_id").gt(lastSeenId));
    query.limit(20);
    query.with(Sort.by(Sort.Direction.ASC, "_id"));
    return mongoTemplate.find(query, Post.class, "posts");
}
```

### Why
Cursor-based pagination avoids scanning and discarding huge offsets.

---

## 13. A build path: from 100 RPS to 50K RPS

This is the practical progression you can discuss in interviews.

### Stage 1: 100 to 1K RPS
Keep it simple:
- one replica set
- good document design
- essential indexes only
- embed aggressively for main access paths
- use single-document atomic updates

### Stage 2: 1K to 5K RPS
Optimize reads and writes:
- add secondary reads for non-critical traffic
- add caching for hot keys
- add compound indexes for top query paths
- use projections to reduce payload size
- add idempotency keys for write safety

### Stage 3: 5K to 15K RPS
Reduce bottlenecks:
- split hot collections
- adopt computed pattern for expensive reads
- adopt bucket pattern for time-series/event data
- add background consumers using change streams
- watch slow queries with `explain`

### Stage 4: 15K to 50K RPS
Scale horizontally:
- shard the hottest collections
- choose shard key based on real query patterns
- separate critical reads from stale-tolerant reads
- avoid scatter-gather on common paths
- use async workflows for non-critical side effects
- keep transactions rare and short

### Supporting components
At higher RPS, MongoDB is usually part of a bigger stack:
- Redis for hot-cache and rate limiting
- Kafka/RabbitMQ for async workflows
- Elasticsearch/OpenSearch for advanced search
- CDN for static and public content
- observability for query latency, replication lag, and chunk imbalance

### Interview answer
MongoDB alone does not get you to 50K RPS. Good data modeling, caching, async architecture, targeted indexes, and eventually sharding do.

---

## 14. Spring Boot starter setup

### Maven dependencies
```xml
<dependencies>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-data-mongodb</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
    </dependency>
</dependencies>
```

### application.yml
```yaml
spring:
  data:
    mongodb:
      uri: mongodb://localhost:27017/app
server:
  port: 8080
```

### Main application
```java
@SpringBootApplication
public class MongoDemoApplication {
    public static void main(String[] args) {
        SpringApplication.run(MongoDemoApplication.class, args);
    }
}
```

### Simple controller
```java
@RestController
@RequestMapping("/orders")
public class OrderController {

    private final OrderRepository orderRepository;

    public OrderController(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @PostMapping
    public Order create(@RequestBody Order order) {
        return orderRepository.save(order);
    }

    @GetMapping("/{id}")
    public Order get(@PathVariable String id) {
        return orderRepository.findById(id).orElseThrow();
    }
}
```

---

## 15. MongoDB vs other databases

### MongoDB vs PostgreSQL
Choose MongoDB for:
- document-centric models
- schema flexibility
- easier horizontal scale

Choose PostgreSQL for:
- heavy relational joins
- strict integrity
- complex transactions
- powerful SQL analytics

### MongoDB vs DynamoDB
Choose MongoDB for:
- richer ad hoc queries
- aggregation pipeline
- more flexible retrieval patterns

Choose DynamoDB for:
- predictable key-based access
- serverless ops
- massive scale with minimal management

### MongoDB vs Cassandra
Choose MongoDB for:
- general-purpose document workloads
- richer query model

Choose Cassandra for:
- extreme write throughput
- always-on write availability
- partition-key-driven workloads

---

## 16. Final mental model

### What makes MongoDB good
- document-centric reads are fast
- schema changes are easy
- nested data is natural
- sharding is built in

### What makes MongoDB dangerous
- bad document modeling
- too many references
- poor shard key choice
- unbounded arrays
- assuming transactions are free
- relying on scatter-gather queries

### The core design rules
1. model around access patterns
2. embed by default, reference deliberately
3. index for real queries, not imagined ones
4. shard only when you need to
5. design for single-document atomicity first
6. use change streams and async workflows to reduce coupling

---

## 17. Quick interview recap

If asked, “How would you use MongoDB in a scalable design?” a strong short answer is:

> I would use MongoDB when the data is naturally document-shaped and the main access patterns are document reads rather than relational joins. I would embed bounded child data that is read together, use references only for shared or unbounded entities, create compound indexes around the highest-frequency queries, and use single-document atomic updates wherever possible. For higher scale, I would add caching, secondary reads for stale-tolerant traffic, computed fields for expensive reads, and shard only the hottest collections using a shard key chosen from real production query patterns.


---

## 18. Complete Spring Boot example you can build from scratch

This section turns the notes above into a small but realistic MongoDB-backed service.

### What this example includes
- Spring Boot + Spring Data MongoDB
- embedded order model
- repository + service + controller
- idempotent order creation
- single-document inventory decrement
- pagination
- compound indexes
- Mongo transaction example for transfer-like flows
- sample requests

### Project structure

```text
mongo-order-demo/
├─ pom.xml
├─ src/main/resources/
│  └─ application.yml
└─ src/main/java/com/example/mongo/
   ├─ MongoOrderDemoApplication.java
   ├─ config/
   │  └─ MongoConfig.java
   ├─ order/
   │  ├─ Order.java
   │  ├─ OrderItem.java
   │  ├─ Address.java
   │  ├─ CustomerSnapshot.java
   │  ├─ CreateOrderRequest.java
   │  ├─ OrderRepository.java
   │  ├─ OrderService.java
   │  └─ OrderController.java
   ├─ inventory/
   │  ├─ InventoryItem.java
   │  ├─ InventoryService.java
   │  └─ InventoryController.java
   └─ transfer/
      ├─ Account.java
      ├─ TransferService.java
      └─ TransferController.java
```

### `pom.xml`

```xml
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <groupId>com.example</groupId>
    <artifactId>mongo-order-demo</artifactId>
    <version>1.0.0</version>

    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>3.3.2</version>
    </parent>

    <properties>
        <java.version>17</java.version>
    </properties>

    <dependencies>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>

        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-data-mongodb</artifactId>
        </dependency>

        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-validation</artifactId>
        </dependency>
    </dependencies>
</project>
```

### `application.yml`

```yaml
spring:
  data:
    mongodb:
      uri: mongodb://localhost:27017/app

server:
  port: 8080

logging:
  level:
    org.springframework.data.mongodb.core.MongoTemplate: INFO
```

### Run Mongo locally

```bash
docker run -d --name mongo -p 27017:27017 mongo:7
```

### Main application

```java
package com.example.mongo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class MongoOrderDemoApplication {
    public static void main(String[] args) {
        SpringApplication.run(MongoOrderDemoApplication.class, args);
    }
}
```

### Transaction configuration

```java
package com.example.mongo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.MongoDatabaseFactory;
import org.springframework.data.mongodb.MongoTransactionManager;

@Configuration
public class MongoConfig {

    @Bean
    MongoTransactionManager transactionManager(MongoDatabaseFactory factory) {
        return new MongoTransactionManager(factory);
    }
}
```

### Order document model

```java
package com.example.mongo.order;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

@Document("orders")
@CompoundIndex(name = "user_date_idx", def = "{'userId': 1, 'createdAt': -1}")
public class Order {

    @Id
    private String id;

    @Indexed(unique = true)
    private String requestId;

    private String userId;
    private CustomerSnapshot customerSnapshot;
    private List<OrderItem> items;
    private Address shippingAddress;
    private BigDecimal total;
    private String status;
    private Instant createdAt;

    public Order() {
    }

    public String getId() {
        return id;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public CustomerSnapshot getCustomerSnapshot() {
        return customerSnapshot;
    }

    public void setCustomerSnapshot(CustomerSnapshot customerSnapshot) {
        this.customerSnapshot = customerSnapshot;
    }

    public List<OrderItem> getItems() {
        return items;
    }

    public void setItems(List<OrderItem> items) {
        this.items = items;
    }

    public Address getShippingAddress() {
        return shippingAddress;
    }

    public void setShippingAddress(Address shippingAddress) {
        this.shippingAddress = shippingAddress;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }
}
```

### Embedded classes

```java
package com.example.mongo.order;

import java.math.BigDecimal;

public class OrderItem {
    private String productId;
    private String name;
    private BigDecimal price;
    private int quantity;

    public OrderItem() {
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
```

```java
package com.example.mongo.order;

public class Address {
    private String city;
    private String zip;

    public Address() {
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getZip() {
        return zip;
    }

    public void setZip(String zip) {
        this.zip = zip;
    }
}
```

```java
package com.example.mongo.order;

public class CustomerSnapshot {
    private String name;
    private String email;

    public CustomerSnapshot() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
```

### Request DTO

```java
package com.example.mongo.order;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public class CreateOrderRequest {

    @NotBlank
    private String requestId;

    @NotBlank
    private String userId;

    private CustomerSnapshot customerSnapshot;

    @NotEmpty
    private List<OrderItem> items;

    private Address shippingAddress;

    public CreateOrderRequest() {
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public CustomerSnapshot getCustomerSnapshot() {
        return customerSnapshot;
    }

    public void setCustomerSnapshot(CustomerSnapshot customerSnapshot) {
        this.customerSnapshot = customerSnapshot;
    }

    public List<OrderItem> getItems() {
        return items;
    }

    public void setItems(List<OrderItem> items) {
        this.items = items;
    }

    public Address getShippingAddress() {
        return shippingAddress;
    }

    public void setShippingAddress(Address shippingAddress) {
        this.shippingAddress = shippingAddress;
    }
}
```

### Repository

```java
package com.example.mongo.order;

import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface OrderRepository extends MongoRepository<Order, String> {
    Optional<Order> findByRequestId(String requestId);
    List<Order> findByUserIdOrderByCreatedAtDesc(String userId, Pageable pageable);
}
```

### Order service

```java
package com.example.mongo.order;

import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

@Service
public class OrderService {

    private final OrderRepository orderRepository;

    public OrderService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    public Order create(CreateOrderRequest request) {
        Order order = new Order();
        order.setRequestId(request.getRequestId());
        order.setUserId(request.getUserId());
        order.setCustomerSnapshot(request.getCustomerSnapshot());
        order.setItems(request.getItems());
        order.setShippingAddress(request.getShippingAddress());
        order.setStatus("PLACED");
        order.setCreatedAt(Instant.now());
        order.setTotal(calculateTotal(request.getItems()));

        try {
            return orderRepository.save(order);
        } catch (DuplicateKeyException e) {
            return orderRepository.findByRequestId(request.getRequestId())
                    .orElseThrow(() -> e);
        }
    }

    public Order get(String id) {
        return orderRepository.findById(id).orElseThrow();
    }

    public List<Order> latestForUser(String userId, int limit) {
        return orderRepository.findByUserIdOrderByCreatedAtDesc(
                userId,
                PageRequest.of(0, limit)
        );
    }

    private BigDecimal calculateTotal(List<OrderItem> items) {
        return items.stream()
                .map(i -> i.getPrice().multiply(BigDecimal.valueOf(i.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
```

### Order controller

```java
package com.example.mongo.order;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    public Order create(@Valid @RequestBody CreateOrderRequest request) {
        return orderService.create(request);
    }

    @GetMapping("/{id}")
    public Order get(@PathVariable String id) {
        return orderService.get(id);
    }

    @GetMapping("/user/{userId}")
    public List<Order> byUser(
            @PathVariable String userId,
            @RequestParam(defaultValue = "10") int limit
    ) {
        return orderService.latestForUser(userId, limit);
    }
}
```

### Inventory document for atomic decrement

```java
package com.example.mongo.inventory;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document("inventory")
public class InventoryItem {

    @Id
    private String productId;

    private int available;

    public InventoryItem() {
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public int getAvailable() {
        return available;
    }

    public void setAvailable(int available) {
        this.available = available;
    }
}
```

### Inventory service using single-document atomicity

```java
package com.example.mongo.inventory;

import com.mongodb.client.result.UpdateResult;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

@Service
public class InventoryService {

    private final MongoTemplate mongoTemplate;

    public InventoryService(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    public boolean reserveOne(String productId) {
        Query query = Query.query(
                Criteria.where("_id").is(productId)
                        .and("available").gt(0)
        );

        Update update = new Update().inc("available", -1);
        UpdateResult result = mongoTemplate.updateFirst(query, update, InventoryItem.class);
        return result.getModifiedCount() == 1;
    }
}
```

### Inventory controller

```java
package com.example.mongo.inventory;

import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/inventory")
public class InventoryController {

    private final InventoryService inventoryService;

    public InventoryController(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    @PostMapping("/{productId}/reserve")
    public Map<String, Object> reserve(@PathVariable String productId) {
        boolean reserved = inventoryService.reserveOne(productId);
        return Map.of("productId", productId, "reserved", reserved);
    }
}
```

### Account document for transaction example

```java
package com.example.mongo.transfer;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document("accounts")
public class Account {

    @Id
    private String id;
    private int balance;

    public Account() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getBalance() {
        return balance;
    }

    public void setBalance(int balance) {
        this.balance = balance;
    }
}
```

### Transfer service with transaction

```java
package com.example.mongo.transfer;

import org.bson.Document;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
public class TransferService {

    private final MongoTemplate mongoTemplate;

    public TransferService(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Transactional
    public void transfer(String fromId, String toId, int amount) {
        Query fromQuery = Query.query(Criteria.where("_id").is(fromId).and("balance").gte(amount));
        Query toQuery = Query.query(Criteria.where("_id").is(toId));

        var fromResult = mongoTemplate.updateFirst(fromQuery, new Update().inc("balance", -amount), "accounts");
        if (fromResult.getModifiedCount() != 1) {
            throw new IllegalStateException("Insufficient funds");
        }

        mongoTemplate.updateFirst(toQuery, new Update().inc("balance", amount), "accounts");

        Document transfer = new Document()
                .append("from", fromId)
                .append("to", toId)
                .append("amount", amount)
                .append("createdAt", Instant.now());

        mongoTemplate.insert(transfer, "transfers");
    }
}
```

### Transfer controller

```java
package com.example.mongo.transfer;

import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/transfers")
public class TransferController {

    private final TransferService transferService;

    public TransferController(TransferService transferService) {
        this.transferService = transferService;
    }

    @PostMapping
    public Map<String, Object> transfer(
            @RequestParam String from,
            @RequestParam String to,
            @RequestParam int amount
    ) {
        transferService.transfer(from, to, amount);
        return Map.of("status", "ok", "from", from, "to", to, "amount", amount);
    }
}
```

### Seed data in Mongo shell

```javascript
use app

db.inventory.insertOne({ _id: "p1", available: 5 })

db.accounts.insertMany([
  { _id: "A", balance: 1000 },
  { _id: "B", balance: 500 }
])
```

### Test order creation

```bash
curl -X POST http://localhost:8080/orders \
  -H "Content-Type: application/json" \
  -d '{
    "requestId": "req-1001",
    "userId": "user-1",
    "customerSnapshot": {
      "name": "John Doe",
      "email": "john@example.com"
    },
    "items": [
      {
        "productId": "p1",
        "name": "Laptop",
        "price": 999,
        "quantity": 1
      },
      {
        "productId": "p2",
        "name": "Mouse",
        "price": 29,
        "quantity": 2
      }
    ],
    "shippingAddress": {
      "city": "San Francisco",
      "zip": "94102"
    }
  }'
```

### Test idempotency

Send the same request again with the same `requestId`.  
Because `requestId` is unique, the service returns the existing order instead of creating a duplicate.

### Test inventory reservation

```bash
curl -X POST http://localhost:8080/inventory/p1/reserve
```

### Test transfer

```bash
curl -X POST "http://localhost:8080/transfers?from=A&to=B&amount=100"
```

### What this example teaches

- **Embedding**: `items` and `customerSnapshot` live inside the order
- **Idempotency**: `requestId` prevents duplicate creates
- **Indexing**: compound index supports “latest orders per user”
- **Atomicity**: inventory decrement uses one conditional document update
- **Transactions**: account transfer uses a Mongo transaction only where needed
- **Scalability**: order reads stay fast because documents match access patterns

### What to add next in a real system

- request validation and exception handlers
- optimistic retry logic for transient transaction failures
- Redis cache for hot reads
- Kafka or RabbitMQ for async notifications
- change streams for order events
- pagination with cursor tokens instead of simple page size
- observability and slow-query monitoring
- sharding only after access patterns are proven
