# MiniThreadPool + MiniFileTransformer Index

# MiniThreadPool Learning Path

## Core Foundations

1. `001_Single_Worker_Thread.md`
   - One worker thread executes tasks

2. `002_Blocking_Task_Queue.md`
   - Blocking queue using wait/notify

3. `003_Fixed_Thread_Pool.md`
   - Multiple worker threads

4. `004_Bounded_Queue_Backpressure.md`
   - Queue capacity + backpressure

5. `005_Rejection_Policies.md`
   - Abort / CallerRuns / Discard

---

## Async Processing

6. `006_Future_Callable_Result.md`
   - Callable + Future result

7. `007_Exception_Handling.md`
   - Worker-safe exception handling

8. `008_Graceful_Shutdown.md`
   - Finish pending tasks safely

9. `009_Shutdown_Now.md`
   - Interrupt workers + drain queue

---

## Advanced Scheduling

10. `010_Scheduled_ThreadPool.md`
    - Delayed + periodic tasks

11. `011_Priority_Task_Queue.md`
    - Priority-based task execution

12. `012_Metrics_And_Monitoring.md`
    - Metrics + observability

13. `013_Production_ThreadPool.md`
    - Final production-style thread pool

---

# Practical Project Using All Concepts

## MiniFileTransformer

`MiniFileTransformer_Project_Guide.md`

Large file processing pipeline:

```text
read large file
transform each line
process using worker pool
write output file
handle errors
metrics
backpressure
graceful shutdown
```

---

# Recommended Learning Order

```text
MiniThreadPool
    ↓
MiniRateLimiter
    ↓
MiniKafka
    ↓
MiniRedis
    ↓
MiniFileTransformer
    ↓
MiniScheduler
    ↓
MiniGateway
```

---

# Key Concepts Learned

## Concurrency

```text
threads
worker pools
blocking queue
producer consumer
wait notify
thread lifecycle
```

## Scalability

```text
backpressure
bounded queue
rejection policy
priority scheduling
graceful shutdown
```

## Production Engineering

```text
metrics
observability
failure handling
resource management
clean architecture
```

## DSA / CP Concepts

```text
queue
priority queue
heap
event simulation
state machine
running metrics
stable ordering
```

---

# Real World Systems Using These Ideas

```text
Kafka
RabbitMQ
Spring ExecutorService
Video Processing Systems
Payment Systems
Notification Systems
ETL Pipelines
Log Processing Pipelines
Search Indexing Systems
```
