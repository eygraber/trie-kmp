package com.eygraber.trie

import kotlin.jvm.JvmName

public fun <V> Array<Pair<String, V>>.toTrie(): Trie<String, V> =
  trieOf(this)

public fun <V> Array<Pair<String, V>>.toMutableTrie(
  /**
   * Will return an implementation that is safer to remove from, but has slightly worse insertion performance.
   *
   * If you don't call [MutableTrie.remove],
   * or don't use large [String] as your keys, you may choose to set this to `false`.
   *
   * The less-safe implementation could potentially leak a [String] key after it has been removed.
   */
  useSaferImplementationForRemovals: Boolean = true,
): MutableTrie<String, V> =
  mutableTrieOf(this, useSaferImplementationForRemovals = useSaferImplementationForRemovals)

@JvmName("toGenericTrie")
public fun <K, V> Array<Pair<List<K>, V>>.toTrie(): GenericTrie<K, V> =
  genericTrieOf(this)

public fun <K, V> Array<Pair<List<K>, V>>.toMutableTrie(): MutableGenericTrie<K, V> =
  mutableGenericTrieOf(this)

public fun <K, V> Array<Pair<List<K>, V>>.toMutableCompactTrie(): MutableGenericTrie<K, V> =
  mutableCompactGenericTrieOf(this)

public fun <V> Collection<Pair<String, V>>.toTrie(): Trie<String, V> =
  trieOf(this)

public fun <V> Collection<Pair<String, V>>.toMutableTrie(): MutableTrie<String, V> =
  mutableTrieOf(this)

@JvmName("toGenericTrie")
public fun <K, V> Collection<Pair<List<K>, V>>.toTrie(): GenericTrie<K, V> =
  genericTrieOf(this)

public fun <K, V> Collection<Pair<List<K>, V>>.toMutableTrie(): MutableGenericTrie<K, V> =
  mutableGenericTrieOf(this)

public fun <K, V> Collection<Pair<List<K>, V>>.toCompactTrie(): GenericTrie<K, V> =
  compactGenericTrieOf(this)

public fun <K, V> Collection<Pair<List<K>, V>>.toMutableCompactTrie(): MutableGenericTrie<K, V> =
  mutableCompactGenericTrieOf(this)

public fun <V> Map<String, V>.toTrie(): Trie<String, V> =
  trieOf(entries.map { (key, value) -> key to value })

public fun <V> Map<String, V>.toMutableTrie(
  /**
   * Will return an implementation that is safer to remove from, but has slightly worse insertion performance.
   *
   * If you don't call [MutableTrie.remove],
   * or don't use large [String] as your keys, you may choose to set this to `false`.
   *
   * The less-safe implementation could potentially leak a [String] key after it has been removed.
   */
  useSaferImplementationForRemovals: Boolean = true,
): MutableTrie<String, V> =
  mutableTrieOf(
    entries.map { (key, value) -> key to value },
    useSaferImplementationForRemovals = useSaferImplementationForRemovals,
  )

public fun <V> MutableTrie<String, V>.toImmutableTrie(): Trie<String, V> =
  this as Trie<String, V>

public fun <K, V> MutableGenericTrie<K, V>.toImmutableTrie(): GenericTrie<K, V> =
  this as GenericTrie<K, V>

public fun <V> Trie<String, V>.toMutableTrie(): MutableTrie<String, V> =
  this as MutableTrie<String, V>

public fun <K, V> GenericTrie<K, V>.toMutableTrie(): MutableGenericTrie<K, V> =
  this as MutableGenericTrie<K, V>
