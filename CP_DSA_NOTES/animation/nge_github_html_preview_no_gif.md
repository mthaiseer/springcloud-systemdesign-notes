# 📘 Next Greater Element (NGE) — Monotonic Stack Dry Run

<div align="center">

<h3>Stack Pattern: Next Greater Element</h3>

<p>
Find the first greater element on the right side of every element.
</p>

</div>

---

## 🧠 Problem

Given an array, find the **Next Greater Element** for every element.

For each element `arr[i]`, find the first element greater than `arr[i]` on its right side.

If no greater element exists, answer is `-1`.

---

## ✅ Example

<div align="center">

<table>
<tr>
<th>Input</th>
<td><code>[2, 1, 2, 4, 3]</code></td>
</tr>
<tr>
<th>Output</th>
<td><code>[4, 2, 4, -1, -1]</code></td>
</tr>
</table>

</div>

---

## ⚠️ Note About Animation in GitHub Markdown

GitHub Markdown does **not** run JavaScript inside `.md` files.

So instead of a JavaScript animation, this note uses a **GitHub-safe visual dry run** using embedded HTML tables.

It works directly in GitHub preview.

---

## ⚡ Core Idea

Use a **monotonic decreasing stack**.

We scan the array from **right to left**.

At every index:

<div align="center">

<table>
<tr>
<th>Step</th>
<th>Meaning</th>
</tr>
<tr>
<td>1</td>
<td>Pop all elements smaller than or equal to the current element.</td>
</tr>
<tr>
<td>2</td>
<td>If stack is empty, answer is <code>-1</code>.</td>
</tr>
<tr>
<td>3</td>
<td>Otherwise, stack top is the next greater element.</td>
</tr>
<tr>
<td>4</td>
<td>Push current element into the stack.</td>
</tr>
</table>

</div>

---

## 🧩 Algorithm

```cpp
vector<int> nextGreaterElement(vector<int>& arr) {
    int n = arr.size();
    vector<int> nge(n, -1);
    stack<int> st;

    for (int i = n - 1; i >= 0; i--) {
        while (!st.empty() && st.top() <= arr[i]) {
            st.pop();
        }

        if (!st.empty()) {
            nge[i] = st.top();
        }

        st.push(arr[i]);
    }

    return nge;
}
```

---

## 🎬 Visual Dry Run

Array:

<div align="center">

<table>
<tr>
<th>Index</th>
<td>0</td>
<td>1</td>
<td>2</td>
<td>3</td>
<td>4</td>
</tr>
<tr>
<th>Value</th>
<td><code>2</code></td>
<td><code>1</code></td>
<td><code>2</code></td>
<td><code>4</code></td>
<td><code>3</code></td>
</tr>
</table>

</div>

---

## Step 1 — Start from Right

Current index: `i = 4`  
Current value: `3`

<div align="center">

<table>
<tr>
<th>Array</th>
<td><code>2</code></td>
<td><code>1</code></td>
<td><code>2</code></td>
<td><code>4</code></td>
<td><strong><code>3</code></strong></td>
</tr>
<tr>
<th>Index</th>
<td>0</td>
<td>1</td>
<td>2</td>
<td>3</td>
<td><strong>4</strong></td>
</tr>
</table>

<br/>

<table>
<tr>
<th>Stack Before</th>
<td><code>[]</code></td>
</tr>
<tr>
<th>Action</th>
<td>Stack is empty, so no greater element exists.</td>
</tr>
<tr>
<th>NGE[4]</th>
<td><code>-1</code></td>
</tr>
<tr>
<th>Stack After</th>
<td><code>[3]</code></td>
</tr>
<tr>
<th>Partial Answer</th>
<td><code>[?, ?, ?, ?, -1]</code></td>
</tr>
</table>

</div>

---

## Step 2 — Move Left

Current index: `i = 3`  
Current value: `4`

<div align="center">

<table>
<tr>
<th>Array</th>
<td><code>2</code></td>
<td><code>1</code></td>
<td><code>2</code></td>
<td><strong><code>4</code></strong></td>
<td><code>3</code></td>
</tr>
<tr>
<th>Index</th>
<td>0</td>
<td>1</td>
<td>2</td>
<td><strong>3</strong></td>
<td>4</td>
</tr>
</table>

<br/>

<table>
<tr>
<th>Stack Before</th>
<td><code>[3]</code></td>
</tr>
<tr>
<th>Action</th>
<td><code>3 <= 4</code>, so pop <code>3</code>. Stack becomes empty.</td>
</tr>
<tr>
<th>NGE[3]</th>
<td><code>-1</code></td>
</tr>
<tr>
<th>Stack After</th>
<td><code>[4]</code></td>
</tr>
<tr>
<th>Partial Answer</th>
<td><code>[?, ?, ?, -1, -1]</code></td>
</tr>
</table>

</div>

---

## Step 3 — Current Value is 2

Current index: `i = 2`  
Current value: `2`

<div align="center">

<table>
<tr>
<th>Array</th>
<td><code>2</code></td>
<td><code>1</code></td>
<td><strong><code>2</code></strong></td>
<td><code>4</code></td>
<td><code>3</code></td>
</tr>
<tr>
<th>Index</th>
<td>0</td>
<td>1</td>
<td><strong>2</strong></td>
<td>3</td>
<td>4</td>
</tr>
</table>

<br/>

<table>
<tr>
<th>Stack Before</th>
<td><code>[4]</code></td>
</tr>
<tr>
<th>Action</th>
<td>Top of stack is <code>4</code>, and <code>4 > 2</code>.</td>
</tr>
<tr>
<th>NGE[2]</th>
<td><code>4</code></td>
</tr>
<tr>
<th>Stack After</th>
<td><code>[4, 2]</code></td>
</tr>
<tr>
<th>Partial Answer</th>
<td><code>[?, ?, 4, -1, -1]</code></td>
</tr>
</table>

</div>

---

## Step 4 — Current Value is 1

Current index: `i = 1`  
Current value: `1`

<div align="center">

<table>
<tr>
<th>Array</th>
<td><code>2</code></td>
<td><strong><code>1</code></strong></td>
<td><code>2</code></td>
<td><code>4</code></td>
<td><code>3</code></td>
</tr>
<tr>
<th>Index</th>
<td>0</td>
<td><strong>1</strong></td>
<td>2</td>
<td>3</td>
<td>4</td>
</tr>
</table>

<br/>

<table>
<tr>
<th>Stack Before</th>
<td><code>[4, 2]</code></td>
</tr>
<tr>
<th>Action</th>
<td>Top of stack is <code>2</code>, and <code>2 > 1</code>.</td>
</tr>
<tr>
<th>NGE[1]</th>
<td><code>2</code></td>
</tr>
<tr>
<th>Stack After</th>
<td><code>[4, 2, 1]</code></td>
</tr>
<tr>
<th>Partial Answer</th>
<td><code>[?, 2, 4, -1, -1]</code></td>
</tr>
</table>

</div>

---

## Step 5 — Current Value is 2

Current index: `i = 0`  
Current value: `2`

<div align="center">

<table>
<tr>
<th>Array</th>
<td><strong><code>2</code></strong></td>
<td><code>1</code></td>
<td><code>2</code></td>
<td><code>4</code></td>
<td><code>3</code></td>
</tr>
<tr>
<th>Index</th>
<td><strong>0</strong></td>
<td>1</td>
<td>2</td>
<td>3</td>
<td>4</td>
</tr>
</table>

<br/>

<table>
<tr>
<th>Stack Before</th>
<td><code>[4, 2, 1]</code></td>
</tr>
<tr>
<th>Action</th>
<td>
Pop <code>1</code> because <code>1 <= 2</code>.<br/>
Pop <code>2</code> because <code>2 <= 2</code>.<br/>
Now top is <code>4</code>, and <code>4 > 2</code>.
</td>
</tr>
<tr>
<th>NGE[0]</th>
<td><code>4</code></td>
</tr>
<tr>
<th>Stack After</th>
<td><code>[4, 2]</code></td>
</tr>
<tr>
<th>Partial Answer</th>
<td><code>[4, 2, 4, -1, -1]</code></td>
</tr>
</table>

</div>

---

## ✅ Final Answer

<div align="center">

<table>
<tr>
<th>Input</th>
<td><code>[2, 1, 2, 4, 3]</code></td>
</tr>
<tr>
<th>Next Greater Element</th>
<td><code>[4, 2, 4, -1, -1]</code></td>
</tr>
</table>

</div>

---

## 🧠 Why This Works

The stack stores only useful candidates for the next greater element.

If an element is smaller than or equal to the current element, it is removed because it cannot be the next greater element for the current element or for elements further left.

That is why every element is pushed once and popped at most once.

---

## ⏱ Complexity

<div align="center">

<table>
<tr>
<th>Time Complexity</th>
<td><code>O(n)</code></td>
</tr>
<tr>
<th>Space Complexity</th>
<td><code>O(n)</code></td>
</tr>
</table>

</div>

---

## 🧪 Similar Problems

- Next Greater Element II
- Daily Temperatures
- Stock Span Problem
- Largest Rectangle in Histogram
- Next Smaller Element
