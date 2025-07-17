package com.eygraber.trie.benchmark

import com.eygraber.trie.MutableTrie
import com.eygraber.trie.mutableNonOptimizedTrieOf
import com.eygraber.trie.mutableTrieOf
import kotlinx.benchmark.Benchmark
import kotlinx.benchmark.BenchmarkMode
import kotlinx.benchmark.BenchmarkTimeUnit
import kotlinx.benchmark.Blackhole
import kotlinx.benchmark.Measurement
import kotlinx.benchmark.Mode
import kotlinx.benchmark.OutputTimeUnit
import kotlinx.benchmark.Scope
import kotlinx.benchmark.Setup
import kotlinx.benchmark.State
import kotlinx.benchmark.Warmup

@State(Scope.Benchmark)
@Warmup(iterations = 5, time = 1, timeUnit = BenchmarkTimeUnit.SECONDS)
@Measurement(iterations = 20, time = 1, timeUnit = BenchmarkTimeUnit.SECONDS)
@OutputTimeUnit(BenchmarkTimeUnit.MICROSECONDS)
@BenchmarkMode(Mode.AverageTime)
class StringBenchmarks {
  private val dataset = BenchmarkData.StringBased.realisticStringMix.toTypedArray()

  private lateinit var compactTrie: MutableTrie<String, Int>
  private lateinit var standardTrie: MutableTrie<String, Int>

  @Setup
  fun setup() {
    compactTrie = mutableTrieOf(*dataset, useSaferImplementationForRemovals = false)
    standardTrie = mutableNonOptimizedTrieOf(*dataset)
  }

  @Benchmark
  fun creationCompact(blackhole: Blackhole) {
    blackhole.consume(mutableTrieOf(*dataset, useSaferImplementationForRemovals = false))
  }

  @Benchmark
  fun creationStandard(blackhole: Blackhole) {
    blackhole.consume(mutableNonOptimizedTrieOf(*dataset))
  }

  @Benchmark
  fun longSharedPrefixSearchCompact(blackhole: Blackhole) {
    blackhole.consume(compactTrie.getAllValuesWithPrefix("inter"))
  }

  @Benchmark
  fun longSharedPrefixSearchStandard(blackhole: Blackhole) {
    blackhole.consume(standardTrie.getAllValuesWithPrefix("inter"))
  }

  @Benchmark
  fun noSharedPrefixSearchCompact(blackhole: Blackhole) {
    blackhole.consume(compactTrie.getAllValuesWithPrefix("bana"))
  }

  @Benchmark
  fun noSharedPrefixSearchStandard(blackhole: Blackhole) {
    blackhole.consume(standardTrie.getAllValuesWithPrefix("bana"))
  }

  @Benchmark
  fun highBranchingFactorPrefixSearchCompact(blackhole: Blackhole) {
    blackhole.consume(compactTrie.getAllValuesWithPrefix("te"))
  }

  @Benchmark
  fun highBranchingFactorPrefixSearchStandard(blackhole: Blackhole) {
    blackhole.consume(standardTrie.getAllValuesWithPrefix("te"))
  }

  @Benchmark
  fun deeplyNestedPrefixSearchCompact(blackhole: Blackhole) {
    blackhole.consume(compactTrie.getAllValuesWithPrefix("app"))
  }

  @Benchmark
  fun deeplyNestedPrefixSearchStandard(blackhole: Blackhole) {
    blackhole.consume(standardTrie.getAllValuesWithPrefix("app"))
  }

  @Benchmark
  fun longSharedRemovalCompact(blackhole: Blackhole) {
    blackhole.consume(compactTrie.getAllValuesWithPrefix("internationalization"))
  }

  @Benchmark
  fun longSharedRemovalStandard(blackhole: Blackhole) {
    blackhole.consume(standardTrie.getAllValuesWithPrefix("internationalization"))
  }

  @Benchmark
  fun noSharedRemovalCompact(blackhole: Blackhole) {
    blackhole.consume(compactTrie.getAllValuesWithPrefix("cherry"))
  }

  @Benchmark
  fun noSharedRemovalStandard(blackhole: Blackhole) {
    blackhole.consume(standardTrie.getAllValuesWithPrefix("cherry"))
  }

  @Benchmark
  fun highBranchingFactorRemovalCompact(blackhole: Blackhole) {
    blackhole.consume(compactTrie.getAllValuesWithPrefix("tell"))
  }

  @Benchmark
  fun highBranchingFactorRemovalStandard(blackhole: Blackhole) {
    blackhole.consume(standardTrie.getAllValuesWithPrefix("tell"))
  }

  @Benchmark
  fun deeplyNestedRemovalCompact(blackhole: Blackhole) {
    blackhole.consume(compactTrie.getAllValuesWithPrefix("appl"))
  }

  @Benchmark
  fun deeplyNestedRemovalStandard(blackhole: Blackhole) {
    blackhole.consume(standardTrie.getAllValuesWithPrefix("appl"))
  }
}
