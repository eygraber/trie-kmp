package com.eygraber.trie

public abstract class AbstractTrie<K, V> : Trie<K, V> {
  abstract override val entries: Set<Map.Entry<K, V>>

  abstract override val size: Int

  override val keys: Set<K>
    get() = object : AbstractSet<K>() {
      override val size: Int get() = this@AbstractTrie.size

      override fun iterator(): Iterator<K> =
        @Suppress("IteratorNotThrowingNoSuchElementException")
        object : Iterator<K> {
          private val entryIterator = entries.iterator()
          override fun hasNext(): Boolean = entryIterator.hasNext()
          override fun next(): K = entryIterator.next().key
        }
    }

  override val values: Collection<V>
    get() = object : AbstractCollection<V>() {
      override val size: Int get() = this@AbstractTrie.size

      override fun iterator(): Iterator<V> =
        @Suppress("IteratorNotThrowingNoSuchElementException")
        object : Iterator<V> {
          private val entryIterator = entries.iterator()
          override fun hasNext(): Boolean = entryIterator.hasNext()
          override fun next(): V = entryIterator.next().value
        }
    }

  abstract override fun startsWith(prefix: K): Boolean
  abstract override fun getAllWithPrefix(prefix: K): Map<K, V>
  abstract override fun getAllValuesWithPrefix(prefix: K): Collection<V>

  override fun isEmpty(): Boolean = size == 0

  override fun containsValue(value: V): Boolean = value in values

  override fun containsKey(key: K): Boolean = get(key) != null

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
}

public abstract class AbstractMutableTrie<K, V> : MutableTrie<K, V>, AbstractTrie<K, V>() {
  abstract override val entries: MutableSet<MutableMap.MutableEntry<K, V>>

  override val keys: MutableSet<K>
    get() = object : AbstractMutableSet<K>() {
      override val size: Int get() = this@AbstractMutableTrie.size

      override fun iterator(): MutableIterator<K> =
        object : MutableIterator<K> {
          private val entryIterator = entries.iterator()
          override fun hasNext(): Boolean = entryIterator.hasNext()
          override fun next(): K = entryIterator.next().key
          override fun remove() {
            entryIterator.remove()
          }
        }

      override fun add(element: K): Boolean = throw UnsupportedOperationException()
    }

  override val values: MutableCollection<V>
    get() = object : AbstractMutableCollection<V>() {
      override val size: Int get() = this@AbstractMutableTrie.size

      override fun iterator(): MutableIterator<V> =
        object : MutableIterator<V> {
          private val entryIterator = entries.iterator()

          override fun hasNext(): Boolean = entryIterator.hasNext()
          override fun next(): V = entryIterator.next().value
          override fun remove() {
            entryIterator.remove()
          }
        }

      override fun add(element: V): Boolean = throw UnsupportedOperationException()
    }

  abstract override fun put(key: K, value: V): V?
  abstract override fun remove(key: K): V?
  abstract override fun clear()

  override fun putAll(from: Map<out K, V>) {
    from.forEach { (key, value) -> put(key, value) }
  }

  /**
   * A shared implementation for a mutable map entry.
   */
  protected inner class TrieEntry(
    override val key: K,
    private var currentValue: V,
  ) : MutableMap.MutableEntry<K, V> {
    override val value: V get() = currentValue
    override fun setValue(newValue: V): V {
      val oldValue = requireNotNull(put(key, newValue))
      this.currentValue = newValue
      return oldValue
    }

    @Suppress("NullableToStringCall")
    override fun toString(): String = "$key=$currentValue"

    override fun hashCode(): Int = key.hashCode() xor value.hashCode()

    override fun equals(other: Any?): Boolean {
      if(this === other) return true
      if(other !is Map.Entry<*, *>) return false
      return key == other.key && value == other.value
    }
  }
}
