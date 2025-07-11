package com.eygraber.trie

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

data class DnaSegment(val value: Char)

@Suppress("PrivatePropertyName")
abstract class GenericTrieTest {
  private val A = DnaSegment('A')
  private val C = DnaSegment('C')
  private val G = DnaSegment('G')
  private val T = DnaSegment('T')

  abstract fun <V> createTrie(vararg pairs: Pair<List<DnaSegment>, V>): MutableGenericTrie<DnaSegment, V>

  @Test
  fun testPutAndGetWithCustomKeys() {
    val trie = createTrie<String>()
    val key1 = listOf(A, C, G)
    val key2 = listOf(A, C, T)

    assertNull(trie.put(key1, "Gene Alpha"))
    assertEquals("Gene Alpha", trie[key1])
    assertEquals(1, trie.size)

    trie[key2] = "Gene Beta"
    assertEquals(2, trie.size)
    assertEquals("Gene Beta", trie[key2])
  }

  @Test
  fun testRemoveWithCustomKeys() {
    val key1 = listOf(A, G, T)
    val key2 = listOf(A, G, C)
    val trie = createTrie(key1 to "X", key2 to "Y")

    assertEquals(2, trie.size)
    val removed = trie.remove(key1)
    assertEquals("X", removed)
    assertEquals(1, trie.size)
    assertNull(trie[key1])
    assertNotNull(trie[key2])
  }

  @Test
  fun testPrefixSearchWithCustomKeys() {
    val trie = createTrie(
      listOf(A, C, G, T) to "Gene 1",
      listOf(A, C, G, A) to "Gene 2",
      listOf(A, T, T, A) to "Gene 3",
    )

    val prefix = listOf(A, C, G)
    assertTrue(trie.startsWith(prefix))

    val values = trie.getAllValuesWithPrefix(prefix)
    assertEquals(2, values.size)
    assertTrue(values.containsAll(listOf("Gene 1", "Gene 2")))
  }

  @Test
  fun testEqualsAndHashCodeWithCustomKeys() {
    val key1 = listOf(A, T)
    val key2 = listOf(C, G)
    val trie1 = createTrie(key1 to "1", key2 to "2")
    val trie2 = createTrie(key2 to "2", key1 to "1")
    val map = mapOf(key1 to "1", key2 to "2")

    assertEquals(trie1, trie2)
    assertEquals(trie1.hashCode(), trie2.hashCode())
    assertEquals(trie1, map)
  }
}

class CompactGenericTrieTest : GenericTrieTest() {
  override fun <V> createTrie(
    vararg pairs: Pair<List<DnaSegment>, V>,
  ): MutableGenericTrie<DnaSegment, V> = mutableCompactGenericTrieOf(*pairs)
}

class StandardGenericTrieTest : GenericTrieTest() {
  override fun <V> createTrie(
    vararg pairs: Pair<List<DnaSegment>, V>,
  ): MutableGenericTrie<DnaSegment, V> = mutableGenericTrieOf(*pairs)
}
