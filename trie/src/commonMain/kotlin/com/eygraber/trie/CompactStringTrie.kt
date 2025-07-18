package com.eygraber.trie

import androidx.collection.SimpleArrayMap

/**
 * A highly-optimized, memory-efficient Compact [MutableTrie] specialized for String keys.
 *
 * This implementation uses an index-based traversal to avoid creating intermediate
 * string objects, providing maximum performance.
 *
 * @param V The type of the values.
 */
public class CompactStringTrie<V> : Trie<String, V>, AbstractMutableTrie<String, V>() {
  private val root = CompactStringTrieNode<V>("")

  override var size: Int = 0
    private set

  override val entries: MutableSet<MutableMap.MutableEntry<String, V>>
    get() = object : AbstractMutableSet<MutableMap.MutableEntry<String, V>>() {
      override val size: Int get() = this@CompactStringTrie.size

      override fun iterator(): MutableIterator<MutableMap.MutableEntry<String, V>> {
        val allEntries = mutableListOf<MutableMap.MutableEntry<String, V>>()
        val path = StringBuilder()

        fun collect(node: CompactStringTrieNode<V>) {
          if(node.isKeyNode()) {
            allEntries.add(TrieEntry(path.toString(), requireNotNull(node.value)))
          }

          node.children.forEachValue { childNode ->
            path.append(childNode.keyPart)
            collect(childNode)
            path.setLength(path.length - childNode.keyPart.length) // Backtrack
          }
        }

        collect(root)

        val backingIterator = allEntries.iterator()
        var lastEntry: MutableMap.MutableEntry<String, V>? = null

        return object : MutableIterator<MutableMap.MutableEntry<String, V>> {
          override fun hasNext() = backingIterator.hasNext()

          override fun next(): MutableMap.MutableEntry<String, V> {
            lastEntry = backingIterator.next()
            return lastEntry
          }

          override fun remove() {
            checkNotNull(lastEntry)
            this@CompactStringTrie.remove(lastEntry.key)
            backingIterator.remove()
          }
        }
      }

      override fun add(element: MutableMap.MutableEntry<String, V>) = throw UnsupportedOperationException()
    }

  @Suppress("ReturnCount")
  override operator fun get(key: String): V? {
    var currentNode = root
    var keyIndex = 0
    while(keyIndex < key.length) {
      val child = currentNode.children[key[keyIndex]] ?: return null
      val commonPrefixLength = child.keyPart.commonPrefixLength(key, keyIndex)
      if(commonPrefixLength < child.keyPart.length) {
        return null // Key diverges
      }
      keyIndex += commonPrefixLength
      currentNode = child
    }
    return if(keyIndex == key.length) currentNode.value else null
  }

  @Suppress("ReturnCount")
  override fun put(key: String, value: V): V? {
    var currentNode = root
    var keyIndex = 0

    while(keyIndex < key.length) {
      val firstChar = key[keyIndex]
      val child = currentNode.children[firstChar]

      if(child == null) {
        // No child, create a new branch for the rest of the key
        currentNode.children[firstChar] = CompactStringTrieNode(key.substring(keyIndex), value)
        size++
        return null
      }

      val commonPrefixLength = child.keyPart.commonPrefixLength(key, keyIndex)

      if(commonPrefixLength == child.keyPart.length) {
        // The existing node's key is a prefix of our key.
        // Continue search from the child node.
        keyIndex += commonPrefixLength
        currentNode = child
        continue
      }

      // --- Split is required ---
      val remainderOfOriginalKey = child.keyPart.substring(commonPrefixLength)
      val remainderOfNewKey = key.substring(keyIndex + commonPrefixLength)

      val newOriginalNode = CompactStringTrieNode(remainderOfOriginalKey, child.value).apply {
        children.putAll(child.children)
      }

      child.keyPart = child.keyPart.substring(0, commonPrefixLength)
      child.children.clear()
      child.children[newOriginalNode.keyPart[0]] = newOriginalNode

      if(remainderOfNewKey.isEmpty()) {
        val oldValue = child.value
        child.value = value
        size++
        return oldValue
      }
      else {
        child.value = null
        child.children[remainderOfNewKey[0]] = CompactStringTrieNode(remainderOfNewKey, value)
        size++
        return null
      }
    }

    // This case handles putting a value at the root (empty string key).
    val oldValue = currentNode.value
    if(oldValue == null) size++
    currentNode.value = value
    return oldValue
  }

  override fun remove(key: String): V? {
    val (oldValue, _) = removeRecursive(root, key, 0)
    if(oldValue != null) {
      size--
    }
    return oldValue
  }

  @Suppress("ReturnCount")
  private fun removeRecursive(
    currentNode: CompactStringTrieNode<V>,
    key: String,
    keyIndex: Int,
  ): Pair<V?, Boolean> {
    if(keyIndex == key.length) {
      val oldValue = currentNode.value
      currentNode.value = null
      return oldValue to (oldValue != null)
    }

    val firstChar = key[keyIndex]
    val child = currentNode.children[firstChar] ?: return null to false

    val commonPrefixLength = child.keyPart.commonPrefixLength(key, keyIndex)
    if(commonPrefixLength < child.keyPart.length) return null to false

    val (oldValue, removed) = removeRecursive(child, key, keyIndex + commonPrefixLength)

    if(removed) {
      // If a descendant was removed, check if we need to prune or merge the child
      if(!child.isKeyNode() && child.children.size() == 1) {
        // Merge child with its single grandchild
        val grandchild = child.children.first()
        child.keyPart += grandchild.keyPart
        child.value = grandchild.value
        child.children.clear()
        child.children.putAll(grandchild.children)
      }
      else if(!child.isKeyNode() && child.children.isEmpty()) {
        // Prune the child if it's now an empty leaf
        currentNode.children.remove(firstChar)
      }
    }

    return oldValue to removed
  }

  override fun clear() {
    root.children.clear()
    root.value = null
    size = 0
  }

  @Suppress("ReturnCount")
  override fun startsWith(prefix: String): Boolean {
    var currentNode = root
    var keyIndex = 0
    while(keyIndex < prefix.length) {
      val child = currentNode.children[prefix[keyIndex]] ?: return false
      val commonPrefixLength = child.keyPart.commonPrefixLength(prefix, keyIndex)
      if(commonPrefixLength < child.keyPart.length && keyIndex + commonPrefixLength < prefix.length) {
        return false
      }
      if(prefix.length - keyIndex <= commonPrefixLength) {
        return true
      }
      keyIndex += commonPrefixLength
      currentNode = child
    }
    return true
  }

  @Suppress("ReturnCount")
  override fun getAllWithPrefix(prefix: String): Map<String, V> {
    val results = mutableMapOf<String, V>()
    var currentNode = root
    var keyIndex = 0
    val path = StringBuilder()

    while(keyIndex < prefix.length) {
      val child = currentNode.children[prefix[keyIndex]] ?: return emptyMap()
      val commonPrefixLength = child.keyPart.commonPrefixLength(prefix, keyIndex)

      if(commonPrefixLength < child.keyPart.length && keyIndex + commonPrefixLength < prefix.length) {
        return emptyMap()
      }

      path.append(child.keyPart)
      currentNode = child

      if(prefix.length - keyIndex <= commonPrefixLength) {
        break
      }

      keyIndex += commonPrefixLength
    }

    if(currentNode.isKeyNode()) {
      results[path.toString()] = requireNotNull(currentNode.value)
    }
    collectAll(currentNode, path, results)

    return results
  }

  @Suppress("ReturnCount")
  override fun getAllValuesWithPrefix(prefix: String): Collection<V> {
    var currentNode = root
    var keyIndex = 0

    while(keyIndex < prefix.length) {
      val child = currentNode.children[prefix[keyIndex]] ?: return emptyList()
      val commonPrefixLength = child.keyPart.commonPrefixLength(prefix, keyIndex)

      if(commonPrefixLength < child.keyPart.length && keyIndex + commonPrefixLength < prefix.length) {
        return emptyList()
      }

      if(prefix.length - keyIndex <= commonPrefixLength) {
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

  private fun collectAll(node: CompactStringTrieNode<V>, path: StringBuilder, results: MutableMap<String, V>) {
    node.children.forEachValue { childNode ->
      path.append(childNode.keyPart)
      if(childNode.isKeyNode()) {
        results[path.toString()] = requireNotNull(childNode.value)
      }
      collectAll(childNode, path, results)
      path.setLength(path.length - childNode.keyPart.length)
    }
  }

  private fun collectAllValues(node: CompactStringTrieNode<V>, results: MutableList<V>) {
    node.children.forEachValue { childNode ->
      if(childNode.isKeyNode()) {
        results.add(requireNotNull(childNode.value))
      }
      collectAllValues(childNode, results)
    }
  }
}

/**
 * A node in the high-performance [CompactStringTrie].
 * It stores a part of the key as a String.
 *
 * @param V The type of value stored in the Trie.
 */
private class CompactStringTrieNode<V>(
  var keyPart: String,
  var value: V? = null,
) {
  val children = SimpleArrayMap<Char, CompactStringTrieNode<V>>()

  fun isKeyNode(): Boolean = value != null

  override fun toString(): String = "$value=$children"

  override fun equals(other: Any?): Boolean {
    if(this === other) return true
    if(other == null || this::class != other::class) return false

    other as CompactStringTrieNode<*>

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
