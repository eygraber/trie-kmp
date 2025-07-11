package com.eygraber.trie

/**
 * A concrete implementation of a mutable, map-like Trie.
 *
 * @param K The type of the elements in the key sequences.
 * @param V The type of the values.
 */
public class StandardGenericTrie<K, V> : MutableGenericTrie<K, V>, AbstractTrie<List<K>, V>() {
  private val root = StandardTrieNode<K, V>()
  private var internalSize = 0

  override val size: Int get() = internalSize

  override val entries: MutableSet<MutableMap.MutableEntry<List<K>, V>>
    get() = object : AbstractMutableSet<MutableMap.MutableEntry<List<K>, V>>() {
      override val size: Int get() = this@StandardGenericTrie.size

      override fun iterator(): MutableIterator<MutableMap.MutableEntry<List<K>, V>> {
        val allEntries = mutableListOf<MutableMap.MutableEntry<List<K>, V>>()
        val path = mutableListOf<K>()

        fun collectAll(node: StandardTrieNode<K, V>) {
          node.value?.let { allEntries.add(TrieEntry(path.toList(), it)) }
          node.children.forEach { (elem, child) ->
            path.add(elem)
            collectAll(child)
            path.removeAt(path.lastIndex)
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
            this@StandardGenericTrie.remove(lastEntry.key)
            backingIterator.remove()
          }
        }
      }

      override fun add(element: MutableMap.MutableEntry<List<K>, V>): Boolean = throw UnsupportedOperationException()
    }

  override fun get(key: List<K>): V? = findNode(key)?.value

  override fun put(key: List<K>, value: V): V? {
    var currentNode = root
    for(element in key) {
      currentNode = currentNode.children.getOrPut(element) { StandardTrieNode() }
    }

    val oldValue = currentNode.value
    currentNode.value = value

    if(oldValue == null) {
      internalSize++
    }
    return oldValue
  }

  @Suppress("ReturnCount")
  override fun remove(key: List<K>): V? {
    if(key.isEmpty()) {
      val oldValue = root.value
      if(oldValue != null) {
        root.value = null
        internalSize--
      }
      return oldValue
    }

    // 1. Find the node and collect the path
    val path = mutableListOf<StandardTrieNode<K, V>>()
    path.add(root)
    var currentNode: StandardTrieNode<K, V>? = root
    for(element in key) {
      currentNode = currentNode?.children?.get(element)
      if(currentNode == null) {
        return null // Key not in trie
      }
      path.add(currentNode)
    }

    val targetNode = path.last()
    val oldValue = targetNode.value ?: return null // Key exists as a prefix, but not as a full key

    // 2. Remove the value
    targetNode.value = null
    internalSize--

    // 3. Prune unnecessary nodes by walking back up the path
    for(i in path.size - 2 downTo 0) {
      val parentNode = path[i]
      val childNode = path[i + 1]
      val elementForChild = key[i]

      // If the child node is now a leaf (no children) and not a key itself, remove it
      if(childNode.children.isEmpty() && childNode.value == null) {
        parentNode.children.remove(elementForChild)
      }
      else {
        // Once we find a node that shouldn't be removed, we can stop
        break
      }
    }

    return oldValue
  }

  override fun clear() {
    root.children.clear()
    internalSize = 0
  }

  override fun startsWith(prefix: List<K>): Boolean = findNode(prefix) != null

  override fun getAllWithPrefix(prefix: List<K>): Map<List<K>, V> {
    val prefixNode = findNode(prefix) ?: return emptyMap()

    val result = mutableMapOf<List<K>, V>()
    val path = prefix.toMutableList()

    // If the prefix itself is a key, add it.
    prefixNode.value?.let {
      result[path.toList()] = it
    }

    findAllFromNode(prefixNode, path, result)
    return result
  }

  override fun getAllValuesWithPrefix(prefix: List<K>): Collection<V> {
    val prefixNode = findNode(prefix) ?: return emptyList()
    val result = mutableListOf<V>()
    // If the prefix itself is a key, add its value.
    prefixNode.value?.let {
      result.add(it)
    }
    findAllValuesFromNode(prefixNode, result)
    return result
  }

  private fun findNode(key: List<K>): StandardTrieNode<K, V>? {
    var currentNode = root
    for(element in key) {
      currentNode = currentNode.children[element] ?: return null
    }
    return currentNode
  }

  private fun findAllFromNode(
    node: StandardTrieNode<K, V>,
    path: MutableList<K>,
    result: MutableMap<List<K>, V>,
  ) {
    for((element, childNode) in node.children) {
      path.add(element)
      childNode.value?.let {
        result[path.toList()] = it
      }
      findAllFromNode(childNode, path, result)
      path.removeAt(path.lastIndex) // Backtrack
    }
  }

  private fun findAllValuesFromNode(
    node: StandardTrieNode<K, V>,
    result: MutableList<V>,
  ) {
    for((_, childNode) in node.children) {
      childNode.value?.let {
        result.add(it)
      }
      findAllValuesFromNode(childNode, result)
    }
  }
}
