# MiniKafka Step 1 — Create the Physical Log File

## Goal

Before Kafka can store messages, it needs storage.

In real Kafka, messages are stored inside **partition log files**.

In our MiniKafka, we start with one simple file:

```text
data/phase1/orders-0.log
```

This file represents:

```text
topic     = orders
partition = 0
```

So:

```text
orders-0.log
```

means:

```text
orders topic, partition 0 log file
```

---

# Step 1 Big Picture

We are starting the storage layer.

```text
Producer  -> not yet
Broker    -> not yet
Topic     -> not yet
Partition -> represented by one file
Log       -> creating now
Consumer  -> not yet
```

Current goal:

```text
Create an empty append-only log file.
```

---

# Step 1.1 — Folder Structure

Create this project structure:

```text
MiniKafka/
└── src/
    └── main/
        └── java/
            └── com/
                └── minikafka/
                    └── step1/
                        └── Step1CreateLogFile.java
```

After running the program, Java will create:

```text
MiniKafka/
├── data/
│   └── phase1/
│       └── orders-0.log
└── src/
    └── main/
        └── java/
            └── com/
                └── minikafka/
                    └── step1/
                        └── Step1CreateLogFile.java
```

---

# Step 1.2 — Why Do We Need a Log File?

Kafka stores messages in logs.

A log is not just random text.

A Kafka log is an ordered sequence of records:

```text
offset 0 -> message 1
offset 1 -> message 2
offset 2 -> message 3
offset 3 -> message 4
```

In MiniKafka, this will later look like:

```text
0|order-1|created
1|order-2|paid
2|order-3|shipped
```

But in Step 1, the file is still empty.

```text
orders-0.log

(empty)
```

---

# Step 1.3 — What Are We Building?

We build this:

```text
MiniKafka storage directory

data/
└── phase1/
    └── orders-0.log
```

This is the first physical storage component of MiniKafka.

---

# Step 1.4 — Java Code

File:

```text
src/main/java/com/minikafka/step1/Step1CreateLogFile.java
```

```java
package com.minikafka.step1;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class Step1CreateLogFile {

    public static void main(String[] args) throws IOException {

        // This path represents one Kafka-like partition log file.
        // orders-0.log means:
        // topic = orders
        // partition = 0
        Path logPath = Path.of("data/phase1/orders-0.log");

        // Get parent directory:
        // data/phase1
        Path parentFolder = logPath.getParent();

        // Create data/phase1 folder if it does not exist.
        if (parentFolder != null) {
            Files.createDirectories(parentFolder);
        }

        // Create orders-0.log file if it does not exist.
        if (!Files.exists(logPath)) {
            Files.createFile(logPath);
            System.out.println("Log file created.");
        } else {
            System.out.println("Log file already exists.");
        }

        // Print full absolute path so you can find it in Windows.
        System.out.println("Log file location:");
        System.out.println(logPath.toAbsolutePath());
    }
}
```

---

# Step 1.5 — What Happens in Background?

This line:

```java
Path logPath = Path.of("data/phase1/orders-0.log");
```

does not create the file yet.

It only creates a Java object pointing to this location:

```text
MiniKafka/
└── data/
    └── phase1/
        └── orders-0.log
```

Think of it like:

```text
Path object = address of future file
```

---

# Step 1.6 — Get Parent Folder

```java
Path parentFolder = logPath.getParent();
```

For this path:

```text
data/phase1/orders-0.log
```

the parent folder is:

```text
data/phase1
```

Visual:

```text
data/phase1/orders-0.log
^^^^^^^^^^^
parent folder
```

---

# Step 1.7 — Create Directories

```java
Files.createDirectories(parentFolder);
```

This creates folders if missing:

```text
data/
└── phase1/
```

Important:

```text
createDirectories() is safe.
```

If folder already exists, it will not crash.

---

# Step 1.8 — Create File

```java
if (!Files.exists(logPath)) {
    Files.createFile(logPath);
}
```

This means:

```text
If orders-0.log does not exist, create it.
```

After this line:

```text
data/
└── phase1/
    └── orders-0.log
```

The file is empty:

```text
orders-0.log

(empty)
```

---

# Step 1.9 — Why We Check File Exists First

If we directly call:

```java
Files.createFile(logPath);
```

and file already exists, Java throws an error:

```text
FileAlreadyExistsException
```

So we safely check first:

```java
if (!Files.exists(logPath)) {
    Files.createFile(logPath);
}
```

---

# Step 1.10 — Run Commands

From project root:

```bash
javac -d out src/main/java/com/minikafka/step1/Step1CreateLogFile.java
java -cp out com.minikafka.step1.Step1CreateLogFile
```

Expected output first time:

```text
Log file created.
Log file location:
C:\...\MiniKafka\data\phase1\orders-0.log
```

Expected output second time:

```text
Log file already exists.
Log file location:
C:\...\MiniKafka\data\phase1\orders-0.log
```

---

# Step 1.11 — Windows Location Explanation

If your project is here:

```text
C:\Users\Mohamed\IdeaProjects\MiniKafka
```

then this path:

```java
Path.of("data/phase1/orders-0.log")
```

creates the file here:

```text
C:\Users\Mohamed\IdeaProjects\MiniKafka\data\phase1\orders-0.log
```

Because the path is relative to the project working directory.

---

# Step 1.12 — Visual Execution Flow

```text
Program starts
     |
     v
Create Path object
     |
     v
Find parent folder
     |
     v
Create data/phase1 directory
     |
     v
Check if orders-0.log exists
     |
     +-------------------------+
     |                         |
     v                         v
File missing              File exists
     |                         |
     v                         v
Create file               Reuse file
     |
     v
Print absolute path
     |
     v
Program ends
```

---

# Step 1.13 — What We Learned

```text
Kafka stores messages on disk.
A topic is split into partitions.
Each partition has its own log file.
MiniKafka starts with one partition log file.
```

Our first MiniKafka storage file:

```text
orders-0.log
```

means:

```text
topic orders, partition 0
```

---

# Step 1.14 — What We Have Built So Far

```text
Supported:
[yes] create storage folder
[yes] create partition log file
[yes] find file location in Windows
[yes] safely handle file already exists

Not yet supported:
[no] append messages
[no] offsets
[no] read records
[no] MessageRecord class
[no] Partition class
[no] Topic class
[no] Broker
[no] Producer
[no] Consumer
```

---

# Step 1.15 — Step 1 Completion Checklist

You are done with Step 1 if:

```text
[ ] You created Step1CreateLogFile.java
[ ] You ran the program
[ ] data/phase1 folder was created
[ ] orders-0.log file was created
[ ] You know where the file is located on Windows
[ ] You understand orders-0.log = topic orders, partition 0
```

---

# Step 1.16 — Next Step

Next we build:

```text
Step 2 — Append messages into orders-0.log
```

Step 2 introduces the most important Kafka concept:

```text
append-only log
```

We will write records like:

```text
0|order-1|created
1|order-2|paid
2|order-3|shipped
```

That becomes the foundation for producer and consumer later.
