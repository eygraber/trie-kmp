package com.eygraber.trie

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

abstract class IntTrieTest {
  abstract fun <V> createTrie(vararg pairs: Pair<List<Int>, V>): MutableGenericTrie<Int, V>

  @Test
  fun testPutAndGetWithIntKeys() {
    val trie = createTrie<String>()
    val key1 = listOf(10, 8, 0, 1)
    val key2 = listOf(10, 8, 0, 2)

    assertNull(trie.put(key1, "Device A"))
    assertEquals("Device A", trie[key1])
    assertEquals(1, trie.size)

    trie[key2] = "Device B"
    assertEquals(2, trie.size)
    assertEquals("Device B", trie[key2])
  }

  @Test
  fun testRemoveWithIntKeys() {
    val key1 = listOf(172, 16, 0, 1)
    val key2 = listOf(172, 16, 0, 2)
    val trie = createTrie(key1 to "A", key2 to "B")

    assertEquals(2, trie.size)
    val removed = trie.remove(key1)
    assertEquals("A", removed)
    assertEquals(1, trie.size)
    assertNull(trie[key1])
    assertNotNull(trie[key2])
  }

  @Test
  fun testPrefixSearchWithIntKeys() {
    val trie = createTrie(
      listOf(10, 8, 0, 1) to "Net A",
      listOf(10, 8, 1, 1) to "Net B",
      listOf(10, 20, 0, 1) to "Net C",
    )

    val prefix = listOf(10, 8)
    assertTrue(trie.startsWith(prefix))

    val values = trie.getAllValuesWithPrefix(prefix)
    assertEquals(2, values.size)
    assertTrue(values.containsAll(listOf("Net A", "Net B")))
  }

  @Test
  fun testEqualsAndHashCodeWithIntKeys() {
    val key1 = listOf(1, 2, 3)
    val key2 = listOf(4, 5, 6)
    val trie1 = createTrie(key1 to "A", key2 to "B")
    val trie2 = createTrie(key2 to "B", key1 to "A")
    val map = mapOf(key1 to "A", key2 to "B")

    assertEquals(trie1, trie2)
    assertEquals(trie1.hashCode(), trie2.hashCode())
    assertEquals(trie1, map)
  }
}

class CompactIntTrieTest : IntTrieTest() {
  override fun <V> createTrie(
    vararg pairs: Pair<List<Int>, V>,
  ): MutableGenericTrie<Int, V> = mutableCompactGenericTrieOf(*pairs)
}

class StandardIntTrieTest : IntTrieTest() {
  override fun <V> createTrie(
    vararg pairs: Pair<List<Int>, V>,
  ): MutableGenericTrie<Int, V> = mutableGenericTrieOf(*pairs)
}
