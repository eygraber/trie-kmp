package com.eygraber.trie

/**
 * A highly-optimized, memory-efficient Compact [MutableTrie] specialized for String keys.
 *
 * This implementation does NOT conform to the `Map` interface, allowing it to avoid the performance
 * overhead of `List<Char>` conversions and boxing. It is the recommended implementation for all
 * String-based use cases where performance is a priority.
 *
 * @param V The type of the values.
 */
public class CompactStringTrie<V> : Trie<String, V>, AbstractTrie<String, V>() {
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

          node.children.forEach { (_, childNode) ->
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
    var searchKey: CharSequence = key

    while(searchKey.isNotEmpty()) {
      val child = currentNode.children[searchKey[0]] ?: return null

      if(!searchKey.startsWith(child.keyPart)) {
        return null
      }

      searchKey = searchKey.subSequence(child.keyPart.length, searchKey.length)
      currentNode = child
    }

    return currentNode.value
  }

  @Suppress("ReturnCount")
  override fun put(key: String, value: V): V? {
    var currentNode = root
    var searchKey: CharSequence = key

    while(true) {
      if(searchKey.isEmpty()) {
        val oldValue = currentNode.value
        if(oldValue == null) size++
        currentNode.value = value
        return oldValue
      }

      val child = currentNode.children[searchKey[0]]
      if(child == null) {
        // No child with this prefix, create a new one
        currentNode.children[searchKey[0]] = CompactStringTrieNode(searchKey.toString(), value)
        size++
        return null
      }

      // Find the length of the common prefix between the search key and the child's key part
      val commonPrefixLength = searchKey.commonPrefixWith(child.keyPart).length

      if(commonPrefixLength == searchKey.length && commonPrefixLength == child.keyPart.length) {
        // Exact match, update value
        val oldValue = child.value
        if(oldValue == null) size++
        child.value = value
        return oldValue
      }

      if(commonPrefixLength < child.keyPart.length) {
        // Split the child node
        val oldChildKeyPart = child.keyPart.substring(commonPrefixLength)
        val newChild = CompactStringTrieNode(oldChildKeyPart, child.value).apply { children.putAll(child.children) }

        child.keyPart = child.keyPart.substring(0, commonPrefixLength)

        val remainingSearchKey = searchKey.subSequence(commonPrefixLength, searchKey.length)
        val oldValue = child.value
        child.value = if(remainingSearchKey.isEmpty()) value else null
        child.children.clear()
        child.children[newChild.keyPart[0]] = newChild

        if(remainingSearchKey.isNotEmpty()) {
          child.children[remainingSearchKey[0]] = CompactStringTrieNode(remainingSearchKey.toString(), value)
        }

        if(child.value != null) size++
        if(remainingSearchKey.isNotEmpty()) size++
        return oldValue
      }

      searchKey = searchKey.subSequence(commonPrefixLength, searchKey.length)
      currentNode = child
    }
  }

  override fun remove(key: String): V? {
    val (oldValue, _) = removeRecursive(root, key)
    if(oldValue != null) {
      size--
    }
    return oldValue
  }

  @Suppress("ReturnCount")
  private fun removeRecursive(
    currentNode: CompactStringTrieNode<V>,
    key: CharSequence,
  ): Pair<V?, Boolean> {
    if(key.isEmpty()) {
      val oldValue = currentNode.value
      currentNode.value = null
      return oldValue to (oldValue != null)
    }

    val firstChar = key[0]
    val child = currentNode.children[firstChar] ?: return null to false
    if(!key.startsWith(child.keyPart)) return null to false

    val remainingKey = key.subSequence(child.keyPart.length, key.length)
    val (oldValue, removed) = removeRecursive(child, remainingKey)

    if(removed) {
      // If a descendant was removed, check if we need to prune or merge the child
      if(!child.isKeyNode() && child.children.size == 1) {
        // Merge child with its single grandchild
        val grandchild = child.children.values.first()
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
    var searchKey: CharSequence = prefix

    while(searchKey.isNotEmpty()) {
      val child = currentNode.children[searchKey[0]] ?: return false

      if(!searchKey.startsWith(child.keyPart) && !child.keyPart.startsWith(searchKey)) {
        return false
      }

      if(searchKey.length <= child.keyPart.length) return true

      currentNode = child
      searchKey = searchKey.subSequence(child.keyPart.length, searchKey.length)
    }

    return true
  }

  @Suppress("ReturnCount")
  override fun getAllWithPrefix(prefix: String): Map<String, V> {
    val results = mutableMapOf<String, V>()
    var currentNode = root
    var searchKey: CharSequence = prefix
    val path = StringBuilder()

    while(searchKey.isNotEmpty()) {
      val child = currentNode.children[searchKey[0]] ?: return emptyMap()
      if(!searchKey.startsWith(child.keyPart) && !child.keyPart.startsWith(searchKey)) {
        return emptyMap()
      }

      path.append(child.keyPart)
      currentNode = child

      if(searchKey.length <= child.keyPart.length) break

      searchKey = searchKey.subSequence(child.keyPart.length, searchKey.length)
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
    var searchKey: CharSequence = prefix

    while(searchKey.isNotEmpty()) {
      val child = currentNode.children[searchKey[0]] ?: return emptyList()
      if(!searchKey.startsWith(child.keyPart) && !child.keyPart.startsWith(searchKey)) {
        return emptyList()
      }
      if(searchKey.length <= child.keyPart.length) {
        currentNode = child
        break
      }
      currentNode = child
      searchKey = searchKey.subSequence(child.keyPart.length, searchKey.length)
    }

    if(currentNode.isKeyNode()) {
      results.add(requireNotNull(currentNode.value))
    }
    collectAllValues(currentNode, results)
    return results
  }

  private fun collectAll(node: CompactStringTrieNode<V>, path: StringBuilder, results: MutableMap<String, V>) {
    node.children.forEach { (_, childNode) ->
      path.append(childNode.keyPart)
      if(childNode.isKeyNode()) {
        results[path.toString()] = requireNotNull(childNode.value)
      }
      collectAll(childNode, path, results)
      path.setLength(path.length - childNode.keyPart.length)
    }
  }

  private fun collectAllValues(node: CompactStringTrieNode<V>, results: MutableList<V>) {
    node.children.forEach { (_, childNode) ->
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
  val children: MutableMap<Char, CompactStringTrieNode<V>> = mutableMapOf()

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
