package com.eygraber.trie

public fun Array<Pair<String, String>>.toTrie(): Trie<String, String> =
  trieOf(*this)

public fun Array<Pair<String, String>>.toMutableTrie(): MutableTrie<String, String> =
  mutableTrieOf(*this)

public fun Array<Pair<String, String>>.toCompactTrie(): Trie<String, String> =
  compactTrieOf(*this)

public fun Array<Pair<String, String>>.toMutableCompactTrie(): MutableTrie<String, String> =
  mutableCompactTrieOf(*this)

public fun <K, V> Array<Pair<List<K>, V>>.toTrie(): GenericTrie<K, V> =
  genericTrieOf(*this)

public fun <K, V> Array<Pair<List<K>, V>>.toMutableTrie(): MutableGenericTrie<K, V> =
  mutableGenericTrieOf(*this)

public fun <K, V> Array<Pair<List<K>, V>>.toCompactTrie(): GenericTrie<K, V> =
  compactGenericTrieOf(*this)

public fun <K, V> Array<Pair<List<K>, V>>.toMutableCompactTrie(): MutableGenericTrie<K, V> =
  mutableCompactGenericTrieOf(*this)

public fun Collection<Pair<String, String>>.toTrie(): Trie<String, String> =
  trieOf(*toTypedArray())

public fun Collection<Pair<String, String>>.toMutableTrie(): MutableTrie<String, String> =
  mutableTrieOf(*toTypedArray())

public fun Collection<Pair<String, String>>.toCompactTrie(): Trie<String, String> =
  compactTrieOf(*toTypedArray())

public fun Collection<Pair<String, String>>.toMutableCompactTrie(): MutableTrie<String, String> =
  mutableCompactTrieOf(*toTypedArray())

public fun <K, V> Collection<Pair<List<K>, V>>.toTrie(): GenericTrie<K, V> =
  genericTrieOf(*toTypedArray())

public fun <K, V> Collection<Pair<List<K>, V>>.toMutableTrie(): MutableGenericTrie<K, V> =
  mutableGenericTrieOf(*toTypedArray())

public fun <K, V> Collection<Pair<List<K>, V>>.toCompactTrie(): GenericTrie<K, V> =
  compactGenericTrieOf(*toTypedArray())

public fun <K, V> Collection<Pair<List<K>, V>>.toMutableCompactTrie(): MutableGenericTrie<K, V> =
  mutableCompactGenericTrieOf(*toTypedArray())

public fun Map<String, String>.toTrie(): Trie<String, String> =
  CompactStringTrie<String>().also { it.putAll(this) }

public fun Map<String, String>.toMutableTrie(): MutableTrie<String, String> =
  CompactStringTrie<String>().also { it.putAll(this) }

public fun Map<String, String>.toCompactTrie(): Trie<String, String> =
  CompactStringTrie<String>().also { it.putAll(this) }

public fun Map<String, String>.toMutableCompactTrie(): MutableTrie<String, String> =
  CompactStringTrie<String>().also { it.putAll(this) }
