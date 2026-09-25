# Part 5. Parity

> **Core idea:** Parity means **even or odd** = remainder modulo `2`. For operation problems, ask: **does one operation preserve parity or flip it?**

## Table of Contents
- [5.1 The Rules](#51-the-rules)
- [5.2 How Parity Turns into a Solution](#52-how-parity-turns-into-a-solution)
- [5.3 Where It Shows Up in CF](#53-where-it-shows-up-in-cf)
- [Section Summary](#section-summary)
- [30-Second Revision](#30-second-revision)

---

## 5.1 The Rules

### ASCII / Structural Visual
```text
EVEN: 0,2,4,6,...  → x % 2 = 0
ODD : 1,3,5,7,...  → x % 2 != 0

E + E → E       O + O → E       E + O → O
E × anything → E                 O × O → O
```

### Core Formula / Rule

| Operation | Result |
|---|---|
| even + even | even |
| odd + odd | even |
| even + odd | odd |
| even × anything | even |
| odd × odd | odd |
| sum even | even number of odd terms |

### Short Derivation
```text
even = 2k
odd  = 2k+1

(2a+1)+(2b+1)
= 2(a+b+1)
→ even
```

### Visual Dry Run
```text
A = [2,3,5,8]

2 → E
3 → O
5 → O
8 → E

oddCount = 2 → even count of odds

sum = 18 → EVEN ✓
```

### Statement → Mathematical Model
```text
"Is the sum even?"
        ↓
count odd values
        ↓
oddCount % 2 == 0
```

### Codeforces Recognition
```text
even / odd
pairing
sum parity
fixed ±2 operations
        ↓
PARITY / MOD 2
```

### Minimal C++
```cpp
bool even = (x % 2 == 0);
bool odd  = (x % 2 != 0);
```

### Common Traps / Edge Cases
For negative integers, `x % 2 == 1` is unsafe: in C++, `-3 % 2 == -1`. Use `x % 2 != 0`.

> **Real-World Engineering Case:** A two-state alternating process can use parity to encode which state is active after a given number of steps.

---

## 5.2 How Parity Turns into a Solution

### ASCII / Structural Visual
```text
START
  ↓
identify quantity
  ↓
apply ONE operation
  ↓
parity preserved or flipped?
  ↓
compare with TARGET
  ↓
possible / impossible
```

### Core Formula / Rule
```text
x ← x + 2k
        ↓
(x+2k) % 2 = x % 2
        ↓
PARITY PRESERVED

x ← x + (2k+1)
        ↓
PARITY FLIPS
```

| Operation | Effect |
|---|---|
| `+2`, `-2` | preserve parity |
| any even change | preserve parity |
| `+1`, `-1` | flip parity |
| any odd change | flip parity |

### Short Derivation
```text
new = x + 2k

new % 2
= (x+2k) % 2
= x % 2
```

### Visual Dry Run — Add 2
```text
3(O)
 ↓ +2
5(O)
 ↓ +2
7(O)
 ↓ +2
9(O)

ODD never becomes EVEN.
```

Target example:
```text
start  = 3 → odd
target = 8 → even
only +2 allowed

→ impossible by parity
```

### Visual Dry Run — CF 4A Watermelon
```text
w = a+b
a,b must be positive EVEN

E + E = E
→ w must be even

smallest:
2+2=4
→ w >= 4

w=8 → 2+6 → YES
w=2 → too small → NO
w=7 → odd → NO
```

### Statement → Mathematical Model
```text
"Add 2 any number of times. Can x become y?"
                    ↓
x % 2 never changes
                    ↓
necessary condition:
x % 2 == y % 2
```

### Codeforces Recognition
```text
add/subtract 2 → parity invariant
add/subtract 1 → parity flips
reachability + fixed operation → compare parity effect
```

### Minimal C++
```cpp
bool sameParity = ((x & 1LL) == (y & 1LL));
bool watermelon = (w >= 4 && w % 2 == 0);
```

### Common Traps / Edge Cases
Matching parity can be necessary but not sufficient; bounds, positivity, and other constraints may still matter. The source's Watermelon example shows this with `w=2`: even, but impossible.

> **Real-World Engineering Case:** A counter changed only in steps of two can reach only values with its starting parity.

---

## 5.3 Where It Shows Up in CF

### ASCII / Structural Visual
```text
CF STATEMENT
     ↓
grid / ±2 / swap / pairing
     ↓
track only MOD 2 state
     ↓
many values collapse into
TWO CLASSES: EVEN / ODD
```

### Core Formula / Rule

| Clue | Parity Idea |
|---|---|
| grid step by step | each step flips `(x+y)%2` |
| add/subtract `2` | element parity fixed |
| swap two elements | permutation parity flips |
| pair elements | need an even count |

### Short Derivation — Grid
```text
(x,y) → (x+1,y)

old sum = x+y
new sum = x+y+1

change = 1
→ parity flips
```

### Visual Dry Run
```text
(0,0): x+y=0 → E
   ↓ right
(1,0): x+y=1 → O
   ↓ up
(1,1): x+y=2 → E

E → O → E
```

Pairing:
```text
5 items: ●● ●● ●  → one leftover
6 items: ●● ●● ●● → no leftover
```

### Statement → Mathematical Model
```text
"move one grid cell each second"
        ↓
x+y changes by ±1
        ↓
parity flips every move

"pair all objects"
        ↓
each pair consumes 2
        ↓
count % 2 == 0
```

### Codeforces Recognition
```text
grid/checkerboard → (x+y)%2
pair everything  → count%2
±2 operations    → parity invariant
odd # operations → parity flips
```

### Minimal C++
```cpp
int color = (x + y) & 1LL;
bool canPairAll = (cnt % 2 == 0);
```

### Common Traps / Edge Cases
Value parity and permutation parity are different concepts. A parity condition may also be only one part of the full solution.

> **Real-World Engineering Case:** Checkerboard partitioning maps every coordinate into one of two classes using `(x+y)%2`.

---

## Section Summary

| Statement Clue | Pattern | Quick Test |
|---|---|---|
| even / odd | modulo 2 | `x%2` |
| sum even | odd-count parity | `oddCount%2==0` |
| add/subtract even | invariant | parity preserved |
| add/subtract odd | flip | parity changes |
| pair all items | count parity | `count%2==0` |
| grid checkerboard | coordinate parity | `(x+y)%2` |

## 30-Second Revision
```text
┌──────────────────────────────────────────────────────┐
│            PARITY — 30 SECOND REVISION               │
├──────────────────────────────────────────────────────┤
│ even                  → x % 2 == 0                   │
│ odd                   → x % 2 != 0                   │
│ odd + odd             → even                         │
│ even + odd            → odd                          │
│ sum even              → even number of odd terms     │
│ ±2                    → parity preserved             │
│ ±1                    → parity flips                 │
│ pair everything       → count even                   │
│ grid color            → (x+y)%2                      │
└──────────────────────────────────────────────────────┘
```
