package com.eygraber.trie

import androidx.collection.SimpleArrayMap

/**
 * A highly-optimized, memory-efficient Compact [MutableTrie] specialized for String keys.
 *
 * This implementation uses an index-based, "view" approach for its keys to eliminate
 * substring allocations during traversal and insertion, providing the highest possible performance.
 *
 * @param V The type of the values.
 */
public class CompactStringViewTrie<V> : Trie<String, V>, AbstractMutableTrie<String, V>() {
  private val root = CompactStringViewTrieNode<V>("", 0, 0)

  override var size: Int = 0
    private set

  override val entries: MutableSet<MutableMap.MutableEntry<String, V>>
    get() = object : AbstractMutableSet<MutableMap.MutableEntry<String, V>>() {
      override val size: Int get() = this@CompactStringViewTrie.size

      override fun iterator(): MutableIterator<MutableMap.MutableEntry<String, V>> {
        val allEntries = mutableListOf<MutableMap.MutableEntry<String, V>>()
        val path = StringBuilder()

        fun collect(node: CompactStringViewTrieNode<V>) {
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
            this@CompactStringViewTrie.remove(lastEntry.key)
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
      val commonPrefixLength = commonPrefixLength(child.sourceKey, child.keyPartStart, child.keyPartEnd, key, keyIndex)

      if(commonPrefixLength < child.keyPartLength) {
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
        // No child, create a new branch for the rest of the key.
        // The new node views the key we are currently inserting.
        currentNode.children[firstChar] = CompactStringViewTrieNode(key, keyIndex, key.length, value)
        size++
        return null
      }

      val commonPrefixLength = commonPrefixLength(child.sourceKey, child.keyPartStart, child.keyPartEnd, key, keyIndex)

      if(commonPrefixLength == child.keyPartLength) {
        // The existing node's key is a prefix of our key.
        // Continue search from the child node.
        keyIndex += commonPrefixLength
        currentNode = child
        continue
      }

      // --- Split is required ---
      val remainderOfNewKeyIndex = keyIndex + commonPrefixLength

      // 1. Create a new node for the remainder of the original key.
      val newOriginalNode = CompactStringViewTrieNode(
        sourceKey = child.sourceKey,
        keyPartStart = child.keyPartStart + commonPrefixLength,
        keyPartEnd = child.keyPartEnd,
        value = child.value,
      ).apply {
        children.putAll(child.children)
      }

      // 2. Repurpose the current child to be the common prefix node.
      child.keyPartEnd = child.keyPartStart + commonPrefixLength
      child.children.clear()
      child.children[newOriginalNode.keyPart[0]] = newOriginalNode

      // 3. Handle the new key's remainder.
      if(remainderOfNewKeyIndex == key.length) {
        // The new key is exactly the common prefix.
        val oldValue = child.value
        child.value = value
        size++
        return oldValue
      }
      else {
        // The new key diverges.
        child.value = null
        child.children[key[remainderOfNewKeyIndex]] = CompactStringViewTrieNode(
          sourceKey = key,
          keyPartStart = remainderOfNewKeyIndex,
          keyPartEnd = key.length,
          value = value,
        )
        size++
        return null
      }
    }

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
    currentNode: CompactStringViewTrieNode<V>,
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

    val commonPrefixLength = commonPrefixLength(child.sourceKey, child.keyPartStart, child.keyPartEnd, key, keyIndex)
    if(commonPrefixLength < child.keyPartLength) return null to false

    val (oldValue, removed) = removeRecursive(child, key, keyIndex + commonPrefixLength)

    if(removed) {
      // After a child has been modified or removed, check its state for pruning or merging.
      if(!child.isKeyNode() && child.children.size() == 1) {
        // This node is now just a pass-through. Merge it with its single child
        // to shorten the tree and mitigate memory retention.
        val grandchild = child.children.first()

        // Create a new, perfectly-sized string for the combined key part.
        val newKeyPart = child.keyPart.toString() + grandchild.keyPart.toString()

        child.sourceKey = newKeyPart
        child.keyPartStart = 0
        child.keyPartEnd = newKeyPart.length
        child.value = grandchild.value
        child.children.clear()
        child.children.putAll(grandchild.children)
      }
      else if(!child.isKeyNode() && child.children.isEmpty()) {
        // If the child is now an empty, non-key leaf, prune it from the tree.
        currentNode.children.remove(firstChar)
      }
      else if(child.sourceKey.length > child.keyPartLength * 2 && child.keyPartLength > 0) {
        // As a final mitigation, if the child node is still valid but its sourceKey
        // is much larger than its keyPart view, compact it to release the
        // reference to the large source string.
        val newKeyPart = child.keyPart.toString()
        child.sourceKey = newKeyPart
        child.keyPartStart = 0
        child.keyPartEnd = newKeyPart.length
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
      val commonPrefixLength =
        commonPrefixLength(child.sourceKey, child.keyPartStart, child.keyPartEnd, prefix, keyIndex)
      if(commonPrefixLength < child.keyPartLength && keyIndex + commonPrefixLength < prefix.length) {
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
      val commonPrefixLength =
        commonPrefixLength(child.sourceKey, child.keyPartStart, child.keyPartEnd, prefix, keyIndex)
      if(commonPrefixLength < child.keyPartLength && keyIndex + commonPrefixLength < prefix.length) {
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
    val results = mutableListOf<V>()
    var currentNode = root
    var keyIndex = 0

    while(keyIndex < prefix.length) {
      val child = currentNode.children[prefix[keyIndex]] ?: return emptyList()
      val commonPrefixLength =
        commonPrefixLength(child.sourceKey, child.keyPartStart, child.keyPartEnd, prefix, keyIndex)
      if(commonPrefixLength < child.keyPartLength && keyIndex + commonPrefixLength < prefix.length) {
        return emptyList()
      }

      if(prefix.length - keyIndex <= commonPrefixLength) {
        currentNode = child
        break
      }

      currentNode = child
      keyIndex += commonPrefixLength
    }

    if(currentNode.isKeyNode()) {
      results.add(requireNotNull(currentNode.value))
    }
    collectAllValues(currentNode, results)
    return results
  }

  private fun collectAll(node: CompactStringViewTrieNode<V>, path: StringBuilder, results: MutableMap<String, V>) {
    node.children.forEachValue { childNode ->
      path.append(childNode.keyPart)
      if(childNode.isKeyNode()) {
        results[path.toString()] = requireNotNull(childNode.value)
      }
      collectAll(childNode, path, results)
      path.setLength(path.length - childNode.keyPart.length)
    }
  }

  private fun collectAllValues(node: CompactStringViewTrieNode<V>, results: MutableList<V>) {
    node.children.forEachValue { childNode ->
      if(childNode.isKeyNode()) {
        results.add(requireNotNull(childNode.value))
      }
      collectAllValues(childNode, results)
    }
  }
}

/**
 * A node in the ultimate-performance String Compact Trie.
 * It stores a "view" of a key part using a reference to the original source string
 * and start/end indices, avoiding the allocation of substring objects.
 *
 * @param V The type of value stored in the Trie.
 */
private class CompactStringViewTrieNode<V>(
  // A reference to the original string that this node's key part comes from.
  var sourceKey: String,
  // The start index (inclusive) of this node's key part within sourceKey.
  var keyPartStart: Int,
  // The end index (exclusive) of this node's key part within sourceKey.
  var keyPartEnd: Int,
  var value: V? = null,
) {
  val children = SimpleArrayMap<Char, CompactStringViewTrieNode<V>>()

  val keyPart: CharSequence
    get() = sourceKey.subSequence(keyPartStart, keyPartEnd)

  val keyPartLength: Int
    get() = keyPartEnd - keyPartStart

  fun isKeyNode(): Boolean = value != null

  override fun toString(): String = "$value=$children"
  override fun equals(other: Any?): Boolean {
    if(this === other) return true
    if(other == null || this::class != other::class) return false

    other as CompactStringViewTrieNode<*>

    if(keyPartStart != other.keyPartStart) return false
    if(keyPartEnd != other.keyPartEnd) return false
    if(sourceKey != other.sourceKey) return false
    if(value != other.value) return false
    if(children != other.children) return false

    return true
  }

  override fun hashCode(): Int {
    var result = keyPartStart
    result = 31 * result + keyPartEnd
    result = 31 * result + sourceKey.hashCode()
    result = 31 * result + (value?.hashCode() ?: 0)
    result = 31 * result + children.hashCode()
    return result
  }
}

/**
 * An allocation-free helper to find the length of the common prefix between a node's key part
 * (represented by a view) and the search key (represented by the full string and an offset).
 */
private fun commonPrefixLength(
  nodeKey: String,
  nodeStart: Int,
  nodeEnd: Int,
  searchKey: String,
  searchStart: Int,
): Int {
  val nodeLength = nodeEnd - nodeStart
  val searchLength = searchKey.length - searchStart
  val minLength = minOf(nodeLength, searchLength)
  for(i in 0 until minLength) {
    if(nodeKey[nodeStart + i] != searchKey[searchStart + i]) {
      return i
    }
  }
  return minLength
}
