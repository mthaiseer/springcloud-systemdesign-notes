# Part 1. Arithmetic Foundations

## 1.0 Reading the Symbols (Cheat Sheet)

```
        -3.5             0.0             3.5
  <------|---------------|---------------|------>
     ⌊-3.5⌋ = -4                     ⌊3.5⌋ = 3   [Floor: Shift Left  ←]
     ⌈-3.5⌉ = -3                     ⌈3.5⌉ = 4   [Ceil:  Shift Right →]
```

| Symbol | Name | Formal Definition | Standard Implementation | Example |
| :--- | :--- | :--- | :--- | :--- |
| $\lfloor x \rfloor$ | **Floor** | $\max \{ k \in \mathbb{Z} \mid k \le x \}$ | `floor(x)` / `a / b` | $\lfloor 3.9 \rfloor = 3, \lfloor -3.1 \rfloor = -4$ |
| $\lceil x \rceil$ | **Ceiling** | $\min \{ k \in \mathbb{Z} \mid k \ge x \}$ | `ceil(x)` / `(a + b - 1) / b` | $\lceil 3.1 \rceil = 4, \lceil -3.9 \rceil = -3$ |
| $a \bmod b$ | **Modulo** | $r = a - b \cdot \lfloor a / b \rfloor$ | `a % b` | $17 \bmod 5 = 2$ |
| $[a, b)$ | **Half-Open Range** | $\{ x \in \mathbb{R} \mid a \le x < b \}$ | `for(int i = a; i < b; ++i)` | $[0, 5) \implies \{0, 1, 2, 3, 4\}$ |
| $\approx$ | **Estimate** | $2^{10} = 1024 \approx 10^3$ | `1 << 10` | $2^{30} \approx 10^9$ (1 Gigabyte) |

```cpp
// Common representation in C++ / Java / Python
double fl = std::floor(3.9);  // 3.0
double cl = std::ceil(3.1);   // 4.0
int rem   = 17 % 5;           // 2
```

> **Real-World Case:** **Pagination APIs.** Range $[0, 10)$ retrieves items 0 through 9, matching zero-indexed database record offsets without off-by-one errors.

---

## 1.1 Quotient and Remainder

```
Dividend (a = 17)
┌───────────┬───────────┬───────────┬─────┐
│  Bucket 1 │  Bucket 2 │  Bucket 3 │ Rem │  Divisor (b) = 5
│     5     │     5     │     5     │  2  │  Quotient (q) = 3, Remainder (r) = 2
└───────────┴───────────┴───────────┴─────┘
```

| Division Behavior | Mathematical Rule | Language Support | Example ($a = -17, b = 5$) |
| :--- | :--- | :--- | :--- |
| **Division Algorithm** | $a = b \cdot q + r \quad (0 \le r < |b|)$ | Universal Math Standard | $q = -4, r = 3$ |
| **Truncating Division** | Rounds fractional quotient toward zero | C, C++, Java, C# | `-17 / 5 = -3`, `-17 % 5 = -2` |
| **Floored Division** | Rounds quotient down toward $-\infty$ | Python | `-17 // 5 = -4`, `-17 % 5 = 3` |

```cpp
// Safe positive modulo in C++/Java (guarantees output in range [0, b - 1])
int safe_mod(int a, int b) { return (a % b + b) % b; }
```

> **Real-World Case:** **Circular Ring Buffers.** Traversing backward from array index `0` via step `-1` wraps to the last element using `safe_mod(-1, N)`.

---

## 1.2 Floor, Ceiling, and Ceil-Division

```
Items (a = 10)  : [x][x][x] | [x][x][x] | [x][x][x] | [x]
Bucket Size (b) :    3      |    3      |    3      |  3  ---> Requires 4 Buckets
```

| Division Type | Real Formula | Pure Integer Code ($a, b > 0$) | Edge Case Behavior |
| :--- | :--- | :--- | :--- |
| **Floor Division** | $\lfloor a / b \rfloor$ | `a / b` | Direct truncated integer division |
| **Ceil Division** | $\lceil a / b \rceil$ | `(a + b - 1) / b` | Prevents precision loss from `(double)` cast |

```cpp
// Exact ceil-division without floating-point conversion
int total_pages = (items + page_size - 1) / page_size;
```

> **Real-World Case:** **Server Auto-Scaling.** Packing $100$ container tasks into virtual hosts with $30$-task capacity provisions $\lceil 100/30 \rceil = \texttt{(100 + 29) / 30} = 4$ servers.

---

## 1.3 Absolute Value, Min, and Max

```
   Distance = |a - b|
   <─────── 7 ───────>
---+─────────────────+--->
  a=-2              b=5
```

| Function | Algebraic Formula | Property / Identity |
| :--- | :--- | :--- |
| **Absolute Value** | $|x| = x \text{ if } x \ge 0 \text{ else } -x$ | $|x| = \sqrt{x^2}$ |
| **Maximum** | $\max(a, b) = \frac{a + b + |a - b|}{2}$ | $\max(a, b) \ge a \text{ and } \max(a, b) \ge b$ |
| **Minimum** | $\min(a, b) = \frac{a + b - |a - b|}{2}$ | $\min(a, b) \le a \text{ and } \min(a, b) \le b$ |

```cpp
// Clamp coordinate 'x' strictly within interval [LOW, HIGH]
int clamped_x = std::max(LOW, std::min(x, HIGH));
```

> **Real-World Case:** **UI Coordinate Constraints.** Restricting a dragged window position `x` between `0` (left screen boundary) and `1920` (right screen boundary).

---

## 1.4 Intervals and Inequalities

```
Interval 1: [ s1 ══════════════ e1 ]
Interval 2:         [ s2 ══════════════ e2 ]
Overlap   :         [ max(s1,s2) ── min(e1,e2) ]  ==> Valid if max(s1,s2) <= min(e1,e2)
```

| Interval Type | Notation | Inclusion Condition | Count of Integers |
| :--- | :--- | :--- | :--- |
| **Closed** | $[a, b]$ | $a \le x \le b$ | $b - a + 1$ |
| **Half-Open** | $[a, b)$ | $a \le x < b$ | $b - a$ |
| **Open** | $(a, b)$ | $a < x < b$ | $b - a - 1$ |

```cpp
// Overlap evaluation for closed intervals [s1, e1] and [s2, e2]
bool is_overlapping = std::max(s1, s2) <= std::min(e1, e2);
```

> **Real-World Case:** **Calendar Conflict Detection.** Evaluating whether an active calendar event (2:00 PM – 3:00 PM) conflicts with a new request (2:30 PM – 3:30 PM).

---

## 1.5 Powers, Logarithms, and Size Estimates

```
Scale     Binary Value    Power of 2    Decimal Approx.    SI Unit
------------------------------------------------------------------
Kilo      2^10            1,024         ≈ 10^3             KB / Thousand
Mega      2^20            1,048,576     ≈ 10^6             MB / Million
Giga      2^30            1,073,741,824 ≈ 10^9             GB / Billion
Exa       2^60            1.15 × 10^18  ≈ 10^18            EB / Exabyte
```

| Identity / Rule | Exact Equation | Algorithm Design Usage |
| :--- | :--- | :--- |
| **Product Property** | $\log_b(A \cdot B) = \log_b(A) + \log_b(B)$ | Decomposing search space trees |
| **Base Conversion** | $\log_b(A) = \frac{\log_c(A)}{\log_c(b)}$ | Translating natural logs to binary operations |
| **Integer Bit Length** | $\lfloor \log_2 N \rfloor + 1$ | Allocating minimal memory register sizes |

```cpp
// Total binary bits required to store scalar value N
int bits_required = (N == 0) ? 1 : std::floor(std::log2(N)) + 1;
```

> **Real-World Case:** **Capacity Planning.** Storing $10^9$ uncompressed 64-bit integers requires $\approx 1\text{ GB} \times 8\text{ bytes} = 8\text{ GB}$ of RAM.

---

## 1.6 Overflow: Choosing `int` or `long long`

```
32-bit int:  [-2,147,483,648 ─────────── 0 ─────────── 2,147,483,647] ≈ ±2 × 10^9
64-bit long: [-9.22 × 10^18 ──────────── 0 ──────────── 9.22 × 10^18] ≈ ±9 × 10^18
```

| Type | Bit Width | Signed Numeric Range | Upper Bound Limit |
| :--- | :--- | :--- | :--- |
| `int` | 32 bits | $-2^{31} \dots 2^{31}-1$ | $\approx \pm 2.14 \times 10^9$ |
| `unsigned int` | 32 bits | $0 \dots 2^{32}-1$ | $\approx 4.29 \times 10^9$ |
| `long long` / `int64_t` | 64 bits | $-2^{63} \dots 2^{63}-1$ | $\approx \pm 9.22 \times 10^{18}$ |

```cpp
int a = 100000, b = 100000;
// Cast to 64-bit prior to multiplication to avoid 32-bit register overflow
long long product = (long long)a * b; 
```

> **Real-World Case:** **Financial Micro-Transactions.** Tracking global monetary balances in cents. $20\text{ million dollars} = 2 \times 10^9\text{ cents}$, approaching 32-bit integer limits.

---

## 1.7 Rounding Without Decimals

```
Number (a) = 17, Divisor (b) = 5   ==>  Exact: 3.4
  Shifted Dividend: a + (b / 2) = 17 + 2 = 19
  Integer Division: 19 / 5 = 3    ==>  Rounds down to 3

Number (a) = 18, Divisor (b) = 5   ==>  Exact: 3.6
  Shifted Dividend: a + (b / 2) = 18 + 2 = 20
  Integer Division: 20 / 5 = 4    ==>  Rounds up to 4
```

| Rounding Target | Formula | Integer Arithmetic ($a, b > 0$) |
| :--- | :--- | :--- |
| **Floor (Truncate)** | $\lfloor a / b \rfloor$ | `a / b` |
| **Ceil (Round Up)** | $\lceil a / b \rceil$ | `(a + b - 1) / b` |
| **Nearest Integer** | $\lfloor \frac{a + b/2}{b} \rfloor$ | `(a + b / 2) / b` |

```cpp
// Round division of non-negative integers to nearest whole integer
int rounded_value = (a + b / 2) / b;
```

> **Real-World Case:** **E-Commerce Checkout.** Computing sub-cent tax allocations on order totals purely through integer cents, avoiding float precision artifacts.

---

## 1.8 Section Summary (What to Remember)

| Goal | Formula / Pattern | Code Snippet |
| :--- | :--- | :--- |
| **Ceil-Division** | Group items into $b$-sized buckets | `(a + b - 1) / b` |
| **Nearest Integer** | Divide and round to nearest | `(a + b / 2) / b` |
| **Safe Modulo** | Wrap negative array indices safely | `(a % b + b) % b` |
| **Interval Overlap** | Detect overlapping ranges | `max(s1, s2) <= min(e1, e2)` |
| **Safe Midpoint** | Prevent addition overflow | `low + (high - low) / 2` |
| **64-bit Promotion** | Prevent product overflow | `(long long)a * b` |