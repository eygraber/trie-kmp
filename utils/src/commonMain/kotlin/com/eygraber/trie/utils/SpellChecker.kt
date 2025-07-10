package com.eygraber.trie.utils

import com.eygraber.trie.Trie

/**
 * A simple spell checker that suggests corrections for a given word.
 * It uses a Trie-based dictionary for efficient lookups.
 */
class SpellChecker(
  private val dictionary: Trie<Char, Boolean>,
) {
  private val alphabet = "abcdefghijklmnopqrstuvwxyz"

  /**
   * Finds spelling suggestions for a word from a dictionary.
   * This implementation checks for known words at edit distance of 1.
   */
  fun suggest(word: String): Set<String> {
    // If the word is already correct, return it.
    if(dictionary.containsKey(word.toList())) return setOf(word)

    return edits1(word).filter { dictionary.containsKey(it.toList()) }.toSet()
  }

  private fun edits1(word: String): Set<String> {
    val edits = mutableSetOf<String>()
    // 1. Deletions (remove one character)
    for(i in word.indices) {
      edits.add(word.substring(0, i) + word.substring(i + 1))
    }
    // 2. Transpositions (swap adjacent characters)
    for(i in 0 until word.length - 1) {
      edits.add(word.substring(0, i) + word[i + 1] + word[i] + word.substring(i + 2))
    }
    // 3. Replacements (change one character)
    for(i in word.indices) {
      for(char in alphabet) {
        edits.add(word.substring(0, i) + char + word.substring(i + 1))
      }
    }
    // 4. Insertions (add one character)
    for(i in 0..word.length) {
      for(char in alphabet) {
        edits.add(word.substring(0, i) + char + word.substring(i))
      }
    }

    return edits
  }
}
