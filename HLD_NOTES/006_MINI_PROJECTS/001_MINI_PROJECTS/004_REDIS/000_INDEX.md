# MiniRedis — 000_INDEX.md

## Clickable Tree Index

```text
MiniRedis/
```

- [000_INDEX.md](./000_INDEX.md) — Master index and learning map
- [001_TCP_Echo_Server.md](./001_TCP_Echo_Server.md) — Build the first TCP echo server
- [002_RESP_Protocol_Parser.md](./002_RESP_Protocol_Parser.md) — Parse Redis RESP protocol
- [003_InMemory_KeyValue_Store.md](./003_InMemory_KeyValue_Store.md) — Add in-memory key-value storage
- [004_SET_GET_DEL_EXISTS.md](./004_SET_GET_DEL_EXISTS.md) — Implement core Redis commands
- [005_TTL_Expiration.md](./005_TTL_Expiration.md) — Add TTL and lazy expiration
- [006_Background_Cleanup.md](./006_Background_Cleanup.md) — Add background cleanup worker
- [007_Thread_Safe_Storage.md](./007_Thread_Safe_Storage.md) — Make storage thread-safe
- [008_RDB_Snapshot.md](./008_RDB_Snapshot.md) — Add snapshot persistence
- [009_AOF_Append_Only_Log.md](./009_AOF_Append_Only_Log.md) — Add append-only log
- [010_Recover_From_AOF.md](./010_Recover_From_AOF.md) — Recover data by replaying AOF
- [011_Multi_Client_Server.md](./011_Multi_Client_Server.md) — Support multiple clients
- [012_Command_Executor_ThreadPool.md](./012_Command_Executor_ThreadPool.md) — Execute commands using thread pool
- [013_LRU_Eviction.md](./013_LRU_Eviction.md) — Add LRU eviction
- [014_LFU_Eviction.md](./014_LFU_Eviction.md) — Add LFU eviction
- [015_List_Data_Type.md](./015_List_Data_Type.md) — Add Redis List commands
- [016_Set_Data_Type.md](./016_Set_Data_Type.md) — Add Redis Set commands
- [017_Hash_Data_Type.md](./017_Hash_Data_Type.md) — Add Redis Hash commands
- [018_Sorted_Set_ZSet.md](./018_Sorted_Set_ZSet.md) — Add Sorted Set and ranking
- [019_Pub_Sub.md](./019_Pub_Sub.md) — Add Pub/Sub messaging
- [020_Streams_Log.md](./020_Streams_Log.md) — Add Redis Streams style log
- [021_Master_Replica.md](./021_Master_Replica.md) — Add master-replica replication
- [022_Replication_Offset.md](./022_Replication_Offset.md) — Track replication offsets
- [023_Consistent_Hashing.md](./023_Consistent_Hashing.md) — Add consistent hashing
- [024_Distributed_Sharding.md](./024_Distributed_Sharding.md) — Add distributed sharding
- [025_Distributed_Lock.md](./025_Distributed_Lock.md) — Add distributed lock
- [026_Redlock_Basic.md](./026_Redlock_Basic.md) — Build basic Redlock
- [027_GeoHash.md](./027_GeoHash.md) — Add geohash storage
- [028_Nearest_Driver_Search.md](./028_Nearest_Driver_Search.md) — Build nearest driver search
- [029_Metrics_And_Monitoring.md](./029_Metrics_And_Monitoring.md) — Add metrics and monitoring
- [030_Load_Testing_With_k6.md](./030_Load_Testing_With_k6.md) — Load test with k6
- [031_Production_MiniRedis.md](./031_Production_MiniRedis.md) — Final production architecture

---

## Tree View

```text
MiniRedis/
├── 000_INDEX.md                         # Master index
├── 001_TCP_Echo_Server.md               # TCP socket server + echo response
├── 002_RESP_Protocol_Parser.md          # Redis protocol parser
├── 003_InMemory_KeyValue_Store.md       # Basic HashMap storage
├── 004_SET_GET_DEL_EXISTS.md            # Core Redis commands
├── 005_TTL_Expiration.md                # TTL and lazy expiration
├── 006_Background_Cleanup.md            # Cleanup daemon
├── 007_Thread_Safe_Storage.md           # Concurrent storage
├── 008_RDB_Snapshot.md                  # Snapshot persistence
├── 009_AOF_Append_Only_Log.md           # Append-only persistence
├── 010_Recover_From_AOF.md              # Recovery by log replay
├── 011_Multi_Client_Server.md           # Multi-client TCP server
├── 012_Command_Executor_ThreadPool.md   # Async command execution
├── 013_LRU_Eviction.md                  # LRU cache eviction
├── 014_LFU_Eviction.md                  # LFU cache eviction
├── 015_List_Data_Type.md                # List commands
├── 016_Set_Data_Type.md                 # Set commands
├── 017_Hash_Data_Type.md                # Hash commands
├── 018_Sorted_Set_ZSet.md               # Sorted set / leaderboard
├── 019_Pub_Sub.md                       # Pub/Sub messaging
├── 020_Streams_Log.md                   # Stream log
├── 021_Master_Replica.md                # Master-replica sync
├── 022_Replication_Offset.md            # Replication offset
├── 023_Consistent_Hashing.md            # Hash ring
├── 024_Distributed_Sharding.md          # Multi-node sharding
├── 025_Distributed_Lock.md              # Lock with TTL
├── 026_Redlock_Basic.md                 # Redlock basics
├── 027_GeoHash.md                       # Geo indexing
├── 028_Nearest_Driver_Search.md         # Nearby search system
├── 029_Metrics_And_Monitoring.md        # Metrics endpoint
├── 030_Load_Testing_With_k6.md          # Performance testing
└── 031_Production_MiniRedis.md          # Production-grade final design
```

---

## Phase Grouping

```text
Phase 1: Networking
├── 001_TCP_Echo_Server.md
└── 002_RESP_Protocol_Parser.md

Phase 2: Core Redis Engine
├── 003_InMemory_KeyValue_Store.md
├── 004_SET_GET_DEL_EXISTS.md
├── 005_TTL_Expiration.md
├── 006_Background_Cleanup.md
└── 007_Thread_Safe_Storage.md

Phase 3: Persistence
├── 008_RDB_Snapshot.md
├── 009_AOF_Append_Only_Log.md
└── 010_Recover_From_AOF.md

Phase 4: Concurrency
├── 011_Multi_Client_Server.md
└── 012_Command_Executor_ThreadPool.md

Phase 5: Eviction
├── 013_LRU_Eviction.md
└── 014_LFU_Eviction.md

Phase 6: Redis Data Structures
├── 015_List_Data_Type.md
├── 016_Set_Data_Type.md
├── 017_Hash_Data_Type.md
└── 018_Sorted_Set_ZSet.md

Phase 7: Messaging
├── 019_Pub_Sub.md
└── 020_Streams_Log.md

Phase 8: Replication
├── 021_Master_Replica.md
└── 022_Replication_Offset.md

Phase 9: Distributed Redis
├── 023_Consistent_Hashing.md
└── 024_Distributed_Sharding.md

Phase 10: Coordination
├── 025_Distributed_Lock.md
└── 026_Redlock_Basic.md

Phase 11: Geo
├── 027_GeoHash.md
└── 028_Nearest_Driver_Search.md

Phase 12: Production
├── 029_Metrics_And_Monitoring.md
├── 030_Load_Testing_With_k6.md
└── 031_Production_MiniRedis.md
```

---

## Recommended Learning Order

Start here:

1. [001_TCP_Echo_Server.md](./001_TCP_Echo_Server.md)
2. [002_RESP_Protocol_Parser.md](./002_RESP_Protocol_Parser.md)
3. [003_InMemory_KeyValue_Store.md](./003_InMemory_KeyValue_Store.md)
4. [004_SET_GET_DEL_EXISTS.md](./004_SET_GET_DEL_EXISTS.md)
5. [005_TTL_Expiration.md](./005_TTL_Expiration.md)

These first five files build the real Redis foundation.
