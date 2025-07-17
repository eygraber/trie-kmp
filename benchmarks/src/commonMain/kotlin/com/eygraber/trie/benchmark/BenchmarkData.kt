package com.eygraber.trie.benchmark

/**
 * A collection of deterministic datasets for benchmarking Trie implementations.
 * Using this data ensures that benchmark results are consistent and comparable
 * across different runs and code versions.
 */
object BenchmarkData {
  object StringBased {
    /**
     * **Scenario:** Long shared prefixes.
     * **Designed to test:** The best case for a compact Trie, highlighting
     * the benefits of path compression.
     */
    val longSharedPrefixStrings: List<Pair<String, Int>> = listOf(
      "international" to 0,
      "internationalization" to 1,
      "interstellar" to 2,
      "interview" to 3,
      "internet" to 4,
      "interpretation" to 5,
      "interruption" to 6,
    )

    /**
     * **Scenario:** No shared prefixes.
     * **Designed to test:** The worst case for a Trie, where its structure provides
     * little advantage over a standard HashMap.
     */
    val noSharedPrefixStrings: List<Pair<String, Int>> = listOf(
      "apple" to 0,
      "banana" to 1,
      "cherry" to 2,
      "date" to 3,
      "elderberry" to 4,
      "fig" to 5,
      "grape" to 6,
    )

    /**
     * **Scenario:** High branching factor.
     * **Designed to test:** The performance of node creation and child lookups when
     * a short prefix is shared by many keys.
     */
    val highBranchingFactorStrings: List<Pair<String, Int>> = listOf(
      "tea" to 0,
      "ted" to 1,
      "tee" to 2,
      "ten" to 3,
      "tell" to 4,
      "temp" to 5,
      "test" to 6,
    )

    /**
     * **Scenario:** Deeply nested keys where keys are prefixes of other keys.
     * **Designed to test:** The handling of nodes that are both terminal (have a value)
     * and have children.
     */
    val deeplyNestedStrings: List<Pair<String, Int>> = listOf(
      "a" to 0,
      "ap" to 1,
      "app" to 2,
      "appl" to 3,
      "apple" to 4,
    )

    /**
     * A large, realistic mix of different string patterns to simulate a
     * real world dictionary.
     */
    val realisticStringMix: List<Pair<String, Int>> = (
      longSharedPrefixStrings +
        noSharedPrefixStrings +
        highBranchingFactorStrings +
        deeplyNestedStrings +
        listOf("a", "b", "c", "cat", "dog", "drought", "i", "in", "inter", "z", "zebra")
          .mapIndexed { index, s -> s to index + 100 }
      ).distinctBy { it.first }
  }

  object GenericBased {
    /**
     * **Scenario:** Long shared prefixes with integer keys.
     */
    val longSharedPrefixInts: List<Pair<List<Int>, Int>> = listOf(
      listOf(10, 20, 30, 40, 50) to 0,
      listOf(10, 20, 30, 40, 51) to 1,
      listOf(10, 20, 30, 41, 0) to 2,
      listOf(10, 20, 33, 0, 0) to 3,
    )

    /**
     * **Scenario:** No shared prefixes with integer keys.
     */
    val noSharedPrefixInts: List<Pair<List<Int>, Int>> = listOf(
      listOf(1, 1, 1) to 0,
      listOf(2, 2, 2) to 1,
      listOf(3, 3, 3) to 2,
      listOf(4, 4, 4) to 3,
    )

    /**
     * **Scenario:** High branching factor with integer keys.
     */
    val highBranchingFactorInts: List<Pair<List<Int>, Int>> = listOf(
      listOf(192, 168, 1, 1) to 0,
      listOf(192, 168, 1, 2) to 1,
      listOf(192, 168, 1, 3) to 2,
      listOf(192, 168, 2, 1) to 3,
      listOf(192, 168, 2, 2) to 4,
    )

    /**
     * **Scenario:** Deeply nested integer keys.
     */
    val deeplyNestedInts: List<Pair<List<Int>, Int>> = listOf(
      listOf(1) to 0,
      listOf(1, 2) to 1,
      listOf(1, 2, 3) to 2,
      listOf(1, 2, 3, 4) to 3,
    )

    /**
     * A large, realistic mix of different integer list patterns.
     */
    val realisticIntMix: List<Pair<List<Int>, Int>> = (
      longSharedPrefixInts +
        noSharedPrefixInts +
        highBranchingFactorInts +
        deeplyNestedInts
      ).distinctBy { it.first }
  }
}
