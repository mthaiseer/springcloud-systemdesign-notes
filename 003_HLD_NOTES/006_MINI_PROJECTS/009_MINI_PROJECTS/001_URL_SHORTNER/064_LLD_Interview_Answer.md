# 064_LLD_Interview_Answer.md
# MiniURLShortener — LLD Interview Answer

> Core mental model: **LLD interview is not only class diagrams and code. It is a controlled journey from requirements → objects → relationships → flows → extensible design → clean code. The interviewer is checking whether you can transform vague product behavior into maintainable object-oriented software.**

---

## Clickable Index

- [1. Why This Exists](#1-why-this-exists)
- [2. The One Core Mental Model](#2-the-one-core-mental-model)
- [3. What LLD Interview Really Tests](#3-what-lld-interview-really-tests)
- [4. The Golden LLD Answer Framework](#4-the-golden-lld-answer-framework)
- [5. LLD vs HLD Mental Model](#5-lld-vs-hld-mental-model)
- [6. Requirement Clarification](#6-requirement-clarification)
- [7. Functional Requirements](#7-functional-requirements)
- [8. Non-Functional Requirements In LLD](#8-non-functional-requirements-in-lld)
- [9. Identifying Core Objects](#9-identifying-core-objects)
- [10. Bottom-Up Object Discovery](#10-bottom-up-object-discovery)
- [11. Relationship Building](#11-relationship-building)
- [12. Class Diagram Thinking](#12-class-diagram-thinking)
- [13. API And Method Design](#13-api-and-method-design)
- [14. Sequence Diagram Thinking](#14-sequence-diagram-thinking)
- [15. Activity Flow Thinking](#15-activity-flow-thinking)
- [16. Applying SOLID Principles](#16-applying-solid-principles)
- [17. Design Pattern Selection](#17-design-pattern-selection)
- [18. Error Handling In LLD](#18-error-handling-in-lld)
- [19. Concurrency And State Management](#19-concurrency-and-state-management)
- [20. Extensibility Mindset](#20-extensibility-mindset)
- [21. Example 1: Design Tic Tac Toe](#21-example-1-design-tic-tac-toe)
- [22. Example 2: Design Parking Lot](#22-example-2-design-parking-lot)
- [23. Example 3: Design URL Shortener LLD](#23-example-3-design-url-shortener-lld)
- [24. Java Code Structure Template](#24-java-code-structure-template)
- [25. Step-by-Step Dry Run](#25-step-by-step-dry-run)
- [26. Common LLD Interview Questions](#26-common-lld-interview-questions)
- [27. Production Failure Stories](#27-production-failure-stories)
- [28. Debugging Mindset](#28-debugging-mindset)
- [29. Common Mistakes](#29-common-mistakes)
- [30. Interview-Ready Answer Script](#30-interview-ready-answer-script)
- [31. Senior Engineer Checklist](#31-senior-engineer-checklist)
- [32. One-Page Cheat Sheet](#32-one-page-cheat-sheet)
- [33. One Picture To Remember](#33-one-picture-to-remember)

---

## 1. Why This Exists

In system design interviews, HLD answers questions like:

```text
How many servers?
How does data scale?
Where is cache?
How does traffic flow?
How do we handle failures?
```

LLD answers a different question:

```text
How do we model the software internally?
Which classes exist?
Who owns state?
Who changes state?
Which object talks to which object?
How do we keep code extensible?
How do we prevent god classes?
```

Many candidates fail LLD because they jump directly to code.

Bad interview flow:

```text
Question: Design Parking Lot
Candidate: starts writing classes immediately
```

Better interview flow:

```text
1. Clarify requirements.
2. Identify actors and use cases.
3. Identify nouns as candidate objects.
4. Identify verbs as methods.
5. Build objects bottom-up.
6. Connect objects with relationships.
7. Draw class diagram.
8. Explain flows.
9. Apply patterns only where needed.
10. Write clean code skeleton.
11. Discuss extensibility.
```

One-line memory:

```text
LLD is HLD inside the application boundary.
```

---

## 2. The One Core Mental Model

LLD interview is a transformation pipeline.

```text
Vague Problem
    |
    v
Clear Requirements
    |
    v
Objects
    |
    v
Relationships
    |
    v
Flows
    |
    v
Design Patterns
    |
    v
Code
```

ASCII:

```text
+------------------+      +------------------+      +------------------+
| Requirements     | ---> | Object Model     | ---> | Class Diagram    |
+------------------+      +------------------+      +------------------+
          |                         |                         |
          v                         v                         v
+------------------+      +------------------+      +------------------+
| Use Cases        | ---> | State + Behavior | ---> | Java Code        |
+------------------+      +------------------+      +------------------+
```

LLD is not memorizing diagrams.

LLD is answering:

```text
Who owns this responsibility?
Who should not own it?
What changes in future?
What should remain stable?
```

Senior mental model:

```text
Classes are not boxes. Classes are responsibility boundaries.
```

---

## 3. What LLD Interview Really Tests

The interviewer is checking:

```text
1. Requirement clarity.
2. Object-oriented thinking.
3. Encapsulation.
4. Responsibility assignment.
5. SOLID principles.
6. Design patterns without overengineering.
7. Code readability.
8. Extensibility.
9. Handling edge cases.
10. Tradeoff explanation.
```

They are not only checking whether you know:

```text
Factory Pattern
Strategy Pattern
Observer Pattern
Singleton Pattern
```

They care more about whether you can say:

```text
This class should not know that detail.
This behavior should be an interface because it changes.
This state belongs inside the aggregate.
This method should not mutate two unrelated objects.
This flow needs validation before state transition.
```

Interview signal table:

```text
+----------------------------+-------------------------------+
| Weak Signal                | Strong Signal                 |
+----------------------------+-------------------------------+
| jumps to code              | clarifies requirements first  |
| creates many random classes| creates responsibility model  |
| uses patterns everywhere   | uses patterns only for change |
| public fields              | encapsulated state            |
| one manager class does all | distributed responsibilities  |
| no edge cases              | state transitions explained   |
| no sequence flow           | clear method call flow        |
+----------------------------+-------------------------------+
```

---

## 4. The Golden LLD Answer Framework

Use this in every LLD interview.

```text
Step 1: Clarify scope.
Step 2: List functional requirements.
Step 3: List constraints and assumptions.
Step 4: Identify actors.
Step 5: Identify use cases.
Step 6: Extract nouns and verbs.
Step 7: Create core objects.
Step 8: Assign responsibilities.
Step 9: Define relationships.
Step 10: Draw class diagram.
Step 11: Explain important flows.
Step 12: Discuss design patterns.
Step 13: Write code skeleton.
Step 14: Discuss edge cases.
Step 15: Discuss extensions.
```

ASCII memory:

```text
Question
   |
   v
Scope -> Objects -> Relations -> Flows -> Code -> Extensions
```

Use this sentence in interview:

```text
I will first clarify the exact scope, then identify the core objects and their responsibilities, then show the relationships and key flows before writing code.
```

This sounds senior because you control the conversation.

---

## 5. LLD vs HLD Mental Model

HLD:

```text
System boundary is large.
Focus is services, databases, queues, caches, load balancers.
```

LLD:

```text
System boundary is one application or module.
Focus is classes, interfaces, methods, state, and object collaboration.
```

ASCII:

```text
HLD VIEW

Client -> LB -> Service -> Cache -> DB -> Queue


LLD VIEW INSIDE SERVICE

Controller -> Service -> Domain Objects -> Repository -> Policies
```

Comparison:

```text
+-------------------+------------------------------+------------------------------+
| Topic             | HLD                          | LLD                          |
+-------------------+------------------------------+------------------------------+
| Unit              | service                      | class/object                 |
| Main concern      | scalability                  | maintainability              |
| Diagram           | architecture diagram         | class/sequence diagram       |
| Data              | storage choice               | entity/value object          |
| Failure           | service/infra failure        | invalid state/exception      |
| Pattern           | CQRS, sharding, caching      | Strategy, Factory, Observer  |
| Question          | how system scales            | how code evolves             |
+-------------------+------------------------------+------------------------------+
```

Golden rule:

```text
HLD decides boxes in infrastructure.
LLD decides boxes in code.
```

---

## 6. Requirement Clarification

Never skip clarification.

For any LLD question, ask:

```text
1. What are the main use cases?
2. Is persistence required?
3. Is concurrency required?
4. Is user authentication required?
5. Are we designing APIs or only domain classes?
6. What edge cases matter?
7. Should I implement full code or core classes only?
```

Example for Parking Lot:

```text
Should the parking lot support multiple floors?
Should it support different vehicle types?
Should pricing be included?
Should we handle online reservation?
Should there be multiple entry and exit gates?
```

Example for Tic Tac Toe:

```text
Is it only 3x3 or NxN?
Is it two-player only?
Can computer player be added later?
Should we detect draw and winner?
Should we support replay?
```

Why clarification matters:

```text
Without scope, every solution can become either too small or overengineered.
```

Good interview phrase:

```text
I will keep v1 simple, but I will design the extension points cleanly.
```

---

## 7. Functional Requirements

Functional requirements describe what the system does.

Template:

```text
The system should allow:
1. User/actor to perform action A.
2. System to validate action A.
3. System to update state.
4. System to return result.
5. System to handle invalid action.
```

For LLD, functional requirements become methods.

Example Tic Tac Toe:

```text
1. Start a game.
2. Add two players.
3. Player makes move.
4. Validate move.
5. Update board.
6. Check winner.
7. Check draw.
8. End game.
```

These map to methods:

```text
Game.start()
Game.makeMove(row, col)
Board.isCellEmpty(row, col)
Board.placeMark(row, col, mark)
WinningStrategy.checkWinner(board, lastMove)
GameStatus changes to WON/DRAW/IN_PROGRESS
```

ASCII:

```text
Requirement sentence
       |
       v
Use case
       |
       v
Method
       |
       v
Class responsibility
```

---

## 8. Non-Functional Requirements In LLD

LLD also has NFRs, but they are code-level.

Examples:

```text
Extensibility
Testability
Thread safety
Low coupling
High cohesion
Clean error handling
Readability
Maintainability
```

LLD NFR table:

```text
+----------------+-----------------------------------------+
| NFR            | LLD Meaning                             |
+----------------+-----------------------------------------+
| Extensible     | easy to add new vehicle/player/rule     |
| Testable       | classes can be unit tested independently|
| Maintainable   | responsibilities are clear              |
| Thread-safe    | shared state protected                  |
| Reusable       | interfaces hide changing behavior       |
| Observable     | errors and important transitions visible|
+----------------+-----------------------------------------+
```

Example:

```text
If pricing rules change often, do not hardcode pricing inside ParkingTicket.
Use PricingStrategy.
```

Bad:

```java
class ParkingTicket {
    double calculatePrice() {
        return hours * 50;
    }
}
```

Better:

```java
interface PricingStrategy {
    Money calculate(ParkingTicket ticket);
}
```

Reason:

```text
Ticket owns parking session data.
PricingStrategy owns pricing rule.
```

---

## 9. Identifying Core Objects

Object discovery starts from nouns.

Given problem statement:

```text
Design a parking lot where vehicles enter, get tickets, park in spots, and pay at exit.
```

Nouns:

```text
ParkingLot
Vehicle
Ticket
Spot
EntryGate
ExitGate
Payment
```

Verbs:

```text
enter
assign spot
park
pay
exit
```

Candidate classes:

```text
ParkingLot
ParkingFloor
ParkingSpot
Vehicle
ParkingTicket
Gate
PaymentService
SpotAssignmentStrategy
PricingStrategy
```

ASCII:

```text
Problem Text
   |
   +-- nouns  ---> classes
   |
   +-- verbs  ---> methods
   |
   +-- rules  ---> policies/strategies
   |
   +-- states ---> enums
```

But do not blindly create every noun as class.

Ask:

```text
Does this noun own data?
Does it own behavior?
Does it change independently?
Does it help express domain clearly?
```

If yes, class.

If no, maybe field or enum.

---

## 10. Bottom-Up Object Discovery

A strong LLD answer builds from small stable objects to larger coordinators.

Example Parking Lot:

```text
VehicleType
SpotType
ParkingSpot
ParkingFloor
ParkingLot
Gate
Ticket
Payment
```

Bottom-up diagram:

```text
Value / Enum Layer
+-------------+   +----------+   +-------------+
| VehicleType |   | SpotType |   | TicketStatus|
+-------------+   +----------+   +-------------+

Entity Layer
+---------+      +-------------+      +---------------+
| Vehicle |      | ParkingSpot |      | ParkingTicket |
+---------+      +-------------+      +---------------+

Aggregate Layer
+--------------+      +------------+
| ParkingFloor | ---> | ParkingLot |
+--------------+      +------------+

Service / Policy Layer
+------------------------+   +-----------------+
| SpotAssignmentStrategy |   | PricingStrategy |
+------------------------+   +-----------------+
```

Why bottom-up works:

```text
Small objects are easy to reason about.
Large objects should coordinate, not do everything.
```

Bad:

```text
ParkingLot does everything:
- stores floors
- validates vehicle
- assigns spot
- creates ticket
- calculates price
- processes payment
- opens gate
```

Better:

```text
ParkingLot coordinates.
SpotAssignmentStrategy assigns.
PricingStrategy calculates.
PaymentService processes.
Ticket stores session.
Spot stores occupancy.
```

---

## 11. Relationship Building

Common relationships:

```text
1. Has-a / composition
2. Uses-a / dependency
3. Is-a / inheritance
4. Implements / interface
5. Association
```

Use composition more than inheritance.

Example:

```text
ParkingLot has ParkingFloors.
ParkingFloor has ParkingSpots.
Vehicle has VehicleType.
ParkingTicket references Vehicle and ParkingSpot.
ParkingLot uses SpotAssignmentStrategy.
```

ASCII:

```text
ParkingLot
   |
   | has many
   v
ParkingFloor
   |
   | has many
   v
ParkingSpot

ParkingLot ----uses----> SpotAssignmentStrategy
ParkingTicket --refs---> Vehicle
ParkingTicket --refs---> ParkingSpot
```

Relationship checklist:

```text
Does A own lifecycle of B?
    yes -> composition

Does A only call B temporarily?
    yes -> dependency

Can B exist without A?
    yes -> association, not composition

Is A truly a subtype of B?
    yes -> inheritance/interface
```

Wrong inheritance example:

```text
Car extends ParkingSpot
```

Why wrong?

```text
A car is not a parking spot.
A car occupies a parking spot.
```

Correct:

```text
ParkingSpot has Vehicle or references parkedVehicle.
```

---

## 12. Class Diagram Thinking

You can draw simple ASCII class diagrams in interview.

Class box format:

```text
+----------------------+
| ClassName            |
+----------------------+
| fields               |
+----------------------+
| methods              |
+----------------------+
```

Example:

```text
+-----------------------------+
| ParkingSpot                 |
+-----------------------------+
| id: String                  |
| type: SpotType              |
| occupied: boolean           |
| vehicle: Vehicle            |
+-----------------------------+
| canFit(vehicle): boolean    |
| park(vehicle): void         |
| unpark(): void              |
+-----------------------------+
```

A class diagram should show:

```text
1. Main domain classes.
2. Important fields.
3. Important methods.
4. Relationships.
5. Interfaces where behavior changes.
```

It does not need every getter/setter.

Good diagram level:

```text
Enough to explain design, not so much that it becomes unreadable.
```

---

## 13. API And Method Design

Methods should express domain actions.

Weak method names:

```java
doStuff()
process()
handle()
update()
```

Strong method names:

```java
startGame()
makeMove(Position position)
assignSpot(Vehicle vehicle)
generateShortCode(Long id)
markTicketPaid(Payment payment)
```

Method design rules:

```text
1. Use names from domain language.
2. Keep parameters minimal.
3. Return meaningful result.
4. Do not expose internal collections directly.
5. Validate before mutating state.
6. Keep side effects clear.
```

Example bad:

```java
public void setBoard(char[][] board) {
    this.board = board;
}
```

Why bad?

```text
External code can replace full board and break game invariants.
```

Better:

```java
public void placeMark(Position position, Mark mark) {
    validatePosition(position);
    validateCellEmpty(position);
    cells[position.row()][position.col()] = mark;
}
```

Mental model:

```text
Methods are doors into the object. Do not leave every wall open.
```

---

## 14. Sequence Diagram Thinking

Sequence diagram shows runtime collaboration.

For Parking Lot entry:

```text
Driver
  |
  v
EntryGate
  |
  v
ParkingLot
  |
  v
SpotAssignmentStrategy
  |
  v
ParkingSpot
  |
  v
ParkingTicket
```

ASCII sequence:

```text
Driver     EntryGate     ParkingLot     Strategy      Spot       Ticket
  |            |              |             |           |           |
  | enter()    |              |             |           |           |
  |----------->|              |             |           |           |
  |            | issueTicket()|             |           |           |
  |            |------------->|             |           |           |
  |            |              | findSpot()  |           |           |
  |            |              |------------>|           |           |
  |            |              |             | select()  |           |
  |            |              |             |---------->|           |
  |            |              |             |<----------|           |
  |            |              | park()      |           |           |
  |            |              |------------------------>|           |
  |            |              | createTicket()          |           |
  |            |              |------------------------------------>|
  |            |<-------------|             |           |           |
  |<-----------|              |             |           |           |
```

Why sequence diagram matters:

```text
Class diagram shows structure.
Sequence diagram shows behavior.
```

A good LLD answer needs both.

---

## 15. Activity Flow Thinking

Activity diagram shows decision flow.

For Tic Tac Toe move:

```text
Player move
   |
   v
Is game in progress?
   |
   +-- no --> reject
   |
   v
Is player turn valid?
   |
   +-- no --> reject
   |
   v
Is cell empty?
   |
   +-- no --> reject
   |
   v
Place mark
   |
   v
Check winner
   |
   +-- yes --> game WON
   |
   v
Check board full
   |
   +-- yes --> game DRAW
   |
   v
Switch turn
```

ASCII:

```text
+-------------+
| Make Move   |
+-------------+
       |
       v
+------------------+
| Validate State   |
+------------------+
       |
       v
+------------------+
| Validate Cell    |
+------------------+
       |
       v
+------------------+
| Place Mark       |
+------------------+
       |
       v
+------------------+
| Winner?          |
+------------------+
   | yes      | no
   v          v
 WON      Board Full?
              | yes
              v
             DRAW
```

Activity flow helps catch missing validations.

---

## 16. Applying SOLID Principles

### Single Responsibility Principle

Each class should have one reason to change.

Bad:

```text
Game class:
- board state
- winner algorithm
- player input
- persistence
- UI rendering
```

Better:

```text
Game -> orchestrates game state
Board -> manages cells
WinningStrategy -> winner detection
Player -> player identity and mark
GameRepository -> persistence if needed
```

### Open/Closed Principle

Open for extension, closed for modification.

Example:

```text
Different winning rules for 3x3, NxN, Connect Four.
```

Use:

```java
interface WinningStrategy {
    boolean hasWinner(Board board, Move lastMove);
}
```

### Liskov Substitution Principle

Subtypes should be safely replaceable.

Bad:

```text
FreeParkingSpot extends ParkingSpot but throws exception for park()
```

### Interface Segregation Principle

Do not force classes to implement methods they do not need.

Bad:

```java
interface PaymentMethod {
    void swipeCard();
    void scanUpi();
    void enterCash();
}
```

Better:

```java
interface PaymentProcessor {
    PaymentResult pay(Money amount);
}
```

### Dependency Inversion Principle

High-level code depends on abstractions.

Bad:

```java
class ParkingLot {
    private NearestSpotAssignmentStrategy strategy;
}
```

Better:

```java
class ParkingLot {
    private SpotAssignmentStrategy strategy;
}
```

SOLID memory:

```text
SRP: one reason to change
OCP: add behavior without editing stable code
LSP: subtype behaves safely
ISP: small focused interfaces
DIP: depend on abstractions
```

---

## 17. Design Pattern Selection

Do not force patterns.

Use patterns when something varies.

Pattern selection table:

```text
+-------------------+-------------------------------+-----------------------------+
| Pattern           | Use When                      | Example                     |
+-------------------+-------------------------------+-----------------------------+
| Strategy          | algorithm changes             | pricing, winner checking    |
| Factory           | object creation varies        | vehicle, payment method     |
| Observer          | events notify many listeners  | game end, ticket paid       |
| State             | behavior depends on state     | ticket/game/order states    |
| Command           | action needs undo/queue/log   | moves, remote control       |
| Template Method   | fixed flow with variable step | payment workflow            |
| Repository        | hide persistence              | URL, ticket, game storage   |
+-------------------+-------------------------------+-----------------------------+
```

Golden rule:

```text
Do not say pattern name first. Explain the changing requirement first.
```

Bad:

```text
I will use Strategy Pattern because it is good.
```

Better:

```text
Pricing can change by vehicle type, weekend, or subscription plan, so I will hide pricing behind PricingStrategy.
```

---

## 18. Error Handling In LLD

LLD should define expected domain errors.

Examples:

```text
InvalidMoveException
CellAlreadyOccupiedException
GameAlreadyFinishedException
NoSpotAvailableException
InvalidTicketException
PaymentFailedException
ShortCodeNotFoundException
```

Error handling flow:

```text
Invalid request/state
       |
       v
Domain method validates invariant
       |
       v
Throws domain exception
       |
       v
Application/API layer maps to response
```

Do not silently ignore invalid behavior.

Bad:

```java
if (!board.isEmpty(position)) {
    return;
}
```

Better:

```java
if (!board.isEmpty(position)) {
    throw new CellAlreadyOccupiedException(position);
}
```

Reason:

```text
Invalid state transitions should be visible.
```

---

## 19. Concurrency And State Management

LLD interviews may ask concurrency.

Examples:

```text
Two cars try same parking spot.
Two players move at same time.
Two users book same seat.
Two requests use same custom alias.
```

Ask:

```text
Is the system single-threaded for interview scope?
Should I handle concurrent calls?
```

If concurrency matters:

```text
1. Keep state private.
2. Validate and mutate atomically.
3. Use locks or synchronized blocks for in-memory design.
4. Use DB constraints/transactions for persistent design.
5. Avoid exposing mutable collections.
```

Example in-memory move:

```java
public synchronized MoveResult makeMove(Player player, Position position) {
    validateTurn(player);
    board.placeMark(position, player.getMark());
    return evaluateGame(position);
}
```

But say clearly:

```text
For real distributed systems, synchronized is not enough. I would rely on database transactions, optimistic locking, or distributed coordination depending on consistency needs.
```

This shows seniority.

---

## 20. Extensibility Mindset

Interviewers love future changes.

Ask yourself:

```text
What can change?
What should not change?
```

Examples:

```text
Tic Tac Toe:
- board size can change
- winning strategy can change
- human/computer player can change

Parking Lot:
- pricing can change
- spot allocation can change
- vehicle types can change
- payment methods can change

URL Shortener:
- shortcode generation can change
- validation policy can change
- storage can change
- analytics can be added
```

Make changing parts interfaces.

```text
Changing behavior -> interface/strategy
Stable domain state -> class/entity
Small constants -> enum
External system -> adapter
Persistence -> repository
```

ASCII:

```text
Stable Core Domain
       |
       +-- uses --> Interface
                       |
                       +-- implementation A
                       +-- implementation B
                       +-- implementation C
```

---

## 21. Example 1: Design Tic Tac Toe

### Requirements

```text
1. Support 3x3 board.
2. Support two players.
3. Players take turns.
4. Reject invalid moves.
5. Detect winner.
6. Detect draw.
7. Keep design extensible for NxN later.
```

### Core Objects

```text
Game
Board
Cell
Player
Move
Position
Mark
GameStatus
WinningStrategy
```

### Bottom-Up Objects

```text
Mark enum:
    X, O, EMPTY

Position:
    row, col

Cell:
    position, mark

Board:
    grid of cells

Player:
    id, name, mark

Move:
    player, position, mark

WinningStrategy:
    checks winner

Game:
    controls turn, board, status
```

ASCII class model:

```text
+------------------+
| Game             |
+------------------+
| board            |
| players          |
| currentTurnIndex |
| status           |
| winner           |
| strategy         |
+------------------+
| start()          |
| makeMove()       |
| switchTurn()     |
+------------------+
        |
        | has
        v
+------------------+        +---------------------+
| Board            |------->| Cell                |
+------------------+        +---------------------+
| size             |        | position            |
| cells            |        | mark                |
+------------------+        +---------------------+
| placeMark()      |        | isEmpty()           |
| isFull()         |        +---------------------+
+------------------+
        |
        | uses
        v
+----------------------+
| WinningStrategy      |
+----------------------+
| hasWinner()          |
+----------------------+
```

### Sequence: Make Move

```text
Player       Game        Board       Strategy
  |           |            |            |
  | move      |            |            |
  |---------->|            |            |
  |           | validate   |            |
  |           |----------->|            |
  |           | place mark |            |
  |           |----------->|            |
  |           |            |            |
  |           | check win  |            |
  |           |----------------------->|
  |           |<-----------------------|
  |           | update status          |
  |<----------|            |            |
```

### Java Code Skeleton

```java
enum Mark {
    X, O, EMPTY
}

enum GameStatus {
    NOT_STARTED,
    IN_PROGRESS,
    WON,
    DRAW
}

record Position(int row, int col) {}

class Player {
    private final String id;
    private final String name;
    private final Mark mark;

    public Player(String id, String name, Mark mark) {
        this.id = id;
        this.name = name;
        this.mark = mark;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public Mark getMark() { return mark; }
}

class Cell {
    private final Position position;
    private Mark mark;

    public Cell(Position position) {
        this.position = position;
        this.mark = Mark.EMPTY;
    }

    public boolean isEmpty() {
        return mark == Mark.EMPTY;
    }

    public void place(Mark mark) {
        if (!isEmpty()) {
            throw new IllegalStateException("Cell already occupied");
        }
        this.mark = mark;
    }

    public Mark getMark() { return mark; }
    public Position getPosition() { return position; }
}

class Board {
    private final int size;
    private final Cell[][] cells;

    public Board(int size) {
        this.size = size;
        this.cells = new Cell[size][size];
        for (int r = 0; r < size; r++) {
            for (int c = 0; c < size; c++) {
                cells[r][c] = new Cell(new Position(r, c));
            }
        }
    }

    public void placeMark(Position position, Mark mark) {
        validate(position);
        cells[position.row()][position.col()].place(mark);
    }

    public Mark getMark(Position position) {
        validate(position);
        return cells[position.row()][position.col()].getMark();
    }

    public boolean isFull() {
        for (int r = 0; r < size; r++) {
            for (int c = 0; c < size; c++) {
                if (cells[r][c].isEmpty()) return false;
            }
        }
        return true;
    }

    private void validate(Position position) {
        if (position.row() < 0 || position.row() >= size ||
            position.col() < 0 || position.col() >= size) {
            throw new IllegalArgumentException("Invalid board position");
        }
    }

    public int getSize() { return size; }
}

interface WinningStrategy {
    boolean hasWinner(Board board, Position lastMove, Mark mark);
}

class StandardWinningStrategy implements WinningStrategy {
    @Override
    public boolean hasWinner(Board board, Position lastMove, Mark mark) {
        int n = board.getSize();
        int row = lastMove.row();
        int col = lastMove.col();

        boolean rowWin = true;
        for (int c = 0; c < n; c++) {
            if (board.getMark(new Position(row, c)) != mark) {
                rowWin = false;
                break;
            }
        }

        boolean colWin = true;
        for (int r = 0; r < n; r++) {
            if (board.getMark(new Position(r, col)) != mark) {
                colWin = false;
                break;
            }
        }

        boolean diagWin = row == col;
        if (diagWin) {
            for (int i = 0; i < n; i++) {
                if (board.getMark(new Position(i, i)) != mark) {
                    diagWin = false;
                    break;
                }
            }
        }

        boolean antiDiagWin = row + col == n - 1;
        if (antiDiagWin) {
            for (int i = 0; i < n; i++) {
                if (board.getMark(new Position(i, n - 1 - i)) != mark) {
                    antiDiagWin = false;
                    break;
                }
            }
        }

        return rowWin || colWin || diagWin || antiDiagWin;
    }
}

class Game {
    private final Board board;
    private final Player[] players;
    private final WinningStrategy winningStrategy;
    private int currentTurnIndex;
    private GameStatus status;
    private Player winner;

    public Game(Board board, Player player1, Player player2, WinningStrategy winningStrategy) {
        this.board = board;
        this.players = new Player[]{player1, player2};
        this.winningStrategy = winningStrategy;
        this.currentTurnIndex = 0;
        this.status = GameStatus.IN_PROGRESS;
    }

    public synchronized GameStatus makeMove(Player player, Position position) {
        if (status != GameStatus.IN_PROGRESS) {
            throw new IllegalStateException("Game is not in progress");
        }

        Player currentPlayer = players[currentTurnIndex];
        if (!currentPlayer.getId().equals(player.getId())) {
            throw new IllegalStateException("Not player's turn");
        }

        board.placeMark(position, player.getMark());

        if (winningStrategy.hasWinner(board, position, player.getMark())) {
            status = GameStatus.WON;
            winner = player;
            return status;
        }

        if (board.isFull()) {
            status = GameStatus.DRAW;
            return status;
        }

        currentTurnIndex = 1 - currentTurnIndex;
        return status;
    }

    public GameStatus getStatus() { return status; }
    public Player getWinner() { return winner; }
}
```

### Why This Design Is Good

```text
1. Board owns cell placement.
2. Game owns turn and status.
3. WinningStrategy owns winner logic.
4. Player is immutable identity + mark.
5. Future NxN works because board size is variable.
6. Future strategy changes do not modify Game.
```

---

## 22. Example 2: Design Parking Lot

### Requirements

```text
1. Parking lot has multiple floors.
2. Each floor has multiple spots.
3. Spots support different vehicle types.
4. Vehicle gets ticket at entry.
5. Vehicle pays at exit.
6. System frees spot after exit.
7. Design should support pricing strategy changes.
```

### Core Objects

```text
ParkingLot
ParkingFloor
ParkingSpot
Vehicle
ParkingTicket
EntryGate
ExitGate
Payment
SpotAssignmentStrategy
PricingStrategy
```

### Class Diagram

```text
+---------------------+
| ParkingLot          |
+---------------------+
| floors              |
| assignmentStrategy  |
| ticketService       |
+---------------------+
| parkVehicle()       |
| exitVehicle()       |
+---------------------+
       |
       | has many
       v
+---------------------+       +---------------------+
| ParkingFloor        | ----> | ParkingSpot         |
+---------------------+       +---------------------+
| floorNo             |       | spotId              |
| spots               |       | spotType            |
+---------------------+       | occupied            |
| availableSpots()    |       | vehicle             |
+---------------------+       +---------------------+
                                | canFit()            |
                                | park()              |
                                | unpark()            |
                                +---------------------+

+------------------------+        +------------------+
| SpotAssignmentStrategy |        | PricingStrategy  |
+------------------------+        +------------------+
| findSpot()             |        | calculate()      |
+------------------------+        +------------------+
```

### Java Code Skeleton

```java
enum VehicleType {
    BIKE, CAR, TRUCK
}

enum SpotType {
    SMALL, MEDIUM, LARGE
}

enum TicketStatus {
    ACTIVE, PAID, LOST
}

class Vehicle {
    private final String number;
    private final VehicleType type;

    public Vehicle(String number, VehicleType type) {
        this.number = number;
        this.type = type;
    }

    public String getNumber() { return number; }
    public VehicleType getType() { return type; }
}

class ParkingSpot {
    private final String spotId;
    private final SpotType spotType;
    private Vehicle vehicle;

    public ParkingSpot(String spotId, SpotType spotType) {
        this.spotId = spotId;
        this.spotType = spotType;
    }

    public boolean isAvailable() {
        return vehicle == null;
    }

    public boolean canFit(Vehicle vehicle) {
        if (!isAvailable()) return false;
        return switch (vehicle.getType()) {
            case BIKE -> true;
            case CAR -> spotType == SpotType.MEDIUM || spotType == SpotType.LARGE;
            case TRUCK -> spotType == SpotType.LARGE;
        };
    }

    public void park(Vehicle vehicle) {
        if (!canFit(vehicle)) {
            throw new IllegalStateException("Vehicle cannot be parked in this spot");
        }
        this.vehicle = vehicle;
    }

    public void unpark() {
        this.vehicle = null;
    }

    public String getSpotId() { return spotId; }
    public SpotType getSpotType() { return spotType; }
}

class ParkingTicket {
    private final String ticketId;
    private final Vehicle vehicle;
    private final ParkingSpot spot;
    private final long entryTimeMillis;
    private long exitTimeMillis;
    private TicketStatus status;

    public ParkingTicket(String ticketId, Vehicle vehicle, ParkingSpot spot) {
        this.ticketId = ticketId;
        this.vehicle = vehicle;
        this.spot = spot;
        this.entryTimeMillis = System.currentTimeMillis();
        this.status = TicketStatus.ACTIVE;
    }

    public void close() {
        if (status != TicketStatus.ACTIVE) {
            throw new IllegalStateException("Ticket is not active");
        }
        this.exitTimeMillis = System.currentTimeMillis();
        this.status = TicketStatus.PAID;
    }

    public long durationMillis() {
        long end = exitTimeMillis == 0 ? System.currentTimeMillis() : exitTimeMillis;
        return end - entryTimeMillis;
    }

    public ParkingSpot getSpot() { return spot; }
    public Vehicle getVehicle() { return vehicle; }
    public TicketStatus getStatus() { return status; }
}

interface SpotAssignmentStrategy {
    ParkingSpot findSpot(List<ParkingFloor> floors, Vehicle vehicle);
}

class FirstAvailableSpotStrategy implements SpotAssignmentStrategy {
    @Override
    public ParkingSpot findSpot(List<ParkingFloor> floors, Vehicle vehicle) {
        for (ParkingFloor floor : floors) {
            for (ParkingSpot spot : floor.getSpots()) {
                if (spot.canFit(vehicle)) return spot;
            }
        }
        throw new IllegalStateException("No spot available");
    }
}

class ParkingFloor {
    private final int floorNo;
    private final List<ParkingSpot> spots;

    public ParkingFloor(int floorNo, List<ParkingSpot> spots) {
        this.floorNo = floorNo;
        this.spots = spots;
    }

    public List<ParkingSpot> getSpots() {
        return Collections.unmodifiableList(spots);
    }
}

interface PricingStrategy {
    long calculateAmount(ParkingTicket ticket);
}

class HourlyPricingStrategy implements PricingStrategy {
    @Override
    public long calculateAmount(ParkingTicket ticket) {
        long hours = Math.max(1, ticket.durationMillis() / (1000 * 60 * 60));
        return hours * 50;
    }
}

class ParkingLot {
    private final List<ParkingFloor> floors;
    private final SpotAssignmentStrategy spotAssignmentStrategy;
    private final PricingStrategy pricingStrategy;

    public ParkingLot(
            List<ParkingFloor> floors,
            SpotAssignmentStrategy spotAssignmentStrategy,
            PricingStrategy pricingStrategy
    ) {
        this.floors = floors;
        this.spotAssignmentStrategy = spotAssignmentStrategy;
        this.pricingStrategy = pricingStrategy;
    }

    public synchronized ParkingTicket parkVehicle(Vehicle vehicle) {
        ParkingSpot spot = spotAssignmentStrategy.findSpot(floors, vehicle);
        spot.park(vehicle);
        return new ParkingTicket(UUID.randomUUID().toString(), vehicle, spot);
    }

    public synchronized long exitVehicle(ParkingTicket ticket) {
        long amount = pricingStrategy.calculateAmount(ticket);
        ticket.close();
        ticket.getSpot().unpark();
        return amount;
    }
}
```

### Why This Design Is Good

```text
1. Spot owns occupancy.
2. ParkingLot coordinates entry/exit.
3. Assignment algorithm is replaceable.
4. Pricing algorithm is replaceable.
5. Floor owns spots.
6. Ticket owns parking session.
```

---

## 23. Example 3: Design URL Shortener LLD

### Requirements

```text
1. Create short URL from long URL.
2. Support optional custom alias.
3. Redirect short code to long URL.
4. Validate URL.
5. Handle duplicate alias.
6. Track status: ACTIVE, BLOCKED, DELETED.
7. Make shortcode generation replaceable.
```

### Core Objects

```text
ShortUrl
ShortCode
LongUrl
UrlStatus
ShortCodeGenerator
UrlValidator
UrlRepository
UrlShorteningService
RedirectService
Clock
```

### Class Diagram

```text
+-------------------------+
| UrlShorteningService    |
+-------------------------+
| repository              |
| generator               |
| validator               |
+-------------------------+
| createShortUrl()        |
+-------------------------+
        | uses
        v
+----------------------+       +----------------------+
| ShortCodeGenerator   |       | UrlValidator         |
+----------------------+       +----------------------+
| generate()           |       | validate()           |
+----------------------+       +----------------------+

+----------------------+       +----------------------+
| UrlRepository        | ----> | ShortUrl             |
+----------------------+       +----------------------+
| save()               |       | id                   |
| findByShortCode()    |       | longUrl              |
| existsByShortCode()  |       | shortCode            |
+----------------------+       | status               |
                               | expiresAt            |
                               +----------------------+
```

### Flow: Create Short URL

```text
Client
  |
  v
UrlShorteningService
  |
  +-- validate long URL
  |
  +-- if custom alias exists, validate alias
  |
  +-- else generate short code
  |
  +-- ensure uniqueness
  |
  +-- create ShortUrl domain object
  |
  +-- save repository
  |
  v
ShortUrlResult
```

### Flow: Redirect

```text
GET /abc123
   |
   v
RedirectService
   |
   +-- validate short code format
   +-- repository.findByShortCode()
   +-- not found -> domain error
   +-- blocked -> domain error
   +-- expired -> domain error
   +-- return longUrl
```

### Java Code Skeleton

```java
enum UrlStatus {
    ACTIVE, BLOCKED, DELETED
}

record ShortCode(String value) {
    public ShortCode {
        if (value == null || !value.matches("^[a-zA-Z0-9_-]{4,32}$")) {
            throw new IllegalArgumentException("Invalid short code");
        }
    }
}

record LongUrl(String value) {
    public LongUrl {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("longUrl is required");
        }
    }
}

class ShortUrl {
    private final Long id;
    private final LongUrl longUrl;
    private final ShortCode shortCode;
    private final Instant createdAt;
    private final Instant expiresAt;
    private UrlStatus status;

    public ShortUrl(Long id, LongUrl longUrl, ShortCode shortCode, Instant createdAt, Instant expiresAt) {
        this.id = id;
        this.longUrl = longUrl;
        this.shortCode = shortCode;
        this.createdAt = createdAt;
        this.expiresAt = expiresAt;
        this.status = UrlStatus.ACTIVE;
    }

    public boolean isExpired(Instant now) {
        return expiresAt != null && expiresAt.isBefore(now);
    }

    public void block() {
        status = UrlStatus.BLOCKED;
    }

    public boolean isRedirectable(Instant now) {
        return status == UrlStatus.ACTIVE && !isExpired(now);
    }

    public LongUrl getLongUrl() { return longUrl; }
    public ShortCode getShortCode() { return shortCode; }
    public UrlStatus getStatus() { return status; }
}

interface ShortCodeGenerator {
    ShortCode generate(Long id);
}

class Base62ShortCodeGenerator implements ShortCodeGenerator {
    private static final String CHARS = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";

    @Override
    public ShortCode generate(Long id) {
        if (id == null || id < 0) throw new IllegalArgumentException("Invalid id");
        if (id == 0) return new ShortCode("0000");

        StringBuilder sb = new StringBuilder();
        long n = id;
        while (n > 0) {
            sb.append(CHARS.charAt((int) (n % 62)));
            n /= 62;
        }
        return new ShortCode(sb.reverse().toString());
    }
}

interface UrlValidator {
    void validate(LongUrl longUrl);
}

class HttpUrlValidator implements UrlValidator {
    @Override
    public void validate(LongUrl longUrl) {
        URI uri = URI.create(longUrl.value());
        String scheme = uri.getScheme();
        if (scheme == null ||
                !(scheme.equalsIgnoreCase("http") || scheme.equalsIgnoreCase("https"))) {
            throw new IllegalArgumentException("Only http/https URLs are allowed");
        }
        if (uri.getHost() == null || uri.getHost().isBlank()) {
            throw new IllegalArgumentException("URL host is required");
        }
    }
}

interface UrlRepository {
    ShortUrl save(ShortUrl shortUrl);
    Optional<ShortUrl> findByShortCode(ShortCode shortCode);
    boolean existsByShortCode(ShortCode shortCode);
}

class UrlShorteningService {
    private final UrlRepository repository;
    private final ShortCodeGenerator generator;
    private final UrlValidator validator;
    private final Clock clock;

    public UrlShorteningService(
            UrlRepository repository,
            ShortCodeGenerator generator,
            UrlValidator validator,
            Clock clock
    ) {
        this.repository = repository;
        this.generator = generator;
        this.validator = validator;
        this.clock = clock;
    }

    public ShortUrl create(Long id, String rawLongUrl, String customAlias, Instant expiresAt) {
        LongUrl longUrl = new LongUrl(rawLongUrl);
        validator.validate(longUrl);

        ShortCode shortCode;
        if (customAlias != null && !customAlias.isBlank()) {
            shortCode = new ShortCode(customAlias);
            if (repository.existsByShortCode(shortCode)) {
                throw new IllegalStateException("Alias already exists");
            }
        } else {
            shortCode = generator.generate(id);
        }

        ShortUrl shortUrl = new ShortUrl(id, longUrl, shortCode, Instant.now(clock), expiresAt);
        return repository.save(shortUrl);
    }
}

class RedirectService {
    private final UrlRepository repository;
    private final Clock clock;

    public RedirectService(UrlRepository repository, Clock clock) {
        this.repository = repository;
        this.clock = clock;
    }

    public LongUrl resolve(String code) {
        ShortCode shortCode = new ShortCode(code);
        ShortUrl shortUrl = repository.findByShortCode(shortCode)
                .orElseThrow(() -> new IllegalStateException("Short code not found"));

        if (shortUrl.getStatus() == UrlStatus.BLOCKED) {
            throw new IllegalStateException("Short code blocked");
        }

        if (shortUrl.isExpired(Instant.now(clock))) {
            throw new IllegalStateException("Short code expired");
        }

        return shortUrl.getLongUrl();
    }
}
```

### Why This Design Is Good

```text
1. ShortCode and LongUrl are value objects.
2. ShortUrl owns lifecycle state.
3. Generation strategy is replaceable.
4. Validation policy is replaceable.
5. Repository hides persistence.
6. Service coordinates use case.
7. Redirect logic is separate from create logic.
```

---

## 24. Java Code Structure Template

For most LLD interviews, organize code like this:

```text
1. Enums
2. Value objects
3. Domain entities
4. Interfaces / strategies
5. Services / managers
6. Demo main or tests
```

Folder style:

```text
lld/problem/
│
├── model/
│   ├── Player.java
│   ├── Board.java
│   └── Game.java
│
├── strategy/
│   ├── WinningStrategy.java
│   └── StandardWinningStrategy.java
│
├── service/
│   └── GameService.java
│
├── exception/
│   └── InvalidMoveException.java
│
└── Main.java
```

Code answer order:

```text
1. enum
2. small immutable objects
3. core entity
4. interface
5. implementation
6. orchestrating service
```

Why this order works:

```text
The interviewer sees your design being built from atoms to system.
```

---

## 25. Step-by-Step Dry Run

### Dry Run: Tic Tac Toe Winning Move

Board before move:

```text
X | X | .
O | O | .
. | . | .
```

Current player:

```text
Player X
```

Move:

```text
Position(0, 2)
```

Flow:

```text
1. Game.makeMove(X, 0,2) called.
2. Game checks status is IN_PROGRESS.
3. Game checks current player is X.
4. Board validates position.
5. Board checks cell empty.
6. Board places X at row 0 col 2.
7. WinningStrategy checks row 0.
8. Row 0 has X X X.
9. Game status becomes WON.
10. Winner becomes Player X.
```

Final board:

```text
X | X | X
O | O | .
. | . | .
```

Result:

```text
GameStatus.WON
winner = X
```

### Dry Run: Parking Lot No Spot

```text
1. Car arrives.
2. EntryGate calls parkingLot.parkVehicle(car).
3. ParkingLot asks strategy.findSpot(floors, car).
4. Strategy scans floors.
5. No compatible available spot found.
6. Strategy throws NoSpotAvailableException.
7. Entry flow returns failure.
```

Lesson:

```text
Assignment strategy owns search failure, not Vehicle and not Ticket.
```

### Dry Run: URL Shortener Duplicate Alias

```text
1. User requests custom alias "admin".
2. UrlShorteningService creates ShortCode("admin").
3. Repository.existsByShortCode(admin) returns true.
4. Service throws AliasAlreadyExistsException.
5. No ShortUrl object is saved.
```

Lesson:

```text
Validation should happen before mutation.
```

---

## 26. Common LLD Interview Questions

Popular LLD questions:

```text
1. Design Tic Tac Toe
2. Design Chess
3. Design Snake and Ladder
4. Design Parking Lot
5. Design Elevator System
6. Design Vending Machine
7. Design Splitwise
8. Design BookMyShow
9. Design Library Management System
10. Design Hotel Booking
11. Design ATM
12. Design Logger
13. Design Rate Limiter
14. Design URL Shortener classes
15. Design Notification System
16. Design Food Delivery order flow
17. Design Cab Booking LLD
18. Design Meeting Scheduler
19. Design File System
20. Design Cache/LRU
```

How to approach any of them:

```text
Game-like problem:
    focus on state, moves, rules, players, winner/end condition

Inventory problem:
    focus on item, container, availability, assignment, payment

Booking problem:
    focus on resource, time slot, reservation, concurrency

Machine problem:
    focus on state pattern and transitions

Notification problem:
    focus on channel strategy and observer/event flow

Cache problem:
    focus on data structure + eviction policy interface
```

---

## 27. Production Failure Stories

### Failure Story 1: God Manager Class

Design:

```text
ParkingLotManager does everything.
```

It handles:

```text
spot allocation
payment
ticket creation
floor management
vehicle validation
pricing
reports
```

Problem:

```text
Any change touches same class.
Tests are hard.
Bug risk increases.
```

Fix:

```text
Split responsibilities:
SpotAssignmentStrategy
PricingStrategy
PaymentService
TicketService
ParkingLot
```

Lesson:

```text
Managers should coordinate, not become the whole system.
```

### Failure Story 2: No State Validation

Bug:

```text
Player makes move after game already WON.
```

Root cause:

```text
Game.makeMove() did not check status.
```

Fix:

```text
Validate state before mutation.
```

Lesson:

```text
Every domain method should protect invariants.
```

### Failure Story 3: Pricing Hardcoded Everywhere

Bug:

```text
Hourly price changes from 50 to weekend dynamic pricing.
```

Code impact:

```text
10 classes need edits.
```

Fix:

```text
PricingStrategy.
```

Lesson:

```text
Changing rules should live behind policies.
```

### Failure Story 4: Exposing Mutable List

Bad:

```java
public List<ParkingSpot> getSpots() {
    return spots;
}
```

External code modifies internal spot list.

Fix:

```java
return Collections.unmodifiableList(spots);
```

Lesson:

```text
Encapsulation means object controls its own state.
```

---

## 28. Debugging Mindset

When LLD design feels messy, ask:

```text
1. Which class has too many responsibilities?
2. Which behavior changes frequently?
3. Which state transition is unsafe?
4. Which object owns this data?
5. Which method mutates state?
6. Which invariant must always hold?
7. Which class is hard to test?
8. Which dependency should be an interface?
```

Debug map:

```text
Too many if-else for algorithm:
    use Strategy

Too many states with different behavior:
    consider State pattern

Creation logic spread everywhere:
    use Factory

Many objects need notification:
    use Observer/Event

Persistence mixed with domain:
    use Repository

External API mixed with core:
    use Adapter
```

Senior thought:

```text
LLD debugging is responsibility debugging.
```

---

## 29. Common Mistakes

### Mistake 1: Jumping directly to code

Wrong:

```text
Immediately writes classes without scope.
```

Correct:

```text
Clarify requirements and explain approach.
```

### Mistake 2: Too many classes

Wrong:

```text
Creates class for every noun.
```

Correct:

```text
Create class only when it owns state, behavior, or change.
```

### Mistake 3: God class

Wrong:

```text
One class does all work.
```

Correct:

```text
Split by responsibility.
```

### Mistake 4: Pattern dumping

Wrong:

```text
Uses Strategy, Factory, Observer, Singleton everywhere.
```

Correct:

```text
Use pattern only when there is a reason.
```

### Mistake 5: Public mutable state

Wrong:

```java
public List<Cell> cells;
```

Correct:

```java
private final List<Cell> cells;
```

### Mistake 6: No edge cases

Wrong:

```text
Only happy path.
```

Correct:

```text
Invalid move, no spot, duplicate alias, full board, expired ticket.
```

### Mistake 7: Confusing HLD with LLD

Wrong:

```text
For Tic Tac Toe, talks about Redis, Kafka, load balancer.
```

Correct:

```text
Focus on classes, state, methods, rules.
```

### Mistake 8: Inheritance abuse

Wrong:

```text
Car extends ParkingSpot.
```

Correct:

```text
Car occupies ParkingSpot.
```

---

## 30. Interview-Ready Answer Script

Use this script:

```text
I will first clarify the scope. For v1, I assume we need the core domain model and important flows, not persistence or UI unless required.

Next, I will list the functional requirements and identify actors. Then I will extract core objects from nouns and methods from actions. I will build smaller objects first, such as enums and value objects, then entities that own state, then services or strategies that coordinate behavior.

I will keep domain state encapsulated, validate state transitions inside domain methods, and use interfaces for behavior that can change, such as pricing, winner detection, allocation, or generation. I will avoid overusing design patterns and only apply them where there is clear variation.

After that I will show a class diagram, explain the main sequence flow, write Java code skeleton, and discuss edge cases and future extensions.
```

For URL Shortener LLD:

```text
The core domain is ShortUrl, which owns longUrl, shortCode, status, expiry, and lifecycle behavior. ShortCode and LongUrl can be value objects because they protect validation rules. UrlShorteningService coordinates create flow, RedirectService coordinates redirect flow, ShortCodeGenerator is an interface because generation strategy may change, UrlValidator is an interface because security policy may evolve, and UrlRepository hides persistence. Domain errors like duplicate alias, not found, blocked, and expired should be explicit exceptions mapped by the API layer.
```

---

## 31. Senior Engineer Checklist

Before finishing any LLD answer, check:

```text
[ ] Requirements clarified
[ ] Functional requirements listed
[ ] Actors identified
[ ] Core objects identified
[ ] Object ownership clear
[ ] Relationships clear
[ ] Class diagram shown
[ ] Main sequence flow explained
[ ] Important edge cases handled
[ ] State transitions validated
[ ] Interfaces used for changing behavior
[ ] No god class
[ ] No unnecessary inheritance
[ ] No public mutable state
[ ] Design patterns justified
[ ] Java code skeleton clean
[ ] Extensibility discussed
[ ] Concurrency mentioned if relevant
[ ] Persistence separated if relevant
[ ] Errors are domain-specific
[ ] Final answer summarized clearly
```

If these are covered, your LLD answer is strong.

---

## 32. One-Page Cheat Sheet

```text
LLD Core Mental Model:
Requirements -> Objects -> Relationships -> Flows -> Code -> Extensions

Ask first:
- scope?
- persistence?
- concurrency?
- core flows?
- edge cases?

Object discovery:
- nouns -> classes
- verbs -> methods
- states -> enums
- changing rules -> interfaces/strategies

Class responsibility:
- who owns data?
- who mutates state?
- who validates invariant?
- who coordinates flow?

Common relationships:
- has-a -> composition
- uses-a -> dependency
- is-a -> inheritance/interface

Design patterns:
- Strategy -> changing algorithm
- Factory -> changing creation
- Observer -> events/listeners
- State -> behavior by state
- Repository -> hide persistence
- Adapter -> hide external system

Strong interview flow:
1. Clarify
2. Requirements
3. Actors
4. Objects
5. Responsibilities
6. Relationships
7. Diagram
8. Sequence
9. Code
10. Edge cases
11. Extensions

Avoid:
- jumping to code
- god classes
- pattern dumping
- public mutable state
- inheritance abuse
- missing edge cases
- HLD discussion in LLD
```

---

## 33. One Picture To Remember

```text
                   LLD INTERVIEW ANSWER MENTAL MODEL

                          Vague Problem
                               |
                               v
                  +---------------------------+
                  | Clarify Scope             |
                  | requirements + constraints|
                  +---------------------------+
                               |
                               v
                  +---------------------------+
                  | Find Objects              |
                  | nouns, verbs, states      |
                  +---------------------------+
                               |
                               v
                  +---------------------------+
                  | Assign Responsibilities   |
                  | who owns data/behavior?   |
                  +---------------------------+
                               |
                               v
                  +---------------------------+
                  | Build Relationships       |
                  | has-a, uses-a, is-a       |
                  +---------------------------+
                               |
                               v
                  +---------------------------+
                  | Explain Flows             |
                  | sequence + activity       |
                  +---------------------------+
                               |
                               v
                  +---------------------------+
                  | Apply Patterns Carefully  |
                  | only where change exists  |
                  +---------------------------+
                               |
                               v
                  +---------------------------+
                  | Write Clean Java Code     |
                  | encapsulated + testable   |
                  +---------------------------+
                               |
                               v
                  +---------------------------+
                  | Discuss Edge Extensions   |
                  | concurrency + future      |
                  +---------------------------+

FINAL MEMORY:

LLD is not drawing classes.
LLD is assigning responsibilities so future change does not destroy the code.
```

---

## Final Retention Summary

Remember these five sentences:

```text
1. LLD starts with requirements, not code.
2. Classes are responsibility boundaries, not random nouns.
3. Stable domain state should be encapsulated inside entities/value objects.
4. Changing behavior should be hidden behind interfaces and strategies.
5. A strong LLD answer explains requirements, objects, relationships, flows, code, edge cases, and extensions.
```

After this chapter, your interview answer structure becomes:

```text
063_System_Design_Interview_Answer.md
    -> how to answer HLD/system design

064_LLD_Interview_Answer.md
    -> how to answer object-oriented design/class design
```
