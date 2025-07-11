package com.eygraber.trie.benchmark

import com.eygraber.trie.MutableTrie
import com.eygraber.trie.mutableCompactTrieOf
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
import kotlin.random.Random

@State(Scope.Benchmark)
@Warmup(iterations = 10, time = 500, timeUnit = BenchmarkTimeUnit.MILLISECONDS)
@Measurement(iterations = 20, time = 1, timeUnit = BenchmarkTimeUnit.SECONDS)
@OutputTimeUnit(BenchmarkTimeUnit.MILLISECONDS)
@BenchmarkMode(Mode.AverageTime)
class StringTrieBenchmarks {
  private val charPool: List<Char> = ('a'..'z') + ('A'..'Z')
  private val wordLength = 10
  private val datasetSize = 50_000 // Number of words in our dataset

  private lateinit var dataset: List<Pair<String, Int>>
  private lateinit var prefixes: List<String>
  private lateinit var removals: List<String>

  private lateinit var compactTrie: MutableTrie<String, Int>
  private lateinit var standardTrie: MutableTrie<String, Int>

  @Setup
  fun setup() {
    val random = Random(42)
    dataset = List(datasetSize) { index ->
      val randomWord = (1..wordLength)
        .map { _ -> random.nextInt(0, charPool.size).let { charPool[it] } }
        .joinToString("")
      randomWord to index
    }

    standardTrie = mutableTrieOf(*dataset.toTypedArray())
    compactTrie = mutableCompactTrieOf(*dataset.toTypedArray())

    prefixes = List(1000) {
      val word = dataset[random.nextInt(0, datasetSize)].first
      word.substring(0, word.length / 2)
    }

    removals = List(1000) {
      dataset[random.nextInt(0, datasetSize)].first
    }
  }

  @Benchmark
  fun compactStringTrieCreation(blackhole: Blackhole) {
    blackhole.consume(mutableCompactTrieOf(*dataset.toTypedArray()))
  }

  @Benchmark
  fun standardStringTrieCreation(blackhole: Blackhole) {
    blackhole.consume(mutableTrieOf(*dataset.toTypedArray()))
  }

  @Benchmark
  fun compactStringTriePrefixSearch(): List<Int> {
    val results = mutableListOf<Int>()
    for(prefix in prefixes) {
      results.addAll(compactTrie.getAllValuesWithPrefix(prefix))
    }
    return results
  }

  @Benchmark
  fun standardStringTriePrefixSearch(): List<Int> {
    val results = mutableListOf<Int>()
    for(prefix in prefixes) {
      results.addAll(standardTrie.getAllValuesWithPrefix(prefix))
    }
    return results
  }

  @Benchmark
  fun compactStringTrieRemoval(blackhole: Blackhole) {
    for(removal in removals) {
      blackhole.consume(compactTrie.remove(removal))
    }
  }

  @Benchmark
  fun standardStringTrieRemoval(blackhole: Blackhole) {
    for(removal in removals) {
      blackhole.consume(standardTrie.remove(removal))
    }
  }
}
