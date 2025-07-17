# Trie KMP

trie-kmp is a lightweight, efficient, and idiomatic library providing Trie data structures for Kotlin Multiplatform.

The library offers several primary implementations tailored for different needs:

- `CompactGenericTrie`: A memory-efficient Trie that compresses non-branching paths.
- `CompactStringTrie`: Similar to `CompactGenericTrie` but optimized for `String` keys.
- `StandardGenericTrie`: A classic, standard Trie.
- `StandardStringTrie`: Similar to `StandardGenericTrie` but optimized for `String` keys.

This allows developers to choose the optimal data structure for their specific use case, whether it's for
autocomplete systems, spell checkers, IP routing tables, or any other prefix-based application.

### Setup

```
repositories {
  mavenCentral()
}

dependencies {
  implementation("com.eygraber:trie-kmp:0.5.0")
}
```

Snapshots can be found [here](https://central.sonatype.org/publish/publish-portal-snapshots/#consuming-via-gradle).

### Usage

#### Recommended Usage for String

For any `String`-based use case, use the optimized `trieOf` / `mutableTrieOf` builders.

```kotlin
// Create a high-performance, memory-efficient compact Trie for Strings
val trie = mutableTrieOf(
  "apple" to "A fruit",
  "application" to "A formal request",
  "apply" to "To make use of"
)

// The API is clean and direct
trie["banana"] = "A long yellow fruit"
val value = trie["apple"] // "A fruit"

// Autocomplete is fast and efficient
val suggestions = trie.getAllWithPrefix("app")
// suggestions will be a Map of "apple", "application", "apply" to their values
```

#### Working with Generic Keys

For non-`String` keys, use the `genericTrieOf` / `mutableGenericTrieOf` builders.

The key type `K` must have a correct `equals`/`hashCode` implementation.
This is useful for cases like IP routing or bioinformatics.

**Example 1: IP Routing**

```kotlin
// Using a list of integers as a key for IP routing
val ipRouteTrie = genericTrieOf(
  listOf(10, 8, 0, 0) to "Local Network A",
  listOf(10, 8, 1, 0) to "Local Network B",
)

// Find all routes under the 10.8.0.0/16 prefix
val localRoutes = ipRouteTrie.getAllWithPrefix(listOf(10, 8))

// localRoutes will contain both "Local Network A" and "Local Network B"
```

**Example 2: Bioinformatics**

```kotlin
// Define a custom data class for DNA segments
data class DnaSegment(val base: Char)

// Create a Trie to store gene sequences
val geneTrie = genericTrieOf(
  listOf(DnaSegment('A'), DnaSegment('T'), DnaSegment('G')) to "Gene for Eye Color",
  listOf(DnaSegment('A'), DnaSegment('T'), DnaSegment('C')) to "Gene for Height",
)

// Find all genes that start with the sequence A -> T
val atPrefixGenes = geneTrie.getAllWithPrefix(
  listOf(DnaSegment('A'), DnaSegment('T')),
)

// atPrefixGenes will contain both "Gene for Eye Color" and "Gene for Height"
```

#### Basic Map Operations

Since `Trie` implements `Map` (and `MutableTrie` implements `MutableMap`), you can use all the standard map functions.

```kotlin
val trie = mutableTrieOf()

// Add a value
trie["hello"] = "A greeting"
trie["world"] = "The earth"

// Get a value
val greeting = trie["hello"] // "A greeting"

// Check for keys
println("world" in trie) // true

trie.remove("world") // "The earth"

trie.containsValue("foo") // false

trie.keys // ["hello", "world"]
trie.values // ["A greeting", "The earth"]
```

#### Prefix Searching (Autocomplete)

This is the core strength of a Trie. The `getAllWithPrefix()` method is perfect for autocomplete suggestions.

```kotlin
val trie = trieOf(
  "team" to 1,
  "tea" to 2,
  "teammate" to 3,
  "ten" to 4,
)

// Get all keys that start with "tea"
val suggestions =
  trie
    .getAllWithPrefix("tea")
    .keys

// suggestions will contain: ["tea", "team", "teammate"]
```

#### Choosing an Implementation

The library provides multiple implementations with different performance characteristics.
The best choice depends on your specific workload.

##### For `String` Keys

| Benchmark                                 | StandardStringTrie (`nonOptimizedTrieOf`) | CompactStringTrie (`trieOf`) | Winner                 |
|:------------------------------------------|:------------------------------------------|:-----------------------------|:-----------------------|
| **Creation Time (37 items)**              | ~2.10 µs                                  | **~1.06 µs**                 | **CompactStringTrie**  |
| **Prefix Search (Deeply Nested)**         | **~0.02 µs/op**                           | ~0.03 µs/op                  | **StandardStringTrie** |
| **Prefix Search (High Branching Factor)** | ~0.03 µs/op                               | **~0.02 µs/op**              | **CompactStringTrie**  |
| **Prefix Search (Long Shared Prefix)**    | ~0.15 µs/op                               | **~0.04 µs/op**              | **CompactStringTrie**  |
| **Prefix Search (No Shared Prefix)**      | ~0.02 µs/op                               | **~0.01 µs/op**              | **CompactStringTrie**  |
| **Autocomplete**                          | **~0.36 µs/op**                           | **~0.36 µs/op**              | **Tie**                |
| **Spell Check**                           | **~9 µs/op**                              | **~9 µs/op**                 | **Tie**                |
| **Removal (Deeply Nested)**               | **~0.02 µs/op**                           | ~0.03 µs/op                  | **StandardStringTrie** |
| **Removal (High Branching Factor)**       | **~0.02 µs/op**                           | **~0.02 µs/op**              | **Tie**                |
| **Removal (Long Shared)**                 | ~0.08 µs/op                               | **~0.04 µs/op**              | **CompactStringTrie**  |
| **Removal (No Shared)**                   | **~0.02 µs/op**                           | **~0.02 µs/op**              | **Tie**                |
| **Memory Usage (Node Count)**             | High                                      | **Low**                      | **CompactStringTrie**  |

##### For Generic Keys (e.g. `List<Int>`)

| Benchmark                                 | StandardGenericTrie (`genericTrieOf`) | CompactGenericTrie (`compactGenericTrieOf`) | Winner                  |
|:------------------------------------------|:--------------------------------------|:--------------------------------------------|:------------------------|
| **Creation Time (37 items)**              | **~0.60 µs**                          | **~0.60 µs**                                | **Tie**                 |
| **Prefix Search (Deeply Nested)**         | **~0.02 µs/op**                       | **~0.02 µs/op**                             | **Tie**                 |
| **Prefix Search (High Branching Factor)** | **~0.02 µs/op**                       | ~0.03 µs/op                                 | **StandardGenericTrie** |
| **Prefix Search (Long Shared Prefix)**    | **~0.03 µs/op**                       | **~0.03 µs/op**                             | **Tie**                 |
| **Prefix Search (No Shared Prefix)**      | **~0.01 µs/op**                       | **~0.01 µs/op**                             | **Tie**                 |
| **Autocomplete**                          | **~0.90 µs/op**                       | ~1.07 µs/op                                 | **StandardGenericTrie** |
| **Spell Check**                           | **~11 µs/op**                         | ~13 µs/op                                   | **StandardGenericTrie** |
| **Removal (Deeply Nested)**               | **~0.02 µs/op**                       | ~0.03 µs/op                                 | **StandardGenericTrie** |
| **Removal (High Branching Factor)**       | **~0.02 µs/op**                       | ~0.03 µs/op                                 | **StandardGenericTrie** |
| **Removal (Long Shared)**                 | **~0.02 µs/op**                       | ~0.04 µs/op                                 | **StandardGenericTrie** |
| **Removal (No Shared)**                   | **~0.01 µs/op**                       | **~0.01 µs/op**                             | **Tie**                 |
| **Memory Usage (Node Count)**             | High                                  | **Low**                                     | **CompactGenericTrie**  |

#### Recommendations

- **For `String` keys, use `CompactStringTrie` as your default.** It offers superior creation time, memory usage, and
  excels at the most common prefix search scenarios (long shared prefixes and high branching).
  Only consider `StandardStringTrie` if your application is dominated by individual lookups (`get`) and removals,
  and you have benchmarked it to be faster for your specific data.
- **For generic keys (`List<E>`), use `StandardGenericTrie` as your default**. The benchmarks show it has a clear and
  consistent performance advantage in nearly every category, including creation, lookups, prefix searches, and removals.
  The only reason to choose `CompactGenericTrie` is if **memory efficiency is your absolute highest priority** and you
  are willing to accept a performance trade-off.
