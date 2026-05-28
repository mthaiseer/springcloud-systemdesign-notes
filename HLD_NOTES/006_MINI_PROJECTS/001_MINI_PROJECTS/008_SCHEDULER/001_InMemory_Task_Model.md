# 001_InMemory_Task_Model.md

# MiniScheduler Phase 001 — In-Memory Task Model

## Clickable Index

- [1. Goal](#1-goal)
- [2. Why This Phase Matters](#2-why-this-phase-matters)
- [3. What We Build](#3-what-we-build)
- [4. Current Architecture](#4-current-architecture)
- [5. Scheduler Mental Model](#5-scheduler-mental-model)
- [6. Folder Structure](#6-folder-structure)
- [7. Step-by-Step Flow](#7-step-by-step-flow)
- [8. Complete Java Code](#8-complete-java-code)
  - [8.1 TaskStatus.java](#81-taskstatusjava)
  - [8.2 TaskType.java](#82-tasktypejava)
  - [8.3 ScheduledTask.java](#83-scheduledtaskjava)
  - [8.4 TaskRepository.java](#84-taskrepositoryjava)
  - [8.5 TaskService.java](#85-taskservicejava)
  - [8.6 Phase001InMemoryTaskModelDriver.java](#86-phase001inmemorytaskmodeldriverjava)
- [9. How To Run](#9-how-to-run)
- [10. Dry Run](#10-dry-run)
- [11. DSA / CP Concepts Used](#11-dsa--cp-concepts-used)
- [12. System Design Relevance](#12-system-design-relevance)
- [13. Scheduler Connection With This Phase](#13-scheduler-connection-with-this-phase)
- [14. Production-Grade Concepts](#14-production-grade-concepts)
- [15. Scalability Discussion](#15-scalability-discussion)
- [16. Interview Notes](#16-interview-notes)
- [17. Common Bugs](#17-common-bugs)
- [18. Current Limitations](#18-current-limitations)
- [19. Next Step](#19-next-step)

---

# 1. Goal

In this phase, we build the first foundation of **MiniScheduler**:

```text
In-Memory Task Model
```

A scheduler is built around a simple idea:

```text
Task
  ->
scheduled time
  ->
execute later
```

Before we can build:

```text
delayed jobs
cron jobs
retry
backoff
DLQ
worker pool
persistent job store
distributed scheduler
leader election
DAG workflows
```

we need a strong task model.

This phase creates:

```text
ScheduledTask
TaskStatus
TaskType
TaskRepository
TaskService
```

No execution yet.

No priority queue yet.

No worker pool yet.

This phase is only about modeling tasks correctly.

---

# 2. Why This Phase Matters

Every scheduler starts with a task record.

Examples:

```text
Send email tomorrow at 9 AM
Retry failed payment after 5 minutes
Generate monthly report at midnight
Clean expired sessions every hour
Run ETL pipeline every night
Send notification after order is shipped
```

All of these are tasks.

A task usually needs:

```text
id
name
type
payload
scheduled time
status
retry count
created time
updated time
```

If the task model is weak, later systems become messy.

This phase gives a clean foundation.

---

# 3. What We Build

We build an in-memory scheduler task model.

Supported behavior:

```text
create task
store task
find task by id
list all tasks
update task status
print task repository
```

Example task:

```text
name = Send welcome email
type = EMAIL
payload = userId=123
scheduledAt = now + 60 seconds
status = PENDING
```

Expected result:

```text
Task stored in memory
```

---

# 4. Current Architecture

```text
+----------------------+
| Driver / Client      |
+----------+-----------+
           |
           | create task
           v
+----------------------+
| TaskService          |
| validation + create  |
+----------+-----------+
           |
           | save task
           v
+----------------------+
| TaskRepository       |
| in-memory HashMap    |
+----------+-----------+
           |
           v
+----------------------+
| ScheduledTask        |
| task data model      |
+----------------------+
```

## Request Flow

```text
1. Driver asks TaskService to create task
2. TaskService validates input
3. TaskService creates ScheduledTask
4. TaskRepository saves task by ID
5. Driver lists all tasks
```

---

# 5. Scheduler Mental Model

A scheduler is not only a loop.

It is a full lifecycle system.

Basic lifecycle:

```text
PENDING
   |
   v
RUNNING
   |
   +----> SUCCESS
   |
   +----> FAILED
              |
              v
            RETRY
              |
              v
            DEAD_LETTER
```

In this phase, we only model status.

Later phases execute transitions.

Current phase:

```text
Task exists
Task has status
Task has scheduled time
Task is stored in memory
```

Future phases:

```text
PriorityQueue checks due tasks
WorkerPool runs tasks
RetryPolicy handles failures
PersistentStore survives restart
DistributedLock prevents double execution
```

---

# 6. Folder Structure

Create this structure:

```text
MiniScheduler/
└── src/
    └── main/
        └── java/
            └── com/
                └── minischeduler/
                    ├── model/
                    │   ├── TaskStatus.java
                    │   ├── TaskType.java
                    │   └── ScheduledTask.java
                    ├── repository/
                    │   └── TaskRepository.java
                    ├── service/
                    │   └── TaskService.java
                    └── driver/
                        └── Phase001InMemoryTaskModelDriver.java
```

Recommended package style:

```text
com.minischeduler.model
com.minischeduler.repository
com.minischeduler.service
com.minischeduler.driver
```

---

# 7. Step-by-Step Flow

## Step 1 — Define TaskStatus

Task can be:

```text
PENDING
RUNNING
SUCCESS
FAILED
CANCELLED
DEAD_LETTER
```

---

## Step 2 — Define TaskType

Task can be:

```text
EMAIL
PAYMENT_RETRY
REPORT_GENERATION
CLEANUP
WEBHOOK
CUSTOM
```

---

## Step 3 — Create ScheduledTask

Task stores:

```text
id
name
type
payload
scheduledAt
status
retryCount
createdAt
updatedAt
```

---

## Step 4 — Store Task In Repository

Repository uses:

```text
Map<String, ScheduledTask>
```

---

## Step 5 — Service Creates Task

TaskService validates and saves task.

---

## Step 6 — Driver Tests It

Driver creates tasks and prints repository.

---

# 8. Complete Java Code

---

## 8.1 TaskStatus.java

### Logic before this class

This enum represents task lifecycle state.

A scheduler needs status because a task changes over time.

Example lifecycle:

```text
PENDING -> RUNNING -> SUCCESS
PENDING -> RUNNING -> FAILED -> RETRY
PENDING -> CANCELLED
FAILED -> DEAD_LETTER
```

In this phase, we only store status.

Later phases change status automatically during execution.

```java
package com.minischeduler.model;

public enum TaskStatus {

    PENDING,

    RUNNING,

    SUCCESS,

    FAILED,

    CANCELLED,

    DEAD_LETTER
}
```

---

## 8.2 TaskType.java

### Logic before this class

This enum describes what kind of job the scheduler needs to run.

Examples:

```text
EMAIL
PAYMENT_RETRY
REPORT_GENERATION
CLEANUP
WEBHOOK
CUSTOM
```

Why this matters:

```text
Different task types may need different executors.
```

Example:

```text
EMAIL -> EmailTaskExecutor
PAYMENT_RETRY -> PaymentRetryExecutor
REPORT_GENERATION -> ReportExecutor
```

Later phases use this to route tasks to executors.

```java
package com.minischeduler.model;

public enum TaskType {

    EMAIL,

    PAYMENT_RETRY,

    REPORT_GENERATION,

    CLEANUP,

    WEBHOOK,

    CUSTOM
}
```

---

## 8.3 ScheduledTask.java

### Logic before this class

This is the core task model.

A task needs:

```text
id
name
type
payload
scheduledAt
status
retryCount
createdAt
updatedAt
```

Important fields:

```text
scheduledAt
```

means when task should run.

```text
status
```

means current lifecycle state.

```text
retryCount
```

is used later when failures happen.

`payload` is a string for now.

Later it can become:

```text
JSON payload
Map<String, Object>
serialized object
database reference
S3 file reference
```

```java
package com.minischeduler.model;

import java.time.Instant;
import java.util.UUID;

public class ScheduledTask {

    private final String id;
    private final String name;
    private final TaskType type;
    private final String payload;
    private final Instant scheduledAt;
    private TaskStatus status;
    private int retryCount;
    private final Instant createdAt;
    private Instant updatedAt;

    public ScheduledTask(
            String name,
            TaskType type,
            String payload,
            Instant scheduledAt
    ) {
        this.id = UUID.randomUUID().toString();
        this.name = validateName(name);
        this.type = validateType(type);
        this.payload = payload;
        this.scheduledAt = validateScheduledAt(scheduledAt);
        this.status = TaskStatus.PENDING;
        this.retryCount = 0;
        this.createdAt = Instant.now();
        this.updatedAt = this.createdAt;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public TaskType getType() {
        return type;
    }

    public String getPayload() {
        return payload;
    }

    public Instant getScheduledAt() {
        return scheduledAt;
    }

    public TaskStatus getStatus() {
        return status;
    }

    public int getRetryCount() {
        return retryCount;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void markRunning() {
        this.status = TaskStatus.RUNNING;
        touch();
    }

    public void markSuccess() {
        this.status = TaskStatus.SUCCESS;
        touch();
    }

    public void markFailed() {
        this.status = TaskStatus.FAILED;
        this.retryCount++;
        touch();
    }

    public void cancel() {
        this.status = TaskStatus.CANCELLED;
        touch();
    }

    public void moveToDeadLetter() {
        this.status = TaskStatus.DEAD_LETTER;
        touch();
    }

    private void touch() {
        this.updatedAt = Instant.now();
    }

    private String validateName(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Task name cannot be empty");
        }

        return value.trim();
    }

    private TaskType validateType(TaskType value) {
        if (value == null) {
            throw new IllegalArgumentException("Task type cannot be null");
        }

        return value;
    }

    private Instant validateScheduledAt(Instant value) {
        if (value == null) {
            throw new IllegalArgumentException("scheduledAt cannot be null");
        }

        return value;
    }

    @Override
    public String toString() {
        return "ScheduledTask{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", type=" + type +
                ", payload='" + payload + '\'' +
                ", scheduledAt=" + scheduledAt +
                ", status=" + status +
                ", retryCount=" + retryCount +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}
```

---

## 8.4 TaskRepository.java

### Logic before this class

This is the in-memory task database.

For now, we use:

```text
Map<String, ScheduledTask>
```

where:

```text
taskId -> ScheduledTask
```

Operations:

```text
save
findById
findAll
delete
count
```

Later this becomes:

```text
PostgreSQL task table
Redis delay queue
persistent task store
distributed shard storage
```

```java
package com.minischeduler.repository;

import com.minischeduler.model.ScheduledTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class TaskRepository {

    private final Map<String, ScheduledTask> tasksById = new HashMap<>();

    public void save(ScheduledTask task) {
        tasksById.put(task.getId(), task);
    }

    public Optional<ScheduledTask> findById(String taskId) {
        return Optional.ofNullable(tasksById.get(taskId));
    }

    public List<ScheduledTask> findAll() {
        return new ArrayList<>(tasksById.values());
    }

    public boolean deleteById(String taskId) {
        return tasksById.remove(taskId) != null;
    }

    public int count() {
        return tasksById.size();
    }
}
```

---

## 8.5 TaskService.java

### Logic before this class

This is the business layer.

The service controls task creation and status updates.

Why not create tasks directly in repository?

Because service layer handles rules:

```text
validate input
create task
save task
update task lifecycle
```

Later, TaskService will coordinate:

```text
retry policy
timeout
cancellation
misfire handling
persistence
metrics
```

```java
package com.minischeduler.service;

import com.minischeduler.model.ScheduledTask;
import com.minischeduler.model.TaskType;
import com.minischeduler.repository.TaskRepository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public class TaskService {

    private final TaskRepository repository;

    public TaskService(TaskRepository repository) {
        this.repository = repository;
    }

    public ScheduledTask createTask(
            String name,
            TaskType type,
            String payload,
            Instant scheduledAt
    ) {
        ScheduledTask task =
                new ScheduledTask(
                        name,
                        type,
                        payload,
                        scheduledAt
                );

        repository.save(task);

        return task;
    }

    public Optional<ScheduledTask> findTask(String taskId) {
        return repository.findById(taskId);
    }

    public List<ScheduledTask> listTasks() {
        return repository.findAll();
    }

    public boolean cancelTask(String taskId) {
        Optional<ScheduledTask> optionalTask =
                repository.findById(taskId);

        if (optionalTask.isEmpty()) {
            return false;
        }

        optionalTask.get().cancel();
        return true;
    }

    public int countTasks() {
        return repository.count();
    }
}
```

---

## 8.6 Phase001InMemoryTaskModelDriver.java

### Logic before this class

This driver proves the task model works.

It creates several tasks:

```text
email task
payment retry task
report generation task
```

Then it lists all stored tasks.

It also demonstrates status change:

```text
PENDING -> CANCELLED
```

```java
package com.minischeduler.driver;

import com.minischeduler.model.ScheduledTask;
import com.minischeduler.model.TaskType;
import com.minischeduler.repository.TaskRepository;
import com.minischeduler.service.TaskService;

import java.time.Instant;

public class Phase001InMemoryTaskModelDriver {

    public static void main(String[] args) {

        TaskRepository repository =
                new TaskRepository();

        TaskService taskService =
                new TaskService(repository);

        ScheduledTask emailTask =
                taskService.createTask(
                        "Send welcome email",
                        TaskType.EMAIL,
                        "userId=101,email=mohamed@example.com",
                        Instant.now().plusSeconds(60)
                );

        ScheduledTask paymentRetryTask =
                taskService.createTask(
                        "Retry failed payment",
                        TaskType.PAYMENT_RETRY,
                        "paymentId=pay_123",
                        Instant.now().plusSeconds(300)
                );

        ScheduledTask reportTask =
                taskService.createTask(
                        "Generate daily report",
                        TaskType.REPORT_GENERATION,
                        "reportDate=2026-05-28",
                        Instant.now().plusSeconds(600)
                );

        taskService.cancelTask(paymentRetryTask.getId());

        System.out.println("Total tasks: " + taskService.countTasks());

        System.out.println();
        System.out.println("All tasks:");

        for (ScheduledTask task : taskService.listTasks()) {
            System.out.println(task);
        }

        System.out.println();
        System.out.println("Find one task by ID:");

        taskService
                .findTask(emailTask.getId())
                .ifPresent(System.out::println);
    }
}
```

---

# 9. How To Run

## IntelliJ

1. Create Java project:

```text
MiniScheduler
```

2. Create packages:

```text
com.minischeduler.model
com.minischeduler.repository
com.minischeduler.service
com.minischeduler.driver
```

3. Add all Java files.

4. Run:

```text
Phase001InMemoryTaskModelDriver
```

---

## Command Line

Compile:

```bash
javac -d out src/main/java/com/minischeduler/model/TaskStatus.java \
             src/main/java/com/minischeduler/model/TaskType.java \
             src/main/java/com/minischeduler/model/ScheduledTask.java \
             src/main/java/com/minischeduler/repository/TaskRepository.java \
             src/main/java/com/minischeduler/service/TaskService.java \
             src/main/java/com/minischeduler/driver/Phase001InMemoryTaskModelDriver.java
```

Run:

```bash
java -cp out com.minischeduler.driver.Phase001InMemoryTaskModelDriver
```

---

# 10. Dry Run

## Step 1 — Create Email Task

Input:

```text
name = Send welcome email
type = EMAIL
payload = userId=101,email=mohamed@example.com
scheduledAt = now + 60 seconds
```

Task created:

```text
id = UUID
status = PENDING
retryCount = 0
createdAt = now
updatedAt = now
```

---

## Step 2 — Save Task

Repository state:

```text
tasksById
└── task-id-1 -> ScheduledTask(...)
```

---

## Step 3 — Create Payment Retry Task

Input:

```text
name = Retry failed payment
type = PAYMENT_RETRY
payload = paymentId=pay_123
scheduledAt = now + 300 seconds
```

Repository:

```text
task-id-1 -> EMAIL task
task-id-2 -> PAYMENT_RETRY task
```

---

## Step 4 — Cancel Payment Task

Flow:

```text
taskService.cancelTask(task-id-2)
    ->
repository.findById(task-id-2)
    ->
task.cancel()
    ->
status = CANCELLED
```

---

## Step 5 — List Tasks

Output:

```text
EMAIL task        -> PENDING
PAYMENT task      -> CANCELLED
REPORT task       -> PENDING
```

Visual:

```text
Driver
  |
  v
TaskService
  |
  v
TaskRepository
  |
  v
Map<taskId, ScheduledTask>
```

---

# 11. DSA / CP Concepts Used

| Concept | Usage |
|---|---|
| HashMap | taskId -> ScheduledTask |
| List | return all tasks |
| Enum | status and type modeling |
| UUID | unique task ID |
| State machine | task status lifecycle |
| Optional | safe lookup |

## Complexity

| Operation | Complexity |
|---|---|
| Save task | O(1) average |
| Find by ID | O(1) average |
| Delete by ID | O(1) average |
| List all tasks | O(N) |

This phase is simple, but it is the foundation of every scheduler.

---

# 12. System Design Relevance

This phase maps to real systems like:

```text
Quartz Scheduler
Airflow
Temporal
Cadence
AWS EventBridge Scheduler
Kubernetes CronJob
Celery beat
Sidekiq scheduled jobs
```

Common real use cases:

```text
send email later
retry payment
generate report
cleanup expired sessions
run batch jobs
send notification
schedule webhook retry
```

In HLD, scheduler is often drawn as:

```text
Task DB
   ->
Scheduler Poller
   ->
Worker Pool
   ->
Task Executor
```

This phase builds the task data model for that architecture.

---

# 13. Scheduler Connection With This Phase

Real scheduler task table usually has:

```text
id
task_type
payload
scheduled_at
status
retry_count
max_retries
created_at
updated_at
locked_by
locked_until
last_error
```

Our current phase has:

```text
id
name
type
payload
scheduledAt
status
retryCount
createdAt
updatedAt
```

Difference:

| Real Scheduler | Current Phase |
|---|---|
| Persistent database | In-memory HashMap |
| Worker execution | Not yet |
| Retry policy | Not yet |
| Distributed lock | Not yet |
| Cron support | Not yet |
| Metrics | Not yet |
| DAG dependencies | Not yet |

---

# 14. Production-Grade Concepts

Production scheduler must consider:

```text
task persistence
task locking
exactly-once vs at-least-once execution
idempotency
retry policy
timeout
worker failure
node crash
misfire handling
clock drift
backpressure
metrics
DLQ
```

The task model should support future fields:

```text
maxRetries
nextRunAt
lastError
lockedBy
lockedUntil
priority
cronExpression
parentTaskId
workflowId
```

---

# 15. Scalability Discussion

Current phase:

```text
single JVM
in-memory HashMap
no execution
```

Good for learning.

Not production-safe.

Production scaling path:

```text
Phase 001: task model
Phase 002: one-time task scheduler
Phase 004: priority queue timer
Phase 011: worker thread pool
Phase 014: persistent task store
Phase 016: distributed lock
Phase 018: distributed scheduler node
Phase 019: task sharding
Phase 031: production architecture
```

Bottlenecks later:

```text
huge task table
many due tasks at same time
worker pool saturation
database locking contention
retry storm
clock skew
hot shard
```

---

# 16. Interview Notes

## Q1. What is the core model of a scheduler?

A scheduler stores tasks with:

```text
task payload
scheduled time
status
retry count
```

Then it finds due tasks and executes them.

---

## Q2. Why not execute task immediately?

Because many jobs need delayed or planned execution:

```text
retry later
send email later
generate report at midnight
run cleanup every hour
```

---

## Q3. Why does task need status?

Because scheduler needs lifecycle tracking:

```text
PENDING
RUNNING
SUCCESS
FAILED
CANCELLED
DEAD_LETTER
```

Without status, we cannot know what happened.

---

## Q4. What happens if scheduler crashes?

In this phase, all tasks are lost.

Production fix:

```text
persistent task store
recovery after restart
distributed locking
idempotent execution
```

---

## Q5. Is scheduler exactly-once?

Usually no.

Most schedulers provide:

```text
at-least-once execution
```

So task handlers must be idempotent.

---

# 17. Common Bugs

## Bug 1 — Missing task status

Problem:

```text
Task exists but we do not know if it ran
```

Fix:

```text
add task lifecycle status
```

---

## Bug 2 — No unique task ID

Problem:

```text
cannot track or cancel task
```

Fix:

```text
UUID per task
```

---

## Bug 3 — Payload too tightly coupled

Problem:

```text
hardcoded Java object only
```

Fix:

```text
use JSON/string payload or external reference
```

---

## Bug 4 — In-memory task loss

Problem:

```text
JVM restart loses tasks
```

Fix:

```text
persistent task store
```

---

## Bug 5 — No validation

Problem:

```text
blank task name
null scheduled time
null task type
```

Fix:

```text
validate in ScheduledTask constructor
```

---

# 18. Current Limitations

Current phase supports:

```text
task creation
task storage
task lookup
task listing
task cancellation
```

It does not support yet:

```text
delayed execution
priority queue
cron
worker pool
retry
backoff
DLQ
persistent storage
recovery
distributed execution
metrics
```

This is expected.

We add those one by one.

---

# 19. Next Step

Next file:

```text
002_One_Time_Task_Scheduler.md
```

In the next phase, we move from:

```text
store task only
```

to:

```text
execute one-time task when scheduled time arrives
```

Architecture becomes:

```text
TaskRepository
  ->
SchedulerLoop
  ->
Due Task
  ->
Executor
```

This is where MiniScheduler starts actually running jobs.
