package com.eygraber.trie.utils

import com.eygraber.trie.GenericTrie
import com.eygraber.trie.Trie

/**
 * A simple autocomplete engine that suggests completions for a given `List<Char>` prefix.
 */
class CharAutoComplete(private val data: GenericTrie<Char, *>) {
  fun suggest(prefix: String): Set<String> = data.getAllWithPrefix(prefix.toList())
    .keys
    .map { it.joinToString("") }
    .toSet()
}

/**
 * A simple autocomplete engine that suggests completions for a given [String] prefix.
 */
class StringAutoComplete(private val data: Trie<String, *>) {
  fun suggest(prefix: String): Set<String> = data.getAllWithPrefix(prefix)
    .keys
    .toSet()
}
