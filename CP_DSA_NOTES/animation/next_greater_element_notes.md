# 📘 Next Greater Element (NGE) — Stack + Animation

## 🧠 Problem
Given an array, find the **Next Greater Element (NGE)** for each element.

- NGE of `x` = first greater element to the **right**
- If none → `-1`

### Example
Input:  [2, 1, 2, 4, 3]  
Output: [4, 2, 4, -1, -1]

---

## ⚡ Key Idea (Monotonic Stack)

- Traverse **right → left**
- Maintain a **decreasing stack**
- Remove all smaller elements → they can't be answers anymore

---

## 🧩 Algorithm

```
for i from n-1 down to 0:
    while stack not empty and stack.top <= arr[i]:
        stack.pop()

    if stack empty:
        nge[i] = -1
    else:
        nge[i] = stack.top

    stack.push(arr[i])
```

---

## 🎬 Dry Run

| i | value | stack before | action | nge[i] | stack after |
|--|------|-------------|--------|--------|------------|
| 4 | 3 | [] | empty → -1 | -1 | [3] |
| 3 | 4 | [3] | pop 3 | -1 | [4] |
| 2 | 2 | [4] | valid top | 4 | [4,2] |
| 1 | 1 | [4,2] | valid top | 2 | [4,2,1] |
| 0 | 2 | [4,2,1] | pop 1,2 | 4 | [4,2] |

---

## 🎥 Animation (React)

```jsx
import React, { useMemo, useState } from "react";

const arr = [2, 1, 2, 4, 3];

function buildSteps(nums) {
  const stack = [];
  const nge = Array(nums.length).fill("?");
  const steps = [];

  for (let i = nums.length - 1; i >= 0; i--) {
    while (stack.length && stack[stack.length - 1] <= nums[i]) {
      stack.pop();
    }

    nge[i] = stack.length ? stack[stack.length - 1] : -1;
    stack.push(nums[i]);

    steps.push({
      i,
      stack: [...stack],
      nge: [...nge],
    });
  }

  return steps;
}

export default function App() {
  const steps = useMemo(() => buildSteps(arr), []);
  const [step, setStep] = useState(0);

  return (
    <div style={{ padding: 20 }}>
      <h2>Next Greater Element Animation</h2>

      <p>Array: {JSON.stringify(arr)}</p>

      <p>Step: {step + 1} / {steps.length}</p>

      <button onClick={() => setStep(step - 1)} disabled={step === 0}>
        Prev
      </button>
      <button onClick={() => setStep(step + 1)} disabled={step === steps.length - 1}>
        Next
      </button>

      <h3>Stack</h3>
      <pre>{JSON.stringify(steps[step].stack)}</pre>

      <h3>NGE</h3>
      <pre>{JSON.stringify(steps[step].nge)}</pre>
    </div>
  );
}
```

---

## ⏱ Complexity

- Time: **O(n)**
- Space: **O(n)**
