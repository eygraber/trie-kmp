package com.eygraber.trie

import com.eygraber.trie.utils.AutoComplete
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

abstract class AutoCompleteTrieTest {
  abstract fun createTrie(vararg words: Pair<String, Int>): Trie<Char, Int>

  @Test
  fun `it suggests all possible completions for a prefix`() {
    val trie = createTrie(
      "apple" to 1,
      "application" to 2,
      "apple pie" to 3,
      "apply" to 4,
      "banana" to 5,
    )

    val autoComplete = AutoComplete(trie)
    val suggestions = autoComplete.suggest("app")

    assertEquals(setOf("apple", "application", "apple pie", "apply"), suggestions)
  }

  @Test
  fun `it suggests nothing for a prefix with no matches`() {
    val trie = createTrie("hello" to 1, "world" to 2)
    val autoComplete = AutoComplete(trie)
    val suggestions = autoComplete.suggest("xyz")
    assertTrue(suggestions.isEmpty())
  }
}

class CompactAutocompleteTest : AutoCompleteTrieTest() {
  override fun createTrie(vararg words: Pair<String, Int>): Trie<Char, Int> = mutableCompactTrieOf(*words)
}

class MapAutocompleteTest : AutoCompleteTrieTest() {
  override fun createTrie(vararg words: Pair<String, Int>): Trie<Char, Int> = mutableTrieOf(*words)
}
