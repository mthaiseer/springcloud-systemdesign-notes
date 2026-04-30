# Prefix Sum Problem Solving Playbook (Concepts • Frameworks • Forms • Tactics)

> A structured guide to solve any **Prefix Sum–related problem** in competitive programming.  
> Built from your notes and extended with additional patterns, frameworks, and tactics.

---

# 0. Big Picture

```mermaid
flowchart TD
    A["Prefix Sum Thinking"] --> B[Concepts]
    A --> C[Frameworks]
    A --> D[Forms]
    A --> E[Tactics]
    B --> B1["1D Prefix"]
    B --> B2["Difference Array"]
    B --> B3["2D Prefix"]
    B --> B4["2D Difference"]
    C --> C1["Query Framework"]
    C --> C2["Update Framework"]
    D --> D1["Subarray Problems"]
    D --> D2["Range Update Problems"]
    D --> D3["Grid Problems"]
    E --> E1["Optimization Tricks"]
    E --> E2["Pattern Recognition"]
```

---

# 1. CONCEPTS (Core Building Blocks)

## 1.1 1D Prefix Sum

**Idea:** Precompute cumulative sums.

```mermaid
flowchart LR
    A[a[i]] --> B[pref[i] = pref["i-1"] + a[i]]
```

### Use When
- Static array
- Many range sum queries

### Formula

```text
sum(l, r) = pref[r] - pref[l-1]
```

---

## 1.2 Difference Array (Inverse Prefix)

**Idea:** Mark where updates start and stop.

```mermaid
flowchart LR
    A["+X on L..R"] --> B[diff[L]+=X]
    A --> C["diff[R+1"]-=X]
    B --> D[Prefix rebuild]
    C --> D
```

---

## 1.3 2D Prefix Sum

**Idea:** Extend prefix to matrix.

```mermaid
flowchart TD
    A[a[i][j]] --> B[pref[i][j]]
    B --> C["use inclusion-exclusion"]
```

Formula:

```text
pref[i][j] = a[i][j] + up + left - overlap
```

---

## 1.4 2D Difference Array

**Idea:** 4-corner marking.

```mermaid
flowchart TD
    A[Rectangle update] --> B["top-left +X"]
    A --> C["top-right -X"]
    A --> D["bottom-left -X"]
    A --> E["bottom-right +X"]
```

---

# 2. FRAMEWORKS (How to Think)

## 2.1 Query Framework

```mermaid
flowchart TD
    A[Need many queries] --> B[Precompute prefix]
    B --> C["Answer in O(1)"]
```

Mental Model:

```text
Store cumulative info → subtract to isolate range
```

---

## 2.2 Update Framework

```mermaid
flowchart TD
    A[Many updates] --> B[Use difference array]
    B --> C[Apply prefix once]
```

Mental Model:

```text
Mark changes → propagate later
```

---

## 2.3 Hybrid Framework

```mermaid
flowchart TD
    A["Updates + Queries mixed"] --> B[Prefix insufficient]
    B --> C["Fenwick / Segment Tree"]
```

---

# 3. PROBLEM FORMS (Recognize Patterns)

## 3.1 Subarray Sum Problems

### Form

```text
Find sum(l, r)
Count subarrays with sum = X
```

### Pattern

```mermaid
flowchart TD
    A[Subarray] --> B[Prefix Sum]
    B --> C[Map previous prefixes]
```

---

## 3.2 Range Update Problems

### Form

```text
Add X to all elements in [L, R]
```

### Pattern

```text
Difference array
```

---

## 3.3 Subarray Sum = K (Important)

### Core Idea

```text
pref[r] - pref[l] = K
=> pref[l] = pref[r] - K
```

### Pattern

```mermaid
flowchart TD
    A[current prefix] --> B["need prefix - K"]
    B --> C[count using map]
```

---

## 3.4 Grid / Matrix Problems

### Form

```text
Sum of rectangle
Update rectangle
```

### Pattern

```text
2D prefix or 2D difference
```

---

## 3.5 Contribution Technique (Advanced)

Instead of iterating subarrays:

```text
Count contribution per element
```

---

# 4. TACTICS (What to Do in Contest)

## 4.1 Constraint-Based Decision

```text
n <= 1e5, q large → prefix sum
many updates → difference array
2D grid → 2D prefix
```

---

## 4.2 Pattern Recognition Cheatsheet

| Clue | Technique |
|------|---------|
| many sum queries | prefix sum |
| range add | difference array |
| subarray sum = K | prefix + map |
| grid sum | 2D prefix |
| rectangle updates | 2D difference |

---

## 4.3 Edge Case Tactics

- Always initialize:

```cpp
freq[0] = 1;
```

- Use `long long`

- Prefer 1-indexing

---

## 4.4 Optimization Ladder

```mermaid
flowchart TD
    A["Brute Force O(n^2)"] --> B["Prefix Sum O(n)"]
    B --> C["Map Optimization O(n)"]
    C --> D["Advanced DS if needed"]
```

---

## 4.5 Mental Trick

```text
Prefix = accumulate
Difference = distribute
```

---

# 5. COMMON EXTENSIONS (IMPORTANT)

## 5.1 Prefix XOR

Used when XOR instead of sum.

```text
xor(l,r) = pref[r] ^ pref[l-1]
```

---

## 5.2 Prefix Min/Max

Not subtractable → need segment tree.

---

## 5.3 Weighted Prefix

```text
Store weighted sums (index * value)
```

---

## 5.4 Circular Prefix

Handle wrap-around using duplication.

---

# 6. MASTER FLOW (Solve Any Problem)

```mermaid
flowchart TD
    A[Problem] --> B{Type?}
    B -->|Subarray| C["Prefix + Map"]
    B -->|Range Query| D[Prefix Sum]
    B -->|Range Update| E[Difference Array]
    B -->|Grid| F[2D Prefix]
    B -->|Grid Update| G[2D Difference]
    C --> H[Optimize]
    D --> H
    E --> H
    F --> H
    G --> H
```

---

# 7. FINAL CHECKLIST

Before coding:

```text
1. Is array static?
2. Are queries many?
3. Are updates many?
4. Is it 1D or 2D?
5. Can I use prefix?
6. Do I need map?
7. Do I need difference array?
```

---

# 8. GOLDEN RULES

```text
Prefix solves queries
Difference solves updates
Map solves counting
2D = extend logic
```

---

# 9. MINIMAL TEMPLATE

```cpp
vector<long long> pref(n+1,0);
for(int i=1;i<=n;i++) pref[i]=pref[i-1]+a[i-1];
```

---

# END
