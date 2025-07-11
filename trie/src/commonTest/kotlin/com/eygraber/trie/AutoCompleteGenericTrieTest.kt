package com.eygraber.trie

import com.eygraber.trie.utils.CharAutoComplete
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

abstract class AutoCompleteGenericTrieTest {
  abstract fun createTrie(vararg words: Pair<String, Int>): GenericTrie<Char, Int>

  @Test
  fun `it suggests all possible completions for a prefix`() {
    val trie = createTrie(
      "apple" to 1,
      "application" to 2,
      "apple pie" to 3,
      "apply" to 4,
      "banana" to 5,
    )

    val autoComplete = CharAutoComplete(trie)
    val suggestions = autoComplete.suggest("app")

    assertEquals(setOf("apple", "application", "apple pie", "apply"), suggestions)
  }

  @Test
  fun `it suggests nothing for a prefix with no matches`() {
    val trie = createTrie("hello" to 1, "world" to 2)
    val autoComplete = CharAutoComplete(trie)
    val suggestions = autoComplete.suggest("xyz")
    assertTrue(suggestions.isEmpty())
  }
}

class CompactAutocompleteGenericTrieTest : AutoCompleteGenericTrieTest() {
  override fun createTrie(vararg words: Pair<String, Int>): GenericTrie<Char, Int> =
    mutableCompactGenericTrieOfString(*words)
}

class StandardAutocompleteGenericTrieTest : AutoCompleteGenericTrieTest() {
  override fun createTrie(vararg words: Pair<String, Int>): GenericTrie<Char, Int> =
    mutableGenericTrieOfString(*words)
}
