package com.eygraber.trie.benchmark

import com.eygraber.trie.nonOptimizedTrieOf
import com.eygraber.trie.trieOf
import com.eygraber.trie.utils.CharAutoComplete
import com.eygraber.trie.utils.StringAutoComplete
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
class AutoCompleteBenchmarks {
  private val prefixToComplete = "inter"

  private lateinit var compactStringTrieAutoComplete: StringAutoComplete
  private lateinit var standardStringTrieAutoComplete: StringAutoComplete

  private lateinit var compactGenericTrieAutoComplete: CharAutoComplete
  private lateinit var standardGenericTrieAutoComplete: CharAutoComplete

  @Setup
  fun setup() {
    val dictionaryWords =
      BenchmarkData
        .StringBased
        .realisticStringMix
        .map { it.first }
        .toSet() // Use a set to ensure unique words

    val standardStringDictionary = nonOptimizedTrieOf(
      dictionaryWords.map { it to true },
    )

    val compactStringDictionary = trieOf(
      pairs = dictionaryWords.map { it to true },
    )

    val standardGenericDictionary = genericTrieOfString(
      *dictionaryWords.map { it to true }.toTypedArray(),
    )

    val compactGenericDictionary = compactGenericTrieOfString(
      *dictionaryWords.map { it to true }.toTypedArray(),
    )

    compactStringTrieAutoComplete = StringAutoComplete(compactStringDictionary)
    standardStringTrieAutoComplete = StringAutoComplete(standardStringDictionary)

    compactGenericTrieAutoComplete = CharAutoComplete(compactGenericDictionary)
    standardGenericTrieAutoComplete = CharAutoComplete(standardGenericDictionary)
  }

  @Benchmark
  fun genericCompact(blackhole: Blackhole) {
    blackhole.consume(compactGenericTrieAutoComplete.suggest(prefixToComplete))
  }

  @Benchmark
  fun genericStandard(blackhole: Blackhole) {
    blackhole.consume(standardGenericTrieAutoComplete.suggest(prefixToComplete))
  }

  @Benchmark
  fun stringCompact(blackhole: Blackhole) {
    blackhole.consume(compactStringTrieAutoComplete.suggest(prefixToComplete))
  }

  @Benchmark
  fun stringStandard(blackhole: Blackhole) {
    blackhole.consume(standardStringTrieAutoComplete.suggest(prefixToComplete))
  }
}
