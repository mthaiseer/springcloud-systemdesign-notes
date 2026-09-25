# Part 22 — Game Mathematics

> **Goal:** transform alternating-turn stories into mathematical state classifications and quickly recognize when parity, modulo, symmetry, invariants, XOR/Nim, or Grundy numbers determine the winner.
>
> **Core workflow:** `Game story → State → Legal moves → Terminal state → W/L pattern → Mathematical invariant → Winning strategy/proof`
>
> **Recognition question:** **What mathematical property separates winning positions from losing positions?**

## Table of Contents

- [22.0 Game Mathematics Mental Model](#220-game-mathematics-mental-model)
- [22.1 Winning and Losing States](#221-winning-and-losing-states)
- [22.2 Backward Reasoning from Terminal States](#222-backward-reasoning-from-terminal-states)
- [22.3 Fixed Number of Moves and Turn Parity](#223-fixed-number-of-moves-and-turn-parity)
- [22.4 Subtraction Games and Modulo Patterns](#224-subtraction-games-and-modulo-patterns)
- [22.5 Complementary Move Strategy](#225-complementary-move-strategy)
- [22.6 Mirror Strategy](#226-mirror-strategy)
- [22.7 Pairing Strategy](#227-pairing-strategy)
- [22.8 Strategy Stealing and Symmetry](#228-strategy-stealing-and-symmetry)
- [22.9 Invariants in Games](#229-invariants-in-games)
- [22.10 Game States as Equivalence Classes](#2210-game-states-as-equivalence-classes)
- [22.11 Multiple Piles and Why Parity Stops Working](#2211-multiple-piles-and-why-parity-stops-working)
- [22.12 Nim Game](#2212-nim-game)
- [22.13 Why Zero Nim-Sum Is Losing](#2213-why-zero-nim-sum-is-losing)
- [22.14 Why Non-Zero Nim-Sum Can Move to Zero](#2214-why-non-zero-nim-sum-can-move-to-zero)
- [22.15 Finding the Winning Nim Move](#2215-finding-the-winning-nim-move)
- [22.16 Binary-Column Interpretation of Nim](#2216-binary-column-interpretation-of-nim)
- [22.17 Misère Warning](#2217-misère-warning)
- [22.18 Sprague-Grundy Numbers](#2218-sprague-grundy-numbers)
- [22.19 Grundy Dry Run — Remove 1 or 2](#2219-grundy-dry-run-remove-1-or-2)
- [22.20 Combining Independent Games](#2220-combining-independent-games)
- [22.21 Periodicity in Game States](#2221-periodicity-in-game-states)
- [22.22 Games Reduced to Parity of a Derived Quantity](#2222-games-reduced-to-parity-of-a-derived-quantity)
- [22.23 Forced Moves and Game Compression](#2223-forced-moves-and-game-compression)
- [22.24 Greedy Moves vs Proven Winning Moves](#2224-greedy-moves-vs-proven-winning-moves)
- [22.25 Minimax vs Mathematical Modeling](#2225-minimax-vs-mathematical-modeling)
- [22.26 60-Second Game Discovery Workflow](#2226-60-second-game-discovery-workflow)
- [22.27 Codeforces Recognition Map](#2227-codeforces-recognition-map)
- [22.28 Proof Templates for Game Problems](#2228-proof-templates-for-game-problems)
- [22.29 Common Mistakes](#2229-common-mistakes)
- [22.30 Fast Revision Card](#2230-fast-revision-card)

---

<a id="220-game-mathematics-mental-model"></a>

## 22.0 Game Mathematics Mental Model

Game problems often look like simulation:

```text
Alice moves
Bob moves
Alice moves
Bob moves
...
```

But many Codeforces games collapse to a mathematical state.

```text
GAME STORY
    ↓
state + legal moves
    ↓
What makes a position winning or losing?
    ↓
invariant / parity / modulo / XOR
    ↓
O(1) or small-state decision
```

### Real-world scenario — chocolates on a table

There are `7` chocolates.

Each player must take exactly one chocolate.

The player taking the last chocolate wins.

```text
7 chocolates
A takes 1 -> 6
B takes 1 -> 5
A takes 1 -> 4
...
A takes last
```

No search is required.

The number of moves is fixed:

```text
moves = 7
```

Odd number of moves:

```text
first player moves last
```

So Alice wins.

### Core question

Do not begin with:

```text
"How do I simulate every move?"
```

Begin with:

```text
"What mathematical property of the state determines the winner?"
```

---

<a id="221-winning-and-losing-states"></a>

## 22.1 Winning and Losing States

For impartial deterministic games, classify each state as:

```text
W = winning
L = losing
```

### Fundamental recurrence

A state is winning if **at least one** legal move reaches a losing state.

```text
W -> exists move to L
```

A state is losing if **every** legal move reaches a winning state.

```text
L -> all moves go to W
```

### Example — take 1 or 2 stones

Last move wins.

Start from `0`:

```text
0 -> L
```

Now derive:

```text
1 -> can move to 0(L)        -> W
2 -> can move to 0(L)        -> W
3 -> moves to 2(W) or 1(W)   -> L
4 -> can move to 3(L)        -> W
5 -> can move to 3(L)        -> W
6 -> only to W states        -> L
```

Pattern:

```text
n: 0 1 2 3 4 5 6 7 8 9
   L W W L W W L W W L
```

Therefore:

```text
losing iff n % 3 == 0
```

### Real-world scenario

Imagine players removing `1` or `2` cookies from a plate.

The useful information is not the full move history.

It is simply:

```text
cookies_remaining mod 3
```

---

<a id="222-backward-reasoning-from-terminal-states"></a>

## 22.2 Backward Reasoning from Terminal States

Game mathematics is usually discovered backward.

### Step 1 — identify terminal state

Example:

```text
no stones remain
```

If the player to move has no legal move and loses:

```text
state 0 = L
```

### Step 2 — build outward

For moves `{1,2}`:

```text
0 = L

1 -> 0
therefore 1 = W

2 -> 0
therefore 2 = W

3 -> 2 or 1
both W
therefore 3 = L
```

### ASCII dependency

```text
       3
      / \
     2   1
     W   W
      \ /
       0
       L
```

### Real-world scenario — staircase race

A token is `n` steps from the finish.

Each player advances it `1` or `2` steps.

Instead of guessing from `n=1000`, solve tiny distances:

```text
0,1,2,3,...
```

and search for a repeating mathematical structure.

---

<a id="223-fixed-number-of-moves-and-turn-parity"></a>

## 22.3 Fixed Number of Moves and Turn Parity

If every legal play lasts exactly `M` moves, winner determination can collapse to parity.

Assuming the player making the last move wins:

```text
M odd  -> first player makes last move
M even -> second player makes last move
```

### Example

Exactly one object is removed per turn.

```text
n=6
```

Number of moves:

```text
M=6
```

Turns:

```text
1 A
2 B
3 A
4 B
5 A
6 B
```

Bob takes the last object.

### Real-world scenario — six tasks

Two people alternate completing exactly one task.

There are exactly six tasks.

```text
A B A B A B
```

The second person completes the final task.

### Recognition trigger

Ask:

```text
"Is the total number of moves independent of player choices?"
```

If yes, parity may solve the entire game.

---

<a id="224-subtraction-games-and-modulo-patterns"></a>

## 22.4 Subtraction Games and Modulo Patterns

Suppose each move removes between:

```text
1 and k
```

stones, and taking the last stone wins.

The losing states are:

```text
n % (k+1) == 0
```

### Why?

If:

```text
n = multiple of (k+1)
```

whatever the current player removes:

```text
1..k
```

the opponent receives a non-multiple.

From a non-multiple:

```text
n = q(k+1)+r
1 <= r <= k
```

remove exactly:

```text
r
```

and give the opponent:

```text
q(k+1)
```

### Example — remove 1, 2, or 3

```text
k=3
k+1=4
```

Losing:

```text
0,4,8,12,...
```

For `n=10`:

```text
10 % 4 = 2
```

Remove `2`:

```text
10 -> 8
```

Give opponent a losing state.

### Real-world scenario — candies

Players may eat `1–3` candies.

The strategy is to leave a multiple of `4`.

---

<a id="225-complementary-move-strategy"></a>

## 22.5 Complementary Move Strategy

A common proof pairs your move with the opponent's move.

For a `1..k` subtraction game, if the opponent removes:

```text
x
```

you remove:

```text
(k+1)-x
```

Together:

```text
x + [(k+1)-x] = k+1
```

### Example — remove 1..3

Target pair sum:

```text
4
```

If opponent takes:

```text
1 -> you take 3
2 -> you take 2
3 -> you take 1
```

Every two turns remove exactly:

```text
4
```

### Visual

```text
Opponent    You       pair total
   1         3             4
   2         2             4
   3         1             4
```

### Real-world scenario — shared chocolate bowl

If you first move to a multiple-of-4 state, complementary responses preserve that invariant after every pair of turns.

### Proof pattern

```text
establish invariant
      ↓
opponent disturbs it
      ↓
your complementary move restores it
```

---

<a id="226-mirror-strategy"></a>

## 22.6 Mirror Strategy

Symmetric games often admit a mirror strategy.

### Idea

Create a symmetric position.

Whenever the opponent changes one side:

```text
mirror the same action on the other side
```

After every pair of turns, symmetry is restored.

### Example

Two identical piles:

```text
pile A = 5
pile B = 5
```

Suppose a move removes any positive number from exactly one pile.

Opponent:

```text
A: 5 -> 3
B: 5
```

Mirror:

```text
A: 3
B: 5 -> 3
```

Symmetry restored:

```text
(3,3)
```

### Real-world scenario — two identical trays

Two trays contain the same number of cookies.

If the opponent removes `x` cookies from one tray, remove `x` from the other.

### Mathematical invariant

```text
pileA == pileB
```

after each of your turns.

Mirror strategies work only when the mirrored move is always legal.

---

<a id="227-pairing-strategy"></a>

## 22.7 Pairing Strategy

Instead of mirroring positions, pair objects or moves.

### Example

There are `2m` objects grouped into pairs:

```text
(1,2)
(3,4)
(5,6)
...
```

If the opponent selects one object from a pair, respond with its partner.

### Real-world scenario — paired seats

Seats are paired:

```text
A1 <-> A2
B1 <-> B2
C1 <-> C2
```

Whenever the opponent claims one seat, claim its paired seat.

### Mathematical effect

Each round consumes:

```text
2 objects
```

and preserves the pairing structure of untouched objects.

### Recognition trigger

Look for:

```text
even-sized symmetric resources
natural complements
paired positions
```

A pairing strategy is often a constructive proof of winning.

---

<a id="228-strategy-stealing-and-symmetry"></a>

## 22.8 Strategy Stealing and Symmetry

Sometimes you do not need to explicitly construct the entire strategy.

A strategy-stealing style argument can show that a supposed second-player advantage would let the first player make a harmless opening move and then imitate that strategy.

### Important caution

This technique is highly problem-specific.

You must prove that:

```text
the extra first move cannot hurt
```

and that the stolen strategy remains legal.

### Real-world intuition

Imagine a board where owning one extra harmless resource can never reduce your future options.

If the second player supposedly has a winning strategy from the initial state, the first player may take one harmless resource and then behave as though they were the second player.

### CF lesson

Do not write:

```text
"first player wins by strategy stealing"
```

without proving the monotonicity/legal-move assumptions.

---

<a id="229-invariants-in-games"></a>

## 22.9 Invariants in Games

An invariant is a quantity or property deliberately preserved through play.

Possible invariants:

```text
parity
sum modulo m
XOR
symmetry
difference between piles
number of odd piles
```

### Example — maintain multiple of 4

In remove-`1..3`:

```text
after my turn:
stones % 4 == 0
```

Opponent removes `x`.

State becomes:

```text
-x mod 4
```

You remove:

```text
4-x
```

Restored:

```text
0 mod 4
```

### Real-world scenario — bank-token game

If two players alter a balance using complementary transactions, one player may maintain a fixed remainder modulo some value.

### Core question

```text
"What property can I force to be true after every one of my turns?"
```

---

<a id="2210-game-states-as-equivalence-classes"></a>

## 22.10 Game States as Equivalence Classes

Sometimes exact state size does not matter.

Only a compressed class matters.

Example:

```text
n mod 3
```

for remove-`1..2`.

Instead of infinitely many states:

```text
0,1,2,3,4,5,6,...
```

we have only:

```text
class 0
class 1
class 2
```

### Transition model

```text
class 0 --remove1--> class 2
class 0 --remove2--> class 1

class 1 --remove1--> class 0
class 2 --remove2--> class 0
```

### Real-world scenario

A weekly schedule has infinitely many day counts:

```text
100,101,102,...
```

but only seven meaningful weekday classes.

Games often compress similarly using modulo, parity, or XOR.

---

<a id="2211-multiple-piles-and-why-parity-stops-working"></a>

## 22.11 Multiple Piles and Why Parity Stops Working

With multiple independent piles, total-stone parity is often insufficient.

### Example

Consider Nim positions:

```text
(1,1)
```

Total stones:

```text
2 even
```

and:

```text
(2,2)
```

Total:

```text
4 even
```

Both are losing in normal Nim.

But:

```text
(1,2)
```

Total:

```text
3 odd
```

is winning.

More importantly, positions with the same total can behave differently:

```text
(1,3) total=4
(2,2) total=4
```

Their strategic structure is not captured by total parity.

### Lesson

When a move changes exactly one independent pile by an arbitrary amount, ask about:

```text
XOR
```

not just:

```text
sum parity
```

---

<a id="2212-nim-game"></a>

## 22.12 Nim Game

Normal Nim:

```text
several piles
on each turn:
choose one pile
remove any positive number
player with no move loses
```

The theorem:

```text
nimSum = pile1 ^ pile2 ^ ... ^ pileN
```

Then:

```text
nimSum == 0 -> losing
nimSum != 0 -> winning
```

### Example

```text
piles = [3,4,5]
```

Binary:

```text
3 = 011
4 = 100
5 = 101
---------
XOR 010 = 2
```

Non-zero:

```text
winning
```

### Real-world scenario — token boxes

Several boxes contain tokens.

On each turn, choose exactly one box and remove as many tokens as desired.

The winning property is not total tokens.

It is:

```text
XOR of box sizes
```

This is one of the most important mathematical transformations in game problems.

---

<a id="2213-why-zero-nim-sum-is-losing"></a>

## 22.13 Why Zero Nim-Sum Is Losing

Let:

```text
X = a1 ^ a2 ^ ... ^ an
```

Suppose:

```text
X=0
```

A legal Nim move changes exactly one pile:

```text
ai -> bi
where bi < ai
```

New XOR:

```text
X'
= X ^ ai ^ bi
= 0 ^ ai ^ bi
= ai ^ bi
```

Since:

```text
ai != bi
```

we have:

```text
ai ^ bi != 0
```

Therefore every move from zero XOR produces non-zero XOR.

So:

```text
zero nim-sum
   ↓ every move
non-zero nim-sum
```

### Real-world intuition

Zero XOR is a balanced binary configuration.

Changing one pile necessarily breaks that balance.

---

<a id="2214-why-non-zero-nim-sum-can-move-to-zero"></a>

## 22.14 Why Non-Zero Nim-Sum Can Move to Zero

Suppose:

```text
X = a1 ^ ... ^ an != 0
```

Take the highest set bit of `X`.

At least one pile `ai` has that bit set.

Define:

```text
bi = ai ^ X
```

For a suitable pile with the highest differing bit set:

```text
bi < ai
```

so it is a legal reduction.

New XOR:

```text
X ^ ai ^ bi
```

Substitute:

```text
bi = ai ^ X
```

Then:

```text
X ^ ai ^ (ai ^ X)
= X ^ X ^ ai ^ ai
= 0
```

### Example

```text
[3,4,5]
nimSum=2
```

Try pile `3`:

```text
3 ^ 2 = 1
```

Reduce:

```text
3 -> 1
```

New piles:

```text
[1,4,5]
```

XOR:

```text
1 ^ 4 ^ 5 = 0
```

### Strategy

```text
non-zero XOR
    ↓
move to zero XOR
    ↓
opponent must make it non-zero
    ↓
restore zero XOR
```

---

<a id="2215-finding-the-winning-nim-move"></a>

## 22.15 Finding the Winning Nim Move

Compute:

```text
X = XOR of all piles
```

If:

```text
X==0
```

no winning move exists against perfect play.

Otherwise find a pile `a[i]` such that:

```text
(a[i] ^ X) < a[i]
```

Then change it to:

```text
a[i] ^ X
```

### Example

```text
piles=[7,4,5]
```

Nim sum:

```text
7 ^ 4 ^ 5

111
100
101
---
110 = 6
```

Try:

```text
7 ^ 6 = 1 < 7
```

So reduce:

```text
7 -> 1
```

New XOR:

```text
1 ^ 4 ^ 5 = 0
```

### Real-world scenario

Think of the pile reduction as restoring balance across every binary column.

---

<a id="2216-binary-column-interpretation-of-nim"></a>

## 22.16 Binary-Column Interpretation of Nim

XOR zero means every binary column contains an even number of `1`s.

Example:

```text
1 = 001
4 = 100
5 = 101
```

Column counts:

```text
bit2: 0+1+1 = 2 even
bit1: 0+0+0 = 0 even
bit0: 1+0+1 = 2 even
```

Thus:

```text
1 ^ 4 ^ 5 = 0
```

### Real-world analogy — switches

Each pile contributes ON/OFF states to binary columns.

XOR asks:

```text
"Is each switch column activated an even or odd number of times?"
```

A zero nim-sum means every column has even parity.

This is why Part 17's XOR modeling directly feeds into game mathematics.

---

<a id="2217-misère-warning"></a>

## 22.17 Misère Warning

Changing the terminal rule can change the mathematics.

Normal play:

```text
player making last move wins
```

Misère play:

```text
player making last move loses
```

For Nim, the usual XOR rule needs a special case when all piles have size `1`.

### All piles are 1

If there are:

```text
n
```

single-token piles, every move removes exactly one pile.

Under misère rules:

```text
n odd  -> first player loses
n even -> first player wins
```

### Example

```text
[1,1,1]
```

Exactly three moves.

The player taking the third/last token loses.

So first player loses.

### Contest lesson

Before applying a memorized game theorem, identify:

```text
normal play?
misère?
who wins when no move exists?
```

---

<a id="2218-sprague-grundy-numbers"></a>

## 22.18 Sprague-Grundy Numbers

For a finite impartial game state `s`, define:

```text
grundy(s)
=
mex({grundy(next) for every legal next state})
```

where `mex` is the minimum excluded non-negative integer.

Examples:

```text
mex({})      = 0
mex({0})     = 1
mex({0,1})   = 2
mex({1,2})   = 0
```

### Winning criterion

```text
grundy(s)==0 -> losing
grundy(s)!=0 -> winning
```

### Real-world intuition

A Grundy number is a mathematical label summarizing the strategic behavior of a state.

Different-looking games with the same Grundy number behave equivalently when combined with other impartial games.

---

<a id="2219-grundy-dry-run-remove-1-or-2"></a>

## 22.19 Grundy Dry Run — Remove 1 or 2

Allowed moves:

```text
n -> n-1
n -> n-2
```

Start:

```text
G(0)=mex({})=0
```

Then:

```text
G(1)=mex({G(0)})
    =mex({0})
    =1

G(2)=mex({G(1),G(0)})
    =mex({1,0})
    =2

G(3)=mex({G(2),G(1)})
    =mex({2,1})
    =0

G(4)=mex({G(3),G(2)})
    =mex({0,2})
    =1
```

Pattern:

```text
n:    0 1 2 3 4 5 6
G(n): 0 1 2 0 1 2 0
```

So:

```text
G(n)=n%3
```

and losing states are exactly:

```text
G(n)=0
```

### Key insight

The modulo pattern from the simple subtraction game is actually a Grundy-number pattern.

---

<a id="2220-combining-independent-games"></a>

## 22.20 Combining Independent Games

If a game is the sum of independent impartial subgames with Grundy values:

```text
g1,g2,...,gn
```

the combined Grundy value is:

```text
G = g1 ^ g2 ^ ... ^ gn
```

Therefore:

```text
G==0 -> losing
G!=0 -> winning
```

### Example

Suppose three independent components have:

```text
G1=1
G2=2
G3=3
```

Then:

```text
1 ^ 2 ^ 3 = 0
```

Combined position is losing.

### Real-world scenario

Imagine several independent puzzle boards.

Each turn you may make a move on exactly one board.

The strategic values do not add numerically:

```text
not g1+g2+...
```

They combine by:

```text
XOR
```

This is the Sprague-Grundy theorem.

---

<a id="2221-periodicity-in-game-states"></a>

## 22.21 Periodicity in Game States

Small move sets often create repeating win/loss or Grundy patterns.

### Example — remove 1 or 2

```text
L W W | L W W | L W W | ...
```

Period:

```text
3
```

### Discovery workflow

Compute small states:

```text
0..20
```

Observe:

```text
pattern?
```

Then **prove** the repetition using transitions or modular reasoning.

### Real-world scenario — rotating shift schedule

A shift pattern may repeat every `k` days.

Knowing day:

```text
n mod k
```

is enough.

Game states can behave the same way.

### Warning

Observed periodicity is not proof.

A short prefix may accidentally repeat.

Derive why transitions preserve the cycle.

---

<a id="2222-games-reduced-to-parity-of-a-derived-quantity"></a>

## 22.22 Games Reduced to Parity of a Derived Quantity

Sometimes the number of objects is not the relevant parity.

A derived quantity is.

Possible examples:

```text
number of odd piles
number of inversions
distance to terminal state
number of forced operations
sum of floor(ai/k)
```

### Generic model

If every move changes a measure `M` by exactly one:

```text
M -> M-1
```

then the game lasts exactly `M` moves.

Winner:

```text
M odd  -> first
M even -> second
```

### Real-world scenario — cleanup game

Suppose every legal move resolves exactly one unresolved pair.

If there are initially:

```text
M=9
```

unresolved pairs and no move can change that rate, exactly nine moves occur.

The parity of `M`, not the raw input size, determines who moves last.

### Contest question

```text
"What quantity decreases predictably every turn?"
```

---

<a id="2223-forced-moves-and-game-compression"></a>

## 22.23 Forced Moves and Game Compression

Some states have only one meaningful legal move.

Long forced sequences can often be compressed.

### Example shape

```text
state A
  |
forced
  v
state B
  |
forced
  v
state C
```

If two forced moves occur before a real decision:

```text
turn changes twice
```

so the same player is effectively responsible for the next strategic choice.

### Real-world scenario — corridor board game

A token may have to travel through several cells with no branching.

Those cells do not represent strategic decisions.

Compress the corridor to:

```text
length parity
```

or another aggregate property.

### Modeling lesson

Separate:

```text
forced mechanics
```

from:

```text
actual choices
```

This can transform a complicated story into a small mathematical game.

---

<a id="2224-greedy-moves-vs-proven-winning-moves"></a>

## 22.24 Greedy Moves vs Proven Winning Moves

A game move that looks locally strongest is not automatically winning.

### Bad heuristic

```text
"Always remove as many stones as possible."
```

For remove-`1..3`, start at:

```text
n=6
```

Greedy removes `3`:

```text
6 -> 3
```

Opponent removes `3` and wins.

Correct move:

```text
6 % 4 = 2
```

Remove `2`:

```text
6 -> 4
```

Now maintain multiples of `4`.

### Real-world scenario

Taking the biggest immediate reward may give the opponent the strategically ideal state.

### Connection to Part 18

As with greedy algorithms, a game strategy needs proof:

```text
my move
   ↓
forces opponent into losing state
```

not merely:

```text
my move looks strongest
```

---

<a id="2225-minimax-vs-mathematical-modeling"></a>

## 22.25 Minimax vs Mathematical Modeling

General games may require game-tree search:

```text
state
 / | \
moves
```

But before using minimax, ask whether the state has mathematical structure.

### Search approach

Potentially:

```text
O(branching^depth)
```

### Mathematical approach

Could become:

```text
O(1) parity/modulo
O(n) XOR
O(number of states) Grundy DP
```

### Example

Remove `1..3` from `10^18` stones.

Simulation/game-tree search is impossible.

Mathematical model:

```text
10^18 % 4
```

is enough.

### Contest rule

```text
huge n + tiny regular move set
```

is a strong signal to search for:

```text
modulo
periodicity
invariant
closed form
```

---

<a id="2226-60-second-game-discovery-workflow"></a>

## 22.26 60-Second Game Discovery Workflow

```text
GAME STORY
    |
    v
Define state + legal moves + terminal rule
    |
    v
Who loses/wins when no move exists?
    |
    v
Compute tiny states
0,1,2,3,...
    |
    +-------------------------------+
    |               |               |
fixed moves?     one pile?      many independent
    |               |               |
 parity          modulo          XOR / Grundy
    |               |               |
    +---------------+---------------+
                    |
                    v
Find losing-state invariant
                    |
                    v
Can every W state move to L?
Can every move from L go to W?
                    |
                  YES
                    |
                    v
                 PROOF
```

### Fast contest questions

```text
1. What exactly is the game state?
2. What are the legal moves?
3. Is normal or misère play used?
4. What is the terminal state?
5. Which tiny states are W/L?
6. Does parity determine the number of moves?
7. Is there a modulo pattern?
8. Can I maintain a complementary/mirror invariant?
9. Are there multiple independent piles?
10. Does XOR appear?
11. Is this exactly Nim?
12. If not Nim, can I compute Grundy numbers?
13. Are components independent and combinable by XOR?
14. Am I seeing a pattern, or have I proved it?
```

---

<a id="2227-codeforces-recognition-map"></a>

## 22.27 Codeforces Recognition Map

| Statement clue | Mathematical game model |
|---|---|
| exactly one forced action per turn | move-count parity |
| remove `1..k` | modulo `k+1` |
| respond to opponent to restore total | complementary strategy |
| symmetric board/piles | mirror strategy |
| natural paired resources | pairing strategy |
| preserve remainder/parity | invariant |
| tiny W/L states repeat | periodicity + proof |
| several piles, remove any amount from one | Nim |
| Nim position | XOR of pile sizes |
| XOR is zero | losing Nim state |
| XOR non-zero | winning Nim state |
| need winning Nim move | make XOR zero |
| last move loses | misère warning |
| arbitrary finite impartial game | Grundy number |
| combine independent impartial games | XOR Grundy values |
| huge state, tiny regular moves | search modulo/closed form |
| forced corridor/sequence | compress forced moves |
| heuristic “largest move” | test against losing-state structure |

---

<a id="2228-proof-templates-for-game-problems"></a>

## 22.28 Proof Templates for Game Problems

### Template A — W/L induction

```text
Define losing states L.

Prove:

1. From every L state,
   every legal move goes to W.

2. From every non-L state,
   there exists a legal move to L.

Therefore exactly the states in L
are losing.
```

### Template B — complementary strategy

```text
Move first to invariant I.

Whenever opponent makes move x,
respond with complement f(x).

Prove:
opponent move + response
restores I.

Eventually opponent receives terminal losing state.
```

### Template C — mirror strategy

```text
Create symmetric state.

For every opponent move on one side,
perform mirrored legal move on other side.

After each pair:
symmetry restored.

Prove mirror remains legal until termination.
```

### Template D — Nim proof

```text
L states:
XOR = 0

prove:
zero XOR -> every move makes XOR non-zero

prove:
non-zero XOR -> some move makes XOR zero

therefore:
XOR=0 exactly characterizes losing states
```

### Template E — Grundy

```text
G(s)=mex(G(next states))

G(s)=0
iff no move reaches another zero-equivalent option
and state is losing.

For independent components:
Gtotal = G1 ^ G2 ^ ...
```

---

<a id="2229-common-mistakes"></a>

## 22.29 Common Mistakes

### 1. Simulating before defining W/L states

Start with the mathematical state classification.

### 2. Assuming odd/even without proving move count is fixed

If players can remove different amounts, number of turns may vary.

### 3. Finding a pattern from five values and treating it as proof

Use induction/invariant/modulo reasoning.

### 4. Applying Nim XOR to every pile game

Nim specifically allows:

```text
choose one pile
reduce it to any smaller non-negative size
```

Different move rules may require Grundy numbers.

### 5. Forgetting terminal convention

Check:

```text
last move wins?
last move loses?
no legal move loses?
```

### 6. Confusing sum with XOR

For Nim:

```text
pile sum
```

does not determine the winner.

### 7. Mirroring without proving the response is legal

Symmetry alone is not enough.

### 8. Using greedy moves

Largest immediate move may hand the opponent a losing-state invariant.

### 9. Forgetting independent-component requirement for XORing Grundy values

Components must interact only through the choice of which component to move in.

### 10. Overusing Grundy theory

If parity or modulo gives an O(1) proof, use the simpler model.

---

<a id="2230-fast-revision-card"></a>

## 22.30 Fast Revision Card

```text
========================================================
PART 22 — GAME MATHEMATICS
========================================================

CORE MODEL

story
  ↓
state + legal moves
  ↓
terminal state
  ↓
winning / losing classification
  ↓
parity / modulo / invariant / XOR / Grundy

--------------------------------------------

W / L RULE

WINNING:
exists move -> losing state

LOSING:
every move -> winning state

--------------------------------------------

FIXED NUMBER OF MOVES

last move wins:

moves odd  -> first wins
moves even -> second wins

ONLY valid when move count is forced

--------------------------------------------

REMOVE 1..K

losing states:

n % (k+1) == 0

winning move:

remove n % (k+1)

--------------------------------------------

COMPLEMENTARY STRATEGY

opponent removes x

you remove:

(k+1)-x

pair total:

k+1

--------------------------------------------

MIRROR STRATEGY

create symmetry
opponent changes one side
mirror on other side
restore symmetry

--------------------------------------------

NIM

nimSum =
a1 ^ a2 ^ ... ^ an

nimSum == 0
-> losing

nimSum != 0
-> winning

winning move:

find ai such that:

(ai ^ nimSum) < ai

replace:

ai -> ai ^ nimSum

--------------------------------------------

WHY NIM WORKS

XOR=0:
every legal move -> XOR != 0

XOR!=0:
there exists move -> XOR = 0

--------------------------------------------

GRUNDY

G(state)
=
mex({G(next)})

G=0 -> losing
G!=0 -> winning

independent games:

Gtotal = G1 ^ G2 ^ ...

--------------------------------------------

MISERE WARNING

last move loses

normal Nim theorem needs special handling,
especially when all piles are 1

--------------------------------------------

PROOF CHECK

Can I prove BOTH?

L -> every move reaches W

W -> some move reaches L

If YES:
losing-state characterization is proven.

--------------------------------------------

CORE CONTEST QUESTION

"What mathematical property separates
WINNING states from LOSING states?"

Then search:

parity
  ↓
modulo
  ↓
invariant / symmetry
  ↓
XOR
  ↓
Grundy
========================================================
```
