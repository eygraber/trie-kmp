package com.eygraber.trie

/**
 * A concrete implementation of a mutable, map-like Trie.
 *
 * @param E The type of the elements in the key sequences.
 * @param V The type of the values.
 */
public class MapTrie<E, V> : AbstractTrie<E, V>() {
  private val root = MapTrieNode<E, V>()
  private var internalSize = 0

  override val size: Int get() = internalSize

  override val entries: MutableSet<MutableMap.MutableEntry<List<E>, V>>
    get() = object : AbstractMutableSet<MutableMap.MutableEntry<List<E>, V>>() {
      override val size: Int get() = this@MapTrie.size
      override fun iterator(): MutableIterator<MutableMap.MutableEntry<List<E>, V>> {
        val allEntries = mutableListOf<MutableMap.MutableEntry<List<E>, V>>()
        val path = mutableListOf<E>()
        fun collectAll(node: MapTrieNode<E, V>) {
          node.value?.let { allEntries.add(TrieEntry(path.toList(), it)) }
          node.children.forEach { (elem, child) ->
            path.add(elem)
            collectAll(child)
            path.removeAt(path.lastIndex)
          }
        }
        collectAll(root)

        val backingIterator = allEntries.iterator()
        var lastEntry: MutableMap.MutableEntry<List<E>, V>? = null

        return object : MutableIterator<MutableMap.MutableEntry<List<E>, V>> {
          override fun hasNext(): Boolean = backingIterator.hasNext()
          override fun next(): MutableMap.MutableEntry<List<E>, V> {
            val entry = backingIterator.next()
            lastEntry = entry
            return entry
          }

          override fun remove() {
            checkNotNull(lastEntry)
            this@MapTrie.remove(lastEntry.key)
            backingIterator.remove()
          }
        }
      }

      override fun add(element: MutableMap.MutableEntry<List<E>, V>): Boolean = throw UnsupportedOperationException()
    }

  override fun get(key: List<E>): V? = findNode(key)?.value

  override fun put(key: List<E>, value: V): V? {
    var currentNode = root
    for(element in key) {
      currentNode = currentNode.children.getOrPut(element) { MapTrieNode() }
    }

    val oldValue = currentNode.value
    currentNode.value = value

    if(oldValue == null) {
      internalSize++
    }
    return oldValue
  }

  @Suppress("ReturnCount")
  override fun remove(key: List<E>): V? {
    if(key.isEmpty()) {
      val oldValue = root.value
      if(oldValue != null) {
        root.value = null
        internalSize--
      }
      return oldValue
    }

    // 1. Find the node and collect the path
    val path = mutableListOf<MapTrieNode<E, V>>()
    path.add(root)
    var currentNode: MapTrieNode<E, V>? = root
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

  override fun startsWith(prefix: List<E>): Boolean = findNode(prefix) != null

  override fun getAllWithPrefix(prefix: List<E>): Map<List<E>, V> {
    val prefixNode = findNode(prefix) ?: return emptyMap()

    val result = mutableMapOf<List<E>, V>()
    val path = prefix.toMutableList()

    // If the prefix itself is a key, add it.
    prefixNode.value?.let {
      result[path.toList()] = it
    }

    findAllFromNode(prefixNode, path, result)
    return result
  }

  override fun getAllValuesWithPrefix(prefix: List<E>): Collection<V> {
    val prefixNode = findNode(prefix) ?: return emptyList()
    val result = mutableListOf<V>()
    // If the prefix itself is a key, add its value.
    prefixNode.value?.let {
      result.add(it)
    }
    findAllValuesFromNode(prefixNode, result)
    return result
  }

  private fun findNode(key: List<E>): MapTrieNode<E, V>? {
    var currentNode = root
    for(element in key) {
      currentNode = currentNode.children[element] ?: return null
    }
    return currentNode
  }

  private fun findAllFromNode(
    node: MapTrieNode<E, V>,
    path: MutableList<E>,
    result: MutableMap<List<E>, V>,
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
    node: MapTrieNode<E, V>,
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

/**
 * Represents a node in the Trie data structure, mapping sequence elements to values.
 *
 * @param E The type of element in the key sequence.
 * @param V The type of value stored in the Trie.
 */
private class MapTrieNode<E, V> {
  /**
   * A map where keys are sequence elements and values are the corresponding child TrieNodes.
   */
  val children: MutableMap<E, MapTrieNode<E, V>> = mutableMapOf()

  /**
   * The value associated with the key that ends at this node.
   * If null, this node is not the end of a complete key.
   */
  var value: V? = null

  override fun toString(): String = "$value=$children"

  override fun equals(other: Any?): Boolean {
    if(this === other) return true
    if(other == null || this::class != other::class) return false

    other as MapTrieNode<*, *>

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
