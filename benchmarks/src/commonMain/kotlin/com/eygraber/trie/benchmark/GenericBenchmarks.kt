package com.eygraber.trie.benchmark

import com.eygraber.trie.MutableTrie
import com.eygraber.trie.mutableCompactGenericTrieOf
import com.eygraber.trie.mutableGenericTrieOf
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
class GenericBenchmarks {
  private val dataset = BenchmarkData.GenericBased.realisticIntMix.toTypedArray()

  private lateinit var compactTrie: MutableTrie<List<Int>, Int>
  private lateinit var standardTrie: MutableTrie<List<Int>, Int>

  @Setup
  fun setup() {
    compactTrie = mutableCompactGenericTrieOf(*dataset)
    standardTrie = mutableGenericTrieOf(*dataset)
  }

  @Benchmark
  fun creationCompact(blackhole: Blackhole) {
    blackhole.consume(mutableCompactGenericTrieOf(*dataset))
  }

  @Benchmark
  fun creationStandard(blackhole: Blackhole) {
    blackhole.consume(mutableGenericTrieOf(*dataset))
  }

  @Benchmark
  fun prefixSearchLongSharedCompact(blackhole: Blackhole) {
    blackhole.consume(compactTrie.getAllValuesWithPrefix(listOf(10, 20, 30)))
  }

  @Benchmark
  fun prefixSearchLongSharedStandard(blackhole: Blackhole) {
    blackhole.consume(standardTrie.getAllValuesWithPrefix(listOf(10, 20, 30)))
  }

  @Benchmark
  fun prefixSearchNoSharedCompact(blackhole: Blackhole) {
    blackhole.consume(compactTrie.getAllValuesWithPrefix(listOf(2)))
  }

  @Benchmark
  fun prefixSearchNoSharedStandard(blackhole: Blackhole) {
    blackhole.consume(standardTrie.getAllValuesWithPrefix(listOf(2)))
  }

  @Benchmark
  fun prefixSearchHighBranchingFactorCompact(blackhole: Blackhole) {
    blackhole.consume(compactTrie.getAllValuesWithPrefix(listOf(192, 168, 1)))
  }

  @Benchmark
  fun prefixSearchHighBranchingFactorStandard(blackhole: Blackhole) {
    blackhole.consume(standardTrie.getAllValuesWithPrefix(listOf(192, 168, 1)))
  }

  @Benchmark
  fun prefixSearchDeeplyNestedCompact(blackhole: Blackhole) {
    blackhole.consume(compactTrie.getAllValuesWithPrefix(listOf(1, 2)))
  }

  @Benchmark
  fun prefixSearchDeeplyNestedStandard(blackhole: Blackhole) {
    blackhole.consume(standardTrie.getAllValuesWithPrefix(listOf(1, 2)))
  }

  @Benchmark
  fun removalLongSharedCompact(blackhole: Blackhole) {
    blackhole.consume(compactTrie.getAllValuesWithPrefix(listOf(10, 20, 30, 40, 51)))
  }

  @Benchmark
  fun removalLongSharedStandard(blackhole: Blackhole) {
    blackhole.consume(standardTrie.getAllValuesWithPrefix(listOf(10, 20, 30, 40, 51)))
  }

  @Benchmark
  fun removalNoSharedCompact(blackhole: Blackhole) {
    blackhole.consume(compactTrie.getAllValuesWithPrefix(listOf(3, 3, 3)))
  }

  @Benchmark
  fun removalNoSharedStandard(blackhole: Blackhole) {
    blackhole.consume(standardTrie.getAllValuesWithPrefix(listOf(3, 3, 3)))
  }

  @Benchmark
  fun removalHighBranchingFactorCompact(blackhole: Blackhole) {
    blackhole.consume(compactTrie.getAllValuesWithPrefix(listOf(192, 168, 2, 1)))
  }

  @Benchmark
  fun removalHighBranchingFactorStandard(blackhole: Blackhole) {
    blackhole.consume(standardTrie.getAllValuesWithPrefix(listOf(192, 168, 2, 1)))
  }

  @Benchmark
  fun removalDeeplyNestedCompact(blackhole: Blackhole) {
    blackhole.consume(compactTrie.getAllValuesWithPrefix(listOf(1, 2, 3)))
  }

  @Benchmark
  fun removalDeeplyNestedStandard(blackhole: Blackhole) {
    blackhole.consume(standardTrie.getAllValuesWithPrefix(listOf(1, 2, 3)))
  }
}
