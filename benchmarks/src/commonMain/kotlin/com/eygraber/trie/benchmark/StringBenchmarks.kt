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
  private val dataset = BenchmarkData.StringBased.realisticStringMix

  private lateinit var compactTrie: MutableTrie<String, Int>
  private lateinit var standardTrie: MutableTrie<String, Int>

  @Setup
  fun setup() {
    compactTrie = mutableTrieOf(dataset, useSaferImplementationForRemovals = false)
    standardTrie = mutableNonOptimizedTrieOf(dataset)
  }

  @Benchmark
  fun creationCompact(blackhole: Blackhole) {
    blackhole.consume(mutableTrieOf(dataset, useSaferImplementationForRemovals = false))
  }

  @Benchmark
  fun creationStandard(blackhole: Blackhole) {
    blackhole.consume(mutableNonOptimizedTrieOf(dataset))
  }

  @Benchmark
  fun prefixSearchLongSharedCompact(blackhole: Blackhole) {
    blackhole.consume(compactTrie.getAllValuesWithPrefix("inter"))
  }

  @Benchmark
  fun prefixSearchLongSharedStandard(blackhole: Blackhole) {
    blackhole.consume(standardTrie.getAllValuesWithPrefix("inter"))
  }

  @Benchmark
  fun prefixSearchNoSharedCompact(blackhole: Blackhole) {
    blackhole.consume(compactTrie.getAllValuesWithPrefix("bana"))
  }

  @Benchmark
  fun prefixSearchNoSharedStandard(blackhole: Blackhole) {
    blackhole.consume(standardTrie.getAllValuesWithPrefix("bana"))
  }

  @Benchmark
  fun prefixSearchHighBranchingFactorCompact(blackhole: Blackhole) {
    blackhole.consume(compactTrie.getAllValuesWithPrefix("te"))
  }

  @Benchmark
  fun prefixSearchHighBranchingFactorStandard(blackhole: Blackhole) {
    blackhole.consume(standardTrie.getAllValuesWithPrefix("te"))
  }

  @Benchmark
  fun prefixSearchDeeplyNestedCompact(blackhole: Blackhole) {
    blackhole.consume(compactTrie.getAllValuesWithPrefix("app"))
  }

  @Benchmark
  fun prefixSearchDeeplyNestedStandard(blackhole: Blackhole) {
    blackhole.consume(standardTrie.getAllValuesWithPrefix("app"))
  }

  @Benchmark
  fun removalLongSharedCompact(blackhole: Blackhole) {
    blackhole.consume(compactTrie.getAllValuesWithPrefix("internationalization"))
  }

  @Benchmark
  fun removalLongSharedStandard(blackhole: Blackhole) {
    blackhole.consume(standardTrie.getAllValuesWithPrefix("internationalization"))
  }

  @Benchmark
  fun removalNoSharedCompact(blackhole: Blackhole) {
    blackhole.consume(compactTrie.getAllValuesWithPrefix("cherry"))
  }

  @Benchmark
  fun removalNoSharedStandard(blackhole: Blackhole) {
    blackhole.consume(standardTrie.getAllValuesWithPrefix("cherry"))
  }

  @Benchmark
  fun removalHighBranchingFactorCompact(blackhole: Blackhole) {
    blackhole.consume(compactTrie.getAllValuesWithPrefix("tell"))
  }

  @Benchmark
  fun removalHighBranchingFactorStandard(blackhole: Blackhole) {
    blackhole.consume(standardTrie.getAllValuesWithPrefix("tell"))
  }

  @Benchmark
  fun removalDeeplyNestedCompact(blackhole: Blackhole) {
    blackhole.consume(compactTrie.getAllValuesWithPrefix("appl"))
  }

  @Benchmark
  fun removalDeeplyNestedStandard(blackhole: Blackhole) {
    blackhole.consume(standardTrie.getAllValuesWithPrefix("appl"))
  }
}
