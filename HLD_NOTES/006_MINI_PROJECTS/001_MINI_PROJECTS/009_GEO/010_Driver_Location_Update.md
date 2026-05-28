# 010_Driver_Location_Update.md

# MiniGeo Phase 10 — Driver Location Update

## Clickable Index

- [1. Goal](#1-goal)
- [2. Why This Phase Matters](#2-why-this-phase-matters)
- [3. Previous Limitation](#3-previous-limitation)
- [4. What We Build](#4-what-we-build)
- [5. Architecture](#5-architecture)
- [6. Geo Mental Model](#6-geo-mental-model)
- [7. Step-by-Step Flow](#7-step-by-step-flow)
- [8. Complete Java Code](#8-complete-java-code)
- [9. Dry Run](#9-dry-run)
- [10. DSA / CP Concepts Used](#10-dsa--cp-concepts-used)
- [11. System Design Relevance](#11-system-design-relevance)
- [12. Production Concepts](#12-production-concepts)
- [13. Scalability Discussion](#13-scalability-discussion)
- [14. Interview Notes](#14-interview-notes)
- [15. Common Bugs](#15-common-bugs)
- [16. Current Limitations](#16-current-limitations)
- [17. Next Step](#17-next-step)

---

# 1. Goal

In this phase, we build:

```text
Driver Location Update
```

Purpose:

```text
Update moving driver locations.
```

---

# 2. Why This Phase Matters

Geo systems answer questions like:

```text
who is nearby
what is inside radius
which driver is nearest
which restaurant is closest
```

This phase improves one critical geo capability.

---

# 3. Previous Limitation

Earlier phases lacked:

```text
distance calculation
candidate filtering
spatial indexing
efficient lookup
streaming updates
```

This phase improves one of those areas.

---

# 4. What We Build

Core geo pipeline:

```text
entity
  ->
location
  ->
spatial structure
  ->
candidate filtering
  ->
distance/ranking
```

This phase focuses on:

```text
Driver Location Update
```

---

# 5. Architecture

```text
Geo Client
   |
   v
Geo Service
   |
   v
Spatial Structure / Store
   |
   v
Distance / Candidate Filtering
   |
   v
Nearby Results
```

---

# 6. Geo Mental Model

Geo search usually works like:

```text
1. Reduce candidate search space
2. Calculate accurate distance
3. Sort by nearest
4. Return top results
```

Without spatial indexing:

```text
O(N)
```

With indexing:

```text
much fewer candidates
```

---

# 7. Step-by-Step Flow

```text
1. Store geo entities
2. Organize entities spatially
3. Filter nearby candidates
4. Calculate distances
5. Return nearest entities
```

---

# 8. Complete Java Code

## 8.1 `GeoPoint.java`

### Logic before this class

Represents one geo coordinate.

```java
package com.minigeo.model;

public class GeoPoint {

    private final double latitude;
    private final double longitude;

    public GeoPoint(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    @Override
    public String toString() {
        return "(" + latitude + "," + longitude + ")";
    }
}
```

---

## 8.2 `GeoEntity.java`

### Logic before this class

Represents one geo-enabled object.

```java
package com.minigeo.model;

public class GeoEntity {

    private final String id;
    private GeoPoint point;

    public GeoEntity(String id, GeoPoint point) {
        this.id = id;
        this.point = point;
    }

    public String getId() {
        return id;
    }

    public GeoPoint getPoint() {
        return point;
    }

    public void updatePoint(GeoPoint point) {
        this.point = point;
    }

    @Override
    public String toString() {
        return id + " -> " + point;
    }
}
```

---

## 8.3 `GeoService.java`

### Logic before this class

This service stores entities and performs geo operations.

```java
package com.minigeo.service;

import com.minigeo.model.GeoEntity;
import com.minigeo.model.GeoPoint;

import java.util.*;

public class GeoService {

    private final Map<String, GeoEntity> entities =
            new HashMap<>();

    public void register(String id, double lat, double lon) {
        entities.put(
                id,
                new GeoEntity(
                        id,
                        new GeoPoint(lat, lon)
                )
        );
    }

    public void update(String id, double lat, double lon) {
        GeoEntity entity = entities.get(id);

        if (entity != null) {
            entity.updatePoint(
                    new GeoPoint(lat, lon)
            );
        }
    }

    public List<GeoEntity> all() {
        return new ArrayList<>(entities.values());
    }

    public Optional<GeoEntity> find(String id) {
        return Optional.ofNullable(entities.get(id));
    }
}
```

---

## 8.4 `Phase10Driver.java`

### Logic before this class

Driver demonstrates geo operations.

```java
package com.minigeo.driver;

import com.minigeo.service.GeoService;

public class Phase10Driver {

    public static void main(String[] args) {

        GeoService geoService =
                new GeoService();

        geoService.register(
                "driver-1",
                44.437,
                26.102
        );

        geoService.register(
                "restaurant-1",
                44.439,
                26.096
        );

        geoService.update(
                "driver-1",
                44.440,
                26.105
        );

        System.out.println("All entities:");

        for (var entity : geoService.all()) {
            System.out.println(entity);
        }
    }
}
```

---

# 9. Dry Run

```text
register(driver-1)
register(restaurant-1)

update(driver-1)

Result:
driver location updated
```

Geo flow:

```text
location update
   ->
store/index update
   ->
search candidate filtering
   ->
distance calculation
   ->
return nearby results
```

---

# 10. DSA / CP Concepts Used

| Concept | Usage |
|---|---|
| HashMap | entity lookup |
| Grid partitioning | spatial grouping |
| Heap/PriorityQueue | nearest N |
| Tree structures | QuadTree/RTree |
| Geometry | distance math |

---

# 11. System Design Relevance

This phase maps to:

```text
Uber
DoorDash
Google Maps
food delivery
nearby search
fleet tracking
location streaming
```

---

# 12. Production Concepts

Production geo systems need:

```text
GeoHash
bounding boxes
spatial indexes
moving object updates
location TTL
region sharding
hotspot handling
Kafka streams
```

---

# 13. Scalability Discussion

Scaling path:

```text
single geo store
  ->
grid partitioning
  ->
GeoHash
  ->
distributed geo shards
  ->
multi-region routing
```

Bottlenecks:

```text
hot cities
frequent location updates
millions of moving drivers
high nearby-search QPS
```

---

# 14. Interview Notes

Common questions:

```text
How do you find nearest drivers?
Why use GeoHash?
How do you avoid scanning all entities?
How do moving drivers update indexes?
How do you shard geo systems?
```

---

# 15. Common Bugs

## Bug 1 — Lat/lon swapped

Fix:

```text
clear validation and naming
```

## Bug 2 — Stale location

Fix:

```text
TTL for moving objects
```

## Bug 3 — Full scan search

Fix:

```text
GeoHash/grid filtering
```

---

# 16. Current Limitations

This phase is simplified.

Production systems additionally need:

```text
distributed geo indexes
moving-object optimization
cache
stream processing
replication
observability
```

---

# 17. Next Step

```text
011_Nearest_Driver_Matching.md
```
