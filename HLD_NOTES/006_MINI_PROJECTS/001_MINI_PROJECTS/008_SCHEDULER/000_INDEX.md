# MiniScheduler — 000_INDEX.md

## Clickable Tree Index

```text
MiniScheduler/
├── 000_INDEX.md
├── 001_InMemory_Task_Model.md
├── 002_One_Time_Task_Scheduler.md
├── 003_Delayed_Task_Execution.md
├── 004_PriorityQueue_Timer.md
├── 005_Recurring_Task_Cron_Basic.md
├── 006_Cron_Expression_Parser.md
├── 007_Task_Status_Model.md
├── 008_Task_Retry_Policy.md
├── 009_Exponential_Backoff.md
├── 010_Dead_Letter_Task_Queue.md
├── 011_Worker_ThreadPool_Execution.md
├── 012_Task_Timeout_Handling.md
├── 013_Task_Cancellation.md
├── 014_Persistent_Task_Store.md
├── 015_Recover_Tasks_After_Restart.md
├── 016_Distributed_Lock_For_Task.md
├── 017_Leader_Election_For_Scheduler.md
├── 018_Distributed_Scheduler_Node.md
├── 019_Task_Sharding.md
├── 020_Misfire_Handling.md
├── 021_Idempotent_Task_Execution.md
├── 022_Dependency_Based_Tasks.md
├── 023_DAG_Task_Scheduler.md
├── 024_Rate_Limited_Task_Execution.md
├── 025_Scheduled_Email_Job.md
├── 026_Payment_Retry_Scheduler.md
├── 027_Report_Generation_Scheduler.md
├── 028_Kafka_Event_Triggered_Scheduler.md
├── 029_Observability_Metrics.md
├── 030_Load_Testing_With_k6.md
└── 031_Production_MiniScheduler.md
```

## Phase Links

- [000_INDEX.md](./000_INDEX.md) — MiniScheduler Master Index
- [001_InMemory_Task_Model.md](./001_InMemory_Task_Model.md) — In Memory Task Model
- [002_One_Time_Task_Scheduler.md](./002_One_Time_Task_Scheduler.md) — One Time Task Scheduler
- [003_Delayed_Task_Execution.md](./003_Delayed_Task_Execution.md) — Delayed Task Execution
- [004_PriorityQueue_Timer.md](./004_PriorityQueue_Timer.md) — PriorityQueue Timer
- [005_Recurring_Task_Cron_Basic.md](./005_Recurring_Task_Cron_Basic.md) — Recurring Task Cron Basic
- [006_Cron_Expression_Parser.md](./006_Cron_Expression_Parser.md) — Cron Expression Parser
- [007_Task_Status_Model.md](./007_Task_Status_Model.md) — Task Status Model
- [008_Task_Retry_Policy.md](./008_Task_Retry_Policy.md) — Task Retry Policy
- [009_Exponential_Backoff.md](./009_Exponential_Backoff.md) — Exponential Backoff
- [010_Dead_Letter_Task_Queue.md](./010_Dead_Letter_Task_Queue.md) — Dead Letter Task Queue
- [011_Worker_ThreadPool_Execution.md](./011_Worker_ThreadPool_Execution.md) — Worker ThreadPool Execution
- [012_Task_Timeout_Handling.md](./012_Task_Timeout_Handling.md) — Task Timeout Handling
- [013_Task_Cancellation.md](./013_Task_Cancellation.md) — Task Cancellation
- [014_Persistent_Task_Store.md](./014_Persistent_Task_Store.md) — Persistent Task Store
- [015_Recover_Tasks_After_Restart.md](./015_Recover_Tasks_After_Restart.md) — Recover Tasks After Restart
- [016_Distributed_Lock_For_Task.md](./016_Distributed_Lock_For_Task.md) — Distributed Lock For Task
- [017_Leader_Election_For_Scheduler.md](./017_Leader_Election_For_Scheduler.md) — Leader Election For Scheduler
- [018_Distributed_Scheduler_Node.md](./018_Distributed_Scheduler_Node.md) — Distributed Scheduler Node
- [019_Task_Sharding.md](./019_Task_Sharding.md) — Task Sharding
- [020_Misfire_Handling.md](./020_Misfire_Handling.md) — Misfire Handling
- [021_Idempotent_Task_Execution.md](./021_Idempotent_Task_Execution.md) — Idempotent Task Execution
- [022_Dependency_Based_Tasks.md](./022_Dependency_Based_Tasks.md) — Dependency Based Tasks
- [023_DAG_Task_Scheduler.md](./023_DAG_Task_Scheduler.md) — DAG Task Scheduler
- [024_Rate_Limited_Task_Execution.md](./024_Rate_Limited_Task_Execution.md) — Rate Limited Task Execution
- [025_Scheduled_Email_Job.md](./025_Scheduled_Email_Job.md) — Scheduled Email Job
- [026_Payment_Retry_Scheduler.md](./026_Payment_Retry_Scheduler.md) — Payment Retry Scheduler
- [027_Report_Generation_Scheduler.md](./027_Report_Generation_Scheduler.md) — Report Generation Scheduler
- [028_Kafka_Event_Triggered_Scheduler.md](./028_Kafka_Event_Triggered_Scheduler.md) — Kafka Event Triggered Scheduler
- [029_Observability_Metrics.md](./029_Observability_Metrics.md) — Observability Metrics
- [030_Load_Testing_With_k6.md](./030_Load_Testing_With_k6.md) — Load Testing With k6
- [031_Production_MiniScheduler.md](./031_Production_MiniScheduler.md) — Production MiniScheduler

---

## Phase Grouping

```text
Phase 1: Task Model And Basic Scheduling
├── 001_InMemory_Task_Model.md
├── 002_One_Time_Task_Scheduler.md
├── 003_Delayed_Task_Execution.md
└── 004_PriorityQueue_Timer.md

Phase 2: Recurring Jobs
├── 005_Recurring_Task_Cron_Basic.md
└── 006_Cron_Expression_Parser.md

Phase 3: Reliability
├── 007_Task_Status_Model.md
├── 008_Task_Retry_Policy.md
├── 009_Exponential_Backoff.md
├── 010_Dead_Letter_Task_Queue.md
├── 011_Worker_ThreadPool_Execution.md
├── 012_Task_Timeout_Handling.md
└── 013_Task_Cancellation.md

Phase 4: Persistence And Recovery
├── 014_Persistent_Task_Store.md
└── 015_Recover_Tasks_After_Restart.md

Phase 5: Distributed Scheduler
├── 016_Distributed_Lock_For_Task.md
├── 017_Leader_Election_For_Scheduler.md
├── 018_Distributed_Scheduler_Node.md
├── 019_Task_Sharding.md
└── 020_Misfire_Handling.md

Phase 6: Workflow Concepts
├── 021_Idempotent_Task_Execution.md
├── 022_Dependency_Based_Tasks.md
└── 023_DAG_Task_Scheduler.md

Phase 7: Real-World Jobs
├── 024_Rate_Limited_Task_Execution.md
├── 025_Scheduled_Email_Job.md
├── 026_Payment_Retry_Scheduler.md
├── 027_Report_Generation_Scheduler.md
└── 028_Kafka_Event_Triggered_Scheduler.md

Phase 8: Production
├── 029_Observability_Metrics.md
├── 030_Load_Testing_With_k6.md
└── 031_Production_MiniScheduler.md
```
