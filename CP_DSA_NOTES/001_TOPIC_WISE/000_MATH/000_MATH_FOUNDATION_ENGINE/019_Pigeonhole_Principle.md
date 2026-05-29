# 019_Pigeonhole_Principle.md

# Pigeonhole Principle For Competitive Programming

---

# 1. Introduction

Pigeonhole Principle is one of the MOST powerful observation techniques in CP.

Many difficult-looking problems become trivial after recognizing:

```text
More objects than containers
```

This principle appears heavily in:
- math observation
- constructive problems
- hashing
- modulo
- counting
- graph theory
- strings

Strong contestants constantly ask:

```text
Can collisions be guaranteed?
```

That is exactly pigeonhole thinking.

---

# 2. What Is Pigeonhole Principle?

Basic idea:

```text
If more pigeons than pigeonholes exist,
at least one pigeonhole contains
multiple pigeons.
```

Mathematically:

```text
If n objects placed into m boxes
and n > m

then at least one box
contains 2 or more objects.
```

---

# 3. Simple Example

Suppose:

```text
13 people
12 months
```

At least two people share:
```text
same birth month
```

Why?

```text
13 objects
12 boxes
```

Collision guaranteed.

---

# 4. Why It Matters In CP

Pigeonhole principle helps:
- prove existence
- guarantee duplicates
- prove collisions
- simplify impossible cases
- solve modulo problems

Very powerful hidden observation.

---

# 5. Modulo And Pigeonhole

Very common pattern.

Example:

```text
n+1 numbers
mod n
```

Possible remainders:

```text
0 to n-1
```

Only:
```text
n distinct remainders
```

Thus:
```text
two numbers share same remainder
```

Guaranteed.

---

# 6. Prefix Sum Pigeonhole Observation

Extremely important CP pattern.

Suppose:
- n prefix sums
- modulo n

Only:
```text
n possible remainders
```

Then:
- either one remainder = 0
- or two prefix sums share same remainder

Thus:
```text
subarray divisible by n exists
```

Very famous observation.

---

# 7. Duplicate Detection

Classic use.

If:
```text
n+1 numbers
```

chosen from:
```text
1 to n
```

Then duplicate guaranteed.

---

# Example

```text
5 numbers chosen from 1..4
```

At least one repeats.

Guaranteed.

---

# 8. Hashing Collision Intuition

Hash tables use pigeonhole principle.

Infinite possible keys:
```text
finite buckets
```

Thus collisions inevitable.

Very important real-world insight.

---

# 9. Graph Theory Observation

In graph with:
```text
n nodes
```

Two nodes must share same degree.

Why?

Possible degrees:
```text
0 to n-1
```

But:
```text
0 and n-1
```

cannot coexist in simple graph.

Only:
```text
n-1 possible degrees
```

for:
```text
n nodes
```

Thus duplicate degree guaranteed.

Classic pigeonhole proof.

---

# 10. String Problems

If string length exceeds alphabet size:

```text
duplicate character guaranteed
```

Example:
```text
27 lowercase letters
```

Only:
```text
26 lowercase characters
```

Thus repetition guaranteed.

---

# 11. CP Observation Mindset

Strong contestants think:

```text
finite states
+
more items
=
collision guaranteed
```

This mindset is extremely powerful.

---

# 12. CP / FAANG Problem Forms

---

# Form 1 — Duplicate Guaranteed

## Problem

n+1 numbers from range 1..n.

Prove duplicate exists.

---

## Observation

More numbers than possible values.

---

## Step-by-Step Working

Example:

```text
5 numbers
range 1..4
```

Only:
```text
4 possible values
```

Thus:
```text
duplicate guaranteed
```

---

## Code

```cpp
#include <bits/stdc++.h>
using namespace std;

bool duplicateGuaranteed(
    int total,
    int rangeSize
) {

    return total > rangeSize;
}
```

---

# Form 2 — Same Modulo Remainder

## Problem

Show two numbers have same modulo.

---

## Observation

Only finite remainders.

---

## Step-by-Step Working

Modulo:
```text
5
```

Possible remainders:

```text
0 1 2 3 4
```

6 numbers:
```text
collision guaranteed
```

---

## Code

```cpp
#include <bits/stdc++.h>
using namespace std;

bool sameRemainder(
    int numbers,
    int mod
) {

    return numbers > mod;
}
```

---

# Form 3 — Subarray Divisible By N

## Problem

Show subarray divisible by n exists.

---

## Observation

Prefix modulo collision.

---

## Step-by-Step Working

Take:
```text
prefix sums modulo n
```

If:
- one modulo = 0
OR
- two equal modulo values

then:
```text
subarray divisible by n
```

---

## Code

```cpp
#include <bits/stdc++.h>
using namespace std;

bool divisibleSubarray(
    vector<int>& a
) {

    int n = a.size();

    vector<int> seen(n, 0);

    int prefix = 0;

    for (int x : a) {

        prefix =
            (prefix + x) % n;

        if (prefix == 0)
            return true;

        if (seen[prefix])
            return true;

        seen[prefix] = 1;
    }

    return false;
}
```

---

# Form 4 — Duplicate Character

## Problem

Determine if duplicate lowercase character guaranteed.

---

## Observation

Only:
```text
26 lowercase letters
```

If length:
```text
> 26
```

duplicate guaranteed.

---

## Code

```cpp
#include <bits/stdc++.h>
using namespace std;

bool duplicateCharGuaranteed(
    int len
) {

    return len > 26;
}
```

---

# Form 5 — Same Degree In Graph

## Problem

Show two nodes share same degree.

---

## Observation

Only:
```text
n-1 possible degrees
```

for:
```text
n nodes
```

---

## Code

```cpp
#include <bits/stdc++.h>
using namespace std;

bool sameDegreeGuaranteed(
    int n
) {

    return n >= 2;
}
```

---

# Form 6 — Hash Collision

## Problem

Will collisions eventually occur?

---

## Observation

Finite buckets:
```text
infinite keys
```

Thus collisions guaranteed.

---

## Code

```cpp
#include <bits/stdc++.h>
using namespace std;

bool collisionGuaranteed(
    long long keys,
    long long buckets
) {

    return keys > buckets;
}
```

---

# Form 7 — Birthday Problem

## Problem

Show same birthday likely.

---

## Observation

365 possible birthdays.

More people:
```text
collision probability rises quickly
```

Classic pigeonhole intuition.

---

## Code

```cpp
#include <bits/stdc++.h>
using namespace std;

bool birthdayGuaranteed(
    int people
) {

    return people > 365;
}
```

---

# 13. Real World Applications

| Real System | Usage |
|---|---|
| Hash tables | collision guarantees |
| Databases | hash partitioning |
| Networking | packet bucket collisions |
| Distributed systems | consistent hashing |
| Cryptography | birthday attacks |
| Compression | repeated patterns |
| Caching | cache collisions |

---

# 14. Real Engineering Insight

Pigeonhole principle means:

```text
Finite states
+
More inputs
=
Collision inevitable
```

This is fundamental in:
- hashing
- caching
- distributed systems
- cryptography

---

# 15. Observation Recognition Signals

Look for:

```text
1. duplicates
2. collisions
3. modulo classes
4. finite states
5. repeated values
6. guarantees
7. hashing
8. buckets
9. categories
10. compression
```

---

# 16. Decision Tree

```text
More objects than states?
→ pigeonhole

Finite modulo classes?
→ collision guaranteed

More characters than alphabet?
→ repetition guaranteed

Hash buckets limited?
→ collisions inevitable

Prefix modulo repeated?
→ divisible subarray
```

---

# 17. Common Traps

```text
1. Missing finite-state observation
2. Brute forcing unnecessarily
3. Wrong modulo count
4. Ignoring collisions
5. Forgetting prefix modulo logic
6. Wrong bucket interpretation
7. Confusing existence with counting
8. Missing hidden pigeonholes
```

---

# 18. Final Checklist

Before solving:

```text
1. Are states finite?
2. Are objects more than states?
3. Can collisions be guaranteed?
4. Is modulo involved?
5. Are duplicates inevitable?
6. Is hashing involved?
7. Can prefix modulo help?
8. Is there hidden compression?
```

---

# 19. Final Mental Shortcut

```text
Pigeonhole Principle
=
More Objects
Than Containers
→ Collision Guaranteed
```
