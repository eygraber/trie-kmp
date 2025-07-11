package com.eygraber.trie.benchmark

import com.eygraber.trie.mutableCompactTrieOf
import com.eygraber.trie.mutableTrieOf
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
import kotlin.random.Random

@State(Scope.Benchmark)
@Warmup(iterations = 5, time = 1, timeUnit = BenchmarkTimeUnit.SECONDS)
@Measurement(iterations = 10, time = 1, timeUnit = BenchmarkTimeUnit.SECONDS)
@OutputTimeUnit(BenchmarkTimeUnit.MILLISECONDS)
@BenchmarkMode(Mode.AverageTime)
class AutoCompleteBenchmarks {
  private val charPool: List<Char> = ('a'..'z').toList()
  private val wordLength = 10
  private val dictionarySize = 20_000
  private val prefixesToCompleteCount = 500

  private lateinit var prefixesToComplete: List<String>

  private lateinit var compactStringTrieAutoComplete: StringAutoComplete<Boolean>
  private lateinit var standardStringTrieAutoComplete: StringAutoComplete<Boolean>

  private lateinit var compactGenericTrieAutoComplete: CharAutoComplete<Boolean>
  private lateinit var standardGenericTrieAutoComplete: CharAutoComplete<Boolean>

  @Setup
  fun setup() {
    val random = Random(42)
    val dictionaryWords = List(dictionarySize) {
      (1..wordLength).map { charPool.random(random) }.joinToString("")
    }.toSet()

    val standardStringDictionary = mutableTrieOf(
      *dictionaryWords.map { it to true }.toTypedArray(),
    )

    val compactStringDictionary = mutableCompactTrieOf(
      *dictionaryWords.map { it to true }.toTypedArray(),
    )

    val standardGenericDictionary = mutableGenericTrieOfString(
      *dictionaryWords.map { it to true }.toTypedArray(),
    )

    val compactGenericDictionary = mutableCompactGenericTrieOfString(
      *dictionaryWords.map { it to true }.toTypedArray(),
    )

    compactStringTrieAutoComplete = StringAutoComplete(compactStringDictionary)
    standardStringTrieAutoComplete = StringAutoComplete(standardStringDictionary)

    compactGenericTrieAutoComplete = CharAutoComplete(compactGenericDictionary)
    standardGenericTrieAutoComplete = CharAutoComplete(standardGenericDictionary)

    prefixesToComplete = List(prefixesToCompleteCount) {
      val word = dictionaryWords.random(random)
      word.substring(0, word.length / 2)
    }
  }

  @Benchmark
  fun autoCompleteCompactStringTrie(blackhole: Blackhole) {
    for(prefix in prefixesToComplete) {
      blackhole.consume(compactStringTrieAutoComplete.suggest(prefix))
    }
  }

  @Benchmark
  fun autoCompleteCompactGenericTrie(blackhole: Blackhole) {
    for(prefix in prefixesToComplete) {
      blackhole.consume(compactGenericTrieAutoComplete.suggest(prefix))
    }
  }

  @Benchmark
  fun autoCompleteStandardStringTrie(blackhole: Blackhole) {
    for(prefix in prefixesToComplete) {
      blackhole.consume(standardStringTrieAutoComplete.suggest(prefix))
    }
  }

  @Benchmark
  fun autoCompleteStandardGenericTrie(blackhole: Blackhole) {
    for(prefix in prefixesToComplete) {
      blackhole.consume(standardGenericTrieAutoComplete.suggest(prefix))
    }
  }
}
