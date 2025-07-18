package com.eygraber.trie

import com.eygraber.trie.utils.StringSpellChecker
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

abstract class SpellCheckStringTrieTest {
  abstract fun createDictionary(words: List<String>): Trie<String, Boolean>

  @Test
  fun `it suggests correct words for a misspelling`() {
    val dictionaryWords = listOf("apple", "apply", "angle", "banana", "people")
    val dictionary = createDictionary(dictionaryWords)

    val spellChecker = StringSpellChecker(dictionary)

    val suggestions = spellChecker.suggest("aple")

    assertEquals(setOf("apple"), suggestions)
  }

  @Test
  fun `it suggests nothing for a word with no close matches`() {
    val dictionaryWords = listOf("cat", "dog", "fish")
    val dictionary = createDictionary(dictionaryWords)

    val spellChecker = StringSpellChecker(dictionary)

    val suggestions = spellChecker.suggest("xyz")

    assertTrue(suggestions.isEmpty())
  }
}

class CompactSpellCheckStringTrieTest : SpellCheckStringTrieTest() {
  override fun createDictionary(
    words: List<String>,
  ): Trie<String, Boolean> = trieOf(words.map { it to true })
}

class StandardSpellCheckStringTrieTest : SpellCheckStringTrieTest() {
  override fun createDictionary(
    words: List<String>,
  ): Trie<String, Boolean> = nonOptimizedTrieOf(words.map { it to true })
}
