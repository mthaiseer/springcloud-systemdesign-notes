# Part 9. Min/Max Mathematical Transformations

> **Core idea:** Min/max problems often become easier after replacing the story with **bounds, balance, extrema, or separated expressions**.

## Table of Contents
- [9.1 Min/Max Identities](#91-minmax-identities)
- [9.2 Minimize the Maximum / Maximize the Minimum](#92-minimize-the-maximum--maximize-the-minimum)
- [9.3 Balance Two Quantities](#93-balance-two-quantities)
- [9.4 Extremal Arguments](#94-extremal-arguments)
- [9.5 Multiple Bounds](#95-multiple-bounds)
- [9.6 Separating Variables](#96-separating-variables)
- [30-Second Revision](#30-second-revision)

---

## 9.1 Min/Max Identities

### ASCII / Structural Visual
```text
a = 3                 b = 9

3 ----------- 9
min           max

sum = 12
gap = 6

max = (sum + gap)/2 = 9
min = (sum - gap)/2 = 3
```

### Core Formula / Rule
```text
max(a,b) = (a+b+abs(a-b))/2
min(a,b) = (a+b-abs(a-b))/2

max(a,b)+min(a,b) = a+b
max(a,b)*min(a,b) = a*b
```

### Short Derivation
Assume `a <= b`:
```text
abs(a-b)=b-a

(a+b+(b-a))/2 = b = max
(a+b-(b-a))/2 = a = min
```

### Real-World Intuition

Two API replicas report different latencies. If monitoring gives you their **combined latency** and the **difference between them**, the larger and smaller values can be recovered without guessing.

### Real-World Example — API Latency

**Scenario:** Two replicas have a combined latency of `120 ms`, and one is `40 ms` slower than the other.

**Variables:**
```text
sum = 120
gap = 40
slow = ?
fast = ?
```

**Model:**
```text
slow + fast = 120
slow - fast = 40
        ↓
slow = (120+40)/2
fast = (120-40)/2
```

**Calculation:**
```text
slow = 160/2 = 80 ms
fast =  80/2 = 40 ms
```

**Result:**
```text
slower replica = 80 ms
faster replica = 40 ms
```

```text
API latency
    ↓
sum + absolute difference
    ↓
min/max identity
```

### Visual Dry Run
```text
a=3, b=9
sum = 12
gap = 6

max = (12+6)/2 = 9
min = (12-6)/2 = 3
```

### Statement → Mathematical Model
```text
"larger and smaller of two values"
          ↓
replace with max/min
          ↓
sometimes rewrite using
sum ± absolute difference
```

### Codeforces Recognition
```text
larger / smaller
extreme of two values
sum + difference information
        ↓
MIN / MAX identities
```

### Minimal C++
```cpp
long long mx = max(a,b);
long long mn = min(a,b);
```

### Common Traps / Edge Cases
The algebraic identity can overflow at `a+b` even when `max(a,b)` itself is representable. In code, `std::min`/`std::max` is normally safer.

> **Real-World Engineering Case:** If two server latencies have known total `12 ms` and gap `6 ms`, the slower and faster latencies are `(12+6)/2=9 ms` and `(12-6)/2=3 ms`.

---

## 9.2 Minimize the Maximum / Maximize the Minimum

### ASCII / Structural Visual
```text
candidate answer X

too small         feasible
   ✗ ✗ ✗ ✗ │ ✓ ✓ ✓ ✓
           boundary
              ↑
        smallest feasible X
```

### Core Formula / Rule
Source pattern:
```text
minimize maximum
    ↓
guess X
    ↓
can(X)?
    ↓
binary search if feasibility is monotonic
```

Likewise:
```text
maximize minimum
    ↓
find largest feasible X
```

### Short Derivation
If:
```text
can(X) = true
```
and every larger `X` is also feasible, feasibility has the form:
```text
false false false true true true
                  ↑
               answer
```

### Real-World Intuition

Suppose files must be distributed across servers while minimizing the **largest load on any server**. Instead of directly constructing the perfect load, guess a candidate maximum `X` and ask whether the distribution is possible.

### Real-World Example — Minimum Server Capacity

**Scenario:** Jobs have sizes `[7,2,5,10,8]` and must be processed in order by `2` workers. Find the smallest possible maximum workload.

**Variables:**
```text
jobs = [7,2,5,10,8]
workers = 2
X = candidate maximum load
```

**Model:**
```text
guess X
   ↓
can all jobs be split into <= 2 contiguous groups
with each group sum <= X?
   ↓
false false ... true true
   ↓
smallest feasible X
```

**Calculation:**
```text
X = 17:
[7,2,5] = 14
[10]     = 10
[8]      = 8
needs 3 groups → NOT feasible

X = 18:
[7,2,5] = 14
[10,8]  = 18
needs 2 groups → feasible
```

**Result:**
```text
minimum possible maximum workload = 18
```

```text
capacity planning
    ↓
monotonic can(X)
    ↓
binary search on answer
```

### Visual Dry Run
Suppose a capacity limit `X` is feasible exactly from `X=7` onward:
```text
X       4 5 6 7 8 9
can(X)  F F F T T T
              ↑
minimum feasible maximum = 7
```

### Statement → Mathematical Model
```text
"minimize the largest load"
        ↓
answer = X
        ↓
ask: can every load be <= X?
        ↓
monotonic feasibility?
        ↓
binary search X
```

### Codeforces Recognition
```text
minimum possible maximum
maximum possible minimum
"at most X" feasibility
"at least X" feasibility
        ↓
BINARY SEARCH ON ANSWER
```

### Minimal C++
```cpp
while (lo < hi) {
    long long mid = lo + (hi-lo)/2;
    if (can(mid)) hi = mid;
    else lo = mid+1;
}
```

### Common Traps / Edge Cases
Binary search requires a monotonic predicate. Do not binary-search merely because the problem says “minimum” or “maximum.”

> **Real-World Engineering Case:** To minimize peak load across machines, test a candidate capacity `X`: can all jobs be assigned without any machine exceeding `X`? If larger capacities never hurt feasibility, search for the smallest feasible capacity.

---

## 9.3 Balance Two Quantities

### ASCII / Structural Visual
```text
total = 11

split:
0 | 11   gap 11
1 | 10   gap  9
...
5 |  6   gap  1  ← best balance
```

### Core Formula / Rule
```text
x + y = total

To make x and y as close as possible:
x ≈ total/2
y ≈ total/2
```

### Short Derivation
Since:
```text
y = total-x

gap = abs(x-y)
    = abs(2x-total)
```
the gap is minimized when `2x` is closest to `total`.

### Real-World Intuition

When identical work must be split between two equivalent workers, the largest imbalance occurs when one gets much more. The best split is therefore as close to half as possible.

### Real-World Example — Load Balancing

**Scenario:** `11` identical requests must be assigned to two replicas as evenly as possible.

**Variables:**
```text
total = 11
x = requests on replica A
y = requests on replica B
```

**Model:**
```text
x+y = 11
minimize abs(x-y)
        ↓
x ≈ 11/2
y ≈ 11/2
```

**Calculation:**
```text
x = floor(11/2) = 5
y = 11-5 = 6

difference = abs(5-6) = 1
```

**Result:**
```text
best split = 5 and 6 requests
```

```text
load balancing
    ↓
fixed sum + minimum gap
    ↓
split near total/2
```

### Visual Dry Run
```text
total = 11

floor(11/2)=5
other = 6

abs(5-6)=1
```

### Statement → Mathematical Model
```text
"split total into two parts
with minimum difference"
        ↓
x+y=S
        ↓
minimize abs(x-y)
        ↓
split near S/2
```

### Codeforces Recognition
```text
balance
closest two totals
minimize difference under fixed sum
        ↓
HALF OF TOTAL
```

### Minimal C++
```cpp
long long x = total/2;
long long y = total-x;
```

### Common Traps / Edge Cases
Additional constraints can invalidate the simple half split: parity, allowed values, subset membership, divisibility, or positivity may matter.

> **Real-World Engineering Case:** Splitting 11 identical requests between two equivalent replicas is best as `5` and `6`; moving farther from half increases the maximum load and imbalance.

---

## 9.4 Extremal Arguments

### ASCII / Structural Visual
```text
array:
[4, 2, 9, 5, 1]
       ↑
      MAX

Instead of analyzing every element first,
ask what MUST be true for the extreme.
```

### Core Formula / Rule
Source idea:
```text
extremal argument
        ↓
look at largest or smallest element first
```

### Short Derivation
An extreme often has fewer possibilities than an arbitrary element. For example, nothing is larger than the maximum, so any rule requiring a larger partner immediately constrains it.

### Real-World Intuition

When a rule must hold for **every** object, inspect the hardest object first. The maximum or minimum often has no room left to satisfy a requirement, immediately proving impossibility.

### Real-World Example — Escalation Hierarchy

**Scenario:** Employee authority levels are `[4,2,9,5,1]`. A proposed rule says every employee must escalate to someone with a **strictly higher** authority level.

**Variables:**
```text
levels = [4,2,9,5,1]
maximum = 9
```

**Model:**
```text
for every level x
need another level > x

        ↓

test the maximum x=9

need level > 9
```

**Calculation:**
```text
max(levels) = 9
no value > 9 exists
```

**Result:**
```text
the rule is impossible
```

```text
universal condition
    ↓
inspect hardest extreme
    ↓
extremal argument
```

### Visual Dry Run
```text
A = [4,2,9,5,1]
max = 9

Question pattern:
"every element must have a strictly larger partner"

For 9:
larger partner does not exist

→ condition impossible
```

### Statement → Mathematical Model
```text
universal condition over all elements
        ↓
test hardest/extreme element
        ↓
maximum or minimum may force answer
```

### Codeforces Recognition
```text
"for every element..."
"must find larger/smaller..."
"repeatedly remove..."
        ↓
CHECK EXTREMES FIRST
```

### Minimal C++
```cpp
auto mn = *min_element(a.begin(),a.end());
auto mx = *max_element(a.begin(),a.end());
```

### Common Traps / Edge Cases
An extremal observation must actually imply the required property; merely noticing the maximum is not a proof.

> **Real-World Engineering Case:** If every worker must escalate a task to someone with strictly higher authority, inspecting the highest-authority worker immediately exposes impossibility because no higher level exists.

---

## 9.5 Multiple Bounds

### ASCII / Structural Visual
```text
answer <= B1
answer <= B2

therefore:

answer <= min(B1,B2)
```

### Core Formula / Rule
Source idea:
```text
two upper bounds
        ↓
answer is bounded by min(bound1,bound2)
```

Dually:
```text
answer >= L1
answer >= L2
        ↓
answer >= max(L1,L2)
```

### Short Derivation
A value satisfying both:
```text
x <= B1
x <= B2
```
must satisfy the tighter constraint:
```text
x <= min(B1,B2)
```

### Real-World Intuition

A process can be limited by several resources at once. Each resource gives an upper bound; the tightest resource becomes the bottleneck.

### Real-World Example — Deployment Bottleneck

**Scenario:** Each application deployment requires `2` CPU tokens and `3` license tokens. The platform has `18` CPU tokens and `15` license tokens.

**Variables:**
```text
CPU available = 18
CPU/deployment = 2

licenses available = 15
licenses/deployment = 3
```

**Model:**
```text
deployments <= floor(18/2) = 9
deployments <= floor(15/3) = 5

both constraints must hold
        ↓
deployments <= min(9,5)
```

**Calculation:**
```text
CPU bound     = 9
license bound = 5

min(9,5)=5
```

**Result:**
```text
maximum deployments = 5
license tokens are the bottleneck
```

```text
multiple resources
    ↓
one bound per resource
    ↓
tightest upper bound = minimum
```

### Visual Dry Run
```text
resource A allows at most 8 jobs
resource B allows at most 5 jobs

both resources required per job

jobs <= 8
jobs <= 5

jobs <= min(8,5)=5
```

### Statement → Mathematical Model
```text
one action consumes multiple limited resources
        ↓
derive one bound per resource
        ↓
all must hold simultaneously
        ↓
take tightest bound
```

### Codeforces Recognition
```text
"limited by A and B"
"at most from each resource"
        ↓
MIN OF UPPER BOUNDS
```

### Minimal C++
```cpp
long long ans = min(bound1,bound2);
```

### Common Traps / Edge Cases
A bound is not automatically achievable. After deriving an upper/lower bound, verify a construction or feasibility argument when the problem requires the exact optimum.

> **Real-World Engineering Case:** A deployment requires one CPU token and one license token. If 8 CPU tokens but only 5 licenses are available, at most `min(8,5)=5` deployments can run, assuming no other constraint blocks them.

---

## 9.6 Separating Variables

### ASCII / Structural Visual
Source transformation:
```text
maximize:

(a_i + i) - (a_j + j)

let:
X_i = a_i+i
Y_j = a_j+j

maximize:
X_i - Y_j

        ↓

take large X
and small Y
```

### Core Formula / Rule
Without coupling constraints between choices:
```text
max(X_i-Y_j)
=
max(X_i) - min(Y_j)
```

The source specifically highlights:
```text
(a_i+i) - (a_j+j)
```
as a separable expression.

### Short Derivation
For every `i,j`:
```text
X_i <= maxX
Y_j >= minY

X_i-Y_j <= maxX-minY
```
Choosing an index attaining `maxX` and one attaining `minY` reaches that bound when the choices are independently allowed.

### Real-World Intuition

A naive pair search may compare every buyer with every seller. If the score can be rewritten as an independent value from the first choice minus an independent value from the second, optimize the two sides separately.

### Real-World Example — Independent Buy/Sell Scores

**Scenario:** Three independent options produce transformed scores `[5,3,10]`. We want the largest difference between one selectable high score and one selectable low score.

**Variables:**
```text
X = [5,3,10]

bestHigh = max(X)
bestLow  = min(X)
```

**Model:**
```text
maximize X_i - X_j
        ↓
choices are independent
        ↓
max(X_i) - min(X_j)
```

**Calculation:**
```text
max(X) = 10
min(X) = 3

best difference = 10-3 = 7
```

**Result:**
```text
maximum independent score gap = 7
```

```text
all pair comparisons
    ↓
separable expression F(i)-G(j)
    ↓
max F - min G
```

### Visual Dry Run
```text
a = [4,1,7]
1-indexed i

a_i+i:
i=1 → 5
i=2 → 3
i=3 → 10

max = 10
min = 3

largest difference:
10-3 = 7
```

### Statement → Mathematical Model
```text
maximize expression involving i and j
        ↓
algebraically regroup
        ↓
F(i) - G(j)
        ↓
maximize F independently
minimize G independently
```

### Codeforces Recognition
```text
pair expression
+
terms can be grouped by index
        ↓
SEPARATE VARIABLES

look for:
F(i)+G(j)
F(i)-G(j)
```

### Minimal C++
```cpp
long long mx = LLONG_MIN, mn = LLONG_MAX;
for (long long i=0;i<n;i++) {
    long long v = a[i] + (i+1);
    mx=max(mx,v);
    mn=min(mn,v);
}
long long ans = mx-mn;
```

### Common Traps / Edge Cases
The independent-extrema step can fail when the statement couples `i` and `j`—for example `i<j`, distinct-index restrictions, or other pair constraints. Then prefix/suffix extrema or another structure may be required.

> **Real-World Engineering Case:** If profit is modeled as the best independently selectable selling score minus the cheapest independently selectable acquisition score, the optimization separates into finding one maximum and one minimum rather than comparing every pair.

---

## 30-Second Revision

```text
┌──────────────────────────────────────────────────────────────┐
│          MIN/MAX TRANSFORMATIONS — QUICK REVISION            │
├──────────────────────────────────────────────────────────────┤
│ max(a,b) → (a+b+abs(a-b))/2                                 │
│ min(a,b) → (a+b-abs(a-b))/2                                 │
│ minimize maximum → feasible(X) + binary search if monotonic  │
│ maximize minimum → same boundary-search idea                 │
│ balance fixed total → values near total/2                    │
│ universal condition → inspect extreme first                  │
│ multiple upper bounds → take min of bounds                   │
│ multiple lower bounds → take max of bounds                   │
│ F(i)-G(j) → max F - min G, if choices independent            │
└──────────────────────────────────────────────────────────────┘
```
