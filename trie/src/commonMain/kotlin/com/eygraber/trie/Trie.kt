package com.eygraber.trie

/**
 * An immutable Trie (prefix tree) that maps sequences of elements to values.
 *
 * A trie is a tree-like data structure that stores a dynamic set of key-value pairs
 * where keys are sequences (like lists or strings). It is highly efficient for prefix-based searches.
 *
 * @param E The type of the elements in the key sequences.
 * @param V The type of the values.
 */
public interface Trie<E, V> : Map<List<E>, V> {
  /**
   * Checks if the Trie contains any key that starts with the given prefix.
   *
   * @param prefix The prefix to search for.
   * @return `true` if there is at least one key with the given prefix, `false` otherwise.
   */
  public fun startsWith(prefix: List<E>): Boolean

  /**
   * Retrieves all key-value pairs in the Trie where the key starts with the given prefix.
   *
   * @param prefix The prefix to search for.
   * @return A new Map containing all entries that match the prefix.
   */
  public fun getAllWithPrefix(prefix: List<E>): Map<List<E>, V>

  /**
   * Retrieves all values in the Trie where the key starts with the given prefix.
   *
   * @param prefix The prefix to search for.
   * @return A collection of all values that have a key matching the prefix.
   */
  public fun getAllValuesWithPrefix(prefix: List<E>): Collection<V>

  @Deprecated("containsValue is not an efficient operation on a Trie")
  override fun containsValue(value: V): Boolean
}

/**
 * A mutable Trie (prefix tree) that maps sequences of elements to values.
 *
 * This interface provides methods to modify the Trie, such as adding and removing entries.
 *
 * @param E The type of the elements in the key sequences.
 * @param V The type of the values.
 */
public interface MutableTrie<E, V> : Trie<E, V>, MutableMap<List<E>, V>

/**
 * Creates a new read-only [Trie] with the given key-value pairs.
 * The key is a sequence of elements.
 */
public fun <E, V> genericTrieOf(vararg pairs: Pair<List<E>, V>): Trie<E, V> = mutableGenericTrieOf(*pairs)

/**
 * Creates a new [MutableTrie] with the given key-value pairs.
 * The key is a sequence of elements.
 */
public fun <E, V> mutableGenericTrieOf(vararg pairs: Pair<List<E>, V>): MutableTrie<E, V> {
  val trie = MapTrie<E, V>()
  trie.putAll(pairs.toMap())
  return trie
}

/**
 * Creates a new compact read-only [Trie] with the given key-value pairs.
 * The key is a sequence of elements.
 */
public fun <E, V> genericCompactTrieOf(vararg pairs: Pair<List<E>, V>): Trie<E, V> = mutableGenericCompactTrieOf(*pairs)

/**
 * Creates a compact new [MutableTrie] with the given key-value pairs.
 * The key is a sequence of elements.
 */
public fun <E, V> mutableGenericCompactTrieOf(vararg pairs: Pair<List<E>, V>): MutableTrie<E, V> {
  val trie = CompactTrie<E, V>()
  trie.putAll(pairs.toMap())
  return trie
}

/**
 * Creates a new read-only [Trie] with the given String keys and values.
 */
public fun <V> trieOf(vararg pairs: Pair<String, V>): Trie<Char, V> = mutableTrieOf(*pairs)

/**
 * Creates a new [MutableTrie] with the given String keys and values.
 */
public fun <V> mutableTrieOf(vararg pairs: Pair<String, V>): MutableTrie<Char, V> {
  val trie = MapTrie<Char, V>()
  pairs.forEach { (key, value) -> trie[key.toList()] = value }
  return trie
}

/**
 * Creates a new compact read-only [Trie] with the given String keys and values.
 */
public fun <V> compactTrieOf(vararg pairs: Pair<String, V>): Trie<Char, V> = mutableCompactTrieOf(*pairs)

/**
 * Creates a new compact [MutableTrie] with the given String keys and values.
 */
public fun <V> mutableCompactTrieOf(vararg pairs: Pair<String, V>): MutableTrie<Char, V> {
  val trie = CompactTrie<Char, V>()
  pairs.forEach { (key, value) -> trie[key.toList()] = value }
  return trie
}

/** Checks if the trie contains the given String key. */
public fun <V> Trie<Char, V>.containsKey(key: String): Boolean = containsKey(key.toList())

/** Checks if the trie contains the given String key. */
public operator fun <V> Trie<Char, V>.contains(key: String): Boolean = containsKey(key.toList())

/** Gets the value corresponding to the given String key. */
public operator fun <V> Trie<Char, V>.get(key: String): V? = get(key.toList())

/** Checks if the Trie contains any key that starts with the given String prefix. */
public fun <V> Trie<Char, V>.startsWith(prefix: String): Boolean = startsWith(prefix.toList())

/** Retrieves all entries where the key starts with the given String prefix. */
public fun <V> Trie<Char, V>.getAllWithPrefix(prefix: String): Map<List<Char>, V> = getAllWithPrefix(prefix.toList())

/** Retrieves all values where the key starts with the given String prefix. */
public fun <V> Trie<Char, V>.getAllValuesWithPrefix(
  prefix: String,
): Collection<V> = getAllValuesWithPrefix(prefix.toList())

/** Associates the specified value with the specified String key in the trie. */
public fun <V> MutableTrie<Char, V>.put(key: String, value: V): V? = put(key.toList(), value)

/** Associates the specified value with the specified String key in the trie. */
public operator fun <V> MutableTrie<Char, V>.set(key: String, value: V): V? = put(key.toList(), value)

/** Removes the mapping for the specified String key from this trie if it is present. */
public fun <V> MutableTrie<Char, V>.remove(key: String): V? = remove(key.toList())

/**
 * Returns an empty read-only trie of String.
 */
public fun <V> emptyTrie(): Trie<Char, V> = @Suppress("UNCHECKED_CAST") (EmptyTrie as Trie<Char, V>)

/**
 * Returns an empty read-only trie of specified type.
 */
public fun <E, V> emptyGenericTrie(): Trie<E, V> = @Suppress("UNCHECKED_CAST") (EmptyTrie as Trie<E, V>)

private object EmptyTrie : Trie<Any, Nothing> {
  override fun startsWith(prefix: List<Any>) = false

  override fun getAllWithPrefix(
    prefix: List<Any>,
  ): Map<List<Any>, Nothing> = emptyMap()

  override fun getAllValuesWithPrefix(
    prefix: List<Any>,
  ): Collection<Nothing> = emptyList()

  override val size: Int = 0
  override val keys: Set<List<Nothing>> = emptySet()
  override val values: Collection<Nothing> = emptyList()
  override val entries: Set<Map.Entry<List<Nothing>, Nothing>> = emptySet()

  override fun isEmpty(): Boolean = true
  override fun containsKey(key: List<Any>) = false
  @Deprecated("containsValue is not an efficient operation on a Trie")
  override fun containsValue(value: Nothing) = false
  override fun get(key: List<Any>) = null
}
