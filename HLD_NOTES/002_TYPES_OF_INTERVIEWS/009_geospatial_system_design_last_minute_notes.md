# Geo-Spatial Data in System Design – Last-Minute Notes

> A concise but comprehensive guide to **storing, indexing, querying, and scaling location data** for systems like Uber, Yelp, Tinder, DoorDash, Airbnb, and Maps.

---

## Why Geo-Spatial Data Is Hard

### Core problem
A user opens a ride-sharing app.  
There are **100,000 active drivers** in the city.  
You need the **10 closest** in **<100 ms**.

Naive approach:
```text
100,000 distance calculations per request
× 1,000 requests/sec
= 100 million calculations/sec
```

That does not scale.

### Why traditional indexes fail
B-trees work well for:
- equality
- one-dimensional ranges

But location is **2D**:
- latitude
- longitude

A B-tree on latitude plus a B-tree on longitude still forces:
- broad scans
- large intersections
- wasted work

### Key challenge
```text
Nearby is not a natural 1D ordering.
Geo queries need special indexing strategies.
```

---

# 1) Why Location Data Is Challenging

## 1.1 The Earth is not flat
Distance on a sphere is different from distance on a plane.

### Flat approximation
```text
d = √[(x2-x1)^2 + (y2-y1)^2]
```

### Correct spherical distance
Use the **Haversine formula**:

```text
a = sin²(Δlat/2) + cos(lat1) × cos(lat2) × sin²(Δlon/2)
distance = 2 × R × arcsin(√a)
```

### Why this matters
- Haversine is accurate
- but slower than simple arithmetic
- doing it thousands of times per request is expensive

---

## 1.2 Traditional indexes do not work well
Two separate 1D indexes cannot efficiently answer:
```text
Find all drivers within 2km
```

You still have to:
- scan candidate sets
- intersect them
- run exact distance checks

---

## 1.3 Proximity queries are fundamentally different
Examples:
- `id = 123` -> direct lookup
- `age between 25 and 35` -> B-tree range
- `10 nearest drivers` -> no natural storage shortcut

---

## 1.4 Real-time updates make it harder
Example:
- 100K active drivers
- update every 4 seconds

```text
100,000 / 4 = 25,000 location updates/sec
```

The system must support:
- fast writes
- fast proximity reads
- constant index updates

---

# 2) Representing Locations

# 2.1 Latitude / Longitude
The most direct representation:

```text
lat = 40.7128
lon = -74.0060
```

### Precision guide

| Decimal Places | Precision | Example |
|---|---|---|
| 0 | 111 km | country |
| 1 | 11 km | city |
| 2 | 1.1 km | neighborhood |
| 3 | 110 m | street |
| 4 | 11 m | building |
| 5 | 1.1 m | person/tree |
| 6 | 11 cm | engineering |

### Rule of thumb
Use **5–6 decimals** for most apps.

### Pros
- universal
- exact
- easy to understand

### Cons
- 2D indexing problem remains
- distance math is expensive

---

# 2.2 Geohash
Geohash converts `(lat, lon)` into a string.

Example:
```text
NYC -> dr5ru6j2c4k8
SF  -> 9q8yyk8yuv27
```

### Key property
Nearby points usually share a prefix.

```text
dr5ru6j2c4k8
dr5ru6j8p2m1
```

Shared prefix means geographic proximity.

### Cell sizes

| Geohash Length | Cell Size | Use |
|---|---|---|
| 4 | 39 km | metro area |
| 5 | 4.9 km | district |
| 6 | 1.2 km | neighborhood |
| 7 | 153 m | block |
| 8 | 38 m | building |

### Why geohash is useful
You can use a **normal B-tree index** on the geohash string.

### Edge problem
Two very close points may fall into different neighboring cells.  
So you must query:
- center geohash
- plus **8 neighbor cells**

### Pros
- simple
- works in almost any DB
- easy to shard/partition by prefix

### Cons
- edge problem
- rectangular cells
- distortions near poles

---

# 2.3 H3
H3 is Uber’s hierarchical hexagonal index.

### Why hexagons
Hexagons have:
- 6 neighbors
- all roughly equal distance

Squares have:
- 4 edge neighbors
- 4 diagonal neighbors at larger distance

### Example resolutions

| H3 Resolution | Avg Edge Length | Use |
|---|---|---|
| 5 | 8 km | city |
| 7 | 1.2 km | neighborhood |
| 9 | 174 m | precise matching |
| 12 | 9 m | exact location |

### Pros
- better neighbor uniformity
- great for heatmaps, matching, surge zones
- hierarchical

### Cons
- more complex
- less universal tooling than geohash

### Best for
- Uber-style production geo systems
- supply/demand aggregation
- large-scale regional analysis

---

# 2.4 S2
Google’s S2 maps Earth to cells on a cube and encodes them into 64-bit IDs.

### Pros
- more uniform global coverage
- strong library support in some systems
- used in Google Maps and MongoDB internals

### Cons
- more conceptual complexity

### Rule
- **Geohash** = simplest general starting point
- **H3 / S2** = better for very large or advanced systems

---

# 3) Spatial Indexing Techniques

# 3.1 Quadtree
A quadtree recursively splits space into 4 quadrants.

### How it works
- start with one big region
- if a cell contains too many points -> split into 4
- repeat recursively

### Pros
- adapts to data density
- good for in-memory search
- intuitive

### Cons
- updates can be expensive
- memory-heavy
- less ideal for high-frequency writes

### Best for
- in-memory matching
- variable-density data
- custom real-time services

---

# 3.2 R-tree
R-trees group nearby objects into bounding rectangles.

### How it works
- leaf nodes store points/objects
- internal nodes store bounding boxes
- query descends only through intersecting boxes

### Pros
- good for spatial DBs
- supports polygons and complex shapes
- balanced tree

### Cons
- inserts/updates are more complex
- implementation complexity higher

### Best for
- PostGIS
- spatial databases
- polygon and containment queries

---

# 3.3 Geohash + B-tree
A practical and simple approach:
- compute geohash
- store it as string
- add B-tree index
- query prefix + neighbor cells

### Pros
- works in standard DBs
- easy to implement
- easy to shard by prefix

### Cons
- query 9 cells for safety
- fixed cell sizing
- less optimal than specialized indexes

### Rule
```text
Start with geohash-based indexing unless requirements truly demand more.
```

---

# 4) Database Options

# 4.1 PostgreSQL + PostGIS
Best choice for:
- rich geo functions
- polygon queries
- strong consistency
- SQL + spatial analysis

### Example
```sql
CREATE TABLE restaurants (
    id SERIAL PRIMARY KEY,
    name VARCHAR(255),
    location GEOGRAPHY(POINT, 4326)
);

CREATE INDEX idx_restaurants_location
ON restaurants USING GIST (location);

SELECT name
FROM restaurants
WHERE ST_DWithin(
    location,
    ST_SetSRID(ST_MakePoint(-74.006, 40.712), 4326),
    1000
);
```

### Pros
- rich spatial functions
- nearest-neighbor support
- polygons, intersections, containment
- SQL + ACID

### Cons
- usually single-node unless you add sharding

---

# 4.2 Redis Geo
Best for:
- real-time proximity
- very high update rate
- driver matching

### Example
```redis
GEOADD drivers -74.006 40.712 "driver:1"
GEOADD drivers -74.010 40.720 "driver:2"
GEORADIUS drivers -74.006 40.712 2 km WITHDIST
```

### Pros
- extremely fast
- good for live matching
- simple commands

### Cons
- simple queries only
- memory-bound
- not for complex polygons/analytics

---

# 4.3 MongoDB
Best for:
- document data + geo
- flexible schema
- horizontal scaling

### Example
```javascript
db.places.createIndex({ location: "2dsphere" });

db.places.find({
  location: {
    $near: {
      $geometry: {
        type: "Point",
        coordinates: [-73.985, 40.758]
      },
      $maxDistance: 1000
    }
  }
});
```

### Pros
- easy scaling
- good document model
- built-in geo support

### Cons
- fewer advanced spatial functions than PostGIS

---

# 4.4 Elasticsearch
Best for:
- text + location search together
- search relevance + geo filters
- faceted search

### Example
```json
{
  "query": {
    "geo_distance": {
      "distance": "1km",
      "location": {
        "lat": 40.712,
        "lon": -74.006
      }
    }
  }
}
```

### Pros
- combine text search + geo
- great for “Italian restaurants near me”

### Cons
- eventual consistency
- not your source of truth

---

## Database selection cheat sheet

| Database | Best For | Limitation |
|---|---|---|
| PostGIS | rich geo analysis | single-node by default |
| Redis Geo | real-time matching | memory-bound, simple queries |
| MongoDB | flexible schema + geo | fewer geo features |
| Elasticsearch | search + location | eventual consistency |

---

# 5) Core Query Patterns

# 5.1 K-Nearest Neighbors (KNN)
Find the closest N results.

Example:
- nearest 10 drivers
- nearest 20 restaurants

### SQL idea
```sql
SELECT name
FROM restaurants
ORDER BY location <-> ST_SetSRID(ST_MakePoint(-74.006, 40.712), 4326)
LIMIT 10;
```

---

# 5.2 Radius search
Find all points within X distance.

Example:
- drivers within 2 km
- shops within 1 km

### Redis example
```python
redis.georadius("drivers", lon, lat, 2, unit="km", withdist=True)
```

---

# 5.3 Bounding box search
Find all points in current map viewport.

Example:
- listings visible on screen

### MongoDB example
```javascript
db.listings.find({
  location: {
    $geoWithin: {
      $box: [
        [-74.1, 40.7],
        [-73.9, 40.8]
      ]
    }
  }
});
```

---

# 5.4 Polygon search
Find all points inside irregular boundary.

Example:
- homes inside Brooklyn polygon
- restaurants inside delivery zone

### PostGIS example
```sql
SELECT *
FROM properties
WHERE ST_Contains(
    ST_GeomFromGeoJSON('{"type":"Polygon","coordinates":[[[...]]]}'),
    location
);
```

---

# 5.5 Distance calculation
Sometimes you just need precise distance between two points.

### Python Haversine
```python
import math

def haversine(lat1, lon1, lat2, lon2):
    R = 6371
    dlat = math.radians(lat2 - lat1)
    dlon = math.radians(lon2 - lon1)

    a = (math.sin(dlat / 2) ** 2 +
         math.cos(math.radians(lat1)) *
         math.cos(math.radians(lat2)) *
         math.sin(dlon / 2) ** 2)

    c = 2 * math.asin(math.sqrt(a))
    return R * c
```

---

# 6) Scaling Strategies

# 6.1 Geographic sharding
Shard by region/city.

```text
US-East shard
US-West shard
Europe shard
Asia shard
```

### Pros
- most queries stay local
- natural partitioning

### Cons
- uneven load (NYC vs Montana)
- cross-region queries harder

---

# 6.2 Geohash-based partitioning
Shard by geohash prefix.

### Pros
- flexible partitioning
- easy split/merge
- natural routing

### Cons
- hotspot prefixes in dense areas

---

# 6.3 Hybrid architecture
Use different DBs for different needs.

Example:
- **Redis Geo** -> live driver positions
- **PostGIS** -> history + analytics
- **Elasticsearch** -> text + geo search

### Best for
Systems with:
- real-time matching
- history queries
- search use cases

---

# 6.4 Cache hot regions
Popular areas get the most queries.

Example:
- Manhattan: 10K req/sec
- rural area: 10 req/sec

### Idea
Cache nearby search results per rounded grid cell.

### Example
```python
def find_nearby_cached(lat, lon, radius):
    cell = f"{round(lat, 2)}:{round(lon, 2)}"
    cached = cache.get(f"nearby:{cell}")
    if cached:
        return filter_by_distance(cached, lat, lon, radius)

    results = db.find_nearby(lat, lon, radius * 1.5)
    cache.set(f"nearby:{cell}", results, ttl=60)
    return filter_by_distance(results, lat, lon, radius)
```

---

# 6.5 In-memory quadtree
For ultra-low-latency real-time matching, keep active entities in memory.

### Best for
- active driver/service matching
- one city / one region per service instance
- microsecond-level matching

### Trade-off
- rebuild on restart
- needs async persistence
- memory management complexity

---

# 7) Uber-Style Driver Matching Architecture

## Requirements
- 500K active drivers
- location updates every 4 sec
- 100K+ ride requests/min peak
- <100 ms nearest driver match

---

## Architecture
```text
Driver App
-> Load Balancer
-> Location Service
-> Redis Geo Cluster (real-time positions)
-> Match Service
-> Rider App

History/analytics -> PostgreSQL/PostGIS
```

---

## Location service flow
Each driver update:
1. update Redis geo index
2. update metadata hash
3. publish event for tracking systems

### Example
```python
def update_driver_location(driver_id, lat, lon, timestamp):
    redis.geoadd("drivers:available", lon, lat, driver_id)

    redis.hset(f"driver:{driver_id}", {
        "lat": lat,
        "lon": lon,
        "updated_at": timestamp,
        "status": "available"
    })

    redis.publish("driver_locations", {
        "driver_id": driver_id,
        "lat": lat,
        "lon": lon
    })
```

---

## Match service flow
1. radius query for nearby candidates
2. fetch driver metadata
3. filter by vehicle type / availability
4. rank by ETA + driver rating
5. return top 10

### Example
```python
def find_drivers_for_ride(rider_lat, rider_lon, vehicle_type):
    nearby = redis.georadius(
        "drivers:available",
        rider_lon, rider_lat,
        2, unit="km",
        withdist=True,
        withcoord=True,
        count=50
    )

    candidates = []
    for driver_id, distance, coords in nearby:
        driver_info = redis.hgetall(f"driver:{driver_id}")
        if driver_info["vehicle_type"] == vehicle_type:
            candidates.append({
                "driver_id": driver_id,
                "distance": distance,
                "rating": driver_info["rating"],
                "eta": estimate_eta(coords, (rider_lat, rider_lon))
            })

    return sorted(candidates, key=lambda d: (d["eta"], -d["rating"]))[:10]
```

---

## Optimizations
- TTL on active drivers (e.g. expire after 10 sec)
- connection pooling
- circuit breaker if Redis slows down
- routing zones / geofencing
- H3 heatmaps for surge pricing and supply guidance

---

# 8) Common Mistakes

## 1. Latitude / Longitude order bugs
Different systems use different order:
- many APIs -> `(lat, lon)`
- GeoJSON / MongoDB / Redis -> `(lon, lat)`

### Rule
Always verify with a known point.

---

## 2. Ignoring Earth curvature
1 degree longitude is not constant across latitudes.

### Rule
Use proper spherical calculations or geo libraries.

---

## 3. Forgetting geohash edge neighbors
If using geohash:
```text
query center cell + 8 neighbors
```

---

## 4. Over-engineering too early
For modest scale:
- PostGIS or MongoDB may be enough

Do not jump to custom sharding/H3 too early.

---

## 5. Ignoring update frequency
An index that works well for static places may fail under:
- 25K updates/sec
- moving drivers/users/devices

---

# 9) Spring Boot Examples – Step by Step

Below are practical Spring Boot examples for the main approaches.

---

# 9.1 Technique A: PostgreSQL + PostGIS

## Step 1: Add dependencies
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
        <groupId>org.postgresql</groupId>
        <artifactId>postgresql</artifactId>
    </dependency>
</dependencies>
```

## Step 2: Create table in Postgres/PostGIS
```sql
CREATE EXTENSION IF NOT EXISTS postgis;

CREATE TABLE drivers (
    id UUID PRIMARY KEY,
    name TEXT,
    status TEXT,
    location GEOGRAPHY(POINT, 4326)
);

CREATE INDEX idx_drivers_location
ON drivers USING GIST (location);
```

## Step 3: JPA entity
```java
import jakarta.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "drivers")
public class DriverEntity {

    @Id
    private UUID id;

    private String name;
    private String status;

    @Column(columnDefinition = "GEOGRAPHY(POINT,4326)")
    private String location;

    // getters/setters
}
```

## Step 4: Repository with native nearby query
```java
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface DriverRepository extends JpaRepository<DriverEntity, UUID> {

    @Query(value = """
        SELECT *
        FROM drivers
        WHERE status = 'available'
          AND ST_DWithin(
                location,
                ST_SetSRID(ST_MakePoint(:lon, :lat), 4326)::geography,
                :radiusMeters
          )
        ORDER BY location <-> ST_SetSRID(ST_MakePoint(:lon, :lat), 4326)::geography
        LIMIT :limit
        """, nativeQuery = true)
    List<DriverEntity> findNearby(
            @Param("lat") double lat,
            @Param("lon") double lon,
            @Param("radiusMeters") double radiusMeters,
            @Param("limit") int limit
    );
}
```

## Step 5: Service
```java
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class DriverSearchService {

    private final DriverRepository repository;

    public DriverSearchService(DriverRepository repository) {
        this.repository = repository;
    }

    public List<DriverEntity> findNearest(double lat, double lon) {
        return repository.findNearby(lat, lon, 2000, 10);
    }
}
```

## Step 6: Controller
```java
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/postgis/drivers")
public class DriverSearchController {

    private final DriverSearchService service;

    public DriverSearchController(DriverSearchService service) {
        this.service = service;
    }

    @GetMapping("/nearby")
    public List<DriverEntity> nearby(
            @RequestParam double lat,
            @RequestParam double lon
    ) {
        return service.findNearest(lat, lon);
    }
}
```

---

# 9.2 Technique B: Redis Geo for real-time matching

## Step 1: Add dependency
```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-redis</artifactId>
</dependency>
```

## Step 2: Redis config
```java
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;

@Configuration
public class RedisConfig {

    @Bean
    public LettuceConnectionFactory redisConnectionFactory() {
        return new LettuceConnectionFactory("localhost", 6379);
    }

    @Bean
    public RedisTemplate<String, Object> redisTemplate(LettuceConnectionFactory cf) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(cf);
        return template;
    }
}
```

## Step 3: Geo service
```java
import org.springframework.data.geo.*;
import org.springframework.data.redis.connection.RedisGeoCommands;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class RedisGeoDriverService {

    private static final String KEY = "drivers:available";
    private final RedisTemplate<String, Object> redisTemplate;

    public RedisGeoDriverService(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public void updateDriverLocation(String driverId, double lat, double lon) {
        redisTemplate.opsForGeo().add(KEY, new Point(lon, lat), driverId);
    }

    public GeoResults<RedisGeoCommands.GeoLocation<Object>> findNearby(double lat, double lon, double radiusKm) {
        Circle area = new Circle(new Point(lon, lat), new Distance(radiusKm, Metrics.KILOMETERS));
        RedisGeoCommands.GeoRadiusCommandArgs args =
                RedisGeoCommands.GeoRadiusCommandArgs.newGeoRadiusArgs()
                        .includeDistance()
                        .sortAscending()
                        .limit(10);

        return redisTemplate.opsForGeo().radius(KEY, area, args);
    }
}
```

## Step 4: Controller
```java
import org.springframework.data.redis.connection.RedisGeoCommands;
import org.springframework.data.geo.GeoResults;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/redis/drivers")
public class RedisGeoDriverController {

    private final RedisGeoDriverService service;

    public RedisGeoDriverController(RedisGeoDriverService service) {
        this.service = service;
    }

    @PostMapping("/{driverId}/location")
    public void updateLocation(
            @PathVariable String driverId,
            @RequestParam double lat,
            @RequestParam double lon
    ) {
        service.updateDriverLocation(driverId, lat, lon);
    }

    @GetMapping("/nearby")
    public GeoResults<RedisGeoCommands.GeoLocation<Object>> nearby(
            @RequestParam double lat,
            @RequestParam double lon,
            @RequestParam(defaultValue = "2") double radiusKm
    ) {
        return service.findNearby(lat, lon, radiusKm);
    }
}
```

---

# 9.3 Technique C: Geohash + standard DB

## Step 1: Add geohash library
Use any Java geohash library, e.g. `ch.hsr:geohash`.

## Step 2: Table
```sql
CREATE TABLE places (
    id UUID PRIMARY KEY,
    name TEXT,
    lat DOUBLE PRECISION,
    lon DOUBLE PRECISION,
    geohash VARCHAR(20)
);

CREATE INDEX idx_places_geohash ON places(geohash);
```

## Step 3: Entity
```java
import jakarta.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "places")
public class PlaceEntity {
    @Id
    private UUID id;
    private String name;
    private double lat;
    private double lon;
    private String geohash;
}
```

## Step 4: Repository
```java
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface PlaceRepository extends JpaRepository<PlaceEntity, UUID> {

    @Query("SELECT p FROM PlaceEntity p WHERE p.geohash LIKE CONCAT(:prefix, '%')")
    List<PlaceEntity> findByGeohashPrefix(@Param("prefix") String prefix);
}
```

## Step 5: Service
```java
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;

@Service
public class GeohashSearchService {

    private final PlaceRepository repository;

    public GeohashSearchService(PlaceRepository repository) {
        this.repository = repository;
    }

    public List<PlaceEntity> nearby(String centerPrefix, List<String> neighborPrefixes) {
        List<PlaceEntity> results = new ArrayList<>();
        results.addAll(repository.findByGeohashPrefix(centerPrefix));
        for (String prefix : neighborPrefixes) {
            results.addAll(repository.findByGeohashPrefix(prefix));
        }
        return results;
    }
}
```

### Important
In production:
- compute center geohash
- compute 8 neighbors
- fetch all 9 prefixes
- filter precisely with Haversine distance afterward

---

# 9.4 Technique D: MongoDB 2dsphere

## Step 1: Dependency
```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-mongodb</artifactId>
</dependency>
```

## Step 2: Document
```java
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
import org.springframework.data.mongodb.core.index.GeoSpatialIndexed;
import org.springframework.data.mongodb.core.index.GeoSpatialIndexType;
import org.springframework.data.mongodb.core.mapping.Document;

@Document("places")
public class PlaceDocument {

    @Id
    private String id;
    private String name;

    @GeoSpatialIndexed(type = GeoSpatialIndexType.GEO_2DSPHERE)
    private GeoJsonPoint location;

    // getters/setters
}
```

## Step 3: Repository
```java
import org.springframework.data.mongodb.repository.MongoRepository;

public interface PlaceMongoRepository extends MongoRepository<PlaceDocument, String> {
}
```

## Step 4: Service using MongoTemplate
```java
import org.springframework.data.geo.Distance;
import org.springframework.data.geo.Metrics;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.NearQuery;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MongoGeoService {

    private final MongoTemplate mongoTemplate;

    public MongoGeoService(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    public List<PlaceDocument> findNearby(double lat, double lon, double maxDistanceMeters) {
        NearQuery query = NearQuery.near(lon, lat)
                .maxDistance(new Distance(maxDistanceMeters / 1000.0, Metrics.KILOMETERS));

        return mongoTemplate.geoNear(query, PlaceDocument.class)
                .getContent()
                .stream()
                .map(hit -> hit.getContent())
                .toList();
    }
}
```

---

# 9.5 Technique E: Elasticsearch geo search

## Step 1: Dependency
Use Spring Data Elasticsearch starter if needed.

## Step 2: Document
```java
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.GeoPointField;
import org.springframework.data.elasticsearch.core.geo.GeoPoint;

@Document(indexName = "places")
public class PlaceSearchDocument {

    @Id
    private String id;
    private String name;

    @GeoPointField
    private GeoPoint location;
}
```

## Step 3: Service using Elasticsearch client
```java
@Service
public class ElasticsearchGeoService {

    public String geoDistanceQuery(double lat, double lon) {
        return """
        {
          "query": {
            "geo_distance": {
              "distance": "1km",
              "location": {
                "lat": %s,
                "lon": %s
              }
            }
          },
          "sort": [
            {
              "_geo_distance": {
                "location": {
                  "lat": %s,
                  "lon": %s
                },
                "order": "asc"
              }
            }
          ]
        }
        """.formatted(lat, lon, lat, lon);
    }
}
```

### Best use
Combine with:
- text relevance
- filters
- ratings/open-now/category search

---

# 10) Practical Recommendation by Use Case

## Case 1: Nearby businesses / restaurants
Start with:
```text
PostGIS or MongoDB
```

## Case 2: Ride-sharing / delivery matching
Use:
```text
Redis Geo for live positions
+ PostGIS for history/analytics
```

## Case 3: Search “pizza near me”
Use:
```text
Elasticsearch + primary DB
```

## Case 4: Global scale / heatmaps / surge pricing
Use:
```text
H3 or S2 + geographic sharding
```

---

# 11) Interview Answer Template

```text
For geo-spatial systems, the main challenge is that proximity queries are inherently two-dimensional, so traditional B-tree indexes are not enough.

If the workload is mostly nearby/radius search at moderate scale, I’d start with PostGIS or MongoDB using geo indexes.
If I need very fast real-time matching with frequent updates, like Uber drivers updating every few seconds, I’d use Redis Geo for the hot path and store history in PostgreSQL/PostGIS.

If the use case mixes text and location, such as “Italian restaurants near me,” I’d add Elasticsearch.
At larger scale, I’d shard geographically or by geohash/H3 cells so queries stay local.

I’d also be careful about coordinate order, Earth curvature, geohash edge cells, and update frequency because these are common failure points in geo systems.
```

---

# 12) Polished Key Takeaways

- **Location queries are not normal queries**  
  “Nearest” and “within radius” require spatial indexing, not just ordinary B-trees.

- **Latitude/longitude is simple but not enough by itself**  
  You still need geo-aware indexing and careful distance calculations.

- **Geohash is the simplest practical starting point**  
  It converts 2D location into a 1D key you can index with a normal database.

- **H3 and S2 are better for advanced large-scale systems**  
  They support more uniform global cells and better large-scale aggregation.

- **Choose the database by query pattern**  
  PostGIS for rich spatial logic, Redis Geo for real-time matching, MongoDB for flexible documents, Elasticsearch for search + location.

- **Real-time systems need different architecture from analytical systems**  
  Live driver matching and historical trip analysis should not share the same storage path.

- **Geographic locality is your natural sharding advantage**  
  Most geo queries are local, so geographic partitioning works well.

- **Start simple, then evolve**  
  PostGIS or MongoDB handles more scale than people expect; only add Redis/H3/sharding when needed.

- **The most common bugs are not algorithmic**  
  They come from lat/lon order mistakes, ignoring Earth curvature, and forgetting cell edge cases.

---

## Final 1-Line Shortcut
```text
Store coordinates -> index spatially -> query by radius/KNN -> shard by geography when needed
```
