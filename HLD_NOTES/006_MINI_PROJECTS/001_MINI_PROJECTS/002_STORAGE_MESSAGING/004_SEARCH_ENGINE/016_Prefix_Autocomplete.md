# 016_Prefix_Autocomplete.md

# MiniSearchEngine — 016 Prefix Autocomplete

## 0. Why This File Exists

Modern search systems provide:

```text
autocomplete
search suggestions
search-as-you-type
instant query completion
```

Example:

```text
User types:
red

Search suggests:
redis
redisson
redis cluster
redis tutorial
```

This feature is called:

```text
Prefix Autocomplete
```

Autocomplete is one of the MOST important features inside:

```text
Google Search
YouTube
Amazon
Netflix
Elasticsearch
IDE search systems
```

This file teaches:

```text
prefix search
autocomplete
tries
prefix queries
edge ngrams
completion suggester
search-as-you-type
Lucene PrefixQuery
Elasticsearch autocomplete
ranking suggestions
Java trie implementation
dry runs
production optimizations
```

Only ASCII diagrams are used.

---

# 1. One-Line Definition

```text
Prefix autocomplete finds terms beginning with a user-typed prefix.
```

---

# 2. Biggest Mental Model

User types:

```text
red
```

Search engine tries to find:

```text
all terms starting with red
```

---

# 3. Prefix Search ASCII

```text
Input Prefix:
red

Matching Terms:
redis
redisson
redirect
reducer
```

---

# 4. Why Autocomplete Important

Autocomplete improves:

```text
search speed
user experience
query discovery
typing effort
search accuracy
```

Without autocomplete:

```text
users must type complete queries
```

---

# 5. Real World Examples

Google:

```text
machine lea...
```

Suggestions:

```text
machine learning
machine learning roadmap
machine learning tutorial
```

Amazon:

```text
iph...
```

Suggestions:

```text
iphone 15
iphone charger
iphone case
```

---

# 6. Prefix Query

Basic query:

```text
red*
```

Meaning:

```text
all terms starting with red
```

---

# 7. Prefix Query ASCII

```text
red*
  ↓
redis
redisson
redirect
```

---

# 8. Why Normal Inverted Index Not Enough

Inverted index optimized for:

```text
exact term lookup
```

Example:

```text
redis → postings
```

But autocomplete requires:

```text
prefix lookup
```

---

# 9. Prefix Lookup Problem

Query:

```text
red*
```

Need to find:

```text
all terms beginning with red
```

This requires efficient prefix traversal.

---

# 10. Trie Data Structure

Most famous autocomplete structure:

```text
Trie (Prefix Tree)
```

Trie stores:

```text
characters level-by-level
```

---

# 11. Trie Mental Model

Each node represents:

```text
one character
```

Path from root forms words.

---

# 12. Trie ASCII

Words:

```text
redis
redisson
redirect
```

Trie:

```text
(root)
   |
   r
   |
   e
   |
   d
  / \
 i   i
 |   |
 s   r
```

---

# 13. Why Trie Powerful

Trie enables:

```text
fast prefix lookup
```

Instead of scanning all words.

---

# 14. Trie Complexity

Search complexity:

```text
O(prefix length)
```

NOT:

```text
O(total words)
```

Huge improvement.

---

# 15. Trie Search Example

Input:

```text
red
```

Traversal:

```text
r → e → d
```

Then collect children.

Results:

```text
redis
redisson
redirect
```

---

# 16. Trie Search ASCII

```text
Prefix:
red

Traverse:
r → e → d

Collect subtree words
```

---

# 17. End Of Word Marker

Trie nodes need:

```text
isWord flag
```

to know complete words.

Example:

```text
red
```

may itself be valid word.

---

# 18. Trie Node ASCII

```text
Node
 ├── children
 └── isWord
```

---

# 19. Autocomplete Pipeline

```text
User Types Prefix
        ↓
Prefix Query
        ↓
Trie Traversal
        ↓
Collect Matching Terms
        ↓
Rank Suggestions
        ↓
Return Suggestions
```

---

# 20. Ranking Suggestions

Autocomplete suggestions ranked using:

```text
popularity
frequency
click-through rate
recency
personalization
```

---

# 21. Ranking Example

User types:

```text
redis
```

Suggestions:

```text
redis tutorial
redis cluster
redis docker
```

More popular queries ranked higher.

---

# 22. Suggestion Ranking ASCII

```text
Matching Terms
      ↓
Popularity Scoring
      ↓
Top Suggestions
```

---

# 23. Prefix Search vs Phrase Search

Phrase search:

```text
checks exact order
```

Prefix search:

```text
finds terms beginning with prefix
```

Different problem.

---

# 24. Prefix Query In Lucene

Lucene internally supports:

```text
PrefixQuery
```

Example:

```text
red*
```

Lucene expands terms beginning with:

```text
red
```

---

# 25. Prefix Query Expansion

Prefix query:

```text
red*
```

may rewrite into:

```text
redis OR redirect OR redisson
```

---

# 26. Prefix Expansion ASCII

```text
red*
  ↓
Term Expansion
  ↓
redis
redirect
redisson
```

---

# 27. Why Prefix Queries Expensive

Large vocabularies may contain:

```text
millions of matching terms
```

Prefix expansion expensive.

---

# 28. Production Limits

Search systems often limit:

```text
max expansions
max suggestions
timeout
```

to avoid overload.

---

# 29. Edge N-Grams

Alternative autocomplete technique:

```text
Edge N-Grams
```

---

# 30. Edge N-Gram Mental Model

Word:

```text
redis
```

Generate prefixes:

```text
r
re
red
redi
redis
```

Store as searchable tokens.

---

# 31. Edge N-Gram ASCII

```text
redis
  ↓

r
re
red
redi
redis
```

---

# 32. Why Edge N-Grams Useful

Enables autocomplete using:

```text
normal inverted index lookup
```

instead of trie traversal.

---

# 33. Elasticsearch Autocomplete

Elasticsearch commonly uses:

```text
edge_ngram analyzer
completion suggester
search_as_you_type
```

---

# 34. Completion Suggester

Special optimized autocomplete structure.

Designed for:

```text
fast low-latency suggestions
```

---

# 35. Search-As-You-Type

Autocomplete updates results:

```text
after every keystroke
```

Example:

```text
r
re
red
redi
redis
```

---

# 36. Search-As-You-Type ASCII

```text
User Typing
     ↓
Incremental Prefix Queries
     ↓
Live Suggestions
```

---

# 37. Typo Tolerance

Autocomplete may support:

```text
fuzzy prefix matching
```

Example:

```text
redsi
```

suggests:

```text
redis
```

---

# 38. Personalized Suggestions

Modern systems personalize suggestions using:

```text
history
location
language
behavior
preferences
```

---

# 39. Popularity Boost

Queries with high usage receive:

```text
higher autocomplete ranking
```

Example:

```text
redis docker
```

may rank higher than:

```text
redis replication architecture tutorial
```

---

# 40. Memory Tradeoffs

Autocomplete structures consume:

```text
memory
```

especially tries.

Tradeoff:

```text
faster lookup
vs
higher memory usage
```

---

# 41. Trie Compression

Large tries optimized using:

```text
compressed tries
radix trees
finite state transducers
```

---

# 42. Radix Tree Mental Model

Instead of one char per node:

```text
store substrings
```

Reduces memory.

---

# 43. Radix Tree ASCII

Instead of:

```text
r → e → d → i → s
```

store:

```text
redis
```

as compressed edge.

---

# 44. Finite State Transducer (FST)

Lucene heavily uses:

```text
FST
```

for autocomplete and dictionaries.

FST extremely memory efficient.

---

# 45. Why Lucene Uses FST

Benefits:

```text
compressed memory
fast traversal
shared prefixes
disk efficiency
```

---

# 46. Autocomplete In Distributed Search

Elasticsearch distributes autocomplete across:

```text
shards
```

Coordinator merges suggestions.

---

# 47. Distributed Autocomplete ASCII

```text
User Prefix
      ↓
Shard1 Suggestions
Shard2 Suggestions
Shard3 Suggestions
      ↓
Merge Suggestions
```

---

# 48. Java TrieNode

```java
import java.util.HashMap;
import java.util.Map;

public class TrieNode {

    Map<Character, TrieNode> children =
            new HashMap<>();

    boolean isWord;
}
```

---

# 49. Java Trie

```java
public class Trie {

    private final TrieNode root =
            new TrieNode();

    public void insert(String word) {

        TrieNode current = root;

        for (char ch : word.toCharArray()) {

            current.children.putIfAbsent(
                    ch,
                    new TrieNode()
            );

            current =
                    current.children.get(ch);
        }

        current.isWord = true;
    }
}
```

---

# 50. Java Insert Dry Run

Insert:

```text
redis
```

Trie path:

```text
r → e → d → i → s
```

Mark:

```text
s.isWord = true
```

---

# 51. Java Prefix Search

```java
public boolean startsWith(
        String prefix) {

    TrieNode current = root;

    for (char ch : prefix.toCharArray()) {

        if (!current.children.containsKey(ch)) {
            return false;
        }

        current =
                current.children.get(ch);
    }

    return true;
}
```

---

# 52. Prefix Search Dry Run

Trie contains:

```text
redis
redisson
redirect
```

Search:

```text
red
```

Traversal:

```text
r → e → d
```

Prefix exists.

---

# 53. Java DFS Suggestion Collection

```java
import java.util.*;

public class SuggestionCollector {

    public void collect(
            TrieNode node,
            String prefix,
            List<String> result) {

        if (node.isWord) {
            result.add(prefix);
        }

        for (Map.Entry<Character,
                TrieNode> entry
                : node.children.entrySet()) {

            collect(
                    entry.getValue(),
                    prefix + entry.getKey(),
                    result
            );
        }
    }
}
```

---

# 54. Suggestion Collection Dry Run

Prefix:

```text
red
```

Subtree words:

```text
redis
redirect
redisson
```

Suggestions returned.

---

# 55. Edge N-Gram Example

Word:

```text
redis
```

Indexed tokens:

```text
r
re
red
redi
redis
```

Query:

```text
red
```

Matches token:

```text
red
```

---

# 56. Trie vs Edge N-Gram

Trie:

```text
fast prefix traversal
higher memory
```

Edge N-Gram:

```text
uses inverted index
larger index size
simpler integration
```

---

# 57. Production Challenges

Real autocomplete systems handle:

```text
billions of queries
multi-language text
typo correction
ranking personalization
distributed suggestions
hot query caching
```

---

# 58. Common Mistakes

## Mistake 1

```text
Scanning all terms for prefixes
```

Too slow.

Use trie/FST.

---

## Mistake 2

```text
Returning unlimited suggestions
```

Need top-k limits.

---

## Mistake 3

```text
Ignoring ranking
```

Autocomplete quality becomes poor.

---

## Mistake 4

```text
Using large wildcard expansion
```

Can overload cluster.

---

# 59. Interview Explanation

If interviewer asks:

```text
How does autocomplete work?
```

Strong answer:

```text
Autocomplete uses prefix matching to find terms beginning with a typed
prefix. Common implementations use tries, edge n-grams, or FSTs to
support low-latency prefix lookup and ranked suggestions.
```

Senior addition:

```text
Lucene often uses FST-based completion suggesters while Elasticsearch
supports edge_ngram analyzers and search_as_you_type fields.
```

---

# 60. Final Mental Model

```text
User Prefix
      ↓
Prefix Traversal
      ↓
Collect Matching Terms
      ↓
Rank Suggestions
      ↓
Return Top Suggestions
```

---

# 61. What To Remember

```text
Autocomplete uses prefix matching.

Trie is classic autocomplete structure.

Edge N-Grams generate prefixes.

Lucene supports PrefixQuery.

Lucene uses FST internally.

Elasticsearch supports completion suggesters.

Suggestions ranked using popularity and relevance.

Autocomplete systems require top-k optimization.
```

---

# 62. Next File

```text
017_Fuzzy_Search_Edit_Distance.md
```

Next you learn:

```text
typo correction
edit distance
Levenshtein distance
fuzzy search
spell correction
Lucene FuzzyQuery
Elasticsearch fuzzy matching
BK trees
production typo tolerance
```
