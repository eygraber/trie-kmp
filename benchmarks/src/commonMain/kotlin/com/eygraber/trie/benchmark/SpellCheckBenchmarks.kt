package com.eygraber.trie.benchmark

import com.eygraber.trie.mutableNonOptimizedTrieOf
import com.eygraber.trie.mutableTrieOf
import com.eygraber.trie.utils.CharSpellChecker
import com.eygraber.trie.utils.StringSpellChecker
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
class SpellCheckBenchmarks {
  private val wordToCorrect = "aple"

  private lateinit var compactStringTrieSpellChecker: StringSpellChecker
  private lateinit var standardStringTrieSpellChecker: StringSpellChecker

  private lateinit var compactGenericTrieSpellChecker: CharSpellChecker
  private lateinit var standardGenericTrieSpellChecker: CharSpellChecker

  @Setup
  fun setup() {
    val dictionaryWords =
      BenchmarkData
        .StringBased
        .realisticStringMix
        .map { it.first }
        .toSet() // Use a set to ensure unique words

    val standardStringDictionary = mutableNonOptimizedTrieOf(
      *dictionaryWords.map { it to true }.toTypedArray(),
    )

    val compactStringDictionary = mutableTrieOf(
      pairs = dictionaryWords.map { it to true }.toTypedArray(),
      useSaferImplementationForRemovals = false,
    )

    val standardGenericDictionary = mutableGenericTrieOfString(
      *dictionaryWords.map { it to true }.toTypedArray(),
    )

    val compactGenericDictionary = mutableCompactGenericTrieOfString(
      *dictionaryWords.map { it to true }.toTypedArray(),
    )

    compactStringTrieSpellChecker = StringSpellChecker(compactStringDictionary)
    standardStringTrieSpellChecker = StringSpellChecker(standardStringDictionary)

    compactGenericTrieSpellChecker = CharSpellChecker(compactGenericDictionary)
    standardGenericTrieSpellChecker = CharSpellChecker(standardGenericDictionary)
  }

  @Benchmark
  fun genericCompact(blackhole: Blackhole) {
    blackhole.consume(compactGenericTrieSpellChecker.suggest(wordToCorrect))
  }

  @Benchmark
  fun genericStandard(blackhole: Blackhole) {
    blackhole.consume(standardGenericTrieSpellChecker.suggest(wordToCorrect))
  }

  @Benchmark
  fun stringCompact(blackhole: Blackhole) {
    blackhole.consume(compactStringTrieSpellChecker.suggest(wordToCorrect))
  }

  @Benchmark
  fun stringStandardTrie(blackhole: Blackhole) {
    blackhole.consume(standardStringTrieSpellChecker.suggest(wordToCorrect))
  }
}
