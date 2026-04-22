# Tic-Tac-Toe Low-Level Design

## 1. Problem Statement
Tic-Tac-Toe is a two-player game played on a **3x3 board**. Players take turns placing their markers (`X` and `O`) on empty cells. The objective is to place **three of the same marker in a row** horizontally, vertically, or diagonally.

This document focuses on the **low-level design** of the game, not the full implementation.

---

## 2. Scope and Assumptions
### In Scope
- Standard **3x3** Tic-Tac-Toe board
- **Two human players**
- Alternate turns
- Invalid move rejection
- Winner detection
- Draw detection
- Scoreboard across multiple games
- Console/demo driven flow

### Out of Scope
- AI/computer player
- Undo/redo
- Move history replay
- GUI/web API
- Multiplayer/networking
- Variable board sizes in the first version

---

## 3. Functional Requirements
1. The system should allow two players to play on a **3x3 grid**.
2. Each player should have a unique marker: `X` or `O`.
3. Players should take turns one by one.
4. A move should only be allowed on an **empty cell**.
5. The system should reject invalid moves.
6. The game should detect when a player wins.
7. The game should detect when the board is full and declare a draw.
8. The system should maintain a **scoreboard across multiple games**.
9. A new game should be creatable without resetting the entire system.

---

## 4. Non-Functional Requirements
1. The design should follow **object-oriented principles**.
2. Responsibilities should be **cleanly separated**.
3. The code should be **modular and extensible**.
4. The design should be **easy to test**.
5. Future extensions such as larger boards, AI, and move history should be possible with minimal changes.

---

## 5. Core Entities
### Enums
- `Symbol`
- `GameStatus`

### Data Classes
- `Player`
- `Cell`

### Core Classes
- `Board`
- `Game`
- `Scoreboard`
- `TicTacToeSystem`

### Interfaces
- `WinningStrategy`
- `GameObserver`

---

## 6. Entity Responsibilities

### `Symbol`
Represents possible values inside a cell.
- `X`
- `O`
- `EMPTY`

### `GameStatus`
Represents the lifecycle state of a game.
- `IN_PROGRESS`
- `WINNER_X`
- `WINNER_O`
- `DRAW`

### `Player`
Represents a participant.
- `name`
- `symbol`

### `Cell`
Represents one board position.
- stores a single `Symbol`

### `Board`
Represents the 3x3 grid.
- place symbols
- check whether a cell is empty
- check whether the board is full
- print/render current state

### `Game`
Coordinates gameplay.
- validates moves
- places symbols
- switches turns
- checks win and draw
- notifies observers on game completion

### `WinningStrategy`
Encapsulates a win-checking rule.
- row check
- column check
- diagonal check

### `GameObserver`
Receives notification when a game ends.

### `Scoreboard`
Tracks wins across games.

### `TicTacToeSystem`
Facade and entry point for external callers.
- creates games
- delegates moves
- owns shared scoreboard

---

## 7. Class Relationships

### Composition
- `Board` **contains** `Cell[][]`
- `Game` **contains** `Board`

### Association
- `Game` **uses** `Player[]`
- `Game` **uses** `WinningStrategy` implementations
- `TicTacToeSystem` **uses** `Game`
- `TicTacToeSystem` **uses** `Scoreboard`

### Interface Implementation
- `RowWinningStrategy` implements `WinningStrategy`
- `ColumnWinningStrategy` implements `WinningStrategy`
- `DiagonalWinningStrategy` implements `WinningStrategy`
- `Scoreboard` implements `GameObserver`

---

## 8. High-Level Flow
1. `TicTacToeSystem` creates a new `Game`.
2. `Game` creates a `Board` and registers required `WinningStrategy` objects.
3. Each move is sent to `Game.makeMove(row, col)`.
4. `Game` validates the move.
5. `Board` places the symbol.
6. `Game` checks all winning strategies.
7. If no winner and board is full, game becomes `DRAW`.
8. If game ends, observers are notified.
9. `Scoreboard` updates total wins.

---

## 9. Design Patterns Used

### 9.1 Strategy Pattern
**Used for:** win detection

**Why:**
Different winning rules should be encapsulated independently instead of being hardcoded inside `Game`.

**Benefits:**
- easy to test each rule separately
- easy to add new winning rules later
- keeps `Game` simpler

**Classes involved:**
- `WinningStrategy`
- `RowWinningStrategy`
- `ColumnWinningStrategy`
- `DiagonalWinningStrategy`

---

### 9.2 Observer Pattern
**Used for:** post-game notifications

**Why:**
`Game` should not be tightly coupled with `Scoreboard` or any future logger/analytics feature.

**Benefits:**
- loose coupling
- extensible notification mechanism
- future listeners can be added without changing `Game`

**Classes involved:**
- `GameObserver`
- `Game`
- `Scoreboard`

---

### 9.3 Facade Pattern
**Used for:** system entry point

**Why:**
External clients should interact with one simple object instead of directly managing `Board`, `Game`, and `Scoreboard`.

**Classes involved:**
- `TicTacToeSystem`

---

### 9.4 Singleton Pattern
**Optional / interview-style choice**

**Used for:** single shared system instance with shared scoreboard

**Why:**
If the system should expose one global entry point and one shared scoreboard, `TicTacToeSystem` can be made singleton.

**Note:**
In production code, dependency injection is often preferred over Singleton.

---

## 10. Class Skeletons (Java-Style)

> These are intentionally incomplete skeletons so you can implement the logic yourself.

### 10.1 `Symbol`
```java
public enum Symbol {
    X('X'),
    O('O'),
    EMPTY('_');

    private final char displayChar;

    Symbol(char displayChar) {
        this.displayChar = displayChar;
    }

    public char getDisplayChar() {
        return displayChar;
    }
}
```

### 10.2 `GameStatus`
```java
public enum GameStatus {
    IN_PROGRESS,
    WINNER_X,
    WINNER_O,
    DRAW
}
```

### 10.3 `Player`
```java
public class Player {
    private final String name;
    private final Symbol symbol;

    public Player(String name, Symbol symbol) {
        // validate name and symbol != EMPTY
        this.name = name;
        this.symbol = symbol;
    }

    public String getName() {
        return name;
    }

    public Symbol getSymbol() {
        return symbol;
    }
}
```

### 10.4 `Cell`
```java
public class Cell {
    private Symbol symbol;

    public Cell() {
        this.symbol = Symbol.EMPTY;
    }

    public boolean isEmpty() {
        return symbol == Symbol.EMPTY;
    }

    public Symbol getSymbol() {
        return symbol;
    }

    public void setSymbol(Symbol symbol) {
        this.symbol = symbol;
    }
}
```

### 10.5 `Board`
```java
public class Board {
    private final int size;
    private final Cell[][] grid;

    public Board(int size) {
        this.size = size;
        this.grid = new Cell[size][size];
        initializeGrid();
    }

    private void initializeGrid() {
        // create Cell objects for all positions
    }

    public void placeSymbol(int row, int col, Symbol symbol) {
        // validate bounds and place symbol
    }

    public boolean isCellEmpty(int row, int col) {
        // return whether the cell is empty
        return false;
    }

    public boolean isWithinBounds(int row, int col) {
        // validate row/col
        return false;
    }

    public boolean isFull() {
        // check whether all cells are occupied
        return false;
    }

    public Cell getCell(int row, int col) {
        // return the cell at row, col
        return null;
    }

    public int getSize() {
        return size;
    }

    public void printBoard() {
        // print current board
    }
}
```

### 10.6 `WinningStrategy`
```java
public interface WinningStrategy {
    boolean checkWin(Board board, int row, int col, Symbol symbol);
}
```

### 10.7 `RowWinningStrategy`
```java
public class RowWinningStrategy implements WinningStrategy {
    @Override
    public boolean checkWin(Board board, int row, int col, Symbol symbol) {
        // check entire row
        return false;
    }
}
```

### 10.8 `ColumnWinningStrategy`
```java
public class ColumnWinningStrategy implements WinningStrategy {
    @Override
    public boolean checkWin(Board board, int row, int col, Symbol symbol) {
        // check entire column
        return false;
    }
}
```

### 10.9 `DiagonalWinningStrategy`
```java
public class DiagonalWinningStrategy implements WinningStrategy {
    @Override
    public boolean checkWin(Board board, int row, int col, Symbol symbol) {
        // check primary and secondary diagonals where relevant
        return false;
    }
}
```

### 10.10 `GameObserver`
```java
public interface GameObserver {
    void update(Game game);
}
```

### 10.11 `Game`
```java
import java.util.ArrayList;
import java.util.List;

public class Game {
    private final Board board;
    private final Player[] players;
    private int currentPlayerIndex;
    private GameStatus status;
    private final List<WinningStrategy> winningStrategies;
    private final List<GameObserver> observers;

    public Game(Player player1, Player player2, int boardSize) {
        this.board = new Board(boardSize);
        this.players = new Player[] {player1, player2};
        this.currentPlayerIndex = 0;
        this.status = GameStatus.IN_PROGRESS;
        this.winningStrategies = new ArrayList<>();
        this.observers = new ArrayList<>();
        registerDefaultWinningStrategies();
    }

    private void registerDefaultWinningStrategies() {
        // add row, column, diagonal strategies
    }

    public void makeMove(int row, int col) {
        // validate game state
        // validate move
        // place symbol on board
        // check win
        // check draw
        // switch turn if needed
        // notify observers if terminal state reached
    }

    public Player getCurrentPlayer() {
        return players[currentPlayerIndex];
    }

    public void switchTurn() {
        // alternate player index
    }

    public void addObserver(GameObserver observer) {
        observers.add(observer);
    }

    private void notifyObservers() {
        // notify all observers
    }

    public Board getBoard() {
        return board;
    }

    public GameStatus getStatus() {
        return status;
    }

    public Player[] getPlayers() {
        return players;
    }
}
```

### 10.12 `Scoreboard`
```java
import java.util.HashMap;
import java.util.Map;

public class Scoreboard implements GameObserver {
    private final Map<String, Integer> scores;

    public Scoreboard() {
        this.scores = new HashMap<>();
    }

    public void recordWin(Player player) {
        // increment player's score
    }

    public int getScore(String playerName) {
        // return score or 0
        return 0;
    }

    public void printScoreboard() {
        // print all recorded scores
    }

    @Override
    public void update(Game game) {
        // update scoreboard if game has a winner
    }
}
```

### 10.13 `TicTacToeSystem`
```java
public class TicTacToeSystem {
    private static TicTacToeSystem instance;
    private final Scoreboard scoreboard;
    private Game currentGame;

    private TicTacToeSystem() {
        this.scoreboard = new Scoreboard();
    }

    public static TicTacToeSystem getInstance() {
        // singleton initialization
        return null;
    }

    public Game createGame(Player player1, Player player2) {
        // create a new game and register scoreboard as observer
        return null;
    }

    public void makeMove(int row, int col) {
        // delegate move to current game
    }

    public void printScoreboard() {
        scoreboard.printScoreboard();
    }

    public Game getCurrentGame() {
        return currentGame;
    }
}
```

### 10.14 `Demo`
```java
public class Demo {
    public static void main(String[] args) {
        Player p1 = new Player("Alice", Symbol.X);
        Player p2 = new Player("Bob", Symbol.O);

        TicTacToeSystem system = TicTacToeSystem.getInstance();
        system.createGame(p1, p2);

        // hardcode sample moves here
        // system.makeMove(0, 0);
        // system.makeMove(1, 1);
        // ...

        system.printScoreboard();
    }
}
```

---

## 11. Suggested Validation Rules
- `Player` should not allow `Symbol.EMPTY`.
- Two players should not have the same symbol.
- Moves should be rejected if:
  - game already ended
  - row/col out of bounds
  - cell already occupied
- `createGame()` should replace any previous finished game.

---

## 12. Extension Points
This design supports future enhancements with limited changes:

### Variable Board Size
- keep `Board.size`
- adjust strategies accordingly

### AI Opponent
- introduce `MoveStrategy` or `PlayerType`

### Move History / Undo
- add `Move` class
- store move stack in `Game`

### Replay / Analytics
- add more `GameObserver` implementations

### GUI/API Layer
- keep `TicTacToeSystem` as application service/facade

---

## 13. Why This Design Works
- **SRP:** each class has a focused responsibility
- **OCP:** new winning rules or observers can be added without changing core logic
- **Loose coupling:** observer and strategy abstractions reduce dependencies
- **Testability:** strategies, board rules, and game flow can be tested independently
- **Extensibility:** future variants fit naturally into the design

---

## 14. Interview Summary
If asked to summarize this design in an interview:

- Use `Board`, `Cell`, `Player`, and `Game` as the main domain objects.
- Use `WinningStrategy` for row/column/diagonal win detection.
- Use `GameObserver` so `Scoreboard` can react when a game ends.
- Use `TicTacToeSystem` as the external entry point.
- Keep version 1 simple, but design it so bigger boards, AI, and history can be added later.

