# Java 8 Features: Learn by Doing

A beginner-friendly, step-by-step guide to the most important Java 8 features.

This guide focuses on practical examples you can copy, run, change, and learn from.

---

## Table of Contents

1. [Before You Start](#1-before-you-start)
2. [Lambda Expressions](#2-lambda-expressions)
3. [Functional Interfaces](#3-functional-interfaces)
4. [Built-in Functional Interfaces](#4-built-in-functional-interfaces)
5. [Method References](#5-method-references)
6. [Streams](#6-streams)
7. [Stream Operations](#7-stream-operations)
8. [Collectors](#8-collectors)
9. [Optional](#9-optional)
10. [Default and Static Methods in Interfaces](#10-default-and-static-methods-in-interfaces)
11. [Java 8 Date and Time API](#11-java-8-date-and-time-api)
12. [Mini Project: Employee Report](#12-mini-project-employee-report)
13. [Practice Tasks](#13-practice-tasks)

---

## 1. Before You Start

### Requirements

You need:

- Java 8 or later
- Any editor or IDE
- Basic Java knowledge: classes, objects, interfaces, lists, loops

### Check Java Version

Open your terminal and run:

```bash
java -version
```

You should see Java 8 or higher.

---

## 2. Lambda Expressions

A lambda expression is a short way to write an anonymous function.

Before Java 8, you often wrote anonymous classes. Java 8 lets you replace many of them with lambdas.

---

### 2.1 Example: Runnable Before Java 8

```java
public class LambdaBeforeJava8 {
    public static void main(String[] args) {
        Runnable task = new Runnable() {
            @Override
            public void run() {
                System.out.println("Running task...");
            }
        };

        task.run();
    }
}
```

### Output

```text
Running task...
```

---

### 2.2 Same Example Using Lambda

```java
public class LambdaExample {
    public static void main(String[] args) {
        Runnable task = () -> System.out.println("Running task...");

        task.run();
    }
}
```

### What Changed?

```java
() -> System.out.println("Running task...")
```

This means:

- `()` means no input parameters
- `->` means lambda arrow
- Right side is the code to execute

---

### 2.3 Lambda With One Parameter

```java
interface Printer {
    void print(String message);
}

public class LambdaOneParameter {
    public static void main(String[] args) {
        Printer printer = message -> System.out.println(message);

        printer.print("Hello Java 8");
    }
}
```

### Output

```text
Hello Java 8
```

You can also write it like this:

```java
Printer printer = (String message) -> System.out.println(message);
```

Java can usually guess the type, so this is enough:

```java
Printer printer = message -> System.out.println(message);
```

---

### 2.4 Lambda With Multiple Parameters

```java
interface Calculator {
    int add(int a, int b);
}

public class LambdaMultipleParameters {
    public static void main(String[] args) {
        Calculator calculator = (a, b) -> a + b;

        int result = calculator.add(10, 20);
        System.out.println(result);
    }
}
```

### Output

```text
30
```

---

### 2.5 Lambda With Multiple Lines

```java
interface MathOperation {
    int operate(int a, int b);
}

public class LambdaMultipleLines {
    public static void main(String[] args) {
        MathOperation multiplication = (a, b) -> {
            int result = a * b;
            return result;
        };

        System.out.println(multiplication.operate(5, 4));
    }
}
```

### Output

```text
20
```

When a lambda has multiple lines, use `{}` and `return` if a value must be returned.

---

## 3. Functional Interfaces

A functional interface is an interface with exactly one abstract method.

Lambdas work with functional interfaces.

---

### 3.1 Create Your Own Functional Interface

```java
@FunctionalInterface
interface Greeting {
    void sayHello(String name);
}

public class FunctionalInterfaceExample {
    public static void main(String[] args) {
        Greeting greeting = name -> System.out.println("Hello, " + name);

        greeting.sayHello("Amit");
    }
}
```

### Output

```text
Hello, Amit
```

### Why Use `@FunctionalInterface`?

It is optional, but recommended.

It tells the compiler:

> This interface must have only one abstract method.

If you add a second abstract method, Java gives an error.

---

### 3.2 Functional Interface With Return Value

```java
@FunctionalInterface
interface Square {
    int calculate(int number);
}

public class FunctionalInterfaceReturnExample {
    public static void main(String[] args) {
        Square square = number -> number * number;

        System.out.println(square.calculate(6));
    }
}
```

### Output

```text
36
```

---

## 4. Built-in Functional Interfaces

Java 8 provides many ready-made functional interfaces in:

```java
java.util.function
```

The most common ones are:

| Interface | Input | Output | Use Case |
|---|---:|---:|---|
| `Predicate<T>` | T | boolean | Check condition |
| `Function<T, R>` | T | R | Convert one value to another |
| `Consumer<T>` | T | void | Perform action |
| `Supplier<T>` | none | T | Provide value |
| `BiFunction<T, U, R>` | T, U | R | Convert two inputs to one output |
| `UnaryOperator<T>` | T | T | Input and output same type |
| `BinaryOperator<T>` | T, T | T | Two same-type inputs, same-type output |

---

### 4.1 Predicate Example

`Predicate` checks a condition and returns `true` or `false`.

```java
import java.util.function.Predicate;

public class PredicateExample {
    public static void main(String[] args) {
        Predicate<Integer> isEven = number -> number % 2 == 0;

        System.out.println(isEven.test(10));
        System.out.println(isEven.test(7));
    }
}
```

### Output

```text
true
false
```

---

### 4.2 Function Example

`Function` converts input into output.

```java
import java.util.function.Function;

public class FunctionExample {
    public static void main(String[] args) {
        Function<String, Integer> stringLength = text -> text.length();

        System.out.println(stringLength.apply("Java 8"));
    }
}
```

### Output

```text
6
```

---

### 4.3 Consumer Example

`Consumer` accepts input but returns nothing.

```java
import java.util.function.Consumer;

public class ConsumerExample {
    public static void main(String[] args) {
        Consumer<String> printer = text -> System.out.println(text);

        printer.accept("Learning Java 8");
    }
}
```

### Output

```text
Learning Java 8
```

---

### 4.4 Supplier Example

`Supplier` takes no input but returns a value.

```java
import java.util.function.Supplier;

public class SupplierExample {
    public static void main(String[] args) {
        Supplier<Double> randomNumber = () -> Math.random();

        System.out.println(randomNumber.get());
    }
}
```

### Example Output

```text
0.7352923101
```

Your output will be different because `Math.random()` returns a random number.

---

### 4.5 BiFunction Example

`BiFunction` accepts two inputs and returns one output.

```java
import java.util.function.BiFunction;

public class BiFunctionExample {
    public static void main(String[] args) {
        BiFunction<Integer, Integer, Integer> add = (a, b) -> a + b;

        System.out.println(add.apply(10, 25));
    }
}
```

### Output

```text
35
```

---

### 4.6 UnaryOperator Example

`UnaryOperator` accepts one value and returns the same type.

```java
import java.util.function.UnaryOperator;

public class UnaryOperatorExample {
    public static void main(String[] args) {
        UnaryOperator<Integer> doubleNumber = number -> number * 2;

        System.out.println(doubleNumber.apply(8));
    }
}
```

### Output

```text
16
```

---

### 4.7 BinaryOperator Example

`BinaryOperator` accepts two values of the same type and returns the same type.

```java
import java.util.function.BinaryOperator;

public class BinaryOperatorExample {
    public static void main(String[] args) {
        BinaryOperator<Integer> multiply = (a, b) -> a * b;

        System.out.println(multiply.apply(6, 7));
    }
}
```

### Output

```text
42
```

---

## 5. Method References

A method reference is a shorter form of a lambda when the lambda only calls an existing method.

---

### 5.1 Lambda vs Method Reference

Lambda:

```java
name -> System.out.println(name)
```

Method reference:

```java
System.out::println
```

---

### 5.2 Static Method Reference

```java
import java.util.function.Function;

public class StaticMethodReferenceExample {
    public static int square(int number) {
        return number * number;
    }

    public static void main(String[] args) {
        Function<Integer, Integer> squareFunction = StaticMethodReferenceExample::square;

        System.out.println(squareFunction.apply(5));
    }
}
```

### Output

```text
25
```

---

### 5.3 Instance Method Reference

```java
import java.util.function.Consumer;

class MessagePrinter {
    public void printMessage(String message) {
        System.out.println(message);
    }
}

public class InstanceMethodReferenceExample {
    public static void main(String[] args) {
        MessagePrinter printer = new MessagePrinter();

        Consumer<String> consumer = printer::printMessage;

        consumer.accept("Hello from method reference");
    }
}
```

### Output

```text
Hello from method reference
```

---

### 5.4 Constructor Reference

```java
import java.util.function.Supplier;

class Student {
    Student() {
        System.out.println("Student object created");
    }
}

public class ConstructorReferenceExample {
    public static void main(String[] args) {
        Supplier<Student> studentSupplier = Student::new;

        Student student = studentSupplier.get();
    }
}
```

### Output

```text
Student object created
```

---

## 6. Streams

A stream is used to process collections in a clean and powerful way.

A stream does not store data. It processes data from a source like a `List`, `Set`, or array.

Common stream tasks:

- Filter data
- Transform data
- Sort data
- Count data
- Group data
- Find data
- Reduce data

---

### 6.1 Stream Without Java 8

Find even numbers from a list.

```java
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class WithoutStreamExample {
    public static void main(String[] args) {
        List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5, 6);
        List<Integer> evenNumbers = new ArrayList<>();

        for (Integer number : numbers) {
            if (number % 2 == 0) {
                evenNumbers.add(number);
            }
        }

        System.out.println(evenNumbers);
    }
}
```

### Output

```text
[2, 4, 6]
```

---

### 6.2 Same Example With Stream

```java
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class StreamFilterExample {
    public static void main(String[] args) {
        List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5, 6);

        List<Integer> evenNumbers = numbers.stream()
                .filter(number -> number % 2 == 0)
                .collect(Collectors.toList());

        System.out.println(evenNumbers);
    }
}
```

### Output

```text
[2, 4, 6]
```

### Step-by-Step

```java
numbers.stream()
```

Creates a stream from the list.

```java
.filter(number -> number % 2 == 0)
```

Keeps only numbers that are even.

```java
.collect(Collectors.toList())
```

Converts the result back into a list.

---

## 7. Stream Operations

Stream operations are usually divided into two types:

| Type | Meaning | Examples |
|---|---|---|
| Intermediate operations | Return another stream | `filter`, `map`, `sorted`, `distinct`, `limit`, `skip` |
| Terminal operations | End the stream and return a result | `collect`, `forEach`, `count`, `reduce`, `findFirst`, `anyMatch` |

A stream pipeline usually looks like this:

```java
source.stream()
      .intermediateOperation()
      .intermediateOperation()
      .terminalOperation();
```

---

### 7.1 forEach

Use `forEach` to loop through data.

```java
import java.util.Arrays;
import java.util.List;

public class ForEachExample {
    public static void main(String[] args) {
        List<String> names = Arrays.asList("Amit", "Neha", "Raj");

        names.stream()
                .forEach(name -> System.out.println(name));
    }
}
```

You can write it shorter with method reference:

```java
names.stream().forEach(System.out::println);
```

### Output

```text
Amit
Neha
Raj
```

---

### 7.2 filter

Use `filter` to keep only matching values.

```java
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class FilterExample {
    public static void main(String[] args) {
        List<String> names = Arrays.asList("Amit", "Neha", "Ankit", "Raj");

        List<String> namesStartingWithA = names.stream()
                .filter(name -> name.startsWith("A"))
                .collect(Collectors.toList());

        System.out.println(namesStartingWithA);
    }
}
```

### Output

```text
[Amit, Ankit]
```

---

### 7.3 map

Use `map` to transform values.

```java
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class MapExample {
    public static void main(String[] args) {
        List<String> names = Arrays.asList("amit", "neha", "raj");

        List<String> upperCaseNames = names.stream()
                .map(name -> name.toUpperCase())
                .collect(Collectors.toList());

        System.out.println(upperCaseNames);
    }
}
```

You can also write:

```java
.map(String::toUpperCase)
```

### Output

```text
[AMIT, NEHA, RAJ]
```

---

### 7.4 sorted

```java
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class SortedExample {
    public static void main(String[] args) {
        List<Integer> numbers = Arrays.asList(5, 2, 8, 1, 3);

        List<Integer> sortedNumbers = numbers.stream()
                .sorted()
                .collect(Collectors.toList());

        System.out.println(sortedNumbers);
    }
}
```

### Output

```text
[1, 2, 3, 5, 8]
```

---

### 7.5 sorted With Custom Comparator

```java
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class CustomSortedExample {
    public static void main(String[] args) {
        List<String> names = Arrays.asList("Amit", "Neha", "Raj", "Ankit");

        List<String> sortedByLength = names.stream()
                .sorted(Comparator.comparing(name -> name.length()))
                .collect(Collectors.toList());

        System.out.println(sortedByLength);
    }
}
```

### Output

```text
[Raj, Amit, Neha, Ankit]
```

---

### 7.6 distinct

```java
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class DistinctExample {
    public static void main(String[] args) {
        List<Integer> numbers = Arrays.asList(1, 2, 2, 3, 3, 4, 5);

        List<Integer> uniqueNumbers = numbers.stream()
                .distinct()
                .collect(Collectors.toList());

        System.out.println(uniqueNumbers);
    }
}
```

### Output

```text
[1, 2, 3, 4, 5]
```

---

### 7.7 limit

```java
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class LimitExample {
    public static void main(String[] args) {
        List<Integer> numbers = Arrays.asList(10, 20, 30, 40, 50);

        List<Integer> firstThree = numbers.stream()
                .limit(3)
                .collect(Collectors.toList());

        System.out.println(firstThree);
    }
}
```

### Output

```text
[10, 20, 30]
```

---

### 7.8 skip

```java
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class SkipExample {
    public static void main(String[] args) {
        List<Integer> numbers = Arrays.asList(10, 20, 30, 40, 50);

        List<Integer> afterSkippingTwo = numbers.stream()
                .skip(2)
                .collect(Collectors.toList());

        System.out.println(afterSkippingTwo);
    }
}
```

### Output

```text
[30, 40, 50]
```

---

### 7.9 count

```java
import java.util.Arrays;
import java.util.List;

public class CountExample {
    public static void main(String[] args) {
        List<String> names = Arrays.asList("Amit", "Neha", "Ankit", "Raj");

        long count = names.stream()
                .filter(name -> name.startsWith("A"))
                .count();

        System.out.println(count);
    }
}
```

### Output

```text
2
```

---

### 7.10 anyMatch, allMatch, noneMatch

```java
import java.util.Arrays;
import java.util.List;

public class MatchExample {
    public static void main(String[] args) {
        List<Integer> numbers = Arrays.asList(2, 4, 6, 8);

        boolean anyGreaterThan5 = numbers.stream().anyMatch(n -> n > 5);
        boolean allEven = numbers.stream().allMatch(n -> n % 2 == 0);
        boolean noneNegative = numbers.stream().noneMatch(n -> n < 0);

        System.out.println(anyGreaterThan5);
        System.out.println(allEven);
        System.out.println(noneNegative);
    }
}
```

### Output

```text
true
true
true
```

---

### 7.11 findFirst

```java
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class FindFirstExample {
    public static void main(String[] args) {
        List<String> names = Arrays.asList("Amit", "Neha", "Ankit", "Raj");

        Optional<String> firstNameStartingWithA = names.stream()
                .filter(name -> name.startsWith("A"))
                .findFirst();

        firstNameStartingWithA.ifPresent(System.out::println);
    }
}
```

### Output

```text
Amit
```

---

### 7.12 reduce

Use `reduce` to combine values into one result.

```java
import java.util.Arrays;
import java.util.List;

public class ReduceExample {
    public static void main(String[] args) {
        List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5);

        int sum = numbers.stream()
                .reduce(0, (a, b) -> a + b);

        System.out.println(sum);
    }
}
```

### Output

```text
15
```

### Step-by-Step

```java
.reduce(0, (a, b) -> a + b)
```

- `0` is the starting value
- `(a, b) -> a + b` adds each number to the result

---

## 8. Collectors

Collectors are used to convert stream results into lists, sets, maps, strings, groups, and statistics.

Import:

```java
import java.util.stream.Collectors;
```

---

### 8.1 Collect to List

```java
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class CollectToListExample {
    public static void main(String[] args) {
        List<String> names = Arrays.asList("Amit", "Neha", "Raj");

        List<String> upperCaseNames = names.stream()
                .map(String::toUpperCase)
                .collect(Collectors.toList());

        System.out.println(upperCaseNames);
    }
}
```

### Output

```text
[AMIT, NEHA, RAJ]
```

---

### 8.2 Collect to Set

```java
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class CollectToSetExample {
    public static void main(String[] args) {
        List<String> names = Arrays.asList("Amit", "Neha", "Amit", "Raj");

        Set<String> uniqueNames = names.stream()
                .collect(Collectors.toSet());

        System.out.println(uniqueNames);
    }
}
```

### Example Output

```text
[Neha, Amit, Raj]
```

Set order is not guaranteed.

---

### 8.3 Joining Strings

```java
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class JoiningExample {
    public static void main(String[] args) {
        List<String> names = Arrays.asList("Amit", "Neha", "Raj");

        String result = names.stream()
                .collect(Collectors.joining(", "));

        System.out.println(result);
    }
}
```

### Output

```text
Amit, Neha, Raj
```

---

### 8.4 Grouping By

```java
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

class Employee {
    private String name;
    private String department;

    public Employee(String name, String department) {
        this.name = name;
        this.department = department;
    }

    public String getName() {
        return name;
    }

    public String getDepartment() {
        return department;
    }
}

public class GroupingByExample {
    public static void main(String[] args) {
        List<Employee> employees = Arrays.asList(
                new Employee("Amit", "IT"),
                new Employee("Neha", "HR"),
                new Employee("Raj", "IT"),
                new Employee("Priya", "Finance")
        );

        Map<String, List<Employee>> employeesByDepartment = employees.stream()
                .collect(Collectors.groupingBy(Employee::getDepartment));

        employeesByDepartment.forEach((department, employeeList) -> {
            System.out.println(department + ":");
            employeeList.forEach(employee -> System.out.println("  " + employee.getName()));
        });
    }
}
```

### Example Output

```text
Finance:
  Priya
HR:
  Neha
IT:
  Amit
  Raj
```

Map order is not guaranteed.

---

### 8.5 Partitioning By

`partitioningBy` splits data into two groups: `true` and `false`.

```java
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class PartitioningByExample {
    public static void main(String[] args) {
        List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5, 6);

        Map<Boolean, List<Integer>> partitioned = numbers.stream()
                .collect(Collectors.partitioningBy(number -> number % 2 == 0));

        System.out.println("Even numbers: " + partitioned.get(true));
        System.out.println("Odd numbers: " + partitioned.get(false));
    }
}
```

### Output

```text
Even numbers: [2, 4, 6]
Odd numbers: [1, 3, 5]
```

---

### 8.6 Counting

```java
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

class Product {
    private String name;
    private String category;

    public Product(String name, String category) {
        this.name = name;
        this.category = category;
    }

    public String getCategory() {
        return category;
    }
}

public class CountingExample {
    public static void main(String[] args) {
        List<Product> products = Arrays.asList(
                new Product("Laptop", "Electronics"),
                new Product("Phone", "Electronics"),
                new Product("Shirt", "Clothing")
        );

        Map<String, Long> countByCategory = products.stream()
                .collect(Collectors.groupingBy(Product::getCategory, Collectors.counting()));

        System.out.println(countByCategory);
    }
}
```

### Example Output

```text
{Clothing=1, Electronics=2}
```

---

## 9. Optional

`Optional` helps avoid `NullPointerException` by representing a value that may or may not exist.

---

### 9.1 Problem Without Optional

```java
public class WithoutOptionalExample {
    public static void main(String[] args) {
        String name = null;

        if (name != null) {
            System.out.println(name.toUpperCase());
        } else {
            System.out.println("Name not found");
        }
    }
}
```

### Output

```text
Name not found
```

---

### 9.2 Same Example With Optional

```java
import java.util.Optional;

public class OptionalExample {
    public static void main(String[] args) {
        Optional<String> name = Optional.ofNullable(null);

        System.out.println(name.orElse("Name not found"));
    }
}
```

### Output

```text
Name not found
```

---

### 9.3 Optional With Value

```java
import java.util.Optional;

public class OptionalWithValueExample {
    public static void main(String[] args) {
        Optional<String> name = Optional.of("Amit");

        name.ifPresent(value -> System.out.println(value.toUpperCase()));
    }
}
```

### Output

```text
AMIT
```

---

### 9.4 Optional map

```java
import java.util.Optional;

public class OptionalMapExample {
    public static void main(String[] args) {
        Optional<String> name = Optional.of("amit");

        Optional<String> upperName = name.map(String::toUpperCase);

        System.out.println(upperName.orElse("No name"));
    }
}
```

### Output

```text
AMIT
```

---

### 9.5 Optional filter

```java
import java.util.Optional;

public class OptionalFilterExample {
    public static void main(String[] args) {
        Optional<String> name = Optional.of("Amit");

        Optional<String> result = name.filter(value -> value.startsWith("A"));

        System.out.println(result.orElse("Not matched"));
    }
}
```

### Output

```text
Amit
```

---

### 9.6 Optional Best Practices

Prefer:

```java
optional.orElse("default value")
optional.orElseGet(() -> "default value")
optional.ifPresent(value -> System.out.println(value))
optional.map(value -> value.toUpperCase())
```

Avoid using this too much:

```java
optional.get()
```

`get()` throws an error if the value is missing.

---

## 10. Default and Static Methods in Interfaces

Java 8 allows interfaces to have:

- Default methods
- Static methods

---

### 10.1 Default Method

A default method has a body inside an interface.

```java
interface Vehicle {
    void start();

    default void stop() {
        System.out.println("Vehicle stopped");
    }
}

class Car implements Vehicle {
    @Override
    public void start() {
        System.out.println("Car started");
    }
}

public class DefaultMethodExample {
    public static void main(String[] args) {
        Car car = new Car();
        car.start();
        car.stop();
    }
}
```

### Output

```text
Car started
Vehicle stopped
```

---

### 10.2 Static Method in Interface

```java
interface MathHelper {
    static int add(int a, int b) {
        return a + b;
    }
}

public class StaticInterfaceMethodExample {
    public static void main(String[] args) {
        int result = MathHelper.add(10, 15);

        System.out.println(result);
    }
}
```

### Output

```text
25
```

---

## 11. Java 8 Date and Time API

Java 8 introduced a better date and time API in:

```java
java.time
```

Common classes:

| Class | Meaning |
|---|---|
| `LocalDate` | Date only |
| `LocalTime` | Time only |
| `LocalDateTime` | Date and time |
| `ZonedDateTime` | Date and time with time zone |
| `Period` | Difference between dates |
| `Duration` | Difference between times |
| `DateTimeFormatter` | Format date/time |

---

### 11.1 LocalDate

```java
import java.time.LocalDate;

public class LocalDateExample {
    public static void main(String[] args) {
        LocalDate today = LocalDate.now();
        LocalDate birthday = LocalDate.of(1995, 5, 20);

        System.out.println(today);
        System.out.println(birthday);
    }
}
```

---

### 11.2 LocalTime

```java
import java.time.LocalTime;

public class LocalTimeExample {
    public static void main(String[] args) {
        LocalTime now = LocalTime.now();

        System.out.println(now);
    }
}
```

---

### 11.3 LocalDateTime

```java
import java.time.LocalDateTime;

public class LocalDateTimeExample {
    public static void main(String[] args) {
        LocalDateTime now = LocalDateTime.now();

        System.out.println(now);
    }
}
```

---

### 11.4 Date Formatting

```java
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class DateFormattingExample {
    public static void main(String[] args) {
        LocalDate today = LocalDate.now();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        String formattedDate = today.format(formatter);

        System.out.println(formattedDate);
    }
}
```

### Example Output

```text
29-04-2026
```

Your output depends on the current date.

---

### 11.5 Period Between Dates

```java
import java.time.LocalDate;
import java.time.Period;

public class PeriodExample {
    public static void main(String[] args) {
        LocalDate start = LocalDate.of(2020, 1, 1);
        LocalDate end = LocalDate.of(2026, 4, 29);

        Period period = Period.between(start, end);

        System.out.println(period.getYears() + " years");
        System.out.println(period.getMonths() + " months");
        System.out.println(period.getDays() + " days");
    }
}
```

---

## 12. Mini Project: Employee Report

This mini project combines lambdas, streams, collectors, method references, and Optional.

---

### Step 1: Create Employee Class

```java
class Employee {
    private int id;
    private String name;
    private String department;
    private double salary;

    public Employee(int id, String name, String department, double salary) {
        this.id = id;
        this.name = name;
        this.department = department;
        this.salary = salary;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDepartment() {
        return department;
    }

    public double getSalary() {
        return salary;
    }

    @Override
    public String toString() {
        return id + " - " + name + " - " + department + " - " + salary;
    }
}
```

---

### Step 2: Create Employee List

```java
import java.util.Arrays;
import java.util.List;

public class EmployeeReportProject {
    public static void main(String[] args) {
        List<Employee> employees = Arrays.asList(
                new Employee(1, "Amit", "IT", 70000),
                new Employee(2, "Neha", "HR", 50000),
                new Employee(3, "Raj", "IT", 80000),
                new Employee(4, "Priya", "Finance", 65000),
                new Employee(5, "Karan", "HR", 45000)
        );
    }
}
```

---

### Step 3: Filter Employees With Salary Greater Than 60000

```java
List<Employee> highSalaryEmployees = employees.stream()
        .filter(employee -> employee.getSalary() > 60000)
        .collect(java.util.stream.Collectors.toList());

highSalaryEmployees.forEach(System.out::println);
```

### Output

```text
1 - Amit - IT - 70000.0
3 - Raj - IT - 80000.0
4 - Priya - Finance - 65000.0
```

---

### Step 4: Get Employee Names Only

```java
List<String> employeeNames = employees.stream()
        .map(Employee::getName)
        .collect(java.util.stream.Collectors.toList());

System.out.println(employeeNames);
```

### Output

```text
[Amit, Neha, Raj, Priya, Karan]
```

---

### Step 5: Sort Employees by Salary

```java
List<Employee> sortedBySalary = employees.stream()
        .sorted(java.util.Comparator.comparing(Employee::getSalary))
        .collect(java.util.stream.Collectors.toList());

sortedBySalary.forEach(System.out::println);
```

---

### Step 6: Group Employees by Department

```java
java.util.Map<String, List<Employee>> employeesByDepartment = employees.stream()
        .collect(java.util.stream.Collectors.groupingBy(Employee::getDepartment));

employeesByDepartment.forEach((department, employeeList) -> {
    System.out.println(department + ":");
    employeeList.forEach(System.out::println);
});
```

---

### Step 7: Find Highest Salary Employee

```java
java.util.Optional<Employee> highestSalaryEmployee = employees.stream()
        .max(java.util.Comparator.comparing(Employee::getSalary));

highestSalaryEmployee.ifPresent(System.out::println);
```

### Output

```text
3 - Raj - IT - 80000.0
```

---

### Step 8: Calculate Average Salary

```java
double averageSalary = employees.stream()
        .mapToDouble(Employee::getSalary)
        .average()
        .orElse(0.0);

System.out.println(averageSalary);
```

### Output

```text
62000.0
```

---

### Complete Mini Project Code

```java
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

class Employee {
    private int id;
    private String name;
    private String department;
    private double salary;

    public Employee(int id, String name, String department, double salary) {
        this.id = id;
        this.name = name;
        this.department = department;
        this.salary = salary;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDepartment() {
        return department;
    }

    public double getSalary() {
        return salary;
    }

    @Override
    public String toString() {
        return id + " - " + name + " - " + department + " - " + salary;
    }
}

public class EmployeeReportProject {
    public static void main(String[] args) {
        List<Employee> employees = Arrays.asList(
                new Employee(1, "Amit", "IT", 70000),
                new Employee(2, "Neha", "HR", 50000),
                new Employee(3, "Raj", "IT", 80000),
                new Employee(4, "Priya", "Finance", 65000),
                new Employee(5, "Karan", "HR", 45000)
        );

        System.out.println("Employees with salary greater than 60000:");
        employees.stream()
                .filter(employee -> employee.getSalary() > 60000)
                .forEach(System.out::println);

        System.out.println("\nEmployee names:");
        List<String> employeeNames = employees.stream()
                .map(Employee::getName)
                .collect(Collectors.toList());
        System.out.println(employeeNames);

        System.out.println("\nEmployees sorted by salary:");
        employees.stream()
                .sorted(Comparator.comparing(Employee::getSalary))
                .forEach(System.out::println);

        System.out.println("\nEmployees grouped by department:");
        Map<String, List<Employee>> employeesByDepartment = employees.stream()
                .collect(Collectors.groupingBy(Employee::getDepartment));
        employeesByDepartment.forEach((department, employeeList) -> {
            System.out.println(department + ":");
            employeeList.forEach(System.out::println);
        });

        System.out.println("\nHighest salary employee:");
        Optional<Employee> highestSalaryEmployee = employees.stream()
                .max(Comparator.comparing(Employee::getSalary));
        highestSalaryEmployee.ifPresent(System.out::println);

        System.out.println("\nAverage salary:");
        double averageSalary = employees.stream()
                .mapToDouble(Employee::getSalary)
                .average()
                .orElse(0.0);
        System.out.println(averageSalary);
    }
}
```

---

## 13. Practice Tasks

Try these tasks yourself.

---

### Task 1: Filter Odd Numbers

Given:

```java
List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9);
```

Use stream to print only odd numbers.

Expected output:

```text
[1, 3, 5, 7, 9]
```

---

### Task 2: Convert Names to Uppercase

Given:

```java
List<String> names = Arrays.asList("amit", "neha", "raj");
```

Expected output:

```text
[AMIT, NEHA, RAJ]
```

---

### Task 3: Count Names Starting With A

Given:

```java
List<String> names = Arrays.asList("Amit", "Neha", "Ankit", "Raj", "Arjun");
```

Expected output:

```text
3
```

---

### Task 4: Find Maximum Number

Given:

```java
List<Integer> numbers = Arrays.asList(10, 25, 5, 40, 30);
```

Expected output:

```text
40
```

Hint:

```java
numbers.stream().max(Integer::compareTo)
```

---

### Task 5: Join Names With Comma

Given:

```java
List<String> names = Arrays.asList("Amit", "Neha", "Raj");
```

Expected output:

```text
Amit, Neha, Raj
```

---

## Quick Revision

### Lambda Syntax

```java
(parameters) -> expression
```

or

```java
(parameters) -> {
    statements;
    return value;
}
```

### Common Stream Pattern

```java
collection.stream()
        .filter(condition)
        .map(transformation)
        .collect(Collectors.toList());
```

### Most Used Java 8 Features

- Lambda expressions
- Functional interfaces
- Method references
- Streams
- Collectors
- Optional
- Default methods in interfaces
- Java Date and Time API

---

## Recommended Learning Order

1. Understand lambdas
2. Practice functional interfaces
3. Learn `Predicate`, `Function`, `Consumer`, and `Supplier`
4. Practice streams with `filter`, `map`, and `collect`
5. Learn sorting, grouping, and reducing
6. Use Optional safely
7. Build small projects using all features together

---

## Final Tip

Do not just read the code.

For every example:

1. Copy it
2. Run it
3. Change values
4. Break it intentionally
5. Fix it
6. Write your own version

That is the fastest way to learn Java 8.
