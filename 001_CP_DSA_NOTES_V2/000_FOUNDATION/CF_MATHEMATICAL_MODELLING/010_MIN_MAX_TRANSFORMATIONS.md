# Part 9 — Min/Max Mathematical Transformations

> **Goal:** convert Codeforces phrases such as *at least*, *at most*, *common interval*, *closest boundary*, *bottleneck*, *minimize the worst*, and *maximize the weakest* into compact `min` / `max` mathematics.
>
> **Modeling workflow:** `Story → Variables → Constraints → Min/Max Form → Proof → Algorithm → C++`

## Table of Contents

- [9.0 Min/Max Mental Model](#90-minmax-mental-model)
- [9.1 Clamp a Value into a Range](#91-clamp-a-value-into-a-range)
- [9.2 Interval Intersection and Overlap Length](#92-interval-intersection-and-overlap-length)
- [9.3 Distance from a Point to an Interval](#93-distance-from-a-point-to-an-interval)
- [9.4 Maximum and Minimum Possible Sum](#94-maximum-and-minimum-possible-sum)
- [9.5 Bounding a Quantity from Multiple Constraints](#95-bounding-a-quantity-from-multiple-constraints)
- [9.6 Pairwise Min/Max Identities](#96-pairwise-minmax-identities)
- [9.7 Absolute Difference through Min/Max](#97-absolute-difference-through-minmax)
- [9.8 Positive Deficit and Cancellation Identities](#98-positive-deficit-and-cancellation-identities)
- [9.9 Bottleneck Modeling](#99-bottleneck-modeling)
- [9.10 Maximum Feasible and Minimum Required](#910-maximum-feasible-and-minimum-required)
- [9.11 Minimize the Maximum](#911-minimize-the-maximum)
- [9.12 Maximize the Minimum](#912-maximize-the-minimum)
- [9.13 Prefix and Suffix Min/Max](#913-prefix-and-suffix-minmax)
- [9.14 Contribution of Min/Max over Pairs](#914-contribution-of-minmax-over-pairs)
- [9.15 Codeforces Recognition Map](#915-codeforces-recognition-map)
- [9.16 Common Mistakes](#916-common-mistakes)
- [9.17 Fast Revision Card](#917-fast-revision-card)

---

## 9.0 Min/Max Mental Model

### Plain-English intuition

```text
min = "Which upper limit stops me first?"
max = "Which lower requirement forces me highest?"
```

If all constraints must hold:

```text
x <= A       x >= A
x <= B       x >= B
x <= C       x >= C
   │            │
   ▼            ▼
x <= min(...) x >= max(...)
```

### Daily-life scenario — road speed

A car can do `130 km/h`, the road limit is `100`, and traffic safely allows `80`.

```text
speed <= 130
speed <= 100
speed <= 80

speed <= min(130,100,80)
      <= 80
```

**Step-by-step modeling**

```text
Story       -> several maximum allowed speeds
Variable    -> s = actual speed
Constraints -> s<=130, s<=100, s<=80
Combine     -> s <= min(130,100,80)
Answer      -> 80 km/h
```

### Real-world engineering scenario — service throughput

```text
API gateway : 1200 req/s
application : 1000 req/s
database    :  700 req/s

throughput = min(1200,1000,700)
           = 700 req/s
```

The database is the bottleneck.

### CF recognition

| Phrase | Mathematical instinct |
|---|---|
| cannot exceed all these limits | `min(...)` |
| must satisfy all minimum requirements | `max(...)` |
| weakest capacity | `min(...)` |
| strongest lower requirement | `max(...)` |

---

## 9.1 Clamp a Value into a Range

### Problem form

Force `x` into `[L,R]`.

### Three cases

```text
x < L          L <= x <= R          x > R
  │                 │                 │
  ▼                 ▼                 ▼
answer=L          answer=x          answer=R
```

### Step-by-step derivation

First enforce the lower bound:

```text
max(x,L)
```

Then enforce the upper bound:

```text
min(max(x,L),R)
```

Therefore:

```text
clamp(x,L,R) = min(max(x,L),R)
```

Equivalent:

```text
clamp(x,L,R) = max(L,min(x,R))
```

### Daily-life scenario — thermostat

A hotel thermostat allows only `[18°C,26°C]`.

```text
requested x = 30
L = 18
R = 26
```

Step 1:

```text
max(30,18) = 30
```

Step 2:

```text
min(30,26) = 26
```

So:

```text
30°C request -> 26°C
```

Try all three regions:

```text
request 15 -> 18
request 22 -> 22
request 30 -> 26

             valid range
        18 ---------------- 26
15 -----|         22         |----- 30
        ↑                     ↑
      push up              push down
```

### CF recognition

```text
"at least L but at most R"
"keep x inside [L,R]"
"cap the value between two limits"
```

**Memory hook:** push up to the floor, then push down to the ceiling.

---

## 9.2 Interval Intersection and Overlap Length

Intervals:

```text
A=[L1,R1]
B=[L2,R2]
```

A common point must be after both starts:

```text
left = max(L1,L2)
```

and before both ends:

```text
right = min(R1,R2)
```

Hence:

```text
intersection = [max(L1,L2), min(R1,R2)]
```

Continuous overlap:

```text
max(0, right-left)
```

Inclusive integer-point count:

```text
max(0, right-left+1)
```

### Daily-life scenario — meeting availability

```text
Alice: 09:00 -------- 12:00
Bob:          10:30 -------- 13:00
```

Step 1 — later start:

```text
max(09:00,10:30)=10:30
```

Step 2 — earlier finish:

```text
min(12:00,13:00)=12:00
```

Step 3:

```text
common availability = [10:30,12:00]
duration = 90 minutes
```

### Why `max(0,...)`?

```text
A: 2---4
B:       7---9

raw overlap = min(4,9)-max(2,7)
            = 4-7
            = -3
```

Time/length cannot be negative:

```text
overlap=max(0,-3)=0
```

**Memory hook:** intersection = later start + earlier end.

---

## 9.3 Distance from a Point to an Interval

For interval `[L,R]`:

```text
x < L          L <= x <= R          x > R
distance=L-x   distance=0            distance=x-R
```

One formula handles all cases:

```text
distance = max(L-x, 0, x-R)
```

### Daily-life scenario — parking zone

Legal parking positions are `[100,200]`.

For `x=230`:

```text
L-x = -130
0   = 0
x-R = 30

distance=max(-130,0,30)=30
```

For `x=150`:

```text
max(-50,0,-50)=0
```

For `x=70`:

```text
max(30,0,-130)=30
```

### Why it works

Inside the interval, both deficits are non-positive, so `0` wins. Outside, exactly one side produces a positive deficit.

**Memory hook:** left deficit / zero / right deficit.

---

## 9.4 Maximum and Minimum Possible Sum

If:

```text
Li <= xi <= Ri
```

then:

```text
ΣLi <= Σxi <= ΣRi
```

So:

```text
minimum total = ΣLi
maximum total = ΣRi
```

### Daily-life scenario — grocery budget

```text
vegetables : [20,40]
meat       : [30,60]
milk/eggs  : [10,25]
```

Minimum:

```text
20+30+10=60
```

Maximum:

```text
40+60+25=125
```

Therefore:

```text
total ∈ [60,125]
```

Target `100` passes the basic feasibility test:

```text
60 <= 100 <= 125
```

Target `50` fails because even the minimum choices cost `60`.

### CF recognition

```text
"each ai must lie between Li and Ri"
"can the total equal S?"
"minimum/maximum possible total"
```

**Memory hook:** add every floor for the smallest total; add every ceiling for the largest.

---

## 9.5 Bounding a Quantity from Multiple Constraints

Suppose:

```text
x >= 4
x >= 9
x >= 6
```

Then:

```text
x >= max(4,9,6)=9
```

Similarly:

```text
x <= 20
x <= 13
x <= 18

x <= min(20,13,18)=13
```

General form:

```text
lo = max(all lower bounds)
hi = min(all upper bounds)

feasible iff lo <= hi
```

### Daily-life scenario — ride height restriction

```text
height >= 120
height >= 130
height <= 190
height <= 185
```

Combine floors:

```text
lo=max(120,130)=130
```

Combine ceilings:

```text
hi=min(190,185)=185
```

Final feasible interval:

```text
[130,185]
```

### CF implementation pattern

```cpp
lo = max(lo, newLower);
hi = min(hi, newUpper);
```

**Memory hook:** many constraints collapse into one interval.

---

## 9.6 Pairwise Min/Max Identities

For any `a,b`:

```text
min(a,b)+max(a,b)=a+b
```

### Proof

If `a<=b`:

```text
min=a
max=b
min+max=a+b
```

If `b<a`, the two values swap roles; their sum is unchanged.

Therefore:

```text
min(a,b)=a+b-max(a,b)
max(a,b)=a+b-min(a,b)
```

### Daily-life scenario — two shopping bags

```text
bag A = 7 kg
bag B = 12 kg

lighter = 7
heavier = 12

lighter+heavier = 19
A+B             = 19
```

If total=`19` and heavier=`12`:

```text
lighter=19-12=7
```

**Memory hook:** min/max reorder the same two values; they do not change their total.

---

## 9.7 Absolute Difference through Min/Max

```text
|a-b| = max(a,b)-min(a,b)
```

### Daily-life scenario — age gap

```text
a=46
b=38

older   = max(46,38)=46
younger = min(46,38)=38

gap=46-38=8
```

Thus:

```text
|46-38|=8
```

### Derive min/max using absolute value

We know:

```text
max+min = a+b
max-min = |a-b|
```

Add:

```text
2max=a+b+|a-b|

max(a,b)=(a+b+|a-b|)/2
```

Subtract:

```text
2min=a+b-|a-b|

min(a,b)=(a+b-|a-b|)/2
```

**Memory hook:** absolute difference = larger − smaller.

---

## 9.8 Positive Deficit and Cancellation Identities

Important identity:

```text
max(a,b)-a = max(0,b-a)
```

Also:

```text
a-min(a,b) = max(0,a-b)
min(a,b)   = a-max(0,a-b)
max(a,b)   = a+max(0,b-a)
```

### Daily-life scenario — prepaid balance

Need:

```text
100 lei
```

Have:

```text
65 lei
```

Shortage:

```text
need-have=100-65=35
```

Top-up cannot be negative:

```text
topUp=max(0,100-65)=35
```

If you have `120`:

```text
max(0,100-120)
=max(0,-20)
=0
```

### CF recognition

Whenever you see:

```text
"additional amount needed"
"missing units"
"extra operations only if short"
```

try:

```text
max(0, need-have)
```

---

## 9.9 Bottleneck Modeling

If one complete product requires several resources:

```text
units <= resourceA/needA
units <= resourceB/needB
units <= resourceC/needC
```

Therefore:

```text
maximum complete units
= min(resourceA/needA,
      resourceB/needB,
      resourceC/needC)
```

### Daily-life scenario — sandwiches

One sandwich needs:

```text
2 bread slices
1 cheese slice
3 tomato slices
```

Inventory:

```text
20 bread  -> 20/2 = 10 sandwiches
7 cheese  ->  7/1 =  7 sandwiches
30 tomato -> 30/3 = 10 sandwiches
```

All ingredients are needed simultaneously:

```text
answer=min(10,7,10)=7
```

Cheese runs out first.

### Engineering scenario — deployment replicas

```text
20 CPU / 2  = 10
36 GB / 4   = 9
200 GB / 10 = 20

replicas=min(10,9,20)=9
```

**Memory hook:** the resource that runs out first controls the answer.

---

## 9.10 Maximum Feasible and Minimum Required

### Maximum feasible

If:

```text
x<=A
x<=B
x<=C
```

then:

```text
largest feasible x=min(A,B,C)
```

### Minimum required

If:

```text
x>=A
x>=B
x>=C
```

then:

```text
smallest feasible x=max(A,B,C)
```

### Daily-life scenario

Suitcase:

```text
airline <= 23 kg
bag     <= 30 kg
you     <= 25 kg

maximum feasible
=min(23,30,25)
=23 kg
```

Delivery:

```text
customer >= 10 boxes
contract >= 12 boxes
economics >= 8 boxes

minimum required
=max(10,12,8)
=12 boxes
```

### Critical memory rule

```text
MAXIMUM feasible -> MIN of upper bounds
MINIMUM required -> MAX of lower bounds
```

---

## 9.11 Minimize the Maximum

General objective:

```text
minimize max(cost1,cost2,...)
```

First measure the worst component using `max`; then choose a decision minimizing it.

### Daily-life scenario — sharing dishes

`11` plates are split between two people.

```text
A=x
B=11-x

worst workload=max(x,11-x)
```

Try:

```text
x=2 -> max(2,9)=9
x=4 -> max(4,7)=7
x=5 -> max(5,6)=6
x=6 -> max(6,5)=6
x=8 -> max(8,3)=8
```

Therefore:

```text
min max(x,11-x)=6
```

For two integer parts summing to `S`:

```text
minimum possible maximum = ceil(S/2)
```

### Why?

If both parts were `< ceil(S/2)`, their sum could not reach `S`. A split using `floor(S/2)` and `ceil(S/2)` achieves the bound.

### CF recognition

```text
minimum possible largest...
minimize worst...
split as evenly as possible...
```

---

## 9.12 Maximize the Minimum

General objective:

```text
maximize min(value1,value2,...)
```

### Daily-life scenario — sharing chocolates

`23` chocolates, `5` children.

Can every child get `5`?

```text
5*5=25 > 23
```

No.

Can every child get `4`?

```text
5*4=20 <= 23
```

Yes.

Therefore:

```text
maximum guaranteed minimum
=floor(23/5)
=4
```

### Proof pattern

```text
Upper bound:
minimum cannot exceed floor(total/n)

Construction:
give floor(total/n) to everyone

Therefore the bound is optimal.
```

### CF recognition

```text
maximize the minimum...
largest guaranteed...
make the weakest value as large as possible...
```

---

## 9.13 Prefix and Suffix Min/Max

Given:

```text
a=[8,3,6,2,7]
```

Prefix minimum:

```text
prefMin[i]=min(prefMin[i-1],a[i])

a       : 8 3 6 2 7
prefMin : 8 3 3 2 2
```

Prefix maximum:

```text
prefMax[i]=max(prefMax[i-1],a[i])

prefMax : 8 8 8 8 8
```

### Daily-life scenario — hottest temperature so far

```text
day         : 1  2  3  4  5
temperature : 18 23 20 27 24
```

Build:

```text
prefMax[1]=18
prefMax[2]=max(18,23)=23
prefMax[3]=max(23,20)=23
prefMax[4]=max(23,27)=27
prefMax[5]=max(27,24)=27

prefMax = 18 23 23 27 27
```

A repeated "hottest so far?" query becomes O(1).

### Transformation

```text
Repeated O(n) scans
        ↓
O(n) preprocessing
        ↓
O(1) extreme lookup
```

**Memory hook:** prefix = extreme seen so far; suffix = extreme from here onward.

---

## 9.14 Contribution of Min/Max over Pairs

Sort:

```text
a0 <= a1 <= ... <= a(n-1)
```

For pair maximums, `a[j]` is the maximum with every earlier element.

With zero-based indexing:

```text
number of earlier elements = j

contribution as maximum = a[j]*j
```

Therefore:

```text
Σ max(ai,aj), i<j
=
Σ a[j]*j
```

For minimums:

```text
number of later elements = n-i-1

Σ min(ai,aj), i<j
=
Σ a[i]*(n-i-1)
```

### Daily-life scenario — heights

Sorted:

```text
150,170,190
```

Pairs:

```text
(150,170) -> max=170
(150,190) -> max=190
(170,190) -> max=190
```

Direct:

```text
170+190+190=550
```

Contribution:

```text
150*0 + 170*1 + 190*2
=0+170+380
=550
```

### Why sorting matters

After sorting, every element to the left is guaranteed `<= a[j]`; therefore `a[j]` automatically becomes the maximum of those pairs.

**Memory hook:** sorted position tells you how many times an element wins as min/max.

---

## 9.15 Codeforces Recognition Map

| Story language | Model |
|---|---|
| at most every listed limit | `min(upper bounds)` |
| at least every listed requirement | `max(lower bounds)` |
| keep inside `[L,R]` | `min(max(x,L),R)` |
| common interval | `max(lefts), min(rights)` |
| overlap length | `max(0,right-left)` |
| shortage | `max(0,need-have)` |
| complete groups from resources | `min(capacities)` |
| minimize worst value | `min(max(...))` |
| maximize weakest value | `max(min(...))` |
| extreme before/after each index | prefix/suffix min/max |
| sum of pair min/max | sort + contribution |

### Sentence-to-math drill

```text
"Keep temperature from 18 to 26."
-> clamp

"When are both people available?"
-> interval intersection

"How much money is missing?"
-> positive deficit

"How many complete meals?"
-> bottleneck

"Make the busiest worker as lightly loaded as possible."
-> minimize maximum

"Give everyone the largest guaranteed amount."
-> maximize minimum
```

### 10-second contest checklist

```text
1. Several ceilings?       -> min
2. Several floors?         -> max
3. Common interval?        -> max(left), min(right)
4. Non-negative shortage?  -> max(0,difference)
5. Worst component?        -> max inside objective
6. Weakest component?      -> min inside objective
7. Repeated extrema?       -> prefix/suffix
8. Pair extrema?           -> sort + contribution
```

---

## 9.16 Common Mistakes

### 1. Reversing interval intersection

Wrong:

```text
left=min(L1,L2)
right=max(R1,R2)
```

That is the outer span.

Correct:

```text
left=max(L1,L2)
right=min(R1,R2)
```

### 2. Allowing negative overlap

Wrong:

```text
overlap=right-left
```

Correct:

```text
overlap=max(0,right-left)
```

### 3. Forgetting `+1` for inclusive integer points

```text
[5,8] = {5,6,7,8}

count=8-5+1=4
```

### 4. Confusing maximum feasible and minimum required

```text
x<=10
x<=7

largest feasible x=7
```

not `10`.

### 5. Overflow

Expressions such as:

```text
a[i]*i
a+b+abs(a-b)
```

may require `long long`.

### Daily-life sanity check

If your formula says:

```text
meeting duration = -2 hours
number of houses = -4
distance = -7 meters
```

the model is missing a non-negativity condition such as `max(0,...)`.

---

## 9.17 Fast Revision Card

```text
====================================================
MIN/MAX TRANSFORMATIONS
====================================================

UPPER BOUNDS
x<=A, x<=B
=> x<=min(A,B)

LOWER BOUNDS
x>=A, x>=B
=> x>=max(A,B)

CLAMP
min(max(x,L),R)

INTERSECTION
left=max(L1,L2)
right=min(R1,R2)

OVERLAP
max(0,right-left)

INTEGER INTERSECTION COUNT
max(0,right-left+1)

DISTANCE TO INTERVAL
max(L-x,0,x-R)

PAIR IDENTITIES
min+max=a+b
|max-min| = max-min
|a-b|=max(a,b)-min(a,b)

POSITIVE DEFICIT
max(0,need-have)

BOTTLENECK
min(capacity1,capacity2,...)

MAX FEASIBLE
min(all upper bounds)

MIN REQUIRED
max(all lower bounds)

MINIMIZE WORST
min(max(...))

MAXIMIZE WEAKEST
max(min(...))

PREFIX/SUFFIX
precompute repeated extrema

SORTED PAIR CONTRIBUTION
pair max: Σ a[j]*j
pair min: Σ a[i]*(n-i-1)

MEMORY:
"What stops me first?"    -> min
"What forces me highest?" -> max
====================================================
```
