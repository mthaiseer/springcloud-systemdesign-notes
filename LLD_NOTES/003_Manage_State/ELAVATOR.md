# Elevator System LLD Reference

> Visual, interview-friendly notes for designing an **Elevator System** using OOP, Mermaid diagrams, and small Java skeleton code.

---

## 1. Requirements

### Functional Requirements

- Support multiple elevators and multiple floors.
- Handle **external requests** from hall buttons: floor + direction.
- Handle **internal requests** from cabin buttons: destination floor.
- Dispatch external requests to the most suitable elevator.
- Each elevator should serve requests using the **LOOK algorithm**.
- Each elevator should show current floor and direction on displays.
- Each elevator should run independently, ideally with its own controller/thread.

### Non-Functional Requirements

- Clean OOP design with separation of concerns.
- Thread-safe request handling.
- Easy to extend with new dispatch strategies.
- Components should be independently testable.
- Scheduling strategy should be replaceable without changing core classes.

---

## 2. Core Use Cases

```mermaid
flowchart TD
    U1[User on floor presses UP/DOWN] --> ES[ElevatorSystem]
    ES --> DS[DispatchStrategy]
    DS --> E[Selected Elevator]
    E --> Q[Add request to queue]
    EC[ElevatorController] --> Q
    EC --> L[Run LOOK algorithm]
    L --> M[Move elevator]
    M --> D[Open/Close door]
    M --> DISP[Update displays]
```

### Main Use Cases

| Use Case | Description |
|---|---|
| External request | Passenger presses hall button on a floor |
| Internal request | Passenger selects destination inside elevator |
| Dispatch elevator | System chooses best elevator for hall request |
| Move elevator | Elevator moves toward next stop |
| Serve floor | Elevator opens door, serves passengers, closes door |
| Update display | Displays update on floor/direction changes |
| Change strategy | System swaps nearest/zone/custom strategy |

---

## 3. Entities + Responsibilities

### Entity Discovery

```mermaid
mindmap
  root((Elevator System))
    Enums
      Direction
      ElevatorState
      DoorState
      RequestType
    Data
      Request
      Door
    Interfaces
      DispatchStrategy
      ElevatorObserver
    Core Classes
      Elevator
      ElevatorController
      Floor
      Display
      ElevatorSystem
      NearestElevatorStrategy
      ZoneBasedStrategy
```

### Responsibility Table

| Entity | Type | Responsibility |
|---|---|---|
| `Direction` | Enum | `UP`, `DOWN`, `IDLE` |
| `ElevatorState` | Enum | Lifecycle state of elevator |
| `DoorState` | Enum | `OPEN`, `CLOSED` |
| `RequestType` | Enum | `INTERNAL`, `EXTERNAL` |
| `Request` | Data Class | Stores floor, direction, type, timestamp |
| `Door` | Data Class | Opens/closes elevator door |
| `Display` | Core Class | Shows current floor and direction |
| `Elevator` | Core Class | Represents elevator car and request queues |
| `Floor` | Core Class | Represents floor buttons and display |
| `ElevatorController` | Core Class | Runs LOOK scheduling algorithm |
| `DispatchStrategy` | Interface | Contract for elevator selection |
| `NearestElevatorStrategy` | Strategy | Chooses closest suitable elevator |
| `ZoneBasedStrategy` | Strategy | Chooses elevator based on floor zone |
| `ElevatorObserver` | Interface | Contract for display update listeners |
| `ElevatorSystem` | Singleton/Facade | Entry point and coordinator |

---

## 4. Relationships

### Step 1: Request Enters the System

```mermaid
flowchart LR
    User[Passenger] --> Floor[Floor Button]
    Floor --> ElevatorSystem
    ElevatorSystem --> DispatchStrategy
    DispatchStrategy --> Elevator
```

### Step 2: Elevator Owns Physical Components

```mermaid
classDiagram
    class Elevator {
        -int id
        -int currentFloor
        -Direction direction
        -ElevatorState state
    }

    class Door {
        -DoorState state
        +open()
        +close()
    }

    class Display {
        -int elevatorId
        -int currentFloor
        -Direction direction
        +show()
    }

    Elevator *-- Door : owns
    Elevator *-- Display : owns cabin display
```

### Step 3: Strategy Relationship

```mermaid
classDiagram
    class ElevatorSystem
    class DispatchStrategy {
        <<interface>>
        +selectElevator(elevators, floor, direction)
    }
    class NearestElevatorStrategy
    class ZoneBasedStrategy

    ElevatorSystem --> DispatchStrategy : uses
    DispatchStrategy <|.. NearestElevatorStrategy
    DispatchStrategy <|.. ZoneBasedStrategy
```

### Step 4: Observer Relationship

```mermaid
classDiagram
    class Elevator
    class ElevatorObserver {
        <<interface>>
        +onElevatorStateChanged(id, floor, direction)
    }
    class Display

    Elevator --> ElevatorObserver : notifies
    ElevatorObserver <|.. Display
```

---

## 5. State Transitions

### Elevator State Diagram

```mermaid
stateDiagram-v2
    [*] --> IDLE

    IDLE --> MOVING_UP: request above
    IDLE --> MOVING_DOWN: request below
    IDLE --> DOOR_OPEN: request at current floor
    IDLE --> OUT_OF_SERVICE: maintenance

    MOVING_UP --> DOOR_OPEN: arrive at requested floor
    MOVING_DOWN --> DOOR_OPEN: arrive at requested floor

    DOOR_OPEN --> MOVING_UP: close door + more up requests
    DOOR_OPEN --> MOVING_DOWN: close door + more down requests
    DOOR_OPEN --> IDLE: close door + no requests

    MOVING_UP --> OUT_OF_SERVICE: failure
    MOVING_DOWN --> OUT_OF_SERVICE: failure
    OUT_OF_SERVICE --> IDLE: maintenance complete
```

### Direction vs State

| Concept | Purpose |
|---|---|
| `Direction` | Movement intent: UP, DOWN, IDLE |
| `ElevatorState` | Lifecycle state: IDLE, MOVING_UP, MOVING_DOWN, DOOR_OPEN, OUT_OF_SERVICE |

Example: elevator can have `Direction.IDLE` and `ElevatorState.DOOR_OPEN` at the same time.

---

## 6. Core Flows

### External Request Flow

```mermaid
sequenceDiagram
    participant User
    participant System as ElevatorSystem
    participant Strategy as DispatchStrategy
    participant Elevator
    participant Controller as ElevatorController
    participant Display

    User->>System: requestElevator(floor, direction)
    System->>Strategy: selectElevator(elevators, floor, direction)
    Strategy-->>System: best elevator
    System->>Elevator: addRequest(floor, direction)
    Controller->>Elevator: getNextStop()
    Controller->>Elevator: moveToFloor(nextStop)
    Elevator->>Display: notifyObservers()
    Controller->>Elevator: openDoor()
    Controller->>Elevator: closeDoor()
```

### Internal Request Flow

```mermaid
flowchart TD
    Passenger[Passenger inside elevator] --> Button[Press destination floor]
    Button --> Elevator[Current Elevator]
    Elevator --> Add[Add internal request]
    Add --> Queue[Add to up/down request set]
    Queue --> Controller[Controller serves via LOOK]
```

### LOOK Algorithm Flow

```mermaid
flowchart TD
    Start[Check pending requests] --> Dir{Current direction?}

    Dir -->|UP| UpCheck{Any request above?}
    UpCheck -->|Yes| MoveUp[Move to nearest higher request]
    UpCheck -->|No| DownCheck1{Any down requests?}
    DownCheck1 -->|Yes| ReverseDown[Reverse to DOWN]
    DownCheck1 -->|No| Idle1[Go IDLE]

    Dir -->|DOWN| DownCheck{Any request below?}
    DownCheck -->|Yes| MoveDown[Move to nearest lower request]
    DownCheck -->|No| UpCheck1{Any up requests?}
    UpCheck1 -->|Yes| ReverseUp[Reverse to UP]
    UpCheck1 -->|No| Idle2[Go IDLE]

    Dir -->|IDLE| AnyReq{Any requests?}
    AnyReq -->|Above| StartUp[Start UP]
    AnyReq -->|Below| StartDown[Start DOWN]
    AnyReq -->|None| Wait[Wait]
```

---

## 7. Design Patterns Used

### 1. Strategy Pattern

Used for choosing which elevator should serve an external request.

```mermaid
classDiagram
    class DispatchStrategy {
        <<interface>>
        +selectElevator(List~Elevator~, int, Direction) Elevator
    }
    class NearestElevatorStrategy
    class ZoneBasedStrategy
    class ElevatorSystem

    ElevatorSystem --> DispatchStrategy
    DispatchStrategy <|.. NearestElevatorStrategy
    DispatchStrategy <|.. ZoneBasedStrategy
```

Why useful:

- Swap dispatch algorithm at runtime.
- Add new strategies without modifying `ElevatorSystem`.
- Follows Open/Closed Principle.

### 2. Observer Pattern

Used for display updates.

```mermaid
classDiagram
    class ElevatorObserver {
        <<interface>>
        +onElevatorStateChanged(int, int, Direction)
    }
    class Display
    class Elevator

    Elevator --> ElevatorObserver : notify
    ElevatorObserver <|.. Display
```

Why useful:

- Elevator does not directly depend on display classes.
- Easy to add monitoring dashboard, logs, analytics, etc.

### 3. Singleton Pattern

Used for `ElevatorSystem` as one central coordinator.

```mermaid
flowchart LR
    Client1 --> ES[ElevatorSystem Singleton]
    Client2 --> ES
    ES --> Elevators
    ES --> DispatchStrategy
```

### 4. Why Not State Pattern?

The state behavior is not complex enough to need separate classes like `IdleState`, `MovingUpState`, etc.

Use enum + guard checks unless each state has very different behavior.

---

## 8. Skeleton Code

> Compact Java-style skeleton for interview revision.

```java
import java.util.*;
import java.util.concurrent.*;

// ---------- Enums ----------

enum Direction {
    UP, DOWN, IDLE
}

enum ElevatorState {
    IDLE, MOVING_UP, MOVING_DOWN, DOOR_OPEN, OUT_OF_SERVICE
}

enum DoorState {
    OPEN, CLOSED
}

enum RequestType {
    INTERNAL, EXTERNAL
}

// ---------- Exception ----------

class ElevatorException extends RuntimeException {
    public ElevatorException(String message) {
        super(message);
    }
}

// ---------- Data Classes ----------

final class Request {
    private final int floor;
    private final Direction direction;
    private final RequestType type;
    private final long timestamp;

    public Request(int floor, Direction direction, RequestType type) {
        this.floor = floor;
        this.direction = direction;
        this.type = type;
        this.timestamp = System.currentTimeMillis();
    }

    public int getFloor() { return floor; }
    public Direction getDirection() { return direction; }
    public RequestType getType() { return type; }
    public long getTimestamp() { return timestamp; }
}

class Door {
    private DoorState state = DoorState.CLOSED;

    public synchronized void open() {
        state = DoorState.OPEN;
        System.out.println("Door opened");
    }

    public synchronized void close() {
        state = DoorState.CLOSED;
        System.out.println("Door closed");
    }

    public synchronized boolean isOpen() {
        return state == DoorState.OPEN;
    }
}

// ---------- Observer ----------

interface ElevatorObserver {
    void onElevatorStateChanged(int elevatorId, int floor, Direction direction);
}

class Display implements ElevatorObserver {
    private final int elevatorId;
    private int currentFloor;
    private Direction currentDirection = Direction.IDLE;

    public Display(int elevatorId) {
        this.elevatorId = elevatorId;
    }

    @Override
    public void onElevatorStateChanged(int elevatorId, int floor, Direction direction) {
        if (this.elevatorId != elevatorId) return;
        this.currentFloor = floor;
        this.currentDirection = direction;
        show();
    }

    public void show() {
        System.out.println("Elevator " + elevatorId + " | Floor: " + currentFloor + " | Direction: " + currentDirection);
    }
}

// ---------- Elevator ----------

class Elevator {
    private final int id;
    private final int totalFloors;
    private int currentFloor = 1;
    private Direction direction = Direction.IDLE;
    private ElevatorState state = ElevatorState.IDLE;
    private final Door door = new Door();

    // LOOK algorithm data structures
    private final NavigableSet<Integer> upRequests = new TreeSet<>();
    private final NavigableSet<Integer> downRequests = new TreeSet<>(Collections.reverseOrder());

    private final List<ElevatorObserver> observers = new CopyOnWriteArrayList<>();

    public Elevator(int id, int totalFloors) {
        this.id = id;
        this.totalFloors = totalFloors;
        addObserver(new Display(id));
    }

    public synchronized void addRequest(int floor, Direction requestDirection) {
        validateFloor(floor);

        if (floor > currentFloor) {
            upRequests.add(floor);
        } else if (floor < currentFloor) {
            downRequests.add(floor);
        } else {
            // Already on requested floor
            openDoor();
            closeDoor();
        }

        if (direction == Direction.IDLE) {
            direction = floor >= currentFloor ? Direction.UP : Direction.DOWN;
        }
    }

    public synchronized Integer getNextStop() {
        if (direction == Direction.UP) {
            Integer next = upRequests.ceiling(currentFloor + 1);
            if (next != null) return next;
            if (!downRequests.isEmpty()) {
                direction = Direction.DOWN;
                return downRequests.first();
            }
        }

        if (direction == Direction.DOWN) {
            Integer next = downRequests.ceiling(currentFloor - 1);
            if (next != null) return next;
            if (!upRequests.isEmpty()) {
                direction = Direction.UP;
                return upRequests.first();
            }
        }

        if (!upRequests.isEmpty()) {
            direction = Direction.UP;
            return upRequests.first();
        }

        if (!downRequests.isEmpty()) {
            direction = Direction.DOWN;
            return downRequests.first();
        }

        direction = Direction.IDLE;
        state = ElevatorState.IDLE;
        return null;
    }

    public synchronized void moveToFloor(int floor) {
        validateFloor(floor);

        if (door.isOpen()) {
            throw new ElevatorException("Cannot move while door is open");
        }

        state = floor > currentFloor ? ElevatorState.MOVING_UP : ElevatorState.MOVING_DOWN;
        direction = floor > currentFloor ? Direction.UP : Direction.DOWN;
        currentFloor = floor;

        upRequests.remove(floor);
        downRequests.remove(floor);
        notifyObservers();
    }

    public synchronized void openDoor() {
        state = ElevatorState.DOOR_OPEN;
        door.open();
        notifyObservers();
    }

    public synchronized void closeDoor() {
        door.close();
        if (!hasRequests()) {
            state = ElevatorState.IDLE;
            direction = Direction.IDLE;
        }
        notifyObservers();
    }

    public boolean hasRequests() {
        return !upRequests.isEmpty() || !downRequests.isEmpty();
    }

    public void addObserver(ElevatorObserver observer) {
        observers.add(observer);
    }

    private void notifyObservers() {
        for (ElevatorObserver observer : observers) {
            observer.onElevatorStateChanged(id, currentFloor, direction);
        }
    }

    private void validateFloor(int floor) {
        if (floor < 1 || floor > totalFloors) {
            throw new ElevatorException("Invalid floor: " + floor);
        }
    }

    public int getId() { return id; }
    public int getCurrentFloor() { return currentFloor; }
    public Direction getDirection() { return direction; }
    public ElevatorState getState() { return state; }
}

// ---------- Floor ----------

class Floor {
    private final int floorNumber;
    private boolean upButtonPressed;
    private boolean downButtonPressed;

    public Floor(int floorNumber) {
        this.floorNumber = floorNumber;
    }

    public void pressUpButton() {
        upButtonPressed = true;
    }

    public void pressDownButton() {
        downButtonPressed = true;
    }

    public void resetButtons() {
        upButtonPressed = false;
        downButtonPressed = false;
    }
}

// ---------- Strategy ----------

interface DispatchStrategy {
    Elevator selectElevator(List<Elevator> elevators, int floor, Direction direction);
}

class NearestElevatorStrategy implements DispatchStrategy {
    @Override
    public Elevator selectElevator(List<Elevator> elevators, int floor, Direction direction) {
        Elevator best = null;
        int bestScore = Integer.MIN_VALUE;

        for (Elevator elevator : elevators) {
            if (elevator.getState() == ElevatorState.OUT_OF_SERVICE) continue;

            int distance = Math.abs(elevator.getCurrentFloor() - floor);
            int score = -distance;

            boolean sameDirection = elevator.getDirection() == direction;
            boolean idle = elevator.getDirection() == Direction.IDLE;

            if (idle) score += 10;
            if (sameDirection) score += 5;

            if (score > bestScore) {
                bestScore = score;
                best = elevator;
            }
        }

        if (best == null) {
            throw new ElevatorException("No elevator available");
        }
        return best;
    }
}

// ---------- Controller ----------

class ElevatorController implements Runnable {
    private final Elevator elevator;
    private volatile boolean running = true;

    public ElevatorController(Elevator elevator) {
        this.elevator = elevator;
    }

    @Override
    public void run() {
        while (running) {
            processRequests();
            sleep(500);
        }
    }

    private void processRequests() {
        Integer nextStop = elevator.getNextStop();
        if (nextStop == null) return;

        elevator.moveToFloor(nextStop);
        elevator.openDoor();
        sleep(500);
        elevator.closeDoor();
    }

    public void stop() {
        running = false;
    }

    private void sleep(long ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}

// ---------- System Facade ----------

class ElevatorSystem {
    private static volatile ElevatorSystem instance;

    private final List<Elevator> elevators = new ArrayList<>();
    private final List<ElevatorController> controllers = new ArrayList<>();
    private DispatchStrategy dispatchStrategy = new NearestElevatorStrategy();
    private final int totalFloors;

    private ElevatorSystem(int numElevators, int totalFloors) {
        this.totalFloors = totalFloors;

        for (int i = 1; i <= numElevators; i++) {
            Elevator elevator = new Elevator(i, totalFloors);
            elevators.add(elevator);
            controllers.add(new ElevatorController(elevator));
        }
    }

    public static ElevatorSystem getInstance(int numElevators, int totalFloors) {
        if (instance == null) {
            synchronized (ElevatorSystem.class) {
                if (instance == null) {
                    instance = new ElevatorSystem(numElevators, totalFloors);
                }
            }
        }
        return instance;
    }

    public void requestElevator(int floor, Direction direction) {
        validateFloor(floor);
        Elevator selected = dispatchStrategy.selectElevator(elevators, floor, direction);
        selected.addRequest(floor, direction);
    }

    public void requestFloorInsideElevator(int elevatorId, int destinationFloor) {
        validateFloor(destinationFloor);
        Elevator elevator = elevators.get(elevatorId - 1);
        elevator.addRequest(destinationFloor, Direction.IDLE);
    }

    public void setDispatchStrategy(DispatchStrategy strategy) {
        this.dispatchStrategy = strategy;
    }

    public void start() {
        for (ElevatorController controller : controllers) {
            new Thread(controller).start();
        }
    }

    public void shutdown() {
        for (ElevatorController controller : controllers) {
            controller.stop();
        }
    }

    private void validateFloor(int floor) {
        if (floor < 1 || floor > totalFloors) {
            throw new ElevatorException("Invalid floor: " + floor);
        }
    }
}
```

### Demo Driver

```java
public class ElevatorDemo {
    public static void main(String[] args) {
        ElevatorSystem system = ElevatorSystem.getInstance(3, 10);
        system.start();

        system.requestElevator(5, Direction.UP);
        system.requestElevator(8, Direction.DOWN);
        system.requestFloorInsideElevator(1, 9);

        // Later
        // system.shutdown();
    }
}
```

---

## 9. Edge Cases

| Edge Case | Expected Handling |
|---|---|
| Invalid floor number | Throw `ElevatorException` |
| Request same as current floor | Open door immediately |
| Elevator out of service | Dispatch strategy should skip it |
| Door open while moving | Reject movement |
| Multiple same-floor requests | Use Set to avoid duplicates |
| No elevator available | Throw meaningful exception |
| Internal request before entering elevator | Should not be allowed by real UI layer |
| External request at top floor going UP | Reject or ignore |
| External request at bottom floor going DOWN | Reject or ignore |
| Concurrent requests | Synchronize request queue updates |

---

## 10. Failure Points

| Failure Point | Risk | Mitigation |
|---|---|---|
| Race condition in request sets | Lost or corrupted requests | Use synchronized methods / locks |
| Wrong dispatch choice | Poor wait time | Improve scoring strategy |
| Door movement bug | Unsafe elevator movement | Guard: never move when door is open |
| Thread never stops | Resource leak | `shutdown()` with volatile running flag |
| Elevator stuck in moving state | Requests blocked | Add timeout/health checks |
| Display stale | Bad user experience | Observer notifications on every state change |
| Singleton hard to test | Poor testability | Use dependency injection in production |
| Request starvation | Some floors wait too long | Add aging/fairness strategy |

---

## 11. Improvements

### Design Improvements

- Add `MaintenanceMode` and technician operations.
- Add emergency stop behavior.
- Add overload detection using weight sensor.
- Add door obstruction sensor.
- Add request aging to avoid starvation.
- Add multiple scheduling strategies: SCAN, LOOK, destination control.
- Add analytics: average wait time, average travel time.
- Replace singleton with dependency injection for better testability.

### Code Improvements

- Use `ReentrantLock` instead of broad `synchronized` methods.
- Use `BlockingQueue` or event-driven controller loop.
- Add unit tests for dispatch strategy and LOOK algorithm.
- Add better thread lifecycle management using `ExecutorService`.
- Persist elevator events for debugging.

---

## Final Class Diagram

```mermaid
classDiagram
    class Direction {
        <<enum>>
        UP
        DOWN
        IDLE
    }

    class ElevatorState {
        <<enum>>
        IDLE
        MOVING_UP
        MOVING_DOWN
        DOOR_OPEN
        OUT_OF_SERVICE
    }

    class DoorState {
        <<enum>>
        OPEN
        CLOSED
    }

    class RequestType {
        <<enum>>
        INTERNAL
        EXTERNAL
    }

    class Request {
        -int floor
        -Direction direction
        -RequestType type
        -long timestamp
    }

    class Door {
        -DoorState state
        +open()
        +close()
        +isOpen() boolean
    }

    class ElevatorObserver {
        <<interface>>
        +onElevatorStateChanged(int elevatorId, int floor, Direction direction)
    }

    class Display {
        -int elevatorId
        -int currentFloor
        -Direction currentDirection
        +show()
    }

    class DispatchStrategy {
        <<interface>>
        +selectElevator(List~Elevator~, int floor, Direction direction) Elevator
    }

    class NearestElevatorStrategy {
        +selectElevator(List~Elevator~, int floor, Direction direction) Elevator
    }

    class ZoneBasedStrategy {
        -Map~Integer,Integer~ zoneAssignments
        +selectElevator(List~Elevator~, int floor, Direction direction) Elevator
    }

    class Elevator {
        -int id
        -int currentFloor
        -Direction direction
        -ElevatorState state
        -NavigableSet~Integer~ upRequests
        -NavigableSet~Integer~ downRequests
        +addRequest(int floor, Direction direction)
        +getNextStop() Integer
        +moveToFloor(int floor)
        +openDoor()
        +closeDoor()
        +notifyObservers()
    }

    class Floor {
        -int floorNumber
        -boolean upButtonPressed
        -boolean downButtonPressed
        +pressUpButton()
        +pressDownButton()
        +resetButtons()
    }

    class ElevatorController {
        -Elevator elevator
        -boolean running
        +run()
        +stop()
    }

    class ElevatorSystem {
        -static ElevatorSystem instance
        -List~Elevator~ elevators
        -List~ElevatorController~ controllers
        -DispatchStrategy dispatchStrategy
        +getInstance(int, int) ElevatorSystem
        +requestElevator(int floor, Direction direction)
        +requestFloorInsideElevator(int elevatorId, int floor)
        +setDispatchStrategy(DispatchStrategy strategy)
        +shutdown()
    }

    Elevator *-- Door : owns
    Elevator *-- Display : owns cabin display
    Elevator --> ElevatorObserver : notifies
    ElevatorObserver <|.. Display

    DispatchStrategy <|.. NearestElevatorStrategy
    DispatchStrategy <|.. ZoneBasedStrategy
    ElevatorSystem --> DispatchStrategy : uses

    ElevatorSystem o-- Elevator : manages
    ElevatorSystem *-- ElevatorController : owns
    ElevatorController --> Elevator : controls
    Floor *-- Display : owns floor display

    Request --> Direction
    Request --> RequestType
    Elevator --> Direction
    Elevator --> ElevatorState
    Door --> DoorState
```

---

## Quick Interview Summary

Design an elevator system using:

- `ElevatorSystem` as facade/singleton.
- `DispatchStrategy` for assigning hall requests.
- `ElevatorController` per elevator for independent operation.
- `Elevator` with two sorted sets: `upRequests` and `downRequests`.
- LOOK algorithm to reduce unnecessary direction changes.
- Observer pattern for display updates.
- Thread-safe request updates.

