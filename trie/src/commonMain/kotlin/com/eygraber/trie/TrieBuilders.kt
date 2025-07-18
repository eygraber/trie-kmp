@file:Suppress("MethodOverloading")

package com.eygraber.trie

import kotlin.jvm.JvmName

/**
 * Creates a new read-only [Trie] with the given String keys and values.
 */
public fun <V> trieOf(pairs: Collection<Pair<String, V>>): Trie<String, V> =
  mutableTrieOf(pairs, useSaferImplementationForRemovals = false)

/**
 * Creates a new read-only [Trie] with the given String keys and values.
 */
@JvmName("trieOfStringArray")
public fun <V> trieOf(pairs: Array<Pair<String, V>>): Trie<String, V> =
  mutableTrieOf(pairs, useSaferImplementationForRemovals = false)

/**
 * Creates a new read-only [Trie] with the given String keys and values.
 */
public fun <V> trieOf(vararg pairs: Pair<String, V>): Trie<String, V> =
  mutableTrieOf(*pairs, useSaferImplementationForRemovals = false)

/**
 * Creates a new [MutableTrie] with the given String keys and values.
 */
public fun <V> mutableTrieOf(
  pairs: Collection<Pair<String, V>>,
  /**
   * Will return an implementation that is safer to remove from, but has slightly worse insertion performance.
   *
   * If you don't call [MutableTrie.remove],
   * or don't use large [String] as your keys, you may choose to set this to `false`.
   *
   * The less-safe implementation could potentially leak a [String] key after it has been removed.
   */
  useSaferImplementationForRemovals: Boolean = true,
): MutableTrie<String, V> {
  // if there will be removals it is better to use CompactStringTrie since
  // CompactStringViewTrie could leak String keys after removal in some scenarios
  val trie = when {
    useSaferImplementationForRemovals -> CompactStringTrie<V>()
    else -> CompactStringViewTrie()
  }
  trie.putAll(pairs)
  return trie
}

/**
 * Creates a new [MutableTrie] with the given String keys and values.
 */
@JvmName("mutableTrieOfArray")
public fun <V> mutableTrieOf(
  pairs: Array<Pair<String, V>>,
  /**
   * Will return an implementation that is safer to remove from, but has slightly worse insertion performance.
   *
   * If you don't call [MutableTrie.remove],
   * or don't use large [String] as your keys, you may choose to set this to `false`.
   *
   * The less-safe implementation could potentially leak a [String] key after it has been removed.
   */
  useSaferImplementationForRemovals: Boolean = true,
): MutableTrie<String, V> {
  // if there will be removals it is better to use CompactStringTrie since
  // CompactStringViewTrie could leak String keys after removal in some scenarios
  val trie = when {
    useSaferImplementationForRemovals -> CompactStringTrie<V>()
    else -> CompactStringViewTrie()
  }
  trie.putAll(pairs)
  return trie
}

/**
 * Creates a new [MutableTrie] with the given String keys and values.
 */
public fun <V> mutableTrieOf(
  vararg pairs: Pair<String, V>,
  /**
   * Will return an implementation that is safer to remove from, but has slightly worse insertion performance.
   *
   * If you don't call [MutableTrie.remove],
   * or don't use large [String] as your keys, you may choose to set this to `false`.
   *
   * The less-safe implementation could potentially leak a [String] key after it has been removed.
   */
  useSaferImplementationForRemovals: Boolean = true,
): MutableTrie<String, V> {
  // if there will be removals it is better to use CompactStringTrie since
  // CompactStringViewTrie could leak String keys after removal in some scenarios
  val trie = when {
    useSaferImplementationForRemovals -> CompactStringTrie<V>()
    else -> CompactStringViewTrie()
  }
  trie.putAll(*pairs)
  return trie
}

/**
 * Creates a new read-only [Trie] with the given String keys and values.
 *
 * This should only be used if your benchmarks show it is more performant than [trieOf].
 */
public fun <V> nonOptimizedTrieOf(pairs: Collection<Pair<String, V>>): Trie<String, V> =
  mutableNonOptimizedTrieOf(pairs)

/**
 * Creates a new read-only [Trie] with the given String keys and values.
 *
 * This should only be used if your benchmarks show it is more performant than [trieOf].
 */
@JvmName("nonOptimizedTrieOfArray")
public fun <V> nonOptimizedTrieOf(pairs: Array<Pair<String, V>>): Trie<String, V> =
  mutableNonOptimizedTrieOf(pairs)

/**
 * Creates a new read-only [Trie] with the given String keys and values.
 *
 * This should only be used if your benchmarks show it is more performant than [trieOf].
 */
public fun <V> nonOptimizedTrieOf(vararg pairs: Pair<String, V>): Trie<String, V> =
  mutableNonOptimizedTrieOf(*pairs)

/**
 * Creates a new [MutableTrie] with the given String keys and values.
 *
 * This should only be used if your benchmarks show it is more performant than [mutableTrieOf].
 */
public fun <V> mutableNonOptimizedTrieOf(pairs: Collection<Pair<String, V>>): MutableTrie<String, V> {
  val trie = StandardStringTrie<V>()
  trie.putAll(pairs)
  return trie
}

/**
 * Creates a new [MutableTrie] with the given String keys and values.
 *
 * This should only be used if your benchmarks show it is more performant than [mutableTrieOf].
 */
@JvmName("mutableNonOptimizedTrieOfArray")
public fun <V> mutableNonOptimizedTrieOf(pairs: Array<Pair<String, V>>): MutableTrie<String, V> {
  val trie = StandardStringTrie<V>()
  trie.putAll(pairs)
  return trie
}

/**
 * Creates a new [MutableTrie] with the given String keys and values.
 *
 * This should only be used if your benchmarks show it is more performant than [mutableTrieOf].
 */
public fun <V> mutableNonOptimizedTrieOf(vararg pairs: Pair<String, V>): MutableTrie<String, V> {
  val trie = StandardStringTrie<V>()
  trie.putAll(*pairs)
  return trie
}

/**
 * Creates a new read-only [GenericTrie] with the given key-value pairs.
 * The key is a sequence of elements.
 */
public fun <K, V> genericTrieOf(pairs: Collection<Pair<List<K>, V>>): GenericTrie<K, V> =
  mutableGenericTrieOf(pairs)

/**
 * Creates a new read-only [GenericTrie] with the given key-value pairs.
 * The key is a sequence of elements.
 */
@JvmName("genericTrieOfArray")
public fun <K, V> genericTrieOf(pairs: Array<Pair<List<K>, V>>): GenericTrie<K, V> =
  mutableGenericTrieOf(pairs)

/**
 * Creates a new read-only [GenericTrie] with the given key-value pairs.
 * The key is a sequence of elements.
 */
public fun <K, V> genericTrieOf(vararg pairs: Pair<List<K>, V>): GenericTrie<K, V> =
  mutableGenericTrieOf(*pairs)

/**
 * Creates a new [MutableGenericTrie] with the given key-value pairs.
 * The key is a sequence of elements.
 */
public fun <K, V> mutableGenericTrieOf(pairs: Collection<Pair<List<K>, V>>): MutableGenericTrie<K, V> {
  val trie = StandardGenericTrie<K, V>()
  trie.putAll(pairs)
  return trie
}

/**
 * Creates a new [MutableGenericTrie] with the given key-value pairs.
 * The key is a sequence of elements.
 */
@JvmName("mutableGenericTrieOfArray")
public fun <K, V> mutableGenericTrieOf(pairs: Array<Pair<List<K>, V>>): MutableGenericTrie<K, V> {
  val trie = StandardGenericTrie<K, V>()
  trie.putAll(pairs)
  return trie
}

/**
 * Creates a new [MutableGenericTrie] with the given key-value pairs.
 * The key is a sequence of elements.
 */
public fun <K, V> mutableGenericTrieOf(vararg pairs: Pair<List<K>, V>): MutableGenericTrie<K, V> {
  val trie = StandardGenericTrie<K, V>()
  trie.putAll(*pairs)
  return trie
}

/**
 * Creates a new compact read-only [GenericTrie] with the given key-value pairs.
 * The key is a sequence of elements.
 *
 * This should only be used if your benchmarks show it is more performant than [genericTrieOf].
 */
public fun <K, V> compactGenericTrieOf(pairs: Collection<Pair<List<K>, V>>): GenericTrie<K, V> =
  mutableCompactGenericTrieOf(pairs)

/**
 * Creates a new compact read-only [GenericTrie] with the given key-value pairs.
 * The key is a sequence of elements.
 *
 * This should only be used if your benchmarks show it is more performant than [genericTrieOf].
 */
@JvmName("compactGenericTrieOfArray")
public fun <K, V> compactGenericTrieOf(pairs: Array<Pair<List<K>, V>>): GenericTrie<K, V> =
  mutableCompactGenericTrieOf(pairs)

/**
 * Creates a new compact read-only [GenericTrie] with the given key-value pairs.
 * The key is a sequence of elements.
 *
 * This should only be used if your benchmarks show it is more performant than [genericTrieOf].
 */
public fun <K, V> compactGenericTrieOf(vararg pairs: Pair<List<K>, V>): GenericTrie<K, V> =
  mutableCompactGenericTrieOf(*pairs)

/**
 * Creates a compact new [MutableGenericTrie] with the given key-value pairs.
 * The key is a sequence of elements.
 *
 * This should only be used if your benchmarks show it is more performant than [mutableGenericTrieOf].
 */
public fun <K, V> mutableCompactGenericTrieOf(pairs: Collection<Pair<List<K>, V>>): MutableGenericTrie<K, V> {
  val trie = CompactGenericTrie<K, V>()
  trie.putAll(pairs)
  return trie
}

/**
 * Creates a compact new [MutableGenericTrie] with the given key-value pairs.
 * The key is a sequence of elements.
 *
 * This should only be used if your benchmarks show it is more performant than [mutableGenericTrieOf].
 */
@JvmName("mutableCompactGenericTrieOfArray")
public fun <K, V> mutableCompactGenericTrieOf(pairs: Array<Pair<List<K>, V>>): MutableGenericTrie<K, V> {
  val trie = CompactGenericTrie<K, V>()
  trie.putAll(pairs)
  return trie
}

/**
 * Creates a compact new [MutableGenericTrie] with the given key-value pairs.
 * The key is a sequence of elements.
 *
 * This should only be used if your benchmarks show it is more performant than [mutableGenericTrieOf].
 */
public fun <K, V> mutableCompactGenericTrieOf(vararg pairs: Pair<List<K>, V>): MutableGenericTrie<K, V> {
  val trie = CompactGenericTrie<K, V>()
  trie.putAll(*pairs)
  return trie
}

/**
 * Returns an empty read-only [Trie]
 */
public fun <K, V> emptyTrie(): Trie<K, V> =
  @Suppress("UNCHECKED_CAST") (EmptyTrie as Trie<K, V>)

/**
 * Returns an empty read-only [GenericTrie] of specified type.
 */
public fun <K, V> emptyGenericTrie(): GenericTrie<K, V> =
  @Suppress("UNCHECKED_CAST") (EmptyGenericTrie as GenericTrie<K, V>)

private object EmptyTrie : Trie<Any, Nothing> {
  override fun startsWith(prefix: Any) = false

  override fun getAllWithPrefix(
    prefix: Any,
  ): Map<Any, Nothing> = emptyMap()

  override fun getAllValuesWithPrefix(
    prefix: Any,
  ): Collection<Nothing> = emptyList()

  override val size: Int = 0
  override val keys: Set<List<Nothing>> = emptySet()
  override val values: MutableCollection<Nothing> = ArrayList(0)
  override val entries: MutableSet<MutableMap.MutableEntry<Any, Nothing>> = HashSet(0)

  override fun isEmpty(): Boolean = true
  override fun containsKey(key: Any) = false
  override fun containsValue(value: Nothing) = false
  override fun get(key: Any) = null
}

private object EmptyGenericTrie : GenericTrie<Any, Nothing> {
  override fun startsWith(prefix: List<Any>) = false

  override fun getAllWithPrefix(
    prefix: List<Any>,
  ): Map<List<Any>, Nothing> = emptyMap()

  override fun getAllValuesWithPrefix(
    prefix: List<Any>,
  ): Collection<Nothing> = emptyList()

  override val size: Int = 0
  override val keys: Set<List<Nothing>> = emptySet()
  override val values: MutableCollection<Nothing> = ArrayList(0)
  override val entries: MutableSet<MutableMap.MutableEntry<List<Any>, Nothing>> = HashSet(0)

  override fun isEmpty(): Boolean = true
  override fun containsKey(key: List<Any>) = false
  override fun containsValue(value: Nothing) = false
  override fun get(key: List<Any>) = null
}
