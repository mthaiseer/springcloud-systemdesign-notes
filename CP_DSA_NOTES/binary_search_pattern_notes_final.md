# Binary Search Pattern Notes (Final Updated)

---

## 🧠 Binary Search Framework (from your notes)

```mermaid
flowchart TD
A[Binary Search] --> B[Log N per check]

A --> C[Binary Search on Answer approx 80 percent]
A --> D[Binary Search on every start approx 20 percent]

C --> E[Contribution technique]
C --> F[Sweep line ideas]
C --> G[2D binary search]

G --> H[Miscellaneous problems]
```

### 🧠 Intuition

Binary Search is not one thing — it appears in multiple forms:

1. **Binary Search on Answer (Most Important - 80%)**
   - Guess answer
   - Check feasibility

2. **Binary Search per Start (Advanced - 20%)**
   - Fix one parameter
   - Binary search another

3. **Hybrid Patterns**
   - Contribution technique
   - Sweep line + BS
   - 2D BS

---

## 🔥 Core Idea

👉 Always think:

> “Can I convert problem into YES / NO check?”

---

## 🚀 Quick Mental Model

```mermaid
flowchart TD
A[Problem] --> B{Can I guess answer}
B -->|Yes| C[Binary Search on Answer]
B -->|No| D{Sorted or monotonic index}
D -->|Yes| E[Normal Binary Search]
D -->|No| F{Function shape}
F -->|Unimodal| G[Ternary Search]
F -->|Else| H[Different technique]
```

---

## ⚡ Quick Notes (Last Minute Revision)

- Binary Search = Guess + Check
- 80 percent problems = BS on Answer
- Always find monotonic behavior
- Minimize max → BS
- Maximize min → BS
- Count ≤ x → use prefix or upper_bound
- Real values → precision loop
- Unimodal → ternary search

---

END
