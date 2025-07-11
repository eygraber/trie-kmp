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
  implementation("com.eygraber:trie-kmp:0.0.2")
}
```

Snapshots can be found [here](https://central.sonatype.org/publish/publish-portal-snapshots/#consuming-via-gradle).

### Usage

#### Recommended Usage for String

For any `String`-based use case, use the optimized `compactTrieOf` / `mutableCompactTrieOf` builders.

```kotlin
// Create a high-performance, memory-efficient compact Trie for Strings
val trie = mutableCompactTrieOf(
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

For non-`String` keys, use the optimized `compactGenericTrieOf` / `mutableCompactGenericTrieOf` builders.

The key type `K` must have a correct `equals`/`hashCode` implementation.
This is useful for cases like IP routing or bioinformatics.

**Example 1: IP Routing**

```kotlin
// Using a list of integers as a key for IP routing
val ipRouteTrie = compactGenericTrieOf(
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
val geneTrie = compactGenericTrieOf(
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
val trie = mutableCompactTrieOf()

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
val trie = compactTrieOf(
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

| Benchmark                            | StandardStringTrie (`trieOf`) | CompactStringTrie (`compactTrieOf`) | Winner                             | 
|:-------------------------------------|:------------------------------|:------------------------------------|:-----------------------------------| 
| **Creation Time**                    | ~11.8 ms/op                   | **~5.1 ms/op**                      | **CompactStringTrie**              | 
| **Prefix Search / Autocomplete**     | ~0.21 ms/op                   | **~0.09 ms/op**                     | **CompactStringTrie**              | 
| **Individual Lookups (Spell Check)** | **~5.6 ms/op**                | ~7.0 ms/op                          | **StandardStringTrie**             |
| **Removal**                          | **~0.04 ms/op**               | ~0.05 ms/op                         | **StandardStringTrie**  (Slightly) |
| **Memory Usage (Node Count)**        | High                          | **Low**                             | **CompactStringTrie**              |

##### For Generic Keys (e.g. `List<Int>`)

| Benchmark                     | StandardGenericTrie (`genericTrieOf`) | CompactGenericTrie (`compactGenericTrieOf`) | Winner                  | 
|:------------------------------|:--------------------------------------|:--------------------------------------------|:------------------------| 
| **Creation Time**             | **~23.4 ms/op**                       | ~37.3 ms/op                                 | **StandardGenericTrie** | 
| **Prefix Search**             | ~0.58 ms/op                           | **~0.49 ms/op**                             | **CompactGenericTrie**  | 
| **Removal**                   | **~0.16 ms/op**                       | ~0.58 ms/op                                 | **StandardGenericTrie** |
| **Memory Usage (Node Count)** | High                                  | **Low**                                     | **CompactGenericTrie**  |

#### Recommendations

 - **For `String` keys, always use the specialized implementation.** They are dramatically faster than the generic
   `List<Char>` versions.
 - **Use `CompactStringTrie` as your default choice for `String` keys.** It is significantly faster at creation and 
   prefix searches (the most common Trie operations) and is vastly more memory-efficient. The very slight performance
   loss in individual lookups is a small price to pay for the huge gains elsewhere.
 - **Use `StandardStringTrie` only in very specific, benchmarked scenarios.** If your application almost exclusively
   performs `remove` or individual `get` / `containsKey` operations, and does very few prefix searches or insertions,
   it might be marginally faster.
 - **For generic keys (e.g. `List<Int>`), the trade-offs are clearer.**
   - Choose `StandardGenericTrie` if your workload is heavy on **creation and removal**.
   - Choose `CompactGenericTrie` if **memory usage and prefix search performance** are your main concerns.
