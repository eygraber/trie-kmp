package com.eygraber.trie.benchmark

import com.eygraber.trie.Trie
import com.eygraber.trie.mutableGenericCompactTrieOf
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
import kotlin.random.Random

data class DnaSegment(
  val value: Char,
)

@Suppress("PrivatePropertyName", "VariableNaming")
@State(Scope.Benchmark)
@Warmup(iterations = 10, time = 500, timeUnit = BenchmarkTimeUnit.MILLISECONDS)
@Measurement(iterations = 20, time = 1, timeUnit = BenchmarkTimeUnit.SECONDS)
@OutputTimeUnit(BenchmarkTimeUnit.MILLISECONDS)
@BenchmarkMode(Mode.AverageTime)
class TrieCustomBenchmarks {
  private val A = DnaSegment('A')
  private val C = DnaSegment('C')
  private val G = DnaSegment('G')
  private val T = DnaSegment('T')

  private val dnaPool: List<DnaSegment> = listOf(A, C, G, T)
  private val geneLength = 15
  private val datasetSize = 50_000

  private lateinit var dataset: List<Pair<List<DnaSegment>, Int>>
  private lateinit var prefixes: List<List<DnaSegment>>

  private lateinit var trie: Trie<DnaSegment, Int>
  private lateinit var compactTrie: Trie<DnaSegment, Int>
  private lateinit var hashMap: Map<List<DnaSegment>, Int>
  private lateinit var list: List<Pair<List<DnaSegment>, Int>>

  @Setup
  fun setup() {
    val random = Random(42)
    dataset = List(datasetSize) { index ->
      val randomGene = (1..geneLength)
        .map { _ -> random.nextInt(0, dnaPool.size).let { dnaPool[it] } }
      randomGene to index
    }

    trie = mutableGenericTrieOf(*dataset.toTypedArray())
    compactTrie = mutableGenericCompactTrieOf(*dataset.toTypedArray())
    hashMap = dataset.toMap()
    list = dataset

    prefixes = List(1000) {
      val gene = dataset[random.nextInt(0, datasetSize)].first
      gene.subList(0, gene.size / 2)
    }
  }

  @Benchmark
  fun customTrieCreation(blackhole: Blackhole) {
    blackhole.consume(mutableGenericTrieOf(*dataset.toTypedArray()))
  }

  @Benchmark
  fun customCompactTrieCreation(blackhole: Blackhole) {
    blackhole.consume(mutableGenericCompactTrieOf(*dataset.toTypedArray()))
  }

  @Benchmark
  fun customTriePrefixSearch(): List<Int> {
    val results = mutableListOf<Int>()
    for(prefix in prefixes) {
      results.addAll(trie.getAllValuesWithPrefix(prefix))
    }
    return results
  }

  @Benchmark
  fun customCompactTriePrefixSearch(): List<Int> {
    val results = mutableListOf<Int>()
    for(prefix in prefixes) {
      results.addAll(compactTrie.getAllValuesWithPrefix(prefix))
    }
    return results
  }

  @Benchmark
  fun customHashMapLinearScan(): List<Int> {
    val results = mutableListOf<Int>()
    for(prefix in prefixes) {
      val values = hashMap.keys
        .filter { it.size >= prefix.size && it.subList(0, prefix.size) == prefix }
        .mapNotNull { hashMap[it] }
      results.addAll(values)
    }
    return results
  }

  @Benchmark
  fun customListLinearScan(): List<Int> {
    val results = mutableListOf<Int>()
    for(prefix in prefixes) {
      val values = list
        .filter { it.first.size >= prefix.size && it.first.subList(0, prefix.size) == prefix }
        .map { it.second }
      results.addAll(values)
    }
    return results
  }
}
