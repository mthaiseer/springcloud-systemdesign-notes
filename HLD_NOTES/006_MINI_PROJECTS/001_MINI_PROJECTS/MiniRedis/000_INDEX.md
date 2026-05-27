# MiniRedis — Complete Project Index

## Clickable Tree

```text
MiniRedis/
├── 000_INDEX.md
├── 001_TCP_Echo_Server.md
├── 002_RESP_Protocol_Parser.md
├── 003_InMemory_KeyValue_Store.md
├── 004_SET_GET_DEL_EXISTS.md
├── 005_TTL_Expiration.md
├── 006_Background_Cleanup.md
├── 007_Thread_Safe_Storage.md
├── 008_RDB_Snapshot.md
├── 009_AOF_Append_Only_Log.md
├── 010_Recover_From_AOF.md
├── 011_Multi_Client_Server.md
├── 012_Command_Executor_ThreadPool.md
├── 013_LRU_Eviction.md
├── 014_LFU_Eviction.md
├── 015_List_Data_Type.md
├── 016_Set_Data_Type.md
├── 017_Hash_Data_Type.md
├── 018_Sorted_Set_ZSet.md
├── 019_Pub_Sub.md
├── 020_Streams_Log.md
├── 021_Master_Replica.md
├── 022_Replication_Offset.md
├── 023_Consistent_Hashing.md
├── 024_Distributed_Sharding.md
├── 025_Distributed_Lock.md
├── 026_Redlock_Basic.md
├── 027_GeoHash.md
├── 028_Nearest_Driver_Search.md
├── 029_Metrics_And_Monitoring.md
├── 030_Load_Testing_With_k6.md
└── 031_Production_MiniRedis.md
```

## Phase Links

- [001_TCP_Echo_Server.md](./001_TCP_Echo_Server.md) — TCP Echo Server
- [002_RESP_Protocol_Parser.md](./002_RESP_Protocol_Parser.md) — RESP Protocol Parser
- [003_InMemory_KeyValue_Store.md](./003_InMemory_KeyValue_Store.md) — In-Memory KeyValue Store
- [004_SET_GET_DEL_EXISTS.md](./004_SET_GET_DEL_EXISTS.md) — SET GET DEL EXISTS
- [005_TTL_Expiration.md](./005_TTL_Expiration.md) — TTL Expiration
- [006_Background_Cleanup.md](./006_Background_Cleanup.md) — Background Cleanup
- [007_Thread_Safe_Storage.md](./007_Thread_Safe_Storage.md) — Thread Safe Storage
- [008_RDB_Snapshot.md](./008_RDB_Snapshot.md) — RDB Snapshot
- [009_AOF_Append_Only_Log.md](./009_AOF_Append_Only_Log.md) — AOF Append Only Log
- [010_Recover_From_AOF.md](./010_Recover_From_AOF.md) — Recover From AOF
- [011_Multi_Client_Server.md](./011_Multi_Client_Server.md) — Multi Client Server
- [012_Command_Executor_ThreadPool.md](./012_Command_Executor_ThreadPool.md) — Command Executor ThreadPool
- [013_LRU_Eviction.md](./013_LRU_Eviction.md) — LRU Eviction
- [014_LFU_Eviction.md](./014_LFU_Eviction.md) — LFU Eviction
- [015_List_Data_Type.md](./015_List_Data_Type.md) — List Data Type
- [016_Set_Data_Type.md](./016_Set_Data_Type.md) — Set Data Type
- [017_Hash_Data_Type.md](./017_Hash_Data_Type.md) — Hash Data Type
- [018_Sorted_Set_ZSet.md](./018_Sorted_Set_ZSet.md) — Sorted Set ZSet
- [019_Pub_Sub.md](./019_Pub_Sub.md) — Pub Sub
- [020_Streams_Log.md](./020_Streams_Log.md) — Streams Log
- [021_Master_Replica.md](./021_Master_Replica.md) — Master Replica
- [022_Replication_Offset.md](./022_Replication_Offset.md) — Replication Offset
- [023_Consistent_Hashing.md](./023_Consistent_Hashing.md) — Consistent Hashing
- [024_Distributed_Sharding.md](./024_Distributed_Sharding.md) — Distributed Sharding
- [025_Distributed_Lock.md](./025_Distributed_Lock.md) — Distributed Lock
- [026_Redlock_Basic.md](./026_Redlock_Basic.md) — Redlock Basic
- [027_GeoHash.md](./027_GeoHash.md) — GeoHash
- [028_Nearest_Driver_Search.md](./028_Nearest_Driver_Search.md) — Nearest Driver Search
- [029_Metrics_And_Monitoring.md](./029_Metrics_And_Monitoring.md) — Metrics And Monitoring
- [030_Load_Testing_With_k6.md](./030_Load_Testing_With_k6.md) — Load Testing With k6
- [031_Production_MiniRedis.md](./031_Production_MiniRedis.md) — Production MiniRedis

## Project Goal

Build a production-style MiniRedis in Java from TCP networking to distributed cache architecture.

Each file includes:

- clickable index
- feature purpose
- previous limitation
- what changed from previous phase
- architecture/flow diagrams
- DSA/CP topics covered
- step-by-step logic before every Java code block
- complete runnable Java code with driver/main class
- dry run
- production-grade concepts
- scalability discussion
- real-world usage examples

## Best Study Order

```text
Networking -> Parser -> Store -> TTL -> Persistence -> Concurrency
-> Eviction -> Data Structures -> Pub/Sub -> Streams
-> Replication -> Sharding -> Locks -> Geo -> Metrics -> Production
```
