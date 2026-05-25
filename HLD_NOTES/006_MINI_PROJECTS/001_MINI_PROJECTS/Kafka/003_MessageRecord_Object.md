# 003_MessageRecord_Object

# MiniKafka Step 3 — MessageRecord Object

## Goal

Until now, we stored records as raw strings:

```text
0|order-1|created
```

This works, but it is difficult to manage in a real system.

In this step, we convert raw strings into proper Java objects.

This is where MiniKafka starts becoming a real system.

---

# Big Picture

Current architecture:

```text
Raw String
    |
    v
Log File
```

After this step:

```text
MessageRecord Object
        |
        v
Serialization
        |
        v
Log File
```

---

# Why Message Objects Are Important

Without objects:

```java
String line = "0|order-1|created";
```

Problems:

```text
- Hard to manage
- Hard to extend
- Hard to validate
- Error-prone parsing
```

With objects:

```java
MessageRecord record =
    new MessageRecord(0, "order-1", "created");
```

Benefits:

```text
- Clean abstraction
- Easy serialization
- Easy deserialization
- Easier broker/producer APIs
- Strong typing
```

---

# Step 3.1 — Folder Structure

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
                    └── step3/
                        ├── MessageRecord.java
                        ├── RecordSerializer.java
                        └── Step3Driver.java
```

---

# Step 3.2 — MessageRecord Class

File:

```text
MessageRecord.java
```

Code:

```java
package com.minikafka.step3;

public class MessageRecord {

    private long offset;

    private String key;

    private String value;

    public MessageRecord(long offset,
                         String key,
                         String value) {

        this.offset = offset;
        this.key = key;
        this.value = value;
    }

    public long getOffset() {
        return offset;
    }

    public String getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return "MessageRecord{" +
                "offset=" + offset +
                ", key='" + key + '\'' +
                ", value='" + value + '\'' +
                '}';
    }
}
```

---

# Step 3.3 — What Happens Internally?

This object:

```java
new MessageRecord(0, "order-1", "created");
```

creates memory structure:

```text
MessageRecord
   |
   +--> offset = 0
   |
   +--> key = "order-1"
   |
   +--> value = "created"
```

Before:

```text
Everything was packed into one string
```

Now:

```text
Data is structured
```

This is a major architecture improvement.

---

# Step 3.4 — Why We Need Serialization

Objects cannot directly be stored in a text file.

We must convert:

```text
Object -> String
```

This process is called:

```text
Serialization
```

And reverse process:

```text
String -> Object
```

is called:

```text
Deserialization
```

---

# Step 3.5 — RecordSerializer Class

File:

```text
RecordSerializer.java
```

Code:

```java
package com.minikafka.step3;

public class RecordSerializer {

    // Convert object into log line
    public static String serialize(MessageRecord record) {

        return record.getOffset()
                + "|"
                + record.getKey()
                + "|"
                + record.getValue();
    }

    // Convert log line into object
    public static MessageRecord deserialize(String line) {

        String[] parts = line.split("\\|", 3);

        long offset = Long.parseLong(parts[0]);

        String key = parts[1];

        String value = parts[2];

        return new MessageRecord(offset, key, value);
    }
}
```

---

# Step 3.6 — Understanding split("\\|", 3)

Input:

```text
0|order-1|created
```

This line:

```java
String[] parts = line.split("\\|", 3);
```

produces:

```text
parts[0] = "0"
parts[1] = "order-1"
parts[2] = "created"
```

Visual:

```text
0|order-1|created
 ^
 split here

0|order-1|created
         ^
 split here
```

Result:

```text
["0", "order-1", "created"]
```

---

# Why "\\|" ?

Pipe symbol:

```text
|
```

has special meaning in regex.

So Java requires escaping:

```java
"\\|"
```

meaning:

```text
split by actual pipe character
```

---

# Step 3.7 — Driver Program

File:

```text
Step3Driver.java
```

Code:

```java
package com.minikafka.step3;

public class Step3Driver {

    public static void main(String[] args) {

        MessageRecord record =
                new MessageRecord(
                        0,
                        "order-1",
                        "created"
                );

        System.out.println("Original Object:");
        System.out.println(record);

        String line =
                RecordSerializer.serialize(record);

        System.out.println();
        System.out.println("Serialized Line:");
        System.out.println(line);

        MessageRecord parsed =
                RecordSerializer.deserialize(line);

        System.out.println();
        System.out.println("Deserialized Object:");
        System.out.println(parsed);
    }
}
```

---

# Step 3.8 — Execution Flow

```text
Create MessageRecord object
           |
           v
Serialize object into log line
           |
           v
Store line in file
           |
           v
Read line from file
           |
           v
Deserialize line back into object
```

---

# Step 3.9 — Run Command

```bash
javac -d out src/main/java/com/minikafka/step3/*.java

java -cp out com.minikafka.step3.Step3Driver
```

---

# Step 3.10 — Expected Output

```text
Original Object:
MessageRecord{offset=0, key='order-1', value='created'}

Serialized Line:
0|order-1|created

Deserialized Object:
MessageRecord{offset=0, key='order-1', value='created'}
```

---

# Step 3.11 — Why This Step Is Important

This is the first true abstraction layer.

Before:

```text
Everything was raw strings
```

Now:

```text
Broker works with objects
Storage works with strings
Serializer converts between them
```

This separation is used everywhere in distributed systems.

---

# Step 3.12 — Current MiniKafka Architecture

```text
MessageRecord
      |
      v
RecordSerializer
      |
      v
Log File
```

Next:

```text
MessageRecord
      |
      v
LogSegment
      |
      v
Partition
      |
      v
Topic
      |
      v
Broker
```

---

# Step 3.13 — Real Kafka Similarity

Real Kafka internally also uses:

```text
Record objects
serialization
binary encoding
deserialization
```

We are currently building simplified text-based Kafka storage.

Later we can move to:

```text
binary storage
byte buffers
memory-mapped files
```

---

# Step 3.14 — Concepts Learned

```text
object modeling
serialization
deserialization
structured records
data abstraction
```

---

# Step 3.15 — Current MiniKafka State

```text
Supported:
[yes] create log file
[yes] append records
[yes] offsets
[yes] MessageRecord objects
[yes] serialization
[yes] deserialization

Not yet:
[no] LogSegment abstraction
[no] read by offset
[no] Partition class
[no] Topic class
[no] Broker
[no] Producer API
[no] Consumer API
```

---

# Step 3 Completion Checklist

```text
[ ] You created MessageRecord class
[ ] You understand serialization
[ ] You understand deserialization
[ ] You can convert object -> string
[ ] You can convert string -> object
[ ] You understand why abstraction matters
```

---

# Step 3 Final Mental Model

```text
Producer creates object
         |
         v
Serialize object
         |
         v
Store in log file
         |
         v
Read line from log
         |
         v
Deserialize back into object
         |
         v
Consumer receives object
```

---

# Next Step

Next we build:

```text
004_LogSegment_Abstraction
```

Currently append/read logic is scattered.

Next we encapsulate everything into:

```java
LogSegment
```

This becomes the core storage engine component of MiniKafka.
