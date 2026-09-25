# Part 2. Algebra for Competitive Programming

> Many Codeforces statements hide equations. The goal is to turn **story → variables → equations → simplification → direct formula / smaller search**.

## Table of Contents

- [Algebra Form 1. Rearranging Equations (Sum and Difference)](#algebra-form-1-rearranging-equations-sum-and-difference)
- [Algebra Form 2. Isolating a Variable](#algebra-form-2-isolating-a-variable)
- [Algebra Form 3. Substitution](#algebra-form-3-substitution)
- [Algebra Form 4. Difference of Squares](#algebra-form-4-difference-of-squares)
- [Algebra Form 5. Expansions](#algebra-form-5-expansions)
- [Algebra Form 6. Pairwise Sums](#algebra-form-6-pairwise-sums)
- [Algebra Form 7. Linear Equation `ax + by = c`](#algebra-form-7-linear-equation-ax--by--c)
- [Algebra Form 8. Systems of Equations](#algebra-form-8-systems-of-equations)
- [Algebra Form 9. Inequalities (Intersection of Constraints)](#algebra-form-9-inequalities-intersection-of-constraints)
- [Algebra Form 10. Bounding (Lower Bound + Construction)](#algebra-form-10-bounding-lower-bound--construction)
- [Section Summary](#section-summary)
- [30-Second Revision](#30-second-revision)

---

## Algebra Form 1. Rearranging Equations (Sum and Difference)

### Structural Visual

```text
x + y = S
x - y = D
─────────── add
2x    = S + D       → x = (S + D) / 2

x + y = S
x - y = D
─────────── subtract
2y    = S - D       → y = (S - D) / 2
```

| Goal | Formula | Required Check |
|---|---|---|
| Recover `x` | `(S + D) / 2` | `S + D` even |
| Recover `y` | `(S - D) / 2` | `S - D` even |
| Non-negative solution | `x >= 0`, `y >= 0` | if problem requires it |

### Visual Dry Run

```text
S = 10, D = 4

x = (10 + 4) / 2 = 7
y = (10 - 4) / 2 = 3

Check:
7 + 3 = 10 ✓
7 - 3 = 4  ✓
```

### Statement → Mathematical Model

```text
"Two numbers have sum 10 and difference 4"
                    ↓
x + y = 10
x - y = 4
                    ↓
add / subtract equations
                    ↓
x = 7, y = 3
```

### Codeforces Recognition

```text
"sum and difference are known"
"sum and max-min are known"
        ↓
ADD / SUBTRACT EQUATIONS
```

```cpp
long long x = (S + D) / 2;
long long y = (S - D) / 2;
```

> **Real-World Case:** If total incoming and outgoing traffic difference are known, two linear equations can recover each direction's traffic.

---

## Algebra Form 2. Isolating a Variable

### Structural Visual

```text
a*x + b = c
    ↓ subtract b
a*x = c - b
    ↓ divide by a
x = (c - b) / a
```

| Condition | Meaning |
|---|---|
| `a != 0` | division is defined |
| `(c-b) % a == 0` | integer `x` exists |
| otherwise | no integer solution |

### Visual Dry Run

```text
3x + 5 = 20
3x     = 15
x      = 5

Check:
3(5) + 5 = 20 ✓

But:
3x + 5 = 21
3x = 16
16 % 3 != 0
→ no integer x
```

### Statement → Mathematical Model

```text
"3 identical boxes plus 5 loose items total 20"
                    ↓
3x + 5 = 20
                    ↓
3x = 15
                    ↓
x = 5
```

### Codeforces Recognition

```text
"unknown appears once in a linear equation"
        ↓
ISOLATE IT
        ↓
replace search with O(1) arithmetic
```

```cpp
bool ok = (a != 0 && (c - b) % a == 0);
long long x = ok ? (c - b) / a : 0;
```

> **Real-World Case:** Recovering per-server capacity from `servers × capacity + overhead = total`.

---

## Algebra Form 3. Substitution

### Structural Visual

```text
x + y = n
    ↓
y = n - x
    ↓ substitute into objective
cost = 3x + 5y
     = 3x + 5(n-x)
     = 5n - 2x
```

| Before | After Substitution |
|---|---|
| variables `x, y` | one variable `x` |
| constraint `x+y=n` | `y=n-x` |
| objective `3x+5y` | `5n-2x` |

### Visual Dry Run

```text
n = 10

cost = 5n - 2x
     = 50 - 2x

x=0  → 50
x=5  → 40
x=10 → 30

As x increases, cost decreases.
So choose the largest allowed x.
```

### Statement → Mathematical Model

```text
"Choose x cheap units and y expensive units"
"x + y = n"
                    ↓
y = n - x
                    ↓
cost = 3x + 5(n-x)
                    ↓
cost = 5n - 2x
                    ↓
maximize x within constraints
```

### Codeforces Recognition

```text
"two variables + one equation"
        ↓
SUBSTITUTE ONE AWAY
```

```cpp
long long y = n - x;
long long cost = 3 * x + 5 * y;
```

> **Real-World Case:** Given a fixed worker count, substitute `slowWorkers = total-fastWorkers` to express cost using one decision variable.

---

## Algebra Form 4. Difference of Squares

### Structural Visual

```text
a² - b²
    ↓ factor
(a-b)(a+b)

Let:
d = a-b
e = a+b

Then:
N = d*e
```

| Relation | Formula |
|---|---|
| Factorization | `a²-b² = (a-b)(a+b)` |
| Recover `a` | `(d+e)/2` |
| Recover `b` | `(e-d)/2` |
| Integer requirement | `d` and `e` same parity |

### Visual Dry Run

```text
N = 15

factor pair:
15 = 3 × 5

d = 3 = a-b
e = 5 = a+b

a = (3+5)/2 = 4
b = (5-3)/2 = 1

Check:
4² - 1² = 16 - 1 = 15 ✓
```

### Statement → Mathematical Model

```text
"Can N be written as difference of two squares?"
                    ↓
N = a² - b²
                    ↓
N = (a-b)(a+b)
                    ↓
search compatible factor pairs
```

### Codeforces Recognition

```text
"a² - b²"
"difference of squares"
        ↓
FACTOR INTO PRODUCT
```

```cpp
long long a = (d + e) / 2;
long long b = (e - d) / 2;
```

> **Real-World Case:** Algebraic factorization can replace direct search when a constraint naturally decomposes into two multiplicative factors.

---

## Algebra Form 5. Expansions

### Structural Visual

```text
(Sum A)²

= (A1 + A2 + ... + An)²

= Σ Ai² + 2 Σ(i<j) Ai*Aj

Therefore:

Σ(i<j) Ai*Aj
= ((ΣAi)² - ΣAi²) / 2
```

| Identity | Formula |
|---|---|
| Square of sum | `(a+b)² = a²+2ab+b²` |
| Square of difference | `(a-b)² = a²-2ab+b²` |
| Three terms | `(a+b+c)² = a²+b²+c²+2(ab+bc+ca)` |
| All pair products | `((sum A)² - sum(Ai²))/2` |

### Visual Dry Run

```text
A = [1, 2, 4]

S = 1+2+4 = 7
Q = 1²+2²+4² = 21

pairProducts = (S²-Q)/2
             = (49-21)/2
             = 14

Direct:
1×2 + 1×4 + 2×4
= 2 + 4 + 8
= 14 ✓
```

### Statement → Mathematical Model

```text
"sum Ai*Aj over every pair i<j"
        ↓
naive: enumerate O(n²) pairs
        ↓
expand (ΣAi)²
        ↓
ΣAi² + 2ΣAiAj
        ↓
pair sum in O(n)
```

### Codeforces Recognition

```text
"all pair products"
"sum over every i<j"
        ↓
TRY EXPANDING (ΣA)²
```

```cpp
long long pairProducts = (sum * sum - sumSquares) / 2;
```

> **Real-World Case:** Aggregate pair interactions can sometimes be computed from global totals instead of materializing every pair.

---

## Algebra Form 6. Pairwise Sums

### Structural Visual

```text
Σ(i<j) (Ai-Aj)²

expand each pair:
Ai² + Aj² - 2AiAj

all square terms → (n-1)Q
all cross terms  → S²-Q

answer:
(n-1)Q - (S²-Q)
= nQ - S²
```

| Goal | Formula |
|---|---|
| Squared pairwise differences | `n*ΣAi² - (ΣAi)²` |
| Absolute pairwise differences after sorting | `Σ Ak*(2k-n-1)` for 1-indexed `k` |

### Visual Dry Run

```text
A = [1, 2, 4]
n = 3
S = 7
Q = 21

Formula:
nQ - S²
= 3×21 - 49
= 14

Direct:
(1-2)² + (1-4)² + (2-4)²
= 1 + 9 + 4
= 14 ✓
```

Second identity:

```text
sorted A = [1,2,4]

k=1: 1×(2-3-1) = -2
k=2: 2×(4-3-1) =  0
k=3: 4×(6-3-1) =  8
                       ──
                        6

Direct:
abs(1-2)+abs(1-4)+abs(2-4)
= 1+3+2 = 6 ✓
```

### Statement → Mathematical Model

```text
"sum distance / squared distance over all pairs"
        ↓
O(n²) looks natural
        ↓
expand / count each element's contribution
        ↓
O(n) after aggregates
(or O(n log n) if sorting is needed)
```

### Codeforces Recognition

```text
"for every pair i<j"
        ↓
EXPANSION / CONTRIBUTION COUNTING
```

```cpp
long long squaredDiffSum = n * sumSquares - sum * sum;
```

> **Real-World Case:** Pairwise variance-like metrics can be computed from count, sum, and sum of squares without storing every pair.

---

## Algebra Form 7. Linear Equation `ax + by = c`

### Structural Visual

```text
ax + by = c

g = gcd(a,b)

g divides ax
g divides by
        ↓
g must divide ax+by
        ↓
g must divide c

Integer solution exists iff:
c % g == 0
```

| Check | Result |
|---|---|
| `gcd(a,b)` divides `c` | integer solutions exist |
| `gcd(a,b)` does not divide `c` | impossible |

### Visual Dry Run

```text
4x + 6y = 10

gcd(4,6) = 2
10 % 2 = 0
→ integer solution exists

Example:
x=1, y=1
4(1)+6(1)=10 ✓

Now:
4x + 6y = 9

9 % 2 != 0
→ impossible
```

### Statement → Mathematical Model

```text
"Use x items of size a and y items of size b
to make exactly c"
        ↓
ax + by = c
        ↓
gcd(a,b) must divide c
```

### Codeforces Recognition

```text
"exact total using two step sizes"
"integer x and y"
        ↓
LINEAR DIOPHANTINE EQUATION
```

```cpp
long long g = std::gcd(a, b);
bool solvable = (c % g == 0);
```

> **Real-World Case:** Combining fixed packet or block sizes to hit an exact total is an integer linear-equation feasibility problem.

---

## Algebra Form 8. Systems of Equations

### Structural Visual

```text
x + y = 10
2x + y = 14

subtract first from second:

x = 4
        ↓
y = 10-4 = 6
```

| Situation | Modeling Consequence |
|---|---|
| enough independent equations | unknowns may be fixed |
| fewer independent equations than unknowns | at least one variable remains free |
| one free bounded variable | loop only over that variable |

### Visual Dry Run

```text
x + y = 10
2x + y = 14

(2x+y) - (x+y) = 14-10
x = 4

y = 10-4 = 6

Check:
4+6 = 10 ✓
8+6 = 14 ✓
```

### Statement → Mathematical Model

```text
"two unknown counts"
+
"two independent totals"
        ↓
write two equations
        ↓
eliminate one variable
        ↓
solve the other
```

### Codeforces Recognition

```text
"multiple unknowns + multiple independent constraints"
        ↓
SYSTEM OF EQUATIONS
```

```cpp
long long x = T2 - T1;
long long y = T1 - x;   // for x+y=T1, 2x+y=T2
```

> **Real-World Case:** Two aggregate measurements can recover two unknown category counts when the equations are independent.

---

## Algebra Form 9. Inequalities (Intersection of Constraints)

### Structural Visual

```text
Constraint A: x >= 3     [3 ───────────────→
Constraint B: x <= 8     ←────────────── 8]

Intersection:
                         [3 ─────── 8]
```

| Rule | Formula |
|---|---|
| all lower bounds | `L = max(lower bounds)` |
| all upper bounds | `R = min(upper bounds)` |
| feasible | `L <= R` |
| impossible | `L > R` |

### Visual Dry Run

```text
x >= 3
x >= 5
x <= 10
x <= 8

Strongest lower bound:
L = max(3,5) = 5

Strongest upper bound:
R = min(10,8) = 8

Feasible values:
5 <= x <= 8
```

### Statement → Mathematical Model

```text
"at least 3"
"at least 5"
"at most 10"
"at most 8"
        ↓
collect lower / upper bounds
        ↓
L = max(...)
R = min(...)
```

### Codeforces Recognition

```text
"at least"
"at most"
"must satisfy all"
        ↓
INTERSECT CONSTRAINTS
```

```cpp
long long L = std::max(L1, L2);
long long R = std::min(R1, R2);
bool feasible = (L <= R);
```

> **Real-World Case:** Scheduling windows are feasible only where every availability constraint overlaps.

---

## Algebra Form 10. Bounding (Lower Bound + Construction)

### Structural Visual

```text
OPTIMAL ANSWER
     ↑
     │ prove achievable
     │
     X  ← construction reaches X
     │
     │ prove impossible beyond X
     ↓
BOUND
```

For the candy-pile example, the source uses an upper bound because the objective is to **maximize** days:

```text
S = a+b+c
M = max(a,b,c)

each day consumes 2 candies
→ days <= S/2

largest pile needs a partner from other piles
→ days <= S-M

answer <= min(S/2, S-M)

construction reaches that bound
→ answer = min(S/2, S-M)
```

### Visual Dry Run

```text
piles = [3,3,4]

S = 10
M = 4

Bound 1:
days <= 10/2 = 5

Bound 2:
days <= 10-4 = 6

So:
days <= min(5,6) = 5

Construction:
[3,3,4]
 → [2,3,3]
 → [2,2,2]
 → [1,1,2]
 → [0,1,1]
 → [0,0,0]

5 days achieved ✓
Therefore answer = 5
```

### Statement → Mathematical Model

```text
"maximize / minimize"
        ↓
derive an unavoidable bound
        ↓
prove no solution can beat it
        ↓
construct a solution attaining it
        ↓
bound = optimum
```

### Codeforces Recognition

```text
"minimum possible"
"maximum possible"
        ↓
BOUND + CONSTRUCTION
```

```cpp
long long S = a + b + c;
long long M = std::max({a, b, c});
long long days = std::min(S / 2, S - M);
```

> **Real-World Case:** Capacity planning often proves a theoretical resource bound and then constructs an allocation that reaches it.

---

## Section Summary

| Goal / Statement Clue | Algebraic Pattern | Safe Implementation / Formula |
|---|---|---|
| sum + difference known | rearrange equations | `x=(S+D)/2`, `y=(S-D)/2` |
| one unknown in linear equation | isolate variable | `(c-b)/a` after divisibility check |
| two variables tied by one equation | substitution | replace one variable |
| difference of squares | factorization | `(a-b)(a+b)` |
| all pair products | expansion | `(S*S-Q)/2` |
| all pair squared differences | contribution identity | `n*Q-S*S` |
| exact total from two integer step sizes | Diophantine feasibility | `c % gcd(a,b) == 0` |
| several independent equations | system | eliminate variables |
| at least / at most | inequality intersection | `L=max(...)`, `R=min(...)` |
| prove optimum | bound + construction | derive bound, then attain it |

## 30-Second Revision

```text
┌─────────────────────────────────────────────────────────────┐
│                 ALGEBRA — 30 SECOND REVISION                │
├─────────────────────────────────────────────────────────────┤
│ sum + difference       → add/subtract equations             │
│ one unknown            → isolate                            │
│ linked variables       → substitute                         │
│ a²-b²                  → (a-b)(a+b)                         │
│ all pair products      → expand (ΣA)²                       │
│ all pair differences   → contribution / expansion           │
│ ax+by=c                → gcd(a,b) divides c                  │
│ multiple equations     → eliminate variables                │
│ at least / at most     → intersect inequalities             │
│ min/max proof          → bound + construction               │
└─────────────────────────────────────────────────────────────┘
```
