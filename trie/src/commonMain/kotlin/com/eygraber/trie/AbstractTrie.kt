package com.eygraber.trie

public abstract class AbstractTrie<E, V> : MutableTrie<E, V> {
  abstract override val size: Int
  abstract override val entries: MutableSet<MutableMap.MutableEntry<List<E>, V>>

  override val keys: MutableSet<List<E>>
    get() = object : AbstractMutableSet<List<E>>() {
      override val size: Int get() = this@AbstractTrie.size
      override fun iterator(): MutableIterator<List<E>> =
        object : MutableIterator<List<E>> {
          private val entryIterator = entries.iterator()
          override fun hasNext(): Boolean = entryIterator.hasNext()
          override fun next(): List<E> = entryIterator.next().key
          override fun remove() = entryIterator.remove()
        }
      override fun add(element: List<E>): Boolean = throw UnsupportedOperationException()
    }

  override val values: MutableCollection<V>
    get() = object : AbstractMutableCollection<V>() {
      override val size: Int get() = this@AbstractTrie.size
      override fun iterator(): MutableIterator<V> =
        object : MutableIterator<V> {
          private val entryIterator = entries.iterator()
          override fun hasNext(): Boolean = entryIterator.hasNext()
          override fun next(): V = entryIterator.next().value
          override fun remove() = entryIterator.remove()
        }
      override fun add(element: V): Boolean = throw UnsupportedOperationException()
    }

  abstract override fun get(key: List<E>): V?
  abstract override fun put(key: List<E>, value: V): V?
  abstract override fun remove(key: List<E>): V?
  abstract override fun clear()
  abstract override fun startsWith(prefix: List<E>): Boolean
  abstract override fun getAllWithPrefix(prefix: List<E>): Map<List<E>, V>
  abstract override fun getAllValuesWithPrefix(prefix: List<E>): Collection<V>

  override fun isEmpty(): Boolean = size == 0

  @Deprecated("containsValue is not an efficient operation on a Trie")
  override fun containsValue(value: V): Boolean = values.any { it == value }

  override fun putAll(from: Map<out List<E>, V>) {
    from.forEach { (key, value) -> put(key, value) }
  }

  override fun containsKey(key: List<E>): Boolean = get(key) != null

  override fun toString(): String =
    entries.joinToString(
      separator = ", ",
      prefix = "{",
      postfix = "}",
    ) { it.toString() }

  override fun hashCode(): Int = entries.hashCode()

  override fun equals(other: Any?): Boolean {
    if(this === other) return true
    if(other !is Map<*, *>) return false
    if(size != other.size) return false
    return try {
      entries.all { (key, value) -> other[key] == value }
    }
    catch(_: ClassCastException) {
      false
    }
    catch(_: NullPointerException) {
      false
    }
  }

  /**
   * A shared implementation for a mutable map entry.
   */
  protected inner class TrieEntry(
    override val key: List<E>,
    private var currentValue: V,
  ) : MutableMap.MutableEntry<List<E>, V> {
    override val value: V get() = currentValue
    override fun setValue(newValue: V): V {
      val oldValue = requireNotNull(put(key, newValue))
      this.currentValue = newValue
      return oldValue
    }

    override fun toString(): String = "$key=$currentValue"

    override fun hashCode(): Int = key.hashCode() xor value.hashCode()

    override fun equals(other: Any?): Boolean {
      if(this === other) return true
      if(other !is Map.Entry<*, *>) return false
      return key == other.key && value == other.value
    }
  }
}
