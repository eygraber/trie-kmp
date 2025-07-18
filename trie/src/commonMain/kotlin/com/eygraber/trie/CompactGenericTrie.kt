package com.eygraber.trie

import androidx.collection.SimpleArrayMap

/**
 * A compact implementation of a [MutableTrie] and [GenericTrie].
 *
 * @param K The type of the elements in the key sequences.
 * @param V The type of the values.
 */
public class CompactGenericTrie<K, V> : MutableGenericTrie<K, V>, AbstractMutableTrie<List<K>, V>() {
  private val root = CompactTrieNode<K, V>(emptyList())
  private var internalSize = 0

  override val size: Int get() = internalSize

  override val entries: MutableSet<MutableMap.MutableEntry<List<K>, V>>
    get() = object : AbstractMutableSet<MutableMap.MutableEntry<List<K>, V>>() {
      override val size: Int get() = this@CompactGenericTrie.size

      override fun iterator(): MutableIterator<MutableMap.MutableEntry<List<K>, V>> {
        val allEntries = mutableListOf<MutableMap.MutableEntry<List<K>, V>>()
        val path = mutableListOf<K>()

        fun collectAll(node: CompactTrieNode<K, V>) {
          node.value?.let { allEntries.add(TrieEntry(path.toList(), it)) }
          node.children.forEachValue { child ->
            path.addAll(child.keyPart)
            collectAll(child)
            repeat(child.keyPart.size) { path.removeAt(path.lastIndex) }
          }
        }

        collectAll(root)

        val backingIterator = allEntries.iterator()
        var lastEntry: MutableMap.MutableEntry<List<K>, V>? = null

        return object : MutableIterator<MutableMap.MutableEntry<List<K>, V>> {
          override fun hasNext(): Boolean = backingIterator.hasNext()

          override fun next(): MutableMap.MutableEntry<List<K>, V> {
            val entry = backingIterator.next()
            lastEntry = entry
            return entry
          }

          override fun remove() {
            checkNotNull(lastEntry)
            this@CompactGenericTrie.remove(lastEntry.key)
            backingIterator.remove()
          }
        }
      }

      override fun add(element: MutableMap.MutableEntry<List<K>, V>): Boolean = throw UnsupportedOperationException()
    }

  @Suppress("ReturnCount")
  override fun get(key: List<K>): V? {
    var currentNode = root
    var keyIndex = 0
    while(keyIndex < key.size) {
      val child = currentNode.children[key[keyIndex]] ?: return null
      val commonPrefixLength = child.keyPart.commonPrefixLength(key, keyIndex)
      if(commonPrefixLength < child.keyPart.size) {
        return null // Key diverges
      }
      keyIndex += commonPrefixLength
      currentNode = child
    }
    return if(keyIndex == key.size) currentNode.value else null
  }

  @Suppress("ReturnCount")
  override fun put(key: List<K>, value: V): V? {
    var currentNode = root
    var keyIndex = 0

    while(keyIndex < key.size) {
      val firstElement = key[keyIndex]
      val child = currentNode.children[firstElement]

      if(child == null) {
        // No child, create a new branch for the rest of the key
        currentNode.children[firstElement] = CompactTrieNode(key.subList(keyIndex, key.size), value)
        internalSize++
        return null
      }

      val commonPrefixLength = child.keyPart.commonPrefixLength(key, keyIndex)

      if(commonPrefixLength == child.keyPart.size) {
        // The existing node's key is a prefix of our key.
        // Continue search from the child node.
        keyIndex += commonPrefixLength
        currentNode = child
        continue
      }

      // --- Split is required ---
      val remainderOfOriginalKey = child.keyPart.subList(commonPrefixLength, child.keyPart.size)
      val remainderOfNewKey = key.subList(keyIndex + commonPrefixLength, key.size)

      val newOriginalNode = CompactTrieNode(remainderOfOriginalKey, child.value).apply {
        children.putAll(child.children)
      }

      child.keyPart = child.keyPart.subList(0, commonPrefixLength)
      child.children.clear()
      child.children[newOriginalNode.keyPart.first()] = newOriginalNode

      if(remainderOfNewKey.isEmpty()) {
        val oldValue = child.value
        child.value = value
        internalSize++
        return oldValue
      }
      else {
        child.value = null
        child.children[remainderOfNewKey.first()] = CompactTrieNode(remainderOfNewKey, value)
        internalSize++
        return null
      }
    }

    // This case handles putting a value at the root (empty list key).
    val oldValue = currentNode.value
    if(oldValue == null) internalSize++
    currentNode.value = value
    return oldValue
  }

  @Suppress("ReturnCount")
  override fun remove(key: List<K>): V? {
    val (oldValue, _) = removeRecursive(root, key, 0)
    if(oldValue != null) {
      internalSize--
    }
    return oldValue
  }

  @Suppress("ReturnCount")
  private fun removeRecursive(
    currentNode: CompactTrieNode<K, V>,
    key: List<K>,
    keyIndex: Int,
  ): Pair<V?, Boolean> {
    if(keyIndex == key.size) {
      val oldValue = currentNode.value
      currentNode.value = null
      return oldValue to (oldValue != null)
    }

    val firstElement = key[keyIndex]
    val child = currentNode.children[firstElement] ?: return null to false

    val commonPrefixLength = child.keyPart.commonPrefixLength(key, keyIndex)
    if(commonPrefixLength < child.keyPart.size) return null to false

    val (oldValue, removed) = removeRecursive(child, key, keyIndex + commonPrefixLength)

    if(removed) {
      if(!child.isKeyNode() && child.children.size() == 1) {
        val grandchild = child.children.first()
        child.keyPart = child.keyPart + grandchild.keyPart
        child.value = grandchild.value
        child.children.clear()
        child.children.putAll(grandchild.children)
      }
      else if(!child.isKeyNode() && child.children.isEmpty()) {
        currentNode.children.remove(firstElement)
      }
    }

    return oldValue to removed
  }

  override fun clear() {
    root.children.clear()
    root.value = null
    internalSize = 0
  }

  @Suppress("ReturnCount")
  override fun startsWith(prefix: List<K>): Boolean {
    var currentNode = root
    var keyIndex = 0
    while(keyIndex < prefix.size) {
      val child = currentNode.children[prefix[keyIndex]] ?: return false
      val commonPrefixLength = child.keyPart.commonPrefixLength(prefix, keyIndex)
      if(commonPrefixLength < child.keyPart.size && keyIndex + commonPrefixLength < prefix.size) {
        return false
      }
      if(prefix.size - keyIndex <= commonPrefixLength) {
        return true
      }
      keyIndex += commonPrefixLength
      currentNode = child
    }
    return true
  }

  @Suppress("ReturnCount")
  override fun getAllWithPrefix(prefix: List<K>): Map<List<K>, V> {
    val results = mutableMapOf<List<K>, V>()
    var currentNode = root
    var keyIndex = 0
    val path = mutableListOf<K>()

    while(keyIndex < prefix.size) {
      val child = currentNode.children[prefix[keyIndex]] ?: return emptyMap()
      val commonPrefixLength = child.keyPart.commonPrefixLength(prefix, keyIndex)

      if(commonPrefixLength < child.keyPart.size && keyIndex + commonPrefixLength < prefix.size) {
        return emptyMap()
      }

      path.addAll(child.keyPart)
      currentNode = child

      if(prefix.size - keyIndex <= commonPrefixLength) {
        break
      }

      keyIndex += commonPrefixLength
    }

    if(currentNode.isKeyNode()) {
      results[path.toList()] = requireNotNull(currentNode.value)
    }
    collectAll(currentNode, path, results)

    return results
  }

  @Suppress("ReturnCount")
  override fun getAllValuesWithPrefix(prefix: List<K>): Collection<V> {
    var currentNode = root
    var keyIndex = 0

    while(keyIndex < prefix.size) {
      val child = currentNode.children[prefix[keyIndex]] ?: return emptyList()
      val commonPrefixLength = child.keyPart.commonPrefixLength(prefix, keyIndex)

      if(commonPrefixLength < child.keyPart.size && keyIndex + commonPrefixLength < prefix.size) {
        return emptyList()
      }

      if(prefix.size - keyIndex <= commonPrefixLength) {
        currentNode = child
        break
      }

      currentNode = child
      keyIndex += commonPrefixLength
    }

    val results = mutableListOf<V>()
    if(currentNode.isKeyNode()) {
      results.add(requireNotNull(currentNode.value))
    }
    collectAllValues(currentNode, results)
    return results
  }

  private fun collectAll(
    node: CompactTrieNode<K, V>,
    path: MutableList<K>,
    results: MutableMap<List<K>, V>,
  ) {
    node.children.forEachValue { childNode ->
      path.addAll(childNode.keyPart)
      if(childNode.isKeyNode()) {
        results[path.toList()] = requireNotNull(childNode.value)
      }
      collectAll(childNode, path, results)
      repeat(childNode.keyPart.size) { path.removeAt(path.lastIndex) }
    }
  }

  private fun collectAllValues(
    node: CompactTrieNode<K, V>,
    results: MutableList<V>,
  ) {
    node.children.forEachValue { childNode ->
      if(childNode.isKeyNode()) {
        results.add(requireNotNull(childNode.value))
      }
      collectAllValues(childNode, results)
    }
  }
}

/**
 * Represents a node in a [CompactGenericTrie].
 * Each node stores a part of a key (a prefix) rather than a single character.
 *
 * @param K The type of element in the key sequence.
 * @param V The type of value stored in the Trie.
 */
internal class CompactTrieNode<K, V>(
  var keyPart: List<K>,
  var value: V? = null,
) {
  val children = SimpleArrayMap<K, CompactTrieNode<K, V>>()

  fun isKeyNode(): Boolean = value != null

  override fun toString(): String = "$value=$children"

  override fun equals(other: Any?): Boolean {
    if(this === other) return true
    if(other == null || this::class != other::class) return false

    other as CompactTrieNode<*, *>

    if(keyPart != other.keyPart) return false
    if(value != other.value) return false
    if(children != other.children) return false

    return true
  }

  override fun hashCode(): Int {
    var result = keyPart.hashCode()
    result = 31 * result + (value?.hashCode() ?: 0)
    result = 31 * result + children.hashCode()
    return result
  }
}
