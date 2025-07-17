package com.eygraber.trie

import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

abstract class StringTrieTest {
  abstract fun <V> createStringTrie(vararg pairs: Pair<String, V>): MutableTrie<String, V>
  abstract fun <V> createGenericTrie(vararg pairs: Pair<String, V>): MutableGenericTrie<Char, V>

  @Test
  fun testPutAndGet() {
    val trie = createStringTrie<String>()
    assertNull(trie.put("hello", "world"))
    assertEquals("world", trie["hello"])
    assertEquals(1, trie.size)

    val updatedValue = trie.put("hello", "kotlin")
    assertEquals("world", updatedValue)
    assertEquals("kotlin", trie["hello"])
    assertEquals(1, trie.size)
  }

  @Test
  fun testGenericPutAndGet() {
    val trie = createGenericTrie<String>()
    assertNull(trie.put("hello".toList(), "world"))
    assertEquals("world", trie["hello".toList()])
    assertEquals(1, trie.size)

    val updatedValue = trie.put("hello".toList(), "kotlin")
    assertEquals("world", updatedValue)
    assertEquals("kotlin", trie["hello".toList()])
    assertEquals(1, trie.size)
  }

  @Test
  fun testRemoveAndPruning() {
    val trie = createStringTrie("team" to 1, "tea" to 2, "ten" to 3)
    assertEquals(3, trie.size)

    val removedValue = trie.remove("team")
    assertEquals(1, removedValue)
    assertFalse(trie.containsKey("team"))
    assertTrue(trie.containsKey("tea"))
    assertEquals(2, trie.size)

    trie.remove("tea")
    assertFalse(trie.containsKey("tea"))
    assertTrue(trie.containsKey("ten"))
    assertEquals(1, trie.size)

    assertNull(trie.remove("nonexistent"))
    assertEquals(1, trie.size)
  }

  @Test
  fun testGenericRemoveAndPruning() {
    val trie = createGenericTrie("team" to 1, "tea" to 2, "ten" to 3)
    assertEquals(3, trie.size)

    val removedValue = trie.remove("team".toList())
    assertEquals(1, removedValue)
    assertFalse(trie.containsKey("team".toList()))
    assertTrue(trie.containsKey("tea".toList()))
    assertEquals(2, trie.size)

    trie.remove("tea".toList())
    assertFalse(trie.containsKey("tea".toList()))
    assertTrue(trie.containsKey("ten".toList()))
    assertEquals(1, trie.size)

    assertNull(trie.remove("nonexistent".toList()))
    assertEquals(1, trie.size)
  }

  @Test
  fun testPrefixSearches() {
    val trie = createStringTrie(
      "apple" to 1,
      "apply" to 2,
      "apricot" to 3,
      "banana" to 4,
      "bandana" to 5,
    )

    assertTrue(trie.startsWith("app"))
    assertFalse(trie.startsWith("cat"))

    val apPrefixResults = trie.getAllWithPrefix("ap")
    assertEquals(3, apPrefixResults.size)
    assertEquals(mapOf("apple" to 1, "apply" to 2, "apricot" to 3), apPrefixResults)

    val banPrefixValues = trie.getAllValuesWithPrefix("ban").sorted()
    assertEquals(2, banPrefixValues.size)
    assertContentEquals(listOf(4, 5), banPrefixValues)

    val applePrefixValues = trie.getAllValuesWithPrefix("apple")
    assertEquals(1, applePrefixValues.size)
    assertContentEquals(listOf(1), applePrefixValues)
  }

  @Test
  fun testGenericPrefixSearches() {
    val trie = createGenericTrie(
      "apple" to 1,
      "apply" to 2,
      "apricot" to 3,
      "banana" to 4,
      "bandana" to 5,
    )

    assertTrue(trie.startsWith("app".toList()))
    assertFalse(trie.startsWith("cat".toList()))

    val apPrefixResults = trie.getAllWithPrefix("ap".toList())
    assertEquals(3, apPrefixResults.size)
    assertEquals(mapOf("apple".toList() to 1, "apply".toList() to 2, "apricot".toList() to 3), apPrefixResults)

    val banPrefixValues = trie.getAllValuesWithPrefix("ban".toList()).sorted()
    assertEquals(2, banPrefixValues.size)
    assertContentEquals(listOf(4, 5), banPrefixValues)

    val applePrefixValues = trie.getAllValuesWithPrefix("apple".toList())
    assertEquals(1, applePrefixValues.size)
    assertContentEquals(listOf(1), applePrefixValues)
  }

  @Test
  fun testEmptyPrefixSearch() {
    val trie = createStringTrie("a" to 1, "b" to 2)
    val allEntries = trie.getAllWithPrefix("")
    assertEquals(trie, allEntries)

    val allValues = trie.getAllValuesWithPrefix("").sorted()
    assertEquals(trie.values.sorted(), allValues)
  }

  @Test
  fun testGenericEmptyPrefixSearch() {
    val trie = createGenericTrie("a" to 1, "b" to 2)
    val allEntries = trie.getAllWithPrefix("".toList())
    assertEquals(trie, allEntries)

    val allValues = trie.getAllValuesWithPrefix("".toList()).sorted()
    assertEquals(trie.values.sorted(), allValues)
  }

  @Test
  fun testClearAndIsEmpty() {
    val trie = createStringTrie("test" to 1)
    assertFalse(trie.isEmpty())

    trie.clear()
    assertTrue(trie.isEmpty())
    assertEquals(0, trie.size)
    assertNull(trie["test"])
  }

  @Test
  fun testGenericClearAndIsEmpty() {
    val trie = createGenericTrie("test" to 1)
    assertFalse(trie.isEmpty())

    trie.clear()
    assertTrue(trie.isEmpty())
    assertEquals(0, trie.size)
    assertNull(trie["test".toList()])
  }

  @Test
  fun testEdgeCases() {
    val trie = createStringTrie<Int>()

    trie[""] = 100
    assertEquals(100, trie[""])
    assertEquals(1, trie.size)

    trie["a"] = 1
    assertEquals(2, trie.size)

    val all = trie.getAllValuesWithPrefix("").sorted()
    assertEquals(2, all.size)
    assertContentEquals(listOf(1, 100), all)

    trie.remove("")
    assertNull(trie[""])
    assertEquals(1, trie.size)
  }

  @Test
  fun testGenericEdgeCases() {
    val trie = createGenericTrie<Int>()

    trie["".toList()] = 100
    assertEquals(100, trie["".toList()])
    assertEquals(1, trie.size)

    trie["a".toList()] = 1
    assertEquals(2, trie.size)

    val all = trie.getAllValuesWithPrefix("".toList()).sorted()
    assertEquals(2, all.size)
    assertContentEquals(listOf(1, 100), all)

    trie.remove("".toList())
    assertNull(trie["".toList()])
    assertEquals(1, trie.size)
  }

  @Test
  fun testEqualsHashCodeAndToString() {
    val trie1 = createStringTrie("hello" to "world", "test" to "case")
    val trie2 = createStringTrie("test" to "case", "hello" to "world")
    val trie3 = createStringTrie("hello" to "world", "test" to "different")
    val standardMap = mapOf("hello" to "world", "test" to "case")

    assertEquals(standardMap.hashCode(), trie1.hashCode())
    assertEquals(trie1.hashCode(), trie2.hashCode())

    assertEquals(trie1, trie2)
    assertNotEquals(trie1, trie3)

    assertTrue(trie1 == standardMap)
  }

  @Test
  fun testGenericEqualsHashCodeAndToString() {
    val trie1 = createGenericTrie("hello" to "world", "test" to "case")
    val trie2 = createGenericTrie("test" to "case", "hello" to "world")
    val trie3 = createGenericTrie("hello" to "world", "test" to "different")
    val standardMap = mapOf("hello".toList() to "world", "test".toList() to "case")

    assertEquals(standardMap.hashCode(), trie1.hashCode())
    assertEquals(trie1.hashCode(), trie2.hashCode())

    assertEquals(trie1, trie2)
    assertNotEquals(trie1, trie3)

    assertTrue(trie1 == standardMap)
  }
}

class CompactStringTrieTest : StringTrieTest() {
  override fun <V> createStringTrie(vararg pairs: Pair<String, V>): MutableTrie<String, V> =
    mutableTrieOf(*pairs)

  override fun <V> createGenericTrie(vararg pairs: Pair<String, V>): MutableGenericTrie<Char, V> =
    mutableCompactGenericTrieOfString(*pairs)
}

class CompactStringViewTrieTest : StringTrieTest() {
  override fun <V> createStringTrie(vararg pairs: Pair<String, V>): MutableTrie<String, V> =
    mutableTrieOf(
      pairs = pairs,
      useSaferImplementationForRemovals = false,
    )

  override fun <V> createGenericTrie(vararg pairs: Pair<String, V>): MutableGenericTrie<Char, V> =
    mutableCompactGenericTrieOfString(*pairs)
}

class StandardStringTrieTest : StringTrieTest() {
  override fun <V> createStringTrie(vararg pairs: Pair<String, V>): MutableTrie<String, V> =
    mutableNonOptimizedTrieOf(*pairs)

  override fun <V> createGenericTrie(vararg pairs: Pair<String, V>): MutableGenericTrie<Char, V> =
    mutableGenericTrieOfString(*pairs)
}
