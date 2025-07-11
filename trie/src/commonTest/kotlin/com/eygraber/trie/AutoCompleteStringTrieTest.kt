package com.eygraber.trie

import com.eygraber.trie.utils.StringAutoComplete
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

abstract class AutoCompleteStringTrieTest {
  abstract fun createTrie(vararg words: Pair<String, Int>): Trie<String, Int>

  @Test
  fun `it suggests all possible completions for a prefix`() {
    val trie = createTrie(
      "apple" to 1,
      "application" to 2,
      "apple pie" to 3,
      "apply" to 4,
      "banana" to 5,
    )

    val autoComplete = StringAutoComplete(trie)
    val suggestions = autoComplete.suggest("app")

    assertEquals(setOf("apple", "application", "apple pie", "apply"), suggestions)
  }

  @Test
  fun `it suggests nothing for a prefix with no matches`() {
    val trie = createTrie("hello" to 1, "world" to 2)
    val autoComplete = StringAutoComplete(trie)
    val suggestions = autoComplete.suggest("xyz")
    assertTrue(suggestions.isEmpty())
  }
}

class CompactAutoCompleteStringTrieTest : AutoCompleteStringTrieTest() {
  override fun createTrie(vararg words: Pair<String, Int>): Trie<String, Int> =
    mutableCompactTrieOf(*words)
}

class StandardAutoCompleteStringTrieTest : AutoCompleteStringTrieTest() {
  override fun createTrie(vararg words: Pair<String, Int>): Trie<String, Int> =
    mutableTrieOf(*words)
}
