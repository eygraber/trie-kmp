package com.eygraber.trie

import com.eygraber.trie.utils.SpellChecker
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

abstract class AbstractSpellCheckTest {
  abstract fun createDictionary(words: List<String>): Trie<Char, Boolean>

  @Test
  fun `it suggests correct words for a misspelling`() {
    val dictionaryWords = listOf("apple", "apply", "angle", "banana", "people")
    val dictionary = createDictionary(dictionaryWords)

    val spellChecker = SpellChecker(dictionary)

    val suggestions = spellChecker.suggest("aple")

    assertEquals(setOf("apple"), suggestions)
  }

  @Test
  fun `it suggests nothing for a word with no close matches`() {
    val dictionaryWords = listOf("cat", "dog", "fish")
    val dictionary = createDictionary(dictionaryWords)

    val spellChecker = SpellChecker(dictionary)

    val suggestions = spellChecker.suggest("xyz")

    assertTrue(suggestions.isEmpty())
  }
}

class MapSpellCheckTest : AbstractSpellCheckTest() {
  override fun createDictionary(
    words: List<String>,
  ): Trie<Char, Boolean> = mutableTrieOf(*words.map { it to true }.toTypedArray())
}

class CompactSpellCheckTest : AbstractSpellCheckTest() {
  override fun createDictionary(
    words: List<String>,
  ): Trie<Char, Boolean> = mutableCompactTrieOf(*words.map { it to true }.toTypedArray())
}
