package com.eygraber.trie

/**
 * A compact implementation of a [MutableTrie] and [GenericTrie].
 *
 * @param K The type of the elements in the key sequences.
 * @param V The type of the values.
 */
public class CompactGenericTrie<K, V> : MutableGenericTrie<K, V>, AbstractTrie<List<K>, V>() {
  private val root = CompactTrieNode<K, V>(emptyList())
  private var internalSize = 0

  override val size: Int get() = internalSize

  override val entries: MutableSet<MutableMap.MutableEntry<List<K>, V>>
    get() = object : AbstractMutableSet<MutableMap.MutableEntry<List<K>, V>>() {
      override val size: Int get() = this@CompactGenericTrie.size

      override fun iterator(): MutableIterator<MutableMap.MutableEntry<List<K>, V>> {
        val allEntries = mutableListOf<MutableMap.MutableEntry<List<K>, V>>()

        fun collectAll(node: CompactTrieNode<K, V>, currentPath: MutableList<K>) {
          node.value?.let { allEntries.add(TrieEntry(currentPath.toList(), it)) }
          node.children.values.forEach { child ->
            currentPath.addAll(child.keyPart)
            collectAll(child, currentPath)
            repeat(child.keyPart.size) { currentPath.removeAt(currentPath.lastIndex) }
          }
        }

        collectAll(root, mutableListOf())

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
    var searchKey = key

    while(searchKey.isNotEmpty()) {
      val child = currentNode.children[searchKey.first()] ?: return null
      val commonPrefixLength = child.keyPart.zip(searchKey).takeWhile { it.first == it.second }.count()

      if(commonPrefixLength < child.keyPart.size) {
        return null // Key diverges within a node's keyPart
      }

      currentNode = child
      searchKey = searchKey.drop(commonPrefixLength)
    }
    return currentNode.value
  }

  @Suppress("ReturnCount")
  override fun put(key: List<K>, value: V): V? {
    var currentNode = root
    var searchKey = key

    while(true) {
      if(searchKey.isEmpty()) {
        val oldValue = currentNode.value
        if(oldValue == null) internalSize++
        currentNode.value = value
        return oldValue
      }

      val child = currentNode.children[searchKey.first()]
      if(child == null) {
        // No child with this prefix, create a new one
        currentNode.children[searchKey.first()] = CompactTrieNode(searchKey, value)
        internalSize++
        return null
      }

      val commonPrefixLength = child.keyPart.zip(searchKey).takeWhile { it.first == it.second }.count()

      if(commonPrefixLength == searchKey.size && commonPrefixLength == child.keyPart.size) {
        // Exact match, update value
        val oldValue = child.value
        if(oldValue == null) internalSize++
        child.value = value
        return oldValue
      }

      if(commonPrefixLength < child.keyPart.size) {
        // Split the child node
        val oldChildKeyPart = child.keyPart.drop(commonPrefixLength)
        val newChild = CompactTrieNode(oldChildKeyPart, child.value)
        newChild.children.putAll(child.children)

        child.keyPart = child.keyPart.take(commonPrefixLength)
        child.value = null
        child.children.clear()
        child.children[newChild.keyPart.first()] = newChild

        val remainingSearchKey = searchKey.drop(commonPrefixLength)
        if(remainingSearchKey.isEmpty()) {
          child.value = value
          internalSize++
          return null
        }
        else {
          val newBranch = CompactTrieNode(remainingSearchKey, value)
          child.children[newBranch.keyPart.first()] = newBranch
          internalSize++
          return null
        }
      }

      // Move to the next node
      searchKey = searchKey.drop(commonPrefixLength)
      currentNode = child
    }
  }

  @Suppress("ReturnCount")
  override fun remove(key: List<K>): V? {
    var parent: CompactTrieNode<K, V>? = null
    var currentNode = root
    var searchKey = key

    while(searchKey.isNotEmpty()) {
      val child = currentNode.children[searchKey.first()] ?: return null
      val commonPrefixLength = child.keyPart.zip(searchKey).takeWhile { it.first == it.second }.count()

      if(commonPrefixLength < child.keyPart.size) return null

      parent = currentNode
      currentNode = child
      searchKey = searchKey.drop(commonPrefixLength)
    }

    if(searchKey.isNotEmpty() || !currentNode.isKeyNode()) return null

    val oldValue = currentNode.value
    currentNode.value = null
    internalSize--

    if(currentNode.children.isEmpty()) {
      // Leaf node, can be removed
      parent?.children?.remove(key.first())
    }
    else if(currentNode.children.size == 1 && parent != null) {
      // Node with one child, needs merging with that child
      val child = currentNode.children.values.first()
      currentNode.keyPart = currentNode.keyPart + child.keyPart
      currentNode.value = child.value
      currentNode.children.clear()
      currentNode.children.putAll(child.children)
    }

    return oldValue
  }

  override fun clear() {
    root.children.clear()
    root.value = null
    internalSize = 0
  }

  @Suppress("ReturnCount")
  override fun startsWith(prefix: List<K>): Boolean {
    var currentNode = root
    var searchKey = prefix
    while(searchKey.isNotEmpty()) {
      val child = currentNode.children[searchKey.first()] ?: return false
      val commonPrefixLength = child.keyPart.zip(searchKey).takeWhile { it.first == it.second }.count()

      if(commonPrefixLength < searchKey.size && commonPrefixLength < child.keyPart.size) {
        return false // Prefix diverges inside a node
      }

      if(searchKey.size <= commonPrefixLength) {
        return true // Prefix is a prefix of or equal to the node's key part
      }

      if(commonPrefixLength != child.keyPart.size) {
        return false // Prefix is longer but doesn't match the whole key part
      }
      currentNode = child
      searchKey = searchKey.drop(commonPrefixLength)
    }

    return true
  }

  @Suppress("ReturnCount")
  override fun getAllWithPrefix(prefix: List<K>): Map<List<K>, V> {
    val results = mutableMapOf<List<K>, V>()
    var currentNode = root
    var searchKey = prefix
    val path = mutableListOf<K>()

    while(searchKey.isNotEmpty()) {
      val child = currentNode.children[searchKey.first()] ?: return emptyMap()
      val commonPrefixLength = child.keyPart.zip(searchKey).takeWhile { it.first == it.second }.count()

      if(commonPrefixLength < searchKey.size && commonPrefixLength < child.keyPart.size) {
        return emptyMap()
      }

      path.addAll(child.keyPart)
      currentNode = child

      if(searchKey.size <= commonPrefixLength) {
        // Prefix ends within this node
        break
      }

      searchKey = searchKey.drop(commonPrefixLength)
    }

    // At this point, `currentNode` is the node containing the end of the prefix.
    // `path` holds the full key to this node.
    if(currentNode.isKeyNode()) {
      results[path.toList()] = requireNotNull(currentNode.value)
    }
    collectAll(currentNode, path, results)

    return results
  }

  @Suppress("ReturnCount")
  override fun getAllValuesWithPrefix(prefix: List<K>): Collection<V> {
    var currentNode = root
    var searchKey = prefix

    while(searchKey.isNotEmpty()) {
      val child = currentNode.children[searchKey.first()] ?: return emptyList()
      val commonPrefixLength = child.keyPart.zip(searchKey).takeWhile { it.first == it.second }.count()

      if(commonPrefixLength < searchKey.size && commonPrefixLength < child.keyPart.size) {
        return emptyList()
      }

      if(searchKey.size <= commonPrefixLength) {
        currentNode = child
        break
      }

      currentNode = child
      searchKey = searchKey.drop(commonPrefixLength)
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
    node.children.forEach { (_, childNode) ->
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
    node.children.forEach { (_, childNode) ->
      if(childNode.isKeyNode()) {
        results.add(requireNotNull(childNode.value))
      }
      collectAllValues(childNode, results)
    }
  }

  private fun collectAllFromNode(
    node: CompactTrieNode<K, V>,
    currentPath: MutableList<K>,
    results: MutableMap<List<K>, V>,
  ) {
    if(node.isKeyNode()) {
      results[currentPath.toList()] = requireNotNull(node.value)
    }
    node.children.values.forEach { child ->
      currentPath.addAll(child.keyPart)
      collectAllFromNode(child, currentPath, results)
      repeat(child.keyPart.size) { currentPath.removeAt(currentPath.lastIndex) }
    }
  }

  private fun collectAllValuesFromNode(node: CompactTrieNode<K, V>, results: MutableList<V>) {
    if(node.isKeyNode()) {
      results.add(requireNotNull(node.value))
    }
    node.children.values.forEach { child ->
      collectAllValuesFromNode(child, results)
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
  val children: MutableMap<K, CompactTrieNode<K, V>> = mutableMapOf()

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
