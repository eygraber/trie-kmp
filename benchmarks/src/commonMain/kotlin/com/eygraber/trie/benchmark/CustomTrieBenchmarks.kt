package com.eygraber.trie.benchmark

import com.eygraber.trie.MutableGenericTrie
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
  private lateinit var removals: List<List<DnaSegment>>

  private lateinit var compactTrie: MutableGenericTrie<DnaSegment, Int>
  private lateinit var standardTrie: MutableGenericTrie<DnaSegment, Int>

  @Setup
  fun setup() {
    val random = Random(42)
    dataset = List(datasetSize) { index ->
      val randomGene = (1..geneLength)
        .map { _ -> random.nextInt(0, dnaPool.size).let { dnaPool[it] } }
      randomGene to index
    }

    standardTrie = mutableGenericTrieOf(*dataset.toTypedArray())
    compactTrie = mutableCompactGenericTrieOf(*dataset.toTypedArray())

    prefixes = List(1000) {
      val gene = dataset[random.nextInt(0, datasetSize)].first
      gene.subList(0, gene.size / 2)
    }

    removals = List(1000) {
      dataset[random.nextInt(0, datasetSize)].first
    }
  }

  @Benchmark
  fun compactGenericTrieCreation(blackhole: Blackhole) {
    blackhole.consume(mutableCompactGenericTrieOf(*dataset.toTypedArray()))
  }

  @Benchmark
  fun standardGenericTrieCreation(blackhole: Blackhole) {
    blackhole.consume(mutableGenericTrieOf(*dataset.toTypedArray()))
  }

  @Benchmark
  fun compactGenericTriePrefixSearch(): List<Int> {
    val results = mutableListOf<Int>()
    for(prefix in prefixes) {
      results.addAll(compactTrie.getAllValuesWithPrefix(prefix))
    }
    return results
  }

  @Benchmark
  fun standardGenericTriePrefixSearch(): List<Int> {
    val results = mutableListOf<Int>()
    for(prefix in prefixes) {
      results.addAll(standardTrie.getAllValuesWithPrefix(prefix))
    }
    return results
  }

  @Benchmark
  fun compactGenericTrieRemoval(blackhole: Blackhole) {
    for(removal in removals) {
      blackhole.consume(compactTrie.remove(removal))
    }
  }

  @Benchmark
  fun standardGenericTrieRemoval(blackhole: Blackhole) {
    for(removal in removals) {
      blackhole.consume(standardTrie.remove(removal))
    }
  }
}
