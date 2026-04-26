# 14 — Design a Search Autocomplete System

> Goal: design a typeahead/autocomplete system that returns the top `k` most popular query suggestions for a given prefix.

---

# Step 0 — What is Search Autocomplete?

When a user types in a search box, the system suggests possible search queries.

Example:

```text
User types: din

Suggestions:
1. dinner ideas
2. dinner recipes
3. dinner near me
4. dinner tonight
5. dinner rolls
```

Other names:

```text
autocomplete
typeahead
search-as-you-type
incremental search
top-k searched queries
```

---

# Step 1 — Clarify Requirements

## Functional Requirements

- Given a prefix, return top 5 suggestions.
- Match only from the beginning of a query.
- Rank suggestions by historical search frequency.
- Support lowercase English alphabetic characters.
- No spell check.
- No autocorrect.
- No capitalization or special characters.

## Non-functional Requirements

- Very fast response time.
- Target latency: under 100 ms.
- Highly available.
- Scalable to high QPS.
- Relevant suggestions.
- Sorted by popularity.
- Fault tolerant.

## Scale

```text
DAU = 10 million
Searches/user/day = 10
Average query length = 20 characters
One request per character typed
```

QPS:

```text
10M users * 10 searches/day * 20 chars
-------------------------------------- ≈ 24K QPS
       24 * 3600 seconds
```

Peak QPS:

```text
~48K QPS
```

New data per day:

```text
20% new queries

10M * 10 * 20 bytes * 20% = 0.4GB/day
```

Interview line:

> Query service must be extremely fast, so we should serve suggestions from memory/cache, not from a relational database scan.

---

# Step 2 — High-Level Design

The system has two major parts:

```text
1. Data gathering service
2. Query service
```

## Visual

```text
                    +----------------------+
                    | User Search Logs     |
                    +----------+-----------+
                               |
                               v
                    +----------------------+
                    | Aggregation Pipeline |
                    +----------+-----------+
                               |
                               v
                    +----------------------+
                    | Trie Builder Workers |
                    +----------+-----------+
                               |
                               v
                    +----------------------+
                    | Trie DB              |
                    +----------+-----------+
                               |
                               v
                    +----------------------+
                    | Trie Cache           |
                    +----------+-----------+
                               ^
                               |
User -> Load Balancer -> API Servers
```

---

# Step 3 — Naive Query Service

Store query frequency in a database.

```text
frequency_table

+----------------+-----------+
| query          | frequency |
+----------------+-----------+
| twitter        | 35        |
| twitch         | 29        |
| twilight       | 25        |
| twin peak      | 21        |
| twitch prime   | 18        |
+----------------+-----------+
```

SQL:

```sql
SELECT *
FROM frequency_table
WHERE query LIKE 'tw%'
ORDER BY frequency DESC
LIMIT 5;
```

## Problem

This is too slow at scale because:

```text
LIKE prefix scan can be expensive.
Sorting large result sets is expensive.
Database becomes bottleneck.
```

---

# Step 4 — Use Trie / Prefix Tree

A trie stores strings by prefix.

Example queries:

```text
tree
try
true
toy
wish
win
```

Visual:

```text
root
 ├── t
 │   ├── tr
 │   │   ├── tree
 │   │   ├── true
 │   │   └── try
 │   └── to
 │       └── toy
 └── w
     └── wi
         ├── wish
         └── win
```

Each node represents a prefix.

```text
root -> t -> tr
prefix = "tr"
```

---

# Step 5 — Store Frequency in Trie

Queries with frequency:

```text
tree: 10
try: 29
true: 35
toy: 14
wish: 25
win: 50
```

Visual:

```text
root
 ├── t
 │   ├── tr
 │   │   ├── tree:10
 │   │   ├── true:35
 │   │   └── try:29
 │   └── to
 │       └── toy:14
 └── w
     └── wi
         ├── wish:25
         └── win:50
```

For prefix:

```text
tr
```

Valid suggestions:

```text
tree:10
true:35
try:29
```

Top 2:

```text
true
try
```

---

# Step 6 — Basic Trie Query Algorithm

To get top `k` suggestions:

```text
1. Find prefix node.
2. Traverse subtree.
3. Collect all valid query words.
4. Sort by frequency.
5. Return top k.
```

Complexity:

```text
O(p) + O(c) + O(c log c)
```

Where:

```text
p = prefix length
c = number of candidate words under prefix
```

## Problem

If prefix is short, subtree can be huge.

Example:

```text
prefix = "a"
```

This may have millions of candidates.

---

# Step 7 — Optimize Trie

## Optimization 1: Limit Prefix Length

Users rarely type very long prefixes.

```text
Max prefix length = 50
```

So prefix lookup is effectively:

```text
O(1)
```

---

## Optimization 2: Cache Top K at Every Node

Store top 5 suggestions at every prefix node.

Example:

```text
node "be" stores:
[best:35, bet:29, bee:20, be:15, beer:10]
```

Visual:

```text
root
 └── b [best, bet, bee, be, beer]
     └── e [best, bet, bee, be, beer]
         ├── s [best]
         │   └── t [best]
         ├── t [bet]
         └── e [bee, beer]
             └── r [beer]
```

Now query flow:

```text
1. Find prefix node.
2. Return cached top 5 list.
```

Complexity:

```text
O(1)
```

Tradeoff:

```text
More memory, much faster reads.
```

Interview line:

> We trade memory for speed by caching top suggestions at each trie node.

---

# Step 8 — Query Service Design

```text
User
 |
 v
Load Balancer
 |
 v
API Servers
 |
 v
Filter Layer
 |
 v
Trie Cache
 |
 v
Trie DB on cache miss
```

Visual:

```text
+------+     +---------------+     +-------------+
| User | --> | Load Balancer | --> | API Servers |
+------+     +---------------+     +------+------+
                                           |
                                           v
                                    +-------------+
                                    | Filter      |
                                    | Layer       |
                                    +------+------+
                                           |
                                           v
                                    +-------------+
                                    | Trie Cache  |
                                    +------+------+
                                           |
                                      cache miss
                                           |
                                           v
                                    +-------------+
                                    | Trie DB     |
                                    +-------------+
```

Query flow:

```text
1. User types prefix.
2. Browser sends AJAX request.
3. API server checks Trie Cache.
4. Cache returns top 5 suggestions.
5. Filter layer removes blocked suggestions.
6. Return response.
```

---

# Step 9 — Browser Caching

Autocomplete results may not change every second.

Use HTTP cache headers:

```http
Cache-Control: private, max-age=3600
```

Meaning:

```text
private      -> cache only for this user/browser
max-age=3600 -> cache for 1 hour
```

Benefit:

```text
Reduces backend QPS.
Improves latency.
```

---

# Step 10 — Data Gathering Service

Do not update trie for every keystroke.

Instead:

```text
Collect logs -> aggregate -> build trie periodically
```

Visual:

```text
+----------------+
| Analytics Logs |
+-------+--------+
        |
        v
+----------------+
| Aggregators    |
+-------+--------+
        |
        v
+----------------+
| Aggregated DB  |
+-------+--------+
        |
        v
+----------------+
| Trie Workers   |
+-------+--------+
        |
        v
+----------------+
| Trie DB        |
+-------+--------+
        |
        v
+----------------+
| Trie Cache     |
+----------------+
```

---

# Step 11 — Analytics Logs

Raw search log:

```text
+-------+---------------------+
| query | time                |
+-------+---------------------+
| tree  | 2019-10-01 22:01:01 |
| try   | 2019-10-01 22:01:05 |
| tree  | 2019-10-01 22:01:30 |
| toy   | 2019-10-01 22:02:22 |
+-------+---------------------+
```

Logs are:

```text
append-only
large volume
not optimized for direct query
```

---

# Step 12 — Aggregated Data

Aggregate by time window.

Example weekly aggregation:

```text
+-------+------------+-----------+
| query | week_start | frequency |
+-------+------------+-----------+
| tree  | 2019-10-01 | 12000     |
| tree  | 2019-10-08 | 15000     |
| toy   | 2019-10-01 | 8500      |
+-------+------------+-----------+
```

Aggregation frequency depends on product:

```text
Search engine autocomplete: daily/weekly may be fine
Twitter/news: near real-time needed
```

---

# Step 13 — Trie Build Strategy

## Offline Build

```text
1. Read aggregated data.
2. Build new trie.
3. Store trie snapshot in Trie DB.
4. Push snapshot to Trie Cache.
5. Atomically switch from old trie to new trie.
```

Visual:

```text
Old Trie serving traffic
        |
        v
Build New Trie in background
        |
        v
Validate New Trie
        |
        v
Atomic Swap
        |
        v
New Trie serving traffic
```

Benefit:

```text
No write pressure on online query service.
```

---

# Step 14 — Trie Storage Options

## Option 1: Serialized Trie

Store full trie snapshot.

Good for:

```text
weekly rebuild
document store
object storage
```

Example:

```text
trie_snapshot_2026_04_26
```

## Option 2: Key-Value Prefix Mapping

Store each prefix as key.

```text
key: "be"
value: ["best", "bet", "bee", "be", "beer"]
```

Visual:

```text
Trie Node               KV Store
---------               -------------------------
prefix "b"      ----->  b  -> [best, bet, bee]
prefix "be"     ----->  be -> [best, bet, beer]
prefix "bee"    ----->  bee -> [bee, beer]
```

For fast query service, KV mapping is very practical.

---

# Step 15 — Updating Trie

## Option A: Rebuild periodically

Recommended.

```text
weekly or daily rebuild
```

Pros:

```text
simple
stable
fast reads
does not slow query service
```

Cons:

```text
not real-time
```

---

## Option B: Update trie nodes directly

If query frequency changes:

```text
beer: 10 -> beer: 30
```

Need to update:

```text
beer node
bee node
be node
b node
root node
```

Visual:

```text
beer changed
   |
   v
update ancestors:
beer -> bee -> be -> b -> root
```

Problem:

```text
expensive at large scale
```

---

# Step 16 — Delete / Filter Bad Suggestions

Some suggestions must be removed:

```text
hateful
violent
adult
dangerous
spam
policy-violating
```

Use filter layer before returning suggestions.

```text
API Server -> Filter Layer -> Trie Cache
```

Flow:

```text
Top 5 from trie:
[query1, bad_query, query3, query4, query5]

Filter layer removes bad_query.

Return:
[query1, query3, query4, query5]
```

Bad suggestions are also removed from source data asynchronously so future trie builds are clean.

---

# Step 17 — Scale the Storage

Trie may not fit on one server.

## Simple Sharding by First Character

```text
a-m -> Shard 1
n-z -> Shard 2
```

Problem:

```text
Uneven distribution.
More queries may start with "c" than "x".
```

---

## Smarter Sharding with Shard Map Manager

Use historical distribution.

```text
Shard 1: a-b
Shard 2: c
Shard 3: d-f
Shard 4: g-m
Shard 5: n-z
```

Visual:

```text
API Server
    |
    v
Shard Map Manager
    |
    +---- prefix "c"  -> Shard 2
    |
    +---- prefix "tr" -> Shard 5
```

Architecture:

```text
+-------------+
| API Servers |
+------+------+
       |
       v
+-------------------+
| Shard Map Manager |
+------+------------+
       |
+------+------+------+
|      |      |      |
v      v      v      v
S1     S2     S3     S4
```

---

# Step 18 — Multi-language Support

For English-only:

```text
26 lowercase letters
```

For multiple languages:

```text
Use Unicode characters in trie nodes.
```

Possible design:

```text
country/language specific tries
```

Example:

```text
Trie_EN_US
Trie_EN_IN
Trie_ES_ES
Trie_FR_FR
```

Benefit:

```text
Different countries can have different top suggestions.
```

---

# Step 19 — Trending / Real-time Suggestions

Weekly rebuild may not catch breaking trends.

Examples:

```text
earthquake today
world cup final
breaking news event
```

Solution ideas:

```text
1. Stream query logs through Kafka.
2. Use sliding window counts.
3. Give more weight to recent queries.
4. Maintain separate trending trie/cache.
5. Merge offline top results with real-time trending results.
```

Visual:

```text
Offline Trie Results
        +
Real-time Trending Results
        |
        v
Ranker
        |
        v
Final Top 5
```

Ranking example:

```text
score = historical_frequency * 0.7 + recent_frequency * 0.3
```

---

# Step 20 — Final Architecture

```text
                         Online Query Path
                         =================

User Browser / Mobile
        |
        v
+----------------+
| Load Balancer  |
+--------+-------+
         |
         v
+----------------+
| API Servers    |
+--------+-------+
         |
         v
+----------------+
| Filter Layer   |
+--------+-------+
         |
         v
+----------------+
| Trie Cache     |
+--------+-------+
         |
    cache miss
         |
         v
+----------------+
| Trie DB        |
+----------------+


                         Offline Build Path
                         ==================

+----------------+
| Search Logs    |
+--------+-------+
         |
         v
+----------------+
| Aggregators    |
+--------+-------+
         |
         v
+----------------+
| Aggregated DB  |
+--------+-------+
         |
         v
+----------------+
| Trie Builders  |
+--------+-------+
         |
         v
+----------------+
| Trie DB        |
+--------+-------+
         |
         v
+----------------+
| Trie Cache     |
+----------------+
```

---

# Step 21 — End-to-End Query Flow

```text
1. User types a character.
2. Browser sends AJAX request with prefix.
3. API server receives request.
4. API server finds shard/cache for prefix.
5. Trie cache returns cached top 5 suggestions.
6. Filter layer removes blocked suggestions.
7. API server returns suggestions.
8. Browser caches response briefly.
```

---

# Step 22 — End-to-End Data Build Flow

```text
1. User searches are written to analytics logs.
2. Logs are sampled if traffic is too high.
3. Aggregators count query frequency.
4. Aggregated data is stored.
5. Workers build a new trie.
6. Each trie node stores top k suggestions.
7. Trie snapshot is saved to Trie DB.
8. Trie Cache is refreshed.
9. New trie replaces old trie atomically.
```

---

# Step 23 — Java Code: Trie Node

```java
import java.util.*;

class TrieNode {
    Map<Character, TrieNode> children = new HashMap<>();
    boolean isWord;
    String word;
    int frequency;

    // Cached top suggestions for this prefix.
    List<Suggestion> topSuggestions = new ArrayList<>();
}

record Suggestion(String query, int frequency) {}
```

---

# Step 24 — Java Code: Autocomplete Trie

```java
import java.util.*;

public class AutocompleteTrie {
    private static final int TOP_K = 5;
    private final TrieNode root = new TrieNode();

    public void insert(String query, int frequency) {
        TrieNode node = root;

        updateTopSuggestions(node, query, frequency);

        for (char c : query.toCharArray()) {
            node = node.children.computeIfAbsent(c, ch -> new TrieNode());
            updateTopSuggestions(node, query, frequency);
        }

        node.isWord = true;
        node.word = query;
        node.frequency = frequency;
    }

    public List<String> search(String prefix) {
        TrieNode node = root;

        for (char c : prefix.toCharArray()) {
            node = node.children.get(c);
            if (node == null) {
                return List.of();
            }
        }

        return node.topSuggestions
                .stream()
                .map(Suggestion::query)
                .toList();
    }

    private void updateTopSuggestions(TrieNode node, String query, int frequency) {
        node.topSuggestions.removeIf(s -> s.query().equals(query));
        node.topSuggestions.add(new Suggestion(query, frequency));

        node.topSuggestions.sort((a, b) -> {
            int freqCompare = Integer.compare(b.frequency(), a.frequency());
            if (freqCompare != 0) return freqCompare;
            return a.query().compareTo(b.query());
        });

        if (node.topSuggestions.size() > TOP_K) {
            node.topSuggestions = new ArrayList<>(node.topSuggestions.subList(0, TOP_K));
        }
    }

    public static void main(String[] args) {
        AutocompleteTrie trie = new AutocompleteTrie();

        trie.insert("twitter", 35);
        trie.insert("twitch", 29);
        trie.insert("twilight", 25);
        trie.insert("twin peak", 21);
        trie.insert("twitch prime", 18);
        trie.insert("twitter search", 14);
        trie.insert("twillo", 10);

        System.out.println(trie.search("tw"));
        // [twitter, twitch, twilight, twin peak, twitch prime]
    }
}
```

---

# Step 25 — Java Code: Simple Query Normalizer

```java
public class QueryNormalizer {
    public static String normalize(String query) {
        if (query == null) {
            return "";
        }

        return query
                .toLowerCase()
                .replaceAll("[^a-z ]", "")
                .replaceAll("\\s+", " ")
                .trim();
    }

    public static void main(String[] args) {
        System.out.println(normalize("  Twitter!!! Search  "));
        // twitter search
    }
}
```

---

# Step 26 — Java Code: Frequency Aggregator

```java
import java.util.*;

public class QueryFrequencyAggregator {
    private final Map<String, Integer> frequencyMap = new HashMap<>();

    public void recordQuery(String query) {
        String normalized = QueryNormalizer.normalize(query);
        if (normalized.isBlank()) return;

        frequencyMap.merge(normalized, 1, Integer::sum);
    }

    public Map<String, Integer> getFrequencies() {
        return frequencyMap;
    }

    public static void main(String[] args) {
        QueryFrequencyAggregator aggregator = new QueryFrequencyAggregator();

        aggregator.recordQuery("tree");
        aggregator.recordQuery("try");
        aggregator.recordQuery("tree");
        aggregator.recordQuery("toy");

        System.out.println(aggregator.getFrequencies());
    }
}
```

---

# Step 27 — Java Code: Build Trie from Aggregated Data

```java
import java.util.Map;

public class TrieBuilder {
    public static AutocompleteTrie build(Map<String, Integer> frequencies) {
        AutocompleteTrie trie = new AutocompleteTrie();

        for (Map.Entry<String, Integer> entry : frequencies.entrySet()) {
            trie.insert(entry.getKey(), entry.getValue());
        }

        return trie;
    }
}
```

---

# Step 28 — Java Code: Filter Bad Suggestions

```java
import java.util.*;

public class SuggestionFilter {
    private final Set<String> blockedQueries = new HashSet<>();

    public void block(String query) {
        blockedQueries.add(QueryNormalizer.normalize(query));
    }

    public List<String> filter(List<String> suggestions) {
        List<String> result = new ArrayList<>();

        for (String suggestion : suggestions) {
            if (!blockedQueries.contains(QueryNormalizer.normalize(suggestion))) {
                result.add(suggestion);
            }
        }

        return result;
    }

    public static void main(String[] args) {
        SuggestionFilter filter = new SuggestionFilter();
        filter.block("bad query");

        System.out.println(filter.filter(List.of("twitter", "bad query", "twitch")));
        // [twitter, twitch]
    }
}
```

---

# Step 29 — Java Code: Mini Autocomplete Service

```java
import java.util.List;
import java.util.Map;

public class AutocompleteService {
    private final AutocompleteTrie trie;
    private final SuggestionFilter filter;

    public AutocompleteService(AutocompleteTrie trie, SuggestionFilter filter) {
        this.trie = trie;
        this.filter = filter;
    }

    public List<String> suggest(String prefix) {
        String normalizedPrefix = QueryNormalizer.normalize(prefix);
        List<String> suggestions = trie.search(normalizedPrefix);
        return filter.filter(suggestions);
    }

    public static void main(String[] args) {
        Map<String, Integer> data = Map.of(
                "twitter", 35,
                "twitch", 29,
                "twilight", 25,
                "twin peak", 21,
                "twitch prime", 18,
                "twitter search", 14
        );

        AutocompleteTrie trie = TrieBuilder.build(data);

        SuggestionFilter filter = new SuggestionFilter();
        filter.block("twilight");

        AutocompleteService service = new AutocompleteService(trie, filter);

        System.out.println(service.suggest("tw"));
        // [twitter, twitch, twin peak, twitch prime, twitter search]
    }
}
```

---

# Step 30 — Scaling Strategy

## Query Service

```text
Use CDN/browser cache for popular prefixes.
Use Trie Cache in memory.
Shard Trie Cache by prefix.
Keep API servers stateless.
```

## Data Pipeline

```text
Use append-only logs.
Sample logs if traffic is huge.
Aggregate periodically.
Build trie offline.
Atomic swap trie snapshot.
```

## Storage

```text
Trie DB stores snapshots or prefix->topK mappings.
Trie Cache serves online traffic.
Shard using shard map manager.
```

---

# Step 31 — Failure Scenarios

## Trie Cache Down

```text
Fallback to Trie DB.
Repopulate cache.
```

## Trie DB Down

```text
Continue serving from existing Trie Cache.
Use stale data temporarily.
```

## Bad Trie Build

```text
Validate new trie before swap.
Keep old trie snapshot for rollback.
```

## Aggregation Pipeline Delay

```text
Query service continues using previous trie.
Freshness is delayed but service remains available.
```

---

# Step 32 — FAANG Talking Points

1. Use trie because autocomplete is prefix-based.
2. Cache top K suggestions at every trie node.
3. This makes query time effectively O(1).
4. Build trie offline from aggregated logs.
5. Do not update trie on every keystroke.
6. Use Trie Cache for low latency.
7. Use Trie DB for persistence.
8. Use browser caching to reduce QPS.
9. Use filter layer for unsafe suggestions.
10. Use shard map manager to balance trie shards.
11. Use country/language-specific tries for localization.
12. Use streaming pipeline for real-time trending suggestions.
13. Use atomic trie snapshot swap.
14. Keep old snapshot for rollback.
15. Query service should remain available with stale cache if build pipeline fails.

---

# Step 33 — One-Minute Interview Summary

> I would build autocomplete using a trie where each node represents a prefix and stores the top 5 most frequent queries for that prefix. This allows the online query service to return suggestions in near O(1) time. The online path is user request to load balancer, API server, filter layer, and Trie Cache, with Trie DB as fallback. I would not update the trie on every query. Instead, raw search logs are collected, sampled if needed, aggregated periodically, and offline workers build a new trie snapshot. The new trie is validated and atomically swapped into Trie Cache. For scale, trie data is sharded by prefix using a shard map manager, and browser caching is used to reduce repeated requests. For unsafe suggestions, a filter layer removes blocked queries before returning results.

---

# Quick Revision

```text
Problem:
prefix -> top 5 popular suggestions

Data structure:
Trie with top K cached at each node

Online query:
User -> LB -> API -> Filter -> Trie Cache -> top 5

Offline build:
Logs -> Aggregator -> Trie Builder -> Trie DB -> Trie Cache

Optimization:
Browser cache
Prefix shard map
Top K at each node
Atomic snapshot swap

Best phrase:
Trade memory for speed by caching top K suggestions at every trie node.
```
