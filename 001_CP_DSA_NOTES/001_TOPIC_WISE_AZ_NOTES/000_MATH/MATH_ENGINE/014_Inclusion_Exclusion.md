# 014_Inclusion_Exclusion.md — MiniMathEngine

# Inclusion Exclusion Principle

> One of the most important counting techniques in CP and FAANG combinatorics.

---

# Clickable Index

1. What Is Inclusion Exclusion?
2. Why This Topic Matters
3. Core Intuition
4. Basic Formula
5. Formula For 3 Sets
6. General Formula
7. Visual Understanding
8. When To Use Inclusion Exclusion
9. Problem Form 1 — Count Multiples
10. Problem Form 2 — Count NOT Divisible
11. Problem Form 3 — Upper Bound Stars And Bars
12. Problem Form 4 — Coprime Counting
13. Problem Form 5 — Bitmask Inclusion Exclusion
14. Dry Run
15. Common Mistakes
16. Decision Tree
17. Real World Mapping
18. Complexity
19. Reusable C++ Template
20. CP / FAANG Problem Forms
21. Practice Checklist
22. Next Step

---

# 1. What Is Inclusion Exclusion?

Inclusion Exclusion Principle helps count elements correctly when sets overlap.

Without it:

```text
we double count overlaps
```

So we:

```text
Add single sets
Subtract pair overlaps
Add triple overlaps
Subtract quadruple overlaps
...
```

Pattern:

```text
+ - + - + -
```

---

# 2. Why This Topic Matters

This appears in:

- combinatorics
- number theory
- probability
- bitmask counting
- coprime counting
- bounded distributions
- forbidden constraints
- graph counting
- subset counting

Very common in:

- Codeforces
- AtCoder
- ICPC
- Google OA
- Meta interviews

---

# 3. Core Intuition

Suppose:

```text
A = numbers divisible by 2
B = numbers divisible by 3
```

If we do:

```text
|A| + |B|
```

numbers divisible by both 2 and 3 are counted twice.

So:

```text
subtract overlap once
```

---

# 4. Basic Formula

For 2 sets:

```text
|A ∪ B|
=
|A| + |B|
- |A ∩ B|
```

---

# 5. Formula For 3 Sets

```text
|A ∪ B ∪ C|
=
|A|
+ |B|
+ |C|
- |A ∩ B|
- |A ∩ C|
- |B ∩ C|
+ |A ∩ B ∩ C|
```

---

# 6. General Formula

For n sets:

```text
odd size subsets   -> add
even size subsets  -> subtract
```

General form:

```text
Answer =
Σ (-1)^(k+1) * intersection_of_k_sets
```

---

# 7. Visual Understanding

Suppose:

```text
A = divisible by 2
B = divisible by 3
```

Numbers divisible by 6 belong to both.

Diagram:

```text
A only      overlap      B only

2 4 8       6 12 18      3 9 15
```

If we count:

```text
|A| + |B|
```

then:

```text
6 12 18 ...
```

are counted twice.

So subtract overlap.

---

# 8. When To Use Inclusion Exclusion

Signals:

```text
overlapping sets
forbidden conditions
multiple constraints
count divisible by
count NOT satisfying
bounded distributions
coprime problems
bitmask subset counting
```

---

# 9. Problem Form 1 — Count Multiples

## Problem

Count numbers from 1 to N divisible by 2 or 3.

---

## Step-by-Step Working

Example:

```text
N = 100
```

Step 1:

Count divisible by 2:

```text
100 / 2 = 50
```

Step 2:

Count divisible by 3:

```text
100 / 3 = 33
```

Step 3:

Numbers divisible by both:

```text
LCM(2,3)=6
100 / 6 = 16
```

Step 4:

Apply Inclusion Exclusion:

```text
50 + 33 - 16 = 67
```

Answer:

```text
67
```

---

## C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

int main() {

    int N = 100;

    int div2 = N / 2;
    int div3 = N / 3;

    // overlap counted twice
    int both = N / 6;

    int ans = div2 + div3 - both;

    cout << ans << endl;

    return 0;
}
```

---

# 10. Problem Form 2 — Count NOT Divisible

## Problem

Count numbers from 1 to N NOT divisible by 2 or 3.

---

## Step-by-Step Working

Example:

```text
N = 100
```

Step 1:

Count divisible by 2 or 3.

Already computed:

```text
67
```

Step 2:

Subtract from total:

```text
100 - 67 = 33
```

Answer:

```text
33
```

---

## C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

int main() {

    int N = 100;

    int div2 = N / 2;
    int div3 = N / 3;
    int both = N / 6;

    int divisible = div2 + div3 - both;

    int notDivisible = N - divisible;

    cout << notDivisible << endl;

    return 0;
}
```

---

# 11. Problem Form 3 — Upper Bound Stars And Bars

## Problem

Count solutions:

```text
x1 + x2 + x3 = 5
0 <= xi <= 2
```

Basic stars and bars fails because of upper bound.

Need:

```text
Inclusion Exclusion
```

---

## Step-by-Step Working

Without upper bound:

```text
C(5+3-1,3-1)
=
C(7,2)
=
21
```

Invalid:

```text
xi >= 3
```

Suppose:

```text
x1 >= 3
```

Then:

```text
y1 = x1 - 3
```

Equation becomes:

```text
y1 + x2 + x3 = 2
```

Ways:

```text
C(4,2)=6
```

Three variables:

```text
3 * 6 = 18
```

Two variables invalid impossible.

Answer:

```text
21 - 18 = 3
```

---

## C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

long long nCr(int n, int r) {

    if(r < 0 || r > n) return 0;

    r = min(r, n-r);

    long long ans = 1;

    for(int i=1;i<=r;i++) {
        ans = ans * (n-r+i) / i;
    }

    return ans;
}

int main() {

    int n = 5;
    int k = 3;
    int limit = 2;

    long long ans = 0;

    for(int bad=0; bad<=k; bad++) {

        int remaining = n - bad*(limit+1);

        if(remaining < 0) continue;

        long long ways =
            nCr(k,bad) *
            nCr(remaining+k-1,k-1);

        if(bad % 2 == 0)
            ans += ways;
        else
            ans -= ways;
    }

    cout << ans << endl;

    return 0;
}
```

---

# 12. Problem Form 4 — Coprime Counting

## Problem

Count numbers from 1 to N coprime with X.

---

## Example

```text
N = 20
X = 12
```

Prime factors of 12:

```text
2,3
```

Count divisible by:

```text
2 or 3
```

Using Inclusion Exclusion:

```text
20/2 = 10
20/3 = 6
20/6 = 3
```

Divisible count:

```text
10 + 6 - 3 = 13
```

Coprime:

```text
20 - 13 = 7
```

---

## C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

int main() {

    int N = 20;

    int div2 = N / 2;
    int div3 = N / 3;
    int both = N / 6;

    int divisible = div2 + div3 - both;

    int coprime = N - divisible;

    cout << coprime << endl;

    return 0;
}
```

---

# 13. Problem Form 5 — Bitmask Inclusion Exclusion

## Problem

Count numbers from 1 to N divisible by any prime in array.

Example:

```text
primes = [2,3,5]
```

---

# Bitmask Idea

Every subset:

```text
subset size odd  -> add
subset size even -> subtract
```

---

## Step-by-Step Working

For subset:

```text
{2,3}
```

LCM:

```text
6
```

Contribution:

```text
N/6
```

Because subset size is even:

```text
subtract
```

---

## Complexity

```text
O(2^k)
```

for k primes.

---

## C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

int main() {

    long long N = 100;

    vector<int> primes = {2,3,5};

    int k = primes.size();

    long long ans = 0;

    for(int mask=1; mask < (1<<k); mask++) {

        long long lcm = 1;
        int bits = 0;

        for(int i=0;i<k;i++) {

            if(mask & (1<<i)) {

                bits++;

                lcm = lcm / gcd(lcm,(long long)primes[i]) * primes[i];
            }
        }

        long long contribution = N / lcm;

        if(bits % 2 == 1)
            ans += contribution;
        else
            ans -= contribution;
    }

    cout << ans << endl;

    return 0;
}
```

---

# 14. Dry Run

Count numbers from 1 to 30 divisible by 2 or 5.

Step 1:

```text
30/2 = 15
```

Step 2:

```text
30/5 = 6
```

Step 3:

Overlap:

```text
LCM(2,5)=10
30/10=3
```

Step 4:

Answer:

```text
15 + 6 - 3 = 18
```

---

# 15. Common Mistakes

## Mistake 1 — Forgetting overlap

Wrong:

```text
A + B
```

Correct:

```text
A + B - overlap
```

---

## Mistake 2 — Wrong sign pattern

Pattern always:

```text
+ - + - +
```

---

## Mistake 3 — Forgetting LCM

For overlap:

```text
use LCM
```

NOT multiplication.

---

## Mistake 4 — Overflow in LCM

Use:

```cpp
lcm = a / gcd(a,b) * b;
```

---

# 16. Decision Tree

```text
Counting problem?
|
+-- Multiple overlapping constraints?
|   |
|   +-- Yes
|       |
|       +-- Count divisible by?
|       |
|       +-- Count forbidden cases?
|       |
|       +-- Upper bounds?
|       |
|       +-- Coprime counting?
|       |
|       +-- Bitmask subsets?
|           |
|           +-- Use Inclusion Exclusion
```

---

# 17. Real World Mapping

| Real System | Inclusion Exclusion Usage |
|---|---|
| Analytics deduplication | overlapping users |
| Ad targeting | audience overlap |
| Permissions | overlapping access |
| Security rules | multiple policies |
| Recommendation systems | shared categories |
| Cache invalidation | overlapping keys |
| Distributed schedulers | conflict resolution |
| Search systems | keyword overlap |

---

# 18. Complexity

For k constraints:

```text
O(2^k)
```

because all subsets processed.

Usually feasible for:

```text
k <= 20
```

---

# 19. Reusable C++ Template

```cpp
#include <bits/stdc++.h>
using namespace std;

long long countDivisible(long long N, vector<int>& nums) {

    int k = nums.size();

    long long ans = 0;

    for(int mask=1; mask < (1<<k); mask++) {

        long long lcm = 1;
        int bits = 0;

        bool overflow = false;

        for(int i=0;i<k;i++) {

            if(mask & (1<<i)) {

                bits++;

                lcm = lcm / gcd(lcm,(long long)nums[i]) * nums[i];

                if(lcm > N) {
                    overflow = true;
                    break;
                }
            }
        }

        if(overflow) continue;

        long long contribution = N / lcm;

        if(bits % 2)
            ans += contribution;
        else
            ans -= contribution;
    }

    return ans;
}

int main() {

    vector<int> nums = {2,3,5};

    cout << countDivisible(100, nums);

    return 0;
}
```

---

# 20. CP / FAANG Problem Forms

## Problem 1 — Count Divisible By Any Prime

### Pattern

```text
Bitmask Inclusion Exclusion
```

### Technique

```text
Subset enumeration
```

### Complexity

```text
O(2^k)
```

### Commented C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

int main() {

    long long N = 1000;

    vector<int> primes = {2,3,5,7};

    int k = primes.size();

    long long ans = 0;

    // iterate all non-empty subsets
    for(int mask=1; mask < (1<<k); mask++) {

        long long lcm = 1;
        int bits = 0;

        for(int i=0;i<k;i++) {

            if(mask & (1<<i)) {

                bits++;

                // maintain subset LCM
                lcm = lcm / gcd(lcm,(long long)primes[i]) * primes[i];
            }
        }

        long long cnt = N / lcm;

        // odd subset size -> add
        if(bits % 2)
            ans += cnt;

        // even subset size -> subtract
        else
            ans -= cnt;
    }

    cout << ans << endl;

    return 0;
}
```

---

## Problem 2 — Bounded Integer Solutions

### Pattern

```text
Stars and Bars + Inclusion Exclusion
```

### Step-by-Step Working

```text
1. Count all unrestricted solutions
2. Subtract invalid solutions
3. Add pair invalid overlaps
4. Continue alternating signs
```

### Commented C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

long long nCr(int n,int r){

    if(r<0 || r>n) return 0;

    r=min(r,n-r);

    long long ans=1;

    for(int i=1;i<=r;i++)
        ans=ans*(n-r+i)/i;

    return ans;
}

int main(){

    int sum=8;
    int vars=4;
    int limit=3;

    long long ans=0;

    for(int bad=0; bad<=vars; bad++){

        int rem=sum-bad*(limit+1);

        if(rem<0) continue;

        long long ways=
            nCr(vars,bad) *
            nCr(rem+vars-1,vars-1);

        if(bad%2==0)
            ans+=ways;
        else
            ans-=ways;
    }

    cout<<ans<<endl;

    return 0;
}
```

---

# 21. Practice Checklist

Before solving:

```text
1. Are sets overlapping?
2. Are values double counted?
3. Can constraints be represented as sets?
4. Need count of forbidden cases?
5. Need count NOT satisfying?
6. Can subset enumeration work?
7. Is k small enough for O(2^k)?
8. Need LCM for overlap?
9. Need alternating signs?
```

---

# 22. Next Step

```text
015_Catalan_Numbers.md
```

Catalan appears in:

```text
balanced parentheses
BST counting
mountain arrays
non-crossing structures
stack-valid sequences
```
