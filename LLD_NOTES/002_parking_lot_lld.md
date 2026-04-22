# Parking Lot Low-Level Design

## 1. Problem Overview
A parking lot is a designated area where vehicles can be parked temporarily. The system supports multiple floors, multiple vehicle types, automatic spot allocation, ticketing, and parking fee calculation.

This document focuses on:
- requirements
- entities and relationships
- design patterns
- class skeletons only

Implementation details are intentionally left out so you can code them yourself.

---

## 2. Clarified Requirements

### 2.1 Functional Requirements
- Support multiple parking floors.
- Each floor can have a configurable number of parking spots.
- Support vehicle types such as **Bike**, **Car**, and **Truck**.
- Support parking spot sizes such as **SMALL**, **MEDIUM**, and **LARGE**.
- Enforce compatibility rules between vehicles and spots.
- Automatically allocate a valid parking spot when a vehicle enters.
- Issue a parking ticket when a vehicle is parked.
- Track entry and exit timestamps.
- Calculate parking fees based on parking duration.
- Support pluggable fee strategies such as flat-rate, hourly, or vehicle-based pricing.
- Show real-time availability grouped by floor and spot size.
- Demo flow can be hardcoded in a driver class.

### 2.2 Non-Functional Requirements
- Follow object-oriented design principles.
- Keep responsibilities clearly separated.
- Design should be modular and extensible.
- Components should be testable in isolation.
- Handle concurrent park/unpark operations safely.
- Avoid race conditions in spot assignment.

---

## 3. Parking Rules

### Vehicle to Spot Compatibility
- **Bike** → SMALL, MEDIUM, LARGE
- **Car** → MEDIUM, LARGE
- **Truck** → LARGE only

> Note: This design uses `VehicleSize` as the core compatibility concept. Concrete vehicle classes map to a size.

---

## 4. Core Entities

### Enums
- `VehicleSize`

### Data / Domain Classes
- `Vehicle` (abstract)
- `Bike`
- `Car`
- `Truck`
- `ParkingTicket`
- `ParkingException`

### Interfaces
- `FeeStrategy`
- `SpotAllocationStrategy`

### Core Classes
- `ParkingSpot`
- `ParkingFloor`
- `ParkingLot`

---

## 5. Entity Responsibilities

| Entity | Type | Responsibility |
|---|---|---|
| `VehicleSize` | Enum | Represents size categories: SMALL, MEDIUM, LARGE |
| `Vehicle` | Abstract class | Common vehicle data such as license plate and size |
| `Bike`, `Car`, `Truck` | Concrete classes | Specialized vehicle types with predefined size |
| `ParkingSpot` | Core class | Manages occupancy and compatibility for one spot |
| `ParkingFloor` | Core class | Holds spots for one floor and provides availability queries |
| `ParkingTicket` | Data class | Tracks one parking session |
| `FeeStrategy` | Interface | Encapsulates fee calculation logic |
| `SpotAllocationStrategy` | Interface | Encapsulates spot-selection logic |
| `ParkingLot` | Core class | Main orchestrator for parking, unparking, ticketing, and availability |
| `ParkingException` | Exception | Represents invalid parking operations |

---

## 6. Class Relationships

### 6.1 Composition
- `ParkingLot` **contains** multiple `ParkingFloor` objects.
- `ParkingFloor` **contains** multiple `ParkingSpot` objects.

### 6.2 Association
- `ParkingSpot` **references** a parked `Vehicle`.
- `ParkingTicket` **references** a `Vehicle` and a `ParkingSpot`.
- `ParkingLot` **uses** `FeeStrategy`.
- `ParkingLot` **uses** `SpotAllocationStrategy`.

### 6.3 Implementation
- `HourlyFeeStrategy`, `FlatRateFeeStrategy`, `VehicleBasedFeeStrategy` **implement** `FeeStrategy`.
- `NearestFirstStrategy`, `BestFitStrategy` **implement** `SpotAllocationStrategy`.

---

## 7. Design Patterns Used

### 7.1 Strategy Pattern — Fee Calculation
Use `FeeStrategy` so fee rules can change without modifying `ParkingLot`.

Possible implementations:
- `FlatRateFeeStrategy`
- `HourlyFeeStrategy`
- `VehicleBasedFeeStrategy`

### 7.2 Strategy Pattern — Spot Allocation
Use `SpotAllocationStrategy` so spot assignment logic stays decoupled from core orchestration.

Possible implementations:
- `NearestFirstStrategy`
- `BestFitStrategy`

### 7.3 Singleton Pattern — ParkingLot
Use a singleton for `ParkingLot` when the system must maintain one shared state across all parking operations.

### 7.4 Facade Pattern — ParkingLot
`ParkingLot` acts as the main entry point for clients. External code does not need to directly manage floors, spots, or strategies.

---

## 8. High-Level Flow

### Vehicle Entry
1. Client sends a vehicle to `ParkingLot.parkVehicle(vehicle)`.
2. `ParkingLot` asks `SpotAllocationStrategy` to find a valid spot.
3. The selected `ParkingSpot` parks the vehicle.
4. `ParkingLot` creates a `ParkingTicket`.
5. Ticket is stored in active tickets map.
6. Ticket is returned.

### Vehicle Exit
1. Client calls `ParkingLot.unparkVehicle(ticketId)`.
2. `ParkingLot` finds the active ticket.
3. Exit time is recorded.
4. `FeeStrategy` calculates the fee.
5. The vehicle is removed from the `ParkingSpot`.
6. Ticket is removed from active tickets.
7. Fee is returned.

---

## 9. Suggested Java Class Skeletons

## 9.1 Enum

```java
public enum VehicleSize {
    SMALL,
    MEDIUM,
    LARGE
}
```

---

## 9.2 Custom Exception

```java
public class ParkingException extends RuntimeException {

    public ParkingException(String message) {
        super(message);
    }
}
```

---

## 9.3 Vehicle Hierarchy

```java
public abstract class Vehicle {
    private final String licensePlate;
    private final VehicleSize size;

    protected Vehicle(String licensePlate, VehicleSize size) {
        this.licensePlate = licensePlate;
        this.size = size;
    }

    public String getLicensePlate() {
        return licensePlate;
    }

    public VehicleSize getSize() {
        return size;
    }
}
```

```java
public class Bike extends Vehicle {

    public Bike(String licensePlate) {
        super(licensePlate, VehicleSize.SMALL);
    }
}
```

```java
public class Car extends Vehicle {

    public Car(String licensePlate) {
        super(licensePlate, VehicleSize.MEDIUM);
    }
}
```

```java
public class Truck extends Vehicle {

    public Truck(String licensePlate) {
        super(licensePlate, VehicleSize.LARGE);
    }
}
```

---

## 9.4 ParkingTicket

```java
import java.time.Duration;
import java.time.LocalDateTime;

public class ParkingTicket {
    private final String ticketId;
    private final Vehicle vehicle;
    private final ParkingSpot parkingSpot;
    private final LocalDateTime entryTime;
    private LocalDateTime exitTime;

    public ParkingTicket(String ticketId, Vehicle vehicle, ParkingSpot parkingSpot) {
        this.ticketId = ticketId;
        this.vehicle = vehicle;
        this.parkingSpot = parkingSpot;
        this.entryTime = LocalDateTime.now();
    }

    public String getTicketId() {
        return ticketId;
    }

    public Vehicle getVehicle() {
        return vehicle;
    }

    public ParkingSpot getParkingSpot() {
        return parkingSpot;
    }

    public LocalDateTime getEntryTime() {
        return entryTime;
    }

    public LocalDateTime getExitTime() {
        return exitTime;
    }

    public void setExitTime(LocalDateTime exitTime) {
        this.exitTime = exitTime;
    }

    public long getDurationInHours() {
        // TODO: implement
        return 0;
    }
}
```

---

## 9.5 Fee Strategy

```java
public interface FeeStrategy {
    double calculateFee(ParkingTicket ticket);
}
```

```java
public class FlatRateFeeStrategy implements FeeStrategy {

    @Override
    public double calculateFee(ParkingTicket ticket) {
        // TODO: implement
        return 0;
    }
}
```

```java
public class HourlyFeeStrategy implements FeeStrategy {

    @Override
    public double calculateFee(ParkingTicket ticket) {
        // TODO: implement
        return 0;
    }
}
```

```java
public class VehicleBasedFeeStrategy implements FeeStrategy {

    @Override
    public double calculateFee(ParkingTicket ticket) {
        // TODO: implement
        return 0;
    }
}
```

---

## 9.6 Spot Allocation Strategy

```java
import java.util.List;

public interface SpotAllocationStrategy {
    ParkingSpot findSpot(List<ParkingFloor> floors, VehicleSize vehicleSize);
}
```

```java
import java.util.List;

public class NearestFirstStrategy implements SpotAllocationStrategy {

    @Override
    public ParkingSpot findSpot(List<ParkingFloor> floors, VehicleSize vehicleSize) {
        // TODO: implement
        return null;
    }
}
```

```java
import java.util.List;

public class BestFitStrategy implements SpotAllocationStrategy {

    @Override
    public ParkingSpot findSpot(List<ParkingFloor> floors, VehicleSize vehicleSize) {
        // TODO: implement
        return null;
    }
}
```

---

## 9.7 ParkingSpot

```java
public class ParkingSpot {
    private final String spotId;
    private final VehicleSize size;
    private Vehicle parkedVehicle;

    public ParkingSpot(String spotId, VehicleSize size) {
        this.spotId = spotId;
        this.size = size;
    }

    public String getSpotId() {
        return spotId;
    }

    public VehicleSize getSize() {
        return size;
    }

    public Vehicle getParkedVehicle() {
        return parkedVehicle;
    }

    public synchronized boolean isAvailable() {
        return parkedVehicle == null;
    }

    public boolean canFitVehicle(VehicleSize vehicleSize) {
        // TODO: implement
        return false;
    }

    public synchronized void parkVehicle(Vehicle vehicle) {
        // TODO: implement
    }

    public synchronized Vehicle unparkVehicle() {
        // TODO: implement
        return null;
    }
}
```

---

## 9.8 ParkingFloor

```java
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ParkingFloor {
    private final int floorNumber;
    private final List<ParkingSpot> spots;

    public ParkingFloor(int floorNumber, Map<VehicleSize, Integer> spotCounts) {
        this.floorNumber = floorNumber;
        this.spots = new ArrayList<>();
        // TODO: create spots based on configuration
    }

    public int getFloorNumber() {
        return floorNumber;
    }

    public List<ParkingSpot> getSpots() {
        return spots;
    }

    public ParkingSpot findAvailableSpot(VehicleSize vehicleSize) {
        // TODO: implement
        return null;
    }

    public int getAvailableSpotCount(VehicleSize vehicleSize) {
        // TODO: implement
        return 0;
    }
}
```

---

## 9.9 ParkingLot

```java
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class ParkingLot {
    private static volatile ParkingLot instance;
    private static final Object LOCK = new Object();

    private final List<ParkingFloor> floors;
    private final Map<String, ParkingTicket> activeTickets;
    private final FeeStrategy feeStrategy;
    private final SpotAllocationStrategy allocationStrategy;

    private ParkingLot(List<ParkingFloor> floors,
                       FeeStrategy feeStrategy,
                       SpotAllocationStrategy allocationStrategy) {
        this.floors = floors;
        this.feeStrategy = feeStrategy;
        this.allocationStrategy = allocationStrategy;
        this.activeTickets = new ConcurrentHashMap<>();
    }

    public static ParkingLot getInstance(List<ParkingFloor> floors,
                                         FeeStrategy feeStrategy,
                                         SpotAllocationStrategy allocationStrategy) {
        if (instance == null) {
            synchronized (LOCK) {
                if (instance == null) {
                    instance = new ParkingLot(floors, feeStrategy, allocationStrategy);
                }
            }
        }
        return instance;
    }

    public ParkingTicket parkVehicle(Vehicle vehicle) {
        // TODO: implement
        return null;
    }

    public double unparkVehicle(String ticketId) {
        // TODO: implement
        return 0;
    }

    public void displayAvailability() {
        // TODO: implement
    }

    public List<ParkingFloor> getFloors() {
        return floors;
    }

    public Map<String, ParkingTicket> getActiveTickets() {
        return activeTickets;
    }
}
```

---

## 9.10 Driver Class

```java
import java.util.List;
import java.util.Map;

public class ParkingLotDemo {

    public static void main(String[] args) {
        ParkingFloor floor1 = new ParkingFloor(1, Map.of(
                VehicleSize.SMALL, 2,
                VehicleSize.MEDIUM, 3,
                VehicleSize.LARGE, 1
        ));

        ParkingFloor floor2 = new ParkingFloor(2, Map.of(
                VehicleSize.SMALL, 2,
                VehicleSize.MEDIUM, 2,
                VehicleSize.LARGE, 2
        ));

        ParkingLot parkingLot = ParkingLot.getInstance(
                List.of(floor1, floor2),
                new HourlyFeeStrategy(),
                new BestFitStrategy()
        );

        Vehicle bike = new Bike("BIKE-101");
        Vehicle car = new Car("CAR-201");
        Vehicle truck = new Truck("TRUCK-301");

        // TODO: simulate parking and unparking flow
    }
}
```

---

## 10. Important Design Notes

### Why use `VehicleSize` instead of `VehicleType` in core parking logic?
Because parking compatibility depends on physical size, not label. This makes the design more extensible for future vehicle categories.

### Why use strategy interfaces?
Because fee rules and allocation rules are likely to change independently of the rest of the system.

### Why synchronize `ParkingSpot` operations?
Because two concurrent park requests must not occupy the same spot.

### Why use `ConcurrentHashMap` for active tickets?
Because park and unpark operations can happen concurrently.

---

## 11. Possible Extensions
- Reservation support
- Entry and exit gates
- Payment service integration
- Spot release timeout
- Lost ticket handling
- Display board per floor
- Admin APIs
- Different rates for weekends or peak hours
- EV charging spots
- Handicapped spots
- Notification service

---

## 12. Summary
This design keeps the parking lot system modular and extensible by separating:
- vehicle modeling
- spot management
- floor organization
- ticket lifecycle
- fee calculation
- allocation logic

The main extensibility points are:
- `FeeStrategy`
- `SpotAllocationStrategy`
- concrete `Vehicle` types

You can now implement the internal logic without changing the overall structure.
