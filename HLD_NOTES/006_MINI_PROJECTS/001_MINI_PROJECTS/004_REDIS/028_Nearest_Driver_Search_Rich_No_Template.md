# MiniRedis Phase 28 — Nearest Driver Search (Rich Version)

# Clickable Index

- [1. Goal](#1-goal)
- [2. Why Nearest Driver Search Exists](#2-why-nearest-driver-search-exists)
- [3. Product-Level Matching Problem](#3-product-level-matching-problem)
- [4. Nearest Driver Mental Model](#4-nearest-driver-mental-model)
- [5. GeoHash Candidate Filtering](#5-geohash-candidate-filtering)
- [6. Distance Sorting Explained](#6-distance-sorting-explained)
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
Nearest Driver Search
```

Main objective:

```text
Find the closest available drivers
near a rider location.
```

This phase builds on:

```text
GeoHash / grid-cell indexing
```

but now adds a product-level workflow:

```text
driver updates location
rider requests ride
system finds nearby candidate drivers
system sorts candidates by distance
system returns nearest N drivers
```

Real-world analogy:

```text
Rider opens Uber/Bolt app.

System needs:
which drivers are closest right now?
```

Core operations:

```text
updateDriverLocation(driverId, lat, lon)
findNearestDrivers(riderLat, riderLon, limit)
```

Production systems using this idea:

```text
Uber
Bolt
Lyft
Grab
Food delivery
Courier dispatch
Emergency vehicle dispatch
```

---

# 2. Why Nearest Driver Search Exists

GeoHash alone only answers:

```text
which drivers are in this cell?
```

But real product needs:

```text
which drivers are nearest?
```

Example:

```text
driver-1 -> 100 meters away
driver-2 -> 700 meters away
driver-3 -> 2 km away
```

A ride-matching system should prefer:

```text
closest available driver
```

But distance alone is not enough.

Real systems also consider:

```text
driver availability
ETA
vehicle type
driver rating
cancellation history
pricing surge zone
traffic
current trip state
```

This MiniRedis phase focuses on the core foundation:

```text
location + distance + nearest sorting
```

---

# 3. Product-Level Matching Problem

Naive approach:

```text
scan every driver
calculate distance
sort all drivers
return nearest
```

If system has:

```text
1 million drivers
```

Every rider request becomes:

```text
1 million distance calculations
```

This is too slow.

Better approach:

```text
1. use GeoHash/grid cell to find candidates
2. compute exact distance only for candidates
3. sort candidates
4. return nearest N
```

This is a classic large-scale system design pattern:

```text
filter first
rank second
```

GeoHash:

```text
reduces search space
```

Distance sorting:

```text
improves result quality
```

---

# 4. Nearest Driver Mental Model

Architecture:

```text
Driver App
   |
   | location update
   v
Geo Driver Index

Rider App
   |
   | find nearby
   v
Candidate Search
   |
   v
Distance Sort
   |
   v
Nearest Drivers
```

Flow:

```text
1. driver sends GPS location
2. server stores driver in geo index
3. rider requests nearby drivers
4. server finds candidate drivers from nearby cell
5. server calculates distance
6. server sorts by distance
7. server returns nearest N
```

Important:

```text
GeoHash does not replace distance calculation.
```

GeoHash only gives:

```text
candidate set
```

Distance formula gives:

```text
actual ranking
```

---

# 5. GeoHash Candidate Filtering

MiniRedis uses simplified cells:

```text
cell = int(lat * 100) + ":" + int(lon * 100)
```

Example:

```text
44.437, 26.102
```

becomes:

```text
4443:2610
```

Drivers in same cell are likely nearby.

Example:

```text
cells
 └── 4443:2610
      ├── driver-1
      ├── driver-2
      └── driver-5
```

Rider query in same cell:

```text
44.437, 26.102
```

Candidate drivers:

```text
driver-1
driver-2
driver-5
```

Production version searches:

```text
same cell + neighboring cells
```

because nearest driver may be across boundary.

---

# 6. Distance Sorting Explained

After candidate filtering, we compute distance.

For learning, this phase uses simple Euclidean approximation:

```text
distance = sqrt((lat1-lat2)^2 + (lon1-lon2)^2)
```

This is good for concept.

Production systems use:

```text
Haversine distance
road network ETA
traffic-aware routing
```

Sorting:

```text
nearest first
```

Example:

```text
driver-1 distance 0.001
driver-2 distance 0.005
driver-3 distance 0.020
```

Result:

```text
driver-1
driver-2
driver-3
```

If limit is:

```text
2
```

return:

```text
driver-1
driver-2
```

---

# 7. Deep Internal Data Structure Explanation

MiniRedis implementation uses:

```text
Map<String, List<DriverLocation>>
```

Meaning:

```text
cellId -> drivers in that cell
```

Also useful in production:

```text
Map<String, String> driverToCell
```

Why?

Because drivers move.

When driver updates location:

```text
old cell must remove driver
new cell must add driver
```

If not removed:

```text
same driver appears in multiple cells
```

This causes wrong nearby results.

---

# DriverLocation Object

Stores:

```text
driverId
latitude
longitude
```

We keep lat/lon because:

```text
distance sorting needs exact coordinates
```

Cell is only approximate.

---

# Candidate Object

Stores:

```text
driverId
distance
```

This is useful for sorting.

---

# Data Structure Summary

```text
cells:
  cellId -> List<DriverLocation>

driverCells:
  driverId -> currentCellId
```

This is closer to real production thinking than simple GEOADD.

---

# 8. Complete Java Code

## 8.1 DriverLocation.java

### Logic Before Code

Represents one live driver position.

```java
package com.miniredis.geo;

/**
 * Stores current location of one driver.
 */
public class DriverLocation {

    public final String driverId;
    public final double lat;
    public final double lon;

    public DriverLocation(
            String driverId,
            double lat,
            double lon
    ) {
        this.driverId = driverId;
        this.lat = lat;
        this.lon = lon;
    }
}
```

---

## 8.2 DriverCandidate.java

### Logic Before Code

Represents one nearby candidate after distance calculation.

```java
package com.miniredis.geo;

/**
 * Candidate driver with calculated distance.
 */
public class DriverCandidate {

    public final String driverId;
    public final double distance;

    public DriverCandidate(
            String driverId,
            double distance
    ) {
        this.driverId = driverId;
        this.distance = distance;
    }

    @Override
    public String toString() {
        return driverId + " distance=" + distance;
    }
}
```

---

## 8.3 NearestDriverIndex.java

### Logic Before Code

This class does the core product workflow:

```text
1. update driver location
2. remove stale old location
3. search candidates in rider cell
4. calculate distance
5. sort nearest first
6. return top N
```

```java
package com.miniredis.geo;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Simplified Uber-style nearest driver index.
 */
public class NearestDriverIndex {

    /**
     * cellId -> drivers inside that cell
     */
    private final Map<String, List<DriverLocation>> cells =
            new HashMap<>();

    /**
     * driverId -> current cellId
     *
     * Needed to remove old location when driver moves.
     */
    private final Map<String, String> driverToCell =
            new HashMap<>();

    /**
     * Update driver location.
     */
    public void updateDriverLocation(
            String driverId,
            double lat,
            double lon
    ) {

        String newCell =
                cell(lat, lon);

        String oldCell =
                driverToCell.get(driverId);

        // --------------------------------
        // STEP 1
        // REMOVE DRIVER FROM OLD CELL
        // --------------------------------

        if (oldCell != null) {

            List<DriverLocation> oldDrivers =
                    cells.get(oldCell);

            if (oldDrivers != null) {

                oldDrivers.removeIf(
                        d -> d.driverId.equals(driverId)
                );
            }
        }

        // --------------------------------
        // STEP 2
        // ADD DRIVER TO NEW CELL
        // --------------------------------

        List<DriverLocation> drivers =
                cells.computeIfAbsent(
                        newCell,
                        c -> new ArrayList<>()
                );

        drivers.add(
                new DriverLocation(
                        driverId,
                        lat,
                        lon
                )
        );

        driverToCell.put(
                driverId,
                newCell
        );
    }

    /**
     * Find nearest drivers from same cell.
     */
    public List<DriverCandidate> findNearestDrivers(
            double riderLat,
            double riderLon,
            int limit
    ) {

        String riderCell =
                cell(riderLat, riderLon);

        List<DriverLocation> candidates =
                cells.getOrDefault(
                        riderCell,
                        List.of()
                );

        List<DriverCandidate> result =
                new ArrayList<>();

        // --------------------------------
        // STEP 1
        // CALCULATE DISTANCE FOR CANDIDATES
        // --------------------------------

        for (DriverLocation driver : candidates) {

            double dist =
                    distance(
                            riderLat,
                            riderLon,
                            driver.lat,
                            driver.lon
                    );

            result.add(
                    new DriverCandidate(
                            driver.driverId,
                            dist
                    )
            );
        }

        // --------------------------------
        // STEP 2
        // SORT BY DISTANCE ASCENDING
        // --------------------------------

        result.sort(
                Comparator.comparingDouble(
                        c -> c.distance
                )
        );

        // --------------------------------
        // STEP 3
        // RETURN TOP N
        // --------------------------------

        if (result.size() > limit) {

            return new ArrayList<>(
                    result.subList(
                            0,
                            limit
                    )
            );
        }

        return result;
    }

    /**
     * Simplified distance formula.
     *
     * Good for learning.
     * Production should use Haversine or ETA.
     */
    private double distance(
            double lat1,
            double lon1,
            double lat2,
            double lon2
    ) {

        double dLat =
                lat1 - lat2;

        double dLon =
                lon1 - lon2;

        return Math.sqrt(
                dLat * dLat
                        + dLon * dLon
        );
    }

    /**
     * Convert coordinates into grid cell.
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
    public Map<String, List<DriverLocation>> snapshot() {
        return new HashMap<>(cells);
    }
}
```

---

## 8.4 Phase028Driver.java

### Logic Before Code

This driver simulates:

```text
driver location updates
rider search
nearest N result
driver movement
```

```java
package com.miniredis.driver;

import com.miniredis.geo.NearestDriverIndex;

public class Phase028Driver {

    public static void main(String[] args) {

        NearestDriverIndex index =
                new NearestDriverIndex();

        // --------------------------------
        // DRIVERS SEND LOCATION UPDATES
        // --------------------------------

        index.updateDriverLocation(
                "driver-1",
                44.437,
                26.102
        );

        index.updateDriverLocation(
                "driver-2",
                44.438,
                26.103
        );

        index.updateDriverLocation(
                "driver-3",
                44.439,
                26.104
        );

        index.updateDriverLocation(
                "driver-4",
                45.000,
                27.000
        );

        // --------------------------------
        // RIDER SEARCHES NEAREST 2 DRIVERS
        // --------------------------------

        System.out.println(
                "nearest drivers = "
                        + index.findNearestDrivers(
                        44.437,
                        26.102,
                        2
                )
        );

        // --------------------------------
        // DRIVER-1 MOVES TO DIFFERENT AREA
        // --------------------------------

        index.updateDriverLocation(
                "driver-1",
                45.000,
                27.000
        );

        System.out.println(
                "nearest after driver-1 moved = "
                        + index.findNearestDrivers(
                        44.437,
                        26.102,
                        3
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
index.updateDriverLocation(
    "driver-1",
    44.437,
    26.102
);
```

Cell:

```text
4443:2610
```

Memory:

```text
cells
 └── 4443:2610
      └── driver-1

driverToCell
 └── driver-1 -> 4443:2610
```

---

## Step 2 — Add driver-2

Code:

```java
index.updateDriverLocation(
    "driver-2",
    44.438,
    26.103
);
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

## Step 3 — Add driver-4 Far Away

Code:

```java
index.updateDriverLocation(
    "driver-4",
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
 │    ├── driver-2
 │    └── driver-3
 │
 └── 4500:2700
      └── driver-4
```

---

## Step 4 — Rider Searches Nearest 2

Code:

```java
index.findNearestDrivers(
    44.437,
    26.102,
    2
);
```

Query cell:

```text
4443:2610
```

Candidates:

```text
driver-1
driver-2
driver-3
```

Distance calculation:

```text
driver-1:
sqrt((44.437-44.437)^2 + (26.102-26.102)^2)
= 0

driver-2:
sqrt((44.437-44.438)^2 + (26.102-26.103)^2)
≈ 0.00141

driver-3:
sqrt((44.437-44.439)^2 + (26.102-26.104)^2)
≈ 0.00282
```

Sorted:

```text
driver-1
driver-2
driver-3
```

Limit:

```text
2
```

Result:

```text
driver-1
driver-2
```

---

## Step 5 — Driver-1 Moves

Code:

```java
index.updateDriverLocation(
    "driver-1",
    45.000,
    27.000
);
```

Execution:

```text
1. old cell = 4443:2610
2. remove driver-1 from old cell
3. new cell = 4500:2700
4. add driver-1 to new cell
```

Memory:

```text
cells
 ├── 4443:2610
 │    ├── driver-2
 │    └── driver-3
 │
 └── 4500:2700
      ├── driver-4
      └── driver-1
```

Now rider query near old location returns:

```text
driver-2
driver-3
```

driver-1 is no longer nearby.

---

# 10. Internal Memory Visualization

```text
NearestDriverIndex

cells
 ├── 4443:2610
 │    ├── driver-1 (44.437, 26.102)
 │    ├── driver-2 (44.438, 26.103)
 │    └── driver-3 (44.439, 26.104)
 │
 └── 4500:2700
      └── driver-4 (45.000, 27.000)

driverToCell
 ├── driver-1 -> 4443:2610
 ├── driver-2 -> 4443:2610
 ├── driver-3 -> 4443:2610
 └── driver-4 -> 4500:2700
```

---

# 11. Complexity Analysis

| Operation | Complexity | Reason |
|---|---|---|
| Update location | O(k) | remove from old cell list |
| Candidate lookup | O(1) | HashMap cell lookup |
| Distance calculation | O(k) | k candidates |
| Sorting | O(k log k) | nearest ordering |
| Top N extraction | O(n) small | return limit |

Where:

```text
k = drivers inside candidate cell
```

Production optimization:

```text
priority queue for top K
neighbor cell search
Haversine distance
driver availability filter
```

---

# 12. Real Production Use Cases

## Ride Matching

Find closest available driver.

## Courier Assignment

Find nearest delivery partner.

## Emergency Dispatch

Find nearest ambulance or police unit.

## Fleet Management

Find nearest truck or technician.

## Food Delivery

Find courier nearest to restaurant.

---

# 13. Redis Production Internals

Redis GEO commands are built on:

```text
sorted sets
```

Real Redis stores coordinates as:

```text
geohash encoded score
```

Nearby search flow:

```text
1. compute geohash ranges
2. fetch candidates from sorted set
3. calculate distance
4. sort/filter result
```

MiniRedis version:

```text
grid cell HashMap
distance sorting
nearest N
```

Production matching systems add:

```text
ETA service
driver state
dispatch rules
pricing
traffic
routing engine
fairness
```

---

# 14. Failure Cases And Bottlenecks

## Problem 1 — Same Cell Only Misses Nearby Drivers

Driver may be in neighboring cell.

Fix:

```text
search adjacent cells
```

## Problem 2 — Dense City Center

Too many drivers in one cell.

Fix:

```text
smaller cell precision
multi-level geohash
top-K heap
```

## Problem 3 — Driver Movement

Driver old location may remain if not removed.

Fix:

```text
driverToCell map
```

## Problem 4 — Distance Is Not ETA

Closest by straight line may not be fastest.

Fix:

```text
road network ETA
traffic-aware routing
```

## Problem 5 — Availability State

Nearby driver may already be busy.

Fix:

```text
filter available drivers only
```

---

# 15. Interview Questions

## Q1

Why not scan all drivers?

Answer:

```text
O(n) is too slow at large scale
```

## Q2

Why GeoHash before distance sorting?

Answer:

```text
reduce candidate set first
```

## Q3

Why keep driverToCell map?

Answer:

```text
remove old location when driver moves
```

## Q4

Why exact distance still needed?

Answer:

```text
cell match is approximate
```

## Q5

What is missing for production Uber matching?

Answer:

```text
neighbor cells
ETA
availability
traffic
fairness
pricing
driver acceptance probability
```

---

# 16. Final Mental Model

```text
GeoHash
   -> candidate filtering

Distance sorting
   -> nearest ranking

Driver state
   -> product correctness
```

Nearest driver search teaches:

```text
spatial indexing
candidate generation
ranking
top-K search
Uber-style system design
Redis GEO usage
```
