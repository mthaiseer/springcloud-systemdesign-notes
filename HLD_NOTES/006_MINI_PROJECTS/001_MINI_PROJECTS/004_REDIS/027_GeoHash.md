# MiniRedis Phase 27 — GeoHash (Rich Version)

# Clickable Index

- [1. Goal](#1-goal)
- [2. Why GeoHash Exists](#2-why-geohash-exists)
- [3. Problem With Naive Nearby Search](#3-problem-with-naive-nearby-search)
- [4. GeoHash Mental Model](#4-geohash-mental-model)
- [5. Grid Cell Search Explained](#5-grid-cell-search-explained)
- [6. GEOADD And GEORADIUS Flow](#6-geoadd-and-georadius-flow)
- [7. Deep Internal Data Structure Explanation](#7-deep-internal-data-structure-explanation)
- [8. Complete Java Code](#8-complete-java-code)
- [9. Step-by-Step Dry Run](#9-step-by-step-dry-run)
- [10. Internal Memory Visualization](#10-internal-memory-visualization)
- [11. Complexity Analysis](#11-complexity-analysis)
- [12. Real Production Use Cases](#12-real-production-use-cases)
- [13. Redis Production Internals](#13-redis-production-internals)
- [14. Failure Cases And Bottlenecks](#14-failure-cases-and-bottlenecks)
- [15. Interview Questions](#15-interview-questions)
- [16. Final Mental Model](#16-final-mental-model)

---

# 1. Goal

In this phase, we build:

```text
GeoHash / Geospatial Index
```

Main objective:

```text
Store latitude-longitude points
and find nearby objects efficiently.
```

Mental model:

```text
2D world map
   ->
small searchable grid cells
```

Example use case:

```text
Find nearby drivers around user location.
```

Commands conceptually introduced:

```text
GEOADD
GEORADIUS
```

Example:

```text
GEOADD drivers driver-1 44.437 26.102
GEOADD drivers driver-2 44.438 26.103

GEORADIUS drivers 44.437 26.102
```

Expected result:

```text
nearby driver IDs
```

Production systems using this idea:

```text
Uber
Bolt
Food delivery
Nearby restaurants
Location search
Maps
Fleet tracking
```

---

# 2. Why GeoHash Exists

Normal Redis key-value lookup answers:

```text
GET user:1
```

But location systems ask:

```text
Find all drivers within 2 km.
```

This is not simple key lookup.

It is a:

```text
spatial query
```

Spatial query needs:

```text
latitude
longitude
distance
nearby search
```

Without spatial indexing:

```text
scan all drivers
calculate distance for each driver
return nearby ones
```

This becomes slow at scale.

Example:

```text
1 million drivers
```

Naive nearby search:

```text
calculate distance 1 million times
```

Too expensive.

GeoHash solves this by grouping nearby coordinates into:

```text
cells
```

Then search only:

```text
same cell + neighboring cells
```

---

# 3. Problem With Naive Nearby Search

Suppose we store:

```text
driverId -> lat/lon
```

Example:

```text
driver-1 -> 44.437, 26.102
driver-2 -> 44.438, 26.103
driver-3 -> 45.000, 27.000
```

User is at:

```text
44.437, 26.102
```

Naive search:

```text
for every driver:
    calculate distance
```

Complexity:

```text
O(n)
```

If:

```text
n = 10 million
```

then every nearby search becomes very expensive.

Problems:

```text
high CPU
high latency
bad p99
poor scalability
```

GeoHash reduces candidate set.

Instead of checking all drivers:

```text
check only drivers in nearby cells
```

---

# 4. GeoHash Mental Model

GeoHash converts:

```text
latitude + longitude
```

into:

```text
cell ID
```

Example:

```text
44.437, 26.102
```

becomes:

```text
4443:2610
```

In this MiniRedis phase, we use simplified grid cells:

```java
int latCell = (int) (lat * 100);
int lonCell = (int) (lon * 100);
```

So:

```text
44.437 * 100 = 4443
26.102 * 100 = 2610
```

Cell:

```text
4443:2610
```

All points inside same small geographic area share same cell.

Mental model:

```text
world map split into boxes
```

Nearby search:

```text
find the user's box
return objects inside that box
```

Later production version:

```text
also search neighboring boxes
```

---

# 5. Grid Cell Search Explained

Imagine map as grid:

```text
+---------+---------+---------+
|  cell A |  cell B |  cell C |
+---------+---------+---------+
|  cell D |  USER   |  cell F |
+---------+---------+---------+
|  cell G |  cell H |  cell I |
+---------+---------+---------+
```

If user is in center cell:

```text
search center cell
```

But true radius search should also check:

```text
8 neighboring cells
```

Because nearby object may be just across boundary.

Simple MiniRedis version:

```text
same cell only
```

Production version:

```text
same cell + adjacent cells + distance filtering
```

Two-step pattern:

```text
1. candidate filtering by cell
2. exact distance check
```

This is extremely common in scalable geo systems.

---

# 6. GEOADD And GEORADIUS Flow

# GEOADD

Command:

```text
GEOADD drivers driver-1 44.437 26.102
```

Execution:

```text
1. compute grid cell
2. create cell bucket if missing
3. store point in bucket
```

Memory:

```text
cells
 └── 4443:2610
      └── driver-1
```

---

# GEORADIUS

Command:

```text
GEORADIUS drivers 44.437 26.102
```

Execution:

```text
1. compute query cell
2. fetch candidates from cell
3. return candidate IDs
```

MiniRedis result:

```text
drivers in same cell
```

Production result:

```text
drivers within actual distance
```

---

# 7. Deep Internal Data Structure Explanation

MiniRedis implementation:

```java
Map<String, List<Point>>
```

Meaning:

```text
cellId -> points inside that cell
```

Example:

```text
cells
 ├── 4443:2610 -> [driver-1, driver-2]
 └── 4500:2700 -> [driver-3]
```

---

# Point Object

Each point stores:

```text
id
latitude
longitude
```

Why store lat/lon again?

Because after candidate filtering:

```text
we may calculate exact distance
```

Cell only gives approximate location.

Exact lat/lon gives accurate distance.

---

# Why HashMap?

Because we need:

```text
fast cell lookup
```

Complexity:

```text
cell lookup = O(1)
```

---

# Why List?

Because many points can exist in same cell.

Example:

```text
100 drivers in same city block
```

So:

```text
cell -> list of drivers
```

---

# Limitation Of Same-Cell Only Search

If driver is in neighboring cell but physically close:

```text
same-cell search misses it
```

Production fix:

```text
search neighboring cells too
then filter by Haversine distance
```

---

# 8. Complete Java Code

## 8.1 GeoPoint.java

### Logic Before Code

A point represents one geo object:

```text
driver
restaurant
user
warehouse
```

It stores:

```text
id + latitude + longitude
```

```java
package com.miniredis.geo;

/**
 * GeoPoint represents one location object.
 */
public class GeoPoint {

    public final String id;
    public final double lat;
    public final double lon;

    public GeoPoint(
            String id,
            double lat,
            double lon
    ) {
        this.id = id;
        this.lat = lat;
        this.lon = lon;
    }
}
```

---

## 8.2 GeoIndex.java

### Logic Before Code

GeoIndex stores geo points inside grid cells.

Core responsibilities:

```text
1. convert lat/lon to cell
2. add point into cell
3. search nearby points
```

```java
package com.miniredis.geo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Simplified GeoHash / grid-cell index.
 */
public class GeoIndex {

    /**
     * cellId -> list of geo points
     */
    private final Map<String, List<GeoPoint>> cells =
            new HashMap<>();

    /**
     * GEOADD key id lat lon
     *
     * Adds a geo object into a grid cell.
     */
    public void add(
            String id,
            double lat,
            double lon
    ) {

        String cellId =
                cell(lat, lon);

        List<GeoPoint> points =
                cells.computeIfAbsent(
                        cellId,
                        c -> new ArrayList<>()
                );

        points.add(
                new GeoPoint(
                        id,
                        lat,
                        lon
                )
        );
    }

    /**
     * GEORADIUS simplified.
     *
     * Returns IDs from same cell.
     */
    public List<String> radius(
            double lat,
            double lon
    ) {

        String cellId =
                cell(lat, lon);

        List<String> result =
                new ArrayList<>();

        List<GeoPoint> candidates =
                cells.getOrDefault(
                        cellId,
                        List.of()
                );

        for (GeoPoint point : candidates) {

            result.add(point.id);
        }

        return result;
    }

    /**
     * Convert latitude/longitude to simplified cell.
     *
     * Example:
     * lat = 44.437 -> 4443
     * lon = 26.102 -> 2610
     *
     * cell = 4443:2610
     */
    private String cell(
            double lat,
            double lon
    ) {

        int latCell =
                (int) (lat * 100);

        int lonCell =
                (int) (lon * 100);

        return latCell + ":" + lonCell;
    }

    /**
     * Debug helper.
     */
    public Map<String, List<GeoPoint>> snapshot() {

        return new HashMap<>(cells);
    }
}
```

---

## 8.3 Phase027Driver.java

### Logic Before Code

This driver simulates:

```text
nearby driver search
```

```java
package com.miniredis.driver;

import com.miniredis.geo.GeoIndex;

public class Phase027Driver {

    public static void main(String[] args) {

        GeoIndex geo =
                new GeoIndex();

        // --------------------------------
        // ADD DRIVERS
        // --------------------------------

        geo.add(
                "driver-1",
                44.437,
                26.102
        );

        geo.add(
                "driver-2",
                44.438,
                26.103
        );

        geo.add(
                "driver-3",
                45.000,
                27.000
        );

        // --------------------------------
        // QUERY NEARBY
        // --------------------------------

        System.out.println(
                "Nearby = "
                        + geo.radius(
                        44.437,
                        26.102
                )
        );
    }
}
```

---

# 9. Step-by-Step Dry Run

## Step 1 — Add driver-1

Code:

```java
geo.add(
    "driver-1",
    44.437,
    26.102
);
```

Cell calculation:

```text
latCell = int(44.437 * 100) = 4443
lonCell = int(26.102 * 100) = 2610
```

Cell ID:

```text
4443:2610
```

Memory:

```text
cells
 └── 4443:2610
      └── driver-1
```

---

## Step 2 — Add driver-2

Code:

```java
geo.add(
    "driver-2",
    44.438,
    26.103
);
```

Cell calculation:

```text
latCell = int(44.438 * 100) = 4443
lonCell = int(26.103 * 100) = 2610
```

Same cell:

```text
4443:2610
```

Memory:

```text
cells
 └── 4443:2610
      ├── driver-1
      └── driver-2
```

---

## Step 3 — Add driver-3

Code:

```java
geo.add(
    "driver-3",
    45.000,
    27.000
);
```

Cell:

```text
4500:2700
```

Memory:

```text
cells
 ├── 4443:2610
 │    ├── driver-1
 │    └── driver-2
 │
 └── 4500:2700
      └── driver-3
```

---

## Step 4 — Query nearby drivers

Code:

```java
geo.radius(
    44.437,
    26.102
);
```

Query cell:

```text
4443:2610
```

Lookup:

```text
cells["4443:2610"]
```

Result:

```text
[driver-1, driver-2]
```

driver-3 is not returned because:

```text
different cell
```

---

# 10. Internal Memory Visualization

```text
GeoIndex

cells
 ├── 4443:2610
 │    ├── driver-1 (44.437, 26.102)
 │    └── driver-2 (44.438, 26.103)
 │
 └── 4500:2700
      └── driver-3 (45.000, 27.000)
```

---

# 11. Complexity Analysis

| Operation | Complexity | Reason |
|---|---|---|
| GEOADD | O(1) average | HashMap cell lookup |
| GEORADIUS same cell | O(k) | k points in matched cell |
| Naive search | O(n) | scan all points |

Where:

```text
k = points in one cell
n = total points
```

Goal:

```text
k << n
```

---

# 12. Real Production Use Cases

## Uber / Bolt Nearby Drivers

```text
find drivers around rider
```

## Food Delivery

```text
nearby restaurants
nearby couriers
```

## Fleet Tracking

```text
vehicles in region
```

## Location-Based Ads

```text
users near shop
```

## Emergency Dispatch

```text
nearest ambulance
nearest police unit
```

---

# 13. Redis Production Internals

Real Redis GEO commands include:

```text
GEOADD
GEODIST
GEOHASH
GEOPOS
GEOSEARCH
```

Redis internally stores geospatial data using:

```text
sorted sets
```

Redis encodes coordinates into:

```text
geohash score
```

Then it uses sorted set range queries to find candidates.

Production search does:

```text
1. encode location
2. find candidate ranges
3. compute exact distance
4. filter final result
```

MiniRedis version:

```text
HashMap grid cells
```

Real Redis version:

```text
ZSet + geohash encoding
```

---

# 14. Failure Cases And Bottlenecks

## Problem 1 — Boundary Miss

Object is physically nearby but in neighboring cell.

Fix:

```text
search adjacent cells
```

## Problem 2 — Dense Cell

City center may contain many drivers.

Result:

```text
large candidate list
```

Fix:

```text
smaller cells
higher precision
secondary filtering
```

## Problem 3 — Moving Objects

Drivers constantly update location.

Problem:

```text
old cell must be cleaned
```

Fix:

```text
id -> current cell map
remove old location before insert
```

## Problem 4 — Exact Distance Needed

Same cell does not guarantee within radius.

Fix:

```text
Haversine distance
```

## Problem 5 — Hot Area

Many queries in one area.

Fix:

```text
replication
local cache
precomputed nearby buckets
```

---

# 15. Interview Questions

## Q1

Why not scan all points?

Answer:

```text
O(n) does not scale for millions of locations
```

## Q2

What is GeoHash?

Answer:

```text
encoding lat/lon into searchable spatial cell
```

## Q3

Why search neighboring cells?

Answer:

```text
nearby points may cross cell boundary
```

## Q4

Why still calculate exact distance?

Answer:

```text
cell match gives candidates only, not exact radius
```

## Q5

How does Redis store GEO internally?

Answer:

```text
as sorted set with encoded geohash score
```

---

# 16. Final Mental Model

```text
Location search
   -> reduce 2D space into cells
   -> search small candidate set
   -> exact distance filter
```

GeoHash teaches:

```text
spatial hashing
candidate filtering
nearby search
Uber-style backend design
Redis GEO internals
```
