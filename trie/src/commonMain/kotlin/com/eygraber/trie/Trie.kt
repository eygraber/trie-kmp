package com.eygraber.trie

/**
 * An immutable Trie (prefix tree) that maps sequences of elements to values.
 *
 * A trie is a tree-like data structure that stores a dynamic set of key-value pairs
 * where keys are sequences (like lists or strings). It is highly efficient for prefix-based searches.
 *
 * @param K The type of the elements in the key sequences.
 * @param V The type of the values.
 */
public interface Trie<K, V> : Map<K, V> {
  /**
   * Returns the number of key-value pairs in this trie.
   */
  override val size: Int

  /**
   * Returns a [MutableSet] of all keys in this trie.
   */
  override val keys: Set<K>

  /**
   * Returns a [MutableCollection] of all values in this trie. Note that this collection may contain duplicate values.
   */
  public override val values: MutableCollection<V>

  /**
   * Returns a [MutableSet] of all key/value pairs in this trie.
   */
  public override val entries: MutableSet<MutableMap.MutableEntry<K, V>>

  /**
   * Returns `true` if this trie contains no key-value pairs.
   */
  override fun isEmpty(): Boolean

  /**
   * Returns `true` if this trie contains a mapping for the specified key.
   */
  override fun containsKey(key: K): Boolean

  /**
   * Returns `true` if the trie contains the specified [value].
   */
  override fun containsValue(value: V): Boolean

  /**
   * Returns the value to which the specified key is mapped,
   * or `null` if this trie contains no mapping for the key.
   */
  override operator fun get(key: K): V?

  /**
   * Checks if the trie contains any key that starts with the given [prefix].
   *
   * @param prefix The prefix to search for.
   * @return `true` if there is at least one key with the given prefix, `false` otherwise.
   */
  public fun startsWith(prefix: K): Boolean

  /**
   * Retrieves all key-value pairs in the trie where the key starts with the given [prefix].
   *
   * @param prefix The prefix to search for.
   * @return A new Map containing all entries that match the prefix.
   */
  public fun getAllWithPrefix(prefix: K): Map<K, V>

  /**
   * Retrieves all values in the trie where the key starts with the given [prefix].
   *
   * @param prefix The prefix to search for.
   * @return A collection of all values that have a key matching the prefix.
   */
  public fun getAllValuesWithPrefix(prefix: K): Collection<V>
}

/**
 * Returns `true` if this trie contains a mapping for the specified key.
 */
public operator fun <K, V> Trie<K, V>.contains(key: K): Boolean = containsKey(key)

/**
 * A mutable [Trie] that maps sequences of elements to values.
 */
public interface MutableTrie<K, V> : Trie<K, V>, MutableMap<K, V> {
  /**
   * Associates the specified [value] with the specified [key] in this trie.
   * If the trie previously contained a mapping for the key, the old value is replaced.
   *
   * @param key The key with which the specified value is to be associated.
   * @param value The value to be associated with the specified key.
   * @return The previous value associated with the key, or `null` if there was no mapping for the key.
   */
  override fun put(key: K, value: V): V?

  /**
   * Updates this trie with key/value pairs from the specified map [from].
   *
   * Unless you already have a `Map instance`, it will be more
   * performant to use the`MutableTrie.putAll(vararg Pair<K, V>)` overload.
   */
  override fun putAll(from: Map<out K, V>)

  /**
   * Removes the mapping for a key from this trie if it is present.
   *
   * @param key The key whose mapping is to be removed from the trie.
   * @return The previous value associated with the key, or `null` if there was no mapping.
   */
  override fun remove(key: K): V?

  /**
   * Removes all of the mappings from this trie.
   * The trie will be empty after this call returns.
   */
  override fun clear()
}

/**
 * Updates this trie with key/value pairs.
 */
@Suppress("NOTHING_TO_INLINE")
public inline fun <K, V> MutableTrie<K, V>.putAll(vararg from: Pair<K, V>) {
  from.forEach { (key, value) -> put(key, value) }
}

/**
 * Associates the specified value with the specified key in this trie.
 * If the trie previously contained a mapping for the key, the old value is replaced.
 *
 * @param key The key with which the specified value is to be associated.
 * @param value The value to be associated with the specified key.
 * @return The previous value associated with the key, or `null` if there was no mapping for the key.
 */
public operator fun <K, V> MutableTrie<K, V>.set(key: K, value: V): V? = put(key, value)

/**
 * A [Trie] that uses [List] as its key type and implements [Map].
 */
public interface GenericTrie<K, V> : Trie<List<K>, V>

/**
 * A [Trie] that uses [List] as its key type and implements [MutableMap].
 */
public interface MutableGenericTrie<K, V> : MutableTrie<List<K>, V>, GenericTrie<K, V>
