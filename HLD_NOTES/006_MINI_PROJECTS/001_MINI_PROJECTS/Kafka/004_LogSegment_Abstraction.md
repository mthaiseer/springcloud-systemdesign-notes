# 004_LogSegment_Abstraction

# MiniKafka Step 4 — LogSegment Abstraction

## Goal

Until now, append and read logic are scattered across multiple classes.

Current problem:

```text
append logic -> inside driver
read logic   -> inside driver
offset logic -> duplicated
```

This is not scalable.

In this step, we encapsulate all storage operations into:

```java
LogSegment
```

This becomes the core storage engine component of MiniKafka.

---

# Big Picture

Before:

```text
Driver
   |
   +--> append logic
   |
   +--> read logic
   |
   +--> serialization logic
```

After:

```text
Driver
   |
   v
LogSegment
   |
   +--> append()
   |
   +--> readAll()
   |
   +--> readFromOffset()
   |
   +--> offset generation
```

---

# Step 4.1 — Folder Structure

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
                    └── step4/
                        ├── MessageRecord.java
                        ├── RecordSerializer.java
                        ├── LogSegment.java
                        └── Step4Driver.java
```

---

# Step 4.2 — LogSegment Responsibilities

```text
[yes] append record
[yes] generate offset
[yes] read all records
[yes] read from offset
[yes] manage file path
```

---

# Step 4.3 — LogSegment Class

```java
package com.minikafka.step4;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class LogSegment {

    private final Path logPath;

    public LogSegment(String filePath) throws IOException {

        this.logPath = Path.of(filePath);

        Files.createDirectories(logPath.getParent());

        if (!Files.exists(logPath)) {
            Files.createFile(logPath);
        }
    }

    public long append(String key,
                       String value) throws IOException {

        long offset = countLines();

        MessageRecord record =
                new MessageRecord(offset, key, value);

        String line =
                RecordSerializer.serialize(record);

        Files.writeString(
                logPath,
                line + System.lineSeparator(),
                StandardOpenOption.APPEND
        );

        return offset;
    }

    public List<MessageRecord> readAll()
            throws IOException {

        List<MessageRecord> result =
                new ArrayList<>();

        List<String> lines =
                Files.readAllLines(logPath);

        for (String line : lines) {

            MessageRecord record =
                    RecordSerializer.deserialize(line);

            result.add(record);
        }

        return result;
    }

    public List<MessageRecord> readFromOffset(
            long startOffset)
            throws IOException {

        List<MessageRecord> result =
                new ArrayList<>();

        List<String> lines =
                Files.readAllLines(logPath);

        for (String line : lines) {

            MessageRecord record =
                    RecordSerializer.deserialize(line);

            if (record.getOffset() >= startOffset) {
                result.add(record);
            }
        }

        return result;
    }

    private long countLines()
            throws IOException {

        try (Stream<String> lines =
                     Files.lines(logPath)) {

            return lines.count();
        }
    }
}
```

---

# Step 4.4 — Driver Program

```java
package com.minikafka.step4;

import java.util.List;

public class Step4Driver {

    public static void main(String[] args)
            throws Exception {

        LogSegment segment =
                new LogSegment(
                        "data/phase1/orders-0.log"
                );

        segment.append("order-1", "created");

        segment.append("order-2", "paid");

        segment.append("order-3", "shipped");

        System.out.println("---- READ ALL ----");

        List<MessageRecord> all =
                segment.readAll();

        for (MessageRecord record : all) {
            System.out.println(record);
        }

        System.out.println();

        System.out.println(
                "---- READ FROM OFFSET 1 ----"
        );

        List<MessageRecord> partial =
                segment.readFromOffset(1);

        for (MessageRecord record : partial) {
            System.out.println(record);
        }
    }
}
```

---

# Step 4.5 — Execution Flow

```text
append()
   |
   v
Generate offset
   |
   v
Create MessageRecord
   |
   v
Serialize
   |
   v
Append to file
```

Read flow:

```text
Read lines
   |
   v
Deserialize
   |
   v
Return MessageRecord objects
```

---

# Step 4.6 — Run Command

```bash
javac -d out src/main/java/com/minikafka/step4/*.java

java -cp out com.minikafka.step4.Step4Driver
```

---

# Step 4.7 — Expected Output

```text
---- READ ALL ----

MessageRecord{offset=0, key='order-1', value='created'}
MessageRecord{offset=1, key='order-2', value='paid'}
MessageRecord{offset=2, key='order-3', value='shipped'}

---- READ FROM OFFSET 1 ----

MessageRecord{offset=1, key='order-2', value='paid'}
MessageRecord{offset=2, key='order-3', value='shipped'}
```

---

# Step 4.8 — Concepts Learned

```text
abstraction
encapsulation
storage engine
offset-based reads
reusable components
```

---

# Step 4.9 — Current MiniKafka State

```text
Supported:
[yes] append records
[yes] offsets
[yes] serialization
[yes] deserialization
[yes] LogSegment abstraction
[yes] read by offset

Not yet:
[no] Partition class
[no] Topic class
[no] Broker
[no] Producer API
[no] Consumer API
```

---

# Next Step

```text
005_Read_From_Specific_Offset
```

We will optimize consumer-style incremental reads.
