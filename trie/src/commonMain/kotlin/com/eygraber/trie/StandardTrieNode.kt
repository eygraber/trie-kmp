package com.eygraber.trie

import androidx.collection.SimpleArrayMap

/**
 * Represents a node in a [Trie], mapping sequence elements to values.
 *
 * @param K The type of element in the key sequence.
 * @param V The type of value stored in the Trie.
 */
internal class StandardTrieNode<K, V> {
  /**
   * A map where keys are sequence elements and values are the corresponding child [StandardTrieNode]s.
   */
  val children = SimpleArrayMap<K, StandardTrieNode<K, V>>()

  /**
   * The value associated with the key that ends at this node.
   * If null, this node is not the end of a complete key.
   */
  var value: V? = null

  fun isKeyNode(): Boolean = value != null

  override fun toString(): String = "$value=$children"

  override fun equals(other: Any?): Boolean {
    if(this === other) return true
    if(other == null || this::class != other::class) return false

    other as StandardTrieNode<*, *>

    if(children != other.children) return false
    if(value != other.value) return false

    return true
  }

  override fun hashCode(): Int {
    var result = children.hashCode()
    result = 31 * result + (value?.hashCode() ?: 0)
    return result
  }
}
