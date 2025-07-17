package com.eygraber.trie

public fun Array<Pair<String, String>>.toTrie(): Trie<String, String> =
  nonOptimizedTrieOf(*this)

public fun Array<Pair<String, String>>.toMutableTrie(): MutableTrie<String, String> =
  mutableNonOptimizedTrieOf(*this)

public fun Array<Pair<String, String>>.toCompactTrie(): Trie<String, String> =
  trieOf(*this)

public fun Array<Pair<String, String>>.toMutableCompactTrie(): MutableTrie<String, String> =
  mutableTrieOf(*this)

public fun <K, V> Array<Pair<List<K>, V>>.toTrie(): GenericTrie<K, V> =
  genericTrieOf(*this)

public fun <K, V> Array<Pair<List<K>, V>>.toMutableTrie(): MutableGenericTrie<K, V> =
  mutableGenericTrieOf(*this)

public fun <K, V> Array<Pair<List<K>, V>>.toCompactTrie(): GenericTrie<K, V> =
  compactGenericTrieOf(*this)

public fun <K, V> Array<Pair<List<K>, V>>.toMutableCompactTrie(): MutableGenericTrie<K, V> =
  mutableCompactGenericTrieOf(*this)

public fun Collection<Pair<String, String>>.toTrie(): Trie<String, String> =
  nonOptimizedTrieOf(*toTypedArray())

public fun Collection<Pair<String, String>>.toMutableTrie(): MutableTrie<String, String> =
  mutableNonOptimizedTrieOf(*toTypedArray())

public fun Collection<Pair<String, String>>.toCompactTrie(): Trie<String, String> =
  trieOf(*toTypedArray())

public fun Collection<Pair<String, String>>.toMutableCompactTrie(): MutableTrie<String, String> =
  mutableTrieOf(*toTypedArray())

public fun <K, V> Collection<Pair<List<K>, V>>.toTrie(): GenericTrie<K, V> =
  genericTrieOf(*toTypedArray())

public fun <K, V> Collection<Pair<List<K>, V>>.toMutableTrie(): MutableGenericTrie<K, V> =
  mutableGenericTrieOf(*toTypedArray())

public fun <K, V> Collection<Pair<List<K>, V>>.toCompactTrie(): GenericTrie<K, V> =
  compactGenericTrieOf(*toTypedArray())

public fun <K, V> Collection<Pair<List<K>, V>>.toMutableCompactTrie(): MutableGenericTrie<K, V> =
  mutableCompactGenericTrieOf(*toTypedArray())

public fun Map<String, String>.toTrie(): Trie<String, String> =
  CompactStringViewTrie<String>().also { it.putAll(this) }

public fun Map<String, String>.toMutableTrie(): MutableTrie<String, String> =
  CompactStringViewTrie<String>().also { it.putAll(this) }

public fun Map<String, String>.toCompactTrie(): Trie<String, String> =
  CompactStringViewTrie<String>().also { it.putAll(this) }

public fun Map<String, String>.toMutableCompactTrie(): MutableTrie<String, String> =
  CompactStringViewTrie<String>().also { it.putAll(this) }
