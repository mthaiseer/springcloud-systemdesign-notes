# 17. Proximity Service — Visual System Design Notes

> Goal: design a service like Yelp / Google Maps nearby search that returns nearby businesses for a user location and radius.

---

## 1. Problem Scope

### Core features

```text
┌──────────────────────────────────────────────┐
│              Proximity Service               │
├──────────────────────────────────────────────┤
│  1. Search nearby businesses                 │
│  2. View business details                    │
│  3. Add / update / delete business info      │
└──────────────────────────────────────────────┘
```

### Requirements

```text
┌───────────────────────┐      ┌────────────────────────┐
│ Functional Requirements│      │ Non-Functional Req.     │
├───────────────────────┤      ├────────────────────────┤
│ Return nearby places  │      │ Low latency             │
│ Radius-based search   │      │ High availability       │
│ Business CRUD         │      │ Scalable reads          │
│ View business detail  │      │ Privacy-aware location  │
└───────────────────────┘      └────────────────────────┘
```

### Assumptions

| Item | Assumption |
|---|---:|
| Daily active users | 100M |
| Businesses | 200M |
| Searches per user/day | 5 |
| Search QPS | ~5,000 |
| Max radius | 20 km |
| Business updates | Effective next day |

---

## 2. API Design

### Search nearby businesses

```http
GET /v1/search/nearby?latitude=37.776720&longitude=-122.416730&radius=500
```

Response:

```json
{
  "total": 10,
  "businesses": [
    {
      "businessId": 101,
      "name": "Taco Place",
      "latitude": 37.7768,
      "longitude": -122.4169,
      "distanceMeters": 36
    }
  ]
}
```

### Business APIs

```text
┌──────────────────────────────┬───────────────────────────────┐
│ API                          │ Purpose                       │
├──────────────────────────────┼───────────────────────────────┤
│ GET /v1/businesses/{id}      │ Get business detail           │
│ POST /v1/businesses          │ Add business                  │
│ PUT /v1/businesses/{id}      │ Update business               │
│ DELETE /v1/businesses/{id}   │ Delete business               │
└──────────────────────────────┴───────────────────────────────┘
```

---

## 3. High-Level Architecture

```text
                         ┌─────────────────┐
                         │      User       │
                         │ Web / Mobile App│
                         └────────┬────────┘
                                  │
                                  ▼
                         ┌─────────────────┐
                         │  Load Balancer  │
                         └───────┬─────────┘
                                 │
              ┌──────────────────┴──────────────────┐
              ▼                                     ▼
┌──────────────────────────┐          ┌──────────────────────────┐
│ Location-Based Service   │          │ Business Service          │
│ /search/nearby           │          │ /businesses/{id}          │
│ Read-heavy, stateless    │          │ Read + low-volume writes  │
└─────────────┬────────────┘          └─────────────┬────────────┘
              │                                     │
              ▼                                     ▼
┌──────────────────────────┐          ┌──────────────────────────┐
│ Redis Cache              │          │ Database Cluster          │
├──────────────────────────┤          ├──────────────────────────┤
│ geohash -> business IDs  │          │ Primary: writes           │
│ businessId -> business   │          │ Replicas: reads           │
└──────────────────────────┘          └──────────────────────────┘
```

### Why split LBS and Business Service?

```text
┌─────────────────────────────┐
│ LBS                         │
├─────────────────────────────┤
│ Search nearby places        │
│ Very high QPS               │
│ Read-only                   │
│ Easy horizontal scaling     │
└─────────────────────────────┘

┌─────────────────────────────┐
│ Business Service            │
├─────────────────────────────┤
│ Business details            │
│ Add / update / delete       │
│ Writes go to primary DB     │
│ Reads use cache / replicas  │
└─────────────────────────────┘
```

---

## 4. Why Simple Latitude/Longitude Search Is Bad

Naive SQL:

```sql
SELECT business_id, latitude, longitude
FROM business
WHERE latitude BETWEEN :lat - :radius AND :lat + :radius
  AND longitude BETWEEN :lng - :radius AND :lng + :radius;
```

Problem:

```text
Latitude index gives many rows
Longitude index gives many rows
Then DB must intersect them

┌──────────────────────┐       ┌──────────────────────┐
│ Latitude candidate   │       │ Longitude candidate  │
│ set can be huge      │       │ set can be huge      │
└──────────┬───────────┘       └──────────┬───────────┘
           └──────────────┬───────────────┘
                          ▼
                ┌──────────────────┐
                │ Expensive filter │
                └──────────────────┘
```

Better idea: map 2D location into a 1D index.

---

## 5. Geospatial Index Choices

```text
┌───────────────────────────────┐
│      Geospatial Indexes       │
└───────────────┬───────────────┘
                │
       ┌────────┴────────┐
       ▼                 ▼
┌─────────────┐   ┌─────────────┐
│ Hash-based  │   │ Tree-based  │
├─────────────┤   ├─────────────┤
│ Even grid   │   │ Quadtree    │
│ Geohash     │   │ Google S2   │
│ Cartesian   │   │ R-tree      │
└─────────────┘   └─────────────┘
```

Recommended for interviews:

```text
┌──────────────┬──────────────────────────────────────┐
│ Option       │ Good interview choice?               │
├──────────────┼──────────────────────────────────────┤
│ Geohash      │ Yes. Simple and practical.           │
│ Quadtree     │ Yes. Good for k-nearest search.      │
│ Google S2    │ Mention, but internals are complex.  │
└──────────────┴──────────────────────────────────────┘
```

---

## 6. Geohash Visual Explanation

Geohash recursively divides the world into smaller cells.

```text
World
┌───────────────────────────────┐
│              North            │
│        ┌────────┬────────┐    │
│        │   01   │   11   │    │
│        ├────────┼────────┤    │
│        │   00   │   10   │    │
│        └────────┴────────┘    │
│              South            │
└───────────────────────────────┘
```

More characters = smaller area.

```text
9        → huge region
9q       → smaller region
9q8      → city-size-ish region
9q8zn    → neighborhood-ish region
9q8znf   → small cell
```

### Radius to geohash precision

| Radius | Geohash length |
|---:|---:|
| 0.5 km | 6 |
| 1 km | 5 |
| 2 km | 5 |
| 5 km | 4 |
| 20 km | 4 |

### Boundary problem

A user can be near a geohash border. Searching only the current cell may miss nearby places.

```text
┌───────────┬───────────┬───────────┐
│ Neighbor  │ Neighbor  │ Neighbor  │
├───────────┼───────────┼───────────┤
│ Neighbor  │ User Cell │ Neighbor  │
├───────────┼───────────┼───────────┤
│ Neighbor  │ Neighbor  │ Neighbor  │
└───────────┴───────────┴───────────┘
```

Solution: search current geohash + 8 neighbors.

---

## 7. Nearby Search Flow With Geohash

```text
┌──────────────┐
│ User Request │
│ lat,lng,r    │
└──────┬───────┘
       ▼
┌──────────────────────────────┐
│ Choose geohash precision     │
│ Example: 500m -> length 6    │
└──────┬───────────────────────┘
       ▼
┌──────────────────────────────┐
│ Calculate current geohash    │
│ + 8 neighboring geohashes    │
└──────┬───────────────────────┘
       ▼
┌──────────────────────────────┐
│ Fetch business IDs from      │
│ geohash cache / index table  │
└──────┬───────────────────────┘
       ▼
┌──────────────────────────────┐
│ Fetch business objects       │
│ from business cache / DB     │
└──────┬───────────────────────┘
       ▼
┌──────────────────────────────┐
│ Calculate exact distance     │
│ Filter within radius         │
│ Rank nearest first           │
└──────┬───────────────────────┘
       ▼
┌──────────────────────────────┐
│ Return paginated results     │
└──────────────────────────────┘
```

---

## 8. Data Model

### Business table

```text
┌─────────────────────────────┐
│ business                    │
├─────────────────────────────┤
│ business_id       PK        │
│ name                        │
│ address                     │
│ city                        │
│ state                       │
│ country                     │
│ latitude                    │
│ longitude                   │
│ category                    │
│ opening_hours               │
│ created_at                  │
│ updated_at                  │
└─────────────────────────────┘
```

### Geospatial index table

Recommended option: one row per `(geohash, business_id)`.

```text
┌─────────────────────────────┐
│ geospatial_index            │
├─────────────────────────────┤
│ geohash          PK part    │
│ business_id      PK part    │
└─────────────────────────────┘
```

Example:

| geohash | business_id |
|---|---:|
| 9q8znf | 101 |
| 9q8znf | 102 |
| 9q8znd | 201 |

Why this is better than storing a JSON array:

```text
┌──────────────────────────────┐
│ JSON array per geohash       │
├──────────────────────────────┤
│ Harder insert/delete         │
│ Need row lock                │
│ Need duplicate scan          │
└──────────────────────────────┘

┌──────────────────────────────┐
│ One row per business         │
├──────────────────────────────┤
│ Easy insert/delete           │
│ Compound key prevents dupes  │
│ Cleaner scaling              │
└──────────────────────────────┘
```

---

## 9. Cache Design

### Cache keys

Do not use raw latitude/longitude as cache key because GPS coordinates can change slightly.

Use geohash instead.

```text
Bad cache key:
  37.776720:-122.416730:500
  37.776721:-122.416729:500
  These may represent almost the same place but create different keys.

Good cache key:
  geohash:9q8znf
```

### Cache layers

```text
┌─────────────────────────────────────────────┐
│ Redis Cluster                               │
├─────────────────────────────────────────────┤
│ geohash -> List<businessId>                 │
│ businessId -> Business object               │
└─────────────────────────────────────────────┘
```

### Cache read flow

```text
┌──────────────┐
│ LBS Service  │
└──────┬───────┘
       ▼
┌─────────────────────┐
│ Redis geohash cache │
└──────┬──────────────┘
       │ hit
       ▼
┌─────────────────────┐
│ business IDs        │
└─────────────────────┘

       │ miss
       ▼
┌─────────────────────┐
│ DB read replica     │
└──────┬──────────────┘
       ▼
┌─────────────────────┐
│ Fill Redis cache    │
└─────────────────────┘
```

---

## 10. Java Reference Code

### Business model

```java
public record Business(
        long businessId,
        String name,
        double latitude,
        double longitude,
        String category
) {}
```

### Radius to geohash precision

```java
public class GeoPrecision {

    public static int geohashLengthForRadiusMeters(int radiusMeters) {
        if (radiusMeters <= 500) return 6;
        if (radiusMeters <= 2_000) return 5;
        return 4; // good enough for 5km to 20km in this simplified design
    }
}
```

### Haversine distance

Used to calculate exact distance after fetching candidates from geohash cells.

```java
public class DistanceUtil {
    private static final double EARTH_RADIUS_METERS = 6_371_000;

    public static double distanceMeters(
            double lat1,
            double lon1,
            double lat2,
            double lon2
    ) {
        double latRad1 = Math.toRadians(lat1);
        double latRad2 = Math.toRadians(lat2);
        double deltaLat = Math.toRadians(lat2 - lat1);
        double deltaLon = Math.toRadians(lon2 - lon1);

        double a = Math.sin(deltaLat / 2) * Math.sin(deltaLat / 2)
                + Math.cos(latRad1) * Math.cos(latRad2)
                * Math.sin(deltaLon / 2) * Math.sin(deltaLon / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return EARTH_RADIUS_METERS * c;
    }
}
```

### Nearby search service — simplified

```java
import java.util.*;
import java.util.stream.Collectors;

public class NearbySearchService {

    private final GeoHashIndex geoHashIndex;
    private final BusinessRepository businessRepository;

    public NearbySearchService(
            GeoHashIndex geoHashIndex,
            BusinessRepository businessRepository
    ) {
        this.geoHashIndex = geoHashIndex;
        this.businessRepository = businessRepository;
    }

    public List<BusinessResult> searchNearby(
            double userLat,
            double userLon,
            int radiusMeters,
            int limit
    ) {
        int precision = GeoPrecision.geohashLengthForRadiusMeters(radiusMeters);

        String currentHash = GeoHash.encode(userLat, userLon, precision);
        List<String> hashesToSearch = new ArrayList<>();
        hashesToSearch.add(currentHash);
        hashesToSearch.addAll(GeoHash.neighbors(currentHash));

        Set<Long> businessIds = new HashSet<>();
        for (String geohash : hashesToSearch) {
            businessIds.addAll(geoHashIndex.getBusinessIds(geohash));
        }

        return businessRepository.findByIds(businessIds)
                .stream()
                .map(b -> new BusinessResult(
                        b,
                        DistanceUtil.distanceMeters(
                                userLat,
                                userLon,
                                b.latitude(),
                                b.longitude()
                        )
                ))
                .filter(result -> result.distanceMeters() <= radiusMeters)
                .sorted(Comparator.comparingDouble(BusinessResult::distanceMeters))
                .limit(limit)
                .collect(Collectors.toList());
    }
}
```

### Result wrapper

```java
public record BusinessResult(
        Business business,
        double distanceMeters
) {}
```

### Interfaces for cache / DB access

```java
import java.util.*;

public interface GeoHashIndex {
    List<Long> getBusinessIds(String geohash);
}

public interface BusinessRepository {
    List<Business> findByIds(Collection<Long> businessIds);
}
```

### Redis-backed geohash index — pseudocode style

```java
import java.util.*;

public class CachedGeoHashIndex implements GeoHashIndex {

    private final RedisClient redis;
    private final GeoIndexRepository geoIndexRepository;

    public CachedGeoHashIndex(RedisClient redis, GeoIndexRepository geoIndexRepository) {
        this.redis = redis;
        this.geoIndexRepository = geoIndexRepository;
    }

    @Override
    public List<Long> getBusinessIds(String geohash) {
        String cacheKey = "geohash:" + geohash;

        List<Long> cachedIds = redis.getList(cacheKey);
        if (cachedIds != null) {
            return cachedIds;
        }

        List<Long> idsFromDb = geoIndexRepository.findBusinessIdsByGeohashPrefix(geohash);
        redis.setList(cacheKey, idsFromDb, 24 * 60 * 60); // TTL: 1 day
        return idsFromDb;
    }
}
```

### Placeholder interfaces

```java
import java.util.*;

public interface RedisClient {
    List<Long> getList(String key);
    void setList(String key, List<Long> value, int ttlSeconds);
}

public interface GeoIndexRepository {
    List<Long> findBusinessIdsByGeohashPrefix(String geohashPrefix);
}
```

### Geohash placeholder

In production, use a tested geohash library. This placeholder shows the service shape.

```java
import java.util.*;

public class GeoHash {

    public static String encode(double latitude, double longitude, int precision) {
        // Use a real geohash library in production.
        // Example output: "9q8znf"
        throw new UnsupportedOperationException("Use a real geohash implementation");
    }

    public static List<String> neighbors(String geohash) {
        // Return 8 neighboring geohashes.
        // Use a real geohash library in production.
        throw new UnsupportedOperationException("Use a real geohash implementation");
    }
}
```

---

## 11. Quadtree Visual Explanation

Quadtree recursively divides the map into four parts until each leaf has a small enough number of businesses.

```text
                         ┌──────────────┐
                         │ World: 200M  │
                         └──────┬───────┘
                                │
        ┌───────────────────────┼───────────────────────┐
        ▼                       ▼                       ▼
┌──────────────┐        ┌──────────────┐        ┌──────────────┐
│ NW: 40M      │        │ NE: 30M      │        │ SW: 70M      │ ...
└──────┬───────┘        └──────────────┘        └──────────────┘
       │
       ▼
┌──────────────────────────────────────┐
│ Keep splitting until each leaf has   │
│ <= 100 businesses                    │
└──────────────────────────────────────┘
```

### Quadtree build pseudocode

```java
public class QuadTreeBuilder {

    private static final int MAX_BUSINESSES_PER_LEAF = 100;

    public void buildQuadTree(QuadTreeNode node) {
        if (node.businessCount() <= MAX_BUSINESSES_PER_LEAF) {
            return;
        }

        node.subdivide();

        for (QuadTreeNode child : node.children()) {
            buildQuadTree(child);
        }
    }
}
```

### Quadtree node shape

```java
import java.util.*;

public class QuadTreeNode {
    private double minLat;
    private double maxLat;
    private double minLon;
    private double maxLon;
    private List<Long> businessIds;
    private List<QuadTreeNode> children;

    public int businessCount() {
        return businessIds == null ? 0 : businessIds.size();
    }

    public void subdivide() {
        // Split current rectangle into NW, NE, SW, SE.
        // Move business IDs into child nodes.
    }

    public List<QuadTreeNode> children() {
        return children == null ? List.of() : children;
    }
}
```

### Geohash vs Quadtree

```text
┌─────────────────┬───────────────────────┬───────────────────────┐
│ Feature         │ Geohash               │ Quadtree              │
├─────────────────┼───────────────────────┼───────────────────────┤
│ Simplicity      │ Easier                │ Harder                │
│ Update index    │ Easy                  │ More complex          │
│ Radius search   │ Good                  │ Good                  │
│ k-nearest       │ Less natural          │ Better                │
│ Dense areas     │ Fixed cell size issue │ Adapts better         │
└─────────────────┴───────────────────────┴───────────────────────┘
```

---

## 12. Scaling the Database

### Business table scaling

Shard by `business_id`.

```text
                  ┌────────────────────┐
                  │ Business Service   │
                  └─────────┬──────────┘
                            │
                  hash(business_id)
                            │
        ┌───────────────────┼───────────────────┐
        ▼                   ▼                   ▼
┌──────────────┐    ┌──────────────┐    ┌──────────────┐
│ Business DB  │    │ Business DB  │    │ Business DB  │
│ Shard 1      │    │ Shard 2      │    │ Shard 3      │
└──────────────┘    └──────────────┘    └──────────────┘
```

### Geospatial index scaling

Prefer replicas first because the index is relatively small but read-heavy.

```text
                      ┌──────────────────┐
                      │ Primary Geo DB   │
                      └────────┬─────────┘
                               │ replicate
          ┌────────────────────┼────────────────────┐
          ▼                    ▼                    ▼
┌─────────────────┐   ┌─────────────────┐   ┌─────────────────┐
│ Geo Replica 1   │   │ Geo Replica 2   │   │ Geo Replica 3   │
│ read traffic    │   │ read traffic    │   │ read traffic    │
└─────────────────┘   └─────────────────┘   └─────────────────┘
```

---

## 13. Multi-Region Deployment

```text
┌──────────────────────────────────────────────────────────────┐
│ Global DNS / Traffic Router                                 │
└───────────────┬──────────────────────────┬──────────────────┘
                │                          │
                ▼                          ▼
┌──────────────────────────┐    ┌──────────────────────────┐
│ US-West Region           │    │ Europe Region            │
├──────────────────────────┤    ├──────────────────────────┤
│ LBS servers              │    │ LBS servers              │
│ Redis geohash cache      │    │ Redis geohash cache      │
│ Business info cache      │    │ Business info cache      │
│ DB replicas              │    │ DB replicas              │
└──────────────────────────┘    └──────────────────────────┘
```

Benefits:

```text
┌──────────────────────────────┐
│ Lower latency                │
│ Better availability          │
│ Regional traffic isolation   │
│ Easier privacy compliance    │
└──────────────────────────────┘
```

---

## 14. Filtering Results

Examples:

- Open now
- Restaurant only
- Price range
- Rating threshold

Simple approach:

```text
1. Fetch candidate business IDs by geohash
2. Hydrate business objects
3. Filter by type / open time / rating
4. Rank by distance and relevance
```

Why this works: after geohash filtering, the candidate set is relatively small.

---

## 15. Final Architecture Diagram

```text
                                ┌──────────────────┐
                                │      Client      │
                                └────────┬─────────┘
                                         │
                                         ▼
                                ┌──────────────────┐
                                │  Load Balancer   │
                                └───────┬──────────┘
                                        │
                ┌───────────────────────┴───────────────────────┐
                ▼                                               ▼
┌─────────────────────────────────┐             ┌─────────────────────────────────┐
│ Location-Based Service          │             │ Business Service                │
│ /search/nearby                  │             │ /businesses/{id}                │
├─────────────────────────────────┤             ├─────────────────────────────────┤
│ 1. Pick geohash precision       │             │ Read business detail            │
│ 2. Get current + neighbors      │             │ Add/update/delete business      │
│ 3. Fetch candidate IDs          │             │ Update DB primary               │
│ 4. Hydrate business objects     │             │ Invalidate/update cache nightly │
│ 5. Distance filter + rank       │             │                                 │
└───────────────┬─────────────────┘             └───────────────┬─────────────────┘
                │                                               │
                ▼                                               ▼
┌─────────────────────────────────┐             ┌─────────────────────────────────┐
│ Redis Cluster                   │             │ Database Cluster                │
├─────────────────────────────────┤             ├─────────────────────────────────┤
│ geohash -> business IDs         │             │ Primary DB: writes              │
│ businessId -> business object   │             │ Replica DBs: reads              │
└─────────────────────────────────┘             └─────────────────────────────────┘
```

---

## 16. Interview Talking Points

```text
┌────────────────────────────────────────────────────────────┐
│ Key things to say                                          │
├────────────────────────────────────────────────────────────┤
│ This is read-heavy, so LBS must scale horizontally.         │
│ Raw lat/lng range query is inefficient for large datasets.  │
│ Use geospatial indexing: geohash, quadtree, or S2.          │
│ Geohash is simple and works well for radius search.         │
│ Search current cell + neighbors to avoid boundary misses.   │
│ Cache geohash -> business IDs and businessId -> object.     │
│ Business updates are low QPS and can be effective next day. │
│ Deploy LBS regionally for latency and privacy compliance.   │
└────────────────────────────────────────────────────────────┘
```

---

## 17. Quick Memory Map

```text
Proximity Service
│
├── API
│   ├── /search/nearby
│   └── /businesses/{id}
│
├── Core Services
│   ├── LBS Service
│   └── Business Service
│
├── Geo Index
│   ├── Geohash
│   ├── Quadtree
│   └── S2
│
├── Cache
│   ├── geohash -> business IDs
│   └── businessId -> business object
│
├── DB
│   ├── business table
│   └── geospatial_index table
│
└── Scaling
    ├── stateless services
    ├── read replicas
    ├── regional deployment
    └── cache replication
```

---

## 18. One-Minute Summary

A proximity service is a read-heavy location-based system. The main challenge is finding nearby businesses quickly from hundreds of millions of records. A naive latitude/longitude search is inefficient, so we use a geospatial index such as geohash or quadtree. For geohash, the system maps the user location to a geohash cell, also searches neighboring cells, fetches candidate business IDs, hydrates business details, filters by exact distance, ranks results, and returns a paginated response. Caching and read replicas reduce latency, while regional deployment improves availability, latency, and privacy compliance.
