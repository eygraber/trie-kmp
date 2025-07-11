package com.eygraber.trie.benchmark

import com.eygraber.trie.CompactGenericTrie
import com.eygraber.trie.GenericTrie
import com.eygraber.trie.MutableGenericTrie
import com.eygraber.trie.StandardGenericTrie
import com.eygraber.trie.set

internal fun <V> genericTrieOfString(vararg pairs: Pair<String, V>): GenericTrie<Char, V> =
  mutableGenericTrieOfString(*pairs)

internal fun <V> mutableGenericTrieOfString(vararg pairs: Pair<String, V>): MutableGenericTrie<Char, V> {
  val trie = StandardGenericTrie<Char, V>()
  pairs.forEach { (key, value) -> trie[key.toList()] = value }
  return trie
}

internal fun <V> compactGenericTrieOfString(vararg pairs: Pair<String, V>): GenericTrie<Char, V> =
  mutableCompactGenericTrieOfString(*pairs)

internal fun <V> mutableCompactGenericTrieOfString(vararg pairs: Pair<String, V>): MutableGenericTrie<Char, V> {
  val trie = CompactGenericTrie<Char, V>()
  pairs.forEach { (key, value) -> trie[key.toList()] = value }
  return trie
}
