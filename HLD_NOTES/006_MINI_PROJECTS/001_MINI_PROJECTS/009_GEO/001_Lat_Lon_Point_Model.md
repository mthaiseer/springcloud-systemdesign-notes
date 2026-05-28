# 001_Lat_Lon_Point_Model.md

# MiniGeo Phase 001 — Latitude / Longitude Point Model

## Clickable Index

- [1. Goal](#1-goal)
- [2. Why This Phase Matters](#2-why-this-phase-matters)
- [3. What We Build](#3-what-we-build)
- [4. Current Architecture](#4-current-architecture)
- [5. Geo System Mental Model](#5-geo-system-mental-model)
- [6. Latitude / Longitude Basics](#6-latitude--longitude-basics)
- [7. Folder Structure](#7-folder-structure)
- [8. Step-by-Step Flow](#8-step-by-step-flow)
- [9. Complete Java Code](#9-complete-java-code)
  - [9.1 GeoPoint.java](#91-geopointjava)
  - [9.2 GeoEntityType.java](#92-geoentitytypejava)
  - [9.3 GeoEntity.java](#93-geoentityjava)
  - [9.4 GeoStore.java](#94-geostorejava)
  - [9.5 GeoService.java](#95-geoservicejava)
  - [9.6 Phase001LatLonPointModelDriver.java](#96-phase001latlonpointmodeldriverjava)
- [10. How To Run](#10-how-to-run)
- [11. Dry Run](#11-dry-run)
- [12. DSA / CP Concepts Used](#12-dsa--cp-concepts-used)
- [13. System Design Relevance](#13-system-design-relevance)
- [14. Geo Connection With This Phase](#14-geo-connection-with-this-phase)
- [15. Production-Grade Concepts](#15-production-grade-concepts)
- [16. Scalability Discussion](#16-scalability-discussion)
- [17. Interview Notes](#17-interview-notes)
- [18. Common Bugs](#18-common-bugs)
- [19. Current Limitations](#19-current-limitations)
- [20. Next Step](#20-next-step)

---

# 1. Goal

In this phase, we build the first foundation of **MiniGeo**:

```text
Latitude / Longitude Point Model
```

Every geo system starts with a simple concept:

```text
object
  ->
location
  ->
latitude + longitude
```

Examples:

```text
driver-1      -> 44.437, 26.102
restaurant-9  -> 44.439, 26.096
user-77       -> 44.430, 26.110
warehouse-5   -> 44.500, 26.000
```

Before we can build:

```text
distance calculation
nearby search
GeoHash
grid index
nearest driver
restaurant search
delivery assignment
moving object tracking
geofencing
geo sharding
```

we need a clean point model.

This phase builds:

```text
GeoPoint
GeoEntityType
GeoEntity
GeoStore
GeoService
```

No distance calculation yet.

No indexing yet.

No nearest search yet.

This phase is only about representing geospatial entities correctly.

---

# 2. Why This Phase Matters

A geo system fails if the location model is wrong.

Real geo systems use location data for:

```text
find nearby drivers
find nearby restaurants
track delivery agents
match riders to drivers
show nearby friends
calculate ETA
detect geofence entry
route requests by region
```

All of them start with:

```text
latitude
longitude
entity id
entity type
metadata
timestamp
```

If this base model is clean, later indexing and search becomes easier.

If this base model is messy, every later phase becomes hard.

---

# 3. What We Build

We build an in-memory geo entity model.

Supported behavior:

```text
create point
validate latitude / longitude
create geo entity
store geo entity
find entity by id
list all entities
update entity location
print geo store
```

Example:

```text
id = driver-1
type = DRIVER
lat = 44.437
lon = 26.102
```

Expected:

```text
driver-1 stored successfully
```

Then:

```text
update driver-1 to new location
```

Expected:

```text
driver-1 location updated
```

---

# 4. Current Architecture

```text
+----------------------+
| Driver / Client      |
+----------+-----------+
           |
           | create/update location
           v
+----------------------+
| GeoService           |
| validation + logic   |
+----------+-----------+
           |
           | save entity
           v
+----------------------+
| GeoStore             |
| in-memory HashMap    |
+----------+-----------+
           |
           v
+----------------------+
| GeoEntity            |
| id/type/point        |
+----------------------+
```

## Request Flow

```text
1. Driver creates a GeoPoint
2. Driver asks GeoService to register entity
3. GeoService validates entity
4. GeoStore stores entity by ID
5. Driver lists all geo entities
6. Driver updates one entity location
```

---

# 5. Geo System Mental Model

A complete geo system usually follows this pipeline:

```text
Raw Location
   |
   v
Validate Lat/Lon
   |
   v
Store Entity Location
   |
   v
Index By Spatial Structure
   |
   v
Filter Candidate Entities
   |
   v
Calculate Distance
   |
   v
Sort / Rank
   |
   v
Return Nearby Results
```

This phase only covers the first part:

```text
Raw Location
   |
   v
Validate Lat/Lon
   |
   v
Store Entity Location
```

Later phases add:

```text
Haversine distance
bounding box
grid indexing
GeoHash
nearest N
driver matching
PostGIS model
Redis GEO model
Elasticsearch geo query
```

---

# 6. Latitude / Longitude Basics

## Latitude

Latitude measures north/south position.

Valid range:

```text
-90 to +90
```

Examples:

```text
0      -> Equator
+90    -> North Pole
-90    -> South Pole
44.437 -> Bucharest area
```

## Longitude

Longitude measures east/west position.

Valid range:

```text
-180 to +180
```

Examples:

```text
0      -> Greenwich meridian
+180   -> east limit
-180   -> west limit
26.102 -> Bucharest area
```

## Valid Point

```text
lat = 44.437
lon = 26.102
```

## Invalid Point

```text
lat = 120
lon = 300
```

Why invalid?

```text
latitude cannot exceed 90
longitude cannot exceed 180
```

---

# 7. Folder Structure

Create this structure:

```text
MiniGeo/
└── src/
    └── main/
        └── java/
            └── com/
                └── minigeo/
                    ├── model/
                    │   ├── GeoPoint.java
                    │   ├── GeoEntityType.java
                    │   └── GeoEntity.java
                    ├── store/
                    │   └── GeoStore.java
                    ├── service/
                    │   └── GeoService.java
                    └── driver/
                        └── Phase001LatLonPointModelDriver.java
```

Recommended package style:

```text
com.minigeo.model
com.minigeo.store
com.minigeo.service
com.minigeo.driver
```

---

# 8. Step-by-Step Flow

## Step 1 — Define GeoPoint

A point contains:

```text
latitude
longitude
```

It validates:

```text
-90 <= latitude <= 90
-180 <= longitude <= 180
```

---

## Step 2 — Define GeoEntityType

Entity can be:

```text
DRIVER
RIDER
RESTAURANT
DELIVERY_AGENT
WAREHOUSE
USER
CUSTOM
```

---

## Step 3 — Define GeoEntity

Entity contains:

```text
id
type
point
metadata
createdAt
updatedAt
```

---

## Step 4 — Store Entity

GeoStore uses:

```text
Map<String, GeoEntity>
```

---

## Step 5 — Update Location

A moving entity like driver changes location:

```text
driver-1 -> new GeoPoint
```

---

## Step 6 — List Entities

Driver prints all stored entities.

---

# 9. Complete Java Code

---

## 9.1 GeoPoint.java

### Logic before this class

`GeoPoint` represents one latitude/longitude point.

It validates the most important geo rule:

```text
latitude must be between -90 and 90
longitude must be between -180 and 180
```

This validation should be close to the model so invalid points cannot enter the system.

Bad:

```text
GeoPoint(200, 999)
```

Good:

```text
GeoPoint(44.437, 26.102)
```

```java
package com.minigeo.model;

public class GeoPoint {

    private final double latitude;
    private final double longitude;

    public GeoPoint(double latitude, double longitude) {
        validateLatitude(latitude);
        validateLongitude(longitude);

        this.latitude = latitude;
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    private void validateLatitude(double value) {
        if (value < -90.0 || value > 90.0) {
            throw new IllegalArgumentException(
                    "Latitude must be between -90 and 90. Actual=" + value
            );
        }
    }

    private void validateLongitude(double value) {
        if (value < -180.0 || value > 180.0) {
            throw new IllegalArgumentException(
                    "Longitude must be between -180 and 180. Actual=" + value
            );
        }
    }

    @Override
    public String toString() {
        return "GeoPoint{" +
                "latitude=" + latitude +
                ", longitude=" + longitude +
                '}';
    }
}
```

---

## 9.2 GeoEntityType.java

### Logic before this class

Different geo entities behave differently.

Examples:

```text
DRIVER moves frequently
RESTAURANT is mostly static
WAREHOUSE is static
RIDER moves during trip
DELIVERY_AGENT moves frequently
```

So we store entity type explicitly.

Later phases can use type for:

```text
filter only drivers
filter only restaurants
match riders to drivers
assign delivery agents
```

```java
package com.minigeo.model;

public enum GeoEntityType {

    DRIVER,

    RIDER,

    RESTAURANT,

    DELIVERY_AGENT,

    WAREHOUSE,

    USER,

    CUSTOM
}
```

---

## 9.3 GeoEntity.java

### Logic before this class

`GeoEntity` represents one real-world object with a location.

Fields:

```text
id
type
point
metadata
createdAt
updatedAt
```

`metadata` is a simple string in this phase.

Later it may become:

```text
Map<String, String>
JSON object
driver availability status
restaurant category
delivery capacity
vehicle type
```

Location can change, so we provide:

```text
updateLocation()
```

This updates:

```text
point
updatedAt
```

```java
package com.minigeo.model;

import java.time.Instant;

public class GeoEntity {

    private final String id;
    private final GeoEntityType type;
    private GeoPoint point;
    private final String metadata;
    private final Instant createdAt;
    private Instant updatedAt;

    public GeoEntity(
            String id,
            GeoEntityType type,
            GeoPoint point,
            String metadata
    ) {
        this.id = validateId(id);
        this.type = validateType(type);
        this.point = validatePoint(point);
        this.metadata = metadata;
        this.createdAt = Instant.now();
        this.updatedAt = this.createdAt;
    }

    public String getId() {
        return id;
    }

    public GeoEntityType getType() {
        return type;
    }

    public GeoPoint getPoint() {
        return point;
    }

    public String getMetadata() {
        return metadata;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void updateLocation(GeoPoint newPoint) {
        this.point = validatePoint(newPoint);
        this.updatedAt = Instant.now();
    }

    private String validateId(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Entity id cannot be empty");
        }

        return value.trim();
    }

    private GeoEntityType validateType(GeoEntityType value) {
        if (value == null) {
            throw new IllegalArgumentException("Entity type cannot be null");
        }

        return value;
    }

    private GeoPoint validatePoint(GeoPoint value) {
        if (value == null) {
            throw new IllegalArgumentException("GeoPoint cannot be null");
        }

        return value;
    }

    @Override
    public String toString() {
        return "GeoEntity{" +
                "id='" + id + '\'' +
                ", type=" + type +
                ", point=" + point +
                ", metadata='" + metadata + '\'' +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}
```

---

## 9.4 GeoStore.java

### Logic before this class

This is the in-memory geo database.

For now, we use:

```text
Map<String, GeoEntity>
```

where:

```text
entityId -> GeoEntity
```

Operations:

```text
save
findById
findAll
deleteById
count
```

Later phases add indexes:

```text
grid index
GeoHash index
QuadTree
RTree
S2 cells
```

For now, storage is simple.

```java
package com.minigeo.store;

import com.minigeo.model.GeoEntity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class GeoStore {

    private final Map<String, GeoEntity> entitiesById = new HashMap<>();

    public void save(GeoEntity entity) {
        entitiesById.put(entity.getId(), entity);
    }

    public Optional<GeoEntity> findById(String entityId) {
        return Optional.ofNullable(entitiesById.get(entityId));
    }

    public List<GeoEntity> findAll() {
        return new ArrayList<>(entitiesById.values());
    }

    public boolean deleteById(String entityId) {
        return entitiesById.remove(entityId) != null;
    }

    public int count() {
        return entitiesById.size();
    }
}
```

---

## 9.5 GeoService.java

### Logic before this class

`GeoService` is the business layer.

It coordinates:

```text
register entity
update entity location
find entity
list entities
```

Why not use GeoStore directly?

Because later GeoService will coordinate:

```text
distance calculation
index updates
TTL cleanup
Kafka location events
driver matching
geofence events
metrics
```

For now, it wraps basic store operations cleanly.

```java
package com.minigeo.service;

import com.minigeo.model.GeoEntity;
import com.minigeo.model.GeoEntityType;
import com.minigeo.model.GeoPoint;
import com.minigeo.store.GeoStore;

import java.util.List;
import java.util.Optional;

public class GeoService {

    private final GeoStore store;

    public GeoService(GeoStore store) {
        this.store = store;
    }

    public GeoEntity register(
            String id,
            GeoEntityType type,
            double latitude,
            double longitude,
            String metadata
    ) {
        GeoPoint point = new GeoPoint(latitude, longitude);

        GeoEntity entity =
                new GeoEntity(
                        id,
                        type,
                        point,
                        metadata
                );

        store.save(entity);

        return entity;
    }

    public boolean updateLocation(
            String entityId,
            double latitude,
            double longitude
    ) {
        Optional<GeoEntity> optionalEntity =
                store.findById(entityId);

        if (optionalEntity.isEmpty()) {
            return false;
        }

        GeoPoint newPoint =
                new GeoPoint(latitude, longitude);

        optionalEntity.get().updateLocation(newPoint);

        return true;
    }

    public Optional<GeoEntity> findById(String entityId) {
        return store.findById(entityId);
    }

    public List<GeoEntity> findAll() {
        return store.findAll();
    }

    public int count() {
        return store.count();
    }
}
```

---

## 9.6 Phase001LatLonPointModelDriver.java

### Logic before this class

This driver proves the geo model works.

It creates:

```text
driver
restaurant
warehouse
user
```

Then it updates driver location.

It also tests invalid latitude/longitude.

```java
package com.minigeo.driver;

import com.minigeo.model.GeoEntity;
import com.minigeo.model.GeoEntityType;
import com.minigeo.service.GeoService;
import com.minigeo.store.GeoStore;

public class Phase001LatLonPointModelDriver {

    public static void main(String[] args) {

        GeoStore store =
                new GeoStore();

        GeoService geoService =
                new GeoService(store);

        GeoEntity driver =
                geoService.register(
                        "driver-1",
                        GeoEntityType.DRIVER,
                        44.437,
                        26.102,
                        "vehicle=car,status=available"
                );

        GeoEntity restaurant =
                geoService.register(
                        "restaurant-1",
                        GeoEntityType.RESTAURANT,
                        44.439,
                        26.096,
                        "category=indian,rating=4.7"
                );

        GeoEntity warehouse =
                geoService.register(
                        "warehouse-1",
                        GeoEntityType.WAREHOUSE,
                        44.500,
                        26.000,
                        "capacity=large"
                );

        geoService.register(
                "user-1",
                GeoEntityType.USER,
                44.430,
                26.110,
                "name=mohamed"
        );

        System.out.println("Initial entities:");
        printAll(geoService);

        System.out.println();
        System.out.println("Updating driver location...");

        geoService.updateLocation(
                "driver-1",
                44.440,
                26.105
        );

        System.out.println();
        System.out.println("After update:");
        printAll(geoService);

        System.out.println();
        System.out.println("Find driver-1:");
        geoService
                .findById("driver-1")
                .ifPresent(System.out::println);

        System.out.println();
        System.out.println("Testing invalid point:");

        try {
            geoService.register(
                    "bad-location",
                    GeoEntityType.CUSTOM,
                    120.0,
                    300.0,
                    "invalid"
            );
        } catch (IllegalArgumentException e) {
            System.out.println("Validation error: " + e.getMessage());
        }

        System.out.println();
        System.out.println("Total entities: " + geoService.count());
    }

    private static void printAll(GeoService geoService) {
        for (GeoEntity entity : geoService.findAll()) {
            System.out.println(entity);
        }
    }
}
```

---

# 10. How To Run

## IntelliJ

1. Create Java project:

```text
MiniGeo
```

2. Create packages:

```text
com.minigeo.model
com.minigeo.store
com.minigeo.service
com.minigeo.driver
```

3. Add all Java files.

4. Run:

```text
Phase001LatLonPointModelDriver
```

---

## Command Line

Compile:

```bash
javac -d out src/main/java/com/minigeo/model/GeoPoint.java \
             src/main/java/com/minigeo/model/GeoEntityType.java \
             src/main/java/com/minigeo/model/GeoEntity.java \
             src/main/java/com/minigeo/store/GeoStore.java \
             src/main/java/com/minigeo/service/GeoService.java \
             src/main/java/com/minigeo/driver/Phase001LatLonPointModelDriver.java
```

Run:

```bash
java -cp out com.minigeo.driver.Phase001LatLonPointModelDriver
```

---

# 11. Dry Run

## Step 1 — Register Driver

Input:

```text
id = driver-1
type = DRIVER
lat = 44.437
lon = 26.102
metadata = vehicle=car,status=available
```

Flow:

```text
GeoService.register()
    ->
new GeoPoint(44.437, 26.102)
    ->
validate lat/lon
    ->
new GeoEntity(...)
    ->
GeoStore.save(entity)
```

Store:

```text
driver-1 -> GeoEntity(DRIVER, 44.437, 26.102)
```

---

## Step 2 — Register Restaurant

Input:

```text
restaurant-1
RESTAURANT
44.439
26.096
```

Store:

```text
driver-1     -> DRIVER
restaurant-1 -> RESTAURANT
```

---

## Step 3 — Update Driver Location

Input:

```text
driver-1 -> 44.440, 26.105
```

Flow:

```text
GeoService.updateLocation()
    ->
GeoStore.findById(driver-1)
    ->
new GeoPoint(44.440, 26.105)
    ->
entity.updateLocation(newPoint)
```

Updated:

```text
driver-1 -> 44.440, 26.105
```

---

## Step 4 — Invalid Location

Input:

```text
lat = 120
lon = 300
```

Validation:

```text
latitude 120 > 90
```

Output:

```text
Validation error
```

---

# 12. DSA / CP Concepts Used

| Concept | Usage |
|---|---|
| HashMap | entityId -> GeoEntity |
| List | return all entities |
| Object modeling | point/entity/store |
| Validation | coordinate bounds |
| Optional | safe lookup |
| State update | moving object location update |

## Complexity

| Operation | Complexity |
|---|---|
| Save entity | O(1) average |
| Find by ID | O(1) average |
| Update location | O(1) average |
| List all | O(N) |

This phase does not solve nearby search yet.

Nearby search without index is:

```text
O(N)
```

Later phases reduce candidates using spatial indexes.

---

# 13. System Design Relevance

This phase maps to systems like:

```text
Uber driver location
DoorDash delivery agents
Google Maps places
Nearby restaurants
Social nearby users
Warehouse location lookup
Fleet tracking
```

In HLD, you often draw:

```text
Mobile App
   ->
Location Service
   ->
Geo Store / Index
   ->
Nearby Search
```

This phase builds the data model for that location service.

---

# 14. Geo Connection With This Phase

Real geo systems use similar base records.

Example driver location record:

```text
driver_id
latitude
longitude
status
updated_at
city_id
vehicle_type
```

Example restaurant record:

```text
restaurant_id
latitude
longitude
category
rating
open_status
```

Current MiniGeo:

```text
id
type
GeoPoint
metadata
createdAt
updatedAt
```

Later phases add:

```text
distance
bounding box
grid index
GeoHash
nearest search
TTL
Kafka updates
PostGIS model
Elasticsearch query
```

---

# 15. Production-Grade Concepts

Production geo systems must consider:

```text
coordinate validation
location freshness
moving object updates
GPS noise
privacy
TTL for stale locations
index updates
hot regions
multi-region routing
storage precision
distance calculation accuracy
```

Important production fields:

```text
location_updated_at
location_accuracy_meters
source
city_id
region_id
availability_status
```

For moving objects like drivers:

```text
old location must be removed from index
new location must be added to index
```

This becomes important when we build GeoHash/Grid index.

---

# 16. Scalability Discussion

Current phase:

```text
single JVM
in-memory HashMap
O(1) lookup by ID
O(N) scan for nearby
```

Good for learning.

Not enough for production nearby search.

Scaling path:

```text
Phase 001: point model
Phase 002: Haversine distance
Phase 004: bounding box filtering
Phase 005: grid index
Phase 006: GeoHash
Phase 008: radius search
Phase 009: nearest N search
Phase 022: shard by GeoHash
Phase 031: production architecture
```

Bottlenecks later:

```text
millions of moving drivers
frequent location updates
hot city centers
high fanout search
index rebuild cost
stale locations
cross-region lookup
```

---

# 17. Interview Notes

## Q1. How do you model location?

Use:

```text
latitude
longitude
entity id
entity type
updated timestamp
```

For moving objects, also store:

```text
last updated time
availability status
accuracy
```

---

## Q2. Why validate lat/lon?

Invalid coordinates corrupt geo indexes and distance calculations.

Valid ranges:

```text
latitude  = -90 to 90
longitude = -180 to 180
```

---

## Q3. Is lat/lon enough for nearby search?

No.

Lat/lon is only the raw data.

Nearby search needs:

```text
distance formula
candidate filtering
spatial index
ranking
```

---

## Q4. Why not scan all drivers?

For small N, scan works.

For millions of drivers:

```text
O(N)
```

is too slow.

Use:

```text
GeoHash
Grid index
QuadTree
RTree
S2 cells
PostGIS
Elasticsearch geo index
```

---

# 18. Common Bugs

## Bug 1 — Latitude and longitude swapped

Problem:

```text
lat = 26.102
lon = 44.437
```

Instead of:

```text
lat = 44.437
lon = 26.102
```

Fix:

```text
clear naming and validation
```

---

## Bug 2 — Invalid coordinate accepted

Problem:

```text
lat = 200
lon = 500
```

Fix:

```text
validate in GeoPoint constructor
```

---

## Bug 3 — Stale moving object location

Problem:

```text
driver stopped sending updates but still appears nearby
```

Fix:

```text
TTL for location
```

---

## Bug 4 — Metadata used as business logic

Problem:

```text
metadata string parsing everywhere
```

Fix:

```text
use structured metadata later
```

---

## Bug 5 — No updatedAt field

Problem:

```text
cannot know if location is fresh
```

Fix:

```text
store updatedAt
```

---

# 19. Current Limitations

Current phase supports:

```text
point creation
entity creation
entity storage
location update
basic validation
```

It does not support yet:

```text
distance calculation
nearby search
radius search
GeoHash
bounding box
grid index
TTL
moving object tracking
driver matching
Kafka location stream
production geo sharding
```

This is expected.

We add those one by one.

---

# 20. Next Step

Next file:

```text
002_Distance_Haversine.md
```

In the next phase, we move from:

```text
store locations
```

to:

```text
calculate distance between two locations
```

This is the first step toward:

```text
nearby search
nearest driver
restaurant search
delivery assignment
ETA estimation
```
