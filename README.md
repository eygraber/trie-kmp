# Trie KMP

trie-kmp is a lightweight, efficient, and idiomatic library providing Trie data structures for Kotlin Multiplatform

The library offers two primary implementations:

 - CompactTrie: A memory-efficient Trie that compresses non-branching paths.
 - MapTrie: A classic, standard Trie.

This allows developers to choose the optimal data structure for their specific use case, whether it's for
autocomplete systems, spell checkers, IP routing tables, or any other prefix-based application.

### Setup

```
repositories {
  mavenCentral()
}

dependencies {
  implementation("com.eygraber:trie-kmp:0.0.1")
}
```

Snapshots can be found [here](https://central.sonatype.org/publish/publish-portal-snapshots/#consuming-via-gradle).

### Usage

#### Creating a Trie

Use the `mutableTrieOf()` and `mutableCompactTrieOf()` builder functions for easy instantiation.

```kotlin
// Create a standard Trie
val standardTrie = mutableTrieOf(
    "apple" to "A fruit",
    "apply" to "To make a formal request"
)

// Create a memory-efficient compact Trie
val compactTrie = mutableCompactTrieOf(
    "banana" to "A long yellow fruit"
)
```

#### Basic Map Operations

Since both Tries implement `MutableMap`, you can use all the standard map functions.

```kotlin
val trie = mutableCompactTrieOf<String>()

// Add a value (using the String extension)
trie["hello"] = "A greeting"
trie["world"] = "The earth"

// Get a value
val greeting = trie["hello"] // "A greeting"

// Check for keys
println("world" in trie) // true
```

#### Prefix Searching (Autocomplete)

This is the core strength of a Trie. The `getAllWithPrefix()` method is perfect for autocomplete suggestions.

```kotlin
val trie = compactTrieOf(
    "team" to 1,
    "tea" to 2,
    "teammate" to 3,
    "ten" to 4
)

// Get all keys that start with "tea"
val suggestions = 
  trie
    .getAllWithPrefix("tea")
    .keys
    .map { it.joinToString("") } // Convert List<Char> back to String

// suggestions will contain: ["tea", "team", "teammate"]
```

#### Working with Generic Keys

While `String` keys are common, the library is fully generic. You can use a `List<E>` as a key, as long as `E` has
a correct `equals`/`hashCode` implementation. This is useful for cases like IP routing or bioinformatics.

**Use Case 1: IP Routing**

```kotlin
// Using a list of integers as a key for IP routing
val ipRouteTrie = genericCompactTrieOf(
  listOf(10, 8, 0, 0) to "Local Network A",
  listOf(10, 8, 1, 0) to "Local Network B",
)

// Find all routes under the 10.8.0.0/16 prefix
val localRoutes = ipRouteTrie.getAllWithPrefix(listOf(10, 8))

// localRoutes will contain both "Local Network A" and "Local Network B"
```

**Use Case 2: Bioinformatics**

```kotlin
// Define a custom data class for DNA segments
data class DnaSegment(val base: Char)

// Create a Trie to store gene sequences
val geneTrie = genericCompactTrieOf(
  listOf(DnaSegment('A'), DnaSegment('T'), DnaSegment('G')) to "Gene for Eye Color",
  listOf(DnaSegment('A'), DnaSegment('T'), DnaSegment('C')) to "Gene for Height",
)

// Find all genes that start with the sequence A -> T
val atPrefixGenes = geneTrie.getAllWithPrefix(
    listOf(DnaSegment('A'), DnaSegment('T'))
)

// atPrefixGenes will contain both "Gene for Eye Color" and "Gene for Height"

```

#### Choosing an Implementation: `MapTrie` vs. `CompactTrie`

The library provides two implementations with different performance characteristics.
The best choice depends on your specific workload.

| Benchmark                            | MapTrie (`mutableTrieOf`) | CompactTrie (`mutableCompactTrieOf`) | Winner                       | 
|:-------------------------------------|:--------------------------|:-------------------------------------|:-----------------------------| 
| **Prefix Search / Autocomplete**     | ~0.14 ms/op               | ~0.18 ms/op                          | **Map Trie** (Slightly)      | 
| **Individual Lookups (Spell Check)** | **~11.7 ms/op**           | ~18.0 ms/op                          | **Map Trie**                 | 
| **String Creation Time**             | ~23.4 ms/op               | **~18.5 ms/op**                      | **Compact Trie**             |
| **Basic String Prefix Search**       | ~0.258 ms/op              | **~0.249 ms/op**                     | **Compact Trie**  (Slightly) |
| **Object Creation Time**             | ~30.787 ms/op             | **~48.204 ms/op**                    | **Map Trie**                 |
| **Basic Object Prefix Search**       | ~0.642 ms/op              | **~.637 ms/op**                      | **Compact Trie**  (Slightly) |
| **Memory Usage (Node Count)**        | High                      | **Low**                              | **Compact Trie**             |

#### Recommendations

 - Use `CompactTrie` when:
     - **Memory efficiency is your top priority**. This is the biggest advantage of the compact implementation. 
        It uses significantly fewer nodes, making it ideal for very large datasets or environments with memory constraints.
     - Your keys have long, shared prefixes (like URLs, file paths, or package names).
     - Creation time for large sets of `String` keys is important.
 - Use `MapTrie` when:
     - **Your primary workload consists of a massive number of individual key lookups** (`get`, `containsKey`).
       The simpler logic of the standard Trie makes its per-operation cost lower, which adds up in lookup-heavy 
       algorithms like the spell-check benchmark.
     - You can afford the higher memory usage.
     - You prefer a simpler, more readable implementation for easier debugging or educational purposes.

**General Guideline: For most common use cases like **autocomplete**, the performance is very close. Start with
`CompactTrie` for its superior memory profile. Only switch to the `MapTrie` if benchmarks of your specific application
show that you are bottlenecked by a very high volume of individual `get`/`containsKey` calls.
