# CH 01 — Core Java
## Senior Java Distributed Backend Interview Series

**Target:** ~50 interview questions  
**Goal:** Explain concepts from fundamentals → internals → trade-offs → failure modes → production use.

---

## Chapter Map

```text
CH 01 — CORE JAVA (~50 Q)
|
+-- 1. Fundamentals (10)
|   +-- JDK / JRE / JVM
|   +-- Compilation and bytecode
|   +-- Primitive vs reference
|   +-- Pass-by-value
|   +-- == vs equals()
|   +-- String immutability
|   +-- String pool
|   +-- StringBuilder vs StringBuffer
|   +-- final / finally / finalize
|   +-- Object class
|
+-- 2. OOP / SOLID (10)
|   +-- Encapsulation
|   +-- Abstraction
|   +-- Inheritance
|   +-- Polymorphism
|   +-- Interface vs abstract class
|   +-- Composition vs inheritance
|   +-- SRP
|   +-- OCP
|   +-- LSP / ISP
|   +-- DIP
|
+-- 3. Collections / HashMap (15)
|   +-- Collection hierarchy
|   +-- ArrayList
|   +-- LinkedList
|   +-- HashSet
|   +-- HashMap internals
|   +-- hashCode / equals
|   +-- collisions
|   +-- resize / load factor
|   +-- treeification
|   +-- LinkedHashMap
|   +-- TreeMap
|   +-- Comparable / Comparator
|   +-- Iterator / fail-fast
|   +-- ConcurrentHashMap overview
|   +-- collection choice scenarios
|
+-- 4. Generics / Exceptions (5)
|   +-- Generics / type safety
|   +-- PECS
|   +-- Type erasure
|   +-- Checked vs unchecked
|   +-- try-with-resources
|
+-- 5. Java 8+ / Modern Java (10)
    +-- Lambda
    +-- Functional interfaces
    +-- Stream pipeline
    +-- map / flatMap
    +-- reduce / collect
    +-- Optional
    +-- Method references
    +-- Records
    +-- Sealed classes / pattern matching
    +-- Modern Java design choices
```

---

# 1. Fundamentals — 10 Questions

## Q1. What is the difference between JDK, JRE and JVM?

### Interview answer
- **JVM** executes Java bytecode.
- **JRE** is the runtime environment needed to run Java applications (conceptually JVM + runtime libraries).
- **JDK** is the development kit: compiler and development tools plus the runtime components.

```text
Java source
   |
   | javac (JDK)
   v
.class bytecode
   |
   v
+-----------------------------+
| JVM                         |
|  Class Loader               |
|       |                     |
|  Bytecode Verification      |
|       |                     |
|  Interpreter / JIT          |
|       |                     |
|  Runtime Memory + GC        |
+-----------------------------+
   |
   v
Native machine instructions
```

```java
public class Hello {
    public static void main(String[] args) {
        System.out.println("Hello");
    }
}
```

```text
Hello.java --javac--> Hello.class --JVM--> machine execution
```

**Senior follow-up:** Why is Java platform-independent?  
Because Java source is compiled into platform-independent bytecode, while each platform has a compatible JVM implementation that executes/translates that bytecode.

---

## Q2. What happens from Java source code to execution?

```text
.java
  |
  | javac
  v
.class (bytecode)
  |
  v
Class Loader
  |
  v
Verification / Linking / Initialization
  |
  v
Execution Engine
  |
  +----> Interpreter
  |
  +----> JIT compiler ----> optimized native code
```

Key idea: frequently executed code can be compiled by the JIT into optimized native code rather than interpreted repeatedly.

---

## Q3. Primitive vs reference types?

```text
int age = 47;
Person p = new Person();

Conceptual method-frame view
+-------------------------+
| age = 47                |
| p = reference ----------+------+
+-------------------------+      |
                                 v
                         +---------------+
                         | Person object |
                         +---------------+
```

Primitive variables contain primitive values. Reference variables contain references to objects (or `null`). Avoid the oversimplification that “all primitives are always stack and all objects are always heap”; actual storage/optimization depends on context and JVM implementation.

---

## Q4. Is Java pass-by-value or pass-by-reference?

**Java is always pass-by-value.** For an object variable, the value being copied is the reference.

```java
static void rename(Person p) {
    p.name = "Bob";
}

static void replace(Person p) {
    p = new Person("Alice");
}
```

```text
Caller:
p ---------> Person("John")

rename(p)
    copied reference ---------+
                              v
                         same Person

replace(p)
    local copied reference ----> new Person("Alice")

Caller's p still points to the original object.
```

**Interview trap:** A method can mutate an object through the copied reference, but assigning a new object to the parameter does not replace the caller's variable.

---

## Q5. `==` vs `equals()`?

For primitives, `==` compares values. For references, `==` tests whether two references refer to the same object. `equals()` is a method whose semantic equality behavior depends on its implementation.

```java
String a = new String("java");
String b = new String("java");

System.out.println(a == b);      // false
System.out.println(a.equals(b)); // true
```

```text
a ----> [ "java" object A ]
b ----> [ "java" object B ]

==       -> same object?       NO
equals() -> same characters?   YES (String implementation)
```

---

## Q6. Why is String immutable?

Once a `String` is created, its logical value cannot be changed.

```java
String s = "hello";
s.concat(" world");
System.out.println(s); // hello

s = s.concat(" world");
System.out.println(s); // hello world
```

```text
Before
s ----> "hello"

s.concat(" world") creates another String

          +----> "hello world"
          |
s ----> "hello"

After assignment
s ----------------> "hello world"
```

Why useful:
- safe sharing of values;
- enables string-pool reuse;
- stable hash code behavior when used as map keys;
- simpler reasoning in concurrent code;
- useful for security-sensitive values such as class names, paths and URLs.

---

## Q7. What is the String Pool?

```java
String a = "java";
String b = "java";
String c = new String("java");

System.out.println(a == b); // true (normally same interned literal)
System.out.println(a == c); // false
```

```text
String Pool
+----------------+
| "java"         | <------ a
|                | <------ b
+----------------+

Heap object created explicitly
+----------------+
| String "java"  | <------ c
+----------------+
```

`intern()` returns the canonical pooled representation for an equal string.

---

## Q8. String vs StringBuilder vs StringBuffer?

| Type | Mutable | Synchronization | Typical use |
|---|---|---|---|
| String | No | N/A | immutable text/value |
| StringBuilder | Yes | No | local string construction |
| StringBuffer | Yes | synchronized methods | legacy/shared synchronized mutation cases |

```java
StringBuilder sql = new StringBuilder("SELECT * FROM users");
sql.append(" WHERE active = true");
```

Repeated concatenation in a loop can create many temporary strings. `StringBuilder` is normally preferred for explicit mutable construction within one thread.

---

## Q9. `final`, `finally`, `finalize()`?

```text
final
  -> language keyword
  -> variable cannot be reassigned
  -> method cannot be overridden
  -> class cannot be subclassed

finally
  -> cleanup block associated with try/catch

finalize()
  -> deprecated/obsolete finalization mechanism
  -> do not use for resource management
```

Use try-with-resources for closeable resources.

```java
try (var input = Files.newInputStream(path)) {
    // use input
}
```

---

## Q10. Important methods from `Object`?

Common interview methods:

```text
Object
+-- equals()
+-- hashCode()
+-- toString()
+-- getClass()
+-- clone()        [special/usually avoided]
+-- wait()
+-- notify()
+-- notifyAll()
```

**Critical rule:** if two objects are equal according to `equals()`, they must return the same `hashCode()`.

---

# 2. OOP / SOLID — 10 Questions

## Q11. Four pillars of OOP?

```text
OOP
|
+-- Encapsulation  -> protect/manage state behind behavior
+-- Abstraction    -> expose essential contract, hide detail
+-- Inheritance    -> derive behavior/type relationship
+-- Polymorphism   -> same contract, different implementations
```

```java
interface PaymentProcessor {
    void pay(BigDecimal amount);
}

class CardPaymentProcessor implements PaymentProcessor {
    public void pay(BigDecimal amount) { /* card */ }
}

class UpiPaymentProcessor implements PaymentProcessor {
    public void pay(BigDecimal amount) { /* UPI */ }
}
```

Client code can depend on `PaymentProcessor` instead of a concrete implementation.

---

## Q12. Abstraction vs encapsulation?

```text
Abstraction
   -> WHAT capability is exposed?

Encapsulation
   -> HOW state/implementation is protected and controlled?
```

---

## Q13. Overloading vs overriding?

```text
Overloading
same method name + different parameter list
resolved primarily at compile time

Overriding
subclass supplies implementation of inherited virtual method
runtime dynamic dispatch chooses implementation
```

---

## Q14. Interface vs abstract class?

Use an interface primarily for a contract/capability that different types can implement. Use an abstract class when related implementations genuinely share state or base implementation and an inheritance relationship is appropriate.

---

## Q15. Composition vs inheritance?

Prefer composition when the relationship is **has-a** rather than **is-a**, and when behavior should be replaceable.

```java
class OrderService {
    private final PaymentProcessor paymentProcessor;

    OrderService(PaymentProcessor paymentProcessor) {
        this.paymentProcessor = paymentProcessor;
    }
}
```

```text
OrderService
     |
     | HAS-A
     v
PaymentProcessor
     |
 +---+---+
 |       |
Card    UPI
```

This is easier to test and change than tightly coupling behavior through inheritance.

---

## Q16. What is SOLID?

```text
S  Single Responsibility
O  Open/Closed
L  Liskov Substitution
I  Interface Segregation
D  Dependency Inversion
```

---

## Q17. SRP?

A module/class should have one cohesive responsibility / one primary reason to change.

Bad:

```java
class OrderService {
    void createOrder() {}
    void sendEmail() {}
    void generatePdf() {}
    void saveAuditLog() {}
}
```

Better separation:

```text
OrderService
   +--> OrderRepository
   +--> NotificationService
   +--> InvoiceService
   +--> AuditService
```

---

## Q18. Open/Closed Principle?

Software entities should be open for extension but closed to unnecessary modification.

```java
interface DiscountPolicy {
    BigDecimal discount(Order order);
}
```

New discount behavior can be added by introducing another implementation instead of continuously modifying a giant conditional.

---

## Q19. LSP and ISP?

**LSP:** Subtypes should be usable where their base abstraction is expected without violating its behavioral contract.

**ISP:** Prefer focused interfaces so clients are not forced to depend on methods they do not need.

---

## Q20. Dependency Inversion Principle?

High-level policy should depend on abstractions rather than concrete infrastructure details.

```text
BAD
OrderService ---> StripePaymentService

BETTER
OrderService ---> PaymentGateway <--- StripePaymentGateway
                              <--- AdyenPaymentGateway
```

Spring constructor injection naturally supports this style.

---

# 3. Collections / HashMap — 15 Questions

## Q21. Explain the Java Collections hierarchy.

```text
Iterable
  |
Collection
  |
  +-- List
  |    +-- ArrayList
  |    +-- LinkedList
  |
  +-- Set
  |    +-- HashSet
  |    +-- LinkedHashSet
  |    +-- SortedSet
  |         +-- TreeSet
  |
  +-- Queue
       +-- Deque
            +-- ArrayDeque

Map (separate hierarchy)
  +-- HashMap
  +-- LinkedHashMap
  +-- SortedMap
  |    +-- TreeMap
  +-- ConcurrentHashMap
```

---

## Q22. ArrayList vs LinkedList?

| Operation / characteristic | ArrayList | LinkedList |
|---|---|---|
| indexed access | O(1) | O(n) |
| append amortized | O(1) | O(1) |
| memory locality | good | poor |
| per-element overhead | lower | higher |
| insert/remove after known node/iterator | shifting may be needed | link update can be O(1) |

In typical backend code, `ArrayList` is the default list choice unless there is a specific reason otherwise.

---

## Q23. How does HashMap work internally? ★★★

High-level `put(key, value)` path:

```text
key
 |
 v
hashCode()
 |
 v
spread/mix hash
 |
 v
bucket index
 |
 v
table[index]
 |
 +--> empty? --------> insert
 |
 +--> matching key? -> replace value
 |
 +--> collision -----> traverse list/tree
```

Simplified conceptual table:

```text
HashMap table[]

[0] -> null
[1] -> (K1,V1) -> (K9,V9)
[2] -> null
[3] -> (K2,V2)
[4] -> TreeNode -> TreeNode -> ...
```

Lookup uses hash/bucket selection and then key equality checks. Average expected lookup is O(1) with a good hash distribution; worst cases are handled more defensively in modern Java using tree bins under specific conditions.

---

## Q24. Why do `equals()` and `hashCode()` matter for HashMap?

Contract:

```text
a.equals(b) == true
        =>
a.hashCode() == b.hashCode()
```

The reverse is not required: equal hash codes can belong to unequal objects.

```java
record UserId(long value) {}

Map<UserId, String> users = new HashMap<>();
users.put(new UserId(10), "Alice");
System.out.println(users.get(new UserId(10))); // Alice
```

Records generate value-oriented `equals()`/`hashCode()` based on components, making them convenient for many value-key use cases.

---

## Q25. What is a HashMap collision?

Different keys can map to the same bucket.

```text
K1 --hash--> bucket 5
K2 --hash--> bucket 5
K3 --hash--> bucket 5

bucket 5
   |
   v
[K1] -> [K2] -> [K3]
```

The map then distinguishes keys using hash/equality checks.

---

## Q26. Load factor and resizing?

Default `HashMap` load factor is commonly `0.75`.

```text
capacity = 16
loadFactor = .75
threshold = 12

size exceeds threshold
        |
        v
resize table (typically capacity grows)
        |
        v
entries redistributed into new table structure
```

Resizing costs work and can temporarily increase allocation/CPU pressure. If a large size is known in advance, appropriate initial sizing can reduce resizing.

---

## Q27. What is HashMap treeification?

When a bucket becomes sufficiently collision-heavy, modern `HashMap` can convert the bucket from a linked structure into a red-black tree, subject to implementation thresholds including sufficient overall table capacity.

```text
Before
bucket -> A -> B -> C -> D -> ...

After threshold conditions

             D
           /   \
          B     F
         / \   / \
        A   C E   G
```

Know the idea more importantly than memorizing magic numbers, though Java 8+ implementation discussions often mention a treeification threshold of 8 and minimum table capacity of 64.

---

## Q28. Why are mutable HashMap keys dangerous?

```java
class UserKey {
    String email;
    // equals/hashCode based on email
}
```

```text
put(key)
email="a@x.com"
      |
      v
bucket 3

mutate key.email="b@x.com"
      |
      v
new hash points conceptually toward bucket 7

BUT entry remains stored in bucket 3
=> lookup can fail
```

Prefer immutable keys whose equality/hash-relevant state does not change while they are in the map.

---

## Q29. HashMap vs LinkedHashMap?

`LinkedHashMap` maintains a linked ordering in addition to hash lookup. It can maintain insertion order or access order.

Classic interview use: LRU-style cache.

```java
class LruCache<K,V> extends LinkedHashMap<K,V> {
    private final int max;

    LruCache(int max) {
        super(16, 0.75f, true); // access order
        this.max = max;
    }

    @Override
    protected boolean removeEldestEntry(Map.Entry<K,V> eldest) {
        return size() > max;
    }
}
```

---

## Q30. HashMap vs TreeMap?

```text
HashMap
+ expected O(1) get/put
+ no sorted-key guarantee

TreeMap
+ O(log n) get/put
+ sorted according to natural order or Comparator
+ navigation/range operations
```

Choose based on requirements, not simply Big-O.

---

## Q31. Comparable vs Comparator?

```java
class Employee implements Comparable<Employee> {
    int id;

    @Override
    public int compareTo(Employee other) {
        return Integer.compare(id, other.id);
    }
}

Comparator<Employee> byName =
        Comparator.comparing(e -> e.name);
```

`Comparable` defines a type's natural ordering. `Comparator` defines an external/custom ordering and allows multiple orderings.

---

## Q32. What is fail-fast iteration?

Many standard collection iterators detect structural modification outside the iterator during iteration and may throw `ConcurrentModificationException` on a best-effort basis.

```java
for (String s : list) {
    if (s.isBlank()) {
        list.remove(s); // unsafe pattern
    }
}
```

Use iterator removal where supported, collection operations such as `removeIf`, or an appropriate concurrent collection depending on the requirement.

---

## Q33. HashSet internals?

Conceptually, `HashSet` is backed by a `HashMap` where set elements are stored as map keys with a dummy value.

```text
HashSet<E>
    |
    v
HashMap<E, PRESENT>
```

Therefore correct `equals()`/`hashCode()` behavior matters for uniqueness.

---

## Q34. Why isn't HashMap thread-safe?

Concurrent unsynchronized reads/writes are not given the thread-safety guarantees required for shared mutable access. Compound operations such as check-then-act are especially problematic.

```java
if (!map.containsKey(key)) {
    map.put(key, create());
}
```

Two threads can both observe absence and both create a value.

For shared concurrent maps, consider `ConcurrentHashMap` and atomic APIs such as:

```java
map.computeIfAbsent(key, k -> create());
```

Detailed `ConcurrentHashMap` internals belong in Chapter 02.

---

## Q35. Which collection would you choose?

```text
Need ordered indexed sequence?
        |
        +--> ArrayList

Need unique values?
        |
        +--> HashSet

Need key -> value lookup?
        |
        +--> HashMap

Need insertion/access ordering + map?
        |
        +--> LinkedHashMap

Need sorted keys/range navigation?
        |
        +--> TreeMap

Need queue/deque operations?
        |
        +--> ArrayDeque

Need shared concurrent key/value map?
        |
        +--> ConcurrentHashMap
```

---

# 4. Generics / Exceptions — 5 Questions

## Q36. Why use generics?

Generics provide compile-time type safety and reusable algorithms/data structures.

```java
List<String> names = new ArrayList<>();
names.add("Alice");
String name = names.get(0);
```

Without generics, callers would need casts and more type errors would surface at runtime.

---

## Q37. Explain `? extends` vs `? super` — PECS. ★★★

```text
PECS
Producer -> Extends
Consumer -> Super
```

```java
static double sum(List<? extends Number> values) {
    double total = 0;
    for (Number n : values) total += n.doubleValue();
    return total;
}

static void addInts(List<? super Integer> values) {
    values.add(10);
}
```

Visual:

```text
Need to READ values as Number
List<? extends Number>

Need to WRITE Integer values
List<? super Integer>
```

---

## Q38. What is type erasure?

Most generic type information is enforced at compile time and erased/translated for JVM execution.

```java
List<String> a = new ArrayList<>();
List<Integer> b = new ArrayList<>();

System.out.println(a.getClass() == b.getClass()); // true
```

Consequences include restrictions such as not doing `new T()` directly and not overloading solely on generic type arguments whose erased signatures collide.

---

## Q39. Checked vs unchecked exceptions?

```text
Throwable
  |
  +-- Error
  |
  +-- Exception
       |
       +-- RuntimeException  -> unchecked
       |
       +-- other Exception   -> checked
```

Senior design discussion: use exceptions to express meaningful failure semantics; don't mechanically make every business failure checked or unchecked. Consider API boundaries, recoverability, and whether callers can reasonably act on the failure.

---

## Q40. Why try-with-resources?

It closes `AutoCloseable` resources reliably and handles exception/suppression rules better than verbose manual cleanup.

```java
try (BufferedReader reader = Files.newBufferedReader(path)) {
    return reader.readLine();
}
```

```text
Acquire
  |
  v
Use resource
  |
  +-- success ----+
  |               |
  +-- exception --+
                  v
              close()
```

---

# 5. Java 8+ / Modern Java — 10 Questions

## Q41. What is a lambda expression?

A lambda provides an implementation for a functional-interface target.

```java
List<String> names = List.of("Bob", "Alice", "John");
names.forEach(name -> System.out.println(name));
```

```text
(parameters) -> expression/body
```

---

## Q42. What is a functional interface?

An interface with one abstract method (it may also contain default/static methods).

```java
@FunctionalInterface
interface Calculator {
    int calculate(int a, int b);
}

Calculator add = (a, b) -> a + b;
```

Important built-ins:

```text
Predicate<T>      T -> boolean
Function<T,R>     T -> R
Consumer<T>       T -> void
Supplier<T>       () -> T
```

---

## Q43. How does a Stream pipeline work? ★★★

```java
List<String> result = users.stream()
        .filter(User::isActive)
        .map(User::getName)
        .sorted()
        .toList();
```

```text
SOURCE
 users
   |
   v
INTERMEDIATE
 filter(active)
   |
   v
 map(name)
   |
   v
 sorted()
   |
   v
TERMINAL
 toList()
```

Intermediate operations are generally lazy; processing begins when a terminal operation is invoked. Streams describe a computation pipeline; they are not data structures.

---

## Q44. `map()` vs `flatMap()`?

```java
List<List<String>> teams = List.of(
    List.of("A", "B"),
    List.of("C", "D")
);

List<String> all = teams.stream()
        .flatMap(Collection::stream)
        .toList();
```

```text
map
A -> X
B -> Y

flatMap
[A,B] [C,D]
   |
   v
A B C D
```

Use `flatMap` when each input produces a nested/stream-like result and you want one flattened stream.

---

## Q45. `reduce()` vs `collect()`?

`reduce` combines stream elements into a value using an associative reduction operation.

```java
int sum = List.of(1,2,3,4)
        .stream()
        .reduce(0, Integer::sum);
```

`collect` performs mutable reduction into containers/results such as lists, maps or grouped structures.

```java
Map<String, List<Employee>> byDept = employees.stream()
        .collect(Collectors.groupingBy(Employee::department));
```

---

## Q46. What is Optional and when should you use it?

```java
Optional<User> user = repository.findById(id);

String name = user
        .map(User::getName)
        .orElse("Unknown");
```

Good use: representing a possibly absent return value in APIs where that improves clarity.

Avoid treating `Optional` as a universal replacement for every nullable field/parameter or blindly calling `get()`.

---

## Q47. What are method references?

```java
names.forEach(System.out::println);
users.stream().map(User::getName).toList();
```

They are concise syntax for lambdas that simply invoke an existing method/constructor.

---

## Q48. What are records?

Records are concise data-carrier classes with generated accessors, constructor, `equals`, `hashCode`, and `toString` based on record components.

```java
public record UserDto(long id, String name) {}
```

```text
Traditional DTO
  fields
  constructor
  accessors
  equals
  hashCode
  toString

       ↓

record UserDto(long id, String name)
```

Records are shallowly immutable in the sense that component fields cannot be reassigned after construction; referenced mutable objects can still mutate internally.

---

## Q49. What are sealed classes?

They restrict which types may extend/implement a type.

```java
sealed interface PaymentResult
        permits Success, Failure {}

record Success(String transactionId) implements PaymentResult {}
record Failure(String reason) implements PaymentResult {}
```

Useful for modeling closed sets of domain alternatives and enabling exhaustive reasoning with modern pattern matching.

---

## Q50. Senior scenario: Streams, loops, or parallel streams?

Don't answer “streams are always better” or “loops are always faster.”

```text
Question
   |
   v
Is transformation naturally pipeline-shaped?
   | yes
   +--> Stream may improve clarity
   |
   no
   +--> loop may be simpler

Need parallelism?
   |
   v
Is work CPU-bound, large enough, splittable,
stateless, and safe for shared execution?
   |
   +--> maybe parallel stream
   |
   +--> otherwise avoid blindly using it
```

For backend request processing, blindly using `parallelStream()` can create contention and unpredictable interaction with the common ForkJoinPool. For I/O concurrency, use an explicit concurrency model appropriate to the application; later chapters cover executors, `CompletableFuture`, and virtual threads.

---

# Senior Cross-Question Drill

A strong interviewer rarely stops after the first answer.

```text
HashMap
  |
  +--> How does put() work?
  |      |
  |      +--> How is bucket selected?
  |      +--> What is a collision?
  |      +--> Why equals() + hashCode()?
  |      +--> What triggers resize?
  |      +--> What is treeification?
  |
  +--> What if key is mutable?
  |
  +--> Why isn't it thread-safe?
  |
  +--> What would you use concurrently?
         |
         +--> ConcurrentHashMap
                |
                +--> How does computeIfAbsent help?
                +--> What are compound operations?
                +--> Chapter 02 internals
```

---

# Mini Coding Drills

## Drill 1 — Correct value object key

```java
public record CustomerId(long value) {
    public CustomerId {
        if (value <= 0) {
            throw new IllegalArgumentException("value must be positive");
        }
    }
}
```

Why this works well as a map key: compact value semantics and stable state.

---

## Drill 2 — Group transactions by account

```java
Map<String, List<Transaction>> byAccount = transactions.stream()
        .collect(Collectors.groupingBy(Transaction::accountId));
```

Follow-up: what if the collection is huge? Discuss memory, whether grouping is actually necessary, database aggregation, streaming/batching, and downstream requirements.

---

## Drill 3 — Strategy instead of `if/else`

```java
interface ShippingCost {
    BigDecimal calculate(Order order);
}

final class StandardShipping implements ShippingCost {
    public BigDecimal calculate(Order order) {
        return new BigDecimal("5.00");
    }
}

final class ExpressShipping implements ShippingCost {
    public BigDecimal calculate(Order order) {
        return new BigDecimal("15.00");
    }
}
```

```text
CheckoutService
      |
      v
ShippingCost
   /       \
Standard  Express
```

This connects Core Java polymorphism/SOLID to later Spring dependency injection and LLD chapters.

---

# Production Thinking — Chapter 01

## Scenario A — HashMap lookup unexpectedly fails

```text
Symptom
Map contains key but get(key) returns null
       |
       v
Check equals/hashCode implementation
       |
       v
Was equality-relevant key state mutated?
       |
       v
Was the correct key type/value used?
```

## Scenario B — High allocation during string construction

```text
Request loop
   |
   v
repeated String concatenation
   |
   v
many temporary objects
   |
   v
higher allocation rate
   |
   v
more GC pressure

Possible fix:
StringBuilder / better API / avoid unnecessary construction
```

## Scenario C — Wrong collection chosen

```text
Need frequent contains(key)
        |
        v
Using List + linear scan
        |
        v
O(n) per lookup
        |
        v
large request latency

If semantics permit:
HashSet / HashMap -> expected O(1)
```

---

# 50-Question Quick Revision Checklist

```text
FUNDAMENTALS
[ ] 01 JDK/JRE/JVM
[ ] 02 Compilation/execution
[ ] 03 Primitive/reference
[ ] 04 Pass-by-value
[ ] 05 == vs equals
[ ] 06 String immutability
[ ] 07 String pool
[ ] 08 StringBuilder/StringBuffer
[ ] 09 final/finally/finalize
[ ] 10 Object methods

OOP / SOLID
[ ] 11 OOP pillars
[ ] 12 Abstraction/encapsulation
[ ] 13 Overloading/overriding
[ ] 14 Interface/abstract class
[ ] 15 Composition/inheritance
[ ] 16 SOLID
[ ] 17 SRP
[ ] 18 OCP
[ ] 19 LSP/ISP
[ ] 20 DIP

COLLECTIONS
[ ] 21 Collections hierarchy
[ ] 22 ArrayList/LinkedList
[ ] 23 HashMap internals
[ ] 24 equals/hashCode
[ ] 25 Collision
[ ] 26 Resize/load factor
[ ] 27 Treeification
[ ] 28 Mutable keys
[ ] 29 LinkedHashMap
[ ] 30 TreeMap
[ ] 31 Comparable/Comparator
[ ] 32 Fail-fast
[ ] 33 HashSet
[ ] 34 HashMap thread safety
[ ] 35 Collection selection

GENERICS / EXCEPTIONS
[ ] 36 Generics
[ ] 37 PECS
[ ] 38 Type erasure
[ ] 39 Checked/unchecked
[ ] 40 Try-with-resources

JAVA 8+ / MODERN JAVA
[ ] 41 Lambda
[ ] 42 Functional interfaces
[ ] 43 Stream pipeline
[ ] 44 map/flatMap
[ ] 45 reduce/collect
[ ] 46 Optional
[ ] 47 Method references
[ ] 48 Records
[ ] 49 Sealed classes
[ ] 50 Stream/loop/parallel-stream trade-off
```

---

# Interview Answer Framework

For every important question, practice this sequence:

```text
1. WHAT?
   |
   v
2. HOW DOES IT WORK?
   |
   v
3. WHY / TRADE-OFF?
   |
   v
4. WHAT CAN GO WRONG?
   |
   v
5. PRODUCTION EXAMPLE
```

Example:

```text
HashMap
  |
  +--> WHAT? key/value hash-based map
  |
  +--> HOW? hash -> bucket -> equality -> list/tree
  |
  +--> WHY? expected O(1) lookup
  |
  +--> FAILURE? bad hash / mutable key / concurrency misuse
  |
  +--> PRODUCTION? lookup tables, caches/local indexes,
                   request aggregation — when semantics fit
```

---

# Chapter 01 Exit Criteria

You are ready to move to **CH 02 — Concurrency** when you can:

1. Explain all 50 questions aloud without reading a script.
2. Draw `HashMap` internals from memory.
3. Explain the `equals()` / `hashCode()` contract and mutable-key failure.
4. Explain pass-by-value using a reference diagram.
5. Apply SOLID to a small backend example rather than only reciting definitions.
6. Write common Stream transformations without searching syntax.
7. Explain when *not* to use streams/parallel streams.
8. Answer at least one production scenario for Strings, collections, and equality.

```text
CH 01 CORE JAVA
      |
      v
Fundamentals
      |
      v
OOP / SOLID
      |
      v
Collections / HashMap ★★★
      |
      v
Generics / Exceptions
      |
      v
Java 8+ / Modern Java
      |
      v
READY FOR CH 02
Concurrency + JMM + Thread Safety
```

---

**Next:** CH 02 — Concurrency (~50 Q): Threads → synchronization → `volatile` → JMM → happens-before → Locks → CAS → Executors → ThreadPool → CompletableFuture → Virtual Threads.
