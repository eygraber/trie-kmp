package com.eygraber.trie.benchmark

import com.eygraber.trie.mutableCompactTrieOf
import com.eygraber.trie.mutableTrieOf
import com.eygraber.trie.utils.SpellChecker
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
class SpellCheckBenchmarks {
  private val charPool: List<Char> = ('a'..'z').toList()
  private val wordLength = 8
  private val dictionarySize = 10_000
  private val wordsToCheckCount = 200

  private lateinit var wordsToCorrect: List<String>
  private lateinit var standardTrieSpellChecker: SpellChecker
  private lateinit var compactTrieSpellChecker: SpellChecker

  @Setup
  fun setup() {
    val random = Random(42)
    val dictionaryWords = List(dictionarySize) {
      (1..wordLength).map { charPool.random(random) }.joinToString("")
    }.toSet() // Use a set to ensure unique words

    val standardDictionary = mutableTrieOf(*dictionaryWords.map { it to true }.toTypedArray())
    val compactDictionary = mutableCompactTrieOf(*dictionaryWords.map { it to true }.toTypedArray())

    standardTrieSpellChecker = SpellChecker(standardDictionary)
    compactTrieSpellChecker = SpellChecker(compactDictionary)

    wordsToCorrect = List(wordsToCheckCount) {
      val originalWord = dictionaryWords.random(random)
      // Apply one deletion to create a misspelling
      if(originalWord.isNotEmpty()) {
        val deletionIndex = random.nextInt(originalWord.length)
        originalWord.removeRange(deletionIndex, deletionIndex + 1)
      }
      else {
        "a" // fallback for empty string case
      }
    }
  }

  @Benchmark
  fun spellCheckTrie(blackhole: Blackhole) {
    for(word in wordsToCorrect) {
      blackhole.consume(standardTrieSpellChecker.suggest(word))
    }
  }

  @Benchmark
  fun spellCheckCompactTrie(blackhole: Blackhole) {
    for(word in wordsToCorrect) {
      blackhole.consume(compactTrieSpellChecker.suggest(word))
    }
  }
}
