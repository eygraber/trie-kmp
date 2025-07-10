package com.eygraber.trie.utils

import com.eygraber.trie.Trie
import com.eygraber.trie.getAllWithPrefix

/**
 * A simple autocomplete engine that suggests completions for a given prefix.
 */
class AutoComplete<V>(private val data: Trie<Char, V>) {
  fun suggest(prefix: String): Set<String> = data.getAllWithPrefix(prefix)
    .keys
    .map { it.joinToString("") }
    .toSet()
}
